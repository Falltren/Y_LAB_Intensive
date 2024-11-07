package com.fallt.controller;

import com.fallt.domain.dto.request.LoginRequest;
import com.fallt.domain.dto.request.UpsertUserRequest;
import com.fallt.domain.dto.response.UserResponse;
import com.fallt.domain.dto.response.ExceptionResponse;
import com.fallt.security.AuthenticationContext;
import com.fallt.service.AuthService;
import com.fallt.service.UserService;
import com.fallt.service.impl.ValidationService;
import com.fallt.util.SessionUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
@Tag(name = "Контроллер аутентификации")
public class SecurityController {

    private final AuthService authService;
    private final UserService userService;
    private final AuthenticationContext authenticationContext;
    private final ValidationService validationService;
    private final SessionUtils sessionUtils;

    @Operation(
            summary = "Создание аккаунта",
            description = "Добавляет нового пользователя в систему"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Успешная регистрация пользователя", content = {
                    @Content(schema = @Schema(implementation = UserResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "400", description = "Указание невалидных данных", content = {
                    @Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "400", description = "Email/пароль уже используется другим пользователем", content = {
                    @Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = "application/json")
            })
    })
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody UpsertUserRequest request) {
        validationService.checkUpsertUserRequest(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.saveUser(request));
    }

    @Operation(
            summary = "Аутентификация пользователя",
            description = "Добавляет пользователя в контекст аутентификации"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный вход в систему", content = {
                    @Content(schema = @Schema(implementation = UserResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "400", description = "Указание невалидных данных", content = {
                    @Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "401", description = "Вход заблокированного пользователя", content = {
                    @Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "404", description = "Пользователь отсутствует в системе", content = {
                    @Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = "application/json")
            })
    })
    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@RequestBody LoginRequest request) {
        validationService.checkLoginRequest(request);
        String sessionId = sessionUtils.getSessionIdFromContext();
        return ResponseEntity.ok(authService.login(request, sessionId, authenticationContext));
    }
}
