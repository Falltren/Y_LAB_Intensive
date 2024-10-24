package com.fallt.servlet;

import com.fallt.dto.request.UpsertHabitRequest;
import com.fallt.dto.response.HabitResponse;
import com.fallt.exception.AlreadyExistException;
import com.fallt.exception.SecurityException;
import com.fallt.exception.ValidationException;
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

@WebServlet("/habits")
public class HabitServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletContext servletContext = getServletContext();
        ObjectMapper objectMapper = (ObjectMapper) servletContext.getAttribute("objectMapper");
        HabitService habitService = (HabitService) servletContext.getAttribute("habitService");
        ValidationService validationService = (ValidationService) servletContext.getAttribute("validationService");
        AuthenticationContext authenticationContext = (AuthenticationContext) servletContext.getAttribute("authContext");
        UpsertHabitRequest request = objectMapper.readValue(req.getInputStream(), UpsertHabitRequest.class);
        String emailCurrentUser = SessionUtils.getCurrentUserEmail(req);
        try {
            authenticationContext.checkAuthentication(emailCurrentUser);
            validationService.checkUpsertHabitRequest(request);
            HabitResponse response = habitService.createHabit(emailCurrentUser, request);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.setContentType("application/json");
            byte[] bytes = objectMapper.writeValueAsBytes(response);
            resp.getOutputStream().write(bytes);
        } catch (ValidationException | AlreadyExistException e) {
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
