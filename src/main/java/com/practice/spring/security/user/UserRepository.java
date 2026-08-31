package com.practice.spring.security.user;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findByUsername(String username);
}
