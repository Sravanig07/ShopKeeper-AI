package com.shelfiq.ocr.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class OcrParsedInvoiceDto {
    private String supplierName;
    private Long matchedSupplierId;
    private String invoiceNumber;
    private LocalDate invoiceDate;
    private BigDecimal totalAmount;
    private String extractedTextSummary;
    private List<OcrInvoiceItemDto> items;

    public OcrParsedInvoiceDto() {
    }

    public OcrParsedInvoiceDto(String supplierName, Long matchedSupplierId, String invoiceNumber,
                               LocalDate invoiceDate, BigDecimal totalAmount, String extractedTextSummary,
                               List<OcrInvoiceItemDto> items) {
        this.supplierName = supplierName;
        this.matchedSupplierId = matchedSupplierId;
        this.invoiceNumber = invoiceNumber;
        this.invoiceDate = invoiceDate;
        this.totalAmount = totalAmount;
        this.extractedTextSummary = extractedTextSummary;
        this.items = items;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public Long getMatchedSupplierId() {
        return matchedSupplierId;
    }

    public void setMatchedSupplierId(Long matchedSupplierId) {
        this.matchedSupplierId = matchedSupplierId;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public LocalDate getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(LocalDate invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getExtractedTextSummary() {
        return extractedTextSummary;
    }

    public void setExtractedTextSummary(String extractedTextSummary) {
        this.extractedTextSummary = extractedTextSummary;
    }

    public List<OcrInvoiceItemDto> getItems() {
        return items;
    }

    public void setItems(List<OcrInvoiceItemDto> items) {
        this.items = items;
    }
}
