package com.shelfiq.product.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class ProductResponseDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String sku;
    private String barcode;
    private String name;
    private String brand;
    private Long categoryId;
    private String categoryName;
    private String description;
    private BigDecimal sellingPrice;
    private BigDecimal costPrice;
    private Integer minStock;
    private Integer safetyStock;
    private Integer currentStock;
    private Integer incomingStock;
    private Integer reservedStock;
    private String stockStatus;
    private Long supplierId;
    private String supplierName;
    private Integer leadTimeDays;
    private String unitOfMeasure;
    private boolean active;

    public ProductResponseDto() {
    }

    public ProductResponseDto(Long id, String sku, String barcode, String name, String brand,
                              Long categoryId, String categoryName, String description,
                              BigDecimal sellingPrice, BigDecimal costPrice,
                              Integer minStock, Integer safetyStock,
                              Integer currentStock, Integer incomingStock, Integer reservedStock,
                              String stockStatus, Long supplierId, String supplierName,
                              Integer leadTimeDays, String unitOfMeasure, boolean active) {
        this.id = id;
        this.sku = sku;
        this.barcode = barcode;
        this.name = name;
        this.brand = brand;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.description = description;
        this.sellingPrice = sellingPrice;
        this.costPrice = costPrice;
        this.minStock = minStock;
        this.safetyStock = safetyStock;
        this.currentStock = currentStock;
        this.incomingStock = incomingStock;
        this.reservedStock = reservedStock;
        this.stockStatus = stockStatus;
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.leadTimeDays = leadTimeDays;
        this.unitOfMeasure = unitOfMeasure;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(BigDecimal sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public BigDecimal getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(BigDecimal costPrice) {
        this.costPrice = costPrice;
    }

    public Integer getMinStock() {
        return minStock;
    }

    public void setMinStock(Integer minStock) {
        this.minStock = minStock;
    }

    public Integer getSafetyStock() {
        return safetyStock;
    }

    public void setSafetyStock(Integer safetyStock) {
        this.safetyStock = safetyStock;
    }

    public Integer getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(Integer currentStock) {
        this.currentStock = currentStock;
    }

    public Integer getIncomingStock() {
        return incomingStock;
    }

    public void setIncomingStock(Integer incomingStock) {
        this.incomingStock = incomingStock;
    }

    public Integer getReservedStock() {
        return reservedStock;
    }

    public void setReservedStock(Integer reservedStock) {
        this.reservedStock = reservedStock;
    }

    public String getStockStatus() {
        return stockStatus;
    }

    public void setStockStatus(String stockStatus) {
        this.stockStatus = stockStatus;
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

    public Integer getLeadTimeDays() {
        return leadTimeDays;
    }

    public void setLeadTimeDays(Integer leadTimeDays) {
        this.leadTimeDays = leadTimeDays;
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
