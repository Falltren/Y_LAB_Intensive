package com.fallt.util;

import com.fallt.repository.AuditDao;
import com.fallt.repository.HabitDao;
import com.fallt.repository.HabitExecutionDao;
import com.fallt.repository.UserDao;
import com.fallt.repository.impl.AuditDaoImpl;
import com.fallt.repository.impl.HabitDaoImpl;
import com.fallt.repository.impl.HabitExecutionDaoImpl;
import com.fallt.repository.impl.UserDaoImpl;
import com.fallt.security.AuthenticationContext;
import com.fallt.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

import static com.fallt.util.Constant.*;

/**
 * Класс, предназначенный для настройки компонентов приложения
 */
public class InstanceCreator implements ServletContextListener {

    private static ServletContext appServletContext;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContextListener.super.contextInitialized(sce);
        LiquibaseRunner liquibaseRunner = new LiquibaseRunner();
        liquibaseRunner.run();
        UserDao userDao = new UserDaoImpl();
        HabitDao habitDao = new HabitDaoImpl();
        HabitExecutionDao habitExecutionDao = new HabitExecutionDaoImpl();
        AuditDao auditDao = new AuditDaoImpl();
        UserService userService = new UserService(userDao);
        HabitService habitService = new HabitService(habitDao, habitExecutionDao, userService);
        AuthService authService = new AuthService(userService);
        AuditService auditService = new AuditService(auditDao);
        StatisticService statisticService = new StatisticService(habitService, userService);
        ValidationService validationService = new ValidationService();
        AuthenticationContext authenticationContext = new AuthenticationContext();


        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        ServletContext context = sce.getServletContext();
        context.setAttribute(OBJECT_MAPPER, objectMapper);
        context.setAttribute(HABIT_SERVICE, habitService);
        context.setAttribute(USER_SERVICE, userService);
        context.setAttribute(AUTH_SERVICE, authService);
        context.setAttribute(STATISTIC_SERVICE, statisticService);
        context.setAttribute(VALIDATION_SERVICE, validationService);
        context.setAttribute(AUTH_CONTEXT, authenticationContext);
        context.setAttribute(AUDIT_SERVICE, auditService);
        appServletContext = context;
    }

    public static ServletContext getServletContext() {
        return appServletContext;
    }
}
