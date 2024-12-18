package com.fallt.servlet;

import com.fallt.dto.request.LoginRequest;
import com.fallt.dto.response.UserResponse;
import com.fallt.exception.AuthenticationException;
import com.fallt.exception.EntityNotFoundException;
import com.fallt.exception.ValidationException;
import com.fallt.security.AuthenticationContext;
import com.fallt.service.impl.AuthServiceImpl;
import com.fallt.service.impl.ValidationService;
import com.fallt.util.Constant;
import com.fallt.util.DelegatingServletInputStream;
import com.fallt.util.DelegatingServletOutputStream;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServletTest {

    @InjectMocks
    private AuthServlet authServlet;

    @Mock
    private AuthServiceImpl authService;

    @Mock
    private ValidationService validationService;

    @Mock
    private ServletConfig servletConfig;

    @Mock
    private ServletContext servletContext;

    @Mock
    private AuthenticationContext authenticationContext;

    @Mock
    private HttpServletRequest req;

    @Mock
    private HttpServletResponse resp;

    @Mock
    private HttpSession session;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() throws ServletException {
        when(servletContext.getAttribute(Constant.AUTH_SERVICE)).thenReturn(authService);
        when(servletContext.getAttribute(Constant.VALIDATION_SERVICE)).thenReturn(validationService);
        when(servletContext.getAttribute(Constant.OBJECT_MAPPER)).thenReturn(objectMapper);
        when(servletContext.getAttribute(Constant.AUTH_CONTEXT)).thenReturn(authenticationContext);
        when(authServlet.getServletContext()).thenReturn(servletContext);
        authServlet.init(servletConfig);
    }

    @Test
    @DisplayName("Аутентификация пользователя с валидными данными")
    void whenAuthenticateUser_thenReturnOk() throws Exception {
        LoginRequest request = createRequest("emil", "pwd");
        UserResponse response = UserResponse.builder()
                .name("user")
                .email("email")
                .build();
        when(req.getSession()).thenReturn(session);
        when(validationService.checkLoginRequest(request)).thenReturn(true);
        when(req.getInputStream()).thenReturn(new DelegatingServletInputStream(objectMapper.writeValueAsBytes(request)));
        when(resp.getOutputStream()).thenReturn(new DelegatingServletOutputStream());
        when(authService.login(request, authenticationContext)).thenReturn(response);

        authServlet.doPost(req, resp);

        verify(resp).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    @DisplayName("Попытка аутентификации без указания пароля")
    void whenAuthenticateUserWithoutPassword_thenReturnBadRequest() throws Exception {
        LoginRequest request = createRequest("email", null);
        when(validationService.checkLoginRequest(request)).thenThrow(ValidationException.class);
        when(req.getInputStream()).thenReturn(new DelegatingServletInputStream(objectMapper.writeValueAsBytes(request)));
        when(resp.getOutputStream()).thenReturn(new DelegatingServletOutputStream());

        authServlet.doPost(req, resp);

        verify(resp).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("Попытка аутентификации с некорректной электронной почтой")
    void whenAuthenticateUserWithIncorrectEmail_thenReturnBadRequest() throws Exception {
        LoginRequest request = createRequest("incorrectEmail", "password");
        when(validationService.checkLoginRequest(request)).thenReturn(true);
        when(authService.login(request, authenticationContext)).thenThrow(EntityNotFoundException.class);
        when(req.getInputStream()).thenReturn(new DelegatingServletInputStream(objectMapper.writeValueAsBytes(request)));
        when(resp.getOutputStream()).thenReturn(new DelegatingServletOutputStream());

        authServlet.doPost(req, resp);

        verify(resp).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("Попытка аутентификации заблокированного пользователя")
    void whenAuthenticateBlockedUser_thenReturnUnauthorized() throws Exception {
        LoginRequest request = createRequest("email", "pwd");
        when(validationService.checkLoginRequest(request)).thenReturn(true);
        when(req.getInputStream()).thenReturn(new DelegatingServletInputStream(objectMapper.writeValueAsBytes(request)));
        when(resp.getOutputStream()).thenReturn(new DelegatingServletOutputStream());
        when(authService.login(request, authenticationContext)).thenThrow(AuthenticationException.class);

        authServlet.doPost(req, resp);

        verify(resp).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    private LoginRequest createRequest(String email, String password) {
        return LoginRequest.builder()
                .email(email)
                .password(password)
                .build();
    }
}
