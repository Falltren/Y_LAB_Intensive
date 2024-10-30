package com.fallt.security;

import com.fallt.entity.Role;
import com.fallt.exception.SecurityException;
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
            throw new SecurityException("Для выполнения данного действия вам необходимо аутентифицироваться");
        }
    }

    /**
     * Проверка наличия у пользователя требуемой роли
     *
     * @param userEmail    Электронный адрес пользователя
     * @param requiredRole Требуемая роль
     */
    public void checkRole(String userEmail, Role requiredRole) {
        checkAuthentication(userEmail);
        Role currentUserRole = context.get(userEmail).getRole();
        if (!currentUserRole.equals(requiredRole)) {
            throw new SecurityException("У вас недостаточно прав для выполнения данного действия"); // при использовании spring будет приводить к статусу 403 в ответе
        }
    }

    public String getEmailCurrentUser(String sessionId) {
        return context.get(sessionId).getEmail();
    }
}
