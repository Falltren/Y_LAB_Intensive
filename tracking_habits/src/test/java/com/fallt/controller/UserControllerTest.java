package com.fallt.controller;

import com.fallt.dto.request.UpsertUserRequest;
import com.fallt.dto.response.UserResponse;
import com.fallt.entity.Role;
import com.fallt.exception.AlreadyExistException;
import com.fallt.exception.AuthorizationException;
import com.fallt.exception.ExceptionHandlingController;
import com.fallt.exception.ValidationException;
import com.fallt.security.AuthenticationContext;
import com.fallt.service.UserService;
import com.fallt.service.ValidationService;
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

import java.util.List;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Mock
    private AuthenticationContext authenticationContext;

    @Mock
    private SessionUtils sessionUtils;

    @Mock
    private ValidationService validationService;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String SESSION_ID = "sessionId";

    private static final String USER_EMAIL = "email";

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(ExceptionHandlingController.class)
                .build();
    }

    @Test
    @DisplayName("Регистрация нового пользователя")
    void whenCreateNewUser_thenReturnOk() throws Exception {
        UpsertUserRequest request = createRequest();
        UserResponse response = createResponse();
        when(userService.saveUser(request)).thenReturn(response);
        String content = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string(objectMapper.writeValueAsString(response)));
    }

    @Test
    @DisplayName("Попытка регистрации нового пользователя с невалидными данными")
    void whenCreateNewUserWithIncorrectData_thenReturnBadRequest() throws Exception {
        UpsertUserRequest request = createRequest();
        when(validationService.checkUpsertUserRequest(request)).thenThrow(ValidationException.class);
        String content = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("BAD REQUEST"));
    }

    @Test
    @DisplayName("Попытка регистрации нового пользователя с уже используемой электронной почтой")
    void whenCreateNewUserWithExistsEmail_thenReturnBadRequest() throws Exception {
        UpsertUserRequest request = createRequest();
        when(userService.saveUser(request)).thenThrow(AlreadyExistException.class);
        String content = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("ALREADY EXIST"));
    }

    @Test
    @DisplayName("Обновление данных о пользователе")
    void whenUpdateUser_thenReturnOk() throws Exception {
        UpsertUserRequest request = createRequest();
        UserResponse response = createResponse();
        String content = objectMapper.writeValueAsString(request);
        when(sessionUtils.getSessionIdFromContext()).thenReturn(SESSION_ID);
        when(authenticationContext.getEmailCurrentUser(SESSION_ID)).thenReturn(USER_EMAIL);
        when(userService.updateUser(USER_EMAIL, request)).thenReturn(response);

        mockMvc.perform(put("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(response)));
    }

    @Test
    @DisplayName("Попытка обновления данных о пользователе с указанием используемого email")
    void whenUpdateUserWithExistsEmail_thenReturnBadRequest() throws Exception {
        UpsertUserRequest request = createRequest();
        String content = objectMapper.writeValueAsString(request);
        when(sessionUtils.getSessionIdFromContext()).thenReturn(SESSION_ID);
        when(authenticationContext.getEmailCurrentUser(SESSION_ID)).thenReturn(USER_EMAIL);
        when(userService.updateUser(USER_EMAIL, request)).thenThrow(AlreadyExistException.class);

        mockMvc.perform(put("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("ALREADY EXIST"));
    }

    @Test
    @DisplayName("Получение данных о всех пользователях")
    void whenGetAllUsers_thenReturnListUsers() throws Exception {
        List<UserResponse> responseList = List.of(
                createResponse("email1"), createResponse("email2")
        );
        when(userService.getAllUsers()).thenReturn(responseList);

        mockMvc.perform(get("/api/v1/users"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(responseList)));
    }

    @Test
    @DisplayName("Получение данных о всех пользователях")
    void whenUserWithoutRoleAdminGetAllUsers_thenReturnForbidden() throws Exception {
        when(sessionUtils.getSessionIdFromContext()).thenReturn(SESSION_ID);
        doThrow(AuthorizationException.class).when(authenticationContext).checkRole(SESSION_ID, Role.ROLE_ADMIN);

        mockMvc.perform(get("/api/v1/users"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Блокировка пользователя")
    void whenBlockingUser_thenReturnOk() throws Exception {
        String email = "user";

        mockMvc.perform(put("/api/v1/users/block?email=" + email))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Попытка блокировки пользователя пользователем без роли ADMIN")
    void whenUserWithoutRoleAdminBlockingUser_thenReturnForbidden() throws Exception{
        String email = "user";
        when(sessionUtils.getSessionIdFromContext()).thenReturn(SESSION_ID);
        doThrow(AuthorizationException.class).when(authenticationContext).checkRole(SESSION_ID, Role.ROLE_ADMIN);

        mockMvc.perform(put("/api/v1/users/block?email=" + email))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Удаление пользователя")
    void whenDeleteUser_thenReturnNoContent() throws Exception {
        when(sessionUtils.getSessionIdFromContext()).thenReturn(SESSION_ID);
        when(authenticationContext.getEmailCurrentUser(SESSION_ID)).thenReturn(USER_EMAIL);

        mockMvc.perform(delete("/api/v1/users"))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    private UpsertUserRequest createRequest() {
        return UpsertUserRequest.builder()
                .name("user")
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

    private UserResponse createResponse(String email) {
        return UserResponse.builder()
                .name("user")
                .email(email)
                .build();
    }
}
