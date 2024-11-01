package com.fallt.repository.impl;

import com.fallt.entity.Habit;
import com.fallt.entity.HabitExecution;
import com.fallt.repository.AbstractTest;
import com.fallt.repository.HabitDao;
import com.fallt.repository.HabitExecutionDao;
import com.fallt.util.DbConnectionManager;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class HabitExecutionDaoImplTest extends AbstractTest {

    private HabitExecutionDaoImpl executionDao;

    private HabitDaoImpl habitDao;

    @BeforeAll
    static void beforeAll() {
        postgreSQLContainer.start();
        migrateDatabase(postgreSQLContainer.getJdbcUrl(), postgreSQLContainer.getUsername(), postgreSQLContainer.getPassword());
        connectionManager.setConnectionSettings(postgreSQLContainer.getJdbcUrl(), postgreSQLContainer.getUsername(), postgreSQLContainer.getPassword(), DRIVER_NAME);
    }

    @AfterAll
    static void afterAll() {
        postgreSQLContainer.stop();
    }

    @BeforeEach
    void setup() {
        executionDao = new HabitExecutionDaoImpl(connectionManager);
        habitDao = new HabitDaoImpl(connectionManager);
        executionDao.setSchema(SCHEMA);
        habitDao.setSchema(SCHEMA);
    }

    @Test
    @DisplayName("Отметка о выполнении привычки")
    void whenConfirmExecution_thenAddNewRecordToDatabase() {
        Habit habit = addHabitToDatabase();
        Long userId = habit.getUser().getId();
        HabitExecution habitExecution = HabitExecution.builder()
                .date(LocalDate.now())
                .habit(habit)
                .build();

        executionDao.save(habitExecution);

        List<Habit> habits = habitDao.getAllUserHabits(userId);

        assertThat(habits.get(0).getSuccessfulExecution()).hasSize(1);
    }
}
