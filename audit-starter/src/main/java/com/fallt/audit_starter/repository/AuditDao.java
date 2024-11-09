package com.fallt.audit_starter.repository;

import com.fallt.audit_starter.domain.entity.AuditLog;

public interface AuditDao {

    void save(AuditLog auditLog);
}
