package com.fallt.service;

import com.fallt.out.ConsoleOutput;
import com.fallt.util.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class RegisterServiceTest {

    private RegistrationService registrationService;

    private UserService userService;

    private ConsoleOutput consoleOutput;

    @BeforeEach
    void setup(){
        userService = Mockito.mock(UserService.class);
        consoleOutput = Mockito.mock(ConsoleOutput.class);
        registrationService = new RegistrationService(userService, consoleOutput);
    }

    @Test
    @DisplayName("Регистрация пользователя")
    void testRegister(){
        registrationService.register("name", "password", "email");

        verify(userService, times(1)).createUser(any());
    }

    @Test
    @DisplayName("Попытка зарегистрировать пользователя, без указания email")
    void testRegistrationWithEmptyName(){
        registrationService.register("name", "password", "");

        verify(userService, times(0)).createUser(any());
        verify(consoleOutput).printMessage(Message.INCORRECT_INPUT);
    }
}
