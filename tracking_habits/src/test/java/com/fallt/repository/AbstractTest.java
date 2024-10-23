package com.fallt.repository;

import com.fallt.entity.ExecutionRate;
import com.fallt.entity.Habit;
import com.fallt.entity.Role;
import com.fallt.entity.User;
import com.fallt.exception.DBException;
import com.fallt.repository.impl.HabitDaoImpl;
import com.fallt.repository.impl.UserDaoImpl;
import com.fallt.util.DBUtils;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
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
                .withReuse(true);
    }

    protected Connection getConnection() {
        try {
            return DriverManager.getConnection(postgreSQLContainer.getJdbcUrl(), postgreSQLContainer.getUsername(), postgreSQLContainer.getPassword());
        } catch (SQLException e) {
            throw new DBException(e.getMessage());
        }
    }

    protected User addUserToDatabase() {
        UserDao userDao = new UserDaoImpl();
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
        HabitDao habitDao = new HabitDaoImpl();
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
        UserDao userDao = new UserDaoImpl();
        userDao.deleteAll();
    }

    protected static void migrateDatabase(){
        try (Connection connection = DBUtils.getConnection()) {
            connection.createStatement().execute("CREATE SCHEMA if not exists service_schema;");

            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
            database.setLiquibaseSchemaName("service_schema");
            database.setDefaultSchemaName("my_schema");
            Liquibase liquibase =
                    new Liquibase("db/changelog/db.test-changelog-master.xml", new ClassLoaderResourceAccessor(), database);
            liquibase.update();
        } catch (LiquibaseException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}


