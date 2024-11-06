package com.fallt.controller;

import com.fallt.dto.request.HabitConfirmRequest;
import com.fallt.dto.request.UpsertHabitRequest;
import com.fallt.dto.response.HabitResponse;
import com.fallt.exception.AlreadyExistException;
import com.fallt.exception.EntityNotFoundException;
import com.fallt.exception.ExceptionHandlingController;
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
import java.util.ArrayList;

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
        UpsertHabitRequest request = createRequest();
        when(sessionUtils.getSessionIdFromContext()).thenReturn(SESSION_ID);
        when(authenticationContext.getEmailCurrentUser(SESSION_ID)).thenReturn(USER_EMAIL);
        String content = objectMapper.writeValueAsString(request);
        HabitResponse response = createResponse();
        when(habitService.saveHabit(USER_EMAIL, request)).thenReturn(response);

        mockMvc.perform(post("/api/v1/habits/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string(objectMapper.writeValueAsString(response)));
    }

    @Test
    @DisplayName("Попытка добавления привычки без указания названия")
    void whenCreateHabitWithMissedTitle_thenReturnBadRequest() throws Exception {
        UpsertHabitRequest request = createRequest();
        String content = objectMapper.writeValueAsString(request);
        doThrow(ValidationException.class).when(validationService).checkUpsertHabitRequest(request);

        mockMvc.perform(post("/api/v1/habits/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Попытка добавления уже имеющейся привычки")
    void whenCreateAlreadyExistsHabit_thenReturnBadRequest() throws Exception {
        UpsertHabitRequest request = createRequest();
        when(sessionUtils.getSessionIdFromContext()).thenReturn(SESSION_ID);
        when(authenticationContext.getEmailCurrentUser(SESSION_ID)).thenReturn(USER_EMAIL);
        String content = objectMapper.writeValueAsString(request);
        when(habitService.saveHabit(USER_EMAIL, request)).thenThrow(AlreadyExistException.class);

        mockMvc.perform(post("/api/v1/habits/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Обновление привычки")
    void whenUpdateHabit_thenReturnOk() throws Exception {
        String title = "title";
        UpsertHabitRequest request = createRequest();
        when(sessionUtils.getSessionIdFromContext()).thenReturn(SESSION_ID);
        when(authenticationContext.getEmailCurrentUser(SESSION_ID)).thenReturn(USER_EMAIL);
        String content = objectMapper.writeValueAsString(request);
        HabitResponse response = createResponse();
        when(habitService.updateHabit(USER_EMAIL, title, request)).thenReturn(response);

        mockMvc.perform(put("/api/v1/habits?title=" + title)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(response)));
    }

    @Test
    @DisplayName("Попытка обновления привычки по некорректному названию")
    void whenUpdateHabitByIncorrectTitle_thenReturnNotFound() throws Exception {
        String title = "incorrectTitle";
        UpsertHabitRequest request = createRequest();
        when(sessionUtils.getSessionIdFromContext()).thenReturn(SESSION_ID);
        when(authenticationContext.getEmailCurrentUser(SESSION_ID)).thenReturn(USER_EMAIL);
        String content = objectMapper.writeValueAsString(request);
        when(habitService.updateHabit(USER_EMAIL, title, request)).thenThrow(EntityNotFoundException.class);

        mockMvc.perform(put("/api/v1/habits?title=" + title)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Попытка обновления привычки с указанием уже имеющегося названия")
    void whenUpdateWithExistsTitle_thenReturnBadRequest() throws Exception {
        String title = "title";
        UpsertHabitRequest request = createRequest();
        when(sessionUtils.getSessionIdFromContext()).thenReturn(SESSION_ID);
        when(authenticationContext.getEmailCurrentUser(SESSION_ID)).thenReturn(USER_EMAIL);
        String content = objectMapper.writeValueAsString(request);
        when(habitService.updateHabit(USER_EMAIL, title, request)).thenThrow(AlreadyExistException.class);

        mockMvc.perform(put("/api/v1/habits?title=" + title)
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

        mockMvc.perform(get("/api/v1/habits"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Удаление привычки")
    void whenDeleteHabit_thenReturnNoContent() throws Exception {
        String title = "title";
        when(sessionUtils.getSessionIdFromContext()).thenReturn(SESSION_ID);
        when(authenticationContext.getEmailCurrentUser(SESSION_ID)).thenReturn(USER_EMAIL);

        mockMvc.perform(delete("/api/v1/habits?title=" + title))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Отметка выполнения привычки")
    void whenConfirmHabitExecution_thenReturnCreated() throws Exception {
        HabitConfirmRequest request = HabitConfirmRequest.builder()
                .title("title")
                .date(LocalDate.now())
                .build();
        String content = objectMapper.writeValueAsString(request);
        when(sessionUtils.getSessionIdFromContext()).thenReturn(SESSION_ID);
        when(authenticationContext.getEmailCurrentUser(SESSION_ID)).thenReturn(USER_EMAIL);

        mockMvc.perform(post("/api/v1/habits/confirm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Попытка отметки привычки без указания даты выполнения")
    void whenConfirmHabitWithMissedDate_thenReturnBadRequest() throws Exception {
        HabitConfirmRequest request = HabitConfirmRequest.builder()
                .title("title")
                .build();
        String content = objectMapper.writeValueAsString(request);
        when(validationService.checkHabitConfirmRequest(request)).thenThrow(ValidationException.class);

        mockMvc.perform(post("/api/v1/habits/confirm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }


    private UpsertHabitRequest createRequest() {
        return UpsertHabitRequest.builder()
                .title("title")
                .text("text")
                .rate("WEEKLY")
                .build();
    }

    private HabitResponse createResponse() {
        return HabitResponse.builder()
                .title("title")
                .text("text")
                .successfulExecution(new ArrayList<>())
                .build();
    }
}
