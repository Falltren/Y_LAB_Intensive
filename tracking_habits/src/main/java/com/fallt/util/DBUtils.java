package com.fallt.util;

import com.fallt.exception.DBException;

import java.sql.*;

/**
 * Утилитный класс для получения соединения с базой данных, с указанными в файле конфигурации настройками
 */
public class DBUtils {

    private static String url;

    private static String user;

    private static String password;

    private DBUtils() {
    }

    static {
        url = PropertiesUtil.getProperty("url");
        user = PropertiesUtil.getProperty("username");
        password = PropertiesUtil.getProperty("password");
    }

    /**
     * Метод получения соединения с базой данных
     *
     * @return Настроенный объект Connection
     */
    public static Connection getConnection() {
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            throw new DBException(e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Не найден драйвер postgresql");
        }
    }

    public static void useTestConnection(String testUrl, String testUser, String testPassword) {
        url = testUrl;
        user = testUser;
        password = testPassword;
    }

    /**
     * Метод закрытия объекта, реализующего интерфейс Statement
     *
     * @param st Объект, реализующий интерфейс Statement
     */
    public static void closeStatement(Statement st) {
        if (st != null) {
            try {
                st.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * Метод для закрытия объекта, реализующего интерфейс ResultSet
     *
     * @param rs Объект, реализующий интерфейс ResultSet
     */
    public static void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
