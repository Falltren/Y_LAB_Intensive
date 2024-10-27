package com.fallt.servlet;

import com.fallt.dto.request.LoginRequest;
import com.fallt.dto.response.UserResponse;
import com.fallt.exception.EntityNotFoundException;
import com.fallt.exception.SecurityException;
import com.fallt.exception.ValidationException;
import com.fallt.security.AuthenticationContext;
import com.fallt.service.AuthService;
import com.fallt.service.ValidationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/users/auth")
public class AuthServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletContext servletContext = getServletContext();
        ObjectMapper objectMapper = (ObjectMapper) servletContext.getAttribute("objectMapper");
        AuthService authService = (AuthService) servletContext.getAttribute("authService");
        ValidationService validationService = (ValidationService) servletContext.getAttribute("validationService");
        AuthenticationContext authenticationContext = (AuthenticationContext) servletContext.getAttribute("authContext");
        LoginRequest request = objectMapper.readValue(req.getInputStream(), LoginRequest.class);
        try {
            if (validationService.checkLoginRequest(request)) {
                UserResponse response = authService.login(request, authenticationContext);
                req.getSession().setAttribute("user", response.getEmail());
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.setContentType("application/json");
                byte[] bytes = objectMapper.writeValueAsBytes(response);
                resp.getOutputStream().write(bytes);
            }
        } catch (ValidationException | EntityNotFoundException e) {
            handleErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, objectMapper, e.getMessage());
        } catch (SecurityException e) {
            handleErrorResponse(resp, HttpServletResponse.SC_UNAUTHORIZED, objectMapper, e.getMessage());
        }
    }

    private void handleErrorResponse(HttpServletResponse resp, int statusCode, ObjectMapper objectMapper, String message) throws IOException {
        resp.setStatus(statusCode);
        byte[] errorMessage = objectMapper.writeValueAsBytes(message);
        resp.getOutputStream().write(errorMessage);
    }
}
