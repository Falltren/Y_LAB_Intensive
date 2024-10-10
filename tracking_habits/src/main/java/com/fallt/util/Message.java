package com.fallt.util;

public class Message {

    private Message() {
    }

    public static final String MAIN_MENU = "\n" + "Главное меню:" + "\n\n"
            + "1 - Регистрация" + "\n"
            + "2 - Вход в систему" + "\n"
            + "0 - завершение работы";

    public static final String USER_MENU = "Действия пользователя:" + "\n"
            + "1 - Добавить новую привычку" + "\n"
            + "2 - Удалить привычку" + "\n"
            + "3 - Редактировать привычку" + "\n"
            + "4 - Просмотреть список привычек" + "\n"
            + "5 - Просмотр статистики и аналитики" + "\n"
            + "0 - Выход из меню";

    public static final String EDIT_MENU = "Выберите информацию, которую необходимо изменить (введите цифры без разделителей)" + "\n"
            + "1 - Тип тренировки" + "\n"
            + "2 - Дата тренировки" + "\n"
            + "3 - Продолжительность тренировки" + "\n"
            + "4 - Количество потраченных калорий" + "\n"
            + "5 - Описание тренировки";

    public static final String ADMIN_MENU = "Выберите действие" + "\n"
            + "1 - Просмотр всех пользователей" + "\n"
            + "2 - Просмотр привычек пользователя (введите электронную почту)" + "\n"
            + "3 - Удалить пользователя (введите электронную почту)" + "\n"
            + "4 - Заблокировать пользователя (введите электронную почту)" + "\n"
            + "0 - Выход из меню";

    public static final String UNAUTHENTICATED_USER = "Вы не прошли аутентификацию. " +
            "Вернитесь в главное меню и выберите пункт - вход в систему";

    public static final String BLOCKED_USER = "Ваша учетная запись заблокирована, обратитесь к администратору";

    public static final String INCORRECT_DATE = "Введена некорректная дата";

    public static final String INCORRECT_MENU_NUMBER = "Введен отсутствующий номер пункта меню" + "\n" + "Пожалуйста повторите ввод";

    public static final String EMAIL_EXIST = "Указанный email уже используется";

    public static final String PASSWORD_EXIST = "Указанный пароль уже используется";

    public static final String HABIT_EXIST = "У вас уже присутствует указанная привычка";

    public static final String INCORRECT_EMAIL = "Указан некорректный email";

    public static final String INCORRECT_HABIT_TITLE = "У вас отсутствует указанная привычка";

    public static final String SUCCESS_REGISTER = "Вы успешно зарегистрированы";

    public static final String SUCCESS_ACTION = "Действие выполнено";
}
