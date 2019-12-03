package com.entities;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority{
    client,
    waiter,
    cooker,
    chef;

    @Override
    public String getAuthority() {
        return null;
    }
}
