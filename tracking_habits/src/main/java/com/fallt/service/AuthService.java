package com.fallt.service;

import com.fallt.aop.audit.ActionType;
import com.fallt.aop.audit.Auditable;
import com.fallt.aop.logging.Loggable;
import com.fallt.dto.request.LoginRequest;
import com.fallt.dto.response.UserResponse;
import com.fallt.entity.User;
import com.fallt.exception.AuthenticationException;
import com.fallt.exception.EntityNotFoundException;
import com.fallt.mapper.UserMapper;
import com.fallt.security.AuthenticationContext;
import com.fallt.security.UserDetails;
import lombok.RequiredArgsConstructor;

/**
 * Аутентификация пользователя
 */
@RequiredArgsConstructor
@Loggable
public class AuthService {

    private final UserService userService;

    /**
     * Проверка наличия пользователя в базе данных
     *
     * @param request               Объект, содержащий электронную почту и пароль пользователя
     * @param authenticationContext Контекст аутентификации, хранящий данные о пользователе, который вошел в систему
     * @return Возвращает объект ответ, содержащий дынные, вошедшего в систему пользователя. Если пользователь не будет
     * найден в системе, будет выброшено EntityNotFoundException, если пользователь был заблокирован,
     * то будет выброшено AuthenticationException
     * или выбрасывается исключение AuthenticationException, если аутентификация завершилась неудачно
     */
    @Auditable(action = ActionType.LOGIN)
    public UserResponse login(LoginRequest request, AuthenticationContext authenticationContext) {
        User user = userService.getUserByEmail(request.getEmail());
        if (user == null || !user.getPassword().equals(request.getPassword())) {
            throw new EntityNotFoundException("Проверьте электронную почту и пароль");
        }
        if (user.isBlocked()) {
            throw new AuthenticationException("Ваша учетная запись заблокирована");
        }
        authenticationContext.authenticate(UserDetails.createUserDetails(user));
        return UserMapper.INSTANCE.toResponse(user);
    }
}
