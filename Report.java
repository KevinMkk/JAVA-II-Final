package com.example.luctfxapp;

public class Report {

    private long reportId;
    private int moduleId;
    private String reportText;
    private String submittedAt;

    // Constructor
    public Report(long reportId, int moduleId, String reportText, String submittedAt) {
        this.reportId = reportId;
        this.moduleId = moduleId;
        this.reportText = reportText;
        this.submittedAt = submittedAt;
    }

    // Getters and Setters
    public long getReportId() {
        return reportId;
    }

    public void setReportId(long reportId) {
        this.reportId = reportId;
    }

    public int getModuleId() {
        return moduleId;
    }

    public void setModuleId(int moduleId) {
        this.moduleId = moduleId;
    }

    public String getReportText() {
        return reportText;
    }

    public void setReportText(String reportText) {
        this.reportText = reportText;
    }

    public String getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(String submittedAt) {
        this.submittedAt = submittedAt;
    }

    // To String method for easy display
    @Override
    public String toString() {
        return "Report{" +
                "reportId=" + reportId +
                ", moduleId=" + moduleId +
                ", reportText='" + reportText + '\'' +
                ", submittedAt='" + submittedAt + '\'' +
                '}';
    }
}
