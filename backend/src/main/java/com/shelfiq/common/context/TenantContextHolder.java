package com.shelfiq.common.context;

import com.shelfiq.common.exception.TenantAccessDeniedException;

public final class TenantContextHolder {

    private static final ThreadLocal<TenantContext> CONTEXT = new ThreadLocal<>();

    private TenantContextHolder() {
    }

    public static void setContext(TenantContext context) {
        CONTEXT.set(context);
    }

    public static TenantContext getContext() {
        return CONTEXT.get();
    }

    public static Long getRequiredStoreId() {
        TenantContext ctx = CONTEXT.get();
        if (ctx == null || ctx.getStoreId() == null) {
            throw new TenantAccessDeniedException("Active store context is required for this operation.");
        }
        return ctx.getStoreId();
    }

    public static Long getRequiredUserId() {
        TenantContext ctx = CONTEXT.get();
        if (ctx == null || ctx.getUserId() == null) {
            throw new TenantAccessDeniedException("Authenticated user context is required.");
        }
        return ctx.getUserId();
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
