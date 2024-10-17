package com.fallt;

import com.fallt.in.UserInput;
import com.fallt.out.ConsoleOutput;
import com.fallt.repository.HabitDao;
import com.fallt.repository.HabitExecutionDao;
import com.fallt.repository.UserDao;
import com.fallt.repository.impl.HabitDaoImpl;
import com.fallt.repository.impl.HabitExecutionDaoImpl;
import com.fallt.repository.impl.UserDaoImpl;
import com.fallt.service.*;
import com.fallt.util.DBUtils;
import com.fallt.util.LiquibaseRunner;

public class Main {
    public static void main(String[] args) {
        LiquibaseRunner liquibaseRunner = new LiquibaseRunner();
        liquibaseRunner.run();
        ConsoleOutput consoleOutput = new ConsoleOutput();
        UserDao userDao = new UserDaoImpl(DBUtils.getConnection());
        HabitDao habitDao = new HabitDaoImpl(DBUtils.getConnection());
        HabitExecutionDao executionDao = new HabitExecutionDaoImpl(DBUtils.getConnection());
        UserInput userInput = new UserInput(consoleOutput);
        UserService userService = new UserService(userDao, consoleOutput);
        RegistrationService registrationService = new RegistrationService(userService, consoleOutput);
        AuthService authService = new AuthService(userService, consoleOutput);
        HabitService habitService = new HabitService(consoleOutput, habitDao, executionDao);
        StatisticService statisticService = new StatisticService();
        UserMenu userMenu = new UserMenu(consoleOutput, userInput, registrationService, authService, userService, habitService, statisticService);
        userMenu.start();
    }
}
