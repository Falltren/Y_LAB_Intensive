package com.fallt.audit_starter.service.impl;

import com.fallt.audit_starter.domain.entity.AuditLog;
import com.fallt.audit_starter.repository.AuditDao;
import com.fallt.audit_starter.service.AuditService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    private final AuditDao auditDao;

    public void save(AuditLog auditLog) {
        auditDao.save(auditLog);
    }

}
