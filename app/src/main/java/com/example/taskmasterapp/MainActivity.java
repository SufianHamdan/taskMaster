package com.example.taskmasterapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    public static final String BTNTITLE = "Title";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button addTaskBtn = findViewById(R.id.addTaskButton);
        addTaskBtn.setOnClickListener(getViewAddTask);

        Button allTasksBtn = findViewById(R.id.allTasksButton);
        allTasksBtn.setOnClickListener(getViewAllTasks);


        // Task Title page
        Button viewTaskDetail = findViewById(R.id.taskTitleBtn1);
        viewTaskDetail.setOnClickListener(getTaskDetail);

        Button viewTaskDetail2 = findViewById(R.id.taskTitleBtn2);
        viewTaskDetail2.setOnClickListener(getTaskDetail2);

        Button viewTaskDetail3 = findViewById(R.id.taskTitleBtn3);
        viewTaskDetail3.setOnClickListener(getTaskDetail3);

        // Setting page
        Button settingBtn = findViewById(R.id.settingBtn);
        settingBtn.setOnClickListener(getSettingPage);
    }

    private View.OnClickListener getViewAddTask = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent goToAddTaskPage = new Intent(getBaseContext(), AddTask.class);
            startActivity(goToAddTaskPage);
        }
    };


    private View.OnClickListener getViewAllTasks = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent goToAllTasksPage = new Intent(getBaseContext(), AllTasks.class);
            startActivity(goToAllTasksPage);
        }
    };

    private View.OnClickListener getTaskDetail = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String titleOne = ((Button) findViewById(R.id.taskTitleBtn1)).getText().toString();
            Intent goToTaskDetailPage = new Intent(getBaseContext(), TaskDetail.class);
            goToTaskDetailPage.putExtra(BTNTITLE, titleOne);
            startActivity(goToTaskDetailPage);
        }
    };

    private View.OnClickListener getTaskDetail2 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String titleTow = ((Button) findViewById(R.id.taskTitleBtn2)).getText().toString();
            Intent goToTaskDetailPage = new Intent(getBaseContext(), TaskDetail.class);
            goToTaskDetailPage.putExtra(BTNTITLE, titleTow);
            startActivity(goToTaskDetailPage);
        }
    };

    private View.OnClickListener getTaskDetail3 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String titleThree = ((Button) findViewById(R.id.taskTitleBtn3)).getText().toString();
            Intent goToTaskDetailPage = new Intent(getBaseContext(), TaskDetail.class);
            goToTaskDetailPage.putExtra(BTNTITLE, titleThree);
            startActivity(goToTaskDetailPage);
        }
    };

    private View.OnClickListener getSettingPage = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent goToSettingPage = new Intent(getBaseContext(), Setting.class);
            startActivity(goToSettingPage);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String username = preference.getString("username", "user") + "'s Tasks";
        TextView userLabel = findViewById(R.id.textViewUserName);
        userLabel.setText(username);
    }


}