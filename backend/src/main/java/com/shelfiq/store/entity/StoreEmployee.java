package com.shelfiq.store.entity;

import com.shelfiq.common.domain.BaseEntity;
import com.shelfiq.user.entity.User;
import jakarta.persistence.*;

@Entity
@Table(name = "store_employees")
public class StoreEmployee extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "designation", length = 50)
    private String designation;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    public StoreEmployee() {
    }

    public StoreEmployee(Store store, User user, String designation) {
        this.store = store;
        this.user = user;
        this.designation = designation;
        this.isActive = true;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
