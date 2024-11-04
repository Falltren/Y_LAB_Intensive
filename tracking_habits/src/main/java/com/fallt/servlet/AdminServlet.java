package com.fallt.servlet;

import com.fallt.dto.response.UserResponse;
import com.fallt.entity.Role;
import com.fallt.exception.AuthenticationException;
import com.fallt.exception.AuthorizationException;
import com.fallt.security.AuthenticationContext;
import com.fallt.service.UserService;
import com.fallt.util.SessionUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

import static com.fallt.util.Constant.*;

/**
 * Сервлет, предназначенный для администрирования пользователей (просмотр списка пользователей, блокировка пользователей)
 */
@WebServlet("/admin")
public class AdminServlet extends HttpServlet {

    private ObjectMapper objectMapper;
    private UserService userService;
    private AuthenticationContext authenticationContext;

    @Override
    public void init() throws ServletException {
        super.init();
        objectMapper = (ObjectMapper) getServletContext().getAttribute(OBJECT_MAPPER);
        userService = (UserService) getServletContext().getAttribute(USER_SERVICE);
        authenticationContext = (AuthenticationContext) getServletContext().getAttribute(AUTH_CONTEXT);

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String emailCurrentUser = SessionUtils.getCurrentUserEmail(req);
        try {
            authenticationContext.checkRole(emailCurrentUser, Role.ROLE_ADMIN);
            List<UserResponse> responseList = userService.getAllUsers();
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType(CONTENT_TYPE);
            byte[] bytes = objectMapper.writeValueAsBytes(responseList);
            resp.getOutputStream().write(bytes);
        } catch (AuthenticationException e) {
            handleErrorResponse(resp, HttpServletResponse.SC_UNAUTHORIZED, objectMapper, e.getMessage());
        } catch (AuthorizationException e) {
            handleErrorResponse(resp, HttpServletResponse.SC_FORBIDDEN, objectMapper, e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String deleteUserEmail = req.getParameter("email");
        String emailCurrentUser = SessionUtils.getCurrentUserEmail(req);
        try {
            authenticationContext.checkRole(emailCurrentUser, Role.ROLE_ADMIN);
            userService.blockingUser(deleteUserEmail);
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (AuthenticationException e) {
            handleErrorResponse(resp, HttpServletResponse.SC_UNAUTHORIZED, objectMapper, e.getMessage());
        } catch (AuthorizationException e) {
            handleErrorResponse(resp, HttpServletResponse.SC_FORBIDDEN, objectMapper, e.getMessage());
        }
    }

    private void handleErrorResponse(HttpServletResponse resp, int statusCode, ObjectMapper objectMapper, String message) throws IOException {
        resp.setStatus(statusCode);
        byte[] errorMessage = objectMapper.writeValueAsBytes(message);
        resp.getOutputStream().write(errorMessage);
    }
}
