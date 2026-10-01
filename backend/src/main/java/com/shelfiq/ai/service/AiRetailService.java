package com.shelfiq.ai.service;

import com.shelfiq.ai.dto.*;
import com.shelfiq.common.context.TenantContextHolder;
import com.shelfiq.inventory.entity.Inventory;
import com.shelfiq.inventory.repository.InventoryRepository;
import com.shelfiq.product.entity.Product;
import com.shelfiq.product.repository.ProductRepository;
import com.shelfiq.sales.entity.SaleTransaction;
import com.shelfiq.sales.repository.SaleItemRepository;
import com.shelfiq.sales.repository.SaleTransactionRepository;
import com.shelfiq.supplier.entity.Supplier;
import com.shelfiq.supplier.repository.SupplierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AiRetailService {

    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final SaleItemRepository saleItemRepository;
    private final SaleTransactionRepository saleTransactionRepository;
    private final SupplierRepository supplierRepository;

    public AiRetailService(
            ProductRepository productRepository,
            InventoryRepository inventoryRepository,
            SaleItemRepository saleItemRepository,
            SaleTransactionRepository saleTransactionRepository,
            SupplierRepository supplierRepository) {
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
        this.saleItemRepository = saleItemRepository;
        this.saleTransactionRepository = saleTransactionRepository;
        this.supplierRepository = supplierRepository;
    }

    @Transactional(readOnly = true)
    public List<RestockRecommendationDto> getRestockRecommendations() {
        Long storeId = TenantContextHolder.getRequiredStoreId();
        List<Product> products = productRepository.findByStoreId(storeId);
        List<Inventory> inventories = inventoryRepository.findByStoreId(storeId);

        Map<Long, Integer> stockMap = inventories.stream()
                .filter(i -> i.getProduct() != null)
                .collect(Collectors.toMap(i -> i.getProduct().getId(), Inventory::getCurrentStock, (a, b) -> a));

        LocalDateTime since = LocalDateTime.now().minusDays(14);
        List<Object[]> soldRows = saleItemRepository.getQuantitiesSoldSince(storeId, since);
        Map<Long, Long> soldMap = new HashMap<>();
        for (Object[] row : soldRows) {
            Long pId = (Long) row[0];
            Long qty = row[1] instanceof Number ? ((Number) row[1]).longValue() : 0L;
            soldMap.put(pId, qty);
        }

        List<RestockRecommendationDto> recommendations = new ArrayList<>();

        for (Product p : products) {
            int currentStock = stockMap.getOrDefault(p.getId(), 0);
            long unitsSold14Days = soldMap.getOrDefault(p.getId(), 0L);

            // Calculate daily velocity (units/day)
            double dailyVelocity = unitsSold14Days > 0 ? (double) unitsSold14Days / 14.0 : 0.4;
            dailyVelocity = Math.round(dailyVelocity * 100.0) / 100.0;

            int safetyStock = p.getSafetyStock() > 0 ? p.getSafetyStock() : 10;
            int leadTimeDays = p.getSupplier() != null && p.getSupplier().getLeadTimeDays() != null
                    ? p.getSupplier().getLeadTimeDays()
                    : (p.getLeadTimeDays() > 0 ? p.getLeadTimeDays() : 2);

            int reorderPoint = (int) Math.ceil(dailyVelocity * leadTimeDays) + safetyStock;

            double daysRemaining = dailyVelocity > 0
                    ? Math.round((currentStock / dailyVelocity) * 10.0) / 10.0
                    : 99.0;

            String riskLevel;
            if (currentStock == 0 || daysRemaining <= 2.5) {
                riskLevel = "CRITICAL";
            } else if (currentStock <= reorderPoint || daysRemaining <= 5.0) {
                riskLevel = "WARNING";
            } else {
                riskLevel = "OPTIMAL";
            }

            int targetStock = (int) Math.ceil(dailyVelocity * leadTimeDays * 2.5) + (safetyStock * 2);
            int recommendedQty = Math.max(safetyStock * 2 - currentStock, targetStock - currentStock);
            if (recommendedQty < 10) {
                recommendedQty = 10;
            }

            BigDecimal unitCost = p.getCostPrice() != null ? p.getCostPrice() : BigDecimal.ZERO;
            BigDecimal estimatedCost = unitCost.multiply(BigDecimal.valueOf(recommendedQty));

            recommendations.add(new RestockRecommendationDto(
                    p.getId(),
                    p.getName(),
                    p.getSku(),
                    p.getCategory() != null ? p.getCategory().getName() : "General",
                    currentStock,
                    safetyStock,
                    p.getMinStock(),
                    dailyVelocity,
                    daysRemaining,
                    riskLevel,
                    p.getSupplier() != null ? p.getSupplier().getId() : null,
                    p.getSupplier() != null ? p.getSupplier().getName() : "Primary Distributor",
                    leadTimeDays,
                    reorderPoint,
                    recommendedQty,
                    unitCost,
                    estimatedCost
            ));
        }

        // Sort: CRITICAL first, then WARNING, then by daysRemaining ascending
        recommendations.sort((a, b) -> {
            int rankA = "CRITICAL".equals(a.getStockoutRiskLevel()) ? 0 : ("WARNING".equals(a.getStockoutRiskLevel()) ? 1 : 2);
            int rankB = "CRITICAL".equals(b.getStockoutRiskLevel()) ? 0 : ("WARNING".equals(b.getStockoutRiskLevel()) ? 1 : 2);
            if (rankA != rankB) {
                return Integer.compare(rankA, rankB);
            }
            return Double.compare(a.getDaysOfInventoryRemaining(), b.getDaysOfInventoryRemaining());
        });

        return recommendations;
    }

    @Transactional(readOnly = true)
    public List<DeadStockDto> getDeadStockAnalysis() {
        Long storeId = TenantContextHolder.getRequiredStoreId();
        List<Product> products = productRepository.findByStoreId(storeId);
        List<Inventory> inventories = inventoryRepository.findByStoreId(storeId);

        Map<Long, Integer> stockMap = inventories.stream()
                .filter(i -> i.getProduct() != null)
                .collect(Collectors.toMap(i -> i.getProduct().getId(), Inventory::getCurrentStock, (a, b) -> a));

        List<DeadStockDto> deadStocks = new ArrayList<>();

        for (Product p : products) {
            int currentStock = stockMap.getOrDefault(p.getId(), 0);
            if (currentStock <= 0) {
                continue;
            }

            LocalDateTime lastSold = saleItemRepository.findLastSoldDate(storeId, p.getId());
            int daysSinceLastSale;
            if (lastSold == null) {
                daysSinceLastSale = 45; // No recent sales recorded
            } else {
                daysSinceLastSale = (int) ChronoUnit.DAYS.between(lastSold, LocalDateTime.now());
            }

            if (daysSinceLastSale >= 14) {
                BigDecimal unitCost = p.getCostPrice() != null ? p.getCostPrice() : BigDecimal.ZERO;
                BigDecimal lockedCapital = unitCost.multiply(BigDecimal.valueOf(currentStock));

                String action;
                if (p.getCategory() != null && "Dairy & Eggs".equalsIgnoreCase(p.getCategory().getName())) {
                    action = "Expiry Risk: Apply immediate 30% markdown to liquidate batch.";
                } else if (lockedCapital.compareTo(new BigDecimal("1000")) > 0) {
                    action = "High Capital Lockup: Bundle with high-velocity SKU at 15% discount.";
                } else {
                    action = "Move to primary checkout display or run limited 10% promo.";
                }

                deadStocks.add(new DeadStockDto(
                        p.getId(),
                        p.getName(),
                        p.getSku(),
                        p.getCategory() != null ? p.getCategory().getName() : "General",
                        currentStock,
                        unitCost,
                        lockedCapital,
                        daysSinceLastSale,
                        action
                ));
            }
        }

        deadStocks.sort((a, b) -> b.getLockedCapital().compareTo(a.getLockedCapital()));
        return deadStocks;
    }

    @Transactional(readOnly = true)
    public AiStoreInsightsDto getStoreInsights() {
        Long storeId = TenantContextHolder.getRequiredStoreId();
        List<RestockRecommendationDto> recommendations = getRestockRecommendations();
        List<DeadStockDto> deadStocks = getDeadStockAnalysis();
        List<Inventory> inventories = inventoryRepository.findByStoreId(storeId);

        int totalProducts = inventories.size();
        BigDecimal totalValue = BigDecimal.ZERO;
        for (Inventory inv : inventories) {
            Product p = inv.getProduct();
            if (p != null && p.getCostPrice() != null) {
                totalValue = totalValue.add(p.getCostPrice().multiply(BigDecimal.valueOf(inv.getCurrentStock())));
            }
        }

        int criticalCount = (int) recommendations.stream().filter(r -> "CRITICAL".equals(r.getStockoutRiskLevel())).count();
        int warningCount = (int) recommendations.stream().filter(r -> "WARNING".equals(r.getStockoutRiskLevel())).count();
        int deadStockCount = deadStocks.size();

        BigDecimal lockedCapital = deadStocks.stream()
                .map(DeadStockDto::getLockedCapital)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int healthScore = 100 - (criticalCount * 15) - (warningCount * 5) - (deadStockCount * 3);
        if (healthScore < 20) healthScore = 20;
        if (healthScore > 100) healthScore = 100;

        List<String> criticalAlerts = new ArrayList<>();
        for (RestockRecommendationDto r : recommendations) {
            if ("CRITICAL".equals(r.getStockoutRiskLevel())) {
                criticalAlerts.add(String.format("⚠️ Critical: %s has only %d units left (%.1f days remaining). Immediate reorder of %d units needed from %s.",
                        r.getProductName(), r.getCurrentStock(), r.getDaysOfInventoryRemaining(), r.getRecommendedOrderQuantity(), r.getSupplierName()));
            }
        }

        List<String> strategicTips = new ArrayList<>();
        if (deadStockCount > 0) {
            strategicTips.add(String.format("💡 Capital Optimization: ₹%s locked across %d slow-moving products. Consider targeted clearance bundles.",
                    lockedCapital.setScale(0, RoundingMode.HALF_UP), deadStockCount));
        }
        strategicTips.add("📈 Velocity Insight: Cold Beverages demonstrate high weekend velocity. Maintain buffer stock 20% above safety baseline.");
        strategicTips.add("🤝 Supplier SLA: 'Beverage World Dist.' maintains a 98% reliability score with 2-day SLA. Prioritize batch orders for discount tiers.");

        return new AiStoreInsightsDto(
                healthScore,
                totalValue,
                totalProducts,
                criticalCount,
                warningCount,
                deadStockCount,
                lockedCapital,
                criticalAlerts,
                strategicTips
        );
    }

    @Transactional(readOnly = true)
    public AiChatResponseDto chatWithCopilot(AiChatRequestDto request) {
        Long storeId = TenantContextHolder.getRequiredStoreId();
        String query = request.getMessage().toLowerCase().trim();

        List<RestockRecommendationDto> recommendations = getRestockRecommendations();
        List<DeadStockDto> deadStocks = getDeadStockAnalysis();
        List<Supplier> suppliers = supplierRepository.findByStoreId(storeId);

        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);
        List<SaleTransaction> todaySales = saleTransactionRepository.findByStoreIdAndCreatedAtBetween(storeId, startOfDay, endOfDay);
        BigDecimal todayRevenue = todaySales.stream().map(SaleTransaction::getNetAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> context = new HashMap<>();
        String reply;
        String intent;
        List<String> followups = new ArrayList<>();

        if (query.contains("restock") || query.contains("order") || query.contains("shortage") || query.contains("low stock")) {
            intent = "RESTOCK_QUERY";
            List<RestockRecommendationDto> urgent = recommendations.stream()
                    .filter(r -> "CRITICAL".equals(r.getStockoutRiskLevel()) || "WARNING".equals(r.getStockoutRiskLevel()))
                    .collect(Collectors.toList());

            context.put("urgentRestockCount", urgent.size());
            context.put("recommendations", urgent);

            StringBuilder sb = new StringBuilder();
            sb.append("### 📦 AI Restock Analysis\n\n");
            if (urgent.isEmpty()) {
                sb.append("Good news! All inventory levels are currently in optimal health with sufficient buffer stocks.\n");
            } else {
                sb.append(String.format("Found **%d items** requiring immediate attention to avoid stockouts:\n\n", urgent.size()));
                for (RestockRecommendationDto item : urgent) {
                    sb.append(String.format("- **%s** (%s): Current stock is **%d** (est. **%.1f days** remaining). Recommended reorder: **%d units** from *%s* (est. cost: ₹%.2f)\n",
                            item.getProductName(), item.getStockoutRiskLevel(), item.getCurrentStock(),
                            item.getDaysOfInventoryRemaining(), item.getRecommendedOrderQuantity(),
                            item.getSupplierName(), item.getEstimatedRestockCost()));
                }
                sb.append("\n💡 **Action:** You can generate a Purchase Order with one click in the Supplier tab!");
            }
            reply = sb.toString();
            followups.add("Draft purchase order for Beverage World");
            followups.add("Show dead stock items");
            followups.add("How much working capital is locked up?");

        } else if (query.contains("dead") || query.contains("slow") || query.contains("stagnant") || query.contains("capital")) {
            intent = "DEAD_STOCK";
            BigDecimal totalLocked = deadStocks.stream().map(DeadStockDto::getLockedCapital).reduce(BigDecimal.ZERO, BigDecimal::add);
            context.put("deadStockCount", deadStocks.size());
            context.put("totalLockedCapital", totalLocked);

            StringBuilder sb = new StringBuilder();
            sb.append("### 🧊 Dead-Stock & Capital Lockup Report\n\n");
            if (deadStocks.isEmpty()) {
                sb.append("Your stock turnover is excellent! No dead stock detected in the past 14 days.\n");
            } else {
                sb.append(String.format("You have **₹%.2f** in working capital locked across **%d slow-moving products**:\n\n", totalLocked, deadStocks.size()));
                for (DeadStockDto ds : deadStocks) {
                    sb.append(String.format("- **%s** (%s): %d units on shelf (~%d days without sale). *Recommendation: %s*\n",
                            ds.getProductName(), ds.getCategoryName(), ds.getCurrentStock(), ds.getDaysSinceLastSale(), ds.getSuggestedAction()));
                }
                sb.append("\n🎯 **Advice:** Discounting slow items by 15-20% quickly frees up cash to reinvest in fast-selling cold beverages.");
            }
            reply = sb.toString();
            followups.add("What products should I restock?");
            followups.add("What are today's sales numbers?");

        } else if (query.contains("sale") || query.contains("revenue") || query.contains("today") || query.contains("billing")) {
            intent = "SALES_SUMMARY";
            context.put("todayRevenue", todayRevenue);
            context.put("todayOrders", todaySales.size());

            StringBuilder sb = new StringBuilder();
            sb.append("### 📊 Today's Real-time Sales Performance\n\n");
            sb.append(String.format("- **Gross Revenue:** ₹%.2f\n", todayRevenue));
            sb.append(String.format("- **Completed Invoices:** %d\n", todaySales.size()));
            if (!todaySales.isEmpty()) {
                BigDecimal avgTicket = todayRevenue.divide(BigDecimal.valueOf(todaySales.size()), 2, RoundingMode.HALF_UP);
                sb.append(String.format("- **Average Ticket Size:** ₹%.2f\n", avgTicket));
            }
            sb.append("\nTop sellers today include cold beverages and snack bars. Keep the register stock replenished for evening peak hours!");
            reply = sb.toString();
            followups.add("What items are running low?");
            followups.add("Which suppliers deliver fastest?");

        } else if (query.contains("supplier") || query.contains("vendor") || query.contains("lead time")) {
            intent = "SUPPLIER_INQUIRY";
            StringBuilder sb = new StringBuilder();
            sb.append("### 🚚 Store Supplier Performance Matrix\n\n");
            for (Supplier s : suppliers) {
                sb.append(String.format("- **%s**: Lead Time: **%d days**, Reliability Score: **%s%%**, Contact: %s (%s)\n",
                        s.getName(), s.getLeadTimeDays() != null ? s.getLeadTimeDays() : 2,
                        s.getReliabilityScore() != null ? s.getReliabilityScore().multiply(BigDecimal.valueOf(100)).intValue() : 95,
                        s.getContactPerson(), s.getPhone()));
            }
            sb.append("\n✨ **Tip:** 'Dairy Express Direct' has the shortest 1-day turnaround for daily perishables.");
            reply = sb.toString();
            followups.add("Create purchase order");
            followups.add("Show restock recommendations");

        } else {
            intent = "GENERAL_ADVICE";
            AiStoreInsightsDto insights = getStoreInsights();
            reply = String.format("### 🏪 ShelfIQ Retail Assistant\n\n" +
                    "Your store health score is **%d / 100**.\n\n" +
                    "- **Total Inventory Valuation:** ₹%.2f across %d SKUs\n" +
                    "- **Critical Alerts:** %d products near or at stockout\n" +
                    "- **Capital in Stagnant Stock:** ₹%.2f\n\n" +
                    "How can I assist you right now? I can help with restock calculations, billing analysis, dead stock clearance, or supplier purchase orders.",
                    insights.getStoreHealthScore(), insights.getTotalInventoryValue(), insights.getTotalProducts(),
                    insights.getCriticalStockoutsCount(), insights.getTotalLockedCapitalInDeadStock());

            followups.add("What items need restock?");
            followups.add("Show dead stock items");
            followups.add("What are today's sales?");
        }

        return new AiChatResponseDto(reply, intent, context, followups);
    }
}
