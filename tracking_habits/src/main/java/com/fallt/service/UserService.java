package com.fallt.service;

import com.fallt.dto.UserDto;
import com.fallt.entity.Role;
import com.fallt.entity.User;
import com.fallt.out.ConsoleOutput;
import com.fallt.util.Message;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.*;

@RequiredArgsConstructor
public class UserService {

    private final ConsoleOutput consoleOutput;

    private final Map<String, User> users = new HashMap<>();

    private final Set<String> passwords = new HashSet<>();

    public Collection<User> getAllUsers = users.values();

    public User getUserByEmail(String email) {
        Optional<User> user = getByEmail(email);
        if (user.isEmpty()) {
            consoleOutput.printMessage(Message.INCORRECT_EMAIL);
            return null;
        }
        return user.get();
    }

    public void createUser(UserDto userDto) {
        User user = User.builder()
                .name(userDto.getName())
                .password(userDto.getPassword())
                .email(userDto.getEmail())
                .createAt(Instant.now())
                .role(Role.USER)
                .isBlocked(false)
                .build();
        users.put(user.getEmail(), user);
        passwords.add(user.getPassword());
    }

    public void updateUser(String userName, UserDto updateUser) {
        User user = users.get(userName);
        if (updateUser.getName() != null) {
            users.remove(user.getName());
            user.setName(updateUser.getName());
        }
        if (updateUser.getPassword() != null) {
            passwords.remove(user.getPassword());
            user.setPassword(updateUser.getPassword());
            passwords.add(updateUser.getPassword());
        }
        if (updateUser.getEmail() != null) {
            user.setEmail(updateUser.getEmail());
        }
        user.setUpdateAt(Instant.now());
        users.put(user.getName(), user);
    }

    public Optional<User> getByEmail(String email) {
        return Optional.ofNullable(users.get(email));
    }

    public void deleteUser(String userName) {
        passwords.remove(getPasswordByName(userName));
        users.remove(userName);
    }

    private String getPasswordByName(String userName) {
        return users.get(userName).getPassword();
    }

    public boolean isExistsEmail(String email) {
        return users.containsKey(email);
    }

    public boolean isExistsPassword(String password) {
        return passwords.contains(password);
    }
}
