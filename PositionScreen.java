package com.example.luctfxapp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;


import java.io.InputStream;
import java.sql.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class PositionScreen {

    public Label empNames;
    public AnchorPane Profile;
    public ImageView profileImageView;
    public Label FirstName;
    public Label Surname;
    public Label Position;
    public Label DateEmp;
    public Label Email;
    public Label Number;
    public Label EMPNumber;
    public Label Address;
    public Label messageLabel;
    public AnchorPane messagePane;
    public Button closeMessageButton;

    public String EmployeeId;
    public AnchorPane positionSetPane;
    public Button setPositionButton;
    public Label selectedEmployeeName;
    public ComboBox<String> positionComboBox;
    public ListView<Employee> employeeListView;
    public ListView<String> notificationsList;
    public TextField responseTextField;
    public Button respondButton;
    public Label academicYearLabel;
    public Label semesterLabel;
    public TextField timeTextField;
    public TextField moduleTextField;
    public TextField classNameTextField;
    public ScrollPane timeTableScrollPane;
    public GridPane timeTableGrid;
    public Button update;
    public AnchorPane anchorPane;
    private String employeeId;

    @FXML
    private Button backButton;

    @FXML
    private Button logoutButton;
    private String UserName;

    private Connection connection;

    private final ObservableList<Employee> employees = FXCollections.observableArrayList();

    public static class Employee {
        private final String name;
        private final String employeeId;
        private String position;
        private String dateOfEmployment;
        private String email;
        private String address;
        private Image profileImage;

        public Employee(String name, String employeeId) {
            this.name = name;
            this.employeeId = employeeId;
        }

        public Employee(String name, String employeeId, String position, String dateOfEmployment, String email, String address, Image profileImage) {
            this.name = name;
            this.employeeId = employeeId;
            this.position = position;
            this.dateOfEmployment = dateOfEmployment;
            this.email = email;
            this.address = address;
            this.profileImage = profileImage;
        }

        public String getName() {
            return name;
        }

        public String getEmployeeId() {
            return employeeId;
        }

        public String getPosition() {
            return position;
        }

        public String getDateOfEmployment() {
            return dateOfEmployment;
        }

        public String getEmail() {
            return email;
        }

        public String getAddress() {
            return address;
        }

        public Image getProfileImage() {
            return profileImage;
        }

        // Constructor, getters, and setters
    }

    @FXML
    public void initialize() {
        backButton.setOnAction(this::handleBackAction);
        logoutButton.setOnAction(this::handleLogoutAction);
        connection = DatabaseManager.getConnection();
        loadCurrentSemester();

        ObservableList<String> notifications = FXCollections.observableArrayList(
                "Employee activity message goes here",
                "Another notification"
        );
        notificationsList.setItems(notifications);

        // Handle click on notification
        notificationsList.setOnMouseClicked(_ -> {
            String selectedMessage = notificationsList.getSelectionModel().getSelectedItem();
            if (selectedMessage != null) {
                messageLabel.setText(selectedMessage);  // Show message in messagePane
                messagePane.setVisible(true);  // Show messagePane
            }
        });

        // Respond button action
        respondButton.setOnAction(_ -> {
            String response = responseTextField.getText();
            if (!response.isEmpty()) {
                // Process response, e.g., send it back to the server
                System.out.println("Response sent: " + response);
                responseTextField.clear();  // Clear the input field
            }
        });

        // Close the message pane
        closeMessageButton.setOnAction(_ -> messagePane.setVisible(false));
        employeeListView.setItems(employees);
        employeeListView.setCellFactory(new Callback<>() {
            @Override
            public ListCell<Employee> call(ListView<Employee> listView) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(Employee employee, boolean empty) {
                        super.updateItem(employee, empty);
                        if (empty || employee == null) {
                            setText(null);
                            setStyle("");
                        } else {
                            setText(employee.getName() + " (" + employee.getEmployeeId() + ")");
                            setStyle("-fx-font-weight: bold; -fx-text-fill: black;");
                            if (employee == employeeListView.getSelectionModel().getSelectedItem()) {
                                setStyle("-fx-background-color: #0078d4; -fx-font-weight: bold; -fx-text-fill: black;");
                            }
                        }
                    }
                };
            }
        });

        // Add listener to update employee details on selection
        employeeListView.getSelectionModel().selectedItemProperty().addListener((_, _, newSelection) -> {
            if (newSelection != null) {
                updateEmployeeDetails(newSelection);
            }
        });
        loadEmployeeData();
        loadReportsData();
    }

    private void updateEmployeeDetails(Employee employee) {
        FirstName.setText(employee.getName());
        Surname.setText(employee.getEmployeeId());
        Position.setText(employee.getPosition());
        DateEmp.setText(employee.getDateOfEmployment());
        Email.setText(employee.getEmail());
        Address.setText(employee.getAddress());
        profileImageView.setImage(employee.getProfileImage());

        selectedEmployeeName.setText(employee.getName() + " " + employee.getEmployeeId());

        // Optionally, set the global EmployeeId variable to the pressed employee's ID
        EmployeeId = employee.getEmployeeId();
        setEmployeeID(EmployeeId);
    }

    private void loadEmployeeData() {
        // Clear the ListView before loading new data
        employeeListView.getItems().clear();

        // SQL query to fetch employee names and employee_ids from the database
        String query = "SELECT name, employee_id FROM employee";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            // Iterate over the result set and add Employee objects to the ListView
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String employeeId = resultSet.getString("employee_id");

                // Create a new Employee object
                Employee employee = new Employee(name, employeeId);

                // Add the employee to the ListView
                employeeListView.getItems().add(employee);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setEmployeeID(String employeeId) {
        EmployeeId = employeeId;
        loadEmployeeDetails();
    }

    private void loadEmployeeDetails() {
        try (Connection connection = DatabaseManager.getConnection()) {
            String query = "SELECT id, name, surname, position, email, phone_number, " +
                    "address, date_of_employment, display_picture " +
                    "FROM employee " +
                    "WHERE employee_id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, EmployeeId);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    FirstName.setText(resultSet.getString("name"));
                    Surname.setText(resultSet.getString("surname"));
                    Position.setText(resultSet.getString("position"));
                    DateEmp.setText(resultSet.getDate("date_of_employment").toString());
                    Email.setText(resultSet.getString("email"));
                    Number.setText(resultSet.getString("phone_number"));
                    EMPNumber.setText(EmployeeId);
                    Address.setText(resultSet.getString("address"));
                    empNames.setText(FirstName.getText() + " " + Surname.getText());
                    selectedEmployeeName.setText(FirstName.getText() + " " + Surname.getText());
                    employeeId=resultSet.getString("id");

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
                        System.err.println("Error reading profile image from Blob: ");
                    }

                } else {
                    // Handle the case where no employee details are found
                    System.out.println("No employee details found.");
                    showMessage("No employee details found.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        loadTimeTable();
    }

    public void showPositionSetPane() {
        positionSetPane.setVisible(true);
    }

    public void setPosition() {
        String selectedPosition = positionComboBox.getValue();
        if (selectedPosition != null) {
            try (Connection connection = DatabaseManager.getConnection()) {
                // Update position in the employee table
                String updatePositionQuery = "UPDATE employee SET position = ? WHERE employee_id = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(updatePositionQuery)) {
                    String empNum = EMPNumber.getText();
                    preparedStatement.setString(1, selectedPosition);
                    preparedStatement.setString(2, empNum);

                    int rowsUpdated = preparedStatement.executeUpdate();
                    if (rowsUpdated > 0) {
                        System.out.println("Employee position updated successfully.");
                    } else {
                        System.out.println("Update not successful for position: " + selectedPosition + " with employee id - " + empNum);
                    }
                }

                String checkSQL;
                String insertSQL;
                String deleteSQL;

                if (selectedPosition.equals("Principal Lecturer")) {
                    checkSQL ="SELECT employee_id FROM principal_lecturer WHERE username = ?";
                    insertSQL = "INSERT INTO principal_lecturer (username, password, employee_id) VALUES (?, ?, ?)";
                    deleteSQL = "DELETE FROM lecturer WHERE employee_id = ?";
                } else if (selectedPosition.equals("Lecturer")) {
                    checkSQL = "SELECT employee_id FROM lecturer WHERE username = ?";
                    insertSQL = "INSERT INTO lecturer (username, password, employee_id) VALUES (?, ?, ?)";
                    deleteSQL = "DELETE FROM principal_lecturer WHERE employee_id = ?";
                } else {
                    throw new IllegalArgumentException("Invalid position");
                }

                String username=FirstName.getText();
                String password=EMPNumber.getText();

                // Delete any existing record in the other position table
                try (PreparedStatement deleteStmt = connection.prepareStatement(deleteSQL)) {
                    deleteStmt.setString(1, String.valueOf(Integer.parseInt(employeeId)));
                    int rowsDeleted = deleteStmt.executeUpdate();
                    if (rowsDeleted > 0) {
                        System.out.println("Old position record deleted successfully.");
                        showMessage("Old position record deleted successfully.");
                    }
                }

                try (PreparedStatement checkStmt = connection.prepareStatement(checkSQL)){
                    System.out.println("User name: " + username);
                    checkStmt.setString(1,username);
                    try (ResultSet resultSet = checkStmt.executeQuery()) {
                        if (resultSet.next()) {
                            System.out.println("Employee ID already exists in the target table.");
                        } else {
                            // If the employee ID does not exist, insert a new record
                            try (PreparedStatement insertStmt = connection.prepareStatement(insertSQL)) {
                                insertStmt.setString(1, username);
                                insertStmt.setString(2, password);
                                insertStmt.setInt(3, Integer.parseInt(employeeId)); // foreign key reference to employee table
                                insertStmt.executeUpdate();
                                System.out.println("New position record inserted successfully.");
                            }
                        }
                    }
                }

                connection.close();
                positionSetPane.setVisible(false);
                showMessage("Position updated successfully.");
            } catch (Exception e) {
                showMessage("Error updating position.");
                e.printStackTrace();
            }
        }
    }

    private void handleBackAction(ActionEvent event) {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close(); // Close the current stage (go back to the previous screen)
    }

    private void handleLogoutAction(ActionEvent event) {
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

    private void loadCurrentSemester() {
        String query = "SELECT year1, year2, semester FROM semester ORDER BY id DESC LIMIT 1";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            if (resultSet.next()) {
                int year1 = resultSet.getInt("year1");
                int year2 = resultSet.getInt("year2");
                int semester = resultSet.getInt("semester");

                academicYearLabel.setText(year1 + "/" + year2);
                semesterLabel.setText(String.valueOf(semester));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadTimeTable() {
        timeTableGrid.getChildren().clear();
        System.out.println("Employee ID = "+employeeId);
        String query = "SELECT tt.*, c.class_name, m.ModuleName " +
                "FROM time_table tt " +
                "JOIN classes c ON tt.class_id = c.class_id " +
                "JOIN modules m ON tt.module_id = m.ModuleID " +
                "WHERE tt.lecturer_id = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, employeeId);
            int rowIndex;
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                rowIndex = 1;
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String className = resultSet.getString("class_name");
                    String moduleName = resultSet.getString("ModuleName");
                    Time time = resultSet.getTime("time");

                    TextField classField = new TextField(className);
                    TextField moduleField = new TextField(moduleName);
                    TextField timeField = new TextField(time != null ? time.toString() : "");

                    Button deleteButton = new Button("Delete");
                    deleteButton.setOnAction(_ -> deleteTimeTableRow(id));

                    Button updateButton = new Button("Update");
                    updateButton.setOnAction(_ -> updateTimeTableRow(id, classField, moduleField, timeField));

                    timeTableGrid.addRow(rowIndex++, classField, moduleField, timeField, deleteButton, updateButton);
                }
            }
            // Fetch options for dropdowns
            List<String> classOptions = fetchOptions("SELECT class_name FROM classes");
            List<String> moduleOptions = fetchOptions("SELECT ModuleName FROM modules");

            // Add row for new entries
            ComboBox<String> newClassComboBox = new ComboBox<>(FXCollections.observableArrayList(classOptions));
            ComboBox<String> newModuleComboBox = new ComboBox<>(FXCollections.observableArrayList(moduleOptions));
            TextField newTimeField = new TextField();

            Button addButton = new Button("Add");
            addButton.setOnAction(_ -> addTimeTableRow(newClassComboBox, newModuleComboBox, newTimeField));

            timeTableGrid.addRow(rowIndex, newClassComboBox, newModuleComboBox, newTimeField, addButton);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Fetches a list of options from the database.
     */
    private List<String> fetchOptions(String query) {
        List<String> options = new ArrayList<>();
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                options.add(resultSet.getString(1)); // Assuming the query returns one column
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return options;
    }

    private void addTimeTableRow(ComboBox<String> classComboBox, ComboBox<String> moduleComboBox, TextField timeField) {
        if (classComboBox.getValue() == null || moduleComboBox.getValue() == null || timeField.getText().isEmpty()) {
            showMessage("All fields must be filled!");
            return;
        }

        String selectedClass = classComboBox.getValue();
        String selectedModule = moduleComboBox.getValue();
        String time = timeField.getText();

        if (employeeId == null || employeeId.isEmpty() || selectedEmployeeName.getText().isEmpty()) {
            showMessage("No employee selected. Please select an employee first.");
            return;
        }

        String query = "INSERT INTO time_table (class_Name, class_id, lecturer_Name, lecturer_id, module_Name, module_id, time) " +
                "VALUES (?, " +
                "(SELECT class_id FROM classes WHERE class_name = ?), ?, ?, ?, " +
                "(SELECT ModuleID FROM modules WHERE ModuleName = ?), ?)";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, selectedClass); // class_Name
            preparedStatement.setString(2, selectedClass); // class_id
            preparedStatement.setString(3, selectedEmployeeName.getText()); // lecturer_Name
            preparedStatement.setString(4, employeeId); // lecturer_id
            preparedStatement.setString(5, selectedModule); // module_Name
            preparedStatement.setString(6, selectedModule); // module_id
            preparedStatement.setString(7, time); // time

            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                showMessage("New timetable entry added successfully.");
                loadTimeTable(); // Refresh grid
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void updateTimeTableRow(int id, TextField classField, TextField moduleField, TextField timeField) {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirm Update");
        confirmationAlert.setContentText("Are you sure you want to update this entry?");
        Optional<ButtonType> result = confirmationAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String query = "UPDATE time_table SET class_id = (SELECT class_id FROM classes WHERE class_name = ?), " +
                    "module_id = (SELECT ModuleID FROM modules WHERE ModuleName = ?), time = ? WHERE id = ?";

            try (Connection connection = DatabaseManager.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, classField.getText());
                preparedStatement.setString(2, moduleField.getText());
                preparedStatement.setString(3, timeField.getText());
                preparedStatement.setInt(4, id);

                int rowsUpdated = preparedStatement.executeUpdate();
                if (rowsUpdated > 0) {
                    showMessage("Entry updated successfully!");
                    loadTimeTable();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void deleteTimeTableRow(int id) {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirm Deletion");
        confirmationAlert.setContentText("Are you sure you want to delete this entry?");
        Optional<ButtonType> result = confirmationAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String query = "DELETE FROM time_table WHERE id = ?";
            try (Connection connection = DatabaseManager.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, id);

                int rowsDeleted = preparedStatement.executeUpdate();
                if (rowsDeleted > 0) {
                    showMessage("Entry deleted successfully!");
                    loadTimeTable();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void loadReportsData() {
        TableView<Report> reportsTable = createReportsTable();
        TableView<WeeklyReport> weeklyReportsTable = createWeeklyReportsTable();

        VBox vbox = new VBox(10); // Vertical box layout for tables
        vbox.getChildren().addAll(reportsTable, weeklyReportsTable);

        ScrollPane scrollPane = new ScrollPane(vbox);
        anchorPane.getChildren().clear();
        anchorPane.getChildren().add(scrollPane);
    }

    private TableView<Report> createReportsTable() {
        TableView<Report> tableView = new TableView<>();
        tableView.setPrefSize(450, 200);

        TableColumn<Report, Long> reportIdColumn = new TableColumn<>("Report ID");
        reportIdColumn.setCellValueFactory(new PropertyValueFactory<>("reportId"));

        TableColumn<Report, Integer> moduleIdColumn = new TableColumn<>("Module ID");
        moduleIdColumn.setCellValueFactory(new PropertyValueFactory<>("moduleId"));

        TableColumn<Report, String> reportTextColumn = new TableColumn<>("Report Text");
        reportTextColumn.setCellValueFactory(new PropertyValueFactory<>("reportText"));

        TableColumn<Report, String> submittedAtColumn = new TableColumn<>("Submitted At");
        submittedAtColumn.setCellValueFactory(new PropertyValueFactory<>("submittedAt"));

        tableView.getColumns().addAll(
                Arrays.asList(reportIdColumn, moduleIdColumn, reportTextColumn, submittedAtColumn)
        );
        tableView.setItems(fetchReportsData());
        return tableView;
    }

    private TableView<WeeklyReport> createWeeklyReportsTable() {
        TableView<WeeklyReport> tableView = new TableView<>();
        tableView.setPrefSize(450, 200);

        TableColumn<WeeklyReport, Long> weeklyReportIdColumn = new TableColumn<>("Weekly Report ID");
        weeklyReportIdColumn.setCellValueFactory(new PropertyValueFactory<>("weeklyReportId"));

        TableColumn<WeeklyReport, String> classNameColumn = new TableColumn<>("Class Name");
        classNameColumn.setCellValueFactory(new PropertyValueFactory<>("className"));

        TableColumn<WeeklyReport, String> challengesColumn = new TableColumn<>("Challenges");
        challengesColumn.setCellValueFactory(new PropertyValueFactory<>("challenges"));

        TableColumn<WeeklyReport, String> recommendationsColumn = new TableColumn<>("Recommendations");
        recommendationsColumn.setCellValueFactory(new PropertyValueFactory<>("recommendations"));

        TableColumn<WeeklyReport, String> submittedAtColumn = new TableColumn<>("Submitted At");
        submittedAtColumn.setCellValueFactory(new PropertyValueFactory<>("submittedAt"));

        tableView.getColumns().addAll(
                Arrays.asList(weeklyReportIdColumn, classNameColumn, challengesColumn, recommendationsColumn, submittedAtColumn)
        );

        tableView.setItems(fetchWeeklyReportsData());
        return tableView;
    }

    private ObservableList<Report> fetchReportsData() {
        ObservableList<Report> reportsList = FXCollections.observableArrayList();
        String query = "SELECT ReportID, ModuleID, ReportText, SubmittedAt FROM reports";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                reportsList.add(new Report(
                        resultSet.getLong("ReportID"),
                        resultSet.getInt("ModuleID"),
                        resultSet.getString("ReportText"),
                        resultSet.getString("SubmittedAt")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reportsList;
    }

    private ObservableList<WeeklyReport> fetchWeeklyReportsData() {
        ObservableList<WeeklyReport> weeklyReportsList = FXCollections.observableArrayList();
        String query = "SELECT WeeklyReportID, ClassName, Challenges, Recommendations, SubmittedAt FROM weeklyreports";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                weeklyReportsList.add(new WeeklyReport(
                        resultSet.getLong("WeeklyReportID"),
                        resultSet.getString("ClassName"),
                        resultSet.getString("Challenges"),
                        resultSet.getString("Recommendations"),
                        resultSet.getString("SubmittedAt")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return weeklyReportsList;
    }

    private void showMessage(String message) {
        messageLabel.setText(message);
        messagePane.setVisible(true);
    }

    public void setUserName(String username) {
        UserName=username;
    }
}
