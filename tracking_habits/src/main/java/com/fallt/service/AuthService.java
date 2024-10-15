package com.fallt.service;

import com.fallt.entity.User;
import com.fallt.out.ConsoleOutput;
import com.fallt.util.Message;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
public class AuthService {

    private final Set<User> authenticatedUsers = new HashSet<>();

    private final UserService userService;

    private final ConsoleOutput consoleOutput;

    public User login(String email, String password) {
        User user = userService.getUserByEmail(email);
        if (user == null || !user.getPassword().equals(password)) {
            consoleOutput.printMessage(Message.UNAUTHENTICATED_USER);
            return null;
        }
        if (user.isBlocked()){
            consoleOutput.printMessage(Message.BLOCKED_USER);
            return null;
        }
        authenticatedUsers.add(user);
        return user;
    }
}
