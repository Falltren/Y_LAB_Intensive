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
import com.fallt.mapper.HabitMapper;
import com.fallt.out.ConsoleOutput;
import com.fallt.repository.HabitDao;
import com.fallt.repository.HabitExecutionDao;
import com.fallt.util.Fetch;
import lombok.RequiredArgsConstructor;

import java.text.MessageFormat;
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
     * @param userEmail Электронная почта пользователя
     * @param title     Название привычки. Если будет передано название привычки, отсутствующее у пользователя
     *                  в консоль будет выведено соответствующее сообщение
     * @param request   Объект с данными по редактируемой привычке
     */
    public HabitResponse updateHabit(String userEmail, String title, UpsertHabitRequest request) {
        User user = userService.getUserByEmail(userEmail);
        Optional<Habit> optionalHabit = findHabit(user, title);
        if (optionalHabit.isEmpty()) {
            throw new EntityNotFoundException(MessageFormat.format("У вас отсутствует привычка с указанным названием: {0}", title));
        }
        if (request.getTitle() != null && findHabit(user, request.getTitle()).isPresent()) {
            throw new AlreadyExistException("Привычка с указанным названием уже существует");
        }
        Habit habit = optionalHabit.get();
        HabitMapper.INSTANCE.updateHabitFromDto(request, habit);
        return HabitMapper.INSTANCE.toResponse(habitDao.update(habit));
    }

    /**
     * Удаление привычки
     *
     * @param email Электронная почта пользователя
     * @param title Название привычки
     */
    public void deleteHabit(String email, String title) {
        User user = userService.getUserByEmail(email);
        habitDao.delete(user.getId(), title);
    }

    /**
     * Получение всех привычек пользователя
     *
     * @param email     Электронный адрес пользователя
     * @param fetchType Параметр, определяющий необходимость получения данных по выполнению привычек
     * @return Список привычек
     */
    public List<HabitResponse> getAllHabits(String email, Fetch fetchType) {
        User user = userService.getUserByEmail(email);
        return HabitMapper.INSTANCE.toResponseList(habitDao.getAllUserHabits(user.getId(), fetchType));
    }

    /**
     * Добавление данных о выполнении привычки
     *
     * @param email   Электронная почта пользователь
     * @param request Объект, содержащий информацию о названии привычки и дате выполнения
     */
    public HabitExecutionResponse confirmHabit(String email, HabitConfirmRequest request) {
        User user = userService.getUserByEmail(email);
        Optional<Habit> optionalHabit = findHabit(user, request.getTitle());
        if (optionalHabit.isEmpty()) {
            throw new EntityNotFoundException(MessageFormat.format("У вас отсутствует привычка с указанным названием: {0}", request.getTitle()));
        }
        HabitExecution habitExecution = HabitExecution.builder()
                .habit(optionalHabit.get())
                .date(request.getDate())
                .build();
        HabitExecutionResponse response = HabitMapper.INSTANCE.toExecutionResponse(executionDao.save(habitExecution));
        response.setTitle(request.getTitle());
        return response;
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
