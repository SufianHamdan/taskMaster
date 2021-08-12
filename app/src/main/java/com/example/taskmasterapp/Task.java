package com.example.taskmasterapp;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Task {

    @PrimaryKey(autoGenerate = true)
    private int uid;

    @ColumnInfo(name = "task_name")
    private final String taskName;
    @ColumnInfo(name = "task_body")
    private final String taskBody;
    @ColumnInfo(name = "task_status")
    private final String status;


    public Task(String taskName, String taskBody, String status) {
        this.taskName = taskName;
        this.taskBody = taskBody;
        this.status = status;
    }

    public int getUid() {
        return uid;
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

    public void setUid(int uid) {
        this.uid = uid;
    }
}
