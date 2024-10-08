package com.fallt.service;

import com.fallt.dto.UserDto;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RegistrationService {

    private final UserService userService;

    public boolean register(String name, String password, String email) {
        if (userService.isExistsEmail(email)) {
            System.out.println("Указанный email уже используется");
            return false;
        }
        if (userService.isExistsPassword(password)) {
            System.out.println("Указанный пароль уже используется");
            return false;
        }
        UserDto userDto = new UserDto(name, password, email);
        userService.createUser(userDto);
        return true;
    }
}
