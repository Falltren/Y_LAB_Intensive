package com.fallt.controller;

import com.fallt.domain.dto.request.HabitConfirmRequest;
import com.fallt.exception.AlreadyExistException;
import com.fallt.exception.EntityNotFoundException;
import com.fallt.controller.advice.ExceptionHandlingController;
import com.fallt.exception.ValidationException;
import com.fallt.security.AuthenticationContext;
import com.fallt.service.HabitService;
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

import static com.fallt.TestConstant.CONFIRM_REQUEST;
import static com.fallt.TestConstant.CREATE_HABIT_PATH;
import static com.fallt.TestConstant.FIRST_HABIT_TITLE;
import static com.fallt.TestConstant.HABIT_BY_TITLE_PATH;
import static com.fallt.TestConstant.HABIT_CONFIRM_PATH;
import static com.fallt.TestConstant.HABIT_CONTROLLER_PATH;
import static com.fallt.TestConstant.HABIT_REQUEST;
import static com.fallt.TestConstant.HABIT_RESPONSE;
import static com.fallt.TestConstant.SECOND_HABIT_TITLE;
import static com.fallt.TestConstant.SESSION_ID;
import static com.fallt.TestConstant.USER_EMAIL;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class HabitControllerTest {

    @InjectMocks
    private HabitController habitController;

    @Mock
    private HabitService habitService;

    @Mock
    private AuthenticationContext authenticationContext;

    @Mock
    private ValidationService validationService;

    @Mock
    private SessionUtils sessionUtils;
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(habitController)
                .setControllerAdvice(ExceptionHandlingController.class)
                .build();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @DisplayName("Добавление привычки")
    void whenCreateHabit_thenReturnCreated() throws Exception {
        String content = objectMapper.writeValueAsString(HABIT_REQUEST);

        when(sessionUtils.getSessionIdFromContext()).thenReturn(SESSION_ID);
        when(authenticationContext.getEmailCurrentUser(SESSION_ID)).thenReturn(USER_EMAIL);
        when(habitService.saveHabit(USER_EMAIL, HABIT_REQUEST)).thenReturn(HABIT_RESPONSE);

        mockMvc.perform(post(CREATE_HABIT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string(objectMapper.writeValueAsString(HABIT_RESPONSE)));
    }

    @Test
    @DisplayName("Попытка добавления привычки без указания названия")
    void whenCreateHabitWithMissedTitle_thenReturnBadRequest() throws Exception {
        String content = objectMapper.writeValueAsString(HABIT_REQUEST);

        doThrow(ValidationException.class).when(validationService).checkUpsertHabitRequest(HABIT_REQUEST);

        mockMvc.perform(post(CREATE_HABIT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Попытка добавления уже имеющейся привычки")
    void whenCreateAlreadyExistsHabit_thenReturnBadRequest() throws Exception {
        String content = objectMapper.writeValueAsString(HABIT_REQUEST);

        when(sessionUtils.getSessionIdFromContext()).thenReturn(SESSION_ID);
        when(authenticationContext.getEmailCurrentUser(SESSION_ID)).thenReturn(USER_EMAIL);
        when(habitService.saveHabit(USER_EMAIL, HABIT_REQUEST)).thenThrow(AlreadyExistException.class);

        mockMvc.perform(post(CREATE_HABIT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Обновление привычки")
    void whenUpdateHabit_thenReturnOk() throws Exception {
        String content = objectMapper.writeValueAsString(HABIT_REQUEST);

        when(sessionUtils.getSessionIdFromContext()).thenReturn(SESSION_ID);
        when(authenticationContext.getEmailCurrentUser(SESSION_ID)).thenReturn(USER_EMAIL);
        when(habitService.updateHabit(USER_EMAIL, FIRST_HABIT_TITLE, HABIT_REQUEST)).thenReturn(HABIT_RESPONSE);

        mockMvc.perform(put(HABIT_BY_TITLE_PATH + FIRST_HABIT_TITLE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(HABIT_RESPONSE)));
    }

    @Test
    @DisplayName("Попытка обновления привычки по некорректному названию")
    void whenUpdateHabitByIncorrectTitle_thenReturnNotFound() throws Exception {
        String title = "incorrectTitle";
        String content = objectMapper.writeValueAsString(HABIT_REQUEST);

        when(sessionUtils.getSessionIdFromContext()).thenReturn(SESSION_ID);
        when(authenticationContext.getEmailCurrentUser(SESSION_ID)).thenReturn(USER_EMAIL);
        when(habitService.updateHabit(USER_EMAIL, title, HABIT_REQUEST)).thenThrow(EntityNotFoundException.class);

        mockMvc.perform(put(HABIT_BY_TITLE_PATH + title)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Попытка обновления привычки с указанием уже имеющегося названия")
    void whenUpdateWithExistsTitle_thenReturnBadRequest() throws Exception {
        String content = objectMapper.writeValueAsString(HABIT_REQUEST);

        when(sessionUtils.getSessionIdFromContext()).thenReturn(SESSION_ID);
        when(authenticationContext.getEmailCurrentUser(SESSION_ID)).thenReturn(USER_EMAIL);
        when(habitService.updateHabit(USER_EMAIL, SECOND_HABIT_TITLE, HABIT_REQUEST)).thenThrow(AlreadyExistException.class);

        mockMvc.perform(put(HABIT_BY_TITLE_PATH + SECOND_HABIT_TITLE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Получение всех привычек")
    void whenGetAllHabits_thenReturnOk() throws Exception {
        when(sessionUtils.getSessionIdFromContext()).thenReturn(SESSION_ID);
        when(authenticationContext.getEmailCurrentUser(SESSION_ID)).thenReturn(USER_EMAIL);

        mockMvc.perform(get(HABIT_CONTROLLER_PATH))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Удаление привычки")
    void whenDeleteHabit_thenReturnNoContent() throws Exception {
        when(sessionUtils.getSessionIdFromContext()).thenReturn(SESSION_ID);
        when(authenticationContext.getEmailCurrentUser(SESSION_ID)).thenReturn(USER_EMAIL);

        mockMvc.perform(delete(HABIT_BY_TITLE_PATH + FIRST_HABIT_TITLE))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Отметка выполнения привычки")
    void whenConfirmHabitExecution_thenReturnCreated() throws Exception {
        HabitConfirmRequest request = CONFIRM_REQUEST;
        request.setDate(LocalDate.now());
        String content = objectMapper.writeValueAsString(request);

        when(sessionUtils.getSessionIdFromContext()).thenReturn(SESSION_ID);
        when(authenticationContext.getEmailCurrentUser(SESSION_ID)).thenReturn(USER_EMAIL);

        mockMvc.perform(post(HABIT_CONFIRM_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Попытка отметки привычки без указания даты выполнения")
    void whenConfirmHabitWithMissedDate_thenReturnBadRequest() throws Exception {
        String content = objectMapper.writeValueAsString(CONFIRM_REQUEST);

        when(validationService.checkHabitConfirmRequest(CONFIRM_REQUEST)).thenThrow(ValidationException.class);

        mockMvc.perform(post(HABIT_CONFIRM_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}
