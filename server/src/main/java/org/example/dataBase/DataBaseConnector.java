package org.example.dataBase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseConnector {
    private final String URL;
    private final String USER;
    private final String PASSWORD;

    public DataBaseConnector(String url, String user, String password) {
        URL = url;
        USER = user;
        PASSWORD = password;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
