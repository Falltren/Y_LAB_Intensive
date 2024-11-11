package com.fallt.controller;

import com.fallt.AbstractTest;
import com.fallt.domain.dto.request.LoginRequest;
import com.fallt.domain.dto.request.UpsertUserRequest;
import com.fallt.domain.dto.response.UserResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static com.fallt.TestConstant.FIRST_USER_EMAIL;
import static com.fallt.TestConstant.FIRST_USER_NAME;
import static com.fallt.TestConstant.FIRST_USER_PASSWORD;
import static com.fallt.TestConstant.LOGIN_PATH;
import static com.fallt.TestConstant.LOGIN_REQUEST;
import static com.fallt.TestConstant.REGISTER_PATH;
import static com.fallt.TestConstant.SECOND_USER_EMAIL;
import static com.fallt.TestConstant.SECOND_USER_NAME;
import static com.fallt.TestConstant.SECOND_USER_PASSWORD;
import static com.fallt.TestConstant.USER_REQUEST;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SecurityControllerTest extends AbstractTest {

    @Test
    @DisplayName("Регистрация нового пользователя")
    void whenCreateNewUser_thenReturnOk() throws Exception {
        UpsertUserRequest userRequest = UpsertUserRequest.builder()
                .name(SECOND_USER_NAME)
                .email(SECOND_USER_EMAIL)
                .password(SECOND_USER_PASSWORD)
                .build();
        UserResponse response = UserResponse.builder()
                .name(SECOND_USER_NAME)
                .email(SECOND_USER_EMAIL)
                .build();
        String content = objectMapper.writeValueAsString(userRequest);

        mockMvc.perform(post(REGISTER_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string(objectMapper.writeValueAsString(response)));
    }

    @Test
    @DisplayName("Попытка регистрации нового пользователя с невалидными данными")
    void whenCreateNewUserWithIncorrectData_thenReturnBadRequest() throws Exception {
        UpsertUserRequest request = UpsertUserRequest.builder()
                .name("?")
                .email(SECOND_USER_EMAIL)
                .password(SECOND_USER_PASSWORD)
                .build();
        String content = objectMapper.writeValueAsString(request);

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

        mockMvc.perform(post(LOGIN_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(FIRST_USER_NAME));
    }

    @Test
    @DisplayName("Попытка входа в систему заблокированного пользователя")
    void whenBlockedUserLogin_thenReturnUnauthorized() throws Exception {
        LoginRequest request = createRequest("blocked@user.com", "BlockedUser1");
        String content = objectMapper.writeValueAsString(request);

        mockMvc.perform(post(LOGIN_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorDescription").value("Ваша учетная запись заблокирована"));
    }

    @Test
    @DisplayName("Попытка входа в систему с отсутствующей в бд электронной почтой")
    void whenLoginWithIncorrectEmail_thenReturnBadRequest() throws Exception {
        LoginRequest request = createRequest(SECOND_USER_EMAIL, SECOND_USER_PASSWORD);
        String content = objectMapper.writeValueAsString(request);

        mockMvc.perform(post(LOGIN_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Попытка входа в систему с невалидным email")
    void whenLoginWithMissedPassword_thenReturnBadRequest() throws Exception {
        LoginRequest request = createRequest("invalid", FIRST_USER_PASSWORD);
        String content = objectMapper.writeValueAsString(request);

        mockMvc.perform(post(LOGIN_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Попытка входа в систему с неверным паролем")
    void whenLoginWithInvalidPassword_thenReturnNotFound() throws Exception {
        LoginRequest request = createRequest(FIRST_USER_EMAIL, SECOND_USER_PASSWORD);
        String content = objectMapper.writeValueAsString(request);

        mockMvc.perform(post(LOGIN_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    private LoginRequest createRequest(String email, String password) {
        return LoginRequest.builder()
                .email(email)
                .password(password)
                .build();
    }

}
