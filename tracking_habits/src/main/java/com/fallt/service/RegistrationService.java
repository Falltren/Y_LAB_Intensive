package com.fallt.service;

import com.fallt.dto.UserDto;
import com.fallt.out.ConsoleOutput;
import com.fallt.util.Message;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RegistrationService {

    private final UserService userService;

    private final ConsoleOutput consoleOutput;

    public boolean register(String name, String password, String email) {
        if (name.isBlank() || password.isBlank() || email.isBlank()) {
            consoleOutput.printMessage(Message.INCORRECT_INPUT);
            return false;
        }
        UserDto userDto = new UserDto(name, password, email);
        return userService.createUser(userDto) != null;
    }
}
