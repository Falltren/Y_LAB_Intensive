package com.fallt.repository.impl;

import com.fallt.entity.ExecutionRate;
import com.fallt.entity.Habit;
import com.fallt.exception.DBException;
import com.fallt.repository.HabitDao;
import com.fallt.util.DBUtils;
import com.fallt.util.PropertiesUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

/**
 * Класс предназначен для взаимодействия с таблицей habits посредствам SQL запросов
 */
@RequiredArgsConstructor
@Repository
public class HabitDaoImpl implements HabitDao {

    private static final String SCHEMA_NAME = PropertiesUtil.getProperty("defaultSchema") + ".";

    @Override
    public Habit save(Habit habit) {
        String sql = "INSERT INTO " + SCHEMA_NAME + "habits (title, text, execution_rate, create_at, user_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DBUtils.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, habit.getTitle());
            preparedStatement.setString(2, habit.getText());
            preparedStatement.setString(3, habit.getExecutionRate().name());
            preparedStatement.setObject(4, habit.getCreateAt());
            preparedStatement.setLong(5, habit.getUser().getId());
            preparedStatement.executeUpdate();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                long habitId = generatedKeys.getLong(1);
                habit.setId(habitId);
            }
            DBUtils.closeResultSet(generatedKeys);
            return habit;
        } catch (SQLException e) {
            throw new DBException(e.getMessage());
        }
    }

    @Override
    public Habit update(Habit habit) {
        String sql = "UPDATE " + SCHEMA_NAME + "habits SET title = ?, text = ?, execution_rate = ? WHERE id = ?";
        try (Connection connection = DBUtils.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
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
        String sql = "SELECT h.*, e.date FROM " + SCHEMA_NAME + "habits h LEFT JOIN " +
                SCHEMA_NAME + "habit_execution e ON e.habit_id = h.id WHERE h.user_id = ?";
        Map<Long, Habit> userHabits = new HashMap<>();
        try (Connection connection = DBUtils.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
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
            DBUtils.closeResultSet(resultSet);
            return new ArrayList<>(userHabits.values());
        } catch (SQLException e) {
            throw new DBException(e.getMessage());
        }
    }

    @Override
    public Optional<Habit> findHabitByTitleAndUserId(Long userId, String title) {
        String sql = "SELECT * FROM " + SCHEMA_NAME + "habits h LEFT JOIN " +
                SCHEMA_NAME + "habit_execution e ON e.habit_id = h.id WHERE h.user_id = ? AND h.title = ?";
        try (Connection connection = DBUtils.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, userId);
            preparedStatement.setString(2, title);
            ResultSet resultSet = preparedStatement.executeQuery();
            Habit habit = null;
            while (resultSet.next()) {
                if (habit == null) {
                    habit = instantiateHabit(resultSet);
                } else {
                    setExistsExecutionDate(habit, resultSet);
                }
            }
            DBUtils.closeResultSet(resultSet);
            return Optional.ofNullable(habit);
        } catch (SQLException e) {
            throw new DBException(e.getMessage());
        }
    }

    @Override
    public void delete(Long userId, String title) {
        String sql = "DELETE FROM " + SCHEMA_NAME + "habits WHERE user_id = ? AND title = ?";
        try (Connection connection = DBUtils.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, userId);
            preparedStatement.setString(2, title);
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new DBException(e.getMessage());
        }
    }

    private Habit instantiateHabit(ResultSet resultSet) throws SQLException {
        return Habit.builder()
                .id(resultSet.getLong("id"))
                .title(resultSet.getString("title"))
                .text(resultSet.getString("text"))
                .executionRate(ExecutionRate.valueOf(resultSet.getString("execution_rate")))
                .createAt(resultSet.getObject("create_at", LocalDate.class))
                .build();
    }

    private void setExistsExecutionDate(Habit habit, ResultSet resultSet) throws SQLException {
        LocalDate date = resultSet.getObject("date", LocalDate.class);
        if (date != null) {
            habit.getSuccessfulExecution().add(date);
        }
    }
}
