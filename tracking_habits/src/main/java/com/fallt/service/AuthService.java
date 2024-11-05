package com.fallt.service;

import com.fallt.dto.request.LoginRequest;
import com.fallt.dto.response.UserResponse;
import com.fallt.security.AuthenticationContext;

/**
 * Аутентификация пользователя в системе
 */
public interface AuthService {

    /**
     * Добавляет пользователя в контекст аутентификации
     *
     * @param request               Объект, содержащий электронную почту и пароль пользователя
     * @param authenticationContext Контекст аутентификации, хранящий данные о пользователе, который вошел в систему
     * @return Возвращает объект ответ, содержащий дынные, вошедшего в систему пользователя. Если пользователь не будет
     * найден в системе, будет выброшено EntityNotFoundException, если пользователь был заблокирован,
     * то будет выброшено AuthenticationException
     */
    UserResponse login(LoginRequest request, AuthenticationContext authenticationContext);
}
