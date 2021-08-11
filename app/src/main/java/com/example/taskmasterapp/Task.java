package com.example.taskmasterapp;

public class Task {

    private String taskName;
    private String taskBody;
    private String status;


    public Task(String taskName, String taskBody, String status) {
        this.taskName = taskName;
        this.taskBody = taskBody;
        this.status = status;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getTaskBody() {
        return taskBody;
    }

    public String getStatus() {
        return status;
    }
}
