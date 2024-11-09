package com.fallt;

import com.fallt.domain.dto.request.HabitConfirmRequest;
import com.fallt.domain.dto.request.LoginRequest;
import com.fallt.domain.dto.request.ReportRequest;
import com.fallt.domain.dto.request.UpsertHabitRequest;
import com.fallt.domain.dto.request.UpsertUserRequest;
import com.fallt.domain.dto.response.HabitResponse;
import com.fallt.domain.dto.response.LoginResponse;
import com.fallt.domain.dto.response.UserResponse;
import com.fallt.domain.entity.Habit;
import com.fallt.domain.entity.User;
import com.fallt.domain.entity.enums.Role;

import java.time.LocalDate;
import java.util.ArrayList;

public class TestConstant {

    public static final String FIRST_USER_NAME = "user1";
    public static final String SECOND_USER_NAME = "user2";
    public static final String FIRST_USER_EMAIL = "email1";
    public static final String SECOND_USER_EMAIL = "email2";
    public static final String FIRST_USER_PASSWORD = "pwd1";
    public static final String SECOND_USER_PASSWORD = "pwd2";
    public static final String FIRST_HABIT_TITLE = "title1";
    public static final String SECOND_HABIT_TITLE = "title2";
    public static final String HABIT_TEXT = "text";
    public static final String DAILY_HABIT = "DAILY";
    public static final String WEEKLY_HABIT = "WEEKLY";
    public static final String SESSION_ID = "sessionId";
    public static final String USER_EMAIL = "email";
    public static final String TOKEN = "token";
    public static final UpsertUserRequest USER_REQUEST = UpsertUserRequest.builder()
            .name(FIRST_USER_NAME)
            .email(FIRST_USER_EMAIL)
            .password(FIRST_USER_PASSWORD)
            .build();
    public static final UserResponse USER_RESPONSE = UserResponse.builder()
            .name(FIRST_USER_NAME)
            .email(FIRST_USER_EMAIL)
            .build();
    public static final UpsertHabitRequest HABIT_REQUEST = UpsertHabitRequest.builder()
            .title(FIRST_HABIT_TITLE)
            .text(HABIT_TEXT)
            .rate(WEEKLY_HABIT)
            .build();
    public static final HabitResponse HABIT_RESPONSE = HabitResponse.builder()
            .title(FIRST_HABIT_TITLE)
            .text(HABIT_TEXT)
            .successfulExecution(new ArrayList<>())
            .build();
    public static final HabitConfirmRequest CONFIRM_REQUEST = HabitConfirmRequest.builder()
            .habitId(1L)
            .date(LocalDate.now())
            .build();
    public static final ReportRequest REPORT_REQUEST = ReportRequest.builder()
            .habitId(1L)
            .start(LocalDate.of(2024, 10, 1))
            .end(LocalDate.of(2024, 10, 20))
            .build();
    public static final LoginRequest LOGIN_REQUEST = LoginRequest.builder()
            .email(FIRST_USER_EMAIL)
            .password(FIRST_USER_PASSWORD)
            .build();
    public static final LoginResponse LOGIN_RESPONSE = LoginResponse.builder()
            .name(FIRST_USER_NAME)
            .token(TOKEN)
            .build();
    public static final User USER_FROM_DATABASE = User.builder()
            .id(1L)
            .name(FIRST_USER_NAME)
            .email(FIRST_USER_EMAIL)
            .password(FIRST_USER_PASSWORD)
            .role(Role.ROLE_USER)
            .build();
    public static final Habit HABIT_FROM_DATABASE = Habit.builder()
            .title(FIRST_HABIT_TITLE)
            .text(HABIT_TEXT)
            .build();
    public static final String USER_CONTROLLER_PATH = "/api/v1/users";
    public static final String USER_BLOCK_PATH = "/api/v1/users/block?email=";
    public static final String CREATE_HABIT_PATH = "/api/v1/habits/create";
    public static final String HABIT_BY_TITLE_PATH = "/api/v1/habits?title=";
    public static final String HABIT_CONFIRM_PATH = "/api/v1/habits/confirm";
    public static final String HABIT_CONTROLLER_PATH = "/api/v1/habits";
    public static final String REGISTER_PATH = "/api/v1/account/register";
    public static final String LOGIN_PATH = "/api/v1/account/login";
    public static final String FULL_REPORT_PATH = "/api/v1/reports/full";
    public static final String STREAK_REPORT_PATH = "/api/v1/reports/streak";

}
