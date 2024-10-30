//package com.fallt.servlet;
//
//import com.fallt.dto.request.UpsertUserRequest;
//import com.fallt.dto.response.UserResponse;
//import com.fallt.exception.AlreadyExistException;
//import com.fallt.exception.SecurityException;
//import com.fallt.service.UserService;
//import com.fallt.security.AuthenticationContext;
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
// * Сервлет, используемый для удаления и обновления данных о пользователе
// */
//@WebServlet("/users")
//public class UserServlet extends HttpServlet {
//
//    private static final String OBJECT_MAPPER = "objectMapper";
//
//    private static final String USER_SERVICE = "userService";
//
//    private static final String AUTH_CONTEXT = "authContext";
//
//    @Override
//    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        ServletContext context = getServletContext();
//        ObjectMapper objectMapper = (ObjectMapper) context.getAttribute(OBJECT_MAPPER);
//        UserService userService = (UserService) context.getAttribute(USER_SERVICE);
//        AuthenticationContext authenticationContext = (AuthenticationContext) context.getAttribute(AUTH_CONTEXT);
//        String emailCurrentUser = SessionUtils.getCurrentUserEmail(req);
//        try {
//            authenticationContext.checkAuthentication(emailCurrentUser);
//            userService.deleteUser(emailCurrentUser);
//            authenticationContext.logout(emailCurrentUser);
//            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
//        } catch (SecurityException e) {
//            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            byte[] errorMessage = objectMapper.writeValueAsBytes(e.getMessage());
//            resp.getOutputStream().write(errorMessage);
//        }
//    }
//
//    @Override
//    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        ServletContext context = getServletContext();
//        ObjectMapper objectMapper = (ObjectMapper) context.getAttribute(OBJECT_MAPPER);
//        UserService userService = (UserService) context.getAttribute(USER_SERVICE);
//        AuthenticationContext authenticationContext = (AuthenticationContext) context.getAttribute(AUTH_CONTEXT);
//        try {
//            authenticationContext.checkAuthentication(SessionUtils.getCurrentUserEmail(req));
//            UpsertUserRequest request = objectMapper.readValue(req.getInputStream(), UpsertUserRequest.class);
//            UserResponse response = userService.updateUser(SessionUtils.getCurrentUserEmail(req), request);
//            resp.setStatus(HttpServletResponse.SC_OK);
//            resp.setContentType("application/json");
//            byte[] bytes = objectMapper.writeValueAsBytes(response);
//            resp.getOutputStream().write(bytes);
//        } catch (AlreadyExistException e) {
//            handleErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, objectMapper, e.getMessage());
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
//
//
//}
