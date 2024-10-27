package com.fallt.servlet;

import com.fallt.dto.request.HabitConfirmRequest;
import com.fallt.exception.EntityNotFoundException;
import com.fallt.exception.SecurityException;
import com.fallt.exception.ValidationException;
import com.fallt.security.AuthenticationContext;
import com.fallt.service.HabitService;
import com.fallt.service.ValidationService;
import com.fallt.util.DelegatingServletInputStream;
import com.fallt.util.DelegatingServletOutputStream;
import com.fallt.util.SessionUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConfirmExecutionHabitServletTest {

    @InjectMocks
    private ConfirmExecutionHabitServlet confirmExecutionHabitServlet;

    @Mock
    private HabitService habitService;

    @Mock
    private ServletConfig servletConfig;

    @Mock
    private ServletContext servletContext;

    @Mock
    private AuthenticationContext authenticationContext;

    @Mock
    private ValidationService validationService;

    @Mock
    private HttpServletRequest req;

    @Mock
    private HttpServletResponse resp;

    @Mock
    private HttpSession session;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() throws ServletException {
        confirmExecutionHabitServlet.init(servletConfig);
        when(servletContext.getAttribute("habitService")).thenReturn(habitService);
        when(servletContext.getAttribute("objectMapper")).thenReturn(objectMapper);
        when(servletContext.getAttribute("authContext")).thenReturn(authenticationContext);
        when(servletContext.getAttribute("validationService")).thenReturn(validationService);
        when(confirmExecutionHabitServlet.getServletContext()).thenReturn(servletContext);
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @DisplayName("Регистрация выполнения привычки")
    void whenConfirmHabitExecution_thenReturnCreated() throws Exception {
        String currentEmail = "userEmail";
        when(req.getSession()).thenReturn(session);
        when(SessionUtils.getCurrentUserEmail(req)).thenReturn(currentEmail);
        HabitConfirmRequest request = new HabitConfirmRequest("title", LocalDate.now());
        when(req.getInputStream()).thenReturn(new DelegatingServletInputStream(objectMapper.writeValueAsBytes(request)));
        when(resp.getOutputStream()).thenReturn(new DelegatingServletOutputStream());
        when(validationService.checkHabitConfirmRequest(request)).thenReturn(true);

        confirmExecutionHabitServlet.doPost(req, resp);

        verify(resp).setStatus(HttpServletResponse.SC_CREATED);
        verify(habitService, times(1)).confirmHabit(currentEmail, request);
    }

    @Test
    @DisplayName("Попытка регистрации выполнения несуществующей привычки")
    void whenConfirmIncorrectHabit_thenReturnBadRequest() throws Exception {
        String currentEmail = "userEmail";
        when(req.getSession()).thenReturn(session);
        when(SessionUtils.getCurrentUserEmail(req)).thenReturn(currentEmail);
        HabitConfirmRequest request = new HabitConfirmRequest("incorrectTitle", LocalDate.now());
        when(req.getInputStream()).thenReturn(new DelegatingServletInputStream(objectMapper.writeValueAsBytes(request)));
        when(resp.getOutputStream()).thenReturn(new DelegatingServletOutputStream());
        when(validationService.checkHabitConfirmRequest(request)).thenReturn(true);
        when(habitService.confirmHabit(currentEmail, request)).thenThrow(EntityNotFoundException.class);

        confirmExecutionHabitServlet.doPost(req, resp);

        verify(resp).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("Попытка регистрации выполнения привычки неаутентифицированным пользователем")
    void whenConfirmHabitAnonymousUser_thenReturnUnauthorized() throws Exception {
        String currentEmail = "userEmail";
        when(req.getSession()).thenReturn(session);
        when(SessionUtils.getCurrentUserEmail(req)).thenReturn(currentEmail);
        HabitConfirmRequest request = new HabitConfirmRequest("title", LocalDate.now());
        when(req.getInputStream()).thenReturn(new DelegatingServletInputStream(objectMapper.writeValueAsBytes(request)));
        when(resp.getOutputStream()).thenReturn(new DelegatingServletOutputStream());
        doThrow(new SecurityException("У вас недостаточно прав для выполнения данного действия")).when(authenticationContext).checkAuthentication(currentEmail);

        confirmExecutionHabitServlet.doPost(req, resp);

        verify(resp).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    @DisplayName("Попытка регистрации выполнения привычки с некорректным запросом")
    void whenConfirmHabitWithIncorrectRequest_thenReturnBadRequest() throws Exception {
        String currentEmail = "userEmail";
        when(req.getSession()).thenReturn(session);
        when(SessionUtils.getCurrentUserEmail(req)).thenReturn(currentEmail);
        HabitConfirmRequest request = new HabitConfirmRequest("title", LocalDate.now());
        when(req.getInputStream()).thenReturn(new DelegatingServletInputStream(objectMapper.writeValueAsBytes(request)));
        when(resp.getOutputStream()).thenReturn(new DelegatingServletOutputStream());
        when(validationService.checkHabitConfirmRequest(request)).thenThrow(ValidationException.class);

        confirmExecutionHabitServlet.doPost(req, resp);

        verify(resp).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

}
