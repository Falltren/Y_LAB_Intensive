package com.fallt.service;

import com.fallt.dto.request.HabitConfirmRequest;
import com.fallt.dto.request.UpsertHabitRequest;
import com.fallt.dto.response.HabitExecutionResponse;
import com.fallt.dto.response.HabitResponse;
import com.fallt.entity.Habit;
import com.fallt.entity.HabitExecution;
import com.fallt.entity.User;
import com.fallt.exception.AlreadyExistException;
import com.fallt.exception.EntityNotFoundException;
import com.fallt.repository.HabitDao;
import com.fallt.repository.HabitExecutionDao;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.fallt.TestConstant.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HabitServiceTest {

    @InjectMocks
    private HabitService habitService;

    @Mock
    private HabitDao habitDao;

    @Mock
    private HabitExecutionDao executionDao;

    @Mock
    private UserService userService;

    @Test
    @DisplayName("Успешное добавление привычки")
    void createHabit() {
        User user = createUser();
        Habit habit = createHabit(FIRST_HABIT_TITLE);
        UpsertHabitRequest request = createHabitDto(FIRST_HABIT_TITLE);
        HabitResponse expected = new HabitResponse(request.getTitle(), request.getText(), new ArrayList<>());
        when(userService.getUserByEmail(user.getEmail())).thenReturn(user);
        when(habitDao.findHabitByTitleAndUserId(user.getId(), request.getTitle())).thenReturn(Optional.empty());
        when(habitDao.save(any(Habit.class))).thenReturn(habit);

        HabitResponse response = habitService.saveHabit(user.getEmail(), request);

        assertThat(response).isEqualTo(expected);
    }

    @Test
    @DisplayName("Попытка добавления привычки с дублирующимся названием")
    void createHabitWithDuplicateTitle() {
        User user = createUser();
        UpsertHabitRequest upsertHabitRequest = createHabitDto(FIRST_HABIT_TITLE);
        when(userService.getUserByEmail(user.getEmail())).thenReturn(user);
        when(habitDao.findHabitByTitleAndUserId(user.getId(), upsertHabitRequest.getTitle())).thenReturn(Optional.of(new Habit()));

        assertThrows(AlreadyExistException.class, () -> habitService.saveHabit(user.getEmail(), upsertHabitRequest));
    }

    @Test
    @DisplayName("Получение привычки по названию")
    void testGetHabitByTitle() {
        User user = createUser();
        Habit habit = createHabit(SECOND_HABIT_TITLE);
        when(habitDao.findHabitByTitleAndUserId(user.getId(), SECOND_HABIT_TITLE)).thenReturn(Optional.of(habit));

        Habit existedHabit = habitService.getHabitByTitle(user, SECOND_HABIT_TITLE);

        assertThat(existedHabit.getTitle()).isEqualTo(SECOND_HABIT_TITLE);
    }

    @Test
    @DisplayName("Попытка получения привычки по отсутствующему названию")
    void testGetHabitByIncorrectTitle() {
        User user = createUser();
        when(habitDao.findHabitByTitleAndUserId(user.getId(), SECOND_HABIT_TITLE)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> habitService.getHabitByTitle(user, SECOND_HABIT_TITLE));
    }

    @Test
    @DisplayName("Удаление привычки")
    void testDeleteHabit() {
        User user = createUser();
        when(userService.getUserByEmail(user.getEmail())).thenReturn(user);

        habitService.deleteHabit(user.getEmail(), SECOND_HABIT_TITLE);

        verify(habitDao, times(1)).delete(user.getId(), SECOND_HABIT_TITLE);
    }

    @Test
    @DisplayName("Обновление данных о привычке")
    void testUpdateHabit() {
        User user = createUser();
        Habit habit = createHabit(SECOND_HABIT_TITLE);
        UpsertHabitRequest request = createHabitDto(FIRST_HABIT_TITLE);
        HabitResponse expected = new HabitResponse(request.getTitle(), request.getText(), new ArrayList<>());
        when(userService.getUserByEmail(user.getEmail())).thenReturn(user);
        when(habitDao.findHabitByTitleAndUserId(user.getId(), habit.getTitle())).thenReturn(Optional.of(habit));
        when(habitDao.update(any(Habit.class))).thenReturn(habit);

        HabitResponse response = habitService.updateHabit(user.getEmail(), habit.getTitle(), request);

        verify(habitDao, times(1)).update(habit);
        assertThat(response).isEqualTo(expected);
    }

    @Test
    @DisplayName("Попытка обновления привычки по некорректному названию")
    void testUpdateHabitByIncorrectTitle() {
        User user = createUser();
        Habit habit = createHabit(FIRST_HABIT_TITLE);
        UpsertHabitRequest upsertHabitRequest = UpsertHabitRequest.builder().title(SECOND_HABIT_TITLE).build();
        when(userService.getUserByEmail(user.getEmail())).thenReturn(user);
        when(habitDao.findHabitByTitleAndUserId(user.getId(), habit.getTitle())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> habitService.updateHabit(user.getEmail(), habit.getTitle(), upsertHabitRequest));

    }

    @Test
    @DisplayName("Попытка обновления привычки с использованием названия уже имеющейся привычки")
    void testUpdateHabitWithExistsTitle() {
        User user = createUser();
        Habit habit = createHabit(FIRST_HABIT_TITLE);
        UpsertHabitRequest request = UpsertHabitRequest.builder().title(FIRST_HABIT_TITLE).build();
        when(userService.getUserByEmail(user.getEmail())).thenReturn(user);
        when(habitDao.findHabitByTitleAndUserId(user.getId(), request.getTitle())).thenReturn(Optional.of(habit));

        assertThrows(AlreadyExistException.class, () -> habitService.updateHabit(user.getEmail(), FIRST_HABIT_TITLE, request));
    }

    @Test
    @DisplayName("Отметка выполнения привычки")
    void testConfirmHabit() {
        User user = createUser();
        Habit habit = createHabit(FIRST_HABIT_TITLE);
        LocalDate execution = LocalDate.of(2024, 10, 20);
        HabitExecution habitExecution = new HabitExecution(1L, execution, habit);
        HabitConfirmRequest request = HabitConfirmRequest.builder()
                .title(habit.getTitle())
                .date(LocalDate.now())
                .build();
        HabitExecutionResponse expected = new HabitExecutionResponse(request.getTitle(), execution);
        when(userService.getUserByEmail(user.getEmail())).thenReturn(user);
        when(habitDao.findHabitByTitleAndUserId(user.getId(), request.getTitle())).thenReturn(Optional.of(habit));
        when(executionDao.save(any(HabitExecution.class))).thenReturn(habitExecution);

        HabitExecutionResponse response = habitService.confirmHabit(user.getEmail(), request);

        assertThat(response).isEqualTo(expected);
    }

    @Test
    @DisplayName("Попытка отметки выполнения несуществующей привычки")
    void testConformHabitWithIncorrectTitle() {
        User user = createUser();
        HabitConfirmRequest request = HabitConfirmRequest.builder()
                .title("incorrectTitle")
                .date(LocalDate.now())
                .build();
        when(userService.getUserByEmail(user.getEmail())).thenReturn(user);
        when(habitDao.findHabitByTitleAndUserId(user.getId(), request.getTitle())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> habitService.confirmHabit(user.getEmail(), request));
    }

    @Test
    @DisplayName("Получение всех привычек пользователя")
    void testGetAllHabits() {
        User user = createUser();
        List<Habit> habits = List.of(
                createHabit(FIRST_HABIT_TITLE),
                createHabit(SECOND_HABIT_TITLE)
        );
        List<HabitResponse> expected = List.of(
                new HabitResponse(FIRST_HABIT_TITLE, HABIT_TEXT, new ArrayList<>()),
                new HabitResponse(SECOND_HABIT_TITLE, HABIT_TEXT, new ArrayList<>())
        );
        when(userService.getUserByEmail(user.getEmail())).thenReturn(user);
        when(habitDao.getAllUserHabits(user.getId())).thenReturn(habits);

        List<HabitResponse> response = habitService.getAllHabits(user.getEmail());

        assertThat(response).isEqualTo(expected);
    }

    private User createUser() {
        return User.builder()
                .id(1L)
                .name(FIRST_USER_NAME)
                .email(FIRST_USER_EMAIL)
                .password(FIRST_USER_PASSWORD)
                .build();
    }

    private UpsertHabitRequest createHabitDto(String title) {
        return UpsertHabitRequest.builder()
                .title(title)
                .text(HABIT_TEXT)
                .rate(WEEKLY_HABIT)
                .build();
    }

    private Habit createHabit(String title) {
        return Habit.builder()
                .title(title)
                .text(HABIT_TEXT)
                .build();
    }
}
