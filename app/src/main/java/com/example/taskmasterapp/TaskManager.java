package com.example.taskmasterapp;

import com.amplifyframework.datastore.generated.model.Task;

import java.util.ArrayList;
import java.util.List;

public class TaskManager {

    private static TaskManager instance = null;
    private List<Task> taskLists = new ArrayList<>();


    private TaskManager() {
    }

    public static TaskManager getInstance() {
        if (instance == null) {
            instance = new TaskManager();
        }

        return instance;

    }

    public void setData(List<Task> data) {
        taskLists = data;
    }

    public List<Task> getData() {
        return taskLists;
    }


}
