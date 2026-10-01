package com.shelfiq.inventory.dto;

import java.time.LocalDateTime;

public class InventoryMovementDto {

    private Long id;
    private Long productId;
    private String productSku;
    private String productName;
    private String movementType;
    private String referenceType;
    private String referenceId;
    private Integer quantityDelta;
    private Integer previousStock;
    private Integer newStock;
    private String reason;
    private String performedBy;
    private LocalDateTime timestamp;

    public InventoryMovementDto() {
    }

    public InventoryMovementDto(Long id, Long productId, String productSku, String productName,
                                String movementType, String referenceType, String referenceId,
                                Integer quantityDelta, Integer previousStock, Integer newStock,
                                String reason, String performedBy, LocalDateTime timestamp) {
        this.id = id;
        this.productId = productId;
        this.productSku = productSku;
        this.productName = productName;
        this.movementType = movementType;
        this.referenceType = referenceType;
        this.referenceId = referenceId;
        this.quantityDelta = quantityDelta;
        this.previousStock = previousStock;
        this.newStock = newStock;
        this.reason = reason;
        this.performedBy = performedBy;
        this.timestamp = timestamp;
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

    public String getProductSku() {
        return productSku;
    }

    public void setProductSku(String productSku) {
        this.productSku = productSku;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getMovementType() {
        return movementType;
    }

    public void setMovementType(String movementType) {
        this.movementType = movementType;
    }

    public String getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(String referenceType) {
        this.referenceType = referenceType;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public Integer getQuantityDelta() {
        return quantityDelta;
    }

    public void setQuantityDelta(Integer quantityDelta) {
        this.quantityDelta = quantityDelta;
    }

    public Integer getPreviousStock() {
        return previousStock;
    }

    public void setPreviousStock(Integer previousStock) {
        this.previousStock = previousStock;
    }

    public Integer getNewStock() {
        return newStock;
    }

    public void setNewStock(Integer newStock) {
        this.newStock = newStock;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(String performedBy) {
        this.performedBy = performedBy;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
