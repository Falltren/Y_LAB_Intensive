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

    public User createUser(UserDto userDto) {
        if (isExistsEmail(userDto.getEmail())) {
            consoleOutput.printMessage(Message.EMAIL_EXIST);
            return null;
        }
        if (isExistsPassword(userDto.getPassword())) {
            consoleOutput.printMessage(Message.PASSWORD_EXIST);
            return null;
        }
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
        return user;
    }

    public void updateUser(String email, UserDto updateUser) {
        User user = users.get(email);
        String oldEmail = user.getEmail();
        if (updateUser.getEmail() != null && !updateUser.getEmail().isBlank()) {
            if (isExistsEmail(updateUser.getEmail())) {
                consoleOutput.printMessage(Message.EMAIL_EXIST);
                return;
            }
            user.setEmail(updateUser.getEmail());
        }
        if (updateUser.getPassword() != null && !updateUser.getPassword().isBlank()) {
            if (isExistsPassword(updateUser.getPassword())){
                consoleOutput.printMessage(Message.PASSWORD_EXIST);
                return;
            }
            passwords.remove(user.getPassword());
            user.setPassword(updateUser.getPassword());
            passwords.add(updateUser.getPassword());
        }
        if (updateUser.getName() != null && !updateUser.getName().isBlank()) {
            users.remove(user.getName());
            user.setName(updateUser.getName());
        }
        user.setUpdateAt(Instant.now());
        users.remove(oldEmail);
        users.put(user.getEmail(), user);
    }

    public Optional<User> getByEmail(String email) {
        return Optional.ofNullable(users.get(email));
    }

    public void deleteUser(String email) {
        passwords.remove(getPasswordByName(email));
        users.remove(email);
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
