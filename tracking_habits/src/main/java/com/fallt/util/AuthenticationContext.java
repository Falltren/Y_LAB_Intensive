package com.fallt.util;

import com.fallt.entity.Role;
import com.fallt.exception.SecurityException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AuthenticationContext {

    private final Map<String, UserDetails> context = new ConcurrentHashMap<>();

    public void authenticate(UserDetails userDetails) {
        context.put(userDetails.getEmail(), userDetails);
    }

    public void logout(String email) {
        context.remove(email);
    }

    public void checkAuthentication(String email) {
        if (!context.containsKey(email)) {
            throw new SecurityException("Для выполнения данного действия вам необходимо аутентифицироваться");
        }
    }

    public void checkRole(String userEmail, Role requiredRole){
        checkAuthentication(userEmail);
        Role currentUserRole = context.get(userEmail).getRole();
        if (!currentUserRole.equals(requiredRole)){
            throw new SecurityException("У вас недостаточно прав для выполнения данного действия");
        }
    }
}
