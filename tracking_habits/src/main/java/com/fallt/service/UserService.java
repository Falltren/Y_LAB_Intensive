package com.fallt.service;

import com.fallt.dto.UserDto;
import com.fallt.entity.Role;
import com.fallt.entity.User;
import com.fallt.out.ConsoleOutput;
import com.fallt.repository.UserDao;
import com.fallt.util.Message;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Класс для работы с пользователями
 */
@RequiredArgsConstructor
public class UserService {

    private final UserDao userDao;

    private final ConsoleOutput consoleOutput;

    /**
     * Получение всех пользователей (доступно только пользователям с ролью ROLE_ADMIN)
     *
     * @return Список пользователей
     */
    public List<User> getAllUsers() {
        return userDao.findAll();
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
            consoleOutput.printMessage(Message.INCORRECT_EMAIL);
            return null;
        }
        return user.get();
    }

    /**
     * Сохранение нового пользователя в базу данных
     *
     * @param userDto Объект с данным пользователя
     * @return Сохраненный в базе данных пользователь с идентификатором
     */
    public User createUser(UserDto userDto) {
        if (isExistsEmail(userDto.getEmail())) {
            consoleOutput.printMessage(Message.EMAIL_EXIST);
            return null;
        }
        if (isExistsPassword(userDto.getPassword())) {
            consoleOutput.printMessage(Message.PASSWORD_EXIST);
            return null;
        }
        User user = User.builder()
                .name(userDto.getName())
                .password(userDto.getPassword())
                .email(userDto.getEmail())
                .createAt(LocalDateTime.now())
                .role(Role.ROLE_USER)
                .isBlocked(false)
                .build();
        return userDao.create(user);
    }

    /**
     * Блокировка пользователя (выставление соответствующего флага в true). Заблокированный пользователь не сможет
     * войти в систему. Данное действие доступно только пользователям с ролью ROLE_ADMIN
     *
     * @param user Пользователь
     */
    public void blockingUser(User user) {
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
    public void updateUser(String email, UserDto updateUser) {
        User user = getUserByEmail(email);
        if (updateUser.getEmail() != null) {
            if (isExistsEmail(updateUser.getEmail())) {
                consoleOutput.printMessage(Message.EMAIL_EXIST);
                return;
            }
            user.setEmail(updateUser.getEmail());
        }
        if (updateUser.getPassword() != null) {
            if (isExistsPassword(updateUser.getPassword())) {
                consoleOutput.printMessage(Message.PASSWORD_EXIST);
                return;
            }
            user.setPassword(updateUser.getPassword());
        }
        if (updateUser.getName() != null) {
            user.setName(updateUser.getName());
        }
        user.setUpdateAt(LocalDateTime.now());
        userDao.update(user);
    }

    /**
     * Удаление пользователя
     *
     * @param user Пользователь
     */
    public void deleteUser(User user) {
        userDao.delete(user);
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
