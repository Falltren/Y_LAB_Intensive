package com.fallt.util;

import com.fallt.exception.DBException;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 * Утилитарный класс для получения соединения с базой данных, с указанными в файле конфигурации настройками
 */
public class DBUtils {
    private static Connection connection;

    private DBUtils() {
    }

    /**
     * Метод получения соединения с базой данных
     *
     * @return Настроенный объект Connection
     */
    public static Connection getConnection() {
        if (connection == null) {
            try {
                Properties props = loadProperties();
                String url = props.getProperty("url");
                String user = props.getProperty("username");
                String password = props.getProperty("password");
                connection = DriverManager.getConnection(url, user, password);
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
        return connection;
    }

    /**
     * Метод закрытия соединения с базой данных
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static Properties loadProperties() {
        try (InputStream inputStream = DBUtils.class.getResourceAsStream("/liquibase.properties")) {
            Properties properties = new Properties();
            properties.load(inputStream);
            return properties;
        } catch (IOException e) {
            throw new DBException(e.getMessage());
        }
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
