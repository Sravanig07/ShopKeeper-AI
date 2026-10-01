package com.shelfiq.common.context;

import java.util.Collections;
import java.util.Set;

public class TenantContext {

    private final Long userId;
    private final String email;
    private final Long storeId;
    private final Set<String> roles;

    public TenantContext(Long userId, String email, Long storeId, Set<String> roles) {
        this.userId = userId;
        this.email = email;
        this.storeId = storeId;
        this.roles = roles != null ? Collections.unmodifiableSet(roles) : Collections.emptySet();
    }

    public Long getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public Long getStoreId() {
        return storeId;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public boolean hasRole(String role) {
        return roles.contains(role);
    }

    public boolean isStoreOwner() {
        return hasRole("ROLE_STORE_OWNER");
    }

    public boolean isAdmin() {
        return hasRole("ROLE_ADMIN");
    }
}
