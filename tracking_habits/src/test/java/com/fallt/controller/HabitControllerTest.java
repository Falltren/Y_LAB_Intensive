package com.fallt.controller;

import com.fallt.AbstractTest;
import com.fallt.domain.dto.request.HabitConfirmRequest;
import com.fallt.domain.dto.request.UpsertHabitRequest;
import com.fallt.domain.dto.response.HabitResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.fallt.TestConstant.AUTHORIZATION_HEADER;
import static com.fallt.TestConstant.CONFIRM_REQUEST;
import static com.fallt.TestConstant.CREATE_HABIT_PATH;
import static com.fallt.TestConstant.DAILY_HABIT;
import static com.fallt.TestConstant.DAILY_HABIT_ID;
import static com.fallt.TestConstant.EXIST_HABIT_REQUEST;
import static com.fallt.TestConstant.FIRST_HABIT_TITLE;
import static com.fallt.TestConstant.HABIT_BY_ID_PATH;
import static com.fallt.TestConstant.HABIT_CONFIRM_PATH;
import static com.fallt.TestConstant.HABIT_CONTROLLER_PATH;
import static com.fallt.TestConstant.HABIT_TEXT;
import static com.fallt.TestConstant.NEW_HABIT_REQUEST;
import static com.fallt.TestConstant.NEW_HABIT_TITLE;
import static com.fallt.TestConstant.NOT_EXIST_ID;
import static com.fallt.TestConstant.SECOND_HABIT_TITLE;
import static com.fallt.TestConstant.THIRD_HABIT_TITLE;
import static com.fallt.TestConstant.WEEKLY_HABIT_ID;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class HabitControllerTest extends AbstractTest {

    @Test
    @DisplayName("Добавление привычки")
    void whenCreateHabit_thenReturnCreated() throws Exception {
        HabitResponse response = HabitResponse.builder()
                .title(NEW_HABIT_TITLE)
                .text(HABIT_TEXT)
                .successfulExecution(new ArrayList<>())
                .build();
        String content = objectMapper.writeValueAsString(NEW_HABIT_REQUEST);

        mockMvc.perform(post(CREATE_HABIT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .header(AUTHORIZATION_HEADER, token))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string(objectMapper.writeValueAsString(response)));
    }

    @Test
    @DisplayName("Попытка добавления привычки неаутентифицированным пользователем")
    void whenAnonymousUserCreateHabit_thenReturnUnauthorized() throws Exception {
        String content = objectMapper.writeValueAsString(NEW_HABIT_REQUEST);

        mockMvc.perform(post(CREATE_HABIT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Попытка добавления привычки без указания названия")
    void whenCreateHabitWithMissedTitle_thenReturnBadRequest() throws Exception {
        UpsertHabitRequest request = new UpsertHabitRequest(null, HABIT_TEXT, DAILY_HABIT);
        String content = objectMapper.writeValueAsString(request);

        mockMvc.perform(post(CREATE_HABIT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .header(AUTHORIZATION_HEADER, token))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Попытка добавления уже имеющейся привычки")
    void whenCreateAlreadyExistsHabit_thenReturnBadRequest() throws Exception {
        String content = objectMapper.writeValueAsString(EXIST_HABIT_REQUEST);

        mockMvc.perform(post(CREATE_HABIT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .header(AUTHORIZATION_HEADER, token))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Обновление привычки")
    void whenUpdateHabit_thenReturnOk() throws Exception {
        String content = objectMapper.writeValueAsString(NEW_HABIT_REQUEST);
        HabitResponse response = HabitResponse.builder()
                .title(NEW_HABIT_TITLE)
                .text(HABIT_TEXT)
                .successfulExecution(List.of(
                        LocalDate.of(2024, 10, 5),
                        LocalDate.of(2024, 10, 20)
                ))
                .build();

        mockMvc.perform(put(HABIT_BY_ID_PATH, WEEKLY_HABIT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .header(AUTHORIZATION_HEADER, token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(response)));
    }

    @Test
    @DisplayName("Попытка обновления привычки неаутентифицированным пользователем")
    void whenAnonymousUpdateHabit_thenReturnUnauthorized() throws Exception {
        String content = objectMapper.writeValueAsString(NEW_HABIT_REQUEST);

        mockMvc.perform(put(HABIT_BY_ID_PATH, WEEKLY_HABIT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Попытка обновления отсутствующей привычки")
    void whenUpdateHabitByIncorrectTitle_thenReturnNotFound() throws Exception {
        String content = objectMapper.writeValueAsString(NEW_HABIT_REQUEST);

        mockMvc.perform(put(HABIT_BY_ID_PATH, NOT_EXIST_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .header(AUTHORIZATION_HEADER, token))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Попытка обновления привычки с указанием уже имеющегося названия")
    void whenUpdateWithExistsTitle_thenReturnBadRequest() throws Exception {
        String content = objectMapper.writeValueAsString(EXIST_HABIT_REQUEST);

        mockMvc.perform(put(HABIT_BY_ID_PATH, WEEKLY_HABIT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .header(AUTHORIZATION_HEADER, token))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Получение всех привычек")
    void whenGetAllHabits_thenReturnOk() throws Exception {
        List<HabitResponse> response = List.of(
                new HabitResponse(FIRST_HABIT_TITLE, HABIT_TEXT, List.of(
                        LocalDate.of(2024, 10, 5),
                        LocalDate.of(2024, 10, 20)
                )),
                new HabitResponse(SECOND_HABIT_TITLE, HABIT_TEXT, List.of(
                        LocalDate.of(2024, 10, 21)
                )),
                new HabitResponse(THIRD_HABIT_TITLE, HABIT_TEXT, List.of(
                        LocalDate.of(2024, 10, 21),
                        LocalDate.of(2024, 10, 23),
                        LocalDate.of(2024, 10, 24)
                ))
        );

        mockMvc.perform(get(HABIT_CONTROLLER_PATH)
                        .header(AUTHORIZATION_HEADER, token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(response)));
    }

    @Test
    @DisplayName("Удаление привычки")
    void whenDeleteHabit_thenReturnNoContent() throws Exception {
        mockMvc.perform(delete(HABIT_BY_ID_PATH, WEEKLY_HABIT_ID)
                        .header(AUTHORIZATION_HEADER, token))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Отметка выполнения привычки")
    void whenConfirmHabitExecution_thenReturnCreated() throws Exception {
        HabitConfirmRequest request = CONFIRM_REQUEST;
        request.setDate(LocalDate.now());
        String content = objectMapper.writeValueAsString(request);

        mockMvc.perform(post(HABIT_CONFIRM_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .header(AUTHORIZATION_HEADER, token))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Попытка отметки привычки без указания даты выполнения")
    void whenConfirmHabitWithMissedDate_thenReturnBadRequest() throws Exception {
        HabitConfirmRequest request = HabitConfirmRequest.builder()
                .habitId(DAILY_HABIT_ID)
                .build();
        String content = objectMapper.writeValueAsString(request);

        mockMvc.perform(post(HABIT_CONFIRM_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .header(AUTHORIZATION_HEADER, token))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

}
