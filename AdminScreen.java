package com.example.luctfxapp;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.*;
import java.time.Duration;
import java.time.LocalDateTime;

public class AdminScreen {

    public Label UserName;

    public ImageView profileImageView;
    public AnchorPane messagePane;
    public Label messageLabel;
    public Button closeMessageButton;
    public GridPane employeeGrid;
    public Label academicYearLabel;
    public Label semesterLabel;
    public GridPane classGrid;
    public GridPane studentsGridPane;
    @FXML
    private Pane Welcome;
    @FXML
    private AnchorPane Profile;
    @FXML
    private AnchorPane Lecturers;
    @FXML
    private AnchorPane Students;
    @FXML
    private AnchorPane Academics;

    @FXML
    private Button profileButton;
    @FXML
    private Button lecturersButton;
    @FXML
    private Button studentsButton;
    @FXML
    private Button academicsButton;
    @FXML
    private Button logoutButton;

    @FXML
    private TextField userNameField, positionField, employmentDateField, contactInfoField, addressField, employeeNumberField, namesField;

    private Connection connection;
    @FXML
    public void initialize() {
        // Show the Welcome section by default
        showWelcome();

        // Load employee details when profile is displayed
        profileButton.setOnAction(_ -> {
            showProfile();
            loadEmployeeDetails(); // Load details when profile is shown
        });

        lecturersButton.setOnAction(_ -> {
            showLecturers();
            loadEmployees(); // Load employees when Lecturers section is shown
        });

        studentsButton.setOnAction(_ -> showStudents());
        academicsButton.setOnAction(_ -> showAcademics());
        logoutButton.setOnAction(_ -> logout());

        closeMessageButton.setOnAction(_ -> messagePane.setVisible(false));

        connection = DatabaseManager.getConnection();
        loadCurrentSemester();
        loadStudentDetails();
        loadClassDetails();
    }

    public void setUserName(String userName) {
        UserName.setText(userName); // Display the logged-in username
    }

    private void showWelcome() {
        Welcome.setVisible(true);
        Profile.setVisible(false);
        Lecturers.setVisible(false);
        Students.setVisible(false);
        Academics.setVisible(false);
    }

    private void showProfile() {
        Welcome.setVisible(false);
        Profile.setVisible(true);
        Lecturers.setVisible(false);
        Students.setVisible(false);
        Academics.setVisible(false);
    }

    private void showLecturers() {
        Welcome.setVisible(false);
        Profile.setVisible(false);
        Lecturers.setVisible(true);
        Students.setVisible(false);
        Academics.setVisible(false);
    }

    private void showStudents() {
        Welcome.setVisible(false);
        Profile.setVisible(false);
        Lecturers.setVisible(false);
        Students.setVisible(true);
        Academics.setVisible(false);
    }

    private void showAcademics() {
        Welcome.setVisible(false);
        Profile.setVisible(false);
        Lecturers.setVisible(false);
        Students.setVisible(false);
        Academics.setVisible(true);
    }

    private void logout() {
        // Handle logout logic here
        String username=UserName.getText();
        logLogout(username);

        System.out.println("User " + username +" logging out...");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("Admin.fxml"));

