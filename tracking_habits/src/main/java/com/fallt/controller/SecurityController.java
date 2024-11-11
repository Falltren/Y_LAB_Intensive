package com.fallt.controller;

import com.fallt.domain.dto.request.LoginRequest;
import com.fallt.domain.dto.request.UpsertUserRequest;
import com.fallt.domain.dto.response.ExceptionResponse;
import com.fallt.domain.dto.response.LoginResponse;
import com.fallt.domain.dto.response.UserResponse;
import com.fallt.service.AuthService;
import com.fallt.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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

    @Operation(summary = "Регистрация аккаунта")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", content = {@Content(schema = @Schema(implementation = UserResponse.class))}),
            @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))}),
            @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))})})
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody @Valid UpsertUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.saveUser(request));
    }

    @Operation(summary = "Аутентификация пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = LoginResponse.class))}),
            @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))}),
            @ApiResponse(responseCode = "401", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))})})
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

}
