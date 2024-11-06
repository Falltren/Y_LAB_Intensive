package com.fallt.repository.impl;

import com.fallt.entity.ExecutionRate;
import com.fallt.entity.Habit;
import com.fallt.entity.User;
import com.fallt.repository.AbstractTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.fallt.TestConstant.FIRST_HABIT_TITLE;
import static com.fallt.TestConstant.SECOND_HABIT_TITLE;
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
    }

    @Test
    @DisplayName("Создание привычки")
    void whenCreateHabit_thenAddHabitToDataBase() {
        Habit habit = createHabit(FIRST_HABIT_TITLE);
        habit.setUser(user);
        habitDao.save(habit);

        List<Habit> habits = habitDao.getAllUserHabits(user.getId());

        assertThat(habits).hasSize(1);
    }

    @Test
    @DisplayName("Получение привычки по названию")
    void whenGetHabitByTitle_thenGetHabitFromDatabase() {
        Habit habit = createHabit(SECOND_HABIT_TITLE);
        habit.setUser(user);
        habitDao.save(habit);

        Optional<Habit> existedHabit = habitDao.findHabitByTitleAndUserId(user.getId(), SECOND_HABIT_TITLE);

        assertThat(existedHabit).isPresent();
        assertThat(existedHabit.get().getTitle()).isEqualTo(SECOND_HABIT_TITLE);
    }

    @Test
    @DisplayName("Получение всех привычек пользователя")
    void whenGetAllHabits_thenReturnListFromDatabase() {
        Habit habit1 = createHabit(FIRST_HABIT_TITLE);
        habit1.setUser(user);
        Habit habit2 = createHabit(SECOND_HABIT_TITLE);
        habit2.setUser(user);
        habitDao.save(habit1);
        habitDao.save(habit2);

        List<Habit> habits = habitDao.getAllUserHabits(user.getId());

        assertThat(habits).hasSize(2);
        assertThat(habits.get(0).getTitle()).isEqualTo(FIRST_HABIT_TITLE);
        assertThat(habits.get(1).getTitle()).isEqualTo(SECOND_HABIT_TITLE);
    }

    @Test
    @DisplayName("Редактирование привычки")
    void whenUpdateHabit_thenUpdateDataInDatabase() {
        Habit habit = createHabit(FIRST_HABIT_TITLE);
        habit.setUser(user);
        Habit existedHabit = habitDao.save(habit);
        existedHabit.setTitle(SECOND_HABIT_TITLE);
        habitDao.update(existedHabit);

        Optional<Habit> optionalHabit = habitDao.findHabitByTitleAndUserId(user.getId(), SECOND_HABIT_TITLE);

        assertThat(optionalHabit).isPresent();
    }

    @Test
    @DisplayName("Удаление привычки")
    void whenDeleteHabit_thenRemoveFromDatabase() {
        Habit habit1 = createHabit(FIRST_HABIT_TITLE);
        habit1.setUser(user);
        Habit habit2 = createHabit(SECOND_HABIT_TITLE);
        habit2.setUser(user);
        habitDao.save(habit1);
        habitDao.save(habit2);
        habitDao.delete(user.getId(), FIRST_HABIT_TITLE);

        List<Habit> habits = habitDao.getAllUserHabits(user.getId());

        assertThat(habits).hasSize(1);
        assertThat(habits.get(0).getTitle()).isEqualTo(SECOND_HABIT_TITLE);
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
