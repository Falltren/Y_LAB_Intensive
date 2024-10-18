package com.fallt.service;

import com.fallt.dto.HabitDto;
import com.fallt.entity.ExecutionRate;
import com.fallt.entity.Habit;
import com.fallt.entity.HabitExecution;
import com.fallt.entity.User;
import com.fallt.out.ConsoleOutput;
import com.fallt.repository.HabitDao;
import com.fallt.repository.HabitExecutionDao;
import com.fallt.repository.impl.HabitDaoImpl;
import com.fallt.util.Fetch;
import com.fallt.util.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class HabitServiceTest {

    private HabitService habitService;

    private ConsoleOutput consoleOutput;

    private HabitDao habitDao;

    private HabitExecutionDao executionDao;

    @BeforeEach
    void setup() {
        executionDao = Mockito.mock(HabitExecutionDao.class);
        habitDao = Mockito.mock(HabitDaoImpl.class);
        consoleOutput = Mockito.mock(ConsoleOutput.class);
        habitService = new HabitService(consoleOutput, habitDao, executionDao);
    }

    @Test
    @DisplayName("Успешное добавление привычки")
    void createHabit() {
        User user = createUser();
        HabitDto habitDto = createHabitDto();
        when(habitDao.findHabitByTitleAndUserId(user.getId(), habitDto.getTitle())).thenReturn(Optional.empty());

        habitService.createHabit(user, habitDto);

        verify(habitDao, times(1)).save(any(Habit.class));
    }

    @Test
    @DisplayName("Попытка добавления привычки с дублирующимся названием")
    void createHabitWithDuplicateTitle() {
        User user = createUser();
        HabitDto habitDto = createHabitDto();
        when(habitDao.findHabitByTitleAndUserId(user.getId(), habitDto.getTitle())).thenReturn(Optional.of(new Habit()));

        habitService.createHabit(user, habitDto);

        verify(consoleOutput).printMessage(Message.HABIT_EXIST);
        verify(habitDao, times(0)).save(any(Habit.class));
    }

    @Test
    @DisplayName("Получение привычки по названию")
    void testGetHabitByTitle() {
        String title = "title";
        User user = createUser();
        Habit habit = createHabit(title);
        when(habitDao.findHabitByTitleAndUserId(user.getId(), title)).thenReturn(Optional.of(habit));

        Habit existedHabit = habitService.getHabitByTitle(user, title);

        assertThat(existedHabit.getTitle()).isEqualTo(title);
    }

    @Test
    @DisplayName("Попытка получения привычки по отсутствующему названию")
    void testGetHabitByIncorrectTitle() {
        String title = "title";
        User user = createUser();
        when(habitDao.findHabitByTitleAndUserId(user.getId(), title)).thenReturn(Optional.empty());

        Habit existedHAbit = habitService.getHabitByTitle(user, title);

        assertThat(existedHAbit).isNull();
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
        String newTitle = "new title";
        User user = createUser();
        Habit habit = createHabit("old title");
        HabitDto habitDto = HabitDto.builder().title(newTitle).build();
        when(habitDao.findHabitByTitleAndUserId(user.getId(), habit.getTitle())).thenReturn(Optional.of(habit));

        habitService.updateHabit(user, habit.getTitle(), habitDto);

        verify(habitDao, times(1)).update(habit);
    }

    @Test
    @DisplayName("Попытка обновления привычки по некорректному названию")
    void testUpdateHabitByIncorrectTitle() {
        String newTitle = "new title";
        User user = createUser();
        Habit habit = createHabit("old title");
        HabitDto habitDto = HabitDto.builder().title(newTitle).build();
        when(habitDao.findHabitByTitleAndUserId(user.getId(), habit.getTitle())).thenReturn(Optional.empty());

        habitService.updateHabit(user, habit.getTitle(), habitDto);

        verify(habitDao, times(0)).update(habit);
        verify(consoleOutput).printMessage(Message.INCORRECT_HABIT_TITLE);
    }

    @Test
    @DisplayName("Отметка выполнения привычки")
    void testConfirmHabit() {
        User user = createUser();
        Habit habit = createHabit("habit");
        when(habitDao.findHabitByTitleAndUserId(user.getId(), habit.getTitle())).thenReturn(Optional.of(habit));

        habitService.confirmHabit(user, habit.getTitle(), LocalDate.now());

        verify(executionDao, times(1)).save(any(HabitExecution.class));
    }

    @Test
    @DisplayName("Получение всех привычек пользователя")
    void testGetAllHabits() {
        User user = createUser();
        List<Habit> habits = List.of(
                createHabit("habit1"),
                createHabit("habit2")
        );
        when(habitDao.getAllUserHabits(user.getId(), Fetch.LAZY)).thenReturn(habits);

        List<Habit> expected = habitService.getAllHabits(user, Fetch.LAZY);

        assertThat(expected).hasSize(2);
        assertThat(habits).isEqualTo(expected);
    }

    private User createUser() {
        return User.builder()
                .id(1L)
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

    private Habit createHabit(String title) {
        return Habit.builder()
                .title(title)
                .text("text")
                .build();
    }
}
