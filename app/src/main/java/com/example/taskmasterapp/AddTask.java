package com.example.taskmasterapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AddTask extends AppCompatActivity {

    AppDatabase database;
    TaskDao taskDao;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_task);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        Button addTask = findViewById(R.id.newTaskSubmit);
        database = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, MainActivity.TASKS)
                .allowMainThreadQueries().build();
        taskDao = database.taskDao();
        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText taskTitle = findViewById(R.id.newTaskName);
                EditText taskBody = findViewById(R.id.newTaskBody);
//                TextView taskStatus = findViewById(R.id.task_status);

                Task newTask = new Task(
                        taskTitle.getText().toString(),
                        taskBody.getText().toString(),
                        "run"
                );
                Toast.makeText(AddTask.this, "Task Added !", Toast.LENGTH_SHORT).show();
                taskDao.addOne(newTask);
            }
        });
    }

}
