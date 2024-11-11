package com.fallt.controller;

import com.fallt.AbstractTest;
import com.fallt.domain.dto.request.ReportRequest;
import com.fallt.domain.dto.response.ExecutionDto;
import com.fallt.domain.dto.response.HabitProgress;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.List;

import static com.fallt.TestConstant.AUTHORIZATION_HEADER;
import static com.fallt.TestConstant.DAILY_HABIT_ID;
import static com.fallt.TestConstant.END_PERIOD;
import static com.fallt.TestConstant.FIRST_HABIT_TITLE;
import static com.fallt.TestConstant.FULL_REPORT_PATH;
import static com.fallt.TestConstant.MONTHLY_HABIT_ID;
import static com.fallt.TestConstant.NOT_EXIST_ID;
import static com.fallt.TestConstant.REPORT_REQUEST;
import static com.fallt.TestConstant.SECOND_HABIT_TITLE;
import static com.fallt.TestConstant.START_PERIOD;
import static com.fallt.TestConstant.STREAK_REPORT_PATH;
import static com.fallt.TestConstant.THIRD_HABIT_TITLE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class StatisticControllerTest extends AbstractTest {

    @Test
    @DisplayName("Получение полной статистики привычки с недельной частотой выполнения")
    void whenGetFullStatisticWeeklyHabit_thenReturnReport() throws Exception {
        String content = objectMapper.writeValueAsString(REPORT_REQUEST);
        List<ExecutionDto> executionDtos = List.of(
                new ExecutionDto(LocalDate.of(2024, 10, 1), LocalDate.of(2024, 10, 6), true),
                new ExecutionDto(LocalDate.of(2024, 10, 7), LocalDate.of(2024, 10, 13), false),
                new ExecutionDto(LocalDate.of(2024, 10, 14), LocalDate.of(2024, 10, 20), true),
                new ExecutionDto(LocalDate.of(2024, 10, 21), LocalDate.of(2024, 10, 25), false)
        );
        HabitProgress expected = new HabitProgress();
        expected.setTitle(FIRST_HABIT_TITLE);
        expected.setSuccessRate(50);
        expected.setExecution(executionDtos);

        mockMvc.perform(get(FULL_REPORT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .header(AUTHORIZATION_HEADER, token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(expected)));
    }

    @Test
    @DisplayName("Получение полной статистики привычки с месячной частотой выполнения")
    void whenGetFullStatisticMonthlyHabit_thenReturnReport() throws Exception {
        ReportRequest request = ReportRequest.builder()
                .habitId(MONTHLY_HABIT_ID)
                .start(START_PERIOD)
                .end(END_PERIOD)
                .build();
        String content = objectMapper.writeValueAsString(request);
        HabitProgress expected = new HabitProgress();
        expected.setTitle(SECOND_HABIT_TITLE);
        expected.setSuccessRate(100);
        expected.setExecution(List.of(
                new ExecutionDto(START_PERIOD, END_PERIOD, true)
        ));

        mockMvc.perform(get(FULL_REPORT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .header(AUTHORIZATION_HEADER, token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(expected)));
    }

    @Test
    @DisplayName("Получение полной статистики привычки с ежедневной частотой выполнения")
    void whenGetFullStatisticDailyHabit_thenReturnReport() throws Exception {
        ReportRequest request = ReportRequest.builder()
                .habitId(DAILY_HABIT_ID)
                .start(LocalDate.of(2024, 10, 21))
                .end(LocalDate.of(2024, 10, 25))
                .build();
        String content = objectMapper.writeValueAsString(request);
        HabitProgress expected = HabitProgress.builder()
                .title(THIRD_HABIT_TITLE)
                .successRate(60)
                .execution(List.of(
                        new ExecutionDto(LocalDate.of(2024, 10, 21), LocalDate.of(2024, 10, 21), true),
                        new ExecutionDto(LocalDate.of(2024, 10, 22), LocalDate.of(2024, 10, 22), false),
                        new ExecutionDto(LocalDate.of(2024, 10, 23), LocalDate.of(2024, 10, 23), true),
                        new ExecutionDto(LocalDate.of(2024, 10, 24), LocalDate.of(2024, 10, 24), true),
                        new ExecutionDto(LocalDate.of(2024, 10, 25), LocalDate.of(2024, 10, 25), false)
                ))
                .build();

        mockMvc.perform(get(FULL_REPORT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .header(AUTHORIZATION_HEADER, token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(expected)));
    }

    @Test
    @DisplayName("Попытка получения полной статистики неаутентифицированным пользователем")
    void whenAnonymousUserGetFullStatistic_thenReturnUnauthorized() throws Exception {
        String content = objectMapper.writeValueAsString(REPORT_REQUEST);

        mockMvc.perform(get(FULL_REPORT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Попытка получения полной статистики с указанием некорректного периода")
    void whenGetFullStatisticByIncorrectTitle_thenReturnBadRequest() throws Exception {
        LocalDate startPeriod = LocalDate.now();
        ReportRequest request = ReportRequest.builder()
                .habitId(100L)
                .start(startPeriod)
                .end(startPeriod.minusDays(20))
                .build();
        String content = objectMapper.writeValueAsString(request);

        mockMvc.perform(get(FULL_REPORT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .header(AUTHORIZATION_HEADER, token))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Попытка получений полной статистики по отсутствующей привычки")
    void whenGetFullStatisticByNotExistHabit_thenReturnNotFound() throws Exception {
        ReportRequest request = ReportRequest.builder()
                .habitId(NOT_EXIST_ID)
                .start(LocalDate.now())
                .end(LocalDate.now())
                .build();
        String content = objectMapper.writeValueAsString(request);

        mockMvc.perform(get(FULL_REPORT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .header(AUTHORIZATION_HEADER, token))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Получение серии выполнения привычки")
    void whenGetStreak_thenReturnOk() throws Exception {
        String content = objectMapper.writeValueAsString(REPORT_REQUEST);

        mockMvc.perform(get(STREAK_REPORT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .header(AUTHORIZATION_HEADER, token))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Попытка получения серии выполнения привычки неаутентифицированным пользователем")
    void whenAnonymousUserGetStrict_thenReturnUnauthorized() throws Exception {
        String content = objectMapper.writeValueAsString(REPORT_REQUEST);

        mockMvc.perform(get(STREAK_REPORT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Попытка получения серии выполнения отсутствующей привычки")
    void whenGetStreakByNotExistHabit_thenReturnNotFound() throws Exception {
        ReportRequest request = ReportRequest.builder()
                .habitId(NOT_EXIST_ID)
                .start(LocalDate.now())
                .end(LocalDate.now())
                .build();
        String content = objectMapper.writeValueAsString(request);
        String expected = MessageFormat.format("У вас отсутствует привычка с указанным ID: {0}", NOT_EXIST_ID);

        mockMvc.perform(get(STREAK_REPORT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .header(AUTHORIZATION_HEADER, token))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorDescription").value(expected));
    }

}
