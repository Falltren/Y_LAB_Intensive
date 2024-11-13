package com.fallt.service.impl;

import com.fallt.aop.audit.ActionType;
import com.fallt.aop.audit.Auditable;
import com.fallt.aop.logging.Loggable;
import com.fallt.domain.dto.request.LoginRequest;
import com.fallt.domain.dto.response.UserResponse;
import com.fallt.domain.entity.User;
import com.fallt.exception.AuthenticationException;
import com.fallt.exception.EntityNotFoundException;
import com.fallt.mapper.UserMapper;
import com.fallt.security.AuthenticationContext;
import com.fallt.security.PasswordEncoder;
import com.fallt.security.UserDetails;
import com.fallt.service.AuthService;
import com.fallt.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Loggable
@Service
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Auditable(action = ActionType.LOGIN)
    public UserResponse login(LoginRequest request, String sessionId, AuthenticationContext authenticationContext) {
        User user = userService.getUserByEmail(request.getEmail());
        if (!passwordEncoder.checkPassword(request.getPassword(), user.getPassword())) {
            throw new EntityNotFoundException("Введены некорректные данные");
        }
        if (user.isBlocked()) {
            throw new AuthenticationException("Ваша учетная запись заблокирована");
        }
        authenticationContext.authenticate(sessionId, UserDetails.createUserDetails(user));
        return UserMapper.INSTANCE.toResponse(user);
    }
}
