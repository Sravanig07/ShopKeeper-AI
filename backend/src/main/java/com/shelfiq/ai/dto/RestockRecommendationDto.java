package com.shelfiq.ai.dto;

import java.math.BigDecimal;

public class RestockRecommendationDto {
    private Long productId;
    private String productName;
    private String sku;
    private String categoryName;
    private int currentStock;
    private int safetyStock;
    private int minStock;
    private double dailySalesVelocity;
    private double daysOfInventoryRemaining;
    private String stockoutRiskLevel; // CRITICAL, WARNING, OPTIMAL
    private Long supplierId;
    private String supplierName;
    private int supplierLeadTimeDays;
    private int reorderPoint;
    private int recommendedOrderQuantity;
    private BigDecimal unitCost;
    private BigDecimal estimatedRestockCost;

    public RestockRecommendationDto() {
    }

    public RestockRecommendationDto(Long productId, String productName, String sku, String categoryName,
                                    int currentStock, int safetyStock, int minStock, double dailySalesVelocity,
                                    double daysOfInventoryRemaining, String stockoutRiskLevel, Long supplierId,
                                    String supplierName, int supplierLeadTimeDays, int reorderPoint,
                                    int recommendedOrderQuantity, BigDecimal unitCost, BigDecimal estimatedRestockCost) {
        this.productId = productId;
        this.productName = productName;
        this.sku = sku;
        this.categoryName = categoryName;
        this.currentStock = currentStock;
        this.safetyStock = safetyStock;
        this.minStock = minStock;
        this.dailySalesVelocity = dailySalesVelocity;
        this.daysOfInventoryRemaining = daysOfInventoryRemaining;
        this.stockoutRiskLevel = stockoutRiskLevel;
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.supplierLeadTimeDays = supplierLeadTimeDays;
        this.reorderPoint = reorderPoint;
        this.recommendedOrderQuantity = recommendedOrderQuantity;
        this.unitCost = unitCost;
        this.estimatedRestockCost = estimatedRestockCost;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(int currentStock) {
        this.currentStock = currentStock;
    }

    public int getSafetyStock() {
        return safetyStock;
    }

    public void setSafetyStock(int safetyStock) {
        this.safetyStock = safetyStock;
    }

    public int getMinStock() {
        return minStock;
    }

    public void setMinStock(int minStock) {
        this.minStock = minStock;
    }

    public double getDailySalesVelocity() {
        return dailySalesVelocity;
    }

    public void setDailySalesVelocity(double dailySalesVelocity) {
        this.dailySalesVelocity = dailySalesVelocity;
    }

    public double getDaysOfInventoryRemaining() {
        return daysOfInventoryRemaining;
    }

    public void setDaysOfInventoryRemaining(double daysOfInventoryRemaining) {
        this.daysOfInventoryRemaining = daysOfInventoryRemaining;
    }

    public String getStockoutRiskLevel() {
        return stockoutRiskLevel;
    }

    public void setStockoutRiskLevel(String stockoutRiskLevel) {
        this.stockoutRiskLevel = stockoutRiskLevel;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public int getSupplierLeadTimeDays() {
        return supplierLeadTimeDays;
    }

    public void setSupplierLeadTimeDays(int supplierLeadTimeDays) {
        this.supplierLeadTimeDays = supplierLeadTimeDays;
    }

    public int getReorderPoint() {
        return reorderPoint;
    }

    public void setReorderPoint(int reorderPoint) {
        this.reorderPoint = reorderPoint;
    }

    public int getRecommendedOrderQuantity() {
        return recommendedOrderQuantity;
    }

    public void setRecommendedOrderQuantity(int recommendedOrderQuantity) {
        this.recommendedOrderQuantity = recommendedOrderQuantity;
    }

    public BigDecimal getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(BigDecimal unitCost) {
        this.unitCost = unitCost;
    }

    public BigDecimal getEstimatedRestockCost() {
        return estimatedRestockCost;
    }

    public void setEstimatedRestockCost(BigDecimal estimatedRestockCost) {
        this.estimatedRestockCost = estimatedRestockCost;
    }
}
