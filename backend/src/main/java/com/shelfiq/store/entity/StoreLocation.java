package com.shelfiq.store.entity;

import com.shelfiq.common.domain.BaseEntity;
import com.shelfiq.store.util.GeohashUtil;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "store_locations")
public class StoreLocation extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false, unique = true)
    private Store store;

    @Column(name = "address_line1", nullable = false)
    private String addressLine1;

    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @Column(name = "state", nullable = false, length = 100)
    private String state;

    @Column(name = "postal_code", nullable = false, length = 20)
    private String postalCode;

    @Column(name = "latitude", nullable = false, precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(name = "longitude", nullable = false, precision = 10, scale = 7)
    private BigDecimal longitude;

    @Column(name = "geohash_5", nullable = false, length = 5)
    private String geohash5;

    @Column(name = "geohash_6", nullable = false, length = 6)
    private String geohash6;

    public StoreLocation() {
    }

    public StoreLocation(String addressLine1, String city, String state, String postalCode, BigDecimal latitude, BigDecimal longitude) {
        this.addressLine1 = addressLine1;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.latitude = latitude;
        this.longitude = longitude;
        calculateGeohashes();
    }

    @PrePersist
    @PreUpdate
    public void calculateGeohashes() {
        if (latitude != null && longitude != null) {
            double lat = latitude.doubleValue();
            double lon = longitude.doubleValue();
            this.geohash5 = GeohashUtil.encode(lat, lon, 5);
            this.geohash6 = GeohashUtil.encode(lat, lon, 6);
        }
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
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
        calculateGeohashes();
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
        calculateGeohashes();
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
