package com.fallt.repository;

import com.fallt.domain.entity.AuditLog;

public interface AuditDao {

    void save(AuditLog auditLog);
}
