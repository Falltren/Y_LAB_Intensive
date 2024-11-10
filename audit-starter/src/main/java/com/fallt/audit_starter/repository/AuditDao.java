package com.fallt.audit_starter.repository;

import com.fallt.audit_starter.domain.entity.AuditLog;

/**
 * Предназначен для взаимодействия с базой данных
 */
public interface AuditDao {

    void save(AuditLog auditLog);

}
