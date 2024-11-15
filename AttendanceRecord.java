package com.example.luctfxapp;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

public class AttendanceRecord {
    private final SimpleStringProperty classDate;
    private final SimpleStringProperty className;
    private final SimpleStringProperty chapter;
    private final SimpleStringProperty studentName;
    private final SimpleBooleanProperty present;

    public AttendanceRecord(String classDate, String className, String chapter, String studentName, boolean present) {
        this.classDate = new SimpleStringProperty(classDate);
        this.className = new SimpleStringProperty(className);
        this.chapter = new SimpleStringProperty(chapter);
        this.studentName = new SimpleStringProperty(studentName);
        this.present = new SimpleBooleanProperty(present);
    }

    public String getClassDate() { return classDate.get(); }
    public String getClassName() { return className.get(); }
    public String getChapter() { return chapter.get(); }
    public String getStudentName() { return studentName.get(); }
    public boolean isPresent() { return present.get(); }
}

