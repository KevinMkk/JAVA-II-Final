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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class Logs {
    public Button backButton;

    @FXML
    private TableView<Log> logsTable;

    @FXML
    private TableColumn<Log, Integer> idColumn;

    @FXML
    private TableColumn<Log, String> rollColumn;

    @FXML
    private TableColumn<Log, String> usernameColumn;

    @FXML
    private TableColumn<Log, String> entryLogColumn;

    @FXML
    private TableColumn<Log, String> exitLogColumn;

    @FXML
    private TableColumn<Log, String> logDurationColumn;

    private final ObservableList<Log> logData = FXCollections.observableArrayList();


    @FXML
    public void initialize() {
        setupTableColumns();
        loadLogsFromDatabase();

        backButton.setOnAction(this::handleBackAction);
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        rollColumn.setCellValueFactory(new PropertyValueFactory<>("roll"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        entryLogColumn.setCellValueFactory(new PropertyValueFactory<>("entryLog"));
        exitLogColumn.setCellValueFactory(new PropertyValueFactory<>("exitLog"));
        logDurationColumn.setCellValueFactory(new PropertyValueFactory<>("logDuration"));
    }

    private void loadLogsFromDatabase() {
        try {
            Connection conn = DatabaseManager.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM logs");

            while (rs.next()) {
                logData.add(new Log(
                        rs.getInt("id"),
                        rs.getString("roll"),
                        rs.getString("username"),
                        rs.getString("entry_log"),
                        rs.getString("exit_log"),
                        rs.getString("log_duration")
                ));
            }

            logsTable.setItems(logData);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleBackAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Admin.fxml"));
            Parent root = loader.load();

            // Close the current window
            Stage currentStage = (Stage) backButton.getScene().getWindow();
            currentStage.close();

            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            newStage.setTitle("Login Screen");
            newStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
