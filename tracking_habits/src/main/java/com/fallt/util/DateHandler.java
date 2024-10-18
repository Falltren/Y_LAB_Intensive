package com.fallt.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Утилитарный класс для обработки пользовательского ввода даты
 */
public class DateHandler {

    private static final String DATE_PATTERN = "dd/MM/yyyy";

    private DateHandler() {
    }

    /**
     * Парсинг введенной строки в объект класса LocalDate
     *
     * @param date Дата в виде строки
     * @return Объект LocalDate
     */
    public static LocalDate getDateFromString(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN);
        return LocalDate.parse(date, formatter);
    }

    /**
     * Проверка введенной даты используемому паттерну
     *
     * @param input Дата в виде строки, введенная пользователем
     * @return Результат проверки соответствия введенной строки паттерну даты
     */
    public static boolean checkInputDate(String input) {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(DATE_PATTERN);
        try {
            dateFormat.parse(input);
        } catch (DateTimeParseException e) {
            return false;
        }
        return true;
    }
}
