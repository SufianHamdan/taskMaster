package com.example.taskmasterapp;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.util.Log;
import android.widget.TextView;

import java.util.Objects;

public class TaskDetail extends AppCompatActivity {

    public static final String TAG = "TaskDetails";
    AppDatabase database;
    TaskDao taskDao;

    @SuppressLint("RestrictedApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_task_detail);
        Objects.requireNonNull(getSupportActionBar()).setDefaultDisplayHomeAsUpEnabled(true);

        String getTaskTitle = getIntent().getStringExtra(MainActivity.TASK_TITLE);
        String getTaskBody = getIntent().getStringExtra(MainActivity.TASK_BODY);
        String getTaskStatus = getIntent().getStringExtra(MainActivity.TASK_STATUS);

        // trying to get task id from main activity and display info from db

//        database = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, MainActivity.TASKS)
//                .allowMainThreadQueries().build();
//        taskDao = database.taskDao();
//        Log.i(TAG, "onCreate: task id from main activity is:" + (getIntent().getStringExtra(MainActivity.TASK_ID)));
//
//        Task task = taskDao.findTaskById(Integer.parseInt(getIntent().getStringExtra(MainActivity.TASK_ID)));
//
//        Log.i(TAG, "onCreate: task name is:" + task.getTaskName());

        TextView taskTitleTextView = findViewById(R.id.singleTaskTitle);
        taskTitleTextView.setText(getTaskTitle);

        TextView taskBodyTextView = findViewById(R.id.task_body);
        taskBodyTextView.setText(getTaskBody);

        TextView taskStatusTextView = findViewById(R.id.singleTaskStatus);
        taskStatusTextView.setText(getTaskStatus);

    }
}
