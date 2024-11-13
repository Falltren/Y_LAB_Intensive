package com.fallt.repository.impl;

import com.fallt.domain.entity.Habit;
import com.fallt.domain.entity.HabitExecution;
import com.fallt.repository.AbstractTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
