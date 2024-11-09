package com.fallt.unit.service;

import com.fallt.domain.dto.request.HabitConfirmRequest;
import com.fallt.domain.dto.request.LoginRequest;
import com.fallt.domain.dto.request.ReportRequest;
import com.fallt.domain.dto.request.UpsertHabitRequest;
import com.fallt.domain.dto.request.UpsertUserRequest;
import com.fallt.exception.ValidationException;
import com.fallt.service.impl.ValidationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static com.fallt.TestConstant.DAILY_HABIT;
import static com.fallt.TestConstant.FIRST_HABIT_TITLE;
import static com.fallt.TestConstant.FIRST_USER_EMAIL;
import static com.fallt.TestConstant.FIRST_USER_NAME;
import static com.fallt.TestConstant.FIRST_USER_PASSWORD;
import static com.fallt.TestConstant.HABIT_TEXT;
import static com.fallt.TestConstant.SECOND_HABIT_TITLE;
import static com.fallt.TestConstant.SECOND_USER_EMAIL;
import static com.fallt.TestConstant.SECOND_USER_PASSWORD;
import static com.fallt.TestConstant.WEEKLY_HABIT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ValidationServiceTest {

    @InjectMocks
    private ValidationService validationService;

    @Test
    @DisplayName("Успешная проверка UpsertUserRequest")
    void testCheckUpsertUserRequest() {
        UpsertUserRequest request = new UpsertUserRequest(FIRST_USER_NAME, FIRST_USER_PASSWORD, FIRST_USER_EMAIL);

        boolean result = validationService.checkUpsertUserRequest(request);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Проверка UpsertUserRequest с некорректным именем")
    void testCheckUpsertUserWithIncorrectName() {
        UpsertUserRequest request = new UpsertUserRequest(null, FIRST_USER_PASSWORD, FIRST_USER_EMAIL);

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
        UpsertUserRequest request = new UpsertUserRequest(FIRST_USER_NAME, FIRST_USER_PASSWORD, null);

        assertThrows(ValidationException.class, () -> validationService.checkUpsertUserRequest(request));
    }

    @Test
    @DisplayName("Успешная проверка LoginRequest")
    void testCheckLoginRequest() {
        LoginRequest request = new LoginRequest(SECOND_USER_EMAIL, SECOND_USER_PASSWORD);

        boolean result = validationService.checkLoginRequest(request);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Проверка LoginRequest с некорректной электронной почтой")
    void testCheckLoginRequestWithIncorrectEmail() {
        LoginRequest request = new LoginRequest("", SECOND_USER_PASSWORD);

        assertThrows(ValidationException.class, () -> validationService.checkLoginRequest(request));
    }

    @Test
    @DisplayName("Проверка LoginRequest с некорректным паролем")
    void testCheckLoginRequestWithIncorrectPassword() {
        LoginRequest request = new LoginRequest(SECOND_USER_EMAIL, "");

        assertThrows(ValidationException.class, () -> validationService.checkLoginRequest(request));
    }

    @Test
    @DisplayName("Успешная проверка UpsertHabitRequest")
    void testCheckUpsertHabitRequest() {
        UpsertHabitRequest request = new UpsertHabitRequest(FIRST_HABIT_TITLE, HABIT_TEXT, DAILY_HABIT);

        boolean result = validationService.checkUpsertHabitRequest(request);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Проверка UpsertHabitRequest с некорректным названием")
    void testCheckUpsertHabitRequestWithIncorrectTitle() {
        UpsertHabitRequest request = new UpsertHabitRequest("t", HABIT_TEXT, WEEKLY_HABIT);

        assertThrows(ValidationException.class, () -> validationService.checkUpsertHabitRequest(request));
    }

    @Test
    @DisplayName("Проверка UpsertHabitRequest с некорректным описанием")
    void testCheckUpsertHabitRequestWithIncorrectText() {
        UpsertHabitRequest request = new UpsertHabitRequest(FIRST_HABIT_TITLE, "te", WEEKLY_HABIT);

        assertThrows(ValidationException.class, () -> validationService.checkUpsertHabitRequest(request));
    }

    @Test
    @DisplayName("Проверка UpsertHabitRequest с отсутствующей частотой выполнения привычки")
    void testCheckUpsertHabitRequestWithMissingRate() {
        UpsertHabitRequest request = new UpsertHabitRequest(FIRST_HABIT_TITLE, HABIT_TEXT, null);

        assertThrows(ValidationException.class, () -> validationService.checkUpsertHabitRequest(request));
    }

    @Test
    @DisplayName("Проверка UpsertHabitRequest с некорректной частотой выполнения привычки")
    void testCheckUpsertHabitRequestWithIncorrectRate() {
        UpsertHabitRequest request = new UpsertHabitRequest(SECOND_HABIT_TITLE, HABIT_TEXT, "YEARLY");

        assertThrows(ValidationException.class, () -> validationService.checkUpsertHabitRequest(request));
    }

    @Test
    @DisplayName("Успешная проверка HabitConfirmRequest")
    void testCheckHabitConfirmRequest() {
        HabitConfirmRequest request = new HabitConfirmRequest(1L, LocalDate.now());

        boolean result = validationService.checkHabitConfirmRequest(request);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Проверка HabitConfirmRequest с отсутствием даты выполнения")
    void testCheckHabitConfirmRequestWithIncorrectDate() {
        HabitConfirmRequest request = new HabitConfirmRequest(1L, null);

        assertThrows(ValidationException.class, () -> validationService.checkHabitConfirmRequest(request));
    }

    @Test
    @DisplayName("Успешная проверка ReportRequest")
    void testCheckReportRequest() {
        ReportRequest request = new ReportRequest(2L,
                LocalDate.of(2024, 10, 1),
                LocalDate.of(2024, 10, 10));

        boolean result = validationService.checkReportRequest(request);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Проверка ReportRequest с отсутствующей датой начала периода")
    void testCheckReportRequestWithMissingStartDate() {
        ReportRequest request = new ReportRequest(1L,
                null,
                LocalDate.of(2024, 10, 10));

        assertThrows(ValidationException.class, () -> validationService.checkReportRequest(request));
    }

    @Test
    @DisplayName("Проверка ReportRequest с датой окончания предшествующей дате начала периода")
    void testCheckReportRequestWhenStartDateIsAfterEndDate() {
        ReportRequest request = new ReportRequest(1L,
                LocalDate.of(2024, 10, 10),
                LocalDate.of(2024, 10, 1));

        assertThrows(ValidationException.class, () -> validationService.checkReportRequest(request));
    }
}