        try {
            AnchorPane newRoot = loader.load();
            Scene scene = new Scene(newRoot);
            Stage primaryStage = (Stage) logoutButton.getScene().getWindow();
            primaryStage.setScene(scene);
        } catch (IOException e) {
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

    private void loadEmployeeDetails() {
        String adminUsername = UserName.getText();

        try (Connection connection = DatabaseManager.getConnection()) {
            String query = "SELECT e.name, e.surname, e.position, e.email, e.phone_number, " +
                    "e.address, e.date_of_employment, e.employee_id, e.display_picture " +
                    "FROM admin a INNER JOIN employee e ON a.employee_id = e.id " +
                    "WHERE a.username = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, adminUsername);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    namesField.setText(resultSet.getString("name") + " " + resultSet.getString("surname"));
                    userNameField.setText(adminUsername);
                    positionField.setText(resultSet.getString("position"));
                    employmentDateField.setText(resultSet.getDate("date_of_employment").toString());
                    contactInfoField.setText(resultSet.getString("email") + ", " + resultSet.getString("phone_number"));
                    employeeNumberField.setText(resultSet.getString("employee_id")); // This field should be read-only
                    addressField.setText(resultSet.getString("address"));

                    Blob displayPictureBlob = resultSet.getBlob("display_picture");
                    InputStream inputStream = null;
                    if (displayPictureBlob != null) {
                        inputStream = displayPictureBlob.getBinaryStream();
                    } else {
                        // Handle the case where displayPictureBlob is null, perhaps by loading a default image
                        System.out.println("No display picture available for this entry.");
                    }

                    if (inputStream != null) {
                        Image profileImage = new Image(inputStream);
                        profileImageView.setImage(profileImage);
                    } else {
                        // You may want to load a placeholder or skip image processing
                        System.err.println("No set profile picture.");
                    }
                } else {
                    // Handle the case where no employee details are found
                    System.out.println("No employee details found.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private File profileImageFile; // File to hold the selected profile picture

    @FXML
    private void updateEmployeeDetails(){
        String adminUsername = userNameField.getText();
        String name = namesField.getText().split(" ")[0].trim();
        String surname = namesField.getText().split(" ")[1].trim();
        String email = contactInfoField.getText().split(",")[0].trim();
        String phoneNumber = contactInfoField.getText().split(",")[1].trim();
        String address = addressField.getText();
        String employeeId = employeeNumberField.getText();
        int id = 0;

        try (Connection connection = DatabaseManager.getConnection()) {
            String updateQuery = "UPDATE employee SET name = ?, surname = ?, email = ?," +
                    " phone_number = ?, address = ? WHERE employee_id = ?";
            String updatePicQuery = "UPDATE employee SET display_picture = ? WHERE employee_id = ?";
            String getIDQuery = "SELECT id from employee WHERE employee_id = ?";
            String updateUserNameQuery = "UPDATE admin SET username = ? WHERE employee_id = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(getIDQuery)) {
                preparedStatement.setString(1, employeeId);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    id=resultSet.getInt("id");
                }
            }
            
            try (PreparedStatement preparedStatement = connection.prepareStatement(updateUserNameQuery)) {
                preparedStatement.setString(1, adminUsername);
                preparedStatement.setInt(2, id);
                preparedStatement.executeUpdate();
            }

            try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, surname);
                preparedStatement.setString(3, email);
                preparedStatement.setString(4, phoneNumber);
                preparedStatement.setString(5, address);
                preparedStatement.setString(6, employeeId);
                preparedStatement.executeUpdate();
            }

            try (PreparedStatement preparedStatement = connection.prepareStatement(updatePicQuery)) {
                preparedStatement.setString(2, employeeId);

                if (profileImageFile != null) {
                    System.out.println("Profile image file path: " + profileImageFile.getAbsolutePath());
                    try (FileInputStream fis = new FileInputStream(profileImageFile)) {
                        preparedStatement.setBlob(1, fis);
                        preparedStatement.executeUpdate();
                        System.out.println("Image updated successfully.");
                    } catch (IOException e) {
                        System.err.println("Error reading profile image file: " + e.getMessage());
                        preparedStatement.setNull(1, Types.BLOB); // Set to null if there’s an issue
                    }
                } else {
                    preparedStatement.setNull(1, Types.BLOB);
                }
            }
            showMessage("Profile updated successfully.");
        } catch (SQLException e) {
            showMessage("Error updating profile.");
            e.printStackTrace();
        }
    }

    // Function to handle profile picture change
    @FXML
    private void changeDisplayPicture() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Display Picture");
        fileChooser.getExtensionFilters().addAll(
                //new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")

        );

