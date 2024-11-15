package com.example.luctfxapp;

public class WeeklyReport {

    private long weeklyReportId;
    private String className;
    private String challenges;
    private String recommendations;
    private String submittedAt;

    // Constructor
    public WeeklyReport(long weeklyReportId, String className, String challenges, String recommendations, String submittedAt) {
        this.weeklyReportId = weeklyReportId;
        this.className = className;
        this.challenges = challenges;
        this.recommendations = recommendations;
        this.submittedAt = submittedAt;
    }

    // Getters and Setters
    public long getWeeklyReportId() {
        return weeklyReportId;
    }

    public void setWeeklyReportId(long weeklyReportId) {
        this.weeklyReportId = weeklyReportId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getChallenges() {
        return challenges;
    }

    public void setChallenges(String challenges) {
        this.challenges = challenges;
    }

    public String getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(String recommendations) {
        this.recommendations = recommendations;
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
        return "WeeklyReport{" +
                "weeklyReportId=" + weeklyReportId +
                ", className='" + className + '\'' +
                ", challenges='" + challenges + '\'' +
                ", recommendations='" + recommendations + '\'' +
                ", submittedAt='" + submittedAt + '\'' +
                '}';
    }
}
