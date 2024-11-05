package com.fallt.repository.impl;

import com.fallt.entity.AuditLog;
import com.fallt.exception.DBException;
import com.fallt.repository.AuditDao;
import com.fallt.util.DBUtils;
import com.fallt.util.PropertiesUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class AuditDaoImpl implements AuditDao {

    private static final String SCHEMA_NAME = PropertiesUtil.getProperty("defaultSchema") + ".";

    @Override
    public void save(AuditLog auditLog) {
        String sql = "INSERT INTO " + SCHEMA_NAME + "audit (email, action, description, date) VALUES (?, ?, ?, ?)";
        try (Connection connection = DBUtils.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
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
