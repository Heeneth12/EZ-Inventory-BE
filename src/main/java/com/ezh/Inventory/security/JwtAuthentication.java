package com.ezh.Inventory.security;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
public class JwtAuthentication extends AbstractAuthenticationToken {

    private final Long userId;
    private final String email;
    private final Long tenantId;

    public JwtAuthentication(Long userId, String email, Long tenantId) {
        super((Collection<? extends GrantedAuthority>) null);
        this.userId = userId;
        this.email = email;
        this.tenantId = tenantId;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null; // token already validated
    }

    @Override
    public Object getPrincipal() {
        return userId;
    }
}