package com.fallt.audit_starter.config;

import com.fallt.audit_starter.condition.EnableAuditCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
@RequiredArgsConstructor
@Conditional(EnableAuditCondition.class)
public class AuditConfig {

}
