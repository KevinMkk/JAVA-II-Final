package com.example.luctfxapp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME = "luct_fx_db";
    private static final String USER = "root";
    private static final String PASSWORD = "123456";

    public static void initializeDatabase() {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement()) {

            // Create database if it doesn't exist
            statement.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
            System.out.println("Database " + DB_NAME + " created or already exists.");

            // Connect to the specific database
            try (Connection dbConnection = DriverManager.getConnection(URL + DB_NAME, USER, PASSWORD);
                 Statement dbStatement = dbConnection.createStatement()) {

                // Create Employee table
                dbStatement.executeUpdate(
                        "CREATE TABLE IF NOT EXISTS employee (" +
                                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                                "name VARCHAR(50) NOT NULL, " +
                                "surname VARCHAR(50) NOT NULL, " +
                                "position VARCHAR(50), " +
                                "email VARCHAR(50) NOT NULL, " +
                                "phone_number VARCHAR(50) NOT NULL, " +
                                "address VARCHAR(50) NOT NULL, " +
                                "date_of_employment DATE NOT NULL, " +
                                "display_picture LONGBLOB, " +
                                "employee_id VARCHAR(50) NOT NULL" +
                                ")"
                );

                // Create Admin table
                dbStatement.executeUpdate(
                        "CREATE TABLE IF NOT EXISTS admin (" +
                                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                                "username VARCHAR(50) NOT NULL, " +
                                "password VARCHAR(50) NOT NULL, " +
                                "employee_id INT, " +  // Add foreign key column
                                "FOREIGN KEY (employee_id) REFERENCES employee(id) ON DELETE SET NULL" +
                                ")"
                );

                // Create Lecturer table
                dbStatement.executeUpdate(
                        "CREATE TABLE IF NOT EXISTS lecturer (" +
                                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                                "username VARCHAR(50) NOT NULL, " +
                                "password VARCHAR(50) NOT NULL, " +
                                "employee_id INT, " +
                                "FOREIGN KEY (employee_id) REFERENCES employee(id) ON DELETE SET NULL" +
                                ")"
                );

                // Create Principal Lecturer table
                dbStatement.executeUpdate(
                        "CREATE TABLE IF NOT EXISTS principal_lecturer (" +
                                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                                "username VARCHAR(50) NOT NULL, " +
                                "password VARCHAR(50) NOT NULL, " +
                                "employee_id INT, " +
                                "FOREIGN KEY (employee_id) REFERENCES employee(id) ON DELETE SET NULL" +
                                ")"
                );

                dbStatement.executeUpdate(
                        "CREATE TABLE IF NOT EXISTS logs (" +
                                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                                "roll VARCHAR(50) NOT NULL, " +
                                "username VARCHAR(50) NOT NULL, " +
                                "employee_id INT, " +
                                "entry_log TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                                "exit_log TIMESTAMP, " +
                                "log_duration TIME, " +
                                "FOREIGN KEY (employee_id) REFERENCES employee(id) ON DELETE SET NULL" +
                                ")"
                );

                dbStatement.executeUpdate(
                        "CREATE TABLE IF NOT EXISTS Modules ( " +
                                "ModuleID SERIAL PRIMARY KEY, " +
                                "ModuleName VARCHAR(100) NOT NULL " +
                                ")"
                );

                dbStatement.executeUpdate(
                        "CREATE TABLE IF NOT EXISTS Reports ( " +
                                "ReportID SERIAL PRIMARY KEY, " +
                                "ModuleID INT REFERENCES Modules(ModuleID), " +
                                "ReportText TEXT NOT NULL, " +
                                "SubmittedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP " +
                                ")"
                );

                dbStatement.executeUpdate(
                        "CREATE TABLE IF NOT EXISTS WeeklyReports ( " +
                                "WeeklyReportID SERIAL PRIMARY KEY, " +
                                "ClassName VARCHAR(100) NOT NULL, " +
                                "Challenges TEXT NOT NULL, " +
                                "Recommendations TEXT NOT NULL, " +
                                "SubmittedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP " +
                                ")"
                );

                dbStatement.executeUpdate(
                        "CREATE TABLE IF NOT EXISTS classes ( " +
                                "class_id INT AUTO_INCREMENT PRIMARY KEY, " +
                                "faculty VARCHAR(100) NOT NULL, " +
                                "class_name VARCHAR(100) NOT NULL " +
                                ")"
                );

                dbStatement.executeUpdate(
                        "CREATE TABLE IF NOT EXISTS semester ( " +
                                "id SERIAL PRIMARY KEY, " +
                                "year1 int(5) NOT NULL, " +
                                "year2 int(5) NOT NULL, " +
                                "semester int(2) NOT NULL " +
                                ")"
                );

                dbStatement.executeUpdate(
                        "CREATE TABLE IF NOT EXISTS BSCSMY1 ( " +
                                "student_id SERIAL PRIMARY KEY, " +
                                "student_name VARCHAR(100), " +
                                "student_surname VARCHAR(100), " +
                                "student_gender VARCHAR(10) " +
                                ")"
                );

                dbStatement.executeUpdate(
                        "CREATE TABLE IF NOT EXISTS BSCSMY2 ( " +
                                "student_id SERIAL PRIMARY KEY, " +
                                "student_name VARCHAR(100), " +
                                "student_surname VARCHAR(100), " +
                                "student_gender VARCHAR(10) " +
                                ")"
                );

                dbStatement.executeUpdate(
                        "CREATE TABLE IF NOT EXISTS BSCSMY3 ( " +
                                "student_id SERIAL PRIMARY KEY, " +
                                "student_name VARCHAR(100), " +
                                "student_surname VARCHAR(100), " +
                                "student_gender VARCHAR(10) " +
                                ")"
                );

                dbStatement.executeUpdate(
                        "CREATE TABLE IF NOT EXISTS BSCSMY4 ( " +
                                "student_id SERIAL PRIMARY KEY, " +
                                "student_name VARCHAR(100), " +
                                "student_surname VARCHAR(100), " +
                                "student_gender VARCHAR(10) " +
                                ")"
                );

                dbStatement.executeUpdate(
                        "CREATE TABLE IF NOT EXISTS BSCITY1 ( " +
                                "student_id SERIAL PRIMARY KEY, " +
                                "student_name VARCHAR(100), " +
                                "student_surname VARCHAR(100), " +
                                "student_gender VARCHAR(10) " +
                                ")"
                );

                dbStatement.executeUpdate(
                        "CREATE TABLE IF NOT EXISTS BSCITY2 ( " +
                                "student_id SERIAL PRIMARY KEY, " +
                                "student_name VARCHAR(100), " +
                                "student_surname VARCHAR(100), " +
                                "student_gender VARCHAR(10) " +
                                ")"
                );

                dbStatement.executeUpdate(
                        "CREATE TABLE IF NOT EXISTS BSCITY3 ( " +
                                "student_id SERIAL PRIMARY KEY, " +
                                "student_name VARCHAR(100), " +
                                "student_surname VARCHAR(100), " +
                                "student_gender VARCHAR(10) " +
                                ")"
                );

                dbStatement.executeUpdate(
                        "CREATE TABLE IF NOT EXISTS BSCITY4 ( " +
                                "student_id SERIAL PRIMARY KEY, " +
                                "student_name VARCHAR(100), " +
                                "student_surname VARCHAR(100), " +
                                "student_gender VARCHAR(10) " +
                                ")"
                );

                dbStatement.executeUpdate(
                        "CREATE TABLE IF NOT EXISTS DITY1 ( " +
                                "student_id SERIAL PRIMARY KEY, " +
                                "student_name VARCHAR(100), " +
                                "student_surname VARCHAR(100), " +
                                "student_gender VARCHAR(10) " +
                                ")"
                );

                dbStatement.executeUpdate(
                        "CREATE TABLE IF NOT EXISTS DITY2 ( " +
                                "student_id SERIAL PRIMARY KEY, " +
                                "student_name VARCHAR(100), " +
                                "student_surname VARCHAR(100), " +
                                "student_gender VARCHAR(10) " +
                                ")"
                );

                dbStatement.executeUpdate(
                        "CREATE TABLE IF NOT EXISTS DITY3 ( " +
                                "student_id SERIAL PRIMARY KEY, " +
                                "student_name VARCHAR(100), " +
                                "student_surname VARCHAR(100), " +
                                "student_gender VARCHAR(10) " +
                                ")"
                );

                dbStatement.executeUpdate(
                        "CREATE TABLE IF NOT EXISTS students ( " +
                                "student_id SERIAL PRIMARY KEY, " +
                                "student_name VARCHAR(100), " +
                                "student_surname VARCHAR(100), " +
                                "student_gender VARCHAR(10), " +
                                "phone_number INT, " +
                                "email VARCHAR(100), " +
                                "class_name VARCHAR(100),"+
                                "class_id INT, " +
                                "FOREIGN KEY (class_id) REFERENCES classes(class_id) ON DELETE SET NULL" +
                                ")"
                );

                dbStatement.executeUpdate(
                        "CREATE TABLE IF NOT EXISTS Attendance_Records ( " +
                                "id SERIAL PRIMARY KEY, " +
                                "Class_Date DATE NOT NULL, " +
                                "Class_Name VARCHAR(100) NOT NULL, " +
                                "Chapter VARCHAR(100) NOT NULL, " +
                                "Learning_Outcomes TEXT, " +
                                "Student_ID INT NOT NULL, " +
                                "Student_Name VARCHAR(100) NOT NULL, " +
                                "Present BOOLEAN NOT NULL " +
                                ")"
                );

                dbStatement.executeUpdate(
                        "CREATE TABLE IF NOT EXISTS time_table ( " +
                                "id SERIAL PRIMARY KEY, " +
                                "class_Name VARCHAR(100) NOT NULL, " +
                                "class_id INT," +
                                "lecturer_Name VARCHAR(100), " +
                                "lecturer_id INT, " +
                                "module_Name VARCHAR(100)," +
                                "module_id INT REFERENCES Modules(ModuleID), " +
                                "time TIME, " +
                                "FOREIGN KEY (class_id) REFERENCES classes(class_id) ON DELETE SET NULL, " +
                                "FOREIGN KEY (lecturer_id) REFERENCES employee(id) ON DELETE SET NULL " +
                                ")"
                );

                System.out.println("Tables initialized successfully.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Load MySQL JDBC driver
            conn = DriverManager.getConnection(URL + DB_NAME, USER, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace(); // Handle the error
        }
        return conn;
    }
}
