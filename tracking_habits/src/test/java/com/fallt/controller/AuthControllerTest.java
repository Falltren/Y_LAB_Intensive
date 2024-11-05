package com.fallt.controller;

import com.fallt.dto.request.LoginRequest;
import com.fallt.dto.response.UserResponse;
import com.fallt.exception.AuthenticationException;
import com.fallt.exception.EntityNotFoundException;
import com.fallt.exception.ExceptionHandlingController;
import com.fallt.exception.ValidationException;
import com.fallt.security.AuthenticationContext;
import com.fallt.service.AuthService;
import com.fallt.service.impl.ValidationService;
import com.fallt.util.SessionUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private AuthService authService;

    @Mock
    private SessionUtils sessionUtils;

    @Mock
    private ValidationService validationService;

    @Mock
    private AuthenticationContext authenticationContext;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(ExceptionHandlingController.class)
                .build();
    }

    @Test
    @DisplayName("Успешный вход в систему")
    void whenLogin_thenReturnOk() throws Exception {
        String sessionId = "sessionId";
        LoginRequest request = createRequest();
        String content = objectMapper.writeValueAsString(request);
        UserResponse response = createResponse();
        when(sessionUtils.getSessionIdFromContext()).thenReturn(sessionId);
        when(authService.login(request, sessionId, authenticationContext)).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(response)));
    }

    @Test
    @DisplayName("Попытка входа в систему заблокированного пользователя")
    void whenBlockedUserLogin_thenReturnUnauthorized() throws Exception {
        String sessionId = "sessionId";
        LoginRequest request = createRequest();
        String content = objectMapper.writeValueAsString(request);
        when(sessionUtils.getSessionIdFromContext()).thenReturn(sessionId);
        when(authService.login(request, sessionId, authenticationContext)).thenThrow(AuthenticationException.class);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("UNAUTHORIZED"));
    }

    @Test
    @DisplayName("Попытка входа в систему с отсутствующей в бд электронной почтой")
    void whenLoginWithIncorrectEmail_thenReturnBadRequest() throws Exception {
        String sessionId = "sessionId";
        LoginRequest request = createRequest();
        String content = objectMapper.writeValueAsString(request);
        when(sessionUtils.getSessionIdFromContext()).thenReturn(sessionId);
        when(authService.login(request, sessionId, authenticationContext)).thenThrow(EntityNotFoundException.class);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT FOUND"));
    }

    @Test
    @DisplayName("Попытка входа в систему с невалидными данными")
    void whenLoginWithMissedPassword_thenReturnBadRequest() throws Exception {
        LoginRequest request = createRequest();
        String content = objectMapper.writeValueAsString(request);
        when(validationService.checkLoginRequest(request)).thenThrow(ValidationException.class);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("BAD REQUEST"));
    }


    private LoginRequest createRequest() {
        return LoginRequest.builder()
                .email("email")
                .password("pwd")
                .build();
    }

    private UserResponse createResponse() {
        return UserResponse.builder()
                .name("user")
                .email("email")
                .build();
    }
}
