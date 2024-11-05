package com.fallt.servlet;

import com.fallt.dto.response.UserResponse;
import com.fallt.entity.Role;
import com.fallt.exception.AuthenticationException;
import com.fallt.security.AuthenticationContext;
import com.fallt.service.impl.UserServiceImpl;
import com.fallt.util.Constant;
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

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServletTest {

    @InjectMocks
    private AdminServlet adminServlet;

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
        when(adminServlet.getServletContext()).thenReturn(servletContext);
        adminServlet.init(servletConfig);
    }

    @Test
    @DisplayName("Получение списка всех пользователей")
    void whenGet_thenReturnOk() throws Exception {
        when(req.getSession()).thenReturn(session);
        String currentEmail = "adminEmail";
        when(SessionUtils.getCurrentUserEmail(req)).thenReturn(currentEmail);
        List<UserResponse> responseList = List.of(
                new UserResponse("user1", "email1"),
                new UserResponse("user2", "email2")
        );
        doNothing().when(authenticationContext).checkRole(currentEmail, Role.ROLE_ADMIN);
        when(resp.getOutputStream()).thenReturn(new DelegatingServletOutputStream());
        when(userService.getAllUsers()).thenReturn(responseList);

        adminServlet.doGet(req, resp);

        verify(resp).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    @DisplayName("Попытка получения списка пользователей пользователем без наличия роли ADMIN")
    void whenGet_thenReturnSecurityException() throws Exception {
        when(req.getSession()).thenReturn(session);
        String currentEmail = "userEmail";
        when(SessionUtils.getCurrentUserEmail(req)).thenReturn(currentEmail);
        doThrow(new AuthenticationException("У вас недостаточно прав для выполнения данного действия")).when(authenticationContext).checkRole(currentEmail, Role.ROLE_ADMIN);
        when(resp.getOutputStream()).thenReturn(new DelegatingServletOutputStream());

        adminServlet.doGet(req, resp);

        verify(resp).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    @DisplayName("Блокировка пользователя")
    void whenBlockingUser_thenReturnOk() throws Exception {
        String blockingUser = "userEmail";
        String currentEmail = "adminEmail";
        when(req.getSession()).thenReturn(session);
        when(SessionUtils.getCurrentUserEmail(req)).thenReturn(currentEmail);
        when(req.getParameter("email")).thenReturn(blockingUser);
        doNothing().when(authenticationContext).checkRole(currentEmail, Role.ROLE_ADMIN);

        adminServlet.doPut(req, resp);

        verify(resp).setStatus(HttpServletResponse.SC_OK);
        verify(userService, times(1)).blockingUser(blockingUser);
    }

    @Test
    @DisplayName("Попытка блокировки пользователем без наличия роли ADMIN")
    void whenBlockingUserWithoutRoleAdmin_thenReturnSecurityException() throws Exception {
        String blockingUser = "userEmail";
        String currentEmail = "adminEmail";
        when(req.getSession()).thenReturn(session);
        when(SessionUtils.getCurrentUserEmail(req)).thenReturn(currentEmail);
        doThrow(new AuthenticationException("У вас недостаточно прав для выполнения данного действия")).when(authenticationContext).checkRole(currentEmail, Role.ROLE_ADMIN);
        when(resp.getOutputStream()).thenReturn(new DelegatingServletOutputStream());

        adminServlet.doPut(req, resp);

        verify(resp).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(userService, times(0)).blockingUser(blockingUser);
    }
}
