package com.fallt.service;

import com.fallt.dto.HabitProgress;
import com.fallt.entity.ExecutionRate;
import com.fallt.entity.Habit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class StatisticServiceTest {

    private StatisticService statisticService;

    @BeforeEach
    void setup() {
        statisticService = new StatisticService();
    }

    @ParameterizedTest
    @MethodSource("dailyExecutionRate")
    @DisplayName("Получение статистики по привычке c дневной частотой выполнения")
    void testGetHabitProgressWithDailyExecutionRate(List<LocalDate> successExecution, int expectedSuccessRate) {
        Habit habit = createHabit();
        habit.setSuccessfulExecution(new TreeSet<>(successExecution));

        HabitProgress progress = statisticService.getHabitProgress(habit,
                LocalDate.of(2024, 9, 11),
                LocalDate.of(2024, 9, 20));

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
        Habit habit = createHabit();
        habit.setExecutionRate(ExecutionRate.WEEKLY);

        habit.setSuccessfulExecution(new TreeSet<>(successExecution));

        HabitProgress progress = statisticService.getHabitProgress(habit,
                LocalDate.of(2024, 9, 11),
                LocalDate.of(2024, 10, 10));

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
        Habit habit = createHabit();
        habit.setExecutionRate(ExecutionRate.MONTHLY);
        habit.setSuccessfulExecution(new TreeSet<>(successExecution));

        HabitProgress progress = statisticService.getHabitProgress(habit,
                LocalDate.of(2024, 6, 11),
                LocalDate.of(2024, 10, 5));

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
        Habit habit = createHabit();
        List<LocalDate> successExecution = List.of(
                LocalDate.of(2024, 9, 12),
                LocalDate.of(2024, 9, 16),
                LocalDate.of(2024, 9, 19)
        );
        habit.setSuccessfulExecution(new TreeSet<>(successExecution));

        int rate = statisticService.getSuccessHabitRate(habit,
                LocalDate.of(2024, 6, 11),
                LocalDate.of(2024, 10, 5));

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

}
