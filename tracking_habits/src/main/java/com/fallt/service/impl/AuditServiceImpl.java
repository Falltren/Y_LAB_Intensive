package com.fallt.service.impl;

import com.fallt.entity.AuditLog;
import com.fallt.repository.AuditDao;
import com.fallt.service.AuditService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    private final AuditDao auditDao;

    public void save(AuditLog auditLog) {
        auditDao.save(auditLog);
    }
}
