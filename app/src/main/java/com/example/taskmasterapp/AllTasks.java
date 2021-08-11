package com.example.taskmasterapp;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class AllTasks extends AppCompatActivity {

    @SuppressLint("RestrictedApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_all_tasks);

        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
    }
}
