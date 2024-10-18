package com.fallt.repository.impl;

import com.fallt.entity.HabitExecution;
import com.fallt.exception.DBException;
import com.fallt.repository.HabitExecutionDao;
import com.fallt.util.DBUtils;
import lombok.RequiredArgsConstructor;

import java.sql.*;

/**
 * Класс предназначен для взаимодействия с таблицей habit_execution посредствам SQL запросов
 */
@RequiredArgsConstructor
public class HabitExecutionDaoImpl implements HabitExecutionDao {

    private final Connection connection;

    @Override
    public void save(HabitExecution execution) {
        String sql = "INSERT INTO habit_execution (date, habit_id) VALUES (?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setObject(1, execution.getDate());
            preparedStatement.setLong(2, execution.getHabit().getId());
            preparedStatement.execute();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                long habitId = generatedKeys.getLong(1);
                execution.setId(habitId);
            }
            connection.commit();
            DBUtils.closeResultSet(generatedKeys);
        } catch (SQLException e) {
            throw new DBException(e.getMessage());
        }
    }
}
