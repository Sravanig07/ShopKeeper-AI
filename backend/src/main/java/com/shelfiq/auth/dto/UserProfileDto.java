package com.shelfiq.auth.dto;

import java.util.Set;

public class UserProfileDto {

    private Long id;
    private String email;
    private String fullName;
    private String phone;
    private Set<String> roles;
    private Long activeStoreId;
    private String activeStoreName;

    public UserProfileDto() {
    }

    public UserProfileDto(Long id, String email, String fullName, String phone, Set<String> roles, Long activeStoreId, String activeStoreName) {
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.phone = phone;
        this.roles = roles;
        this.activeStoreId = activeStoreId;
        this.activeStoreName = activeStoreName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public Long getActiveStoreId() {
        return activeStoreId;
    }

    public void setActiveStoreId(Long activeStoreId) {
        this.activeStoreId = activeStoreId;
    }

    public String getActiveStoreName() {
        return activeStoreName;
    }

    public void setActiveStoreName(String activeStoreName) {
        this.activeStoreName = activeStoreName;
    }
}
