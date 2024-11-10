package com.fallt.audit_starter.config;

import com.fallt.audit_starter.aop.AuditAspect;
import com.fallt.audit_starter.condition.EnableAuditCondition;
import com.fallt.audit_starter.repository.AuditDao;
import com.fallt.audit_starter.repository.impl.AuditDaoImpl;
import com.fallt.audit_starter.security.UserDetails;
import com.fallt.audit_starter.service.AuditService;
import com.fallt.audit_starter.service.impl.AuditServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import javax.sql.DataSource;

@Configuration
@EnableAspectJAutoProxy
@RequiredArgsConstructor
@Conditional(EnableAuditCondition.class)
public class AuditConfig {

    @Bean
    public AuditDao auditDao(DataSource dataSource) {
        return new AuditDaoImpl(dataSource);
    }

    @Bean
    public AuditService auditService(AuditDao auditDao) {
        return new AuditServiceImpl(auditDao);
    }

    @Bean
    public AuditAspect auditAspect(AuditService auditService, UserDetails userDetails) {
        return new AuditAspect(auditService, userDetails);
    }

}
