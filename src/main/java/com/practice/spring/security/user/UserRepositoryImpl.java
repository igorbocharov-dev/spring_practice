package com.practice.spring.security.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class UserRepositoryImpl implements UserRepository{

    private final UserManager userManager;

    @Autowired
    public UserRepositoryImpl(UserManager userManager) {
        this.userManager = userManager;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        List<User> users = userManager.getUsers();
        return users.stream().filter(user -> user.username().equals(username)).findFirst();
    }
}
