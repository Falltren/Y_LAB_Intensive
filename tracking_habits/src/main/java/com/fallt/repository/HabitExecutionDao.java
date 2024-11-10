package com.fallt.repository;

import com.fallt.domain.entity.HabitExecution;

/**
 * Интерфейс для взаимодействия с таблицей выполнения привычек в базе данных
 */
public interface HabitExecutionDao {

    /**
     * Сохранение выполнения привычки в базу данных
     *
     * @param execution Объект класса HabitExecution, содержащий дату выполнения привычки
     */
    HabitExecution save(HabitExecution execution);

}
