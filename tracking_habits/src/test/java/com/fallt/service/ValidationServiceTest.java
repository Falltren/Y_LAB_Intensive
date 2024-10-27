package com.fallt.service;

import com.fallt.dto.request.*;
import com.fallt.exception.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ValidationServiceTest {

    @InjectMocks
    private ValidationService validationService;

    @Test
    @DisplayName("Успешная проверка UpsertUserRequest")
    void testCheckUpsertUserRequest() {
        UpsertUserRequest request = new UpsertUserRequest("user", "pwd", "email");

        boolean result = validationService.checkUpsertUserRequest(request);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Проверка UpsertUserRequest с некорректным именем")
    void testCheckUpsertUserWithIncorrectName() {
        UpsertUserRequest request = new UpsertUserRequest(null, "pwd", "email");

        assertThrows(ValidationException.class, () -> validationService.checkUpsertUserRequest(request));
    }

    @Test
    @DisplayName("Проверка UpsertUserRequest с некорректным паролем")
    void testCheckUpsertUserWithIncorrectPassword() {
        UpsertUserRequest request = new UpsertUserRequest("user", null, "email");

        assertThrows(ValidationException.class, () -> validationService.checkUpsertUserRequest(request));
    }

    @Test
    @DisplayName("Проверка UpsertUserRequest с некорректной электронной почтой")
    void testCheckUpsertUserWithIncorrectEmail() {
        UpsertUserRequest request = new UpsertUserRequest("user", "pwd", null);

        assertThrows(ValidationException.class, () -> validationService.checkUpsertUserRequest(request));
    }

    @Test
    @DisplayName("Успешная проверка LoginRequest")
    void testCheckLoginRequest() {
        LoginRequest request = new LoginRequest("email", "pwd");

        boolean result = validationService.checkLoginRequest(request);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Проверка LoginRequest с некорректной электронной почтой")
    void testCheckLoginRequestWithIncorrectEmail() {
        LoginRequest request = new LoginRequest("", "pwd");

        assertThrows(ValidationException.class, () -> validationService.checkLoginRequest(request));
    }

    @Test
    @DisplayName("Проверка LoginRequest с некорректным паролем")
    void testCheckLoginRequestWithIncorrectPassword() {
        LoginRequest request = new LoginRequest("email", "");

        assertThrows(ValidationException.class, () -> validationService.checkLoginRequest(request));
    }

    @Test
    @DisplayName("Успешная проверка UpsertHabitRequest")
    void testCheckUpsertHabitRequest() {
        UpsertHabitRequest request = new UpsertHabitRequest("title", "text", "DAILY");

        boolean result = validationService.checkUpsertHabitRequest(request);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Проверка UpsertHabitRequest с некорректным названием")
    void testCheckUpsertHabitRequestWithIncorrectTitle() {
        UpsertHabitRequest request = new UpsertHabitRequest("t", "text", "WEEKLY");

        assertThrows(ValidationException.class, () -> validationService.checkUpsertHabitRequest(request));
    }

    @Test
    @DisplayName("Проверка UpsertHabitRequest с некорректным описанием")
    void testCheckUpsertHabitRequestWithIncorrectText() {
        UpsertHabitRequest request = new UpsertHabitRequest("title", "te", "WEEKLY");

        assertThrows(ValidationException.class, () -> validationService.checkUpsertHabitRequest(request));
    }

    @Test
    @DisplayName("Проверка UpsertHabitRequest с отсутствующей частотой выполнения привычки")
    void testCheckUpsertHabitRequestWithMissingRate() {
        UpsertHabitRequest request = new UpsertHabitRequest("title", "te", null);

        assertThrows(ValidationException.class, () -> validationService.checkUpsertHabitRequest(request));
    }

    @Test
    @DisplayName("Проверка UpsertHabitRequest с некорректной частотой выполнения привычки")
    void testCheckUpsertHabitRequestWithIncorrectRate() {
        UpsertHabitRequest request = new UpsertHabitRequest("title", "text", "YEARLY");

        assertThrows(ValidationException.class, () -> validationService.checkUpsertHabitRequest(request));
    }

    @Test
    @DisplayName("Успешная проверка HabitConfirmRequest")
    void testCheckHabitConfirmRequest() {
        HabitConfirmRequest request = new HabitConfirmRequest("title", LocalDate.now());

        boolean result = validationService.checkHabitConfirmRequest(request);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Проверка HabitConfirmRequest с некорректным названием привычки")
    void testCheckHabitConfirmRequestWithIncorrectTitle() {
        HabitConfirmRequest request = new HabitConfirmRequest("t", LocalDate.now());

        assertThrows(ValidationException.class, () -> validationService.checkHabitConfirmRequest(request));
    }

    @Test
    @DisplayName("Проверка HabitConfirmRequest с отсутствием даты выполнения")
    void testCheckHabitConfirmRequestWithIncorrectDate() {
        HabitConfirmRequest request = new HabitConfirmRequest("title", null);

        assertThrows(ValidationException.class, () -> validationService.checkHabitConfirmRequest(request));
    }

    @Test
    @DisplayName("Успешная проверка ReportRequest")
    void testCheckReportRequest() {
        ReportRequest request = new ReportRequest("title",
                LocalDate.of(2024, 10, 1),
                LocalDate.of(2024, 10, 10));

        boolean result = validationService.checkReportRequest(request);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Проверка ReportRequest с некорректным название привычки")
    void testCheckReportRequestWithIncorrectTitle() {
        ReportRequest request = new ReportRequest("",
                LocalDate.of(2024, 10, 1),
                LocalDate.of(2024, 10, 10));

        assertThrows(ValidationException.class, () -> validationService.checkReportRequest(request));
    }

    @Test
    @DisplayName("Проверка ReportRequest с отсутствующей датой начала периода")
    void testCheckReportRequestWithMissingStartDate(){
        ReportRequest request = new ReportRequest("",
                null,
                LocalDate.of(2024, 10, 10));

        assertThrows(ValidationException.class, () -> validationService.checkReportRequest(request));
    }

    @Test
    @DisplayName("Проверка ReportRequest с датой окончания предшествующей дате начала периода")
    void testCheckReportRequestWhenStartDateIsAfterEndDate(){
        ReportRequest request = new ReportRequest("",
                LocalDate.of(2024, 10, 10),
                LocalDate.of(2024, 10, 1));

        assertThrows(ValidationException.class, () -> validationService.checkReportRequest(request));
    }
}
