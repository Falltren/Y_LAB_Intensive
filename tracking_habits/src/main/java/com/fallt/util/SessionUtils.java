package com.fallt.util;

import jakarta.servlet.http.HttpServletRequest;

public class SessionUtils {

    private SessionUtils() {
    }

    public static String getCurrentUserEmail(HttpServletRequest request) {
        return String.valueOf(request.getSession().getAttribute("user"));
    }
}
