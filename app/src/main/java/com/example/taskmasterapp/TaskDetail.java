package com.example.taskmasterapp;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;

import java.util.Objects;

public class TaskDetail extends AppCompatActivity {

    @SuppressLint("RestrictedApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_task_detail);
        Objects.requireNonNull(getSupportActionBar()).setDefaultDisplayHomeAsUpEnabled(true);

        String getTaskTitle = getIntent().getStringExtra(MainActivity.TASK_TITLE);
        String getTaskBody = getIntent().getStringExtra(MainActivity.TASK_BODY);
        String getTaskStatus = getIntent().getStringExtra(MainActivity.TASK_STATUS);

        TextView taskTitleTextView = findViewById(R.id.singleTaskTitle);
        taskTitleTextView.setText(getTaskTitle);

        TextView taskBodyTextView = findViewById(R.id.task_body);
        taskBodyTextView.setText(getTaskBody);

        TextView taskStatusTextView = findViewById(R.id.singleTaskStatus);
        taskStatusTextView.setText(getTaskStatus);

    }
}
