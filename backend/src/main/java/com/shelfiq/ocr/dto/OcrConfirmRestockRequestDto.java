package com.shelfiq.ocr.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public class OcrConfirmRestockRequestDto {

    private Long supplierId;

    @NotNull(message = "Invoice number is required")
    private String invoiceNumber;

    @NotEmpty(message = "At least one item must be restocked")
    private List<RestockItem> items;

    public static class RestockItem {
        @NotNull(message = "Product ID is required")
        private Long productId;

        @NotNull(message = "Quantity is required")
        private Integer quantity;

        private BigDecimal unitCost;

        public RestockItem() {
        }

        public RestockItem(Long productId, Integer quantity, BigDecimal unitCost) {
            this.productId = productId;
            this.quantity = quantity;
            this.unitCost = unitCost;
        }

        public Long getProductId() {
            return productId;
        }

        public void setProductId(Long productId) {
            this.productId = productId;
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
    }

    public OcrConfirmRestockRequestDto() {
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public List<RestockItem> getItems() {
        return items;
    }

    public void setItems(List<RestockItem> items) {
        this.items = items;
    }
}
