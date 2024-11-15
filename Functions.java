package com.example.luctfxapp;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.util.ArrayList;
import java.util.List;

public class Functions {

    // List to hold Student instances
    private static List<Student> studentList = new ArrayList<>();

    // Method to add a student to the list
    public static void addStudent(int id, String name) {
        Student newStudent = new Student(id, name, "Unknown");  // Use a default value for gender
        studentList.add(newStudent);
        showConfirmation("Student added: " + name);
    }

    // Method to mark a student as present by ID
    public static void markStudentPresent(int id) {
        for (Student student : studentList) {
            if (student.getId() == id) {  // Compare integer ID directly
                student.setPresent(true);
                showConfirmation("Marked present: " + student.getName());
                return;
            }
        }
        showError("Student not found with ID: " + id);
    }

    // Method to get all students as a list
    public static List<Student> getStudentList() {
        return studentList;
    }

    // Alert helper methods
    public static void showError(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void showConfirmation(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
