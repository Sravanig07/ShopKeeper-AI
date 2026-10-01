package com.shelfiq.sales.dto;

import com.shelfiq.sales.entity.SaleTransaction.PaymentMethod;
import com.shelfiq.sales.entity.SaleTransaction.SaleStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class SaleResponseDto {
    private Long id;
    private String invoiceNumber;
    private Long storeId;
    private String cashierName;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal taxAmount;
    private BigDecimal netAmount;
    private PaymentMethod paymentMethod;
    private String paymentReference;
    private String customerName;
    private String customerPhone;
    private SaleStatus status;
    private LocalDateTime createdAt;
    private List<SaleItemResponseDto> items;

    public SaleResponseDto() {
    }

    public SaleResponseDto(Long id, String invoiceNumber, Long storeId, String cashierName,
                           BigDecimal totalAmount, BigDecimal discountAmount, BigDecimal taxAmount,
                           BigDecimal netAmount, PaymentMethod paymentMethod, String paymentReference,
                           String customerName, String customerPhone, SaleStatus status,
                           LocalDateTime createdAt, List<SaleItemResponseDto> items) {
        this.id = id;
        this.invoiceNumber = invoiceNumber;
        this.storeId = storeId;
        this.cashierName = cashierName;
        this.totalAmount = totalAmount;
        this.discountAmount = discountAmount;
        this.taxAmount = taxAmount;
        this.netAmount = netAmount;
        this.paymentMethod = paymentMethod;
        this.paymentReference = paymentReference;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.status = status;
        this.createdAt = createdAt;
        this.items = items;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public String getCashierName() {
        return cashierName;
    }

    public void setCashierName(String cashierName) {
        this.cashierName = cashierName;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public BigDecimal getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(BigDecimal netAmount) {
        this.netAmount = netAmount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public void setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public SaleStatus getStatus() {
        return status;
    }

    public void setStatus(SaleStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<SaleItemResponseDto> getItems() {
        return items;
    }

    public void setItems(List<SaleItemResponseDto> items) {
        this.items = items;
    }
}
