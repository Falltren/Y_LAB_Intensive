package com.fallt.repository;

import com.fallt.entity.AuditLog;

public interface AuditDao {

    void save(AuditLog auditLog);
}
