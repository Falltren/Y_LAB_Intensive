package com.fallt.repository.impl;

import com.fallt.entity.ExecutionRate;
import com.fallt.entity.Habit;
import com.fallt.entity.User;
import com.fallt.repository.AbstractTest;
import com.fallt.repository.HabitDao;
import com.fallt.util.DbConnectionManager;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class HabitDaoImplTest extends AbstractTest {

    private HabitDaoImpl habitDao;

    private User user;

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
        habitDao = new HabitDaoImpl(connectionManager);
        user = addUserToDatabase();
        habitDao.setSchema(SCHEMA);
    }

    @Test
    @DisplayName("Создание привычки")
    void whenCreateHabit_thenAddHabitToDataBase() {
        String title = "new habit";
        Habit habit = createHabit(title);
        habit.setUser(user);
        habitDao.save(habit);

        List<Habit> habits = habitDao.getAllUserHabits(user.getId());

        assertThat(habits).hasSize(1);
    }

    @Test
    @DisplayName("Получение привычки по названию")
    void whenGetHabitByTitle_thenGetHabitFromDatabase() {
        String title = "new habit";
        Habit habit = createHabit(title);
        habit.setUser(user);
        habitDao.save(habit);

        Optional<Habit> existedHabit = habitDao.findHabitByTitleAndUserId(user.getId(), title);

        assertThat(existedHabit).isPresent();
        assertThat(existedHabit.get().getTitle()).isEqualTo(title);
    }

    @Test
    @DisplayName("Получение всех привычек пользователя")
    void whenGetAllHabits_thenReturnListFromDatabase() {
        Habit habit1 = createHabit("habit1");
        habit1.setUser(user);
        Habit habit2 = createHabit("habit2");
        habit2.setUser(user);
        habitDao.save(habit1);
        habitDao.save(habit2);

        List<Habit> habits = habitDao.getAllUserHabits(user.getId());

        assertThat(habits).hasSize(2);
        assertThat(habits.get(0).getTitle()).isEqualTo("habit1");
        assertThat(habits.get(1).getTitle()).isEqualTo("habit2");
    }

    @Test
    @DisplayName("Редактирование привычки")
    void whenUpdateHabit_thenUpdateDataInDatabase() {
        String title = "habit";
        Habit habit = createHabit(title);
        habit.setUser(user);
        Habit existedHabit = habitDao.save(habit);
        existedHabit.setTitle("newTitle");
        habitDao.update(existedHabit);

        Optional<Habit> optionalHabit = habitDao.findHabitByTitleAndUserId(user.getId(), "newTitle");

        assertThat(optionalHabit).isPresent();
    }

    @Test
    @DisplayName("Удаление привычки")
    void whenDeleteHabit_thenRemoveFromDatabase() {
        Habit habit1 = createHabit("habit1");
        habit1.setUser(user);
        Habit habit2 = createHabit("habit2");
        habit2.setUser(user);
        habitDao.save(habit1);
        habitDao.save(habit2);
        habitDao.delete(user.getId(), "habit1");

        List<Habit> habits = habitDao.getAllUserHabits(user.getId());

        assertThat(habits).hasSize(1);
        assertThat(habits.get(0).getTitle()).isEqualTo("habit2");
    }

    private Habit createHabit(String title) {
        return Habit.builder()
                .title(title)
                .text("habit")
                .createAt(LocalDate.now())
                .executionRate(ExecutionRate.WEEKLY)
                .user(new User())
                .build();
    }
}
