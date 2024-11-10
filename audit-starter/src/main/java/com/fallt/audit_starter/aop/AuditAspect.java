package com.fallt.audit_starter.aop;

import com.fallt.audit_starter.domain.entity.AuditLog;
import com.fallt.audit_starter.domain.entity.enums.ActionType;
import com.fallt.audit_starter.security.UserDetails;
import com.fallt.audit_starter.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

@Aspect
@RequiredArgsConstructor
public class AuditAspect {

    private final AuditService auditService;
    private final UserDetails userDetails;

    @Before("@annotation(auditable) && execution(* *(..))")
    public void auditing(JoinPoint joinPoint, Auditable auditable) {
        String methodName = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
        ActionType action = auditable.action();
        AuditLog audit = AuditLog.builder()
                .action(action)
                .userEmail(userDetails.getUserName())
                .description("User called method: " + methodName)
                .build();
        auditService.save(audit);
    }

}
