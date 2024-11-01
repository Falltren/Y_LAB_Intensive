package com.fallt.repository;

import com.fallt.entity.ExecutionRate;
import com.fallt.entity.Habit;
import com.fallt.entity.Role;
import com.fallt.entity.User;
import com.fallt.repository.impl.HabitDaoImpl;
import com.fallt.repository.impl.UserDaoImpl;
import com.fallt.util.DbConnectionManager;
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
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Testcontainers
public abstract class AbstractTest {

    protected static final String DRIVER_NAME = "org.postgresql.Driver";

    protected static final String SCHEMA = "my_schema";

    protected static PostgreSQLContainer postgreSQLContainer;
    protected static DbConnectionManager connectionManager = new DbConnectionManager();

    static {
        DockerImageName postgres = DockerImageName.parse("postgres:15.4");
        postgreSQLContainer = new PostgreSQLContainer<>(postgres)
                .withUsername("testUser")
                .withPassword("testPassword")
                .withDatabaseName("testBase")
                .withReuse(true);
    }


    protected User addUserToDatabase() {
        UserDaoImpl userDao = new UserDaoImpl(connectionManager);
        userDao.setSchema(SCHEMA);
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
        HabitDaoImpl habitDao = new HabitDaoImpl(connectionManager);
        habitDao.setSchema(SCHEMA);
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

    protected void clearDatabase(UserDaoImpl userDao) {
        userDao.deleteAll();
    }

    protected static void migrateDatabase(String url, String username, String password) {
        try (Connection connection = connectionManager.getConnection(url, username, password, DRIVER_NAME)) {
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


