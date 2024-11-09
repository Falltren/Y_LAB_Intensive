package com.fallt.service;

import com.fallt.domain.dto.request.LoginRequest;
import com.fallt.domain.dto.response.LoginResponse;

/**
 * Аутентификация пользователя
 */
public interface AuthService {

    /**
     * Аутентификация пользователя в системе
     *
     * @param request Объект, содержащий данные необходимые для идентификации пользователя
     * @return После проверки предоставленных данных возвращается ответ, содержащий токен доступа.
     * Если аутентификация завершилась неудачно, будет выброшено исключение AuthenticationException
     */
    LoginResponse login(LoginRequest request);

}
