package com.shelfiq.purchase.dto;

import com.shelfiq.purchase.entity.PurchaseOrder.PoStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class PurchaseOrderResponseDto {
    private Long id;
    private String poNumber;
    private Long storeId;
    private Long supplierId;
    private String supplierName;
    private PoStatus status;
    private BigDecimal totalCost;
    private String notes;
    private LocalDate expectedDeliveryDate;
    private LocalDateTime receivedAt;
    private String receivedBy;
    private LocalDateTime createdAt;
    private List<PurchaseOrderItemDto> items;

    public PurchaseOrderResponseDto() {
    }

    public PurchaseOrderResponseDto(Long id, String poNumber, Long storeId, Long supplierId,
                                    String supplierName, PoStatus status, BigDecimal totalCost,
                                    String notes, LocalDate expectedDeliveryDate, LocalDateTime receivedAt,
                                    String receivedBy, LocalDateTime createdAt, List<PurchaseOrderItemDto> items) {
        this.id = id;
        this.poNumber = poNumber;
        this.storeId = storeId;
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.status = status;
        this.totalCost = totalCost;
        this.notes = notes;
        this.expectedDeliveryDate = expectedDeliveryDate;
        this.receivedAt = receivedAt;
        this.receivedBy = receivedBy;
        this.createdAt = createdAt;
        this.items = items;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPoNumber() {
        return poNumber;
    }

    public void setPoNumber(String poNumber) {
        this.poNumber = poNumber;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
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

    public PoStatus getStatus() {
        return status;
    }

    public void setStatus(PoStatus status) {
        this.status = status;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDate getExpectedDeliveryDate() {
        return expectedDeliveryDate;
    }

    public void setExpectedDeliveryDate(LocalDate expectedDeliveryDate) {
        this.expectedDeliveryDate = expectedDeliveryDate;
    }

    public LocalDateTime getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(LocalDateTime receivedAt) {
        this.receivedAt = receivedAt;
    }

    public String getReceivedBy() {
        return receivedBy;
    }

    public void setReceivedBy(String receivedBy) {
        this.receivedBy = receivedBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<PurchaseOrderItemDto> getItems() {
        return items;
    }

    public void setItems(List<PurchaseOrderItemDto> items) {
        this.items = items;
    }
}
