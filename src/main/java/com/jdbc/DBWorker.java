package com.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBWorker {
    private static String URL = "jdbc:postgresql://";
    private static String databaseName = "/test";
    private static final String USER = "postgres";
    private static final String PASSWORD = "dins";
    private Connection connection;
    public DBWorker(String ipAddr){ ;
        URL = URL + ipAddr + databaseName;
        try {
            connection = DriverManager.getConnection(URL,USER,PASSWORD);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public Connection getConnection(){
        return connection;
    }
}
