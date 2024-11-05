package com.fallt.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Класс предназначен для считывания свойств из файла конфигурации
 */
public class PropertiesUtil {
    
    private static final String PROPERTY_FILE = "liquibase.properties";

    private static Properties properties;

    static {
        loadProperties();
    }

    private PropertiesUtil(){}

    private static void loadProperties() {
        try (InputStream inputStream = PropertiesUtil.class.getClassLoader().getResourceAsStream(PROPERTY_FILE)) {
            properties = new Properties();
            properties.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static String getProperty(String propertyKey) {
        return properties.getProperty(propertyKey);
    }
}
