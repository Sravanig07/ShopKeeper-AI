package com.shelfiq.purchase.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public class PurchaseOrderCreateDto {

    @NotNull(message = "Supplier ID is required")
    private Long supplierId;

    @NotEmpty(message = "Items list cannot be empty")
    @Valid
    private List<PurchaseOrderItemDto> items;

    private String notes;

    private LocalDate expectedDeliveryDate;

    public PurchaseOrderCreateDto() {
    }

    public PurchaseOrderCreateDto(Long supplierId, List<PurchaseOrderItemDto> items, String notes, LocalDate expectedDeliveryDate) {
        this.supplierId = supplierId;
        this.items = items;
        this.notes = notes;
        this.expectedDeliveryDate = expectedDeliveryDate;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public List<PurchaseOrderItemDto> getItems() {
        return items;
    }

    public void setItems(List<PurchaseOrderItemDto> items) {
        this.items = items;
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
}
