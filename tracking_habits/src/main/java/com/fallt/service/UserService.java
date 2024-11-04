package com.fallt.service;

import com.fallt.aop.audit.ActionType;
import com.fallt.aop.audit.Auditable;
import com.fallt.aop.logging.Loggable;
import com.fallt.dto.request.UpsertUserRequest;
import com.fallt.dto.response.UserResponse;
import com.fallt.entity.Role;
import com.fallt.entity.User;
import com.fallt.exception.AlreadyExistException;
import com.fallt.exception.EntityNotFoundException;
import com.fallt.mapper.UserMapper;
import com.fallt.out.ConsoleOutput;
import com.fallt.repository.UserDao;
import lombok.RequiredArgsConstructor;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Класс для работы с пользователями
 */
@RequiredArgsConstructor
@Loggable
public class UserService {

    private final UserDao userDao;

    /**
     * Получение всех пользователей (доступно только пользователям с ролью ROLE_ADMIN)
     *
     * @return Список пользователей
     */
    @Auditable(action = ActionType.GET)
    public List<UserResponse> getAllUsers() {
        return UserMapper.INSTANCE.toResponseList(userDao.findAll());
    }

    /**
     * Сохранение нового пользователя в базу данных
     *
     * @param request Объект с данным пользователя
     * @return Сохраненный в базе данных пользователь с идентификатором
     */
    @Auditable(action = ActionType.CREATE)
    public UserResponse saveUser(UpsertUserRequest request) {
        if (isExistsEmail(request.getEmail())) {
            throw new AlreadyExistException(MessageFormat.format("Электронная почта: {0} уже используется", request.getEmail()));
        }
        if (isExistsPassword(request.getPassword())) {
            throw new AlreadyExistException(MessageFormat.format("Пароль: {0} уже используется", request.getPassword()));
        }
        User user = UserMapper.INSTANCE.toEntity(request);
        user.setRole(Role.ROLE_USER);
        user.setBlocked(false);
        User savedUser = userDao.create(user);
        return UserMapper.INSTANCE.toResponse(savedUser);
    }

    /**
     * Блокировка пользователя (выставление соответствующего флага в true). Заблокированный пользователь не сможет
     * войти в систему. Данное действие доступно только пользователям с ролью ROLE_ADMIN
     *
     * @param email Почта пользователя
     */
    @Auditable(action = ActionType.UPDATE)
    public void blockingUser(String email) {
        User user = getUserByEmail(email);
        user.setBlocked(true);
        user.setUpdateAt(LocalDateTime.now());
        userDao.update(user);
    }

    /**
     * Обновление пользователя
     *
     * @param email      Электронный адрес обновляемого пользователя
     * @param updateUser Объект с обновляемыми данными пользователя
     */
    @Auditable(action = ActionType.UPDATE)
    public UserResponse updateUser(String email, UpsertUserRequest updateUser) {
        User user = getUserByEmail(email);
        if (updateUser.getEmail() != null && isExistsEmail(updateUser.getEmail())) {
            throw new AlreadyExistException(MessageFormat.format("Электронная почта: {0} уже используется", updateUser.getEmail()));
        }

        if (updateUser.getPassword() != null && isExistsPassword(updateUser.getPassword())) {
            throw new AlreadyExistException(MessageFormat.format("Пароль: {0} уже используется", updateUser.getPassword()));
        }
        UserMapper.INSTANCE.updateUserFromDto(updateUser, user);
        user.setUpdateAt(LocalDateTime.now());
        return UserMapper.INSTANCE.toResponse(userDao.update(user));
    }

    /**
     * Удаление пользователя
     *
     * @param email Электронная почта пользователя
     */
    @Auditable(action = ActionType.DELETE)
    public void deleteUser(String email) {
        userDao.delete(email);
    }

    /**
     * Получение пользователя по электронной почте
     *
     * @param email Электронная почта
     * @return Объект класса User, если в базе данных присутствует пользователь с указанной электронной почтой
     * или null, если пользователь не найден
     */
    @Auditable(action = ActionType.GET)
    public User getUserByEmail(String email) {
        return userDao.getUserByEmail(email).orElseThrow(
                () -> new EntityNotFoundException(MessageFormat.format("Пользователь с email: {0} не найден", email)));
    }

    /**
     * Возвращает true, если в базе данных существует пользователь с указанной электронной почтой
     *
     * @param email Электронная почта пользователя
     * @return Результат поиска пользователя по электронной почте
     */
    public boolean isExistsEmail(String email) {
        return userDao.getUserByEmail(email).isPresent();
    }

    /**
     * Возвращает true, если в базе данных существует пользователь с указанным паролем
     *
     * @param password Пароль пользователя
     * @return Результат поиска пользователя по паролю
     */
    public boolean isExistsPassword(String password) {
        return userDao.getUserByPassword(password).isPresent();
    }
}
