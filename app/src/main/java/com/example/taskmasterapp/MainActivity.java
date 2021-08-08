package com.example.taskmasterapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button addTaskBtn = findViewById(R.id.addTaskButton);
        addTaskBtn.setOnClickListener(getViewAddTask);

        Button allTasksBtn = findViewById(R.id.allTasksButton);
        allTasksBtn.setOnClickListener(getViewAllTasks);

    }

    private View.OnClickListener getViewAddTask = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(getBaseContext(), AddTask.class);
            startActivity(i);
        }
    };


    private View.OnClickListener getViewAllTasks = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(getBaseContext(), AllTasks.class);
            startActivity(i);
        }
    };


}