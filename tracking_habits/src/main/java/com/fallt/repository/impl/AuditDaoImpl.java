package com.fallt.repository.impl;

import com.fallt.entity.AuditLog;
import com.fallt.exception.DBException;
import com.fallt.repository.AuditDao;
import com.fallt.util.DbConnectionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import static com.fallt.util.Constant.INSERT_AUDIT_QUERY;

@Repository
@RequiredArgsConstructor
public class AuditDaoImpl implements AuditDao {

    private final DbConnectionManager connectionManager;

    @Override
    public void save(AuditLog auditLog) {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_AUDIT_QUERY, Statement.RETURN_GENERATED_KEYS)) {
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
