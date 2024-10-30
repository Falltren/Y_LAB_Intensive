package com.fallt.controller;

import com.fallt.dto.request.LoginRequest;
import com.fallt.dto.response.UserResponse;
import com.fallt.security.AuthenticationContext;
import com.fallt.service.AuthService;
import com.fallt.service.ValidationService;
import com.fallt.util.SessionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    private final AuthenticationContext authenticationContext;

    private final ValidationService validationService;

    private final SessionUtils sessionUtils;

    @PostMapping("/login")
    public UserResponse login(@RequestBody LoginRequest request) {
        validationService.checkLoginRequest(request);
        String sessionId = sessionUtils.getSessionIdFromContext();
        return authService.login(request, sessionId, authenticationContext);
    }
}
