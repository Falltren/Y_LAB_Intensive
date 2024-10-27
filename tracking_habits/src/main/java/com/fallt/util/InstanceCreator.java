package com.fallt.util;

import com.fallt.out.ConsoleOutput;
import com.fallt.repository.HabitDao;
import com.fallt.repository.HabitExecutionDao;
import com.fallt.repository.UserDao;
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

public class InstanceCreator implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContextListener.super.contextInitialized(sce);
        LiquibaseRunner liquibaseRunner = new LiquibaseRunner();
        liquibaseRunner.run();
        ConsoleOutput consoleOutput = new ConsoleOutput();
        UserDao userDao = new UserDaoImpl();
        HabitDao habitDao = new HabitDaoImpl();
        HabitExecutionDao habitExecutionDao = new HabitExecutionDaoImpl();
        UserService userService = new UserService(userDao, consoleOutput);
        HabitService habitService = new HabitService(habitDao, habitExecutionDao, userService);
        AuthService authService = new AuthService(userService);
        StatisticService statisticService = new StatisticService(habitService, userService);
        ValidationService validationService = new ValidationService();
        AuthenticationContext authenticationContext = new AuthenticationContext();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        ServletContext context = sce.getServletContext();
        context.setAttribute("objectMapper", objectMapper);
        context.setAttribute("habitService", habitService);
        context.setAttribute("userService", userService);
        context.setAttribute("authService", authService);
        context.setAttribute("statisticService", statisticService);
        context.setAttribute("validationService", validationService);
        context.setAttribute("authContext", authenticationContext);
    }
}