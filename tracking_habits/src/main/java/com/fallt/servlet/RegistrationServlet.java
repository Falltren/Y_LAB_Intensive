//package com.fallt.servlet;
//
//import com.fallt.dto.request.UpsertUserRequest;
//import com.fallt.dto.response.UserResponse;
//import com.fallt.exception.AlreadyExistException;
//import com.fallt.exception.ValidationException;
//import com.fallt.service.UserService;
//import com.fallt.service.ValidationService;
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
// * Сервлет, используемый для регистрации новых пользователей
// */
//@WebServlet("/register")
//public class RegistrationServlet extends HttpServlet {
//
//    @Override
//    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        ServletContext servletContext = getServletContext();
//        UserService userService = (UserService) servletContext.getAttribute("userService");
//        ValidationService validationService = (ValidationService) servletContext.getAttribute("validationService");
//        ObjectMapper objectMapper = (ObjectMapper) servletContext.getAttribute("objectMapper");
//        UpsertUserRequest request = objectMapper.readValue(req.getInputStream(), UpsertUserRequest.class);
//        try {
//            if (validationService.checkUpsertUserRequest(request)) {
//                UserResponse response = userService.saveUser(request);
//                resp.setStatus(HttpServletResponse.SC_CREATED);
//                resp.setContentType("application/json");
//                byte[] bytes = objectMapper.writeValueAsBytes(response);
//                resp.getOutputStream().write(bytes);
//            }
//        } catch (AlreadyExistException | ValidationException e) {
//            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            byte[] errorMessage = objectMapper.writeValueAsBytes(e.getMessage());
//            resp.getOutputStream().write(errorMessage);
//        }
//    }
//}
//
