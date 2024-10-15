package com.fallt.util;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Класс предназначен для настройки объекта Liquibase
 */
public class LiquibaseRunner {


    public void run() {
        try {
            Connection connection = DBUtils.getConnection();
            Database database =
                    DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
            String serviceSchemaName = "service_schema";
            createServiceSchema(connection, serviceSchemaName);
            database.setLiquibaseSchemaName(serviceSchemaName);
            database.setDefaultSchemaName("my_schema");
            Liquibase liquibase =
                    new Liquibase("db/changelog/db.changelog-master.xml", new ClassLoaderResourceAccessor(), database);
            liquibase.update();
            System.out.println("Migration is completed successfully");
        } catch (LiquibaseException e) {
            System.out.println("SQL Exception in migration " + e.getMessage());
        }
    }

    private void createServiceSchema(Connection connection, String schemaName) {
        try (Statement statement = connection.createStatement()) {
            statement.execute("create schema if not exists " + schemaName);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
