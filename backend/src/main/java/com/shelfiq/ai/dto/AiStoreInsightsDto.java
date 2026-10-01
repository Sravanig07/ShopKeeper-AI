package com.shelfiq.ai.dto;

import java.math.BigDecimal;
import java.util.List;

public class AiStoreInsightsDto {
    private int storeHealthScore; // 0 to 100
    private BigDecimal totalInventoryValue;
    private int totalProducts;
    private int criticalStockoutsCount;
    private int lowStockWarningsCount;
    private int deadStockItemsCount;
    private BigDecimal totalLockedCapitalInDeadStock;
    private List<String> criticalAlerts;
    private List<String> strategicTips;

    public AiStoreInsightsDto() {
    }

    public AiStoreInsightsDto(int storeHealthScore, BigDecimal totalInventoryValue, int totalProducts,
                              int criticalStockoutsCount, int lowStockWarningsCount, int deadStockItemsCount,
                              BigDecimal totalLockedCapitalInDeadStock, List<String> criticalAlerts,
                              List<String> strategicTips) {
        this.storeHealthScore = storeHealthScore;
        this.totalInventoryValue = totalInventoryValue;
        this.totalProducts = totalProducts;
        this.criticalStockoutsCount = criticalStockoutsCount;
        this.lowStockWarningsCount = lowStockWarningsCount;
        this.deadStockItemsCount = deadStockItemsCount;
        this.totalLockedCapitalInDeadStock = totalLockedCapitalInDeadStock;
        this.criticalAlerts = criticalAlerts;
        this.strategicTips = strategicTips;
    }

    public int getStoreHealthScore() {
        return storeHealthScore;
    }

    public void setStoreHealthScore(int storeHealthScore) {
        this.storeHealthScore = storeHealthScore;
    }

    public BigDecimal getTotalInventoryValue() {
        return totalInventoryValue;
    }

    public void setTotalInventoryValue(BigDecimal totalInventoryValue) {
        this.totalInventoryValue = totalInventoryValue;
    }

    public int getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(int totalProducts) {
        this.totalProducts = totalProducts;
    }

    public int getCriticalStockoutsCount() {
        return criticalStockoutsCount;
    }

    public void setCriticalStockoutsCount(int criticalStockoutsCount) {
        this.criticalStockoutsCount = criticalStockoutsCount;
    }

    public int getLowStockWarningsCount() {
        return lowStockWarningsCount;
    }

    public void setLowStockWarningsCount(int lowStockWarningsCount) {
        this.lowStockWarningsCount = lowStockWarningsCount;
    }

    public int getDeadStockItemsCount() {
        return deadStockItemsCount;
    }

    public void setDeadStockItemsCount(int deadStockItemsCount) {
        this.deadStockItemsCount = deadStockItemsCount;
    }

    public BigDecimal getTotalLockedCapitalInDeadStock() {
        return totalLockedCapitalInDeadStock;
    }

    public void setTotalLockedCapitalInDeadStock(BigDecimal totalLockedCapitalInDeadStock) {
        this.totalLockedCapitalInDeadStock = totalLockedCapitalInDeadStock;
    }

    public List<String> getCriticalAlerts() {
        return criticalAlerts;
    }

    public void setCriticalAlerts(List<String> criticalAlerts) {
        this.criticalAlerts = criticalAlerts;
    }

    public List<String> getStrategicTips() {
        return strategicTips;
    }

    public void setStrategicTips(List<String> strategicTips) {
        this.strategicTips = strategicTips;
    }
}
