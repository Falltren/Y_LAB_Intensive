package com.fallt.service;

import com.fallt.domain.dto.request.UpsertUserRequest;
import com.fallt.domain.dto.response.UserResponse;
import com.fallt.domain.entity.User;

import java.util.List;

/**
 * Интерфейс, предназначенный для работы с пользователями
 */
public interface UserService {

    /**
     * Получение всех пользователей
     *
     * @return Список пользователей
     */
    List<UserResponse> getAllUsers();

    /**
     * Получение пользователя по электронной почте
     *
     * @param email Электронная почта
     * @return Объект класса User, если в базе данных присутствует пользователь с указанной электронной почтой
     * или null, если пользователь не найден
     */
    User getUserByEmail(String email);

    /**
     * Сохранение нового пользователя в базу данных
     *
     * @param request Объект с данным пользователя
     * @return Сохраненный в базе данных пользователь с идентификатором
     */
    UserResponse saveUser(UpsertUserRequest request);

    /**
     * Блокировка пользователя (выставление соответствующего флага в true). Заблокированный пользователь не сможет
     * войти в систему. Данное действие доступно только пользователям с ролью ROLE_ADMIN
     *
     * @param email Почта пользователя
     */
    void blockingUser(String email);

    /**
     * Обновление пользователя
     *
     * @param email      Электронный адрес обновляемого пользователя
     * @param updateUser Объект с обновляемыми данными пользователя
     */
    UserResponse updateUser(String email, UpsertUserRequest updateUser);

    /**
     * Удаление пользователя
     *
     * @param email Электронная почта пользователя
     */
    void deleteUser(String email);
}
