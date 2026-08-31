package com.practice.spring.security.user;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class UserManager {

    private final List<User> users = new ArrayList<>();

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserManager(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void initUsers(){
        User user1 = new User(1L, "Ivan", passwordEncoder.encode("password123"), List.of(Authority.READ.getAuthority()));
        User user2 = new User(2L, "Maxim", passwordEncoder.encode("password123"), List.of(Authority.WRITE.getAuthority()));
        User user3 = new User(3L, "Alexander",passwordEncoder.encode("password123"), List.of(Authority.ADMIN.getAuthority()));
        users.addAll(List.of(user1, user2, user3));
        log.info("Users init successful: {}", users);
    }

    public List<User> getUsers() {
        return users;
    }
}
