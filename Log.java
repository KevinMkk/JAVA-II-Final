package com.example.luctfxapp;

public class Log {
    private final int id;
    private final String roll;
    private final String username;
    private final String entryLog;
    private final String exitLog;
    private final String logDuration;

    public Log(int id, String roll, String username, String entryLog, String exitLog, String logDuration) {
        this.id = id;
        this.roll = roll;
        this.username = username;
        this.entryLog = entryLog;
        this.exitLog = exitLog;
        this.logDuration = logDuration;
    }

    public int getId() {
        return id;
    }

    public String getRoll() {
        return roll;
    }

    public String getUsername() {
        return username;
    }

    public String getEntryLog() {
        return entryLog;
    }

    public String getExitLog() {
        return exitLog;
    }

    public String getLogDuration() {
        return logDuration;
    }
}
