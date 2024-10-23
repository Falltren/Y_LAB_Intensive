package com.fallt.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {

    private static Properties properties;

    static {
        loadProperties();
    }

    private PropertiesUtil(){}

    private static void loadProperties() {
        try (InputStream inputStream = PropertiesUtil.class.getClassLoader().getResourceAsStream("liquibase.properties")) {
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
