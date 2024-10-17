package com.fallt.service;

import com.fallt.dto.HabitDto;
import com.fallt.entity.ExecutionRate;
import com.fallt.entity.Habit;
import com.fallt.entity.User;
import com.fallt.out.ConsoleOutput;
import com.fallt.repository.HabitDao;
import com.fallt.util.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

class HabitServiceTest {

    private HabitService habitService;

    private ConsoleOutput consoleOutput;

    private HabitDao habitDao;

    @BeforeEach
    void setup() {
        habitDao = Mockito.mock(HabitDao.class);
        consoleOutput = Mockito.mock(ConsoleOutput.class);
        habitService = new HabitService(consoleOutput, habitDao);
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
    void createHabitWithDuplicateTitle() {
        User user = createUser();
        HabitDto habitDto = createHabitDto();
        habitService.createHabit(user, habitDto);

        habitService.createHabit(user, habitDto);

        assertThat(user.getHabits()).hasSize(1);
        verify(consoleOutput).printMessage(Message.HABIT_EXIST);
    }

    @Test
    @DisplayName("Получение привычки по названию")
    void testGetHabitByTitle() {
        User user = createUser();
        HabitDto habitDto = createHabitDto();
        habitService.createHabit(user, habitDto);

        Habit existedHAbit = habitService.getHabitByTitle(user, habitDto.getTitle());

        assertThat(existedHAbit.getText()).isEqualTo("text");
        assertThat(existedHAbit.getTitle()).isEqualTo("habit");

    }

    @Test
    @DisplayName("Попытка получения привычки по отсутствующему названию")
    void testGetHabitByIncorrectTitle() {
        User user = createUser();
        HabitDto habitDto = createHabitDto();
        habitService.createHabit(user, habitDto);

        Habit existedHAbit = habitService.getHabitByTitle(user, "someTitle");

        assertThat(existedHAbit).isNull();
        verify(consoleOutput).printMessage(Message.INCORRECT_HABIT_TITLE);
    }

    @Test
    @DisplayName("Удаление привычки")
    void testDeleteHabit() {
        User user = createUser();
        HabitDto habitDto = createHabitDto();
        habitService.createHabit(user, habitDto);

        habitService.deleteHabit(user, habitDto.getTitle());

        assertThat(user.getHabits()).isEmpty();
    }

    @Test
    @DisplayName("Обновление данных о привычке")
    void testUpdateHabit() {
        User user = createUser();
        HabitDto habitDto = createHabitDto();
        HabitDto updateDto = HabitDto.builder().text("newText").title("newTitle").rate(ExecutionRate.MONTHLY).build();
        habitService.createHabit(user, habitDto);

        habitService.updateHabit(user, habitDto.getTitle(), updateDto);

        assertThat(user.getHabits()).hasSize(1);
        assertThat(user.getHabits().get(0).getTitle()).isEqualTo("newTitle");
        assertThat(user.getHabits().get(0).getText()).isEqualTo("newText");
        assertThat(user.getHabits().get(0).getExecutionRate()).isEqualTo(ExecutionRate.MONTHLY);
    }

    @Test
    @DisplayName("Попытка обновления привычки по некорректному названию")
    void testUpdateHabitByIncorrectTitle() {
        User user = createUser();
        HabitDto habitDto = createHabitDto();
        HabitDto updateDto = HabitDto.builder().text("newText").title("newTitle").build();
        habitService.createHabit(user, habitDto);

        habitService.updateHabit(user, "someTitle", updateDto);

        assertThat(user.getHabits()).hasSize(1);
        assertThat(user.getHabits().get(0).getText()).isEqualTo("text");
        assertThat(user.getHabits().get(0).getTitle()).isEqualTo("habit");
        verify(consoleOutput).printMessage(Message.INCORRECT_HABIT_TITLE);
    }

    @Test
    @DisplayName("Отметка выполнения привычки")
    void testConfirmHabit() {
        User user = createUser();
        HabitDto habitDto = createHabitDto();
        habitService.createHabit(user, habitDto);

        habitService.confirmHabit(user, habitDto.getTitle(), LocalDate.now());

        assertThat(user.getHabits().get(0).getSuccessfulExecution()).contains(LocalDate.now());
    }

    @Test
    @DisplayName("Получение всех привычек пользователя")
    void testGetAllHabits() {
        User user = createUser();
        HabitDto habitDto1 = createHabitDto();
        HabitDto habitDto2 = HabitDto.builder().title("title").text("text2").rate(ExecutionRate.DAILY).build();
        habitService.createHabit(user, habitDto1);
        habitService.createHabit(user, habitDto2);

        List<Habit> habits = habitService.getAllHabits(user);

        assertThat(habits).hasSize(2);
        assertThat(habits.get(0).getTitle()).isEqualTo("habit");
        assertThat(habits.get(1).getTitle()).isEqualTo("title");
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
