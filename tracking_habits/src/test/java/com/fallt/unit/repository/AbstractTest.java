package com.fallt.unit.repository;

import com.fallt.domain.entity.enums.ExecutionRate;
import com.fallt.domain.entity.Habit;
import com.fallt.domain.entity.enums.Role;
import com.fallt.domain.entity.User;
import com.fallt.repository.impl.HabitDaoImpl;
import com.fallt.repository.impl.UserDaoImpl;
import com.fallt.util.DbConnectionManager;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.postgresql.ds.PGSimpleDataSource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Testcontainers
public abstract class AbstractTest {

    protected static final String DRIVER_NAME = "org.postgresql.Driver";

    protected static PostgreSQLContainer postgreSQLContainer;
    protected static DbConnectionManager connectionManager = new DbConnectionManager();
    protected static DataSource dataSource = new PGSimpleDataSource();

    static {
        DockerImageName postgres = DockerImageName.parse("postgres:15.4");
        postgreSQLContainer = new PostgreSQLContainer<>(postgres)
                .withUsername("testUser")
                .withPassword("testPassword")
                .withDatabaseName("testBase")
                .withReuse(true);
    }


    protected User addUserToDatabase() {
        UserDaoImpl userDao = new UserDaoImpl(dataSource);
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
        HabitDaoImpl habitDao = new HabitDaoImpl(dataSource);
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


