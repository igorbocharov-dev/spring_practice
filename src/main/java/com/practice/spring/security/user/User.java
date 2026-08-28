package com.practice.spring.security.user;

import java.util.Collection;

public record User(Long id, String username, String password, Collection<String> authorities) {
}
