package com.practice.spring.dto.security;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public record TokenSubject(Collection<? extends GrantedAuthority> authorities, String username) {
}
