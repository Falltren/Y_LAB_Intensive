package com.fallt.servlet;

import com.fallt.dto.request.LoginRequest;
import com.fallt.dto.response.UserResponse;
import com.fallt.exception.AuthenticationException;
import com.fallt.exception.EntityNotFoundException;
import com.fallt.exception.ValidationException;
import com.fallt.security.AuthenticationContext;
import com.fallt.service.AuthService;
import com.fallt.service.impl.ValidationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import static com.fallt.util.Constant.*;

/**
 * Сервлет, предназначенный для аутентификации пользователя
 */
@WebServlet("/users/auth")
public class AuthServlet extends HttpServlet {

    private ObjectMapper objectMapper;
    private AuthService authService;
    private AuthenticationContext authenticationContext;
    private ValidationService validationService;

    @Override
    public void init() throws ServletException {
        super.init();
        objectMapper = (ObjectMapper) getServletContext().getAttribute(OBJECT_MAPPER);
        authService = (AuthService) getServletContext().getAttribute(AUTH_SERVICE);
        authenticationContext = (AuthenticationContext) getServletContext().getAttribute(AUTH_CONTEXT);
        validationService = (ValidationService) getServletContext().getAttribute(VALIDATION_SERVICE);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LoginRequest request = objectMapper.readValue(req.getInputStream(), LoginRequest.class);
        try {
            if (validationService.checkLoginRequest(request)) {
                UserResponse response = authService.login(request, authenticationContext);
                req.getSession().setAttribute("user", response.getEmail());
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.setContentType(CONTENT_TYPE);
                byte[] bytes = objectMapper.writeValueAsBytes(response);
                resp.getOutputStream().write(bytes);
            }
        } catch (ValidationException | EntityNotFoundException e) {
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
