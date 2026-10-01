package com.shelfiq.store.dto;

import java.math.BigDecimal;

public class StoreResponseDto {

    private Long id;
    private String storeCode;
    private String name;
    private String businessType;
    private String currency;
    private String timezone;
    private String addressLine1;
    private String city;
    private String state;
    private String postalCode;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String geohash5;
    private String geohash6;

    public StoreResponseDto() {
    }

    public StoreResponseDto(Long id, String storeCode, String name, String businessType, String currency, String timezone,
                            String addressLine1, String city, String state, String postalCode,
                            BigDecimal latitude, BigDecimal longitude, String geohash5, String geohash6) {
        this.id = id;
        this.storeCode = storeCode;
        this.name = name;
        this.businessType = businessType;
        this.currency = currency;
        this.timezone = timezone;
        this.addressLine1 = addressLine1;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.latitude = latitude;
        this.longitude = longitude;
        this.geohash5 = geohash5;
        this.geohash6 = geohash6;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStoreCode() {
        return storeCode;
    }

    public void setStoreCode(String storeCode) {
        this.storeCode = storeCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public String getGeohash5() {
        return geohash5;
    }

    public void setGeohash5(String geohash5) {
        this.geohash5 = geohash5;
    }

    public String getGeohash6() {
        return geohash6;
    }

    public void setGeohash6(String geohash6) {
        this.geohash6 = geohash6;
    }
}
