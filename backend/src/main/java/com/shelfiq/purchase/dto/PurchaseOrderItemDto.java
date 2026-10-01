package com.shelfiq.purchase.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class PurchaseOrderItemDto {
    private Long id;

    @NotNull(message = "Product ID is required")
    private Long productId;

    private String productName;
    private String sku;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    private BigDecimal unitCost;
    private BigDecimal subtotal;

    public PurchaseOrderItemDto() {
    }

    public PurchaseOrderItemDto(Long id, Long productId, String productName, String sku,
                                Integer quantity, BigDecimal unitCost, BigDecimal subtotal) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.sku = sku;
        this.quantity = quantity;
        this.unitCost = unitCost;
        this.subtotal = subtotal;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(BigDecimal unitCost) {
        this.unitCost = unitCost;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
}
