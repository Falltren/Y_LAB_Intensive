package com.fallt.repository.impl;

import com.fallt.entity.Role;
import com.fallt.entity.User;
import com.fallt.repository.AbstractTest;
import com.fallt.repository.UserDao;
import com.fallt.util.DBUtils;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.fallt.TestConstant.*;
import static org.assertj.core.api.Assertions.assertThat;

class UserDaoImplTest extends AbstractTest {

    private UserDao userDao;

    @BeforeEach
    void setup() {
        userDao = new UserDaoImpl();
    }

    @AfterEach
    void afterEach() {
        clearDatabase();
    }

    @BeforeAll
    static void startContainer() {
        postgreSQLContainer.start();
        DBUtils.useTestConnection(postgreSQLContainer.getJdbcUrl(), postgreSQLContainer.getUsername(), postgreSQLContainer.getPassword());
        migrateDatabase();
    }

    @AfterAll
    static void stopContainer() {
        postgreSQLContainer.stop();
    }

    @Test
    @DisplayName("Сохранение нового пользователя")
    void whenCreateNewUser_thenUserAddedToDatabase() {
        User user = createUser(FIRST_USER_EMAIL);
        userDao.create(user);

        List<User> users = userDao.findAll();

        assertThat(users).hasSize(1);
    }

    @Test
    @DisplayName("Обновление данных о пользователе")
    void whenUpdateUser_thenUpdateDataInDatabase() {
        User user = createUser(FIRST_USER_EMAIL);
        User existedUser = userDao.create(user);
        existedUser.setEmail(SECOND_USER_EMAIL);
        userDao.update(existedUser);

        Optional<User> oldUser = userDao.getUserByEmail(FIRST_USER_EMAIL);
        Optional<User> userFromDb = userDao.getUserByEmail(SECOND_USER_EMAIL);

        assertThat(oldUser).isEmpty();
        assertThat(userFromDb).isPresent();
        assertThat(userFromDb.get().getId()).isNotNull();
    }

    @Test
    @DisplayName("Получение пользователя по email")
    void whenGetUserByCorrectEmail_thenReturnUserFromDatabase() {
        User user = createUser(SECOND_USER_EMAIL);
        userDao.create(user);

        Optional<User> existedUser = userDao.getUserByEmail(SECOND_USER_EMAIL);

        assertThat(existedUser).isPresent();
        assertThat(existedUser.get().getId()).isNotNull();
        assertThat(existedUser.get().getEmail()).isEqualTo(SECOND_USER_EMAIL);
    }

    @Test
    @DisplayName("Получение пользователя по паролю")
    void whenGetUserByPassword_thenReturnUserFromDatabase() {
        User user = createUser(FIRST_USER_EMAIL);
        user.setPassword(FIRST_USER_PASSWORD);
        userDao.create(user);

        Optional<User> existedUser = userDao.getUserByPassword(FIRST_USER_PASSWORD);

        assertThat(existedUser).isPresent();
    }

    @Test
    @DisplayName("Удаление пользователя")
    void whenDeleteUser_thenRemoveUserFromDatabase() {
        User user1 = createUser(FIRST_USER_EMAIL);
        User user2 = createUser(SECOND_USER_EMAIL);
        User existedUser = userDao.create(user1);
        userDao.create(user2);
        userDao.delete(existedUser.getEmail());

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
