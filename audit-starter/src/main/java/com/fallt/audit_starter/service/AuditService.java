package com.fallt.audit_starter.service;

import com.fallt.audit_starter.domain.entity.AuditLog;

public interface AuditService {

    /**
     * Сохраняет действие пользователя
     *
     * @param auditLog Действие пользователя, включающее вызываемый метод, время
     */
    void save(AuditLog auditLog);
}
