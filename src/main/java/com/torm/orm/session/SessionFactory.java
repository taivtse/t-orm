package com.torm.orm.session;

import com.torm.orm.exception.TormException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class SessionFactory {
    private static final ResourceBundle DATABASE_BUNDLE = ResourceBundle.getBundle("database");
    private static final String DB_URL = DATABASE_BUNDLE.getString("db.url");
    private static final String DB_USERNAME = DATABASE_BUNDLE.getString("db.username");
    private static final String DB_PASSWORD = DATABASE_BUNDLE.getString("db.password");
    private static final String DB_DRIVER = DATABASE_BUNDLE.getString("db.driver");

    static {
        try {
            Class.forName(DB_DRIVER);
        } catch (ClassNotFoundException e) {
            throw new TormException(e);
        }
    }

    public static Session openSession() {
        try {
            Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            connection.setAutoCommit(false);

            return new SessionImpl(connection);
        } catch (SQLException e) {
            throw new TormException(e);
        }
    }
}
