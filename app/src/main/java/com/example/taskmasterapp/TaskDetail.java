package com.example.taskmasterapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class TaskDetail extends AppCompatActivity {

    @SuppressLint("RestrictedApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_task_detail);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        String getTaskTitle = getIntent().getStringExtra(MainActivity.BTNTITLE);
        TextView taskTitleTextView = findViewById(R.id.singleTaskTitle);
        taskTitleTextView.setText(getTaskTitle);

    }
}
