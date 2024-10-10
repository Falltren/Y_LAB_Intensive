package com.fallt.service;

import com.fallt.entity.Role;
import com.fallt.entity.User;
import com.fallt.in.UserInput;
import com.fallt.out.ConsoleOutput;
import com.fallt.util.Message;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@RequiredArgsConstructor
public class UserMenu {

    private boolean isStop;

    private final ConsoleOutput consoleOutput;

    private final UserInput userInput;

    private final RegistrationService registrationService;

    private final AuthService authService;

    private final UserService userService;

    public void start() {
        while (!isStop) {
            consoleOutput.printMessage(Message.MAIN_MENU);
            String selection = userInput.getUserInput();
            switch (selection) {
                case "1" -> registerMenu();
                case "2" -> authenticationMenu();
                case "0" -> isStop = true;
                default -> consoleOutput.printMessage(Message.INCORRECT_MENU_NUMBER);
            }
        }
    }

    private void registerMenu() {
        String name = userInput.inputName();
        String password = userInput.inputPassword();
        String confirmPassword = userInput.inputEmail();
        boolean successRegister = registrationService.register(name, password, confirmPassword);
        if (successRegister) {
            consoleOutput.printMessage(Message.SUCCESS_REGISTER);
        }
    }

    private void authenticationMenu() {
        String email = userInput.inputEmail();
        String password = userInput.inputPassword();
        User registerUser = authService.login(email, password);
        if (registerUser == null) {
            return;
        }
        if (registerUser.getRole().equals(Role.ADMIN)) {
            getAdminMenu();
        } else {
//            getUserMenu(registerUser);
        }
    }

    private void getAdminMenu() {
        while (true) {
            consoleOutput.printMessage(Message.ADMIN_MENU);
            String selection = userInput.getUserInput();
            switch (selection) {
                case "1" -> consoleOutput.printCollection(userService.getAllUsers);
                case "2" -> viewUserHabitsMenu();
                case "3" -> deleteUserMenu();
                case "4" -> blockingUserMenu();
                case "0" -> {
                    return;
                }
                default -> consoleOutput.printMessage(Message.INCORRECT_MENU_NUMBER);
            }
        }
    }

    private void viewUserHabitsMenu() {
        String email = userInput.inputEmail();
        User user = userService.getUserByEmail(email);
        if (user != null) {
            consoleOutput.printCollection(user.getHabits());

        }
    }

    private void deleteUserMenu() {
        String email = userInput.inputEmail();
        User user = userService.getUserByEmail(email);
        if (user != null) {
            userService.deleteUser(user.getEmail());
            consoleOutput.printMessage(Message.SUCCESS_ACTION);
        }
    }

    private void blockingUserMenu() {
        String email = userInput.inputEmail();
        User user = userService.getUserByEmail(email);
        if (user != null) {
            user.setBlocked(true);
            consoleOutput.printMessage(Message.SUCCESS_ACTION);
        }
    }

//    private void getUserMenu(User user) {
//        while (true) {
//            consoleOutput.printMessage(Message.USER_MENU);
//            String selection = userInput.getUserInput();
//            switch (selection) {
//                case "1" -> inputHabitMenu(user);
//                case "2" -> deleteHabitMenu(user);
//                case "3" -> editTrainingMenu(user);
//                case "4" -> reportPrinter.printAllTrainings(user.getTrainings());
//                case "5" -> getCaloriesReportMenu(user);
//                case "0" -> {
//                    return;
//                }
//                default -> consoleOutput.printMessage(Message.INCORRECT_MENU_NUMBER);
//            }
//        }
//    }
//
//    private void inputHabitMenu(User user) {
//        if (!authService.isAuthenticated(user)) {
//            consoleOutput.printMessage(Message.UNAUTHENTICATED_USER);
//            return;
//        }
//        String type = userInput.;
//        String date = userInput.inputDate();
//        if (!DateHandler.checkInputDate(date)) {
//            System.out.println(Message.INCORRECT_DATE);
//            return;
//        }
//        LocalDate trainingDate = DateHandler.getDateFromString(date);
//        String duration = userInput.getUserInput("Введите продолжительность тренировки в минутах");
//        String spentCalories = userInput.getUserInput("Введите количество потраченных калорий");
//        String description = userInput.getUserInput("Введите дополнительную информацию о тренировке (при необходимости)");
//        trainingService.addNewTraining(user, type, trainingDate, Integer.parseInt(duration),
//                Integer.parseInt(spentCalories), description);
//    }
}
