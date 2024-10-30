//package com.fallt.servlet;
//
//import com.fallt.dto.request.UpsertHabitRequest;
//import com.fallt.exception.AlreadyExistException;
//import com.fallt.exception.SecurityException;
//import com.fallt.exception.ValidationException;
//import com.fallt.security.AuthenticationContext;
//import com.fallt.service.HabitService;
//import com.fallt.service.ValidationService;
//import com.fallt.util.DelegatingServletInputStream;
//import com.fallt.util.DelegatingServletOutputStream;
//import com.fallt.util.SessionUtils;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import jakarta.servlet.ServletConfig;
//import jakarta.servlet.ServletContext;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.servlet.http.HttpSession;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class HabitServletTest {
//
//    @InjectMocks
//    private HabitServlet habitServlet;
//
//    @Mock
//    private HabitService habitService;
//
//
//    @Mock
//    private ServletConfig servletConfig;
//
//    @Mock
//    private ServletContext servletContext;
//
//    @Mock
//    private AuthenticationContext authenticationContext;
//
//    @Mock
//    private ValidationService validationService;
//
//    @Mock
//    private HttpServletRequest req;
//
//    @Mock
//    private HttpServletResponse resp;
//
//    @Mock
//    private HttpSession session;
//
//    private ObjectMapper objectMapper = new ObjectMapper();
//
//    @BeforeEach
//    void setup() throws ServletException {
//        habitServlet.init(servletConfig);
//        when(servletContext.getAttribute("habitService")).thenReturn(habitService);
//        when(servletContext.getAttribute("objectMapper")).thenReturn(objectMapper);
//        when(servletContext.getAttribute("authContext")).thenReturn(authenticationContext);
//        when(habitServlet.getServletContext()).thenReturn(servletContext);
//    }
//
//    @Test
//    @DisplayName("Получение списка привычек")
//    void whenGetListHabits_thenReturnOk() throws Exception {
//        String currentEmail = "email";
//        when(req.getSession()).thenReturn(session);
//        when(SessionUtils.getCurrentUserEmail(req)).thenReturn(currentEmail);
//        when(resp.getOutputStream()).thenReturn(new DelegatingServletOutputStream());
//
//        habitServlet.doGet(req, resp);
//
//        verify(resp).setStatus(HttpServletResponse.SC_OK);
//        verify(habitService, times(1)).getAllHabits(currentEmail);
//    }
//
//    @Test
//    @DisplayName("Попытка получения списка привычек неаутентифицированным пользователем")
//    void whenAnonymousUserGetListHabits_thenReturnUnauthorized() throws Exception {
//        String currentEmail = "email";
//        when(req.getSession()).thenReturn(session);
//        when(SessionUtils.getCurrentUserEmail(req)).thenReturn(currentEmail);
//        when(resp.getOutputStream()).thenReturn(new DelegatingServletOutputStream());
//        doThrow(new SecurityException("Для выполнения данного действия вам необходимо аутентифицироваться")).when(authenticationContext).checkAuthentication(currentEmail);
//
//        habitServlet.doGet(req, resp);
//
//        verify(resp).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//    }
//
//    @Test
//    @DisplayName("Создание привычки")
//    void whenCreateHabit_thenReturnCreated() throws Exception {
//        String currentEmail = "email";
//        UpsertHabitRequest request = createRequest();
//        when(servletContext.getAttribute("validationService")).thenReturn(validationService);
//        when(req.getSession()).thenReturn(session);
//        when(SessionUtils.getCurrentUserEmail(req)).thenReturn(currentEmail);
//        when(req.getInputStream()).thenReturn(new DelegatingServletInputStream(objectMapper.writeValueAsBytes(request)));
//        when(resp.getOutputStream()).thenReturn(new DelegatingServletOutputStream());
//        when(validationService.checkUpsertHabitRequest(request)).thenReturn(true);
//
//        habitServlet.doPost(req, resp);
//
//        verify(resp).setStatus(HttpServletResponse.SC_CREATED);
//        verify(habitService, times(1)).saveHabit(currentEmail, request);
//    }
//
//    @Test
//    @DisplayName("Попытка создания привычки с некорректными данными в запросе")
//    void whenCreateHabitWithIncorrectRequest_thenReturnBadRequest() throws Exception {
//        String currentEmail = "email";
//        UpsertHabitRequest request = createRequest();
//        when(servletContext.getAttribute("validationService")).thenReturn(validationService);
//        when(req.getSession()).thenReturn(session);
//        when(SessionUtils.getCurrentUserEmail(req)).thenReturn(currentEmail);
//        when(req.getInputStream()).thenReturn(new DelegatingServletInputStream(objectMapper.writeValueAsBytes(request)));
//        when(resp.getOutputStream()).thenReturn(new DelegatingServletOutputStream());
//        when(validationService.checkUpsertHabitRequest(request)).thenThrow(ValidationException.class);
//
//        habitServlet.doPost(req, resp);
//
//        verify(resp).setStatus(HttpServletResponse.SC_BAD_REQUEST);
//    }
//
//    @Test
//    @DisplayName("Попытка создания привычки неаутентифицированным пользователем")
//    void whenAnonymousUserCreateHabit_thenReturnUnauthorized() throws Exception {
//        String currentEmail = "email";
//        UpsertHabitRequest request = createRequest();
//        when(servletContext.getAttribute("validationService")).thenReturn(validationService);
//        when(req.getSession()).thenReturn(session);
//        when(SessionUtils.getCurrentUserEmail(req)).thenReturn(currentEmail);
//        when(req.getInputStream()).thenReturn(new DelegatingServletInputStream(objectMapper.writeValueAsBytes(request)));
//        when(resp.getOutputStream()).thenReturn(new DelegatingServletOutputStream());
//        doThrow(new SecurityException("Для выполнения данного действия вам необходимо аутентифицироваться")).when(authenticationContext).checkAuthentication(currentEmail);
//
//        habitServlet.doPost(req, resp);
//
//        verify(resp).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//    }
//
//    @Test
//    @DisplayName("Обновление данных о привычке")
//    void whenUpdateHabit_thenReturnOk() throws Exception {
//        String currentEmail = "email";
//        String existedTitle = req.getParameter("title");
//        UpsertHabitRequest request = createRequest();
//        when(req.getSession()).thenReturn(session);
//        when(SessionUtils.getCurrentUserEmail(req)).thenReturn(currentEmail);
//        when(req.getInputStream()).thenReturn(new DelegatingServletInputStream(objectMapper.writeValueAsBytes(request)));
//        when(resp.getOutputStream()).thenReturn(new DelegatingServletOutputStream());
//
//        habitServlet.doPut(req, resp);
//
//        verify(resp).setStatus(HttpServletResponse.SC_OK);
//        verify(habitService, times(1)).updateHabit(currentEmail, existedTitle, request);
//    }
//
//    @Test
//    @DisplayName("Попытка обновления привычки с использованием существующего названия")
//    void whenUpdateHabitUsingExistsTitle_thenReturnBadRequest() throws Exception {
//        String currentEmail = "email";
//        String existedTitle = req.getParameter("title");
//        UpsertHabitRequest request = createRequest();
//        when(req.getSession()).thenReturn(session);
//        when(SessionUtils.getCurrentUserEmail(req)).thenReturn(currentEmail);
//        when(req.getInputStream()).thenReturn(new DelegatingServletInputStream(objectMapper.writeValueAsBytes(request)));
//        when(resp.getOutputStream()).thenReturn(new DelegatingServletOutputStream());
//        when(habitService.updateHabit(currentEmail, existedTitle, request)).thenThrow(AlreadyExistException.class);
//
//        habitServlet.doPut(req, resp);
//
//        verify(resp).setStatus(HttpServletResponse.SC_BAD_REQUEST);
//    }
//
//    @Test
//    @DisplayName("Попытка обновления привычки неаутентифицированным пользователем")
//    void whenAnonymousUserUpdateHabit_thenReturnUnauthorized() throws Exception {
//        String currentEmail = "email";
//        UpsertHabitRequest request = createRequest();
//        when(req.getSession()).thenReturn(session);
//        when(SessionUtils.getCurrentUserEmail(req)).thenReturn(currentEmail);
//        when(req.getInputStream()).thenReturn(new DelegatingServletInputStream(objectMapper.writeValueAsBytes(request)));
//        when(resp.getOutputStream()).thenReturn(new DelegatingServletOutputStream());
//        doThrow(new SecurityException("Для выполнения данного действия вам необходимо аутентифицироваться")).when(authenticationContext).checkAuthentication(currentEmail);
//
//        habitServlet.doPut(req, resp);
//
//        verify(resp).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//    }
//
//    @Test
//    @DisplayName("Удаление привычки")
//    void whenDeleteHabit_thenReturnNoContent() throws Exception {
//        String currentEmail = "email";
//        when(req.getSession()).thenReturn(session);
//        when(SessionUtils.getCurrentUserEmail(req)).thenReturn(currentEmail);
//
//        habitServlet.doDelete(req, resp);
//
//        verify(resp).setStatus(HttpServletResponse.SC_NO_CONTENT);
//    }
//
//    @Test
//    @DisplayName("Попытка удаления привычки неаутентифицированным пользователем")
//    void whenAnonymousUserDeleteHabit_thenReturnUnauthorized() throws Exception {
//        String currentEmail = "email";
//        when(req.getSession()).thenReturn(session);
//        when(SessionUtils.getCurrentUserEmail(req)).thenReturn(currentEmail);
//        when(resp.getOutputStream()).thenReturn(new DelegatingServletOutputStream());
//        doThrow(new SecurityException("Для выполнения данного действия вам необходимо аутентифицироваться")).when(authenticationContext).checkAuthentication(currentEmail);
//
//        habitServlet.doDelete(req, resp);
//
//        verify(resp).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//    }
//
//
//    private UpsertHabitRequest createRequest() {
//        return UpsertHabitRequest.builder()
//                .title("title")
//                .text("text")
//                .rate("WEEKLY")
//                .build();
//    }
//}
