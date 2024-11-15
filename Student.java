package com.example.luctfxapp;

import javafx.beans.property.*;

public class Student {
    private IntegerProperty id;
    private StringProperty name;
    private StringProperty gender;
    private BooleanProperty present;

    // Constructor that initializes properties
    public Student(int id, String name, String gender) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.gender = new SimpleStringProperty(gender);
        this.present = new SimpleBooleanProperty(false);  // Default attendance is false
    }

    // Getter and setter for id property
    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public IntegerProperty idProperty() {
        return id;
    }

    // Getter and setter for name property
    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public StringProperty nameProperty() {
        return name;
    }

    // Getter and setter for gender property
    public String getGender() {
        return gender.get();
    }

    public void setGender(String gender) {
        this.gender.set(gender);
    }

    public StringProperty genderProperty() {
        return gender;
    }

    // Getter and setter for present property
    public boolean isPresent() {
        return present.get();
    }

    public void setPresent(boolean present) {
        this.present.set(present);
    }

    public BooleanProperty presentProperty() {
        return present;
    }
}
