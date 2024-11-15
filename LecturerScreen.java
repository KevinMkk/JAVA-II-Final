package com.example.luctfxapp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.stage.Stage;

import java.sql.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class LecturerScreen {

    public Button logoutButton;
    public TableColumn studentGenderColumn;
    @FXML
    private ComboBox<String> classComboBox;

    private String UserName;
    @FXML
    private TextField chapterField;

    @FXML
    private TextArea outcomeField;

    @FXML
    private TableView<Student> attendanceTable;

    @FXML
    private TableColumn<Student, Boolean> presentColumn;

    @FXML
    private TableColumn<Student, Number> studentIdColumn;  // Use Number type for IntegerProperty

    @FXML
    private TableColumn<Student, String> studentNameColumn;

    @FXML
    private DatePicker classDatePicker;

    private final ObservableList<Student> studentsInClass = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        clearForm();
        setupAttendanceTable();
        loadClassOptions();

        // Set a listener to load students when a class is selected
        classComboBox.getSelectionModel().selectedItemProperty().addListener((_, _, newValue) -> {
            if (newValue != null) {
                loadStudentsForClass();
            }
        });
    }

    // Load available class names into the ComboBox
    private void loadClassOptions() {
        String query = "SELECT DISTINCT class_name FROM classes";
        try (Connection connection = DatabaseManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                classComboBox.getItems().add(resultSet.getString("class_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Failed to load classes.");
        }
    }

    // Clear form fields
    private void clearForm() {
        chapterField.clear();
        outcomeField.clear();
        classDatePicker.setValue(null);
        classComboBox.getSelectionModel().clearSelection();
        studentsInClass.clear();
    }

    // Set up the attendance table
    private void setupAttendanceTable() {
        studentIdColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        studentNameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        presentColumn.setCellValueFactory(cellData -> cellData.getValue().presentProperty());
        presentColumn.setCellFactory(CheckBoxTableCell.forTableColumn(presentColumn));
        attendanceTable.setItems(studentsInClass);
        attendanceTable.setEditable(true);
    }

    // Load students for a selected class
    private void loadStudentsForClass() {
        String selectedClass = classComboBox.getSelectionModel().getSelectedItem();
        if (selectedClass != null) {
            String sql = "SELECT student_id, student_name, student_gender FROM " + selectedClass;

            try (Connection connection = DatabaseManager.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet resultSet = statement.executeQuery()) {

                studentsInClass.clear();

                while (resultSet.next()) {
                    int studentId = resultSet.getInt("student_id");
                    String studentName = resultSet.getString("student_name");
                    String studentGender = resultSet.getString("student_gender"); // Added gender if needed
                    studentsInClass.add(new Student(studentId, studentName, studentGender)); // Assuming Student class constructor takes 3 params
                }

                attendanceTable.setItems(studentsInClass);
            } catch (SQLException e) {
                e.printStackTrace();
                showError("Failed to load students for the selected class.");
            }
        }
    }

    // Save attendance and outcomes
    @FXML
    private void saveAttendanceAndOutcomes() {
        LocalDate classDate = classDatePicker.getValue();
        String selectedClass = classComboBox.getValue();

        if (classDate == null || selectedClass == null || chapterField.getText().isEmpty() || outcomeField.getText().isEmpty()) {
            showError("All fields are required.");
            return;
        }

        if (isDuplicateAttendance(classDate, selectedClass)) {
            showError("Attendance for this date and class has already been recorded.");
            return;
        }

        saveDataToDatabase(classDate, selectedClass);
    }

    // Check for duplicate attendance records
    private boolean isDuplicateAttendance(LocalDate classDate, String selectedClass) {
        String query = "SELECT * FROM attendance_records WHERE class_date = ? AND class_name = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setDate(1, Date.valueOf(classDate));
            statement.setString(2, selectedClass);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Save attendance and learning outcomes to the database
    private void saveDataToDatabase(LocalDate classDate, String selectedClass) {
        String insertQuery = "INSERT INTO Attendance_Records (Class_Date, Class_Name, Chapter, Learning_Outcomes, Student_ID, Student_Name, Present) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(insertQuery)) {

            for (Student student : studentsInClass) {
                statement.setDate(1, Date.valueOf(classDate));
                statement.setString(2, selectedClass);
                statement.setString(3, chapterField.getText());
                statement.setString(4, outcomeField.getText());
                statement.setInt(5, student.getId());
                statement.setString(6, student.getName());
                statement.setBoolean(7, student.isPresent());
                statement.addBatch();
            }

            statement.executeBatch();
            showConfirmation();
            clearForm();
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Failed to save data. Please try again.");
        }
    }

    // Show error alert
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Show confirmation alert
    private void showConfirmation() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("Attendance and learning outcomes saved successfully!");
        alert.showAndWait();
    }

    // Handle Logout Button Action
    @FXML
    private void handleLogout() {
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

    public void setUserName(String username) {
        UserName=username;
    }
}
