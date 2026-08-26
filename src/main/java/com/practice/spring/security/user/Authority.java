package com.practice.spring.security.user;

import org.springframework.security.core.GrantedAuthority;

public enum Authority implements GrantedAuthority {

    READ, WRITE, ADMIN;

    @Override
    public String getAuthority() {
        return "notes." + name();
    }
}
