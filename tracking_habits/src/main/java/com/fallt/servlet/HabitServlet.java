//package com.fallt.servlet;
//
//import com.fallt.dto.request.UpsertHabitRequest;
//import com.fallt.dto.response.HabitResponse;
//import com.fallt.exception.AlreadyExistException;
//import com.fallt.exception.EntityNotFoundException;
//import com.fallt.exception.SecurityException;
//import com.fallt.exception.ValidationException;
//import com.fallt.security.AuthenticationContext;
//import com.fallt.service.HabitService;
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
//import java.util.List;
//
///**
// * Сервлет, предназначенный для выполнения CRUD операций с привычками
// */
//@WebServlet("/habits")
//public class HabitServlet extends HttpServlet {
//
//    private static final String OBJECT_MAPPER = "objectMapper";
//
//    private static final String VALIDATION_SERVICE = "validationService";
//
//    private static final String AUTH_CONTEXT = "authContext";
//
//    private static final String HABIT_SERVICE = "habitService";
//
//    private static final String CONTENT_TYPE = "application/json";
//
//    @Override
//    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        ServletContext servletContext = getServletContext();
//        ObjectMapper objectMapper = (ObjectMapper) servletContext.getAttribute(OBJECT_MAPPER);
//        HabitService habitService = (HabitService) servletContext.getAttribute(HABIT_SERVICE);
//        AuthenticationContext authenticationContext = (AuthenticationContext) servletContext.getAttribute(AUTH_CONTEXT);
//        String emailCurrentUser = SessionUtils.getCurrentUserEmail(req);
//        try {
//            authenticationContext.checkAuthentication(emailCurrentUser);
//            List<HabitResponse> response = habitService.getAllHabits(emailCurrentUser);
//            resp.setStatus(HttpServletResponse.SC_OK);
//            resp.setContentType(CONTENT_TYPE);
//            byte[] bytes = objectMapper.writeValueAsBytes(response);
//            resp.getOutputStream().write(bytes);
//        } catch (SecurityException e) {
//            handleErrorResponse(resp, HttpServletResponse.SC_UNAUTHORIZED, objectMapper, e.getMessage());
//        }
//    }
//
//    @Override
//    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        ServletContext servletContext = getServletContext();
//        ObjectMapper objectMapper = (ObjectMapper) servletContext.getAttribute(OBJECT_MAPPER);
//        HabitService habitService = (HabitService) servletContext.getAttribute(HABIT_SERVICE);
//        ValidationService validationService = (ValidationService) servletContext.getAttribute(VALIDATION_SERVICE);
//        AuthenticationContext authenticationContext = (AuthenticationContext) servletContext.getAttribute(AUTH_CONTEXT);
//        UpsertHabitRequest request = objectMapper.readValue(req.getInputStream(), UpsertHabitRequest.class);
//        String emailCurrentUser = SessionUtils.getCurrentUserEmail(req);
//        try {
//            authenticationContext.checkAuthentication(emailCurrentUser);
//            validationService.checkUpsertHabitRequest(request);
//            HabitResponse response = habitService.saveHabit(emailCurrentUser, request);
//            resp.setStatus(HttpServletResponse.SC_CREATED);
//            resp.setContentType(CONTENT_TYPE);
//            byte[] bytes = objectMapper.writeValueAsBytes(response);
//            resp.getOutputStream().write(bytes);
//        } catch (ValidationException | AlreadyExistException e) {
//            handleErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, objectMapper, e.getMessage());
//        } catch (SecurityException e) {
//            handleErrorResponse(resp, HttpServletResponse.SC_UNAUTHORIZED, objectMapper, e.getMessage());
//        }
//    }
//
//    @Override
//    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        ServletContext servletContext = getServletContext();
//        ObjectMapper objectMapper = (ObjectMapper) servletContext.getAttribute(OBJECT_MAPPER);
//        HabitService habitService = (HabitService) servletContext.getAttribute(HABIT_SERVICE);
//        AuthenticationContext authenticationContext = (AuthenticationContext) servletContext.getAttribute(AUTH_CONTEXT);
//        String existedTitle = req.getParameter("title");
//        UpsertHabitRequest request = objectMapper.readValue(req.getInputStream(), UpsertHabitRequest.class);
//        String emailCurrentUser = SessionUtils.getCurrentUserEmail(req);
//        try {
//            authenticationContext.checkAuthentication(emailCurrentUser);
//            HabitResponse response = habitService.updateHabit(emailCurrentUser, existedTitle, request);
//            resp.setStatus(HttpServletResponse.SC_OK);
//            resp.setContentType(CONTENT_TYPE);
//            byte[] bytes = objectMapper.writeValueAsBytes(response);
//            resp.getOutputStream().write(bytes);
//        } catch (AlreadyExistException | EntityNotFoundException e) {
//            handleErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, objectMapper, e.getMessage());
//        } catch (SecurityException e) {
//            handleErrorResponse(resp, HttpServletResponse.SC_UNAUTHORIZED, objectMapper, e.getMessage());
//        }
//    }
//
//    @Override
//    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        ServletContext servletContext = getServletContext();
//        ObjectMapper objectMapper = (ObjectMapper) servletContext.getAttribute(OBJECT_MAPPER);
//        HabitService habitService = (HabitService) servletContext.getAttribute(HABIT_SERVICE);
//        AuthenticationContext authenticationContext = (AuthenticationContext) servletContext.getAttribute(AUTH_CONTEXT);
//        String emailCurrentUser = SessionUtils.getCurrentUserEmail(req);
//        String habitTitle = req.getParameter("title");
//        try {
//            authenticationContext.checkAuthentication(emailCurrentUser);
//            habitService.deleteHabit(emailCurrentUser, habitTitle);
//            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
//        } catch (SecurityException e) {
//            handleErrorResponse(resp, HttpServletResponse.SC_UNAUTHORIZED, objectMapper, e.getMessage());
//        }
//    }
//
//    private void handleErrorResponse(HttpServletResponse resp, int statusCode, ObjectMapper objectMapper, String message) throws IOException {
//        resp.setStatus(statusCode);
//        byte[] errorMessage = objectMapper.writeValueAsBytes(message);
//        resp.getOutputStream().write(errorMessage);
//    }
//}
