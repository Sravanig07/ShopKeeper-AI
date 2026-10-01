package com.shelfiq.inventory.dto;

import java.math.BigDecimal;

public class InventorySummaryDto {

    private int totalProducts;
    private int totalUnitsInStock;
    private BigDecimal totalInventoryValue;
    private int lowStockCount;
    private int outOfStockCount;

    public InventorySummaryDto() {
    }

    public InventorySummaryDto(int totalProducts, int totalUnitsInStock, BigDecimal totalInventoryValue, int lowStockCount, int outOfStockCount) {
        this.totalProducts = totalProducts;
        this.totalUnitsInStock = totalUnitsInStock;
        this.totalInventoryValue = totalInventoryValue;
        this.lowStockCount = lowStockCount;
        this.outOfStockCount = outOfStockCount;
    }

    public int getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(int totalProducts) {
        this.totalProducts = totalProducts;
    }

    public int getTotalUnitsInStock() {
        return totalUnitsInStock;
    }

    public void setTotalUnitsInStock(int totalUnitsInStock) {
        this.totalUnitsInStock = totalUnitsInStock;
    }

    public BigDecimal getTotalInventoryValue() {
        return totalInventoryValue;
    }

    public void setTotalInventoryValue(BigDecimal totalInventoryValue) {
        this.totalInventoryValue = totalInventoryValue;
    }

    public int getLowStockCount() {
        return lowStockCount;
    }

    public void setLowStockCount(int lowStockCount) {
        this.lowStockCount = lowStockCount;
    }

    public int getOutOfStockCount() {
        return outOfStockCount;
    }

    public void setOutOfStockCount(int outOfStockCount) {
        this.outOfStockCount = outOfStockCount;
    }
}
