package org.map.socialnetwork.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {

    private static Connection connectionDB = null;
    private static ConnectionManager instance = null;
    private String url;
    private String username;
    private String password;



    private ConnectionManager() throws SQLException {
        this.url = "jdbc:postgresql://localhost:5432/social_network";
        this.username = "postgres";
        this.password = "2103";

        setConnection();
    }

    public static ConnectionManager getInstance() throws SQLException {
        if(instance == null)
            instance = new ConnectionManager();
        return instance;
    }

    public Connection getConnection() {
        if(connectionDB == null)
            throw new RuntimeException("No connection established!");

        return connectionDB;
    }

    public void setConnection() throws SQLException {
        connectionDB = DriverManager.getConnection(this.url, this.username, this.password);
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
