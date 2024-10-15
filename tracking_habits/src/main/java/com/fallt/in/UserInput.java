package com.fallt.in;

import com.fallt.out.ConsoleOutput;
import lombok.RequiredArgsConstructor;

import java.util.Scanner;

@RequiredArgsConstructor
public class UserInput {

    private final Scanner scanner = new Scanner(System.in);

    private final ConsoleOutput consoleOutput;

    public String inputName() {
        consoleOutput.printMessage("Введите ваше имя");
        return scanner.nextLine();
    }

    public String inputEmail() {
        consoleOutput.printMessage("Введите ваш электронный адрес");
        return scanner.nextLine();
    }

    public String inputPassword() {
        consoleOutput.printMessage("Введите ваш пароль");
        return scanner.nextLine();
    }

    public String inputDate() {
        consoleOutput.printMessage("Введите дату в формате дд/мм/гггг");
        return scanner.nextLine();
    }

    public String getUserInput() {
        return scanner.nextLine();
    }

    public String getUserInput(String text) {
        consoleOutput.printMessage(text);
        return scanner.nextLine();
    }
}
