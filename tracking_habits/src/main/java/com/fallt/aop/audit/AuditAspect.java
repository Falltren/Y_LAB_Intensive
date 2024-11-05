package com.fallt.aop.audit;

import com.fallt.entity.AuditLog;
import com.fallt.security.AuthenticationContext;
import com.fallt.security.UserDetails;
import com.fallt.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

    private final AuthenticationContext authenticationContext;

    private final AuditService auditService;

    @Before("@annotation(auditable) && execution(* *(..))")
    public void auditing(JoinPoint joinPoint, Auditable auditable) {
        String methodName = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
        ActionType action = auditable.action();
        UserDetails userDetails = authenticationContext.getCurrentUser();
        AuditLog audit = AuditLog.builder()
                .action(action)
                .userEmail(getUserEmail(userDetails))
                .description("User called method: " + methodName)
                .build();
        auditService.save(audit);
    }

    private String getUserEmail(UserDetails userDetails) {
        return userDetails == null ? "anonymous" : userDetails.getEmail();
    }
}
