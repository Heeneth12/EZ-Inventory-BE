package com.ezh.Inventory.utils;

import com.ezh.Inventory.security.JwtAuthentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserContextUtil {

    private UserContextUtil() {
        // private constructor to prevent object creation
    }

    private static JwtAuthentication getAuth() {
        Object authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthentication auth) {
            return auth;
        }
        return null;
    }

    public static Long getUserId() {
        JwtAuthentication auth = getAuth();
        return auth != null ? auth.getUserId() : null;
    }

    public static Long getTenantId() {
        JwtAuthentication auth = getAuth();
        return auth != null ? auth.getTenantId() : null;
    }

    public static String getEmail() {
        JwtAuthentication auth = getAuth();
        return auth != null ? auth.getEmail() : null;
    }
}
