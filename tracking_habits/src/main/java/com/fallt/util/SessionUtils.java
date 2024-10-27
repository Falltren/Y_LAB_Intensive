package com.fallt.util;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Утилитный класс для получения данных о текущем пользователе из сессии
 */
public class SessionUtils {

    private SessionUtils() {
    }

    public static String getCurrentUserEmail(HttpServletRequest request) {
        return String.valueOf(request.getSession().getAttribute("user"));
    }
}
