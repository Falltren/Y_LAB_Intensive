package com.fallt;

import com.fallt.in.UserInput;
import com.fallt.out.ConsoleOutput;
import com.fallt.service.*;

public class Main {
    public static void main(String[] args) {
        ConsoleOutput consoleOutput = new ConsoleOutput();
        UserInput userInput = new UserInput(consoleOutput);
        UserService userService = new UserService(consoleOutput);
        RegistrationService registrationService = new RegistrationService(userService, consoleOutput);
        AuthService authService = new AuthService(userService, consoleOutput);
        HabitService habitService = new HabitService(consoleOutput);
        StatisticService statisticService = new StatisticService();
        UserMenu userMenu = new UserMenu(consoleOutput, userInput, registrationService, authService, userService, habitService, statisticService);
        userMenu.start();
    }
}
