package com.fallt.service;

import com.fallt.aop.Loggable;
import com.fallt.dto.request.UpsertUserRequest;
import com.fallt.dto.response.UserResponse;
import lombok.RequiredArgsConstructor;

/**
 * Регистрация пользователя
 */
@RequiredArgsConstructor
@Loggable
public class RegistrationService {

    private final UserService userService;

    /**
     * Регистрация пользователя
     *
     * @param request Объект, содержащий данные об имени, пароле, электронной почте пользователя
     * @return true, если пользователь успешно зарегистрирован и false в обратном случае
     */
    public UserResponse register(UpsertUserRequest request) {
        return userService.createUser(request);
    }
}
