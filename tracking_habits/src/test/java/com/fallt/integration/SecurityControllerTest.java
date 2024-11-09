package com.fallt.integration;

import com.fallt.domain.dto.request.UpsertUserRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static com.fallt.TestConstant.LOGIN_PATH;
import static com.fallt.TestConstant.LOGIN_REQUEST;
import static com.fallt.TestConstant.LOGIN_RESPONSE;
import static com.fallt.TestConstant.REGISTER_PATH;
import static com.fallt.TestConstant.USER_REQUEST;
import static com.fallt.TestConstant.USER_RESPONSE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SecurityControllerTest extends AbstractTest {


    @Test
    @DisplayName("Регистрация нового пользователя")
    void whenCreateNewUser_thenReturnOk() throws Exception {
        String content = objectMapper.writeValueAsString(USER_REQUEST);

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
        UpsertUserRequest request = USER_REQUEST;
        request.setName("?");
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
                .andExpect(content().string(objectMapper.writeValueAsString(LOGIN_RESPONSE)));
    }

    @Test
    @DisplayName("Попытка входа в систему заблокированного пользователя")
    void whenBlockedUserLogin_thenReturnUnauthorized() throws Exception {
        String content = objectMapper.writeValueAsString(LOGIN_REQUEST);

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

        mockMvc.perform(post(LOGIN_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

}
