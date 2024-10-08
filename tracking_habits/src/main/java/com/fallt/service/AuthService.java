package com.fallt.service;

import com.fallt.entity.User;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
public class AuthService {

    private final Set<User> authenticatedUsers = new HashSet<>();

    private final UserService userService;

    public User login(String email, String password) {
        Optional<User> optionalUser = userService.getByEmail(email);
        if (optionalUser.isEmpty() || !optionalUser.get().getPassword().equals(password)) {
            System.out.println("Введены некорректные данные, повторите ввод!");
            return null;
        }
        User user = optionalUser.get();
        authenticatedUsers.add(user);
        return user;
    }

    public void logout(User user) {
        authenticatedUsers.remove(user);
    }

    public boolean isAuthenticated(User user) {
        return authenticatedUsers.contains(user);
    }
}
