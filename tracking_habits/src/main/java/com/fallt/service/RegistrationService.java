package com.fallt.service;

import com.fallt.dto.UserDto;
import com.fallt.out.ConsoleOutput;
import com.fallt.util.Message;
import lombok.RequiredArgsConstructor;

/**
 * Регистрация пользователя
 */
@RequiredArgsConstructor
public class RegistrationService {

    private final UserService userService;

    private final ConsoleOutput consoleOutput;

    /**
     * Регистрация пользователя
     *
     * @param name     Имя пользователя
     * @param password Пароль пользователя
     * @param email    Электронный адрес пользователя
     * @return true, если пользователь успешно зарегистрирован и false в обратном случае
     */
    public boolean register(String name, String password, String email) {
        if (name.isBlank() || password.isBlank() || email.isBlank()) {
            consoleOutput.printMessage(Message.INCORRECT_INPUT);
            return false;
        }
        UserDto userDto = new UserDto(name, password, email);
        return userService.createUser(userDto) != null;
    }
}
