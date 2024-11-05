package com.fallt.controller;

import com.fallt.dto.request.ReportRequest;
import com.fallt.exception.EntityNotFoundException;
import com.fallt.exception.ExceptionHandlingController;
import com.fallt.exception.ValidationException;
import com.fallt.security.AuthenticationContext;
import com.fallt.service.StatisticService;
import com.fallt.service.impl.ValidationService;
import com.fallt.util.SessionUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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

import java.time.LocalDate;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class StatisticControllerTest {

    @InjectMocks
    private StatisticController statisticController;

    @Mock
    private StatisticService statisticService;

    @Mock
    private SessionUtils sessionUtils;

    @Mock
    private ValidationService validationService;

    @Mock
    private AuthenticationContext authenticationContext;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String SESSION_ID = "sessionId";

    private static final String USER_EMAIL = "email";

    private static final String FULL_REPORT_URL = "/api/v1/reports/full";
    private static final String STREAK_REPORT_URL = "/api/v1/reports/streak";

    @BeforeEach
    void setup() {
        objectMapper.registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders.standaloneSetup(statisticController)
                .setControllerAdvice(ExceptionHandlingController.class)
                .build();
    }

    @Test
    @DisplayName("Получение полной статистики")
    void whenGetFullStatistic_thenReturnOk() throws Exception {
        ReportRequest request = createRequest();
        String content = objectMapper.writeValueAsString(request);
        when(sessionUtils.getSessionIdFromContext()).thenReturn(SESSION_ID);
        when(authenticationContext.getEmailCurrentUser(SESSION_ID)).thenReturn(USER_EMAIL);

        mockMvc.perform(get(FULL_REPORT_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Попытка получения полной статистики с указанием некорректного названия")
    void whenGetFullStatisticByIncorrectTitle_thenReturnBadRequest() throws Exception {
        ReportRequest request = createRequest();
        String content = objectMapper.writeValueAsString(request);
        doThrow(ValidationException.class).when(validationService).checkReportRequest(request);

        mockMvc.perform(get(FULL_REPORT_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Попытка получений полной статистики по отсутствующей привычки")
    void whenGetFullStatisticByNotExistHabit_thenReturnNotFound() throws Exception {
        ReportRequest request = createRequest();
        String content = objectMapper.writeValueAsString(request);
        when(sessionUtils.getSessionIdFromContext()).thenReturn(SESSION_ID);
        when(authenticationContext.getEmailCurrentUser(SESSION_ID)).thenReturn(USER_EMAIL);
        when(statisticService.getHabitProgress(USER_EMAIL, request)).thenThrow(EntityNotFoundException.class);

        mockMvc.perform(get(FULL_REPORT_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Получение серии выполнения привычки")
    void whenGetStreak_thenReturnOk() throws Exception {
        ReportRequest request = createRequest();
        String content = objectMapper.writeValueAsString(request);
        when(sessionUtils.getSessionIdFromContext()).thenReturn(SESSION_ID);
        when(authenticationContext.getEmailCurrentUser(SESSION_ID)).thenReturn(USER_EMAIL);

        mockMvc.perform(get(STREAK_REPORT_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Попытка получения серии выполнения привычки с указанием некорректного названия")
    void whenGetStrictByIncorrectTitle_thenReturnBadRequest() throws Exception {
        ReportRequest request = createRequest();
        String content = objectMapper.writeValueAsString(request);
        doThrow(ValidationException.class).when(validationService).checkReportRequest(request);

        mockMvc.perform(get(STREAK_REPORT_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Попытка получения серии выполнения привычки с указанием отсутствующей привычки")
    void whenGetStreakByNotExistHabit_thenReturnNotFound() throws Exception {
        ReportRequest request = createRequest();
        String content = objectMapper.writeValueAsString(request);
        when(sessionUtils.getSessionIdFromContext()).thenReturn(SESSION_ID);
        when(authenticationContext.getEmailCurrentUser(SESSION_ID)).thenReturn(USER_EMAIL);
        when(statisticService.getHabitStreak(USER_EMAIL, request)).thenThrow(EntityNotFoundException.class);

        mockMvc.perform(get(STREAK_REPORT_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    private ReportRequest createRequest() {
        return ReportRequest.builder()
                .title("title")
                .start(LocalDate.of(2024, 10, 1))
                .end(LocalDate.of(2024, 10, 20))
                .build();
    }
}
