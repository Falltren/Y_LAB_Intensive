package com.fallt.util;

public final class Constant {

    private Constant() {
    }

    public static final String INSERT_USER_QUERY = """
            INSERT INTO my_schema.users (name, password, email, role, create_at, update_at, is_blocked, is_active)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;
    public static final String UPDATE_USER_QUERY = """
            UPDATE my_schema.users
            SET name = ?, password = ?, email = ?, role = ?, update_at = ?, is_blocked = ?
            WHERE id = ?
            """;
    public static final String DELETE_USER_QUERY = """
            UPDATE my_schema.users
            SET is_active = false
            WHERE id = ?
            """;
    public static final String FIND_ALL_USERS_QUERY = """
            SELECT * FROM my_schema.users
            """;
    public static final String FIND_USER_BY_EMAIL_QUERY = """
            SELECT * FROM my_schema.users
            WHERE email = ?
            """;
    public static final String FIND_USER_BY_PASSWORD_QUERY = """
            SELECT * FROM my_schema.users
            WHERE password = ?
            """;
    public static final String FIND_USER_BY_ID_QUERY = """
            SELECT * FROM my_schema.users
            WHERE id = ?
            """;
    public static final String INSERT_HABIT_QUERY = """
            INSERT INTO my_schema.habits (title, text, execution_rate, create_at, user_id)
            VALUES (?, ?, ?, ?, ?)
            """;
    public static final String UPDATE_HABIT_QUERY = """
            UPDATE my_schema.habits
            SET title = ?, text = ?, execution_rate = ? WHERE id = ?
            """;
    public static final String FIND_ALL_HABITS_QUERY = """
            SELECT h.*, e.date FROM my_schema.habits h
            LEFT JOIN my_schema.habit_execution e
            ON e.habit_id = h.id
            WHERE h.user_id = ?
            """;
    public static final String FIND_HABIT_BY_TITLE_AND_USER = """
            SELECT * FROM my_schema.habits h
            LEFT JOIN my_schema.habit_execution e
            ON e.habit_id = h.id
            WHERE h.user_id = ? AND h.title = ?
            """;
    public static final String FIND_HABIT_BY_ID = """
            SELECT * FROM my_schema.habits h
            LEFT JOIN my_schema.habit_execution e
            ON e.habit_id = h.id
            WHERE h.id = ?
            """;
    public static final String DELETE_HABIT = """
            DELETE FROM my_schema.habits
            WHERE id = ?
            """;
    public static final String INSERT_HABIT_EXECUTION_QUERY = """
            INSERT INTO my_schema.habit_execution (date, habit_id) VALUES (?, ?)
            """;
    public static final String INCORRECT_EMAIL_MESSAGE = "Электронная почта должна быть указана";
    public static final String NAME_LENGTH_MESSAGE = "Имя должно содержать от 3 до 30 символов";
    public static final String PASSWORD_FORMAT_MESSAGE = "Неверный формат пароля. Пароль должен состоять из букв, цифр и символов. Обязательно содержать заглавную латинскую букву, цифру и иметь длину не менее 8 символов";
    public static final String NOT_BLANK_MESSAGE = "Поле должно быть заполнено";
    public static final String NOT_NULL_MESSAGE = "Поле не может быть пустым";
    public static final String INCORRECT_EMAIL_FORMAT_MESSAGE = "Неверный формат email";
    public static final String MAX_EMAIL_LENGTH_MESSAGE = "Длина email должна быть не более 30 символов";
    public static final String REPORT_PERIOD_MESSAGE = "Дата окончания не может предшествовать дате начала периода";
    public static final String HABIT_TITLE_LENGTH_MESSAGE = "Название привычки должно содержать от 3 до 30 символов";
    public static final String HABIT_TEXT_LENGTH_MESSAGE = "Описание привычки должно содержать не более 100 символов";

}
