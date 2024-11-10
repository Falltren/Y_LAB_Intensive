package com.fallt.audit_starter.aop;

import com.fallt.audit_starter.domain.entity.enums.ActionType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация {@code Auditable} используется для пометки методов или классов,
 * которые должны быть отслежены для аудита действий пользователя.
 *
 * <p>Эта аннотация позволяет указать тип действия, выполняемого пользователем,
 * с помощью enum {@code ActionType}.</p>
 *
 *
 * <p>Пример использования:</p>
 * <pre>
 * {@code
 * @Auditable(action = ActionType.CREATE)
 * public void createUser (User user) {
 *     // Логика создания пользователя
 * }
 * }
 * </pre>
 *
 * @see ActionType
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Auditable {
    /**
     * Возвращает тип действия пользователя
     */
    ActionType action();
}
