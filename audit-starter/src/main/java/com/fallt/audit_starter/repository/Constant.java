package com.fallt.audit_starter.repository;

public final class Constant {

    private Constant(){}

    public static final String INSERT_AUDIT_QUERY = """
            INSERT INTO my_schema.audit (email, action, description, date) VALUES (?, ?, ?, ?)
            """;
}
