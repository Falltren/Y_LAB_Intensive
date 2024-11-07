package com.fallt.security;

import com.fallt.domain.entity.enums.Role;
import com.fallt.exception.AuthenticationException;
import com.fallt.exception.AuthorizationException;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Класс предназначенный для хранения данных об аутентифицированных пользователях
 */
@Component
public class AuthenticationContext {

    private final Map<String, UserDetails> context = new ConcurrentHashMap<>();

    /**
     * Добавление пользователя в контекст аутентификации
     *
     * @param userDetails Класс, содержащий данные о пользователе
     */
    public void authenticate(String sessionId, UserDetails userDetails) {
        context.put(sessionId, userDetails);
    }

    /**
     * Удаление пользователя из контекста аутентификации
     *
     * @param email Электронный адрес пользователя
     */
    public void logout(String email) {
        context.remove(email);
    }

    /**
     * Проверка наличия пользователя в контексте аутентификации
     *
     * @param sessionId Id сессии
     */
    public void checkAuthentication(String sessionId) {
        if (!context.containsKey(sessionId)) {
            throw new AuthenticationException("Для выполнения данного действия вам необходимо аутентифицироваться");
        }
    }

    /**
     * Проверка наличия у пользователя требуемой роли
     *
     * @param sessionId    Электронный адрес пользователя
     * @param requiredRole Требуемая роль
     */
    public void checkRole(String sessionId, Role requiredRole) {
        checkAuthentication(sessionId);
        Role currentUserRole = context.get(sessionId).getRole();
        if (!currentUserRole.equals(requiredRole)) {
            throw new AuthorizationException("У вас недостаточно прав для выполнения данного действия");
        }
    }

    public String getEmailCurrentUser(String sessionId) {
        return context.get(sessionId).getEmail();
    }

    public UserDetails getCurrentUser() {
        return context.values().stream().findFirst().orElse(null);
    }
}
