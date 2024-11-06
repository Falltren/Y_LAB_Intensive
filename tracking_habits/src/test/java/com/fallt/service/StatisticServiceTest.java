package com.fallt.service;

import com.fallt.dto.request.ReportRequest;
import com.fallt.dto.response.HabitProgress;
import com.fallt.entity.ExecutionRate;
import com.fallt.entity.Habit;
import com.fallt.entity.Role;
import com.fallt.entity.User;
import com.fallt.service.impl.HabitServiceImpl;
import com.fallt.service.impl.StatisticServiceImpl;
import com.fallt.service.impl.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatisticServiceTest {

    @InjectMocks
    private StatisticServiceImpl statisticService;

    @Mock
    private HabitServiceImpl habitService;

    @Mock
    private UserServiceImpl userService;

    @ParameterizedTest
    @MethodSource("dailyExecutionRate")
    @DisplayName("Получение статистики по привычке c дневной частотой выполнения")
    void testGetHabitProgressWithDailyExecutionRate(List<LocalDate> successExecution, int expectedSuccessRate) {
        String title = "title";
        String email = "email";
        Habit habit = createHabit();
        User user = createUser();
        ReportRequest request = new ReportRequest("title",
                LocalDate.of(2024, 9, 11),
                LocalDate.of(2024, 9, 20));
        when(userService.getUserByEmail(email)).thenReturn(user);
        when(habitService.getHabitByTitle(user, title)).thenReturn(habit);

        habit.setSuccessfulExecution(new TreeSet<>(successExecution));

        HabitProgress progress = statisticService.getHabitProgress(email, request);

        assertThat(progress.getTitle()).isEqualTo("title");
        assertThat(progress.getSuccessRate()).isEqualTo(expectedSuccessRate);
    }

    private static Stream<Arguments> dailyExecutionRate() {
        return Stream.of(
                Arguments.of(List.of(LocalDate.of(2024, 9, 12)), 10),
                Arguments.of(List.of(LocalDate.of(2024, 9, 15), LocalDate.of(2024, 9, 17)), 20),
                Arguments.of(List.of(LocalDate.of(2024, 9, 13), LocalDate.of(2024, 9, 14), LocalDate.of(2024, 9, 19)), 30),
                Arguments.of(List.of(), 0),
                Arguments.of(List.of(LocalDate.of(2024, 9, 11), LocalDate.of(2024, 10, 20)), 10)
        );
    }

    @ParameterizedTest
    @MethodSource("weeklyExecutionRate")
    @DisplayName("Получение статистики по привычке с недельной частотой выполнения")
    void testGetHabitProgressWithWeeklyExecutionRate(List<LocalDate> successExecution, int expectedSuccessRate) {
        String title = "title";
        String email = "email";
        Habit habit = createHabit();
        habit.setExecutionRate(ExecutionRate.WEEKLY);
        habit.setSuccessfulExecution(new TreeSet<>(successExecution));
        User user = createUser();
        ReportRequest request = new ReportRequest("title",
                LocalDate.of(2024, 9, 11),
                LocalDate.of(2024, 10, 10));
        when(userService.getUserByEmail(email)).thenReturn(user);
        when(habitService.getHabitByTitle(user, title)).thenReturn(habit);

        HabitProgress progress = statisticService.getHabitProgress(email, request);

        assertThat(progress.getTitle()).isEqualTo("title");
        assertThat(progress.getSuccessRate()).isEqualTo(expectedSuccessRate);
    }

    private static Stream<Arguments> weeklyExecutionRate() {
        return Stream.of(
                Arguments.of(List.of(LocalDate.of(2024, 9, 12)), 20),
                Arguments.of(List.of(LocalDate.of(2024, 9, 15), LocalDate.of(2024, 9, 22)), 40),
                Arguments.of(List.of(LocalDate.of(2024, 9, 13), LocalDate.of(2024, 9, 22), LocalDate.of(2024, 10, 2)), 60),
                Arguments.of(List.of(), 0),
                Arguments.of(List.of(LocalDate.of(2024, 9, 30), LocalDate.of(2024, 10, 9)), 40)
        );
    }

    @ParameterizedTest
    @MethodSource("monthlyExecutionRate")
    @DisplayName("Получение статистики по привычке с месячной частотой выполнения")
    void testGetHabitProgressWithMonthlyExecutionRate(List<LocalDate> successExecution, int expectedSuccessRate) {
        String title = "title";
        String email = "email";
        Habit habit = createHabit();
        habit.setExecutionRate(ExecutionRate.MONTHLY);
        habit.setSuccessfulExecution(new TreeSet<>(successExecution));
        User user = createUser();
        ReportRequest request = new ReportRequest("title",
                LocalDate.of(2024, 6, 11),
                LocalDate.of(2024, 10, 5));
        when(userService.getUserByEmail(email)).thenReturn(user);
        when(habitService.getHabitByTitle(user, title)).thenReturn(habit);

        HabitProgress progress = statisticService.getHabitProgress(email, request);

        assertThat(progress.getTitle()).isEqualTo("title");
        assertThat(progress.getSuccessRate()).isEqualTo(expectedSuccessRate);
    }

    private static Stream<Arguments> monthlyExecutionRate() {
        return Stream.of(
                Arguments.of(List.of(LocalDate.of(2024, 6, 11)), 20),
                Arguments.of(List.of(LocalDate.of(2024, 9, 15), LocalDate.of(2024, 10, 3)), 40),
                Arguments.of(List.of(LocalDate.of(2024, 7, 13), LocalDate.of(2024, 8, 22), LocalDate.of(2024, 9, 2)), 60),
                Arguments.of(List.of(), 0),
                Arguments.of(List.of(LocalDate.of(2024, 5, 30), LocalDate.of(2024, 10, 1)), 20)
        );
    }

    @Test
    @DisplayName("Получение процента выполнения привычки")
    void testGetSuccessHabitRate() {
        String title = "title";
        String email = "email";
        User user = createUser();
        Habit habit = createHabit();
        List<LocalDate> successExecution = List.of(
                LocalDate.of(2024, 9, 12),
                LocalDate.of(2024, 9, 16),
                LocalDate.of(2024, 9, 19)
        );
        habit.setSuccessfulExecution(new TreeSet<>(successExecution));
        when(userService.getUserByEmail(email)).thenReturn(user);
        when(habitService.getHabitByTitle(user, title)).thenReturn(habit);
        ReportRequest request = new ReportRequest("title",
                LocalDate.of(2024, 6, 11),
                LocalDate.of(2024, 10, 5));

        int rate = statisticService.getSuccessHabitRate(email, request);

        assertThat(rate).isEqualTo(3);
    }

    private Habit createHabit() {
        Habit habit = new Habit();
        habit.setText("text");
        habit.setTitle("title");
        habit.setExecutionRate(ExecutionRate.DAILY);
        habit.setCreateAt(LocalDate.of(2024, 6, 9));
        return habit;
    }

    private User createUser() {
        return User.builder()
                .name("user")
                .password("pwd")
                .email("email")
                .createAt(LocalDateTime.now())
                .role(Role.ROLE_USER)
                .isBlocked(false)
                .build();
    }

}
