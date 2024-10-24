package com.fallt.service;

import com.fallt.dto.request.UpsertHabitRequest;
import com.fallt.dto.response.HabitResponse;
import com.fallt.entity.Habit;
import com.fallt.entity.HabitExecution;
import com.fallt.entity.User;
import com.fallt.exception.AlreadyExistException;
import com.fallt.mapper.HabitMapper;
import com.fallt.out.ConsoleOutput;
import com.fallt.repository.HabitDao;
import com.fallt.repository.HabitExecutionDao;
import com.fallt.util.Fetch;
import com.fallt.util.Message;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Класс для работы с привычками
 */
@RequiredArgsConstructor
public class HabitService {

    private final ConsoleOutput consoleOutput;

    private final HabitDao habitDao;

    private final HabitExecutionDao executionDao;

    private final UserService userService;

    /**
     * Метод создания привычки
     *
     * @param userEmail Электронная почта пользователя
     * @param request   Объект с данными по новой привычке
     */
    public HabitResponse createHabit(String userEmail, UpsertHabitRequest request) {
        User user = userService.getUserByEmail(userEmail);
        if (findHabit(user, request.getTitle()).isPresent()) {
            throw new AlreadyExistException("Привычка с указанным названием уже существует");
        }
        Habit habit = HabitMapper.INSTANCE.toEntity(request);
        habit.setUser(user);
        return HabitMapper.INSTANCE.toResponse(habitDao.save(habit));
    }

    /**
     * Обновление привычки
     *
     * @param user    Пользователь
     * @param title   Название привычки. Если будет передано название привычки, отсутствующее у пользователя
     *                в консоль будет выведено соответствующее сообщение
     * @param request Объект с данными по редактируемой привычке
     */
    public void updateHabit(User user, String title, UpsertHabitRequest request) {
        Optional<Habit> optionalHabit = findHabit(user, title);
        if (optionalHabit.isEmpty()) {
            consoleOutput.printMessage(Message.INCORRECT_HABIT_TITLE);
            return;
        }
        Habit habit = optionalHabit.get();
        HabitMapper.INSTANCE.updateHabitFromDto(request, habit);
        habitDao.update(habit);
    }

    /**
     * Удаление привычки
     *
     * @param user  Пользователь
     * @param title Название привычки
     */
    public void deleteHabit(User user, String title) {
        habitDao.delete(user.getId(), title);
    }

    /**
     * Получение всех привычек пользователя
     *
     * @param user      Пользователь
     * @param fetchType Параметр, определяющий необходимость получения данных по выполнению привычек
     * @return Список привычек
     */
    public List<Habit> getAllHabits(User user, Fetch fetchType) {
        return habitDao.getAllUserHabits(user.getId(), fetchType);
    }

    /**
     * Добавление данных о выполнении привычки
     *
     * @param user  Пользователь
     * @param title Название привычки
     * @param date  Дата выполнения привычки
     */
    public void confirmHabit(User user, String title, LocalDate date) {
        Optional<Habit> optionalHabit = findHabit(user, title);
        if (optionalHabit.isEmpty()) {
            consoleOutput.printMessage(Message.INCORRECT_HABIT_TITLE);
            return;
        }
        HabitExecution habitExecution = HabitExecution.builder()
                .habit(optionalHabit.get())
                .date(date)
                .build();
        executionDao.save(habitExecution);
    }

    private Optional<Habit> findHabit(User user, String title) {
        return habitDao.findHabitByTitleAndUserId(user.getId(), title);
    }

    /**
     * Получение привычки пользователя по названию
     *
     * @param user  Пользователь
     * @param title Название привычки
     * @return Объект класса Habit, если соответствующая привычка найдена в базе данных или null,
     * если привычка у пользователя отсутствует
     */
    public Habit getHabitByTitle(User user, String title) {
        return findHabit(user, title).orElse(null);
    }

}
