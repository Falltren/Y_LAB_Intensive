//package com.fallt.servlet;
//
//import com.fallt.dto.request.ReportRequest;
//import com.fallt.dto.response.HabitProgress;
//import com.fallt.exception.EntityNotFoundException;
//import com.fallt.exception.SecurityException;
//import com.fallt.exception.ValidationException;
//import com.fallt.security.AuthenticationContext;
//import com.fallt.service.StatisticService;
//import com.fallt.service.ValidationService;
//import com.fallt.util.SessionUtils;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import jakarta.servlet.ServletContext;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.annotation.WebServlet;
//import jakarta.servlet.http.HttpServlet;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//
//import java.io.IOException;
//
///**
// * Сервлет, используемый для получения статистики по привычкам
// */
//@WebServlet("/reports/full")
//public class StatisticServlet extends HttpServlet {
//
//    private static final String OBJECT_MAPPER = "objectMapper";
//
//    private static final String STATISTIC_SERVICE = "statisticService";
//
//    private static final String VALIDATION_SERVICE = "validationService";
//
//    private static final String AUTH_CONTEXT = "authContext";
//
//    private static final String CONTENT_TYPE = "application/json";
//
//    @Override
//    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        ServletContext context = getServletContext();
//        ObjectMapper objectMapper = (ObjectMapper) context.getAttribute(OBJECT_MAPPER);
//        StatisticService statisticService = (StatisticService) context.getAttribute(STATISTIC_SERVICE);
//        AuthenticationContext authenticationContext = (AuthenticationContext) context.getAttribute(AUTH_CONTEXT);
//        ValidationService validationService = (ValidationService) context.getAttribute(VALIDATION_SERVICE);
//        String emailCurrentUser = SessionUtils.getCurrentUserEmail(req);
//        ReportRequest request = objectMapper.readValue(req.getInputStream(), ReportRequest.class);
//        try {
//            authenticationContext.checkAuthentication(emailCurrentUser);
//            validationService.checkReportRequest(request);
//            HabitProgress response = statisticService.getHabitProgress(emailCurrentUser, request);
//            resp.setStatus(HttpServletResponse.SC_OK);
//            resp.setContentType(CONTENT_TYPE);
//            byte[] bytes = objectMapper.writeValueAsBytes(response);
//            resp.getOutputStream().write(bytes);
//        } catch (SecurityException e) {
//            handleErrorResponse(resp, HttpServletResponse.SC_UNAUTHORIZED, objectMapper, e.getMessage());
//        } catch (ValidationException | EntityNotFoundException e) {
//            handleErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, objectMapper, e.getMessage());
//        }
//    }
//
//    private void handleErrorResponse(HttpServletResponse resp, int statusCode, ObjectMapper objectMapper, String message) throws IOException {
//        resp.setStatus(statusCode);
//        byte[] errorMessage = objectMapper.writeValueAsBytes(message);
//        resp.getOutputStream().write(errorMessage);
//    }
//}
