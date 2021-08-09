package com.example.taskmasterapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class AllTasks extends AppCompatActivity {

    @SuppressLint("RestrictedApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_all_tasks);

        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
    }
}
