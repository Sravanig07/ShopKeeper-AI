package com.shelfiq.supplier.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class SupplierDto {

    private Long id;

    @NotBlank(message = "Supplier name is required")
    private String name;

    private String contactPerson;
    private String email;
    private String phone;

    @NotNull(message = "Lead time in days is required")
    @Positive(message = "Lead time must be at least 1 day")
    private Integer leadTimeDays = 3;

    private BigDecimal reliabilityScore = new BigDecimal("0.95");

    public SupplierDto() {
    }

    public SupplierDto(Long id, String name, String contactPerson, String email, String phone, Integer leadTimeDays, BigDecimal reliabilityScore) {
        this.id = id;
        this.name = name;
        this.contactPerson = contactPerson;
        this.email = email;
        this.phone = phone;
        this.leadTimeDays = leadTimeDays;
        this.reliabilityScore = reliabilityScore;
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
