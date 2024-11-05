package com.fallt.servlet;

import com.fallt.dto.request.ReportRequest;
import com.fallt.dto.response.HabitProgress;
import com.fallt.exception.AuthenticationException;
import com.fallt.exception.EntityNotFoundException;
import com.fallt.exception.ValidationException;
import com.fallt.security.AuthenticationContext;
import com.fallt.service.StatisticService;
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
 * Сервлет, используемый для получения статистики по привычкам
 */
@WebServlet("/reports/full")
public class StatisticServlet extends HttpServlet {

    private ObjectMapper objectMapper;
    private StatisticService statisticService;
    private AuthenticationContext authenticationContext;
    private ValidationService validationService;

    @Override
    public void init() throws ServletException {
        super.init();
        objectMapper = (ObjectMapper) getServletContext().getAttribute(OBJECT_MAPPER);
        statisticService = (StatisticService) getServletContext().getAttribute(STATISTIC_SERVICE);
        authenticationContext = (AuthenticationContext) getServletContext().getAttribute(AUTH_CONTEXT);
        validationService = (ValidationService) getServletContext().getAttribute(VALIDATION_SERVICE);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String emailCurrentUser = SessionUtils.getCurrentUserEmail(req);
        ReportRequest request = objectMapper.readValue(req.getInputStream(), ReportRequest.class);
        try {
            authenticationContext.checkAuthentication(emailCurrentUser);
            validationService.checkReportRequest(request);
            HabitProgress response = statisticService.getHabitProgress(emailCurrentUser, request);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType(CONTENT_TYPE);
            byte[] bytes = objectMapper.writeValueAsBytes(response);
            resp.getOutputStream().write(bytes);
        } catch (AuthenticationException e) {
            handleErrorResponse(resp, HttpServletResponse.SC_UNAUTHORIZED, objectMapper, e.getMessage());
        } catch (ValidationException | EntityNotFoundException e) {
            handleErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, objectMapper, e.getMessage());
        }
    }

    private void handleErrorResponse(HttpServletResponse resp, int statusCode, ObjectMapper objectMapper, String message) throws IOException {
        resp.setStatus(statusCode);
        byte[] errorMessage = objectMapper.writeValueAsBytes(message);
        resp.getOutputStream().write(errorMessage);
    }
}
