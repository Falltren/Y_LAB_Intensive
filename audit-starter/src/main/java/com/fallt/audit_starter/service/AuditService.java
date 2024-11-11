package com.fallt.audit_starter.service;

import com.fallt.audit_starter.domain.entity.AuditLog;

public interface AuditService {

    /**
     * Сохраняет действие пользователя
     *
     * @param auditLog Объект, описывающий действие пользователя
     */
    void save(AuditLog auditLog);

}
