//package com.fallt.integration;
//
//import com.fallt.domain.dto.response.UserResponse;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.http.MediaType;
//
//import java.util.List;
//
//import static com.fallt.TestConstant.FIRST_USER_EMAIL;
//import static com.fallt.TestConstant.FIRST_USER_NAME;
//import static com.fallt.TestConstant.SECOND_USER_EMAIL;
//import static com.fallt.TestConstant.SECOND_USER_NAME;
//import static com.fallt.TestConstant.USER_BLOCK_PATH;
//import static com.fallt.TestConstant.USER_CONTROLLER_PATH;
//import static com.fallt.TestConstant.USER_REQUEST;
//import static com.fallt.TestConstant.USER_RESPONSE;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//class UserControllerTest extends AbstractTest {
//
//    @Test
//    @DisplayName("Обновление данных о пользователе")
//    void whenUpdateUser_thenReturnOk() throws Exception {
//        String content = objectMapper.writeValueAsString(USER_REQUEST);
//        Long id = 1L;
//
//        mockMvc.perform(put(USER_CONTROLLER_PATH + "/{id}", id)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(content))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(content().string(objectMapper.writeValueAsString(USER_RESPONSE)));
//    }
//
//    @Test
//    @DisplayName("Попытка обновления данных о пользователе с указанием используемого email")
//    void whenUpdateUserWithExistsEmail_thenReturnBadRequest() throws Exception {
//        String content = objectMapper.writeValueAsString(USER_REQUEST);
//
//        mockMvc.perform(put(USER_CONTROLLER_PATH)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(content))
//                .andDo(print())
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    @DisplayName("Получение данных о всех пользователях")
//    void whenGetAllUsers_thenReturnListUsers() throws Exception {
//        List<UserResponse> responseList = List.of(
//                UserResponse.builder().email(FIRST_USER_EMAIL).name(FIRST_USER_NAME).build(),
//                UserResponse.builder().email(SECOND_USER_EMAIL).name(SECOND_USER_NAME).build()
//        );
//
//        mockMvc.perform(get(USER_CONTROLLER_PATH))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(content().string(objectMapper.writeValueAsString(responseList)));
//    }
//
//    @Test
//    @DisplayName("Получение данных о всех пользователях")
//    void whenUserWithoutRoleAdminGetAllUsers_thenReturnForbidden() throws Exception {
//
//        mockMvc.perform(get(USER_CONTROLLER_PATH))
//                .andDo(print())
//                .andExpect(status().isForbidden());
//    }
//
//    @Test
//    @DisplayName("Блокировка пользователя")
//    void whenBlockingUser_thenReturnOk() throws Exception {
//        mockMvc.perform(put(USER_BLOCK_PATH + FIRST_USER_EMAIL))
//                .andDo(print())
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    @DisplayName("Попытка блокировки пользователя пользователем без роли ADMIN")
//    void whenUserWithoutRoleAdminBlockingUser_thenReturnForbidden() throws Exception {
//
//        mockMvc.perform(put(USER_BLOCK_PATH + FIRST_USER_EMAIL))
//                .andDo(print())
//                .andExpect(status().isForbidden());
//    }
//
//    @Test
//    @DisplayName("Удаление пользователя")
//    void whenDeleteUser_thenReturnNoContent() throws Exception {
//        mockMvc.perform(delete(USER_CONTROLLER_PATH))
//                .andDo(print())
//                .andExpect(status().isNoContent());
//    }
//
//}
