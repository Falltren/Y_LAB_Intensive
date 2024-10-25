package com.fallt.servlet;

import com.fallt.dto.request.HabitConfirmRequest;
import com.fallt.dto.response.HabitExecutionResponse;
import com.fallt.exception.EntityNotFoundException;
import com.fallt.exception.SecurityException;
import com.fallt.service.HabitService;
import com.fallt.service.ValidationService;
import com.fallt.util.AuthenticationContext;
import com.fallt.util.SessionUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/habits/confirm")
public class ConfirmExecutionHabitServlet extends HttpServlet {

    private static final String OBJECT_MAPPER = "objectMapper";

    private static final String VALIDATION_SERVICE = "validationService";

    private static final String AUTH_CONTEXT = "authContext";

    private static final String HABIT_SERVICE = "habitService";

    private static final String CONTENT_TYPE = "application/json";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletContext servletContext = getServletContext();
        ObjectMapper objectMapper = (ObjectMapper) servletContext.getAttribute(OBJECT_MAPPER);
        HabitService habitService = (HabitService) servletContext.getAttribute(HABIT_SERVICE);
        ValidationService validationService = (ValidationService) servletContext.getAttribute(VALIDATION_SERVICE);
        AuthenticationContext authenticationContext = (AuthenticationContext) servletContext.getAttribute(AUTH_CONTEXT);
        String emailCurrentUser = SessionUtils.getCurrentUserEmail(req);
        HabitConfirmRequest request = objectMapper.readValue(req.getInputStream(), HabitConfirmRequest.class);
        try {
            authenticationContext.checkAuthentication(emailCurrentUser);
            HabitExecutionResponse response = habitService.confirmHabit(emailCurrentUser, request);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.setContentType(CONTENT_TYPE);
            byte[] bytes = objectMapper.writeValueAsBytes(response);
            resp.getOutputStream().write(bytes);
        } catch (EntityNotFoundException e) {
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
