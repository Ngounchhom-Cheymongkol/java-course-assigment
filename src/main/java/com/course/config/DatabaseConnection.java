package com.course.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    public void connect() {
        String url = "jdbc:postgresql://localhost:5435/java_course_db"; // Your DB URL
        String user = "postgres"; // Your DB username
        String password = "123456"; // Your DB password

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println("✅ Connected to PostgreSQL database!");
        } catch (SQLException e) {
            System.out.println("❌ Connection failed!");
            e.printStackTrace();
        }
    }
}
