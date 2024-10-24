package com.fallt.service;

import com.fallt.dto.request.LoginRequest;
import com.fallt.dto.request.UpsertUserRequest;
import com.fallt.exception.ValidationException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ValidationService {

    public boolean checkUpsertUserRequest(UpsertUserRequest request) {
        if (request.getName() == null || (request.getName().length() < 3 || request.getName().length() > 30)) {
            throw new ValidationException("Имя должно содержать от 3 до 30 символов");
        }
        if (request.getPassword() == null || request.getPassword().length() < 3 || request.getPassword().length() > 20) {
            throw new ValidationException("Пароль должен содержать от 3 до 20 символов");
        }
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new ValidationException("Значение электронной почты должно быть указано");
        }
        return true;
    }

    public boolean checkLoginRequest(LoginRequest request) {
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new ValidationException("Электронная почта должна быть указана");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new ValidationException("Пароль должен быть указан");
        }
        return true;
    }
}
