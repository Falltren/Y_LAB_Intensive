package com.fallt.servlet;

import com.fallt.dto.request.HabitConfirmRequest;
import com.fallt.dto.response.HabitExecutionResponse;
import com.fallt.exception.AuthenticationException;
import com.fallt.exception.EntityNotFoundException;
import com.fallt.exception.ValidationException;
import com.fallt.security.AuthenticationContext;
import com.fallt.service.HabitService;
import com.fallt.service.impl.ValidationService;
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
 * Сервлет, предназначенный для отметки выполнения привычки
 */
@WebServlet("/habits/confirm")
public class ConfirmExecutionHabitServlet extends HttpServlet {

    private ObjectMapper objectMapper;
    private HabitService habitService;
    private ValidationService validationService;
    private AuthenticationContext authenticationContext;

    @Override
    public void init() throws ServletException {
        super.init();
        objectMapper = (ObjectMapper) getServletContext().getAttribute(OBJECT_MAPPER);
        habitService = (HabitService) getServletContext().getAttribute(HABIT_SERVICE);
        authenticationContext = (AuthenticationContext) getServletContext().getAttribute(AUTH_CONTEXT);
        validationService = (ValidationService) getServletContext().getAttribute(VALIDATION_SERVICE);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String emailCurrentUser = SessionUtils.getCurrentUserEmail(req);
        HabitConfirmRequest request = objectMapper.readValue(req.getInputStream(), HabitConfirmRequest.class);
        try {
            authenticationContext.checkAuthentication(emailCurrentUser);
            validationService.checkHabitConfirmRequest(request);
            HabitExecutionResponse response = habitService.confirmHabit(emailCurrentUser, request);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.setContentType(CONTENT_TYPE);
            byte[] bytes = objectMapper.writeValueAsBytes(response);
            resp.getOutputStream().write(bytes);
        } catch (EntityNotFoundException | ValidationException e) {
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
