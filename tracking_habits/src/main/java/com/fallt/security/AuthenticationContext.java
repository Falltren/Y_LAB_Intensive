package com.fallt.security;

import com.fallt.entity.Role;
import com.fallt.exception.AuthenticationException;
import com.fallt.exception.AuthorizationException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Класс предназначенный для хранения данных об аутентифицированных пользователях
 */
public class AuthenticationContext {

    private final Map<String, UserDetails> context = new ConcurrentHashMap<>();

    /**
     * Добавление пользователя в контекст аутентификации
     *
     * @param userDetails Класс, содержащий данные о пользователе
     */
    public void authenticate(UserDetails userDetails) {
        context.put(userDetails.getEmail(), userDetails);
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
     * @param email Электронный адрес пользователя
     */
    public void checkAuthentication(String email) {
        if (!context.containsKey(email)) {
            throw new AuthenticationException("Для выполнения данного действия вам необходимо аутентифицироваться");
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
            throw new AuthorizationException("У вас недостаточно прав для выполнения данного действия");
        }
    }

    public UserDetails getCurrentUser(){
        return context.values().stream().findFirst().orElse(null);
    }
}
