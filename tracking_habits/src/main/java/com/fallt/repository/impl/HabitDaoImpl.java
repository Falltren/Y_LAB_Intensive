package com.fallt.repository.impl;

import com.fallt.entity.ExecutionRate;
import com.fallt.entity.Habit;
import com.fallt.exception.DBException;
import com.fallt.repository.HabitDao;
import com.fallt.util.DBUtils;
import com.fallt.util.Fetch;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@RequiredArgsConstructor
public class HabitDaoImpl implements HabitDao {

    private final Connection connection;

    @Override
    public void save(Habit habit) {
        String sql = "INSERT INTO my_schema.habits (title, text, execution_rate, create_at, user_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
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
            connection.commit();
            DBUtils.closeResultSet(generatedKeys);
        } catch (SQLException e) {
            throw new DBException(e.getMessage());
        }
    }

    @Override
    public void update(Habit habit) {
        String sql = "UPDATE my_schema.habits SET title = ?, text = ?, execution_rate = ? WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, habit.getTitle());
            preparedStatement.setString(2, habit.getText());
            preparedStatement.setString(3, habit.getExecutionRate().name());
            preparedStatement.setLong(4, habit.getId());
            preparedStatement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            throw new DBException(e.getMessage());
        }
    }

    @Override
    public List<Habit> getAllUserHabits(Long userId, Fetch fetchType) {
        if (fetchType.equals(Fetch.LAZY)) {
            return getAllHabitsWithoutExecutions(userId);
        } else {
            return getAllHabitsWithExecutions(userId);
        }
    }

    public List<Habit> getAllHabitsWithoutExecutions(Long userId) {
        List<Habit> habits = new ArrayList<>();
        String sql = "SELECT * FROM my_schema.habits WHERE user_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Habit habit = instantiateHabit(resultSet);
                habits.add(habit);
            }
            DBUtils.closeResultSet(resultSet);
        } catch (SQLException e) {
            throw new DBException(e.getMessage());
        }
        return habits;
    }

    public List<Habit> getAllHabitsWithExecutions(Long userId) {
        String sql = """
                SELECT h.*, e.date
                FROM my_schema.habits h
                LEFT JOIN my_schema.habit_execution e
                ON e.habit_id = h.id
                WHERE h.user_id = ?
                """;
        Map<Long, Habit> userHabits = new HashMap<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                if (userHabits.containsKey(id)) {
                    Habit habit = userHabits.get(id);
                    setExistsExecutionDate(habit, resultSet);
                } else {
                    Habit habit = instantiateHabit(resultSet);
                    userHabits.put(id, habit);
                }
            }
            return new ArrayList<>(userHabits.values());
        } catch (SQLException e) {
            throw new DBException(e.getMessage());
        }
    }

    @Override
    public Optional<Habit> findHabitByTitleAndUserId(Long userId, String title) {
        String sql = """
                SELECT * 
                FROM my_schema.habits h 
                LEFT JOIN my_schema.habit_execution e 
                ON e.habit_id = h.id 
                WHERE h.user_id = ? AND h.title = ?
                """;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
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
    public void delete(Long id, String title) {
        String sql = "DELETE FROM my_schema.habits WHERE id = ? AND title = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);
            preparedStatement.setString(1, title);
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
