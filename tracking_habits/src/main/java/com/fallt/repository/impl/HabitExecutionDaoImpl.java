package com.fallt.repository.impl;

import com.fallt.entity.HabitExecution;
import com.fallt.exception.DBException;
import com.fallt.repository.HabitExecutionDao;
import com.fallt.util.DbConnectionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static com.fallt.util.Constant.INSERT_HABIT_EXECUTION_QUERY;

/**
 * Класс предназначен для взаимодействия с таблицей habit_execution посредствам SQL запросов
 */
@RequiredArgsConstructor
@Repository
public class HabitExecutionDaoImpl implements HabitExecutionDao {

    private final DbConnectionManager connectionManager;

    @Override
    public HabitExecution save(HabitExecution execution) {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_HABIT_EXECUTION_QUERY, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setObject(1, execution.getDate());
            preparedStatement.setLong(2, execution.getHabit().getId());
            preparedStatement.execute();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                long habitId = generatedKeys.getLong(1);
                execution.setId(habitId);
            }
            connectionManager.closeResultSet(generatedKeys);
            return execution;
        } catch (SQLException e) {
            throw new DBException(e.getMessage());
        }
    }
}
