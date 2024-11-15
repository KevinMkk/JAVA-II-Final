package com.example.luctfxapp;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class UserAuthenticator {

    public static String authenticateUser(String username, String password) {
        try (Connection connection = DatabaseManager.getConnection()) {
            // Check in Admin table
            if (checkUserRole(connection, "admin", username, password)) {
                logLogin(username);
                return "Admin";
            }
            // Check in Lecturer table
            if (checkUserRole(connection, "lecturer", username, password)) {
                logLogin(username);
                return "Lecturer";
            }
            // Check in Principal Lecturer table
            if (checkUserRole(connection, "principal_lecturer", username, password)) {
                logLogin(username);
                return "PrincipalLecturer";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "InvalidCredentials";
    }

    private static boolean checkUserRole(Connection connection, String tableName, String username, String password) throws Exception {
        String query = "SELECT * FROM " + tableName + " WHERE username = ? AND password = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    private static void logLogin(String username) {
        String logFileName = "log.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFileName, true))) {
            String logEntry = String.format("User: %s logged in at %s\n", username, LocalDateTime.now());
            writer.write(logEntry);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int getEmployeeId(String tableName, String username) {
        String query = "SELECT employee_id FROM " + tableName + " WHERE username = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("employee_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
