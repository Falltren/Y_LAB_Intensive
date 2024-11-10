package com.fallt;

import com.fallt.domain.dto.request.HabitConfirmRequest;
import com.fallt.domain.dto.request.LoginRequest;
import com.fallt.domain.dto.request.ReportRequest;
import com.fallt.domain.dto.request.UpsertHabitRequest;
import com.fallt.domain.dto.request.UpsertUserRequest;
import com.fallt.domain.entity.enums.Role;

import java.time.LocalDate;

public class TestConstant {

    public static final Long ADMIN_ID = 100L;
    public static final Long FIRST_USER_ID = 101L;
    public static final Long SECOND_USER_ID = 102L;
    public static final Long WEEKLY_HABIT_ID = 100L;
    public static final Long MONTHLY_HABIT_ID = 101L;
    public static final Long DAILY_HABIT_ID = 102L;
    public static final Long NOT_EXIST_ID = 1000L;
    public static final String ROLE_USER = Role.ROLE_USER.name();
    public static final String ROLE_ADMIN = Role.ROLE_ADMIN.name();
    public static final String ADMIN_NAME = "admin";
    public static final String FIRST_USER_NAME = "user1";
    public static final String SECOND_USER_NAME = "user2";
    public static final String ADMIN_EMAIL = "admin@admin.com";
    public static final String FIRST_USER_EMAIL = "email1@user.com";
    public static final String SECOND_USER_EMAIL = "email2@user.com";
    public static final String FIRST_USER_PASSWORD = "user1Pass";
    public static final String SECOND_USER_PASSWORD = "user2Pass";
    public static final String FIRST_HABIT_TITLE = "title1";
    public static final String SECOND_HABIT_TITLE = "title2";
    public static final String THIRD_HABIT_TITLE = "title3";
    public static final String NEW_HABIT_TITLE = "newHabit";
    public static final String HABIT_TEXT = "text";
    public static final String DAILY_HABIT = "DAILY";
    public static final String WEEKLY_HABIT = "WEEKLY";
    public static final LocalDate START_PERIOD = LocalDate.of(2024, 10, 1);
    public static final LocalDate END_PERIOD = LocalDate.of(2024, 10, 25);
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final UpsertUserRequest USER_REQUEST = UpsertUserRequest.builder()
            .name(FIRST_USER_NAME)
            .email(FIRST_USER_EMAIL)
            .password(FIRST_USER_PASSWORD)
            .build();
    public static final UpsertHabitRequest NEW_HABIT_REQUEST = UpsertHabitRequest.builder()
            .title(NEW_HABIT_TITLE)
            .text(HABIT_TEXT)
            .rate(WEEKLY_HABIT)
            .build();
    public static final UpsertHabitRequest EXIST_HABIT_REQUEST = UpsertHabitRequest.builder()
            .title(FIRST_HABIT_TITLE)
            .text(HABIT_TEXT)
            .rate(DAILY_HABIT)
            .build();
    public static final HabitConfirmRequest CONFIRM_REQUEST = HabitConfirmRequest.builder()
            .habitId(WEEKLY_HABIT_ID)
            .date(LocalDate.now())
            .build();
    public static final ReportRequest REPORT_REQUEST = ReportRequest.builder()
            .habitId(100L)
            .start(LocalDate.of(2024, 10, 1))
            .end(LocalDate.of(2024, 10, 25))
            .build();
    public static final LoginRequest LOGIN_REQUEST = LoginRequest.builder()
            .email(FIRST_USER_EMAIL)
            .password(FIRST_USER_PASSWORD)
            .build();
    public static final String USER_CONTROLLER_PATH = "/api/v1/users";
    public static final String USER_BY_ID = "/api/v1/users/{id}";
    public static final String USER_BLOCK_PATH = "/api/v1/users/block/{id}";
    public static final String CREATE_HABIT_PATH = "/api/v1/habits/create";
    public static final String HABIT_BY_ID_PATH = "/api/v1/habits/{id}";
    public static final String HABIT_CONFIRM_PATH = "/api/v1/habits/confirm";
    public static final String HABIT_CONTROLLER_PATH = "/api/v1/habits";
    public static final String REGISTER_PATH = "/api/v1/account/register";
    public static final String LOGIN_PATH = "/api/v1/account/login";
    public static final String FULL_REPORT_PATH = "/api/v1/reports/full";
    public static final String STREAK_REPORT_PATH = "/api/v1/reports/streak";

}
