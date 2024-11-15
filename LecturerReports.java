package com.example.luctfxapp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.sql.*;
import java.time.Duration;
import java.time.LocalDateTime;

public class LecturerReports {
    public Button logoutButton;
    public Button backButton;
    @FXML
    private TableView<AttendanceRecord> attendanceTable;
    @FXML
    private TableColumn<AttendanceRecord, String> colClassDate;
    @FXML
    private TableColumn<AttendanceRecord, String> colClassName;
    @FXML
    private TableColumn<AttendanceRecord, String> colChapter;
    @FXML
    private TableColumn<AttendanceRecord, String> colStudentName;
    @FXML
    private TableColumn<AttendanceRecord, Boolean> colPresent;

    private String UserName;

    @FXML
    public void initialize() {
        backButton.setOnAction(this::handleBackAction);
        colClassDate.setCellValueFactory(new PropertyValueFactory<>("classDate"));
        colClassName.setCellValueFactory(new PropertyValueFactory<>("className"));
        colChapter.setCellValueFactory(new PropertyValueFactory<>("chapter"));
        colStudentName.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        colPresent.setCellValueFactory(new PropertyValueFactory<>("present"));
        loadAttendanceRecords();
    }

    private void loadAttendanceRecords() {
        ObservableList<AttendanceRecord> records = FXCollections.observableArrayList();
        String query = "SELECT Class_Date, Class_Name, Chapter, Student_Name, Present FROM attendance_records";
        try (Connection connection = DatabaseManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(query)) {
            while (rs.next()) {
                records.add(new AttendanceRecord(
                        rs.getDate("Class_Date").toString(),
                        rs.getString("Class_Name"),
                        rs.getString("Chapter"),
                        rs.getString("Student_Name"),
                        rs.getBoolean("Present")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        attendanceTable.setItems(records);
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
    private void handleBackAction(ActionEvent event) {
        try {
            // Load the PrincipalLecturerScreen FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("PrincipalLecturerScreen.fxml"));
            Parent root = loader.load();

            // Close the current window
            Stage currentStage = (Stage) backButton.getScene().getWindow();
            currentStage.close();

            // Open the PrincipalLecturerScreen
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            newStage.setTitle("Principal Lecturer Screen");
            newStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
