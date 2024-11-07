package com.fallt.controller;

import com.fallt.exception.AlreadyExistException;
import com.fallt.exception.AuthenticationException;
import com.fallt.exception.EntityNotFoundException;
import com.fallt.exception.ExceptionHandlingController;
import com.fallt.exception.ValidationException;
import com.fallt.security.AuthenticationContext;
import com.fallt.service.AuthService;
import com.fallt.service.UserService;
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

import static com.fallt.TestConstant.LOGIN_PATH;
import static com.fallt.TestConstant.LOGIN_REQUEST;
import static com.fallt.TestConstant.REGISTER_PATH;
import static com.fallt.TestConstant.SESSION_ID;
import static com.fallt.TestConstant.USER_REQUEST;
import static com.fallt.TestConstant.USER_RESPONSE;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class SecurityControllerTest {

    @InjectMocks
    private SecurityController securityController;

    @Mock
    private AuthService authService;

    @Mock
    private UserService userService;

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
        mockMvc = MockMvcBuilders.standaloneSetup(securityController)
                .setControllerAdvice(ExceptionHandlingController.class)
                .build();
    }

    @Test
    @DisplayName("Регистрация нового пользователя")
    void whenCreateNewUser_thenReturnOk() throws Exception {
        String content = objectMapper.writeValueAsString(USER_REQUEST);

        when(userService.saveUser(USER_REQUEST)).thenReturn(USER_RESPONSE);

        mockMvc.perform(post(REGISTER_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string(objectMapper.writeValueAsString(USER_RESPONSE)));
    }

    @Test
    @DisplayName("Попытка регистрации нового пользователя с невалидными данными")
    void whenCreateNewUserWithIncorrectData_thenReturnBadRequest() throws Exception {
        String content = objectMapper.writeValueAsString(USER_REQUEST);

        when(validationService.checkUpsertUserRequest(USER_REQUEST)).thenThrow(ValidationException.class);

        mockMvc.perform(post(REGISTER_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Попытка регистрации нового пользователя с уже используемой электронной почтой")
    void whenCreateNewUserWithExistsEmail_thenReturnBadRequest() throws Exception {
        String content = objectMapper.writeValueAsString(USER_REQUEST);

        when(userService.saveUser(USER_REQUEST)).thenThrow(AlreadyExistException.class);

        mockMvc.perform(post(REGISTER_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Успешный вход в систему")
    void whenLogin_thenReturnOk() throws Exception {
        String content = objectMapper.writeValueAsString(LOGIN_REQUEST);

        when(sessionUtils.getSessionIdFromContext()).thenReturn(SESSION_ID);
        when(authService.login(LOGIN_REQUEST, SESSION_ID, authenticationContext)).thenReturn(USER_RESPONSE);

        mockMvc.perform(post(LOGIN_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(USER_RESPONSE)));
    }

    @Test
    @DisplayName("Попытка входа в систему заблокированного пользователя")
    void whenBlockedUserLogin_thenReturnUnauthorized() throws Exception {
        String content = objectMapper.writeValueAsString(LOGIN_REQUEST);

        when(sessionUtils.getSessionIdFromContext()).thenReturn(SESSION_ID);
        when(authService.login(LOGIN_REQUEST, SESSION_ID, authenticationContext)).thenThrow(AuthenticationException.class);

        mockMvc.perform(post(LOGIN_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Попытка входа в систему с отсутствующей в бд электронной почтой")
    void whenLoginWithIncorrectEmail_thenReturnBadRequest() throws Exception {
        String content = objectMapper.writeValueAsString(LOGIN_REQUEST);

        when(sessionUtils.getSessionIdFromContext()).thenReturn(SESSION_ID);
        when(authService.login(LOGIN_REQUEST, SESSION_ID, authenticationContext)).thenThrow(EntityNotFoundException.class);

        mockMvc.perform(post(LOGIN_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Попытка входа в систему с невалидными данными")
    void whenLoginWithMissedPassword_thenReturnBadRequest() throws Exception {
        String content = objectMapper.writeValueAsString(LOGIN_REQUEST);

        when(validationService.checkLoginRequest(LOGIN_REQUEST)).thenThrow(ValidationException.class);

        mockMvc.perform(post(LOGIN_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}
