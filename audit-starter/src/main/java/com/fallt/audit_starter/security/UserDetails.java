package com.fallt.audit_starter.security;

/**
 * Предоставляет уникальные данные о пользователе, которые будут
 * использоваться при аудите действий пользователя
 */
public interface UserDetails {

    String getUserName();

}
