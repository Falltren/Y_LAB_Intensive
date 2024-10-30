package com.fallt.util;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Класс для получения данных о текущей id сессии
 */
@Component
public class SessionUtils {

    public String getSessionIdFromContext() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new IllegalStateException("Сессия не найдена");
        }
        return attributes.getRequest().getSession().getId();
    }

}
