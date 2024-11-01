package com.fallt.util;

import com.fallt.config.YamlPropertySourceFactory;
import com.fallt.exception.DBException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.sql.*;

/**
 * Класс для получения соединения с базой данных, с указанными в файле конфигурации настройками
 */
@Component
@PropertySource(value = "classpath:application.yaml", factory = YamlPropertySourceFactory.class)
public class DbConnectionManager {

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.driver-class-name}")
    private String driver;


    /**
     * Метод получения соединения с базой данных
     *
     * @return Настроенный объект Connection
     */
    public Connection getConnection() {
        try {
            Class.forName(driver);
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            throw new DBException(e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Не найден драйвер postgresql");
        }
    }

    public Connection getConnection(String testUrl, String testUser, String testPassword, String driver) {
        try {
            Class.forName(driver);
            url = testUrl;
            username = testUser;
            password = testPassword;
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            throw new DBException(e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Не найден драйвер postgresql");
        }
    }

    public void setConnectionSettings(String url, String username, String password, String driver) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.driver = driver;
    }

    /**
     * Метод закрытия объекта, реализующего интерфейс Statement
     *
     * @param st Объект, реализующий интерфейс Statement
     */
    public void closeStatement(Statement st) {
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
    public void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
