package com.fallt.audit_starter.aop;

import com.fallt.audit_starter.config.AuditConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация {@code EnableAudit} предназначена для включения введения аудита
 * действий пользователя.
 * <p>Для фиксирования действий пользователя необходимо
 * использовать аннотацию над соответствующим методом, классом.</p>
 * @see Auditable
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(AuditConfig.class)
public @interface EnableAudit {
}
