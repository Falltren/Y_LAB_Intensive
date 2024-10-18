package com.fallt.repository;

import com.fallt.entity.ExecutionRate;
import com.fallt.entity.Habit;
import com.fallt.entity.Role;
import com.fallt.entity.User;
import com.fallt.exception.DBException;
import com.fallt.repository.impl.HabitDaoImpl;
import com.fallt.repository.impl.UserDaoImpl;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Testcontainers
public abstract class AbstractTest {

    protected static PostgreSQLContainer postgreSQLContainer;

    static {
        DockerImageName postgres = DockerImageName.parse("postgres:15.4");
        postgreSQLContainer = new PostgreSQLContainer<>(postgres)
                .withUsername("testUser")
                .withPassword("testPassword")
                .withDatabaseName("testBase")
                .withInitScript("init_script_test.sql")
                .withReuse(true);
    }

    protected Connection getConnection() {
        try {
            Connection connection = DriverManager.getConnection(postgreSQLContainer.getJdbcUrl(), postgreSQLContainer.getUsername(), postgreSQLContainer.getPassword());
            connection.setAutoCommit(false);
            return connection;
        } catch (SQLException e) {
            throw new DBException(e.getMessage());
        }
    }

    protected User addUserToDatabase() {
        UserDao userDao = new UserDaoImpl(getConnection());
        User userForCreate = User.builder()
                .name("user")
                .email("user@u.u")
                .password("pwd")
                .createAt(LocalDateTime.now())
                .role(Role.ROLE_USER)
                .build();
        return userDao.create(userForCreate);
    }

    protected Habit addHabitToDatabase() {
        HabitDao habitDao = new HabitDaoImpl(getConnection());
        User user = addUserToDatabase();
        Habit habit = Habit.builder()
                .title("test habit")
                .text("habit")
                .executionRate(ExecutionRate.DAILY)
                .user(user)
                .createAt(LocalDate.of(2024, 10, 1))
                .build();
        return habitDao.save(habit);
    }

    protected void clearDatabase() {
        UserDao userDao = new UserDaoImpl(getConnection());
        userDao.deleteAll();
    }
}


