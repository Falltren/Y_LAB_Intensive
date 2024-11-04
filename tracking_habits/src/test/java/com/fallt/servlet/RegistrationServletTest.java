package com.fallt.servlet;

import com.fallt.dto.request.UpsertUserRequest;
import com.fallt.exception.AlreadyExistException;
import com.fallt.exception.ValidationException;
import com.fallt.service.UserService;
import com.fallt.service.ValidationService;
import com.fallt.util.Constant;
import com.fallt.util.DelegatingServletInputStream;
import com.fallt.util.DelegatingServletOutputStream;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrationServletTest {

    @InjectMocks
    private RegistrationServlet registrationServlet;

    @Mock
    private ServletConfig servletConfig;

    @Mock
    private ServletContext servletContext;

    @Mock
    private UserService userService;

    @Mock
    private ValidationService validationService;

    @Mock
    private HttpServletRequest req;

    @Mock
    private HttpServletResponse resp;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() throws ServletException {
        when(servletContext.getAttribute(Constant.USER_SERVICE)).thenReturn(userService);
        when(servletContext.getAttribute(Constant.VALIDATION_SERVICE)).thenReturn(validationService);
        when(servletContext.getAttribute(Constant.OBJECT_MAPPER)).thenReturn(objectMapper);
        when(registrationServlet.getServletContext()).thenReturn(servletContext);
        registrationServlet.init(servletConfig);
    }

    @Test
    @DisplayName("Успешная регистрация пользователя")
    void whenUserRegister_thenReturnCreated() throws Exception {
        UpsertUserRequest request = createRequest();
        when(validationService.checkUpsertUserRequest(request)).thenReturn(true);
        when(req.getInputStream()).thenReturn(new DelegatingServletInputStream(objectMapper.writeValueAsBytes(request)));
        when(resp.getOutputStream()).thenReturn(new DelegatingServletOutputStream());

        registrationServlet.doPost(req, resp);

        verify(resp).setStatus(HttpServletResponse.SC_CREATED);
        verify(userService).saveUser((request));
    }

    @Test
    @DisplayName("Ввод невалидных данных при регистрации")
    void whenUserRegisterWithoutEmail_thenReturnBadRequest() throws Exception {
        UpsertUserRequest request = createRequest();
        when(validationService.checkUpsertUserRequest(request)).thenThrow(ValidationException.class);
        when(req.getInputStream()).thenReturn(new DelegatingServletInputStream(objectMapper.writeValueAsBytes(request)));
        when(resp.getOutputStream()).thenReturn(new DelegatingServletOutputStream());

        registrationServlet.doPost(req, resp);

        verify(resp).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(userService, never()).saveUser(request);
    }

    @Test
    @DisplayName("Попытка регистрации с существующим в базе данных email")
    void whenUserRegisterWithExistEmail_thenReturnAlreadyExistException() throws Exception {
        UpsertUserRequest request = createRequest();
        when(validationService.checkUpsertUserRequest(request)).thenReturn(true);
        when(req.getInputStream()).thenReturn(new DelegatingServletInputStream(objectMapper.writeValueAsBytes(request)));
        when(resp.getOutputStream()).thenReturn(new DelegatingServletOutputStream());
        when(userService.saveUser(request)).thenThrow(AlreadyExistException.class);

        registrationServlet.doPost(req, resp);

        verify(resp).setStatus(HttpServletResponse.SC_BAD_REQUEST);

    }

    private UpsertUserRequest createRequest() {
        return UpsertUserRequest.builder()
                .name("newUser")
                .password("newPwd")
                .email("email")
                .build();
    }
}
