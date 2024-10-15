package com.fallt.util;

public class Message {

    private Message() {
    }

    public static final String MAIN_MENU = "\n" + "Главное меню:" + "\n\n"
            + "1 - Регистрация" + "\n"
            + "2 - Вход в систему" + "\n"
            + "0 - завершение работы";

    public static final String USER_MENU = "Действия пользователя:" + "\n"
            + "1 - Редактировать аккаунт" + "\n"
            + "2 - Удалить аккаунт" + "\n"
            + "3 - Добавить новую привычку" + "\n"
            + "4 - Удалить привычку" + "\n"
            + "5 - Редактировать привычку" + "\n"
            + "6 - Просмотреть список привычек" + "\n"
            + "7 - Отметить выполнение привычки" + "\n"
            + "8 - Просмотр статистики и аналитики" + "\n"
            + "0 - Выход из меню";

    public static final String ADMIN_MENU = "Выберите действие" + "\n"
            + "1 - Просмотр всех пользователей" + "\n"
            + "2 - Просмотр привычек пользователя (введите электронную почту)" + "\n"
            + "3 - Удалить пользователя (введите электронную почту)" + "\n"
            + "4 - Заблокировать пользователя (введите электронную почту)" + "\n"
            + "0 - Выход из меню";

    public static final String STATISTIC_MENU = "Выберите действие" + "\n"
            + "1 - Статистика выполнения привычки за указанный период" + "\n"
            + "2 - Расчет процента успешного выполнения привычки за указанный период" + "\n"
            + "3 - Формирование полного отчета по привычкам за указанный период" + "\n"
            + "0 - Выход из меню";

    public static final String UNAUTHENTICATED_USER = "Неверный пароль или адрес электронной почты";

    public static final String BLOCKED_USER = "Ваша учетная запись заблокирована, обратитесь к администратору";

    public static final String INCORRECT_DATE = "Введена некорректная дата";

    public static final String INCORRECT_INPUT = "Некорректный ввод";

    public static final String INCORRECT_MENU_NUMBER = "Введен отсутствующий номер пункта меню" + "\n" + "Пожалуйста повторите ввод";

    public static final String EMAIL_EXIST = "Указанный email уже используется";

    public static final String PASSWORD_EXIST = "Указанный пароль уже используется";

    public static final String HABIT_EXIST = "У вас уже присутствует указанная привычка";

    public static final String INCORRECT_EMAIL = "Указан некорректный email";

    public static final String INCORRECT_HABIT_TITLE = "У вас отсутствует указанная привычка";

    public static final String SUCCESS_REGISTER = "Вы успешно зарегистрированы";

    public static final String INPUT_HABIT_TITLE = "Введите название привычки";

    public static final String SUCCESS_ACTION = "Действие выполнено";
}
