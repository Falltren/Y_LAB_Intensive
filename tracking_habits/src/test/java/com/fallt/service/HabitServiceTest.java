package com.fallt.service;

import com.fallt.domain.dto.response.HabitExecutionResponse;
import com.fallt.domain.dto.response.HabitResponse;
import com.fallt.domain.entity.Habit;
import com.fallt.domain.entity.HabitExecution;
import com.fallt.exception.AlreadyExistException;
import com.fallt.exception.EntityNotFoundException;
import com.fallt.repository.HabitDao;
import com.fallt.repository.HabitExecutionDao;
import com.fallt.service.impl.HabitServiceImpl;
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

import static com.fallt.TestConstant.CONFIRM_REQUEST;
import static com.fallt.TestConstant.FIRST_HABIT_TITLE;
import static com.fallt.TestConstant.HABIT_FROM_DATABASE;
import static com.fallt.TestConstant.HABIT_REQUEST;
import static com.fallt.TestConstant.HABIT_RESPONSE;
import static com.fallt.TestConstant.HABIT_TEXT;
import static com.fallt.TestConstant.SECOND_HABIT_TITLE;
import static com.fallt.TestConstant.USER_FROM_DATABASE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HabitServiceTest {

    @InjectMocks
    private HabitServiceImpl habitService;

    @Mock
    private HabitDao habitDao;

    @Mock
    private HabitExecutionDao executionDao;

    @Mock
    private UserService userService;

    @Test
    @DisplayName("Успешное добавление привычки")
    void createHabit() {
        when(userService.getUserByEmail(USER_FROM_DATABASE.getEmail())).thenReturn(USER_FROM_DATABASE);
        when(habitDao.findByTitleAndUserId(USER_FROM_DATABASE.getId(), HABIT_REQUEST.getTitle())).thenReturn(Optional.empty());
        when(habitDao.save(any(Habit.class))).thenReturn(HABIT_FROM_DATABASE);

        HabitResponse response = habitService.saveHabit(USER_FROM_DATABASE.getEmail(), HABIT_REQUEST);

        assertThat(response).isEqualTo(HABIT_RESPONSE);
    }

    @Test
    @DisplayName("Попытка добавления привычки с дублирующимся названием")
    void createHabitWithDuplicateTitle() {
        when(userService.getUserByEmail(USER_FROM_DATABASE.getEmail())).thenReturn(USER_FROM_DATABASE);
        when(habitDao.findByTitleAndUserId(USER_FROM_DATABASE.getId(), HABIT_REQUEST.getTitle())).thenReturn(Optional.of(new Habit()));

        assertThrows(AlreadyExistException.class, () -> habitService.saveHabit(USER_FROM_DATABASE.getEmail(), HABIT_REQUEST));
    }

    @Test
    @DisplayName("Получение привычки по названию")
    void testGetHabitByTitle() {
        when(habitDao.findByTitleAndUserId(USER_FROM_DATABASE.getId(), FIRST_HABIT_TITLE)).thenReturn(Optional.of(HABIT_FROM_DATABASE));

        Habit existedHabit = habitService.getHabitByTitle(USER_FROM_DATABASE, FIRST_HABIT_TITLE);
        assertThat(existedHabit.getTitle()).isEqualTo(FIRST_HABIT_TITLE);
    }

    @Test
    @DisplayName("Попытка получения привычки по отсутствующему названию")
    void testGetHabitByIncorrectTitle() {
        when(habitDao.findByTitleAndUserId(USER_FROM_DATABASE.getId(), SECOND_HABIT_TITLE)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> habitService.getHabitByTitle(USER_FROM_DATABASE, SECOND_HABIT_TITLE));
    }

    @Test
    @DisplayName("Удаление привычки")
    void testDeleteHabit() {
        when(userService.getUserByEmail(USER_FROM_DATABASE.getEmail())).thenReturn(USER_FROM_DATABASE);

        habitService.deleteHabit(2L);
        verify(habitDao, times(1)).delete(2L);
    }

    @Test
    @DisplayName("Обновление данных о привычке")
    void testUpdateHabit() {
        Habit habitFromDb = HABIT_FROM_DATABASE;
        habitFromDb.setTitle(SECOND_HABIT_TITLE);

        when(userService.getUserByEmail(USER_FROM_DATABASE.getEmail())).thenReturn(USER_FROM_DATABASE);
        when(habitDao.findByTitleAndUserId(USER_FROM_DATABASE.getId(), SECOND_HABIT_TITLE)).thenReturn(Optional.of(habitFromDb));
        when(habitDao.update(any(Habit.class))).thenReturn(HABIT_FROM_DATABASE);

        HabitResponse response = habitService.updateHabit(1L, HABIT_REQUEST);
        verify(habitDao, times(1)).update(HABIT_FROM_DATABASE);
        assertThat(response).isEqualTo(HABIT_RESPONSE);
    }

    @Test
    @DisplayName("Попытка обновления привычки по некорректному названию")
    void testUpdateHabitByIncorrectTitle() {
        when(userService.getUserByEmail(USER_FROM_DATABASE.getEmail())).thenReturn(USER_FROM_DATABASE);
        when(habitDao.findByTitleAndUserId(USER_FROM_DATABASE.getId(), SECOND_HABIT_TITLE)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> habitService.updateHabit(1L, HABIT_REQUEST));

    }

    @Test
    @DisplayName("Попытка обновления привычки с использованием названия уже имеющейся привычки")
    void testUpdateHabitWithExistsTitle() {
        when(userService.getUserByEmail(USER_FROM_DATABASE.getEmail())).thenReturn(USER_FROM_DATABASE);
        when(habitDao.findByTitleAndUserId(USER_FROM_DATABASE.getId(), FIRST_HABIT_TITLE)).thenReturn(Optional.of(HABIT_FROM_DATABASE));

        assertThrows(AlreadyExistException.class, () -> habitService.updateHabit(1L, HABIT_REQUEST));
    }

    @Test
    @DisplayName("Отметка выполнения привычки")
    void testConfirmHabit() {
        LocalDate execution = LocalDate.of(2024, 10, 20);
        HabitExecution habitExecution = new HabitExecution(1L, execution, HABIT_FROM_DATABASE);
        HabitExecutionResponse expected = new HabitExecutionResponse(FIRST_HABIT_TITLE, execution);

        when(userService.getUserByEmail(USER_FROM_DATABASE.getEmail())).thenReturn(USER_FROM_DATABASE);
        when(habitDao.findByTitleAndUserId(USER_FROM_DATABASE.getId(), FIRST_HABIT_TITLE)).thenReturn(Optional.of(HABIT_FROM_DATABASE));
        when(executionDao.save(any(HabitExecution.class))).thenReturn(habitExecution);

        HabitExecutionResponse response = habitService.confirmHabit(USER_FROM_DATABASE.getEmail(), CONFIRM_REQUEST);
        assertThat(response).isEqualTo(expected);
    }

    @Test
    @DisplayName("Попытка отметки выполнения несуществующей привычки")
    void testConformHabitWithIncorrectTitle() {
        when(userService.getUserByEmail(USER_FROM_DATABASE.getEmail())).thenReturn(USER_FROM_DATABASE);
        when(habitDao.findByTitleAndUserId(USER_FROM_DATABASE.getId(), FIRST_HABIT_TITLE)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> habitService.confirmHabit(USER_FROM_DATABASE.getEmail(), CONFIRM_REQUEST));
    }

    @Test
    @DisplayName("Получение всех привычек пользователя")
    void testGetAllHabits() {
        List<Habit> habits = List.of(
                Habit.builder().title(FIRST_HABIT_TITLE).text(HABIT_TEXT).build(),
                Habit.builder().title(SECOND_HABIT_TITLE).text(HABIT_TEXT).build()
        );
        List<HabitResponse> expected = List.of(
                HabitResponse.builder().title(FIRST_HABIT_TITLE).text(HABIT_TEXT).successfulExecution(new ArrayList<>()).build(),
                HabitResponse.builder().title(SECOND_HABIT_TITLE).text(HABIT_TEXT).successfulExecution(new ArrayList<>()).build()
        );

        when(userService.getUserByEmail(USER_FROM_DATABASE.getEmail())).thenReturn(USER_FROM_DATABASE);
        when(habitDao.getAllUserHabits(USER_FROM_DATABASE.getId())).thenReturn(habits);

        List<HabitResponse> response = habitService.getAllHabits(USER_FROM_DATABASE.getEmail());
        assertThat(response).isEqualTo(expected);
    }
}
