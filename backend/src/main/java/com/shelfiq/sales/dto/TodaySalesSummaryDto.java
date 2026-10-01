package com.shelfiq.sales.dto;

import java.math.BigDecimal;
import java.util.List;

public class TodaySalesSummaryDto {
    private BigDecimal totalRevenue;
    private long transactionCount;
    private BigDecimal averageTicketSize;
    private long totalUnitsSold;
    private List<TopSoldItemDto> topSoldItems;

    public static class TopSoldItemDto {
        private Long productId;
        private String productName;
        private String sku;
        private long unitsSold;
        private BigDecimal totalRevenue;

        public TopSoldItemDto() {
        }

        public TopSoldItemDto(Long productId, String productName, String sku, long unitsSold, BigDecimal totalRevenue) {
            this.productId = productId;
            this.productName = productName;
            this.sku = sku;
            this.unitsSold = unitsSold;
            this.totalRevenue = totalRevenue;
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

        public long getUnitsSold() {
            return unitsSold;
        }

        public void setUnitsSold(long unitsSold) {
            this.unitsSold = unitsSold;
        }

        public BigDecimal getTotalRevenue() {
            return totalRevenue;
        }

        public void setTotalRevenue(BigDecimal totalRevenue) {
            this.totalRevenue = totalRevenue;
        }
    }

    public TodaySalesSummaryDto() {
    }

    public TodaySalesSummaryDto(BigDecimal totalRevenue, long transactionCount, BigDecimal averageTicketSize,
                                long totalUnitsSold, List<TopSoldItemDto> topSoldItems) {
        this.totalRevenue = totalRevenue;
        this.transactionCount = transactionCount;
        this.averageTicketSize = averageTicketSize;
        this.totalUnitsSold = totalUnitsSold;
        this.topSoldItems = topSoldItems;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public long getTransactionCount() {
        return transactionCount;
    }

    public void setTransactionCount(long transactionCount) {
        this.transactionCount = transactionCount;
    }

    public BigDecimal getAverageTicketSize() {
        return averageTicketSize;
    }

    public void setAverageTicketSize(BigDecimal averageTicketSize) {
        this.averageTicketSize = averageTicketSize;
    }

    public long getTotalUnitsSold() {
        return totalUnitsSold;
    }

    public void setTotalUnitsSold(long totalUnitsSold) {
        this.totalUnitsSold = totalUnitsSold;
    }

    public List<TopSoldItemDto> getTopSoldItems() {
        return topSoldItems;
    }

    public void setTopSoldItems(List<TopSoldItemDto> topSoldItems) {
        this.topSoldItems = topSoldItems;
    }
}
