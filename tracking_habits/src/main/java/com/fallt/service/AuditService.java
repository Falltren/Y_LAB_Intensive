package com.fallt.service;

import com.fallt.entity.AuditLog;
import com.fallt.repository.AuditDao;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AuditService {

    private final AuditDao auditDao;

    public void save(AuditLog auditLog) {
        auditDao.save(auditLog);
    }
}
