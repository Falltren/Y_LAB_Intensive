package com.fallt.service;

import com.fallt.domain.dto.request.HabitConfirmRequest;
import com.fallt.domain.dto.request.UpsertHabitRequest;
import com.fallt.domain.dto.response.HabitExecutionResponse;
import com.fallt.domain.dto.response.HabitResponse;
import com.fallt.domain.entity.Habit;
import com.fallt.domain.entity.User;

import java.util.List;

/**
 * Интерфейс, предназначенный для работы с привычками
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
     * @param id      Идентификатор привычки
     * @param request Объект с данными по редактируемой привычке
     */
    HabitResponse updateHabit(Long id, UpsertHabitRequest request);

    /**
     * Удаление привычки
     *
     * @param id Идентификатор привычки
     */
    void deleteHabit(Long id);

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
     * Если привычка отсутствует, будет выброшено исключение EntityNotFoundException
     */
    Habit getHabitByTitle(User user, String title);

    /**
     * Получение привычки по идентификатору
     *
     * @param id Идентификатор привычки
     * @return Объект класса Habit, если соответствующая привычка найдена в базе данных.
     * Если привычка отсутствует, будет выброшено исключение EntityNotFoundException
     */
    Habit getHabitById(Long id);

}
