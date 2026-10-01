package com.shelfiq.store.entity;

import com.shelfiq.common.domain.BaseEntity;
import com.shelfiq.user.entity.User;
import jakarta.persistence.*;

@Entity
@Table(name = "stores")
public class Store extends BaseEntity {

    @Column(name = "store_code", unique = true, nullable = false, length = 50)
    private String storeCode;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "business_type", nullable = false, length = 50)
    private String businessType;

    @Column(name = "currency", nullable = false, length = 10)
    private String currency = "INR";

    @Column(name = "timezone", nullable = false, length = 50)
    private String timezone = "Asia/Kolkata";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @OneToOne(mappedBy = "store", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private StoreLocation location;

    public Store() {
    }

    public Store(String storeCode, String name, String businessType, User owner) {
        this.storeCode = storeCode;
        this.name = name;
        this.businessType = businessType;
        this.owner = owner;
        this.currency = "INR";
        this.timezone = "Asia/Kolkata";
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

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public StoreLocation getLocation() {
        return location;
    }

    public void setLocation(StoreLocation location) {
        this.location = location;
        if (location != null) {
            location.setStore(this);
        }
    }
}
