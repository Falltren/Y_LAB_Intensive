package com.fallt.repository;

import com.fallt.entity.HabitExecution;

/**
 * Интерфейс для взаимодействия с таблицей выполнения привычек в базе данных
 */
public interface HabitExecutionDao {
    /**
     * Сохранение выполнения привычки в базу данных
     *
     * @param execution Объект класса HabitExecution
     */
    void save(HabitExecution execution);
}
