package com.practice.spring.dto.security;

import java.util.Collection;

public record TokenSubject(Collection<String> authorities, String username) {
}
