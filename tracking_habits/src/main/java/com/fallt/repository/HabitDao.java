package com.fallt.repository;

import com.fallt.entity.Habit;
import com.fallt.util.Fetch;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс для взаимодействия с таблицей привычек в базе данных
 */
public interface HabitDao {
    /**
     * Сохранение привычки в базу данных
     *
     * @param habit Объект класса Habit
     * @return Сохраненная в базе данных сущность Habit с идентификатором
     */
    Habit save(Habit habit);

    /**
     * Обновление данных о привычке
     *
     * @param habit Объект класса Habit
     */
    void update(Habit habit);

    /**
     * Получение списка привычек пользователя
     *
     * @param userId    Идентификатор пользователя
     * @param fetchType Параметр, указывающий необходимость выгрузки данных по выполнению привычек вместе с привычками
     * @return Список привычек
     */
    List<Habit> getAllUserHabits(Long userId, Fetch fetchType);

    /**
     * Поиск привычки по названию
     *
     * @param userId Идентификатор пользователя
     * @param title  Название привычки
     * @return Объект Optional с найденной по указанному названию привычкой или Optional.empty()
     * если привычка не найдена
     */
    Optional<Habit> findHabitByTitleAndUserId(Long userId, String title);

    /**
     * Удаление привычки пользователя по ее названию
     *
     * @param id    Идентификатор пользователя
     * @param title Название привычки
     */
    void delete(Long id, String title);

}
