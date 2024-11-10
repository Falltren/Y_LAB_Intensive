package com.fallt.repository.impl;

import com.fallt.domain.entity.Habit;
import com.fallt.domain.entity.enums.ExecutionRate;
import com.fallt.exception.DBException;
import com.fallt.repository.HabitDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.fallt.util.Constant.DELETE_HABIT;
import static com.fallt.util.Constant.FIND_ALL_HABITS_QUERY;
import static com.fallt.util.Constant.FIND_HABIT_BY_ID;
import static com.fallt.util.Constant.FIND_HABIT_BY_TITLE_AND_USER;
import static com.fallt.util.Constant.INSERT_HABIT_QUERY;
import static com.fallt.util.Constant.UPDATE_HABIT_QUERY;

/**
 * Класс предназначен для взаимодействия с таблицей habits посредствам SQL запросов
 */
@RequiredArgsConstructor
@Repository
public class HabitDaoImpl implements HabitDao {

    private final DataSource dataSource;

    @Override
    public Habit save(Habit habit) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_HABIT_QUERY, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, habit.getTitle());
            preparedStatement.setString(2, habit.getText());
            preparedStatement.setString(3, habit.getExecutionRate().name());
            preparedStatement.setObject(4, habit.getCreateAt());
            preparedStatement.setLong(5, habit.getUser().getId());
            preparedStatement.executeUpdate();
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    long habitId = generatedKeys.getLong(1);
                    habit.setId(habitId);
                }

            }
            return habit;
        } catch (SQLException e) {
            throw new DBException(e.getMessage());
        }
    }

    @Override
    public Habit update(Habit habit) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_HABIT_QUERY)) {
            preparedStatement.setString(1, habit.getTitle());
            preparedStatement.setString(2, habit.getText());
            preparedStatement.setString(3, habit.getExecutionRate().name());
            preparedStatement.setLong(4, habit.getId());
            preparedStatement.executeUpdate();
            return habit;
        } catch (SQLException e) {
            throw new DBException(e.getMessage());
        }
    }

    public List<Habit> getAllUserHabits(Long userId) {
        Map<Long, Habit> userHabits = new HashMap<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_HABITS_QUERY)) {
            preparedStatement.setLong(1, userId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Long id = resultSet.getLong("id");
                    if (userHabits.containsKey(id)) {
                        Habit habit = userHabits.get(id);
                        setExistsExecutionDate(habit, resultSet);
                    } else {
                        Habit habit = instantiateHabit(resultSet);
                        setExistsExecutionDate(habit, resultSet);
                        userHabits.put(id, habit);
                    }
                }
            }
            return new ArrayList<>(userHabits.values());
        } catch (SQLException e) {
            throw new DBException(e.getMessage());
        }
    }

    @Override
    public Optional<Habit> findByTitleAndUserId(Long userId, String title) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_HABIT_BY_TITLE_AND_USER)) {
            preparedStatement.setLong(1, userId);
            preparedStatement.setString(2, title);
            Habit habit = null;
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    if (habit == null) {
                        habit = instantiateHabit(resultSet);
                    } else {
                        setExistsExecutionDate(habit, resultSet);
                    }
                }
            }
            return Optional.ofNullable(habit);
        } catch (SQLException e) {
            throw new DBException(e.getMessage());
        }
    }

    @Override
    public Optional<Habit> findById(Long id) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_HABIT_BY_ID)) {
            preparedStatement.setLong(1, id);
            Habit habit = null;
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    if (habit == null) {
                        habit = instantiateHabit(resultSet);
                    } else {
                        setExistsExecutionDate(habit, resultSet);
                    }
                }
            }
            return Optional.ofNullable(habit);
        } catch (SQLException e) {
            throw new DBException(e.getMessage());
        }
    }

    @Override
    public void delete(Long id) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_HABIT)) {
            preparedStatement.setLong(1, id);
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new DBException(e.getMessage());
        }
    }

    private Habit instantiateHabit(ResultSet resultSet) throws SQLException {
        Habit habit = Habit.builder()
                .id(resultSet.getLong("id"))
                .title(resultSet.getString("title"))
                .text(resultSet.getString("text"))
                .executionRate(ExecutionRate.valueOf(resultSet.getString("execution_rate")))
                .createAt(resultSet.getObject("create_at", LocalDate.class))
                .build();
        setExistsExecutionDate(habit, resultSet);
        return habit;
    }

    private void setExistsExecutionDate(Habit habit, ResultSet resultSet) throws SQLException {
        LocalDate date = resultSet.getObject("date", LocalDate.class);
        if (date != null) {
            habit.getSuccessfulExecution().add(date);
        }
    }

}
