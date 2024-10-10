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
        if (userService.isExistsEmail(email)) {
            consoleOutput.printMessage(Message.EMAIL_EXIST);
            return false;
        }
        if (userService.isExistsPassword(password)) {
            consoleOutput.printMessage(Message.PASSWORD_EXIST);
            return false;
        }
        UserDto userDto = new UserDto(name, password, email);
        userService.createUser(userDto);
        return true;
    }
}
