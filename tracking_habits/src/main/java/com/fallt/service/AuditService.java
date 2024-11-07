package com.fallt.service;

import com.fallt.domain.entity.AuditLog;

public interface AuditService {

    /**
     * Сохраняет действие пользователя
     * @param auditLog Действие пользователя, включающее вызываемый метод, время
     */
    void save(AuditLog auditLog);
}
