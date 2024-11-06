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
            WHERE email = ?
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
    public static final String DELETE_ALL_USERS_QUERY = """
            DELETE FROM my_schema.users
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
    public static final String DELETE_HABIT = """
            DELETE FROM my_schema.habits
            WHERE user_id = ? AND title = ?
            """;
    public static final String INSERT_HABIT_EXECUTION_QUERY = """
            INSERT INTO my_schema.habit_execution (date, habit_id) VALUES (?, ?)
            """;
    public static final String INSERT_AUDIT_QUERY = """
            INSERT INTO my_schema.audit (email, action, description, date) VALUES (?, ?, ?, ?)
            """;
}
