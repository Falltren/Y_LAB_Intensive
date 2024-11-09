package com.fallt.service.impl;

import com.fallt.audit_starter.aop.Auditable;
import com.fallt.audit_starter.domain.entity.enums.ActionType;
import com.fallt.domain.dto.request.LoginRequest;
import com.fallt.domain.dto.response.LoginResponse;
import com.fallt.domain.entity.User;
import com.fallt.exception.AuthenticationException;
import com.fallt.exception.EntityNotFoundException;
import com.fallt.logging.annotation.Loggable;
import com.fallt.security.JwtUtil;
import com.fallt.security.PasswordEncoder;
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
    private final JwtUtil jwtUtil;

    @Auditable(action = ActionType.LOGIN)
    public LoginResponse login(LoginRequest request) {
        User user = userService.getUserByEmail(request.getEmail());
        if (!passwordEncoder.checkPassword(request.getPassword(), user.getPassword())) {
            throw new EntityNotFoundException("Введены некорректные данные");
        }
        if (user.isBlocked()) {
            throw new AuthenticationException("Ваша учетная запись заблокирована");
        }
        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole().name());
        return LoginResponse.builder()
                .name(user.getName())
                .token(token)
                .build();
    }

}
