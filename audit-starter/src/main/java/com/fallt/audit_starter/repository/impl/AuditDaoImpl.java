package com.fallt.audit_starter.repository.impl;


import com.fallt.audit_starter.domain.entity.AuditLog;
import com.fallt.audit_starter.repository.AuditDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import static com.fallt.audit_starter.repository.Constant.INSERT_AUDIT_QUERY;

@Repository
@RequiredArgsConstructor
@Slf4j
public class AuditDaoImpl implements AuditDao {

    private final DataSource dataSource;

    @Override
    public void save(AuditLog auditLog) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_AUDIT_QUERY, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, auditLog.getUserEmail());
            preparedStatement.setString(2, auditLog.getAction().name());
            preparedStatement.setString(3, auditLog.getDescription());
            preparedStatement.setObject(4, auditLog.getDate());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            log.error("Ошибка при выполнении запроса {}", e.getMessage());
        }
    }
}
