package com.shelfiq.ocr.dto;

import java.math.BigDecimal;

public class OcrInvoiceItemDto {
    private String rawDescription;
    private Long matchedProductId;
    private String matchedProductName;
    private String sku;
    private Integer quantity;
    private BigDecimal unitCost;
    private BigDecimal lineTotal;
    private double matchConfidence;
    private boolean matched;

    public OcrInvoiceItemDto() {
    }

    public OcrInvoiceItemDto(String rawDescription, Long matchedProductId, String matchedProductName,
                             String sku, Integer quantity, BigDecimal unitCost, BigDecimal lineTotal,
                             double matchConfidence, boolean matched) {
        this.rawDescription = rawDescription;
        this.matchedProductId = matchedProductId;
        this.matchedProductName = matchedProductName;
        this.sku = sku;
        this.quantity = quantity;
        this.unitCost = unitCost;
        this.lineTotal = lineTotal;
        this.matchConfidence = matchConfidence;
        this.matched = matched;
    }

    public String getRawDescription() {
        return rawDescription;
    }

    public void setRawDescription(String rawDescription) {
        this.rawDescription = rawDescription;
    }

    public Long getMatchedProductId() {
        return matchedProductId;
    }

    public void setMatchedProductId(Long matchedProductId) {
        this.matchedProductId = matchedProductId;
    }

    public String getMatchedProductName() {
        return matchedProductName;
    }

    public void setMatchedProductName(String matchedProductName) {
        this.matchedProductName = matchedProductName;
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

    public BigDecimal getLineTotal() {
        return lineTotal;
    }

    public void setLineTotal(BigDecimal lineTotal) {
        this.lineTotal = lineTotal;
    }

    public double getMatchConfidence() {
        return matchConfidence;
    }

    public void setMatchConfidence(double matchConfidence) {
        this.matchConfidence = matchConfidence;
    }

    public boolean isMatched() {
        return matched;
    }

    public void setMatched(boolean matched) {
        this.matched = matched;
    }
}
