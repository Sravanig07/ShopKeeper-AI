package com.shelfiq.sales.dto;

import java.math.BigDecimal;
import java.util.List;

public class LeaderboardResponseDto {

    private String groupBy; // CITY, STATE, REGION
    private String type; // ITEM, CATEGORY
    private String timeRange; // TODAY, WEEK, MONTH, ALL
    private List<LeaderboardGroupDto> groups;

    public LeaderboardResponseDto() {
    }

    public LeaderboardResponseDto(String groupBy, String type, String timeRange, List<LeaderboardGroupDto> groups) {
        this.groupBy = groupBy;
        this.type = type;
        this.timeRange = timeRange;
        this.groups = groups;
    }

    public String getGroupBy() {
        return groupBy;
    }

    public void setGroupBy(String groupBy) {
        this.groupBy = groupBy;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTimeRange() {
        return timeRange;
    }

    public void setTimeRange(String timeRange) {
        this.timeRange = timeRange;
    }

    public List<LeaderboardGroupDto> getGroups() {
        return groups;
    }

    public void setGroups(List<LeaderboardGroupDto> groups) {
        this.groups = groups;
    }

    public static class LeaderboardGroupDto {
        private String geoName; // e.g. "Bengaluru", "Karnataka", "South India", etc.
        private String geoType; // CITY, STATE, REGION
        private long totalUnits;
        private BigDecimal totalRevenue;
        private List<RankedItemDto> rankings;

        public LeaderboardGroupDto() {
        }

        public LeaderboardGroupDto(String geoName, String geoType, long totalUnits, BigDecimal totalRevenue, List<RankedItemDto> rankings) {
            this.geoName = geoName;
            this.geoType = geoType;
            this.totalUnits = totalUnits;
            this.totalRevenue = totalRevenue;
            this.rankings = rankings;
        }

        public String getGeoName() {
            return geoName;
        }

        public void setGeoName(String geoName) {
            this.geoName = geoName;
        }

        public String getGeoType() {
            return geoType;
        }

        public void setGeoType(String geoType) {
            this.geoType = geoType;
        }

        public long getTotalUnits() {
            return totalUnits;
        }

        public void setTotalUnits(long totalUnits) {
            this.totalUnits = totalUnits;
        }

        public BigDecimal getTotalRevenue() {
            return totalRevenue;
        }

        public void setTotalRevenue(BigDecimal totalRevenue) {
            this.totalRevenue = totalRevenue;
        }

        public List<RankedItemDto> getRankings() {
            return rankings;
        }

        public void setRankings(List<RankedItemDto> rankings) {
            this.rankings = rankings;
        }
    }

    public static class RankedItemDto {
        private int rank;
        private Long id;
        private String name;
        private String category;
        private String sku;
        private long unitsSold;
        private BigDecimal revenue;
        private double marketSharePercentage;
        private double growthRate; // velocity change %
        private String dominantCity;

        public RankedItemDto() {
        }

        public RankedItemDto(int rank, Long id, String name, String category, String sku, long unitsSold, BigDecimal revenue, double marketSharePercentage, double growthRate, String dominantCity) {
            this.rank = rank;
            this.id = id;
            this.name = name;
            this.category = category;
            this.sku = sku;
            this.unitsSold = unitsSold;
            this.revenue = revenue;
            this.marketSharePercentage = marketSharePercentage;
            this.growthRate = growthRate;
            this.dominantCity = dominantCity;
        }

        public int getRank() {
            return rank;
        }

        public void setRank(int rank) {
            this.rank = rank;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
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

        public BigDecimal getRevenue() {
            return revenue;
        }

        public void setRevenue(BigDecimal revenue) {
            this.revenue = revenue;
        }

        public double getMarketSharePercentage() {
            return marketSharePercentage;
        }

        public void setMarketSharePercentage(double marketSharePercentage) {
            this.marketSharePercentage = marketSharePercentage;
        }

        public double getGrowthRate() {
            return growthRate;
        }

        public void setGrowthRate(double growthRate) {
            this.growthRate = growthRate;
        }

        public String getDominantCity() {
            return dominantCity;
        }

        public void setDominantCity(String dominantCity) {
            this.dominantCity = dominantCity;
        }
    }
}
