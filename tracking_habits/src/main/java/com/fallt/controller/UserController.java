package com.fallt.controller;

import com.fallt.dto.request.UpsertUserRequest;
import com.fallt.dto.response.UserResponse;
import com.fallt.entity.Role;
import com.fallt.exception.ExceptionResponse;
import com.fallt.security.AuthenticationContext;
import com.fallt.service.UserService;
import com.fallt.service.impl.ValidationService;
import com.fallt.util.SessionUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Управление пользовательскими аккаунтами")
public class UserController {

    private final UserService userService;
    private final AuthenticationContext authenticationContext;
    private final SessionUtils sessionUtils;
    private final ValidationService validationService;

    @Operation(
            summary = "Получение всех аккаунтов",
            description = "Предоставляет данные по всем пользователям"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Получение списка пользователей", content = {
                    @Content(array = @ArraySchema(schema = @Schema(implementation = UserResponse.class)), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "401", description = "Запрос от неаутентифицированного пользователя", content = {
                    @Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "403", description = "Запрос от пользователя, не имеющего роль ROLE_ADMIN", content = {
                    @Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = "application/json")
            })
    })
    @GetMapping
    public List<UserResponse> getAllUsers() {
        String sessionId = sessionUtils.getSessionIdFromContext();
        authenticationContext.checkRole(sessionId, Role.ROLE_ADMIN);
        return userService.getAllUsers();
    }

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
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(@RequestBody UpsertUserRequest request) {
        validationService.checkUpsertUserRequest(request);
        return userService.saveUser(request);
    }

    @Operation(
            summary = "Обновление данных о пользователе",
            description = "Обновление данных о пользователе"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Обновление аккаунта", content = {
                    @Content(schema = @Schema(implementation = UserResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "400", description = "Указание невалидных данных", content = {
                    @Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "400", description = "Email/пароль уже используется другим пользователем", content = {
                    @Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "401", description = "Запрос от неаутентифицированного пользователя", content = {
                    @Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = "application/json")
            })
    })
    @PutMapping
    public UserResponse updateUser(@RequestBody UpsertUserRequest request) {
        String email = authenticationContext.getEmailCurrentUser(sessionUtils.getSessionIdFromContext());
        return userService.updateUser(email, request);
    }

    @Operation(
            summary = "Блокировка пользователя",
            description = "Запрещает пользователю вход в систему"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Блокировка пользователя"),
            @ApiResponse(responseCode = "401", description = "Запрос от неаутентифицированного пользователя", content = {
                    @Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "403", description = "Запрос от пользователя, не имеющего роль ROLE_ADMIN", content = {
                    @Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = "application/json")
            })
    })
    @PutMapping("/block")
    public void blockUser(@RequestParam("email") String email) {
        String sessionId = sessionUtils.getSessionIdFromContext();
        authenticationContext.checkRole(sessionId, Role.ROLE_ADMIN);
        userService.blockingUser(email);
    }

    @Operation(
            summary = "Удаление пользователя",
            description = "Удаление пользователя"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Удаление аккаунта"),
            @ApiResponse(responseCode = "401", description = "Запрос от неаутентифицированного пользователя", content = {
                    @Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = "application/json")
            })
    })
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser() {
        String email = authenticationContext.getEmailCurrentUser(sessionUtils.getSessionIdFromContext());
        userService.deleteUser(email);
    }
}
