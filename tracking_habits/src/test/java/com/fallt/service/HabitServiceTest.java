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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

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
        User user = createUser();
        Habit habit = createHabit("habit");
        UpsertHabitRequest request = createHabitDto();
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
        UpsertHabitRequest upsertHabitRequest = createHabitDto();
        when(userService.getUserByEmail(user.getEmail())).thenReturn(user);
        when(habitDao.findHabitByTitleAndUserId(user.getId(), upsertHabitRequest.getTitle())).thenReturn(Optional.of(new Habit()));

        assertThrows(AlreadyExistException.class, () -> habitService.saveHabit(user.getEmail(), upsertHabitRequest));
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

        assertThrows(EntityNotFoundException.class, () -> habitService.getHabitByTitle(user, title));
    }

    @Test
    @DisplayName("Удаление привычки")
    void testDeleteHabit() {
        String title = "title";
        User user = createUser();
        when(userService.getUserByEmail(user.getEmail())).thenReturn(user);

        habitService.deleteHabit(user.getEmail(), title);

        verify(habitDao, times(1)).delete(user.getId(), title);
    }

    @Test
    @DisplayName("Обновление данных о привычке")
    void testUpdateHabit() {
        User user = createUser();
        Habit habit = createHabit("old title");
        UpsertHabitRequest request = createHabitDto();
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
        String newTitle = "new title";
        User user = createUser();
        Habit habit = createHabit("old title");
        UpsertHabitRequest upsertHabitRequest = UpsertHabitRequest.builder().title(newTitle).build();
        when(userService.getUserByEmail(user.getEmail())).thenReturn(user);
        when(habitDao.findHabitByTitleAndUserId(user.getId(), habit.getTitle())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> habitService.updateHabit(user.getEmail(), habit.getTitle(), upsertHabitRequest));

    }

    @Test
    @DisplayName("Попытка обновления привычки с использованием названия уже имеющейся привычки")
    void testUpdateHabitWithExistsTitle() {
        String title = "exists title";
        User user = createUser();
        Habit habit = createHabit("exists title");
        UpsertHabitRequest request = UpsertHabitRequest.builder().title(title).build();
        when(userService.getUserByEmail(user.getEmail())).thenReturn(user);
        when(habitDao.findHabitByTitleAndUserId(user.getId(), request.getTitle())).thenReturn(Optional.of(habit));

        assertThrows(AlreadyExistException.class, () -> habitService.updateHabit(user.getEmail(), title, request));
    }

    @Test
    @DisplayName("Отметка выполнения привычки")
    void testConfirmHabit() {
        User user = createUser();
        Habit habit = createHabit("habit");
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
                createHabit("habit1"),
                createHabit("habit2")
        );
        List<HabitResponse> expected = List.of(
                new HabitResponse("habit1", "text", new ArrayList<>()),
                new HabitResponse("habit2", "text", new ArrayList<>())
        );
        when(userService.getUserByEmail(user.getEmail())).thenReturn(user);
        when(habitDao.getAllUserHabits(user.getId())).thenReturn(habits);

        List<HabitResponse> response = habitService.getAllHabits(user.getEmail());

        assertThat(response).isEqualTo(expected);
    }

    private User createUser() {
        return User.builder()
                .id(1L)
                .name("user")
                .email("user@user.user")
                .password("user")
                .build();
    }

    private UpsertHabitRequest createHabitDto() {
        return UpsertHabitRequest.builder()
                .title("habit")
                .text("text")
                .rate("WEEKLY")
                .build();
    }

    private Habit createHabit(String title) {
        return Habit.builder()
                .title(title)
                .text("text")
                .build();
    }
}
