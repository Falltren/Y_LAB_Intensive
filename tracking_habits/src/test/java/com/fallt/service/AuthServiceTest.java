package com.fallt.service;

import com.fallt.entity.User;
import com.fallt.out.ConsoleOutput;
import com.fallt.util.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private AuthService authService;

    private UserService userService;

    private ConsoleOutput consoleOutput;

    @BeforeEach
    void setup() {
        userService = Mockito.mock(UserService.class);
        consoleOutput = Mockito.mock(ConsoleOutput.class);
        authService = new AuthService(userService, consoleOutput);
    }

    @Test
    @DisplayName("Успешная аутентификация пользователя")
    void testLogin() {
        String email = "user@user.user";
        User user = createUser();
        when(userService.getUserByEmail(email)).thenReturn(user);

        User authenticatedUser = authService.login(email, "user");

        assertThat(authenticatedUser).isEqualTo(user);
        verify(consoleOutput, never()).printMessage(Message.UNAUTHENTICATED_USER);
        verify(consoleOutput, never()).printMessage(Message.BLOCKED_USER);
    }

    @Test
    @DisplayName("Попытка аутентификации с некорректным паролем")
    void testLoginWithInvalidPassword() {
        String email = "user@user.user";
        User user = createUser();
        when(userService.getUserByEmail(email)).thenReturn(user);

        User authenticatedUser = authService.login(email, "1user1");

        assertThat(authenticatedUser).isNull();
        verify(consoleOutput).printMessage(Message.UNAUTHENTICATED_USER);
    }

    @Test
    @DisplayName("Попытка аутентификации заблокированного пользователя")
    void testLoginBlockedUser(){
        String email = "user@user.user";
        User user = createUser();
        user.setBlocked(true);
        when(userService.getUserByEmail(email)).thenReturn(user);

        User authenticatedUser = authService.login(email, "user");

        assertThat(authenticatedUser).isNull();
        verify(consoleOutput).printMessage(Message.BLOCKED_USER);
    }

    private User createUser() {
        return User.builder()
                .name("user")
                .email("user@user.user")
                .password("user")
                .build();
    }

}
