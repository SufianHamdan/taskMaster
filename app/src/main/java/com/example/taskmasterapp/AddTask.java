package com.example.taskmasterapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.annotation.SuppressLint;

public class AddTask extends AppCompatActivity {

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_task);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
    }

}
