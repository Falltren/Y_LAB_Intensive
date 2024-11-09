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
     * @param request Объект, содержащий информацию о добавляемой привычке
     * @return Данные о созданной пользователем привычке
     */
    HabitResponse saveHabit(UpsertHabitRequest request);

    /**
     * Обновление привычки
     *
     * @param id      Идентификатор привычки
     * @param request Объект с данными по измененной привычке
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
     * @return Список привычек
     */
    List<HabitResponse> getAllHabits();

    /**
     * Добавление данных о выполнении привычки
     *
     * @param request Объект, содержащий информацию о названии привычки и дате выполнения
     */
    HabitExecutionResponse confirmHabit(HabitConfirmRequest request);

    /**
     * Получение привычки пользователя по названию
     *
     * @param userId  Идентификатор пользователя
     * @param title Название привычки
     * @return Объект класса Habit, если соответствующая привычка найдена в базе данных.
     * Если привычка отсутствует, будет выброшено исключение EntityNotFoundException
     */
    Habit getHabitByUserIdAndTitle(Long userId, String title);

    /**
     * Получение привычки по идентификатору
     *
     * @param id Идентификатор привычки
     * @return Объект класса Habit, если соответствующая привычка найдена в базе данных.
     * Если привычка отсутствует, будет выброшено исключение EntityNotFoundException
     */
    Habit getHabitById(Long id);

}
