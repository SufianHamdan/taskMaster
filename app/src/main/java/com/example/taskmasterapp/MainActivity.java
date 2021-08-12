package com.example.taskmasterapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String BTNTITLE = "Title";
    public static final String TASK_TITLE = "task_title";
    public static final String TASK_BODY = "task_body";
    public static final String TASK_STATUS = "task_status";
    public static final String TASKS = "Tasks";
    public static final String TASK_ID = "task_id";

    private List<Task> tasks;

    TaskAdapter taskAdapter;
    TaskDao taskDao;
    AppDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button addTaskBtn = findViewById(R.id.addTaskButton);
        addTaskBtn.setOnClickListener(getViewAddTask);

        Button allTasksBtn = findViewById(R.id.allTasksButton);
        allTasksBtn.setOnClickListener(getViewAllTasks);


        // Task Title page
//        Button viewTaskDetail = findViewById(R.id.taskTitleBtn1);
//        viewTaskDetail.setOnClickListener(getTaskDetail);
//
//        Button viewTaskDetail2 = findViewById(R.id.taskTitleBtn2);
//        viewTaskDetail2.setOnClickListener(getTaskDetail2);
//
//        Button viewTaskDetail3 = findViewById(R.id.taskTitleBtn3);
//        viewTaskDetail3.setOnClickListener(getTaskDetail3);

        // Setting page
        Button settingBtn = findViewById(R.id.settingBtn);
        settingBtn.setOnClickListener(getSettingPage);

        RecyclerView recyclerView = findViewById(R.id.tasks_recycleView);

//        tasks = new ArrayList<>();
//        tasks.add( new Task("Display Username", "this is the first task", "in progress"));
//        tasks.add(new Task("Pass values to task detail", "this is the second task", "new"));
//        tasks.add(new Task("implement task adapter", "this is the third task", "completed"));
//        tasks.add(new Task("create task class", "this is the fourth task", "assigned"));
        database = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, TASKS)
                .allowMainThreadQueries().build();
        taskDao = database.taskDao();

        tasks = taskDao.findAll();

        taskAdapter = new TaskAdapter(tasks, new TaskAdapter.OnTaskClickListener() {
            @Override
            public void onTaskClicked(int position) {
                Intent goToDetailsIntent = new Intent(getApplicationContext(), TaskDetail.class);
                goToDetailsIntent.putExtra(TASK_TITLE, tasks.get(position).getTaskName());
                goToDetailsIntent.putExtra(TASK_BODY, tasks.get(position).getTaskBody());
                goToDetailsIntent.putExtra(TASK_STATUS, tasks.get(position).getStatus());
//                goToDetailsIntent.putExtra(TASK_ID, tasks.get(position).getUid());
                startActivity(goToDetailsIntent);
            }

            @Override
            public void onDeleteTask(int position) {
                taskDao.delete(tasks.get(position));
                tasks.remove(position);
                listItemDeleted();
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                this,
                LinearLayoutManager.VERTICAL,
                false);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(taskAdapter);


    }
    @SuppressLint("NotifyDataSetChanged")
    private void listItemDeleted() {
        taskAdapter.notifyDataSetChanged();
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

//    private View.OnClickListener getTaskDetail = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            String titleOne = ((Button) findViewById(R.id.taskTitleBtn1)).getText().toString();
//            Intent goToTaskDetailPage = new Intent(getBaseContext(), TaskDetail.class);
//            goToTaskDetailPage.putExtra(BTNTITLE, titleOne);
//            startActivity(goToTaskDetailPage);
//        }
//    };
//
//    private View.OnClickListener getTaskDetail2 = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            String titleTow = ((Button) findViewById(R.id.taskTitleBtn2)).getText().toString();
//            Intent goToTaskDetailPage = new Intent(getBaseContext(), TaskDetail.class);
//            goToTaskDetailPage.putExtra(BTNTITLE, titleTow);
//            startActivity(goToTaskDetailPage);
//        }
//    };
//
//    private View.OnClickListener getTaskDetail3 = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            String titleThree = ((Button) findViewById(R.id.taskTitleBtn3)).getText().toString();
//            Intent goToTaskDetailPage = new Intent(getBaseContext(), TaskDetail.class);
//            goToTaskDetailPage.putExtra(BTNTITLE, titleThree);
//            startActivity(goToTaskDetailPage);
//        }
//    };

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