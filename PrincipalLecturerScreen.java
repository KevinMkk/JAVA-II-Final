package com.example.luctfxapp;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class PrincipalLecturerScreen {
    public Button logoutButton;
    public Button ViewLecturerReports;
    @FXML
    private ComboBox<String> moduleBox;

    @FXML
    private TextArea reportArea;

    @FXML
    private TextField classField;

    @FXML
    private TextArea challengesArea;

    @FXML
    private TextArea recommendationsArea;

    @FXML
    private Button submitReportButton;

    @FXML
    private Button submitWeeklyReportButton;

    private Connection connection;

    private String UserName;

    @FXML
    public void initialize() {
        // Initialize the database connection
        initializeDatabaseConnection();

        // Initialize ComboBox with module options from the database
        if (connection != null) {
            List<String> modules = selectModules();
            moduleBox.getItems().addAll(modules);
        } else {
            System.err.println("Database connection is not established.");
        }

        // Set button actions
        submitReportButton.setOnAction(_ -> handleSubmitReport());
        submitWeeklyReportButton.setOnAction(_ -> handleSubmitWeeklyReport());

        // Add listeners to enable the buttons once the required fields are populated
        moduleBox.valueProperty().addListener((_, _, _) -> enableSubmitReportButton());
        reportArea.textProperty().addListener((_, _, _) -> enableSubmitReportButton());

        classField.textProperty().addListener((_, _, _) -> enableSubmitWeeklyReportButton());
        challengesArea.textProperty().addListener((_, _, _) -> enableSubmitWeeklyReportButton());
        recommendationsArea.textProperty().addListener((_, _, _) -> enableSubmitWeeklyReportButton());
    }

    // Method to enable/disable the Submit Report button based on field values
    private void enableSubmitReportButton() {
        submitReportButton.setDisable(moduleBox.getValue() == null || reportArea.getText().isEmpty());
    }

    // Method to enable/disable the Submit Weekly Report button based on field values
    private void enableSubmitWeeklyReportButton() {
        submitWeeklyReportButton.setDisable(
                classField.getText().isEmpty() ||
                        challengesArea.getText().isEmpty() ||
                        recommendationsArea.getText().isEmpty()
        );
    }

    // Method to handle the submission of a report
    @FXML
    private void handleSubmitReport() {
        String module = moduleBox.getValue();
        String report = reportArea.getText();

        if (module == null || report.isEmpty()) {
            showAlert("Error", "Please fill in all fields for report submission!");
        } else {
            insertReportIntoDatabase(module, report);
            showAlert("Success", "Report for " + module + " submitted successfully!");
            reportArea.clear();
        }
    }

    // Method to handle the submission of a weekly report
    @FXML
    private void handleSubmitWeeklyReport() {
        String className = classField.getText();
        String challenges = challengesArea.getText();
        String recommendations = recommendationsArea.getText();

        if (className.isEmpty() || challenges.isEmpty() || recommendations.isEmpty()) {
            showAlert("Error", "Please fill in all fields for weekly report submission!");
        } else {
            insertWeeklyReportIntoDatabase(className, challenges, recommendations);
            showAlert("Success", "Weekly report submitted successfully!");
            classField.clear();
            challengesArea.clear();
            recommendationsArea.clear();
        }
    }

    // Method to show alerts for success or error messages
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Method to initialize the database connection
    private void initializeDatabaseConnection() {
        String url = "jdbc:mysql://localhost:3306/luct_fx_db"; // Adjust your database URL
        String user = "root"; // Your database username
        String password = "123456"; // Your database password

        try {
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the MySQL database successfully!");
        } catch (SQLException e) {
            System.err.println("Failed to connect to MySQL database: " + e.getMessage());
        }
    }

    // Method to fetch all modules from the database and return as a list
    private List<String> selectModules() {
        List<String> moduleNames = new ArrayList<>();
        String query = "SELECT ModuleName FROM Modules"; // The SELECT query to fetch module names

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            // Iterate through the result set and add module names to the list
            while (rs.next()) {
                String moduleName = rs.getString("ModuleName");
                moduleNames.add(moduleName);
            }

        } catch (SQLException e) {
            System.err.println("Failed to fetch modules: " + e.getMessage());
        }

        return moduleNames;
    }

    // Method to insert a report into the database
    private void insertReportIntoDatabase(String module, String report) {
        String query = "INSERT INTO Reports (ModuleID, ReportText) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            // Get the ModuleID by querying the Modules table
            String selectModuleIdQuery = "SELECT ModuleID FROM Modules WHERE ModuleName = ?";
            try (PreparedStatement selectStmt = connection.prepareStatement(selectModuleIdQuery)) {
                selectStmt.setString(1, module);
                try (ResultSet rs = selectStmt.executeQuery()) {
                    if (rs.next()) {
                        int moduleId = rs.getInt("ModuleID");
                        stmt.setInt(1, moduleId);
                        stmt.setString(2, report);
                        stmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Failed to insert report: " + e.getMessage());
        }
    }

    // Method to insert a weekly report into the database
    private void insertWeeklyReportIntoDatabase(String className, String challenges, String recommendations) {
        String query = "INSERT INTO WeeklyReports (ClassName, Challenges, Recommendations) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, className);
            stmt.setString(2, challenges);
            stmt.setString(3, recommendations);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Failed to insert weekly report: " + e.getMessage());
        }
    }

    public void setUserName(String username) {
        UserName=username;
    }

    public void handleLogout() {
        try {
            String username=UserName;
            logLogout(username);
            // Close the current window
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.close();

            // Open the login screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Admin.fxml"));
            Parent root = loader.load();
            Stage loginStage = new Stage();
            loginStage.setScene(new Scene(root));
            loginStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void logLogout(String username) {
        try (Connection connection = DatabaseManager.getConnection()) {
            // Get the last login entry for this user
            String selectQuery = "SELECT id, entry_log FROM logs WHERE username = ? ORDER BY entry_log DESC LIMIT 1";
            PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
            selectStatement.setString(1, username);
            ResultSet resultSet = selectStatement.executeQuery();

            if (resultSet.next()) {
                int logId = resultSet.getInt("id");
                Timestamp entryLogTimestamp = resultSet.getTimestamp("entry_log");

                // Update the log with exit time and duration
                String updateQuery = "UPDATE logs SET exit_log = ?, log_duration = ? WHERE id = ?";
                PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                Timestamp exitLogTimestamp = Timestamp.valueOf(LocalDateTime.now());
                updateStatement.setTimestamp(1, exitLogTimestamp);

                // Calculate duration
                Duration duration = Duration.between(entryLogTimestamp.toLocalDateTime(), exitLogTimestamp.toLocalDateTime());
                updateStatement.setTime(2, Time.valueOf(duration.toHours() + ":" + duration.toMinutesPart() + ":" + duration.toSecondsPart()));
                updateStatement.setInt(3, logId);

                updateStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void handleViewReports() {
        String username = UserName;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("LecturerReports.fxml"));
        try {
            Stage stage = (Stage) ViewLecturerReports.getScene().getWindow();
            stage.close();
            // Load the new FXML file
            AnchorPane root = loader.load();

            LecturerReports controller = loader.getController();
            controller.setUserName(username);

            // Create and show the new stage
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            newStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
