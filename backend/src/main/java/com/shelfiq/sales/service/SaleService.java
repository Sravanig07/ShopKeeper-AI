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

        if ("ITEM".equals(type)) {
            // If current store location has actual sales, prioritize actual products
            if (isCurrentStoreLocation && !actualProductUnits.isEmpty()) {
                List<java.util.Map.Entry<Long, Long>> sorted = actualProductUnits.entrySet().stream()
                        .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                        .toList();

                int rank = 1;
                for (java.util.Map.Entry<Long, Long> entry : sorted) {
                    Product p = productMap.get(entry.getKey());
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
                            0.0, // calculated later
                            +18.4,
                            geoName
                    ));
                }
            }

            // Fill with retail benchmark items if fewer than 6
            String[][] benchmarkItems = getBenchmarkItemsForGeo(geoName);
            int startRank = rankings.size() + 1;
            for (String[] it : benchmarkItems) {
                if (rankings.size() >= 6) break;
                long units = Long.parseLong(it[3]);
                BigDecimal rev = new BigDecimal(it[4]);
                totalUnits += units;
                totalRev = totalRev.add(rev);

                rankings.add(new LeaderboardResponseDto.RankedItemDto(
                        startRank++,
                        (long) (1000 + rankings.size()),
                        it[0], // name
                        it[1], // category
                        it[2], // sku
                        units,
                        rev,
                        0.0,
                        Double.parseDouble(it[5]), // growth
                        it[6] // dominant city
                ));
            }
        } else { // CATEGORY
            if (isCurrentStoreLocation && !actualCategoryUnits.isEmpty()) {
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
                            +15.2,
                            geoName
                    ));
                }
            }

            // Benchmark categories
            String[][] benchmarkCategories = getBenchmarkCategoriesForGeo(geoName);
            int startRank = rankings.size() + 1;
            for (String[] it : benchmarkCategories) {
                if (rankings.size() >= 6) break;
                long units = Long.parseLong(it[1]);
                BigDecimal rev = new BigDecimal(it[2]);
                totalUnits += units;
                totalRev = totalRev.add(rev);

                rankings.add(new LeaderboardResponseDto.RankedItemDto(
                        startRank++,
                        (long) (2000 + rankings.size()),
                        it[0], // category name
                        it[0],
                        "CAT-" + it[0].toUpperCase().replace(" ", "-"),
                        units,
                        rev,
                        0.0,
                        Double.parseDouble(it[3]), // growth %
                        it[4] // dominant city
                ));
            }
        }

        // Calculate market share percentages
        final BigDecimal finalTotalRev = totalRev.compareTo(BigDecimal.ZERO) > 0 ? totalRev : BigDecimal.ONE;
        for (LeaderboardResponseDto.RankedItemDto r : rankings) {
            double share = r.getRevenue().multiply(BigDecimal.valueOf(100))
                    .divide(finalTotalRev, 1, RoundingMode.HALF_UP).doubleValue();
            r.setMarketSharePercentage(share);
        }

        return new LeaderboardResponseDto.LeaderboardGroupDto(geoName, geoType, totalUnits, totalRev, rankings);
    }

    private String[][] getBenchmarkItemsForGeo(String geo) {
        return switch (geo) {
            case "Mumbai", "Maharashtra", "Pune" -> new String[][]{
                    {"Amul Taaza Homogenised Milk 1L", "Dairy & Bread", "DRY-AML-1000", "420", "29400.00", "+22.5", "Mumbai"},
                    {"Cadbury Dairy Milk Silk 150g", "Snacks & Munchies", "SNK-CAD-150", "310", "55800.00", "+18.2", "Pune"},
                    {"Coca-Cola 500ml Pet Bottle", "Beverages & Cold Drinks", "BEV-COKE-500", "280", "11200.00", "+14.8", "Mumbai"},
                    {"Parle-G Gold Biscuits 250g", "Snacks & Munchies", "SNK-PRL-250", "260", "7800.00", "+9.4", "Nagpur"},
                    {"Tata Salt Vacuum Evaporated 1kg", "Staples & Grains", "STP-TTS-1000", "210", "5880.00", "+11.0", "Mumbai"},
                    {"Surf Excel Easy Wash Detergent 1kg", "Household Supplies", "HSD-SRF-1000", "145", "20300.00", "+16.7", "Thane"}
            };
            case "Delhi NCR", "Delhi", "Haryana", "North India" -> new String[][]{
                    {"Mother Dairy Full Cream Milk 1L", "Dairy & Bread", "DRY-MD-1000", "480", "33600.00", "+26.1", "New Delhi"},
                    {"Lay's India's Magic Masala 50g", "Snacks & Munchies", "SNK-LYS-50", "390", "7800.00", "+21.4", "Gurugram"},
                    {"Red Bull Energy Drink 250ml", "Beverages & Cold Drinks", "BEV-RBL-250", "270", "33750.00", "+28.9", "Noida"},
                    {"Maggi 2-Minute Masala Noodles 280g", "Packaged Foods", "PKG-MAG-280", "340", "19040.00", "+15.3", "New Delhi"},
                    {"Dettol Original Antiseptic Liquid 250ml", "Personal Care & Hygiene", "PC-DTL-250", "190", "24700.00", "+12.8", "Faridabad"},
                    {"Fortune Sunlite Refined Sunflower Oil 1L", "Staples & Grains", "STP-FTN-1000", "165", "23100.00", "+10.5", "Ghaziabad"}
            };
            case "Hyderabad", "Telangana", "Andhra Pradesh" -> new String[][]{
                    {"Heritage Daily Health Milk 1L", "Dairy & Bread", "DRY-HRT-1000", "380", "25840.00", "+19.7", "Hyderabad"},
                    {"Thums Up Charged 500ml", "Beverages & Cold Drinks", "BEV-THM-500", "340", "13600.00", "+24.2", "Secunderabad"},
                    {"Haldiram's All in One Mixture 200g", "Snacks & Munchies", "SNK-HLD-200", "240", "13200.00", "+14.6", "Hyderabad"},
                    {"India Gate Basmati Rice Feast Rozzana 1kg", "Staples & Grains", "STP-IGB-1000", "175", "19250.00", "+16.8", "Vijayawada"},
                    {"Priya Mango Pickle with Garlic 300g", "Packaged Foods", "PKG-PRY-300", "185", "12950.00", "+17.2", "Visakhapatnam"},
                    {"Colgate Strong Teeth Toothpaste 200g", "Personal Care & Hygiene", "PC-CLG-200", "160", "18400.00", "+8.9", "Hyderabad"}
            };
            case "Tamil Nadu", "Chennai", "Puducherry" -> new String[][]{
                    {"Aavin Green Magic Standardised Milk 1L", "Dairy & Bread", "DRY-AVN-1000", "440", "23760.00", "+21.8", "Chennai"},
                    {"Bru Instant Coffee Powder 200g", "Beverages & Cold Drinks", "BEV-BRU-200", "290", "55100.00", "+25.4", "Coimbatore"},
                    {"Sakthi Sambar Powder 200g", "Staples & Grains", "STP-SKT-200", "260", "15600.00", "+18.9", "Madurai"},
                    {"Ponni Boiled Rice Delux 5kg", "Staples & Grains", "STP-PNI-5000", "180", "46800.00", "+14.2", "Tiruchirappalli"},
                    {"Hamam Neem Tulsi Aloe Vera Soap 100g", "Personal Care & Hygiene", "PC-HMM-100", "220", "11000.00", "+12.1", "Salem"},
                    {"Gold Winner Refined Sunflower Oil 1L", "Staples & Grains", "STP-GLD-1000", "175", "26250.00", "+16.5", "Chennai"}
            };
            case "Gujarat", "Ahmedabad" -> new String[][]{
                    {"Amul Gold Full Cream Milk 1L", "Dairy & Bread", "DRY-AMG-1000", "470", "32900.00", "+24.6", "Ahmedabad"},
                    {"Balaji Wafers Simply Salted 100g", "Snacks & Munchies", "SNK-BLJ-100", "360", "10800.00", "+27.3", "Rajkot"},
                    {"Wagh Bakri Premium Leaf Tea 500g", "Beverages & Cold Drinks", "BEV-WB-500", "250", "35000.00", "+19.8", "Ahmedabad"},
                    {"Fortune Cottonseed Filtered Oil 1L", "Staples & Grains", "STP-FTN-COT", "190", "28500.00", "+15.0", "Surat"},
                    {"Vadilal Gourmet Ice Cream 1L", "Dairy & Bread", "DRY-VDL-1000", "160", "36800.00", "+22.1", "Vadodara"},
                    {"Fogg Scent Xpressio Perfume 100ml", "Personal Care & Hygiene", "PC-FGG-100", "130", "39000.00", "+18.4", "Ahmedabad"}
            };
            case "Kerala", "Kochi" -> new String[][]{
                    {"Milma Rich Cream Milk 1L", "Dairy & Bread", "DRY-MLM-1000", "410", "23780.00", "+20.1", "Kochi"},
                    {"Kera Pure Coconut Cooking Oil 1L", "Staples & Grains", "STP-KRA-1000", "230", "57500.00", "+22.8", "Thiruvananthapuram"},
                    {"Eastern Chicken Masala 100g", "Staples & Grains", "STP-EST-100", "270", "13500.00", "+19.4", "Kozhikode"},
                    {"Nirapara Vadi Matta Rice 5kg", "Staples & Grains", "STP-NRP-5000", "150", "42000.00", "+16.7", "Thrissur"},
                    {"Medimix Ayurvedic Classic Soap 125g", "Personal Care & Hygiene", "PC-MDM-125", "210", "10500.00", "+14.0", "Kochi"},
                    {"Britannia Marie Gold Biscuits 300g", "Snacks & Munchies", "SNK-BRT-MAR", "240", "8400.00", "+11.5", "Kollam"}
            };
            case "Punjab", "Chandigarh" -> new String[][]{
                    {"Verka Standard Pasteurized Milk 1L", "Dairy & Bread", "DRY-VRK-1000", "450", "29250.00", "+23.5", "Ludhiana"},
                    {"Uncle Chipps Spicy Treat 50g", "Snacks & Munchies", "SNK-UNC-50", "330", "6600.00", "+20.8", "Chandigarh"},
                    {"Catch Sprinklers Chaat Masala 100g", "Staples & Grains", "STP-CTC-100", "210", "14700.00", "+17.6", "Amritsar"},
                    {"MDH Deggi Mirch Powder 100g", "Staples & Grains", "STP-MDH-100", "240", "21600.00", "+15.9", "Jalandhar"},
                    {"Cremica Mixed Fruit Jam 500g", "Packaged Foods", "PKG-CRM-500", "160", "20800.00", "+18.2", "Ludhiana"},
                    {"Surf Excel Matic Front Load 1kg", "Household Supplies", "HSD-SRF-MTC", "140", "32200.00", "+13.4", "Patiala"}
            };
            case "Uttar Pradesh", "Bihar", "Jharkhand", "Madhya Pradesh", "Chhattisgarh", "Central India", "Lucknow" -> new String[][]{
                    {"Parle-G Original Glucose Biscuits 250g", "Snacks & Munchies", "SNK-PRL-250", "520", "15600.00", "+24.1", "Kanpur"},
                    {"Ghadi Detergent Powder 1kg", "Household Supplies", "HSD-GHD-1000", "380", "26600.00", "+21.5", "Kanpur"},
                    {"Fortune Kachi Ghani Pure Mustard Oil 1L", "Staples & Grains", "STP-FTN-MST", "290", "44950.00", "+25.8", "Varanasi"},
                    {"Maggi 2-Minute Masala Noodles 280g", "Packaged Foods", "PKG-MAG-280", "320", "17920.00", "+18.2", "Lucknow"},
                    {"Frooti Fresh 'N' Juicy Mango Drink 600ml", "Beverages & Cold Drinks", "BEV-FRT-600", "270", "10800.00", "+20.6", "Patna"},
                    {"Dettol Original Soap 75g (Pack of 3)", "Personal Care & Hygiene", "PC-DTL-SOAP", "195", "21450.00", "+12.7", "Bhopal"}
            };
            case "West Bengal", "Odisha", "East India", "Kolkata" -> new String[][]{
                    {"Mother Dairy Classic Cow Milk 1L", "Dairy & Bread", "DRY-MDC-1000", "460", "29900.00", "+23.1", "Kolkata"},
                    {"Fortune Premium Kachi Ghani Mustard Oil 1L", "Staples & Grains", "STP-FTN-MST", "340", "52700.00", "+26.4", "Kolkata"},
                    {"Tata Tea Premium Desh Ki Chai 500g", "Beverages & Cold Drinks", "BEV-TTA-500", "280", "36400.00", "+21.0", "Siliguri"},
                    {"Bisk Farm Top Gold Marie Biscuits 300g", "Snacks & Munchies", "SNK-BSK-300", "310", "9300.00", "+17.5", "Bhubaneswar"},
                    {"Boroline Antiseptic Ayurvedic Cream 40g", "Personal Care & Hygiene", "PC-BRL-40", "260", "11700.00", "+19.8", "Kolkata"},
                    {"Dettol Original Liquid 250ml", "Personal Care & Hygiene", "PC-DTL-250", "180", "23400.00", "+14.3", "Cuttack"}
            };
            case "Rajasthan", "Jaipur" -> new String[][]{
                    {"Saras Fresh Toned Milk 1L", "Dairy & Bread", "DRY-SRS-1000", "430", "23650.00", "+22.4", "Jaipur"},
                    {"Bikaji Bhujia Asli Bikaneri 400g", "Snacks & Munchies", "SNK-BKJ-400", "340", "37400.00", "+25.9", "Bikaner"},
                    {"Everest Super Garam Masala 100g", "Staples & Grains", "STP-EVR-100", "260", "22100.00", "+18.1", "Jodhpur"},
                    {"Haldiram's Tins Gulab Jamun 1kg", "Packaged Foods", "PKG-HLD-GJ", "180", "39600.00", "+21.0", "Udaipur"},
                    {"Tata Salt Vacuum Evaporated 1kg", "Staples & Grains", "STP-TTS-1000", "240", "6720.00", "+11.5", "Kota"},
                    {"Nivea Soft Light Moisturiser 200ml", "Personal Care & Hygiene", "PC-NVA-200", "145", "37700.00", "+16.8", "Jaipur"}
            };
            case "Goa" -> new String[][]{
                    {"Goa Dairy Pasteurised Toned Milk 1L", "Dairy & Bread", "DRY-GOA-1000", "390", "21450.00", "+18.5", "Panaji"},
                    {"Cadbury Dairy Milk Silk Roasted Almond 150g", "Snacks & Munchies", "SNK-CAD-ALM", "280", "53200.00", "+22.1", "Margao"},
                    {"Coca-Cola Zero Sugar 300ml Can", "Beverages & Cold Drinks", "BEV-COKE-ZR", "310", "12400.00", "+24.8", "Vasco da Gama"},
                    {"Lay's Classic Salted Potato Chips 50g", "Snacks & Munchies", "SNK-LYS-CLS", "290", "5800.00", "+15.2", "Mapusa"},
                    {"Red Bull Energy Drink 250ml", "Beverages & Cold Drinks", "BEV-RBL-250", "230", "28750.00", "+29.4", "Panaji"},
                    {"Dettol Aloe Vera Liquid Handwash 200ml", "Personal Care & Hygiene", "PC-DTL-ALW", "150", "14850.00", "+12.0", "Ponda"}
            };
            case "Himachal Pradesh", "Uttarakhand", "Jammu and Kashmir", "Ladakh" -> new String[][]{
                    {"Verka Fresh Homogenised Milk 1L", "Dairy & Bread", "DRY-VRK-1000", "380", "24700.00", "+19.2", "Shimla"},
                    {"Dabur 100% Pure Squeezy Honey 500g", "Packaged Foods", "PKG-DBR-500", "240", "48000.00", "+26.7", "Dehradun"},
                    {"Maggi Hot & Sweet Tomato Chilli Sauce 500g", "Packaged Foods", "PKG-MAG-SAU", "210", "29400.00", "+18.3", "Haridwar"},
                    {"Red Bull Energy Drink 250ml", "Beverages & Cold Drinks", "BEV-RBL-250", "200", "25000.00", "+27.1", "Srinagar"},
                    {"Patanjali Dant Kanti Natural Toothpaste 200g", "Personal Care & Hygiene", "PC-PTN-200", "220", "24200.00", "+14.6", "Rishikesh"},
                    {"Vaseline Intensive Care Deep Moisture 400ml", "Personal Care & Hygiene", "PC-VSL-400", "170", "47600.00", "+21.5", "Jammu"}
            };
            case "Assam", "Arunachal Pradesh", "Manipur", "Meghalaya", "Mizoram", "Nagaland", "Sikkim", "Tripura", "North-East India" -> new String[][]{
                    {"Tata Tea Gold Premium Assam Blend 500g", "Beverages & Cold Drinks", "BEV-TTG-500", "360", "54000.00", "+27.4", "Guwahati"},
                    {"Wai Wai Quick Masala Ready-to-Eat Noodles 70g", "Packaged Foods", "PKG-WAI-70", "410", "10250.00", "+28.2", "Shillong"},
                    {"Amul Taaza Long-Life Toned Milk 1L", "Dairy & Bread", "DRY-AML-1000", "310", "21700.00", "+17.9", "Dimapur"},
                    {"Britannia Good Day Cashew Cookies 200g", "Snacks & Munchies", "SNK-BRT-CSH", "260", "9100.00", "+14.5", "Agartala"},
                    {"Fortune Kachi Ghani Mustard Oil 1L", "Staples & Grains", "STP-FTN-MST", "220", "34100.00", "+19.0", "Aizawl"},
                    {"Lifebuoy Total Germ Protection Soap 125g", "Personal Care & Hygiene", "PC-LFB-125", "230", "9200.00", "+11.8", "Imphal"}
            };
            default -> new String[][]{ // Bengaluru, Karnataka, South India default
                    {"Nandini Toned Fresh Milk 1L", "Dairy & Bread", "DRY-NAN-1000", "490", "24500.00", "+25.4", "Bengaluru"},
                    {"Coca-Cola 500ml Pet Bottle", "Beverages & Cold Drinks", "BEV-COKE-500", "360", "14400.00", "+21.2", "Bengaluru"},
                    {"Aashirvaad Shudh Chakki Atta 5kg", "Staples & Grains", "STP-ASH-5000", "180", "49500.00", "+17.6", "Mysuru"},
                    {"Britannia Good Day Butter Cookies 200g", "Snacks & Munchies", "SNK-BRT-200", "295", "10325.00", "+14.1", "Bengaluru"},
                    {"Red Bull Energy Drink 250ml", "Beverages & Cold Drinks", "BEV-RBL-250", "210", "26250.00", "+31.0", "Bengaluru"},
                    {"Dettol Original Liquid Handwash 200ml", "Personal Care & Hygiene", "PC-DTL-200", "165", "16335.00", "+13.8", "Hubballi"}
            };
        };
    }

    private String[][] getBenchmarkCategoriesForGeo(String geo) {
        return new String[][]{
                {"Beverages & Cold Drinks", "1420", "98400.00", "+23.8", geo},
                {"Snacks & Munchies", "1280", "74600.00", "+19.4", geo},
                {"Dairy & Bread", "1190", "68500.00", "+16.2", geo},
                {"Staples & Grains", "840", "112400.00", "+14.7", geo},
                {"Packaged Foods", "720", "52800.00", "+11.9", geo},
                {"Personal Care & Hygiene", "580", "64200.00", "+15.3", geo},
                {"Household Supplies", "460", "47900.00", "+8.5", geo}
        };
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
