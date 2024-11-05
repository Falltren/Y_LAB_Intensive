package com.fallt.servlet;

import com.fallt.dto.request.UpsertUserRequest;
import com.fallt.dto.response.UserResponse;
import com.fallt.exception.AlreadyExistException;
import com.fallt.exception.ValidationException;
import com.fallt.service.UserService;
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
 * Сервлет, используемый для регистрации новых пользователей
 */
@WebServlet("/register")
public class RegistrationServlet extends HttpServlet {

    private ObjectMapper objectMapper;
    private UserService userService;
    private ValidationService validationService;

    @Override
    public void init() throws ServletException {
        super.init();
        objectMapper = (ObjectMapper) getServletContext().getAttribute(OBJECT_MAPPER);
        userService = (UserService) getServletContext().getAttribute(USER_SERVICE);
        validationService = (ValidationService) getServletContext().getAttribute(VALIDATION_SERVICE);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UpsertUserRequest request = objectMapper.readValue(req.getInputStream(), UpsertUserRequest.class);
        try {
            if (validationService.checkUpsertUserRequest(request)) {
                UserResponse response = userService.saveUser(request);
                resp.setStatus(HttpServletResponse.SC_CREATED);
                resp.setContentType(CONTENT_TYPE);
                byte[] bytes = objectMapper.writeValueAsBytes(response);
                resp.getOutputStream().write(bytes);
            }
        } catch (AlreadyExistException | ValidationException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            byte[] errorMessage = objectMapper.writeValueAsBytes(e.getMessage());
            resp.getOutputStream().write(errorMessage);
        }
    }
}

