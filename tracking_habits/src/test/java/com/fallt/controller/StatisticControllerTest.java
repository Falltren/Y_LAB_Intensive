package com.fallt.controller;

import com.fallt.exception.EntityNotFoundException;
import com.fallt.controller.advice.GlobalExceptionHandler;
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

import static com.fallt.TestConstant.FULL_REPORT_PATH;
import static com.fallt.TestConstant.REPORT_REQUEST;
import static com.fallt.TestConstant.SESSION_ID;
import static com.fallt.TestConstant.STREAK_REPORT_PATH;
import static com.fallt.TestConstant.USER_EMAIL;
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

    @BeforeEach
    void setup() {
        objectMapper.registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders.standaloneSetup(statisticController)
                .setControllerAdvice(GlobalExceptionHandler.class)
                .build();
    }

    @Test
    @DisplayName("Получение полной статистики")
    void whenGetFullStatistic_thenReturnOk() throws Exception {
        String content = objectMapper.writeValueAsString(REPORT_REQUEST);

        when(sessionUtils.getSessionIdFromContext()).thenReturn(SESSION_ID);
        when(authenticationContext.getEmailCurrentUser(SESSION_ID)).thenReturn(USER_EMAIL);

        mockMvc.perform(get(FULL_REPORT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Попытка получения полной статистики с указанием некорректного названия")
    void whenGetFullStatisticByIncorrectTitle_thenReturnBadRequest() throws Exception {
        String content = objectMapper.writeValueAsString(REPORT_REQUEST);

        doThrow(ValidationException.class).when(validationService).checkReportRequest(REPORT_REQUEST);

        mockMvc.perform(get(FULL_REPORT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Попытка получений полной статистики по отсутствующей привычки")
    void whenGetFullStatisticByNotExistHabit_thenReturnNotFound() throws Exception {
        String content = objectMapper.writeValueAsString(REPORT_REQUEST);

        when(sessionUtils.getSessionIdFromContext()).thenReturn(SESSION_ID);
        when(authenticationContext.getEmailCurrentUser(SESSION_ID)).thenReturn(USER_EMAIL);
        when(statisticService.getHabitProgress(USER_EMAIL, REPORT_REQUEST)).thenThrow(EntityNotFoundException.class);

        mockMvc.perform(get(FULL_REPORT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Получение серии выполнения привычки")
    void whenGetStreak_thenReturnOk() throws Exception {
        String content = objectMapper.writeValueAsString(REPORT_REQUEST);

        when(sessionUtils.getSessionIdFromContext()).thenReturn(SESSION_ID);
        when(authenticationContext.getEmailCurrentUser(SESSION_ID)).thenReturn(USER_EMAIL);

        mockMvc.perform(get(STREAK_REPORT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Попытка получения серии выполнения привычки с указанием некорректного названия")
    void whenGetStrictByIncorrectTitle_thenReturnBadRequest() throws Exception {
        String content = objectMapper.writeValueAsString(REPORT_REQUEST);

        doThrow(ValidationException.class).when(validationService).checkReportRequest(REPORT_REQUEST);

        mockMvc.perform(get(STREAK_REPORT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Попытка получения серии выполнения привычки с указанием отсутствующей привычки")
    void whenGetStreakByNotExistHabit_thenReturnNotFound() throws Exception {
        String content = objectMapper.writeValueAsString(REPORT_REQUEST);

        when(sessionUtils.getSessionIdFromContext()).thenReturn(SESSION_ID);
        when(authenticationContext.getEmailCurrentUser(SESSION_ID)).thenReturn(USER_EMAIL);
        when(statisticService.getHabitStreak(USER_EMAIL, REPORT_REQUEST)).thenThrow(EntityNotFoundException.class);

        mockMvc.perform(get(STREAK_REPORT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}
