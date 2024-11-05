package com.fallt.servlet;

import com.fallt.dto.request.ReportRequest;
import com.fallt.exception.AuthenticationException;
import com.fallt.exception.ValidationException;
import com.fallt.security.AuthenticationContext;
import com.fallt.service.impl.StatisticServiceImpl;
import com.fallt.service.impl.ValidationService;
import com.fallt.util.Constant;
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
class StatisticServletTest {

    @InjectMocks
    private StatisticServlet statisticServlet;

    @Mock
    private StatisticServiceImpl statisticService;

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
        when(servletContext.getAttribute(Constant.VALIDATION_SERVICE)).thenReturn(validationService);
        when(servletContext.getAttribute(Constant.STATISTIC_SERVICE)).thenReturn(statisticService);
        when(servletContext.getAttribute(Constant.OBJECT_MAPPER)).thenReturn(objectMapper);
        when(servletContext.getAttribute(Constant.AUTH_CONTEXT)).thenReturn(authenticationContext);
        when(statisticServlet.getServletContext()).thenReturn(servletContext);
        objectMapper.registerModule(new JavaTimeModule());
        statisticServlet.init(servletConfig);
    }

    @Test
    @DisplayName("Получение отчета")
    void whenGetReport_thenReturnOk() throws Exception {
        String currentEmail = "email";
        ReportRequest request = createRequest();
        when(req.getSession()).thenReturn(session);
        when(SessionUtils.getCurrentUserEmail(req)).thenReturn(currentEmail);
        when(req.getInputStream()).thenReturn(new DelegatingServletInputStream(objectMapper.writeValueAsBytes(request)));
        when(resp.getOutputStream()).thenReturn(new DelegatingServletOutputStream());
        when(validationService.checkReportRequest(request)).thenReturn(true);

        statisticServlet.doGet(req, resp);

        verify(resp).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    @DisplayName("Попытка получения отчета неаутентифицированным пользователем")
    void whenAnonymousUserGetReport_thenReturnUnauthorized() throws Exception {
        String currentEmail = "email";
        ReportRequest request = createRequest();
        when(req.getSession()).thenReturn(session);
        when(SessionUtils.getCurrentUserEmail(req)).thenReturn(currentEmail);
        when(req.getInputStream()).thenReturn(new DelegatingServletInputStream(objectMapper.writeValueAsBytes(request)));
        when(resp.getOutputStream()).thenReturn(new DelegatingServletOutputStream());
        doThrow(new AuthenticationException("Для выполнения данного действия вам необходимо аутентифицироваться")).when(authenticationContext).checkAuthentication(currentEmail);

        statisticServlet.doGet(req, resp);

        verify(resp).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    @DisplayName("Попытка получения отчета с некорректными данными в запросе")
    void whenGetReportWithIncorrectRequest_thenReturnBadRequest() throws Exception {
        String currentEmail = "email";
        ReportRequest request = createRequest();
        when(req.getSession()).thenReturn(session);
        when(SessionUtils.getCurrentUserEmail(req)).thenReturn(currentEmail);
        when(req.getInputStream()).thenReturn(new DelegatingServletInputStream(objectMapper.writeValueAsBytes(request)));
        when(resp.getOutputStream()).thenReturn(new DelegatingServletOutputStream());
        when(validationService.checkReportRequest(request)).thenThrow(ValidationException.class);

        statisticServlet.doGet(req, resp);

        verify(resp).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    private ReportRequest createRequest() {
        return ReportRequest.builder()
                .title("title")
                .start(LocalDate.of(2024, 10, 1))
                .end(LocalDate.of(2024, 10, 20))
                .build();
    }


}
