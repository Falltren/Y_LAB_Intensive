package com.fallt.service;

import com.fallt.dto.request.HabitConfirmRequest;
import com.fallt.dto.request.UpsertHabitRequest;
import com.fallt.dto.response.HabitExecutionResponse;
import com.fallt.dto.response.HabitResponse;
import com.fallt.entity.Habit;
import com.fallt.entity.User;

import java.util.List;

/**
 * Класс для работы с привычками
 */
public interface HabitService {

    /**
     * Метод создания привычки
     *
     * @param userEmail Электронная почта пользователя
     * @param request   Объект с данными по новой привычке
     */
    HabitResponse saveHabit(String userEmail, UpsertHabitRequest request);

    /**
     * Обновление привычки
     *
     * @param userEmail Электронная почта пользователя
     * @param title     Название привычки. Если будет передано название привычки, отсутствующее у пользователя
     *                  в консоль будет выведено соответствующее сообщение
     * @param request   Объект с данными по редактируемой привычке
     */
    HabitResponse updateHabit(String userEmail, String title, UpsertHabitRequest request);

    /**
     * Удаление привычки
     *
     * @param email Электронная почта пользователя
     * @param title Название привычки
     */
    void deleteHabit(String email, String title);

    /**
     * Получение всех привычек пользователя
     *
     * @param email Электронный адрес пользователя
     * @return Список привычек
     */
    List<HabitResponse> getAllHabits(String email);

    /**
     * Добавление данных о выполнении привычки
     *
     * @param email   Электронная почта пользователь
     * @param request Объект, содержащий информацию о названии привычки и дате выполнения
     */
    HabitExecutionResponse confirmHabit(String email, HabitConfirmRequest request);

    /**
     * Получение привычки пользователя по названию
     *
     * @param user  Пользователь
     * @param title Название привычки
     * @return Объект класса Habit, если соответствующая привычка найдена в базе данных.
     * Если привычка отсутствует, будет выброшено EntityNotFoundException
     */
    Habit getHabitByTitle(User user, String title);

}
