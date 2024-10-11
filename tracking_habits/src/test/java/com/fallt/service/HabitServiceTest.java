package com.fallt.service;

import com.fallt.dto.HabitDto;
import com.fallt.entity.ExecutionRate;
import com.fallt.entity.Habit;
import com.fallt.entity.User;
import com.fallt.out.ConsoleOutput;
import com.fallt.util.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

class HabitServiceTest {

    private HabitService habitService;

    private ConsoleOutput consoleOutput;

    @BeforeEach
    void setup() {
        consoleOutput = Mockito.mock(ConsoleOutput.class);
        habitService = new HabitService(consoleOutput);
    }

    @Test
    @DisplayName("Успешное добавление привычки")
    void createHabit() {
        User user = createUser();
        HabitDto habitDto = createHabitDto();

        habitService.createHabit(user, habitDto);

        Habit habit = user.getHabits().get(0);
        assertThat(user.getHabits()).hasSize(1);
        assertThat(habit.getTitle()).isEqualTo(habitDto.getTitle());
        assertThat(habit.getText()).isEqualTo(habitDto.getText());
    }

    @Test
    @DisplayName("Попытка добавления привычки с дублирующимся названием")
    void createHabitWithDuplicateTitle(){
        User user = createUser();
        HabitDto habitDto = createHabitDto();

        habitService.createHabit(user, habitDto);
        habitService.createHabit(user, habitDto);

        assertThat(user.getHabits()).hasSize(1);
        verify(consoleOutput).printMessage(Message.HABIT_EXIST);
    }

    @Test
    @DisplayName("Получение привычки по названию")
    void testGetHabitByTitle(){

    }

    private User createUser() {
        return User.builder()
                .name("user")
                .email("user@user.user")
                .password("user")
                .build();
    }

    private HabitDto createHabitDto() {
        return HabitDto.builder()
                .title("habit")
                .text("text")
                .rate(ExecutionRate.WEEKLY)
                .build();
    }
}
