package com.fallt.audit_starter.domain.entity.enums;

import com.fallt.audit_starter.aop.Auditable;

/**
 * Обозначает соответствующим тип при ведении аудита действий пользователя
 *
 * @see Auditable
 */
public enum ActionType {
    CREATE, UPDATE, DELETE, LOGIN, GET
}
