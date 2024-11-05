package com.fallt.repository.impl;

import com.fallt.entity.AuditLog;
import com.fallt.exception.DBException;
import com.fallt.repository.AuditDao;
import com.fallt.util.DbConnectionManager;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

@Repository
@RequiredArgsConstructor
public class AuditDaoImpl implements AuditDao {

    private final DbConnectionManager connectionManager;

    @Setter
    @Value("${spring.liquibase.default-schema}")
    private String schema;

    @Override
    public void save(AuditLog auditLog) {
        String sql = "INSERT INTO " + schema + ".audit (email, action, description, date) VALUES (?, ?, ?, ?)";
        try (Connection connection = connectionManager.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, auditLog.getUserEmail());
            preparedStatement.setString(2, auditLog.getAction().name());
            preparedStatement.setString(3, auditLog.getDescription());
            preparedStatement.setObject(4, auditLog.getDate());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DBException(e.getMessage());
        }
    }
}
