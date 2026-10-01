package com.shelfiq.ai.dto;

import java.math.BigDecimal;

public class DeadStockDto {
    private Long productId;
    private String productName;
    private String sku;
    private String categoryName;
    private int currentStock;
    private BigDecimal unitCost;
    private BigDecimal lockedCapital;
    private int daysSinceLastSale;
    private String suggestedAction;

    public DeadStockDto() {
    }

    public DeadStockDto(Long productId, String productName, String sku, String categoryName,
                        int currentStock, BigDecimal unitCost, BigDecimal lockedCapital,
                        int daysSinceLastSale, String suggestedAction) {
        this.productId = productId;
        this.productName = productName;
        this.sku = sku;
        this.categoryName = categoryName;
        this.currentStock = currentStock;
        this.unitCost = unitCost;
        this.lockedCapital = lockedCapital;
        this.daysSinceLastSale = daysSinceLastSale;
        this.suggestedAction = suggestedAction;
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

    public BigDecimal getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(BigDecimal unitCost) {
        this.unitCost = unitCost;
    }

    public BigDecimal getLockedCapital() {
        return lockedCapital;
    }

    public void setLockedCapital(BigDecimal lockedCapital) {
        this.lockedCapital = lockedCapital;
    }

    public int getDaysSinceLastSale() {
        return daysSinceLastSale;
    }

    public void setDaysSinceLastSale(int daysSinceLastSale) {
        this.daysSinceLastSale = daysSinceLastSale;
    }

    public String getSuggestedAction() {
        return suggestedAction;
    }

    public void setSuggestedAction(String suggestedAction) {
        this.suggestedAction = suggestedAction;
    }
}