        profileImageFile = fileChooser.showOpenDialog(null);
        if (profileImageFile != null) {
            Image image = new Image(profileImageFile.toURI().toString());
            profileImageView.setImage(image); // Display the image in the ImageView
        }
    }

    private void loadEmployees() {
        try (Connection connection = DatabaseManager.getConnection()) {
            String query = "SELECT employee_id, name, position, email, phone_number FROM employee";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                ResultSet resultSet = preparedStatement.executeQuery();

                int row = 1; // Start after header row
                while (resultSet.next()) {
                    String employeeId = resultSet.getString("employee_id");
                    String name = resultSet.getString("name");
                    String position = resultSet.getString("position");
                    String contactInfo = resultSet.getString("email") + ", " + resultSet.getString("phone_number");

                    // Add data to new row
                    employeeGrid.add(new Label(employeeId), 0, row);
                    employeeGrid.add(new Label(name), 1, row);
                    employeeGrid.add(new Label(position), 2, row);
                    employeeGrid.add(new Label(contactInfo), 3, row);

                    // Optional: Add a "Select" button for each row
                    Button selectButton = new Button("Select");
                    selectButton.setOnAction(_ -> handleSelect(employeeId, selectButton.getScene().getWindow()));
                    employeeGrid.add(selectButton, 4, row);

                    row++;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void handleSelect(String employeeId, Window ownerWindow) {
        // Implement the logic for what happens when an employee is selected.
        System.out.println("Selected Employee ID: " + employeeId);

        String username = UserName.getText();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("PositionScreen.fxml"));
        try {
            // Load the new FXML file
            AnchorPane newRoot = loader.load();
            Scene scene = new Scene(newRoot);

            PositionScreen controller = loader.getController();
            controller.setEmployeeID(employeeId);
            controller.setUserName(username);

            // Create and show the new stage
            Stage newStage = new Stage();
            newStage.initOwner(ownerWindow); // Set the owner for better stage management
            newStage.setScene(scene);
            newStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadCurrentSemester() {
        String query = "SELECT year1, year2, semester FROM semester ORDER BY id DESC LIMIT 1";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            if (resultSet.next()) {
                int year1 = resultSet.getInt("year1");
                int year2 = resultSet.getInt("year2");
                int semester = resultSet.getInt("semester");

                academicYearLabel.setText(year1 + "/" + year2);
                semesterLabel.setText("Semester " + semester);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void addNewSemester() {
        String query = "SELECT year1, year2, semester FROM semester ORDER BY id DESC LIMIT 1";
        try (PreparedStatement selectStatement = connection.prepareStatement(query);
             ResultSet resultSet = selectStatement.executeQuery()) {

            if (resultSet.next()) {
                int year1 = resultSet.getInt("year1");
                int year2 = resultSet.getInt("year2");
                int semester = resultSet.getInt("semester");

                if (semester == 1) {
                    semester = 2;
                } else {
                    semester = 1;
                    year1++;
                    year2++;
                }

                String insertQuery = "INSERT INTO semester (year1, year2, semester) VALUES (?, ?, ?)";
                try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                    insertStatement.setInt(1, year1);
                    insertStatement.setInt(2, year2);
                    insertStatement.setInt(3, semester);
                    insertStatement.executeUpdate();

                    academicYearLabel.setText(year1 + "/" + year2);
                    semesterLabel.setText("Semester " + semester);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadClassDetails() {
        String query = "SELECT class_id, class_name, faculty FROM classes"; // Example SQL
        try (PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            int row = 1; // Start from row 1 since row 0 is for headers
            while (resultSet.next()) {
                // Create labels for each column data
                Label idLabel = new Label(resultSet.getString("class_id"));
                Label classNameLabel = new Label(resultSet.getString("class_name"));
                Label facultyLabel = new Label(resultSet.getString("faculty"));

                // Add these labels to the GridPane in specified columns
                classGrid.add(idLabel, 0, row);
                classGrid.add(classNameLabel, 1, row);
                classGrid.add(facultyLabel, 2, row);

                row++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadStudentDetails() {
        try (Connection connection = DatabaseManager.getConnection()) {
            String query = "SELECT student_id, student_name, student_surname, class_name, phone_number, email FROM students";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);



            // Initialize row index for GridPane
            int rowIndex = 1;
            while (rs.next()) {
                int id = rs.getInt("student_id");
                String name = rs.getString("student_name");
                String surname = rs.getString("student_surname");
                String fullName = name + " " + surname;
                String course = rs.getString("class_name");
                String phone = rs.getString("phone_number");
                String email = rs.getString("email");
                String contactInfo = phone + " " + email;

                // Create labels for each student detail
                Label idLabel = new Label(String.valueOf(id));
                Label fullNameLabel = new Label(fullName);
                Label courseLabel = new Label(course);
                Label contactInfoLabel = new Label(contactInfo);

                // Add labels to the GridPane
                studentsGridPane.add(idLabel, 0, rowIndex);
                studentsGridPane.add(fullNameLabel, 1, rowIndex);
                studentsGridPane.add(courseLabel, 2, rowIndex);
                studentsGridPane.add(contactInfoLabel, 3, rowIndex);

                // You can also add a "Select" button for each student to view more details or edit
                Button selectButton = new Button("Select");
                selectButton.setOnAction(_ -> {
                    // Handle selection (e.g., navigate to student details page)
                });
                studentsGridPane.add(selectButton, 4, rowIndex);

                // Increment the row index
                rowIndex++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showMessage(String message) {
        messageLabel.setText(message);
        messagePane.setVisible(true);
    }
}
