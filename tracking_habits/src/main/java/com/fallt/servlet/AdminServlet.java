package com.fallt.servlet;

import com.fallt.dto.response.UserResponse;
import com.fallt.entity.Role;
import com.fallt.exception.SecurityException;
import com.fallt.service.UserService;
import com.fallt.security.AuthenticationContext;
import com.fallt.util.SessionUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * Сервлет, предназначенный для администрирования пользователей (просмотр списка пользователей, блокировка пользователей)
 */
@WebServlet("/admin")
public class AdminServlet extends HttpServlet {

    private static final String OBJECT_MAPPER = "objectMapper";
    private static final String USER_SERVICE = "userService";
    private static final String AUTH_CONTEXT = "authContext";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletContext context = getServletContext();
        ObjectMapper objectMapper = (ObjectMapper) context.getAttribute(OBJECT_MAPPER);
        UserService userService = (UserService) context.getAttribute(USER_SERVICE);
        AuthenticationContext authenticationContext = (AuthenticationContext) context.getAttribute(AUTH_CONTEXT);
        String emailCurrentUser = SessionUtils.getCurrentUserEmail(req);
        try {
            authenticationContext.checkRole(emailCurrentUser, Role.ROLE_ADMIN);
            List<UserResponse> responseList = userService.getAllUsers();
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("application/json");
            byte[] bytes = objectMapper.writeValueAsBytes(responseList);
            resp.getOutputStream().write(bytes);
        } catch (SecurityException e) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            byte[] errorMessage = objectMapper.writeValueAsBytes(e.getMessage());
            resp.getOutputStream().write(errorMessage);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String deleteUserEmail = req.getParameter("email");
        String emailCurrentUser = SessionUtils.getCurrentUserEmail(req);
        ServletContext context = getServletContext();
        ObjectMapper objectMapper = (ObjectMapper) context.getAttribute(OBJECT_MAPPER);
        UserService userService = (UserService) context.getAttribute(USER_SERVICE);
        AuthenticationContext authenticationContext = (AuthenticationContext) context.getAttribute(AUTH_CONTEXT);
        try {
            authenticationContext.checkRole(emailCurrentUser, Role.ROLE_ADMIN);
            userService.blockingUser(deleteUserEmail);
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (SecurityException e) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            byte[] errorMessage = objectMapper.writeValueAsBytes(e.getMessage());
            resp.getOutputStream().write(errorMessage);
        }
    }
}
