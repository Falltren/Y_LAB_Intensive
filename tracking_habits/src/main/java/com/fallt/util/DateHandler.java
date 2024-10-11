package com.fallt.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateHandler {

    private static final String DATE_PATTERN = "dd/MM/yyyy";

    private DateHandler() {
    }

    /**
     * Метод конвертирует строку в дату в соответствии с используемым паттерном
     * @param date Дата в виде строки
     * @return Дата в виде объекта класса LocalDate
     */
    public static LocalDate getDateFromString(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN);
        return LocalDate.parse(date, formatter);
    }

    /**
     * Метод осуществляет проверку введенной строки на соответствие формату даты
     * @param input Строка
     * @return Результат проверки строки
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
