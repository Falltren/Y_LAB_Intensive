package com.fallt.controller;

import com.fallt.dto.response.UserResponse;
import com.fallt.entity.Role;
import com.fallt.exception.AlreadyExistException;
import com.fallt.exception.AuthorizationException;
import com.fallt.exception.ExceptionHandlingController;
import com.fallt.security.AuthenticationContext;
import com.fallt.service.UserService;
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

import static com.fallt.TestConstant.FIRST_USER_EMAIL;
import static com.fallt.TestConstant.FIRST_USER_NAME;
import static com.fallt.TestConstant.SECOND_USER_EMAIL;
import static com.fallt.TestConstant.SECOND_USER_NAME;
import static com.fallt.TestConstant.SESSION_ID;
import static com.fallt.TestConstant.USER_BLOCK_PATH;
import static com.fallt.TestConstant.USER_CONTROLLER_PATH;
import static com.fallt.TestConstant.USER_EMAIL;
import static com.fallt.TestConstant.USER_REQUEST;
import static com.fallt.TestConstant.USER_RESPONSE;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(ExceptionHandlingController.class)
                .build();
    }

    @Test
    @DisplayName("Обновление данных о пользователе")
    void whenUpdateUser_thenReturnOk() throws Exception {
        String content = objectMapper.writeValueAsString(USER_REQUEST);

        when(sessionUtils.getSessionIdFromContext()).thenReturn(SESSION_ID);
        when(authenticationContext.getEmailCurrentUser(SESSION_ID)).thenReturn(USER_EMAIL);
        when(userService.updateUser(USER_EMAIL, USER_REQUEST)).thenReturn(USER_RESPONSE);

        mockMvc.perform(put(USER_CONTROLLER_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(USER_RESPONSE)));
    }

    @Test
    @DisplayName("Попытка обновления данных о пользователе с указанием используемого email")
    void whenUpdateUserWithExistsEmail_thenReturnBadRequest() throws Exception {
        String content = objectMapper.writeValueAsString(USER_REQUEST);

        when(sessionUtils.getSessionIdFromContext()).thenReturn(SESSION_ID);
        when(authenticationContext.getEmailCurrentUser(SESSION_ID)).thenReturn(USER_EMAIL);
        when(userService.updateUser(USER_EMAIL, USER_REQUEST)).thenThrow(AlreadyExistException.class);

        mockMvc.perform(put(USER_CONTROLLER_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Получение данных о всех пользователях")
    void whenGetAllUsers_thenReturnListUsers() throws Exception {
        List<UserResponse> responseList = List.of(
                UserResponse.builder().email(FIRST_USER_EMAIL).name(FIRST_USER_NAME).build(),
                UserResponse.builder().email(SECOND_USER_EMAIL).name(SECOND_USER_NAME).build()
        );

        when(userService.getAllUsers()).thenReturn(responseList);

        mockMvc.perform(get(USER_CONTROLLER_PATH))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(responseList)));
    }

    @Test
    @DisplayName("Получение данных о всех пользователях")
    void whenUserWithoutRoleAdminGetAllUsers_thenReturnForbidden() throws Exception {
        when(sessionUtils.getSessionIdFromContext()).thenReturn(SESSION_ID);
        doThrow(AuthorizationException.class).when(authenticationContext).checkRole(SESSION_ID, Role.ROLE_ADMIN);

        mockMvc.perform(get(USER_CONTROLLER_PATH))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Блокировка пользователя")
    void whenBlockingUser_thenReturnOk() throws Exception {
        mockMvc.perform(put(USER_BLOCK_PATH + FIRST_USER_EMAIL))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Попытка блокировки пользователя пользователем без роли ADMIN")
    void whenUserWithoutRoleAdminBlockingUser_thenReturnForbidden() throws Exception {
        when(sessionUtils.getSessionIdFromContext()).thenReturn(SESSION_ID);
        doThrow(AuthorizationException.class).when(authenticationContext).checkRole(SESSION_ID, Role.ROLE_ADMIN);

        mockMvc.perform(put(USER_BLOCK_PATH + FIRST_USER_EMAIL))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Удаление пользователя")
    void whenDeleteUser_thenReturnNoContent() throws Exception {
        when(sessionUtils.getSessionIdFromContext()).thenReturn(SESSION_ID);
        when(authenticationContext.getEmailCurrentUser(SESSION_ID)).thenReturn(USER_EMAIL);

        mockMvc.perform(delete(USER_CONTROLLER_PATH))
                .andDo(print())
                .andExpect(status().isNoContent());
    }
}
