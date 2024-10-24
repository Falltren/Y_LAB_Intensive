package com.fallt.service;

import com.fallt.dto.request.UpsertUserRequest;
import com.fallt.dto.response.UserResponse;
import com.fallt.out.ConsoleOutput;
import lombok.RequiredArgsConstructor;

/**
 * Регистрация пользователя
 */
@RequiredArgsConstructor
public class RegistrationService {

    private final UserService userService;

    /**
     * Регистрация пользователя
     *
     * @param name     Имя пользователя
     * @param password Пароль пользователя
     * @param email    Электронный адрес пользователя
     * @return true, если пользователь успешно зарегистрирован и false в обратном случае
     */
    public UserResponse register(UpsertUserRequest request) {
        return userService.createUser(request);
    }
}
