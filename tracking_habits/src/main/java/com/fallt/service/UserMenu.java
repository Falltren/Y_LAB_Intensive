//package com.fallt.service;
//
//import com.fallt.dto.request.HabitDto;
//import com.fallt.dto.request.UpsertUserRequest;
//import com.fallt.entity.ExecutionRate;
//import com.fallt.entity.Habit;
//import com.fallt.entity.Role;
//import com.fallt.entity.User;
//import com.fallt.in.UserInput;
//import com.fallt.out.ConsoleOutput;
//import com.fallt.util.DateHandler;
//import com.fallt.util.Fetch;
//import com.fallt.util.Message;
//import lombok.RequiredArgsConstructor;
//
//import java.time.LocalDate;
//
///**
// * Содержит логику перехода между различными меню программы
// */
//@RequiredArgsConstructor
//public class UserMenu {
//
//    private boolean isStop;
//
//    private final ConsoleOutput consoleOutput;
//
//    private final UserInput userInput;
//
//    private final RegistrationService registrationService;
//
//    private final AuthService authService;
//
//    private final UserService userService;
//
//    private final HabitService habitService;
//
//    private final StatisticService statisticService;
//
//    /**
//     * Запуск
//     */
//    public void start() {
//        while (!isStop) {
//            consoleOutput.printMessage(Message.MAIN_MENU);
//            String selection = userInput.getUserInput();
//            switch (selection) {
//                case "1" -> registerMenu();
//                case "2" -> authenticationMenu();
//                case "0" -> isStop = true;
//                default -> consoleOutput.printMessage(Message.INCORRECT_MENU_NUMBER);
//            }
//        }
//    }
//
//    private void registerMenu() {
//        String name = userInput.inputName();
//        String password = userInput.inputPassword();
//        String email = userInput.inputEmail();
//        boolean successRegister = true;
//        if (successRegister) {
//            consoleOutput.printMessage(Message.SUCCESS_REGISTER);
//        }
//    }
//
//    private void authenticationMenu() {
//        String email = userInput.inputEmail();
//        String password = userInput.inputPassword();
//        User registerUser = authService.login(email, password);
//        if (registerUser == null) {
//            return;
//        }
//        if (registerUser.getRole().equals(Role.ROLE_ADMIN)) {
//            getAdminMenu();
//        } else {
//            getUserMenu(registerUser);
//        }
//    }
//
//    private void getAdminMenu() {
//        while (true) {
//            consoleOutput.printMessage(Message.ADMIN_MENU);
//            String selection = userInput.getUserInput();
//            switch (selection) {
//                case "1" -> consoleOutput.printCollection(userService.getAllUsers());
//                case "2" -> viewUserHabitsMenu();
//                case "3" -> deleteUserMenu();
//                case "4" -> blockingUserMenu();
//                case "0" -> {
//                    return;
//                }
//                default -> consoleOutput.printMessage(Message.INCORRECT_MENU_NUMBER);
//            }
//        }
//    }
//
//    private void viewUserHabitsMenu() {
//        String email = userInput.inputEmail();
//        User user = userService.getUserByEmail(email);
//        if (user != null) {
//            consoleOutput.printCollection(user.getHabits());
//
//        }
//    }
//
//    private void deleteUserMenu() {
//        String email = userInput.inputEmail();
//        User user = userService.getUserByEmail(email);
//        if (user != null) {
//            userService.deleteUser(user);
//            consoleOutput.printMessage(Message.SUCCESS_ACTION);
//        }
//    }
//
//    private void blockingUserMenu() {
//        String email = userInput.inputEmail();
//        User user = userService.getUserByEmail(email);
//        if (user != null) {
//            userService.blockingUser(user);
//            consoleOutput.printMessage(Message.SUCCESS_ACTION);
//        }
//    }
//
//    private void getUserMenu(User user) {
//        while (true) {
//            consoleOutput.printMessage(Message.USER_MENU);
//            String selection = userInput.getUserInput();
//            switch (selection) {
//                case "1" -> editAccount(user);
//                case "2" -> {
//                    userService.deleteUser(user);
//                    return;
//                }
//                case "3" -> inputHabitMenu(user);
//                case "4" -> deleteHabitMenu(user);
//                case "5" -> editHabitMenu(user);
//                case "6" -> consoleOutput.printCollection(habitService.getAllHabits(user, Fetch.LAZY));
//                case "7" -> confirmHabitMenu(user);
//                case "8" -> getStatisticMenu(user);
//                case "0" -> {
//                    return;
//                }
//                default -> consoleOutput.printMessage(Message.INCORRECT_MENU_NUMBER);
//            }
//        }
//    }
//
//    private void editAccount(User user) {
//        String name = userInput.inputName();
//        String password = userInput.inputPassword();
//        String email = userInput.inputEmail();
//        UpsertUserRequest dto = new UpsertUserRequest();
//        if (!name.isBlank()) {
//            dto.setName(name);
//        }
//        if (!password.isBlank()) {
//            dto.setPassword(password);
//        }
//        if (!email.isBlank()) {
//            dto.setEmail(email);
//        }
//        userService.updateUser(user.getEmail(), dto);
//    }
//
//    private void inputHabitMenu(User user) {
//        String title = userInput.getUserInput(Message.INPUT_HABIT_TITLE);
//        String text = userInput.getUserInput("Введите описание привычки");
//        ExecutionRate rate = ExecutionRate.valueOf(userInput.getUserInput("Введите частоту выполнения привычки (DAILY/WEEKLY/MONTHLY)"));
//        habitService.createHabit(user, new HabitDto(title, text, rate));
//    }
//
//    private void deleteHabitMenu(User user) {
//        String title = userInput.getUserInput(Message.INPUT_HABIT_TITLE);
//        habitService.deleteHabit(user, title);
//    }
//
//    private void editHabitMenu(User user) {
//        String title = userInput.getUserInput("Введите название редактируемой привычки");
//        String newTitle = userInput.getUserInput("Введите новое название привычки");
//        String newText = userInput.getUserInput("Введите новое описание привычки");
//        ExecutionRate newRate = ExecutionRate.valueOf(userInput.getUserInput("Введите новую частоту привычки"));
//        HabitDto updateHabit = new HabitDto(newTitle, newText, newRate);
//        habitService.updateHabit(user, title, updateHabit);
//    }
//
//    private void confirmHabitMenu(User user) {
//        String date = userInput.inputDate();
//        if (!DateHandler.checkInputDate(date)) {
//            consoleOutput.printMessage(Message.INCORRECT_DATE);
//            return;
//        }
//        String title = userInput.getUserInput(Message.INPUT_HABIT_TITLE);
//        habitService.confirmHabit(user, title, DateHandler.getDateFromString(date));
//    }
//
//    private void getStatisticMenu(User user) {
//        while (true) {
//            consoleOutput.printMessage(Message.STATISTIC_MENU);
//            String selection = userInput.getUserInput();
//            switch (selection) {
//                case "1" -> getStreak(user);
//                case "2" -> getPercentage(user);
//                case "3" -> getFullStatistics(user);
//                case "0" -> {
//                    return;
//                }
//                default -> consoleOutput.printMessage(Message.INCORRECT_MENU_NUMBER);
//            }
//        }
//    }
//
//    private void getStreak(User user) {
//        String title = userInput.getUserInput(Message.INPUT_HABIT_TITLE);
//        Habit habit = habitService.getHabitByTitle(user, title);
//        if (habit == null) {
//            return;
//        }
//        String from = userInput.getUserInput("Введите дату начала периода в формате дд/мм/гггг");
//        if (!DateHandler.checkInputDate(from)) {
//            consoleOutput.printMessage(Message.INCORRECT_DATE);
//            return;
//        }
//        LocalDate dateFrom = DateHandler.getDateFromString(from);
//        String to = userInput.getUserInput("Введите дату окончания периода в формате дд/мм/гггг");
//        if (!DateHandler.checkInputDate(to)) {
//            consoleOutput.printMessage(Message.INCORRECT_DATE);
//            return;
//        }
//        LocalDate dateTo = DateHandler.getDateFromString(to);
//        consoleOutput.printCollection(statisticService.getHabitStreak(habit, dateFrom, dateTo));
//    }
//
//    private void getPercentage(User user) {
//        String title = userInput.getUserInput(Message.INPUT_HABIT_TITLE);
//        Habit habit = habitService.getHabitByTitle(user, title);
//        if (habit == null) {
//            return;
//        }
//        String from = userInput.getUserInput("Введите дату начала периода в формате дд/мм/гггг");
//        if (!DateHandler.checkInputDate(from)) {
//            consoleOutput.printMessage(Message.INCORRECT_DATE);
//            return;
//        }
//        LocalDate dateFrom = DateHandler.getDateFromString(from);
//        String to = userInput.getUserInput("Введите дату начала периода в формате дд/мм/гггг");
//        if (!DateHandler.checkInputDate(to)) {
//            consoleOutput.printMessage(Message.INCORRECT_DATE);
//            return;
//        }
//        LocalDate dateTo = DateHandler.getDateFromString(to);
//        consoleOutput.printObject(statisticService.getSuccessHabitRate(habit, dateFrom, dateTo));
//    }
//
//    private void getFullStatistics(User user) {
//        String from = userInput.getUserInput("Введите дату начала периода в формате дд/мм/гггг");
//        if (!DateHandler.checkInputDate(from)) {
//            consoleOutput.printMessage(Message.INCORRECT_DATE);
//            return;
//        }
//        LocalDate dateFrom = DateHandler.getDateFromString(from);
//        String to = userInput.getUserInput("Введите дату начала периода в формате дд/мм/гггг");
//        if (!DateHandler.checkInputDate(to)) {
//            consoleOutput.printMessage(Message.INCORRECT_DATE);
//            return;
//        }
//        LocalDate dateTo = DateHandler.getDateFromString(to);
//        for (Habit h : habitService.getAllHabits(user, Fetch.EAGER)) {
//            consoleOutput.printObject(statisticService.getHabitProgress(h, dateFrom, dateTo));
//        }
//    }
//}
