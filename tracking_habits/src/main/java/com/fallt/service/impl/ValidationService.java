package com.fallt.service.impl;

import com.fallt.domain.dto.request.HabitConfirmRequest;
import com.fallt.domain.dto.request.LoginRequest;
import com.fallt.domain.dto.request.ReportRequest;
import com.fallt.domain.dto.request.UpsertHabitRequest;
import com.fallt.domain.dto.request.UpsertUserRequest;
import com.fallt.domain.entity.enums.ExecutionRate;
import com.fallt.exception.ValidationException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class ValidationService {

    public boolean checkUpsertUserRequest(UpsertUserRequest request) {
        if (request.getName() == null || request.getName().length() < 3 || request.getName().length() > 30) {
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

    public boolean checkUpsertHabitRequest(UpsertHabitRequest request) {
        if (request.getTitle() == null || request.getTitle().length() < 3 || request.getTitle().length() > 30) {
            throw new ValidationException("Название привычки должно содержать от 3 до 30 символов");
        }
        if (request.getText() == null || request.getText().length() < 3 || request.getText().length() > 100) {
            throw new ValidationException("Описание привычки должно содержать от 3 до 100 символов");
        }
        if (request.getRate() == null) {
            throw new ValidationException("Частота выполнения привычки должна быть указана");
        }
        if (!getExistedExecutionValue().contains(request.getRate())) {
            throw new ValidationException("Указана некорректная частота. Возможные значения: " + getExistedExecutionValue());
        }
        return true;
    }

    public boolean checkHabitConfirmRequest(HabitConfirmRequest request) {
        if (request.getDate() == null) {
            throw new ValidationException("Дата выполнения привычки должны быть указана");
        }
        if (request.getTitle() == null || request.getTitle().length() < 3 || request.getTitle().length() > 30) {
            throw new ValidationException("Название привычки должно содержать от 3 до 30 символов");
        }
        return true;
    }

    public boolean checkReportRequest(ReportRequest request) {
        if (request.getTitle() == null || request.getTitle().length() < 3 || request.getTitle().length() > 30) {
            throw new ValidationException("Название привычки должно содержать от 3 до 30 символов");
        }
        if (request.getStart() == null || request.getEnd() == null) {
            throw new ValidationException("Дата начала и окончания отчетного периода должны быть указаны");
        }
        if (request.getStart().isAfter(request.getEnd())) {
            throw new ValidationException("Дата начала периода не может быть после даты окончания");
        }
        return true;
    }

    private List<String> getExistedExecutionValue() {
        return Arrays.stream(ExecutionRate.values()).map(Enum::name).toList();
    }
}
