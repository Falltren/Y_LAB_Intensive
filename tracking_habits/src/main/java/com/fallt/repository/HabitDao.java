package com.fallt.repository;

import com.fallt.domain.entity.Habit;

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
    Habit update(Habit habit);

    /**
     * Получение списка привычек пользователя
     *
     * @param userId Идентификатор пользователя
     * @return Список привычек пользователя
     */
    List<Habit> getAllUserHabits(Long userId);

    /**
     * Поиск привычки по названию
     *
     * @param userId Идентификатор пользователя
     * @param title  Название привычки
     * @return Объект {@link Optional} с найденной по указанному названию привычкой
     * или {@link Optional#empty()} если привычка не найдена
     */
    Optional<Habit> findByTitleAndUserId(Long userId, String title);

    /**
     * Поиск привычки по идентификатору
     *
     * @param id Идентификатор привычки
     * @return Объект {@link Optional} с найденной по указанному идентификатору привычкой
     * или {@link Optional#empty()} если привычка не найдена
     */
    Optional<Habit> findById(Long id);

    /**
     * Удаление привычки пользователя
     *
     * @param id Идентификатор привычки
     */
    void delete(Long id);

}
