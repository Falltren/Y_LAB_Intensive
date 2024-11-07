package com.fallt.service;

import com.fallt.dto.response.UserResponse;
import com.fallt.entity.User;
import com.fallt.exception.AuthenticationException;
import com.fallt.exception.EntityNotFoundException;
import com.fallt.security.AuthenticationContext;
import com.fallt.security.PasswordEncoder;
import com.fallt.service.impl.AuthServiceImpl;
import com.fallt.service.impl.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.fallt.TestConstant.FIRST_USER_EMAIL;
import static com.fallt.TestConstant.FIRST_USER_PASSWORD;
import static com.fallt.TestConstant.LOGIN_REQUEST;
import static com.fallt.TestConstant.USER_FROM_DATABASE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private UserServiceImpl userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationContext authenticationContext;

    @Test
    @DisplayName("Успешная аутентификация пользователя")
    void testLogin() {
        when(userService.getUserByEmail(USER_FROM_DATABASE.getEmail())).thenReturn(USER_FROM_DATABASE);
        when(passwordEncoder.checkPassword(LOGIN_REQUEST.getPassword(), USER_FROM_DATABASE.getPassword())).thenReturn(true);

        UserResponse response = authService.login(LOGIN_REQUEST, USER_FROM_DATABASE.getEmail(), authenticationContext);

        assertThat(response.getEmail()).isEqualTo(USER_FROM_DATABASE.getEmail());
        assertThat(response.getName()).isEqualTo(USER_FROM_DATABASE.getName());
    }

    @Test
    @DisplayName("Попытка аутентификации с некорректным паролем")
    void testLoginWithInvalidPassword() {
        when(userService.getUserByEmail(USER_FROM_DATABASE.getEmail())).thenReturn(USER_FROM_DATABASE);
        when(passwordEncoder.checkPassword(LOGIN_REQUEST.getPassword(), USER_FROM_DATABASE.getPassword())).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> authService.login(LOGIN_REQUEST, USER_FROM_DATABASE.getEmail(), authenticationContext));
    }

    @Test
    @DisplayName("Попытка аутентификации заблокированного пользователя")
    void testLoginBlockedUser() {
        User user = new User();
        user.setPassword(FIRST_USER_PASSWORD);
        user.setEmail(FIRST_USER_EMAIL);
        user.setBlocked(true);

        when(userService.getUserByEmail(user.getEmail())).thenReturn(user);
        when(passwordEncoder.checkPassword(LOGIN_REQUEST.getPassword(), user.getPassword())).thenReturn(true);

        assertThrows(AuthenticationException.class, () -> authService.login(LOGIN_REQUEST, user.getEmail(), authenticationContext));
    }
}
