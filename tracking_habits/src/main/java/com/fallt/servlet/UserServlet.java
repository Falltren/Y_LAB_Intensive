package com.fallt.servlet;

import com.fallt.dto.request.UpsertUserRequest;
import com.fallt.dto.response.UserResponse;
import com.fallt.exception.AlreadyExistException;
import com.fallt.exception.AuthenticationException;
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

import static com.fallt.util.Constant.*;

/**
 * Сервлет, используемый для удаления и обновления данных о пользователе
 */
@WebServlet("/users")
public class UserServlet extends HttpServlet {

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
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String emailCurrentUser = SessionUtils.getCurrentUserEmail(req);
        try {
            authenticationContext.checkAuthentication(emailCurrentUser);
            userService.deleteUser(emailCurrentUser);
            authenticationContext.logout(emailCurrentUser);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (AuthenticationException e) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            byte[] errorMessage = objectMapper.writeValueAsBytes(e.getMessage());
            resp.getOutputStream().write(errorMessage);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            authenticationContext.checkAuthentication(SessionUtils.getCurrentUserEmail(req));
            UpsertUserRequest request = objectMapper.readValue(req.getInputStream(), UpsertUserRequest.class);
            UserResponse response = userService.updateUser(SessionUtils.getCurrentUserEmail(req), request);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType(CONTENT_TYPE);
            byte[] bytes = objectMapper.writeValueAsBytes(response);
            resp.getOutputStream().write(bytes);
        } catch (AlreadyExistException e) {
            handleErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, objectMapper, e.getMessage());
        } catch (AuthenticationException e) {
            handleErrorResponse(resp, HttpServletResponse.SC_UNAUTHORIZED, objectMapper, e.getMessage());
        }
    }

    private void handleErrorResponse(HttpServletResponse resp, int statusCode, ObjectMapper objectMapper, String message) throws IOException {
        resp.setStatus(statusCode);
        byte[] errorMessage = objectMapper.writeValueAsBytes(message);
        resp.getOutputStream().write(errorMessage);
    }


}
