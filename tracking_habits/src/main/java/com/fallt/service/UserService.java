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
     * Если пользователь не найден, будет выброшено исключение EntityNotFoundException
     */
    User getUserByEmail(String email);

    /**
     * @param id Идентификатор пользователя
     * @return Объект класса User, если в базе данных присутствует пользователь с указанным идентификатором
     * Если пользователь не найден, будет выброшено исключение EntityNotFoundException
     */
    User getUserById(Long id);

    /**
     * Сохранение нового пользователя в базу данных
     *
     * @param request Объект с данным пользователя. Если для нового пользователя
     *                будет указан email или пароль, уже используемые другим пользователем, новый пользователь
     *                не будет добавлен в систему и будет выброшено исключение {@code AlreadyExistException}
     * @return Сохраненный в базе данных пользователь с идентификатором.
     */
    UserResponse saveUser(UpsertUserRequest request);

    /**
     * Блокировка пользователя. Заблокированный пользователь не сможет
     * войти в систему. Данное действие доступно только пользователям с ролью ROLE_ADMIN
     *
     * @param id Идентификатор блокируемого пользователя
     */
    void blockingUser(Long id);

    /**
     * Обновление пользователя
     *
     * @param id         Идентификатор пользователя
     * @param updateUser Объект с обновляемыми данными пользователя. Если новый email или пароль
     *                   уже используются другими пользователями, то обновления не произойдет и
     *                   будет выброшено исключение {@code AlreadyExistException}
     */
    UserResponse updateUser(Long id, UpsertUserRequest updateUser);

    /**
     * Удаление пользователя
     *
     * @param id Идентификатор пользователя
     */
    void deleteUser(Long id);

}
