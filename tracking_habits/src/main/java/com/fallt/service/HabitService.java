package com.fallt.service;

import com.fallt.domain.dto.request.HabitConfirmRequest;
import com.fallt.domain.dto.request.UpsertHabitRequest;
import com.fallt.domain.dto.response.HabitExecutionResponse;
import com.fallt.domain.dto.response.HabitResponse;
import com.fallt.domain.entity.Habit;

import java.util.List;

/**
 * Интерфейс, предназначенный для работы с привычками
 */
public interface HabitService {

    /**
     * @param request Объект, содержащий информацию о добавляемой привычке. Если у пользователя
     *                уже присутствует привычка с указанным названием, будет выброшено исключение AlreadyExistException
     * @return Данные о созданной пользователем привычке
     */
    HabitResponse saveHabit(UpsertHabitRequest request);

    /**
     * Обновление привычки
     *
     * @param id      Идентификатор привычки
     * @param request Объект с данными по измененной привычке. Если у пользователя
     *                уже присутствует привычка с указанным названием, будет выброшено исключение AlreadyExistException
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
     * Получение привычки по идентификатору
     *
     * @param id Идентификатор привычки
     * @return Объект класса Habit, если соответствующая привычка найдена в базе данных.
     * Если привычка отсутствует, будет выброшено исключение EntityNotFoundException
     */
    Habit getHabitById(Long id);

}
