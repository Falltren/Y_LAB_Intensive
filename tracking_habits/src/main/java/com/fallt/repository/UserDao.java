package com.fallt.repository;

import com.fallt.entity.User;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс для взаимодействия с таблицей пользователя в базе данных
 */
public interface UserDao {
    /**
     * Сохранение нового пользователя в базу данных
     *
     * @param user Объект класса User
     * @return Сохраненная в базе данных сущность User с идентификатором
     */
    User create(User user);

    /**
     * Обновление данных о пользователе
     *
     * @param user Объект класса User
     */
    void update(User user);

    /**
     * Удаление пользователя из базы данных
     *
     * @param user Объект класса User
     */
    void delete(User user);

    /**
     * Получение списка пользователей из базы данных
     *
     * @return Список пользователей
     */
    List<User> findAll();

    /**
     * Метод поиска пользователя в базе данных по email
     *
     * @param email Электронная почта пользователя
     * @return Объект Optional с найденным по указанному электронному адресу пользователем или Optional.empty()
     * если пользователь не найден
     */
    Optional<User> getUserByEmail(String email);

    /**
     * Метод поиска пользователя в базе данных по паролю
     *
     * @param password Пароль пользователя
     * @return Объект Optional с найденным по указанному паролю пользователем или Optional.empty()
     * если пользователь не найден
     */
    Optional<User> getUserByPassword(String password);

    /**
     * Удаление всех пользователей из базы данных
     */
    void deleteAll();

}
