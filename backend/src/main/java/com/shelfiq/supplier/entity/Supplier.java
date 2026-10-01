package com.shelfiq.supplier.entity;

import com.shelfiq.common.domain.BaseEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "suppliers", indexes = {
        @Index(name = "idx_suppliers_store_id", columnList = "store_id")
})
public class Supplier extends BaseEntity {

    @Column(name = "store_id", nullable = false)
    private Long storeId;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "contact_person", length = 100)
    private String contactPerson;

    @Column(name = "email", length = 150)
    private String email;

    @Column(name = "phone", length = 50)
    private String phone;

    @Column(name = "lead_time_days", nullable = false)
    private Integer leadTimeDays = 3;

    @Column(name = "reliability_score", precision = 3, scale = 2)
    private BigDecimal reliabilityScore = new BigDecimal("0.95");

    public Supplier() {
    }

    public Supplier(Long storeId, String name, String contactPerson, String email, String phone, Integer leadTimeDays, BigDecimal reliabilityScore) {
        this.storeId = storeId;
        this.name = name;
        this.contactPerson = contactPerson;
        this.email = email;
        this.phone = phone;
        this.leadTimeDays = leadTimeDays != null ? leadTimeDays : 3;
        this.reliabilityScore = reliabilityScore != null ? reliabilityScore : new BigDecimal("0.95");
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getLeadTimeDays() {
        return leadTimeDays;
    }

    public void setLeadTimeDays(Integer leadTimeDays) {
        this.leadTimeDays = leadTimeDays;
    }

    public BigDecimal getReliabilityScore() {
        return reliabilityScore;
    }

    public void setReliabilityScore(BigDecimal reliabilityScore) {
        this.reliabilityScore = reliabilityScore;
    }
}
