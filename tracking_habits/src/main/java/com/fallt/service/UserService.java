package com.fallt.service;

import com.fallt.dto.UserDto;
import com.fallt.entity.Role;
import com.fallt.entity.User;
import com.fallt.out.ConsoleOutput;
import com.fallt.repository.UserDao;
import com.fallt.util.Message;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class UserService {

    private final UserDao userDao;

    private final ConsoleOutput consoleOutput;

    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    public User getUserByEmail(String email) {
        Optional<User> user = userDao.getUserByEmail(email);
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
                .createAt(LocalDateTime.now())
                .role(Role.ROLE_USER)
                .isBlocked(false)
                .build();
        return userDao.create(user);
    }

    public void blockingUser(User user) {
        user.setBlocked(true);
        user.setUpdateAt(LocalDateTime.now());
        userDao.update(user);
    }

    public void updateUser(String email, UserDto updateUser) {
        User user = getUserByEmail(email);
        if (updateUser.getEmail() != null && !updateUser.getEmail().isBlank()) {
            if (isExistsEmail(updateUser.getEmail())) {
                consoleOutput.printMessage(Message.EMAIL_EXIST);
                return;
            }
            user.setEmail(updateUser.getEmail());
        }
        if (updateUser.getPassword() != null && !updateUser.getPassword().isBlank()) {
            if (isExistsPassword(updateUser.getPassword())) {
                consoleOutput.printMessage(Message.PASSWORD_EXIST);
                return;
            }
            user.setPassword(updateUser.getPassword());
        }
        if (updateUser.getName() != null && !updateUser.getName().isBlank()) {
            user.setName(updateUser.getName());
        }
        user.setUpdateAt(LocalDateTime.now());
        userDao.update(user);
    }

    public void deleteUser(User user) {
        userDao.delete(user);
    }

    public boolean isExistsEmail(String email) {
        return userDao.getUserByEmail(email).isPresent();
    }

    public boolean isExistsPassword(String password) {
        return userDao.getUserByPassword(password).isPresent();
    }
}
