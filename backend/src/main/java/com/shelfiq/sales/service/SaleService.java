package com.shelfiq.sales.service;

import com.shelfiq.common.context.TenantContextHolder;
import com.shelfiq.common.exception.InsufficientStockException;
import com.shelfiq.common.exception.ResourceNotFoundException;
import com.shelfiq.common.response.PagedResponse;
import com.shelfiq.inventory.entity.Inventory;
import com.shelfiq.inventory.entity.InventoryMovement;
import com.shelfiq.inventory.repository.InventoryMovementRepository;
import com.shelfiq.inventory.repository.InventoryRepository;
import com.shelfiq.product.entity.Product;
import com.shelfiq.product.repository.ProductRepository;
import com.shelfiq.sales.dto.*;
import com.shelfiq.sales.entity.SaleItem;
import com.shelfiq.sales.entity.SaleTransaction;
import com.shelfiq.sales.repository.SaleItemRepository;
import com.shelfiq.sales.repository.SaleTransactionRepository;
import com.shelfiq.user.entity.User;
import com.shelfiq.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SaleService {

    private final SaleTransactionRepository saleTransactionRepository;
    private final SaleItemRepository saleItemRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryMovementRepository inventoryMovementRepository;
    private final UserRepository userRepository;

    public SaleService(
            SaleTransactionRepository saleTransactionRepository,
            SaleItemRepository saleItemRepository,
            ProductRepository productRepository,
            InventoryRepository inventoryRepository,
            InventoryMovementRepository inventoryMovementRepository,
            UserRepository userRepository) {
        this.saleTransactionRepository = saleTransactionRepository;
        this.saleItemRepository = saleItemRepository;
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
        this.inventoryMovementRepository = inventoryMovementRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public SaleResponseDto checkout(SaleCheckoutRequestDto dto) {
        Long storeId = TenantContextHolder.getRequiredStoreId();
        Long userId = TenantContextHolder.getRequiredUserId();
        User cashier = userRepository.findById(userId).orElse(null);

        String datePrefix = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String randomSuffix = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        String invoiceNumber = "INV-" + datePrefix + "-" + randomSuffix;

        SaleTransaction transaction = new SaleTransaction(
                storeId,
                invoiceNumber,
                cashier,
                dto.getPaymentMethod(),
                dto.getPaymentReference(),
                dto.getCustomerName(),
                dto.getCustomerPhone()
        );

        BigDecimal grossTotal = BigDecimal.ZERO;
        BigDecimal itemDiscountsTotal = BigDecimal.ZERO;

        for (SaleItemRequestDto itemDto : dto.getItems()) {
            Product product = productRepository.findByIdAndStoreId(itemDto.getProductId(), storeId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", itemDto.getProductId()));

            Inventory inventory = inventoryRepository.findByProductIdAndStoreId(product.getId(), storeId)
                    .orElseGet(() -> new Inventory(storeId, product, 0));

            int currentStock = inventory.getCurrentStock();
            if (currentStock < itemDto.getQuantity()) {
                throw new InsufficientStockException(
                        String.format("Insufficient stock for '%s'. Available: %d, Requested: %d",
                                product.getName(), currentStock, itemDto.getQuantity())
                );
            }

            // Deduct stock
            int newStock = currentStock - itemDto.getQuantity();
            inventory.setCurrentStock(newStock);
            inventoryRepository.save(inventory);

            // Record movement audit
            InventoryMovement movement = new InventoryMovement(
                    storeId,
                    product,
                    InventoryMovement.MovementType.SALE,
                    InventoryMovement.ReferenceType.SALE_TRANSACTION,
                    invoiceNumber,
                    -itemDto.getQuantity(),
                    currentStock,
                    newStock,
                    "POS Checkout " + invoiceNumber,
                    cashier
            );
            inventoryMovementRepository.save(movement);

            BigDecimal unitPrice = itemDto.getUnitPrice() != null ? itemDto.getUnitPrice() : product.getSellingPrice();
            BigDecimal discount = itemDto.getDiscountAmount() != null ? itemDto.getDiscountAmount() : BigDecimal.ZERO;

            SaleItem item = new SaleItem(product, itemDto.getQuantity(), unitPrice, discount);
            transaction.addItem(item);

            grossTotal = grossTotal.add(unitPrice.multiply(BigDecimal.valueOf(itemDto.getQuantity())));
            itemDiscountsTotal = itemDiscountsTotal.add(discount);
        }

        BigDecimal orderDiscount = dto.getOrderDiscount() != null ? dto.getOrderDiscount() : BigDecimal.ZERO;
        BigDecimal totalDiscount = itemDiscountsTotal.add(orderDiscount);

        BigDecimal taxableAmount = grossTotal.subtract(totalDiscount);
        if (taxableAmount.compareTo(BigDecimal.ZERO) < 0) {
            taxableAmount = BigDecimal.ZERO;
        }

        // Standard 5% GST/VAT for retail demo
        BigDecimal tax = taxableAmount.multiply(new BigDecimal("0.05")).setScale(2, RoundingMode.HALF_UP);
        BigDecimal netAmount = taxableAmount.add(tax).setScale(2, RoundingMode.HALF_UP);

        transaction.setTotalAmount(grossTotal);
        transaction.setDiscountAmount(totalDiscount);
        transaction.setTaxAmount(tax);
        transaction.setNetAmount(netAmount);

        SaleTransaction saved = saleTransactionRepository.save(transaction);
        return toResponseDto(saved);
    }

    @Transactional(readOnly = true)
    public PagedResponse<SaleResponseDto> getSales(Pageable pageable) {
        Long storeId = TenantContextHolder.getRequiredStoreId();
        Page<SaleTransaction> page = saleTransactionRepository.findByStoreIdOrderByCreatedAtDesc(storeId, pageable);

        List<SaleResponseDto> dtos = page.getContent().stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());

        return new PagedResponse<>(
                dtos,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }

    @Transactional(readOnly = true)
    public SaleResponseDto getSaleById(Long id) {
        Long storeId = TenantContextHolder.getRequiredStoreId();
        SaleTransaction txn = saleTransactionRepository.findByIdAndStoreId(id, storeId)
                .orElseThrow(() -> new ResourceNotFoundException("SaleTransaction", "id", id));
        return toResponseDto(txn);
    }

    @Transactional(readOnly = true)
    public TodaySalesSummaryDto getTodayAnalytics() {
        Long storeId = TenantContextHolder.getRequiredStoreId();
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

        List<SaleTransaction> todaySales = saleTransactionRepository.findByStoreIdAndCreatedAtBetween(storeId, startOfDay, endOfDay);

        long count = todaySales.size();
        BigDecimal totalRevenue = todaySales.stream()
                .map(SaleTransaction::getNetAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal avgTicket = count > 0
                ? totalRevenue.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        long totalUnits = 0;
        for (SaleTransaction txn : todaySales) {
            for (SaleItem item : txn.getItems()) {
                totalUnits += item.getQuantity();
            }
        }

        List<Object[]> topRows = saleItemRepository.findTopSoldProductsSince(storeId, startOfDay, PageRequest.of(0, 5));
        List<TodaySalesSummaryDto.TopSoldItemDto> topItems = new ArrayList<>();
        for (Object[] row : topRows) {
            Long pId = (Long) row[0];
            String name = (String) row[1];
            String sku = (String) row[2];
            long units = row[3] instanceof Number ? ((Number) row[3]).longValue() : 0L;
            BigDecimal rev = row[4] instanceof BigDecimal ? (BigDecimal) row[4] : BigDecimal.ZERO;
            topItems.add(new TodaySalesSummaryDto.TopSoldItemDto(pId, name, sku, units, rev));
        }

        return new TodaySalesSummaryDto(totalRevenue, count, avgTicket, totalUnits, topItems);
    }

    @Transactional(readOnly = true)
    public LeaderboardResponseDto getRegionalLeaderboard(String groupBy, String type, String timeRange) {
        Long storeId = TenantContextHolder.getRequiredStoreId();
        String safeGroup = groupBy != null ? groupBy.trim().toUpperCase() : "CITY";
        String safeType = type != null ? type.trim().toUpperCase() : "ITEM";
        String safeTime = timeRange != null ? timeRange.trim().toUpperCase() : "TODAY";

        LocalDateTime since = switch (safeTime) {
            case "WEEK" -> LocalDateTime.now().minusDays(7);
            case "MONTH" -> LocalDateTime.now().minusDays(30);
            case "ALL" -> LocalDateTime.of(2020, 1, 1, 0, 0);
            default -> LocalDate.now().atStartOfDay(); // TODAY
        };

        // Fetch actual sales since timestamp
        List<SaleTransaction> txns = saleTransactionRepository.findByStoreIdAndCreatedAtBetween(
                storeId, since, LocalDateTime.now());

        // Extract actual sales stats per product & category
        java.util.Map<Long, Long> actualProductUnits = new java.util.HashMap<>();
        java.util.Map<Long, BigDecimal> actualProductRev = new java.util.HashMap<>();
        java.util.Map<Long, Product> productMap = new java.util.HashMap<>();
        java.util.Map<String, Long> actualCategoryUnits = new java.util.HashMap<>();
        java.util.Map<String, BigDecimal> actualCategoryRev = new java.util.HashMap<>();

        for (SaleTransaction txn : txns) {
            for (SaleItem item : txn.getItems()) {
                if (item.getProduct() != null) {
                    Product p = item.getProduct();
                    Long pId = p.getId();
                    productMap.put(pId, p);
                    actualProductUnits.put(pId, actualProductUnits.getOrDefault(pId, 0L) + item.getQuantity());
                    actualProductRev.put(pId, actualProductRev.getOrDefault(pId, BigDecimal.ZERO).add(item.getSubtotal()));

                    String catName = p.getCategory() != null ? p.getCategory().getName() : "General";
                    actualCategoryUnits.put(catName, actualCategoryUnits.getOrDefault(catName, 0L) + item.getQuantity());
                    actualCategoryRev.put(catName, actualCategoryRev.getOrDefault(catName, BigDecimal.ZERO).add(item.getSubtotal()));
                }
            }
        }

        List<LeaderboardResponseDto.LeaderboardGroupDto> groups = new ArrayList<>();

        if ("CITY".equals(safeGroup)) {
            String[] cities = {
                "Bengaluru", "Mumbai", "Delhi NCR", "Hyderabad", "Chennai", "Kolkata",
                "Pune", "Ahmedabad", "Jaipur", "Lucknow", "Chandigarh", "Kochi"
            };
            for (String city : cities) {
                groups.add(buildGroup(city, "CITY", safeType, actualProductUnits, actualProductRev, productMap, actualCategoryUnits, actualCategoryRev, "Bengaluru".equalsIgnoreCase(city)));
            }
        } else if ("STATE".equals(safeGroup)) {
            String[] states = {
                "Andhra Pradesh", "Arunachal Pradesh", "Assam", "Bihar", "Chhattisgarh",
                "Goa", "Gujarat", "Haryana", "Himachal Pradesh", "Jharkhand",
                "Karnataka", "Kerala", "Madhya Pradesh", "Maharashtra", "Manipur",
                "Meghalaya", "Mizoram", "Nagaland", "Odisha", "Punjab",
                "Rajasthan", "Sikkim", "Tamil Nadu", "Telangana", "Tripura",
                "Uttar Pradesh", "Uttarakhand", "West Bengal",
                // Union Territories
                "Delhi NCR", "Jammu and Kashmir", "Ladakh", "Chandigarh", "Puducherry"
            };
            for (String state : states) {
                groups.add(buildGroup(state, "STATE", safeType, actualProductUnits, actualProductRev, productMap, actualCategoryUnits, actualCategoryRev, "Karnataka".equalsIgnoreCase(state)));
            }
        } else { // REGION
            String[] regions = {"South India", "West India", "North India", "East India", "Central India", "North-East India"};
            for (String region : regions) {
                groups.add(buildGroup(region, "REGION", safeType, actualProductUnits, actualProductRev, productMap, actualCategoryUnits, actualCategoryRev, "South India".equalsIgnoreCase(region)));
            }
        }

        return new LeaderboardResponseDto(safeGroup, safeType, safeTime, groups);
    }

    private LeaderboardResponseDto.LeaderboardGroupDto buildGroup(
            String geoName,
            String geoType,
            String type,
            java.util.Map<Long, Long> actualProductUnits,
            java.util.Map<Long, BigDecimal> actualProductRev,
            java.util.Map<Long, Product> productMap,
            java.util.Map<String, Long> actualCategoryUnits,
            java.util.Map<String, BigDecimal> actualCategoryRev,
            boolean isCurrentStoreLocation) {

        List<LeaderboardResponseDto.RankedItemDto> rankings = new ArrayList<>();
        long totalUnits = 0;
        BigDecimal totalRev = BigDecimal.ZERO;

        // Strictly rank real store sales - zero seed data
        if (isCurrentStoreLocation) {
            if ("ITEM".equals(type) && !actualProductUnits.isEmpty()) {
                List<java.util.Map.Entry<Long, Long>> sorted = actualProductUnits.entrySet().stream()
                        .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                        .toList();

                int rank = 1;
                for (java.util.Map.Entry<Long, Long> entry : sorted) {
                    Product p = productMap.get(entry.getKey());
                    if (p == null) continue;
                    long units = entry.getValue();
                    BigDecimal rev = actualProductRev.getOrDefault(entry.getKey(), BigDecimal.ZERO);
                    totalUnits += units;
                    totalRev = totalRev.add(rev);

                    rankings.add(new LeaderboardResponseDto.RankedItemDto(
                            rank++,
                            p.getId(),
                            p.getName(),
                            p.getCategory() != null ? p.getCategory().getName() : "General",
                            p.getSku(),
                            units,
                            rev,
                            0.0,
                            0.0,
                            geoName
                    ));
                }
            } else if ("CATEGORY".equals(type) && !actualCategoryUnits.isEmpty()) {
                List<java.util.Map.Entry<String, Long>> sorted = actualCategoryUnits.entrySet().stream()
                        .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                        .toList();

                int rank = 1;
                for (java.util.Map.Entry<String, Long> entry : sorted) {
                    String catName = entry.getKey();
                    long units = entry.getValue();
                    BigDecimal rev = actualCategoryRev.getOrDefault(catName, BigDecimal.ZERO);
                    totalUnits += units;
                    totalRev = totalRev.add(rev);

                    rankings.add(new LeaderboardResponseDto.RankedItemDto(
                            rank++,
                            (long) rank,
                            catName,
                            catName,
                            "CAT-" + catName.toUpperCase().replace(" ", "-"),
                            units,
                            rev,
                            0.0,
                            0.0,
                            geoName
                    ));
                }
            }
        }

        // Calculate market share percentages
        if (totalRev.compareTo(BigDecimal.ZERO) > 0) {
            for (LeaderboardResponseDto.RankedItemDto r : rankings) {
                double share = r.getRevenue().multiply(BigDecimal.valueOf(100))
                        .divide(totalRev, 1, RoundingMode.HALF_UP).doubleValue();
                r.setMarketSharePercentage(share);
            }
        }

        return new LeaderboardResponseDto.LeaderboardGroupDto(geoName, geoType, totalUnits, totalRev, rankings);
    }

    private SaleResponseDto toResponseDto(SaleTransaction txn) {
        List<SaleItemResponseDto> itemDtos = txn.getItems().stream()
                .map(i -> new SaleItemResponseDto(
                        i.getId(),
                        i.getProduct() != null ? i.getProduct().getId() : null,
                        i.getProduct() != null ? i.getProduct().getName() : "Unknown",
                        i.getProduct() != null ? i.getProduct().getSku() : "N/A",
                        i.getQuantity(),
                        i.getUnitPrice(),
                        i.getSubtotal(),
                        i.getDiscountAmount()
                ))
                .collect(Collectors.toList());

        return new SaleResponseDto(
                txn.getId(),
                txn.getInvoiceNumber(),
                txn.getStoreId(),
                txn.getCashier() != null ? txn.getCashier().getFullName() : "Staff",
                txn.getTotalAmount(),
                txn.getDiscountAmount(),
                txn.getTaxAmount(),
                txn.getNetAmount(),
                txn.getPaymentMethod(),
                txn.getPaymentReference(),
                txn.getCustomerName(),
                txn.getCustomerPhone(),
                txn.getStatus(),
                txn.getCreatedAt(),
                itemDtos
        );
    }
}
