package com.practice.spring.security.user;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class UserManager {

    private final List<User> users = new ArrayList<>();

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserManager(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void initUsers(){
        User user1 = new User(1L, passwordEncoder.encode("password123"), "Ivan", List.of(Authority.READ));
        User user2 = new User(2L, passwordEncoder.encode("password123"), "Maxim", List.of(Authority.WRITE));
        User user3 = new User(3L, passwordEncoder.encode("password123"), "Alexander", List.of(Authority.ADMIN));
        users.addAll(List.of(user1, user2, user3));
    }

    public List<User> getUsers() {
        return users;
    }
}
