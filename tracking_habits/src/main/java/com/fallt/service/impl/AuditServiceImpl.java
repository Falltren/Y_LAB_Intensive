package com.fallt.service.impl;

import com.fallt.domain.entity.AuditLog;
import com.fallt.repository.AuditDao;
import com.fallt.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuditServiceImpl implements AuditService {

    private final AuditDao auditDao;

    public void save(AuditLog auditLog) {
        auditDao.save(auditLog);
    }
}
