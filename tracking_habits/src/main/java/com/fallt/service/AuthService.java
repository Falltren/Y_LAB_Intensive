package com.fallt.service;

import com.fallt.entity.User;
import com.fallt.out.ConsoleOutput;
import com.fallt.util.Message;
import lombok.RequiredArgsConstructor;

/**
 * Аутентификация пользователя
 */
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;

    private final ConsoleOutput consoleOutput;

    /**
     * Проверка наличия пользователя в базе данных
     *
     * @param email    Электронный адрес пользователя
     * @param password Пароль пользователя
     * @return Возвращает объект класса User в случае успешной аутентификации или null если аутентификация завершилась неудачно
     */
    public User login(String email, String password) {
        User user = userService.getUserByEmail(email);
        if (user == null || !user.getPassword().equals(password)) {
            consoleOutput.printMessage(Message.UNAUTHENTICATED_USER);
            return null;
        }
        if (user.isBlocked()) {
            consoleOutput.printMessage(Message.BLOCKED_USER);
            return null;
        }
        return user;
    }
}
