package com.fallt.controller;

import com.fallt.domain.dto.request.UpsertUserRequest;
import com.fallt.domain.dto.response.ExceptionResponse;
import com.fallt.domain.dto.response.UserResponse;
import com.fallt.domain.entity.enums.Role;
import com.fallt.security.AuthenticationContext;
import com.fallt.service.UserService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Управление пользовательскими аккаунтами")
public class UserController {

    private final UserService userService;
    private final AuthenticationContext authenticationContext;
    private final SessionUtils sessionUtils;

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
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        String sessionId = sessionUtils.getSessionIdFromContext();
        authenticationContext.checkRole(sessionId, Role.ROLE_ADMIN);
        return ResponseEntity.ok(userService.getAllUsers());
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
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable("id") Long id, @RequestBody UpsertUserRequest request) {
        String email = authenticationContext.getEmailCurrentUser(sessionUtils.getSessionIdFromContext());
        return ResponseEntity.ok(userService.updateUser(id, request));
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
    @PutMapping("/block/{id}")
    public ResponseEntity<Void> blockUser(@PathVariable("id") Long id) {
        String sessionId = sessionUtils.getSessionIdFromContext();
        authenticationContext.checkRole(sessionId, Role.ROLE_ADMIN);
        userService.blockingUser(id);
        return ResponseEntity.status(HttpStatus.OK).build();
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
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id) {
        String email = authenticationContext.getEmailCurrentUser(sessionUtils.getSessionIdFromContext());
        userService.deleteUser(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
