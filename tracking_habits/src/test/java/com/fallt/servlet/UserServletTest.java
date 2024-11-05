package com.fallt.servlet;

import com.fallt.dto.request.UpsertUserRequest;
import com.fallt.dto.response.UserResponse;
import com.fallt.exception.AlreadyExistException;
import com.fallt.exception.AuthenticationException;
import com.fallt.security.AuthenticationContext;
import com.fallt.service.impl.UserServiceImpl;
import com.fallt.util.Constant;
import com.fallt.util.DelegatingServletInputStream;
import com.fallt.util.DelegatingServletOutputStream;
import com.fallt.util.SessionUtils;
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

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServletTest {

    @InjectMocks
    private UserServlet userServlet;

    @Mock
    private UserServiceImpl userService;

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
        when(servletContext.getAttribute(Constant.USER_SERVICE)).thenReturn(userService);
        when(servletContext.getAttribute(Constant.OBJECT_MAPPER)).thenReturn(objectMapper);
        when(servletContext.getAttribute(Constant.AUTH_CONTEXT)).thenReturn(authenticationContext);
        when(userServlet.getServletContext()).thenReturn(servletContext);
        userServlet.init(servletConfig);
    }

    @Test
    @DisplayName("Удаление пользователя")
    void whenDeleteUser_thenReturnNoContent() throws Exception {
        String currentEmail = "email";
        when(req.getSession()).thenReturn(session);
        when(SessionUtils.getCurrentUserEmail(req)).thenReturn(currentEmail);

        userServlet.doDelete(req, resp);

        verify(resp).setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @Test
    @DisplayName("Попытка удаления пользователя неаутентифицированным пользователем")
    void whenDeleteUserAnonymousUser_thenReturnUnauthorized() throws Exception {
        String currentEmail = "email";
        when(req.getSession()).thenReturn(session);
        when(SessionUtils.getCurrentUserEmail(req)).thenReturn(currentEmail);
        doThrow(new AuthenticationException("У вас недостаточно прав для выполнения данного действия")).when(authenticationContext).checkAuthentication(currentEmail);
        when(resp.getOutputStream()).thenReturn(new DelegatingServletOutputStream());

        userServlet.doDelete(req, resp);

        verify(resp).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    @DisplayName("Обновление данных о пользователе")
    void whenUpdateUser_thenReturnOk() throws Exception {
        String currentEmail = "email";
        when(req.getSession()).thenReturn(session);
        when(SessionUtils.getCurrentUserEmail(req)).thenReturn(currentEmail);
        UpsertUserRequest request = createRequest();
        when(req.getInputStream()).thenReturn(new DelegatingServletInputStream(objectMapper.writeValueAsBytes(request)));
        when(resp.getOutputStream()).thenReturn(new DelegatingServletOutputStream());
        UserResponse response = new UserResponse("newUser", "email");
        when(userService.updateUser(currentEmail, request)).thenReturn(response);

        userServlet.doPut(req, resp);

        verify(resp).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    @DisplayName("Попытка обновления данных о пользователе с использованием существующего пароля")
    void whenUpdateUserWithExistsPassword_whenReturnBadRequest() throws Exception {
        String currentEmail = "email";
        when(req.getSession()).thenReturn(session);
        when(SessionUtils.getCurrentUserEmail(req)).thenReturn(currentEmail);
        UpsertUserRequest request = createRequest();
        when(req.getInputStream()).thenReturn(new DelegatingServletInputStream(objectMapper.writeValueAsBytes(request)));
        when(resp.getOutputStream()).thenReturn(new DelegatingServletOutputStream());
        when(userService.updateUser(currentEmail, request)).thenThrow(AlreadyExistException.class);

        userServlet.doPut(req, resp);

        verify(resp).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("Попытка обновления данных о пользователе неаутентифицированным пользователем")
    void whenAnonymousUserUpdate_thenReturnUnauthorized() throws Exception {
        String currentEmail = "email";
        when(req.getSession()).thenReturn(session);
        when(SessionUtils.getCurrentUserEmail(req)).thenReturn(currentEmail);
        doThrow(new AuthenticationException("У вас недостаточно прав для выполнения данного действия")).when(authenticationContext).checkAuthentication(currentEmail);
        when(resp.getOutputStream()).thenReturn(new DelegatingServletOutputStream());

        userServlet.doPut(req, resp);

        verify(resp).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    private UpsertUserRequest createRequest() {
        return UpsertUserRequest.builder()
                .name("newUser")
                .password("newPwd")
                .email("email")
                .build();
    }

}
