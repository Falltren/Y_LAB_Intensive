package com.fallt.controller;

import com.fallt.AbstractTest;
import com.fallt.domain.dto.request.UpsertUserRequest;
import com.fallt.domain.dto.response.UserResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.List;

import static com.fallt.TestConstant.ADMIN_EMAIL;
import static com.fallt.TestConstant.ADMIN_ID;
import static com.fallt.TestConstant.ADMIN_NAME;
import static com.fallt.TestConstant.AUTHORIZATION_HEADER;
import static com.fallt.TestConstant.FIRST_USER_EMAIL;
import static com.fallt.TestConstant.FIRST_USER_ID;
import static com.fallt.TestConstant.FIRST_USER_NAME;
import static com.fallt.TestConstant.ROLE_ADMIN;
import static com.fallt.TestConstant.SECOND_USER_EMAIL;
import static com.fallt.TestConstant.SECOND_USER_ID;
import static com.fallt.TestConstant.SECOND_USER_NAME;
import static com.fallt.TestConstant.USER_BLOCK_PATH;
import static com.fallt.TestConstant.USER_BY_ID;
import static com.fallt.TestConstant.USER_CONTROLLER_PATH;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest extends AbstractTest {

    @Test
    @DisplayName("Обновление данных о пользователе")
    void whenUpdateUser_thenReturnOk() throws Exception {
        UpsertUserRequest request = UpsertUserRequest.builder()
                .email(SECOND_USER_EMAIL)
                .name(SECOND_USER_NAME)
                .build();
        String content = objectMapper.writeValueAsString(request);
        UserResponse response = UserResponse.builder()
                .email(SECOND_USER_EMAIL)
                .name(SECOND_USER_NAME)
                .build();

        mockMvc.perform(put(USER_BY_ID, FIRST_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .header(AUTHORIZATION_HEADER, token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(response)));
    }

    @Test
    @DisplayName("Попытка обновления данных о пользователе с указанием используемого email")
    void whenUpdateUserWithExistsEmail_thenReturnBadRequest() throws Exception {
        UpsertUserRequest request = UpsertUserRequest.builder()
                .email(ADMIN_EMAIL)
                .name(SECOND_USER_NAME)
                .build();
        String content = objectMapper.writeValueAsString(request);

        mockMvc.perform(put(USER_BY_ID, FIRST_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .header(AUTHORIZATION_HEADER, token))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Получение данных о всех пользователях")
    void whenGetAllUsers_thenReturnListUsers() throws Exception {
        String token = createJwtToken(ADMIN_ID, ADMIN_EMAIL, ROLE_ADMIN);
        List<UserResponse> responseList = List.of(
                UserResponse.builder().email(ADMIN_EMAIL).name(ADMIN_NAME).build(),
                UserResponse.builder().email(FIRST_USER_EMAIL).name(FIRST_USER_NAME).build(),
                UserResponse.builder().email("blocked@user.com").name("blocked").build()
        );

        mockMvc.perform(get(USER_CONTROLLER_PATH)
                        .header(AUTHORIZATION_HEADER, token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(responseList)));
    }

    @Test
    @DisplayName("Попытка получения данных о всех пользователях пользователем без роли ADMIN")
    void whenUserWithoutRoleAdminGetAllUsers_thenReturnForbidden() throws Exception {
        mockMvc.perform(get(USER_CONTROLLER_PATH)
                        .header(AUTHORIZATION_HEADER, token))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Блокировка пользователя")
    void whenBlockingUser_thenReturnOk() throws Exception {
        String token = createJwtToken(ADMIN_ID, FIRST_USER_EMAIL, ROLE_ADMIN);

        mockMvc.perform(put(USER_BLOCK_PATH, FIRST_USER_ID)
                        .header(AUTHORIZATION_HEADER, token))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Попытка блокировки пользователя пользователем без роли ADMIN")
    void whenUserWithoutRoleAdminBlockingUser_thenReturnForbidden() throws Exception {
        mockMvc.perform(put(USER_BLOCK_PATH, SECOND_USER_ID)
                        .header(AUTHORIZATION_HEADER, token))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Удаление пользователя")
    void whenDeleteUser_thenReturnNoContent() throws Exception {
        mockMvc.perform(delete(USER_BY_ID, FIRST_USER_ID)
                        .header(AUTHORIZATION_HEADER, token))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

}
