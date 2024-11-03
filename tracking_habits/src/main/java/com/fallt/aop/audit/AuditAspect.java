package com.fallt.aop.audit;

import com.fallt.entity.AuditLog;
import com.fallt.security.AuthenticationContext;
import com.fallt.security.UserDetails;
import com.fallt.service.AuditService;
import com.fallt.util.InstanceCreator;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

@Aspect
public class AuditAspect {

    @Before("@annotation(auditable) && execution(* *(..))")
    public void auditing(JoinPoint joinPoint, Auditable auditable) {
        String methodName = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
        AuthenticationContext context = (AuthenticationContext) InstanceCreator.getServletContext().getAttribute("authContext");
        AuditService auditService = (AuditService) InstanceCreator.getServletContext().getAttribute("auditService");
        ActionType action = auditable.action();
        UserDetails userDetails = context.getCurrentUser();
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
