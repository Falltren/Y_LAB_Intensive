package com.fallt.service;

import com.fallt.domain.dto.request.LoginRequest;
import com.fallt.domain.dto.response.UserResponse;
import com.fallt.security.AuthenticationContext;

/**
 * Аутентификация пользователя
 */
public interface AuthService {

    /**
     * Проверка наличия пользователя в базе данных
     *
     * @param request               Дто, содержащий электронную почту и пароль пользователя
     * @param authenticationContext Контекст аутентификации, хранящий данные о пользователе, который вошел в приложение
     * @return Возвращает объект класса User в случае успешной аутентификации
     * или выбрасывается исключение AuthenticationException, если аутентификация завершилась неудачно
     */
    UserResponse login(LoginRequest request, String sessionId, AuthenticationContext authenticationContext);
}
