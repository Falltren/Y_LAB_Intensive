package com.fallt.repository.impl;

import com.fallt.entity.Role;
import com.fallt.entity.User;
import com.fallt.repository.AbstractTest;
import com.fallt.repository.UserDao;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class UserDaoImplTest extends AbstractTest {

    private UserDao userDao;

    @BeforeEach
    void setup() {
        Connection connection = getConnection();
        userDao = new UserDaoImpl(connection);
    }

    @AfterEach
    void afterEach() {
        clearDatabase();
    }

    @BeforeAll
    static void startContainer() {
        postgreSQLContainer.start();
    }

    @AfterAll
    static void stopContainer() {
        postgreSQLContainer.stop();
    }

    @Test
    @DisplayName("Сохранение нового пользователя")
    void whenCreateNewUser_thenUserAddedToDatabase() {
        User user1 = createUser("user1");
        userDao.create(user1);

        List<User> users = userDao.findAll();

        assertThat(users).hasSize(1);
    }

    @Test
    @DisplayName("Обновление данных о пользователе")
    void whenUpdateUser_thenUpdateDataInDatabase() {
        String oldEmail = "user@u.u";
        String newEmail = "newEmail";
        User user = createUser(oldEmail);
        User existedUser = userDao.create(user);
        existedUser.setEmail(newEmail);
        userDao.update(existedUser);

        Optional<User> oldUser = userDao.getUserByEmail(oldEmail);
        Optional<User> userFromDb = userDao.getUserByEmail(newEmail);

        assertThat(oldUser).isEmpty();
        assertThat(userFromDb).isPresent();
        assertThat(userFromDb.get().getId()).isNotNull();
    }

    @Test
    @DisplayName("Получение пользователя по email")
    void whenGetUserByCorrectEmail_thenReturnUserFromDatabase() {
        String email = "user@u.u";
        User user = createUser(email);
        userDao.create(user);

        Optional<User> existedUser = userDao.getUserByEmail(email);

        assertThat(existedUser).isPresent();
        assertThat(existedUser.get().getId()).isNotNull();
        assertThat(existedUser.get().getEmail()).isEqualTo(email);
    }


    @Test
    @DisplayName("Получение пользователя по паролю")
    void whenGetUserByPassword_thenReturnUserFromDatabase() {
        User user = createUser("email");
        String password = "user1pwd";
        user.setPassword(password);
        userDao.create(user);

        Optional<User> existedUser = userDao.getUserByPassword(password);

        assertThat(existedUser).isPresent();
    }

    @Test
    @DisplayName("Удаление пользователя")
    void whenDeleteUser_thenRemoveUserFromDatabase() {
        User user1 = createUser("user1");
        User user2 = createUser("user2");
        User existedUser = userDao.create(user1);
        userDao.create(user2);
        userDao.delete(existedUser);

        List<User> users = userDao.findAll();

        assertThat(users).hasSize(1);
    }

    private User createUser(String email) {
        return User.builder()
                .name("user")
                .email(email)
                .password("pwd")
                .role(Role.ROLE_USER)
                .createAt(LocalDateTime.now())
                .isBlocked(false)
                .build();
    }
}
