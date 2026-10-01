package com.shelfiq.store.dto;

import java.time.LocalDateTime;

public class EmployeeResponseDto {

    private Long id;
    private Long userId;
    private String fullName;
    private String email;
    private String phone;
    private String designation;
    private boolean active;
    private LocalDateTime joinedAt;

    public EmployeeResponseDto() {
    }

    public EmployeeResponseDto(Long id, Long userId, String fullName, String email, String phone, String designation, boolean active, LocalDateTime joinedAt) {
        this.id = id;
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.designation = designation;
        this.active = active;
        this.joinedAt = joinedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }
}
