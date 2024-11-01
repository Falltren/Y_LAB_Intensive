package com.fallt.controller;

import com.fallt.dto.request.LoginRequest;
import com.fallt.dto.response.UserResponse;
import com.fallt.exception.AuthenticationException;
import com.fallt.exception.EntityNotFoundException;
import com.fallt.exception.ValidationException;
import com.fallt.security.AuthenticationContext;
import com.fallt.service.AuthService;
import com.fallt.service.ValidationService;
import com.fallt.util.SessionUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Контроллер аутентификации")
public class AuthController {

    private final AuthService authService;

    private final AuthenticationContext authenticationContext;

    private final ValidationService validationService;

    private final SessionUtils sessionUtils;

    @Operation(
            summary = "Аутентификация пользователя",
            description = "Добавляет пользователя в контекст аутентификации"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный вход в систему", content = {
                    @Content(schema = @Schema(implementation = UserResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "400", description = "Указание невалидных данных", content = {
                    @Content(schema = @Schema(implementation = ValidationException.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "401", description = "Вход заблокированного пользователя", content = {
                    @Content(schema = @Schema(implementation = AuthenticationException.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "404", description = "Пользователь отсутствует в системе", content = {
                    @Content(schema = @Schema(implementation = EntityNotFoundException.class), mediaType = "application/json")
            })
    })
    @PostMapping("/login")
    public UserResponse login(@RequestBody LoginRequest request) {
        validationService.checkLoginRequest(request);
        String sessionId = sessionUtils.getSessionIdFromContext();
        return authService.login(request, sessionId, authenticationContext);
    }
}
