package com.fallt.service;

import com.fallt.dto.request.LoginRequest;
import com.fallt.dto.response.UserResponse;
import com.fallt.entity.User;
import com.fallt.exception.EntityNotFoundException;
import com.fallt.exception.AuthenticationException;
import com.fallt.security.AuthenticationContext;
import com.fallt.service.impl.AuditServiceImpl;
import com.fallt.service.impl.AuthServiceImpl;
import com.fallt.service.impl.UserServiceImpl;
import com.fallt.util.InstanceCreator;
import jakarta.servlet.ServletContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.fallt.util.Constant.AUDIT_SERVICE;
import static com.fallt.util.Constant.AUTH_CONTEXT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    private MockedStatic<InstanceCreator> mockedStatic;

    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private UserServiceImpl userService;

    @Mock
    private AuthenticationContext authenticationContext;

    @Mock
    private AuditServiceImpl auditService;

    @Mock
    private ServletContext servletContext;

    @BeforeEach
    void setup(){
        mockedStatic = mockStatic(InstanceCreator.class);
        mockedStatic.when(InstanceCreator::getServletContext).thenReturn(servletContext);
        when(servletContext.getAttribute(AUTH_CONTEXT)).thenReturn(authenticationContext);
        when(servletContext.getAttribute(AUDIT_SERVICE)).thenReturn(auditService);
    }

    @AfterEach
    void tearDown(){
        mockedStatic.close();
    }

    @Test
    @DisplayName("Успешная аутентификация пользователя")
    void testLogin() {
        LoginRequest request = createRequest();
        User user = createUser();
        when(userService.getUserByEmail(user.getEmail())).thenReturn(user);

        UserResponse response = authService.login(request, authenticationContext);

        assertThat(response.getEmail()).isEqualTo(user.getEmail());
        assertThat(response.getName()).isEqualTo(user.getName());
    }

    @Test
    @DisplayName("Попытка аутентификации с некорректным паролем")
    void testLoginWithInvalidPassword() {
        LoginRequest request = createRequest();
        request.setPassword("incorrectPassword");
        User user = createUser();
        when(userService.getUserByEmail(user.getEmail())).thenReturn(user);

        assertThrows(EntityNotFoundException.class, () -> authService.login(request, authenticationContext));
    }

    @Test
    @DisplayName("Попытка аутентификации заблокированного пользователя")
    void testLoginBlockedUser() {
        LoginRequest request = createRequest();
        User user = createUser();
        user.setBlocked(true);
        when(userService.getUserByEmail(user.getEmail())).thenReturn(user);

        assertThrows(AuthenticationException.class, () -> authService.login(request, authenticationContext));
    }

    private LoginRequest createRequest() {
        return LoginRequest.builder()
                .email("email")
                .password("pwd")
                .build();
    }

    private User createUser() {
        return User.builder()
                .id(1L)
                .name("user")
                .email("email")
                .password("pwd")
                .build();
    }

}
