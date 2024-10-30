package com.fallt.service;

import com.fallt.aop.audit.Auditable;
import com.fallt.aop.logging.Loggable;
import com.fallt.dto.request.UpsertUserRequest;
import com.fallt.dto.response.UserResponse;
import com.fallt.entity.Role;
import com.fallt.entity.User;
import com.fallt.exception.AlreadyExistException;
import com.fallt.mapper.UserMapper;
import com.fallt.out.ConsoleOutput;
import com.fallt.repository.UserDao;
import com.fallt.util.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Класс для работы с пользователями
 */
@RequiredArgsConstructor
@Loggable
@Auditable
@Service
public class UserService {

    private final UserDao userDao;

    /**
     * Получение всех пользователей (доступно только пользователям с ролью ROLE_ADMIN)
     *
     * @return Список пользователей
     */
    public List<UserResponse> getAllUsers() {
        return UserMapper.INSTANCE.toResponseList(userDao.findAll());
    }

    /**
     * Получение пользователя по электронной почте
     *
     * @param email Электронная почта
     * @return Объект класса User, если в базе данных присутствует пользователь с указанной электронной почтой
     * или null, если пользователь не найден
     */
    public User getUserByEmail(String email) {
        Optional<User> user = userDao.getUserByEmail(email);
        if (user.isEmpty()) {
            System.out.println(Message.INCORRECT_EMAIL);
            return null;
        }
        return user.get();
    }

    /**
     * Сохранение нового пользователя в базу данных
     *
     * @param request Объект с данным пользователя
     * @return Сохраненный в базе данных пользователь с идентификатором
     */
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
    public void deleteUser(String email) {
        userDao.delete(email);
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
