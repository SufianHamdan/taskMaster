package com.example.taskmasterapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.UserStateDetails;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.pinpoint.PinpointConfiguration;
import com.amazonaws.mobileconnectors.pinpoint.PinpointManager;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Team;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private FirebaseAnalytics mFirebaseAnalytics;

    private static final String TAG = "MainActivity";
    private static PinpointManager pinpointManager;

    public static final String TASK_TITLE = "task_title";
    public static final String TASK_BODY = "task_body";
    public static final String TASK_STATUS = "task_status";
    public static final String TASK_TEAM = "task_team";

    //    private List<Task> tasks;
//    private List<Team> taskBasedTeam = TaskManager.getInstance().getData();


    Handler handler;
    TaskAdapter taskAdapter;
    RecyclerView recyclerView;

    public static PinpointManager getPinpointManager(final Context applicationContext) {
        if (pinpointManager == null) {
            final AWSConfiguration awsConfig = new AWSConfiguration(applicationContext);
            AWSMobileClient.getInstance().initialize(applicationContext, awsConfig, new Callback<UserStateDetails>() {
                @Override
                public void onResult(UserStateDetails userStateDetails) {
                    Log.i("INIT", userStateDetails.getUserState().toString());
                }

                @Override
                public void onError(Exception e) {
                    Log.e("INIT", "Initialization error.", e);
                }
            });

            PinpointConfiguration pinpointConfig = new PinpointConfiguration(
                    applicationContext,
                    AWSMobileClient.getInstance(),
                    awsConfig);

            pinpointManager = new PinpointManager(pinpointConfig);

            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        final String token = task.getResult();
                        Log.d(TAG, "Registering push notifications token: " + token);
                        pinpointManager.getNotificationClient().registerDeviceToken(token);
                    });
        }
        return pinpointManager;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//            Team team1 = Team.builder().name("Team A").build();
//            Team team2 = Team.builder().name("Team B").build();
//            Team team3 = Team.builder().name("Team C").build();
//
//            Amplify.API.mutate(ModelMutation.create(team1), success -> {
//            }, failure -> Log.e("save", "onCreate: ", failure));
//            Amplify.API.mutate(ModelMutation.create(team2), success -> {
//            }, failure -> Log.e("save", "onCreate: ", failure));
//            Amplify.API.mutate(ModelMutation.create(team3), success -> {
//            }, failure -> Log.e("save", "onCreate: ", failure));

        // Initialize PinpointManager
        getPinpointManager(getApplicationContext());

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        handler = new Handler(Looper.getMainLooper(), msg -> {
            Objects.requireNonNull(recyclerView.getAdapter()).notifyDataSetChanged();
            return false;
        });


        // Task Title page
//        Button viewTaskDetail = findViewById(R.id.taskTitleBtn1);
//        viewTaskDetail.setOnClickListener(getTaskDetail);
//
//        Button viewTaskDetail2 = findViewById(R.id.taskTitleBtn2);
//        viewTaskDetail2.setOnClickListener(getTaskDetail2);
//
//        Button viewTaskDetail3 = findViewById(R.id.taskTitleBtn3);
//        viewTaskDetail3.setOnClickListener(getTaskDetail3);

//        Amplify.DataStore.query(com.amplifyframework.datastore.generated.model.Task.class,
//                todos -> {
//                    while (todos.hasNext()) {
//                        todo = todos.next();
//                        Log.i("Tutorial", "==== Todo ====");
//                        Log.i("Tutorial", "Name: " + todo.getTaskName());
////                        tasks.add(todo);
//                    }
//                },
//                failure -> Log.e("Tutorial", "Could not query DataStore", failure)
//        );

//        database = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, TASKS)
//                .allowMainThreadQueries().build();
//        taskDao = database.taskDao();
//
//        tasks = taskDao.findAll();


        // Setting page

        Button settingBtn = findViewById(R.id.settingBtn);
        settingBtn.setOnClickListener(getSettingPage);

        Button addTaskBtn = findViewById(R.id.addTaskButton);
        addTaskBtn.setOnClickListener(getViewAddTask);


    }

    //    @SuppressLint("NotifyDataSetChanged")
    //private void listItemDeleted() {
        //taskAdapter.notifyDataSetChanged();
    //}

    // goToAddTaskPage function
    private final View.OnClickListener getViewAddTask = v -> {
        Intent goToAddTaskPage = new Intent(getBaseContext(), AddTask.class);
        startActivity(goToAddTaskPage);
    };


    private final View.OnClickListener getSettingPage = v -> {
        Intent goToSettingPage = new Intent(getBaseContext(), Setting.class);
        startActivity(goToSettingPage);
    };

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String username = preference.getString("username", "user") + "'s Tasks";
        TextView userLabel = findViewById(R.id.textViewUserName);
        userLabel.setText(username);

    ArrayList<Team> tasksBasedTeam = new ArrayList<>();

    //Amplify
        Amplify.API.query(ModelQuery.list(Team.class,
            Team.NAME.eq(preference.getString("team_name", "Team A"))),
    response -> {
        for (Team obj : response.getData()) {
            tasksBasedTeam.add(obj);
            Log.i("task in list", "has been added to the list -> " + obj.getTask());
        }//for


        recyclerView = findViewById(R.id.tasks_recycleView);
        taskAdapter = new TaskAdapter(tasksBasedTeam.get(0).getTask(), position -> {
            Intent goToDetailsIntent = new Intent(getApplicationContext(), TaskDetail.class);
            goToDetailsIntent.putExtra(TASK_TITLE, tasksBasedTeam.get(0).getTask().get(position).getTitle());
            goToDetailsIntent.putExtra(TASK_BODY, tasksBasedTeam.get(0).getTask().get(position).getBody());
            goToDetailsIntent.putExtra(TASK_STATUS, tasksBasedTeam.get(0).getTask().get(position).getState());
            goToDetailsIntent.putExtra(TASK_TEAM, tasksBasedTeam.get(0).getTask().get(position).getTeam().getName());

//                goToDetailsIntent.putExtra(TASK_ID, tasks.get(position).getUid());
            startActivity(goToDetailsIntent);
        }//task adapter
        );

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                this,
                LinearLayoutManager.VERTICAL,
                false);

        runOnUiThread(() -> {
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setAdapter(taskAdapter);
        });
        handler.sendEmptyMessage(1);
    }
                ,error -> Log.e("error", "failed to add task -> " + error.toString())
                );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_menu, menu);
        return true;
    }

    public void signOutHandler(MenuItem item) {
        Amplify.Auth.signOut(
                () -> {
                    Log.i("AuthQuickstart", "Signed out successfully");
                    Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                    startActivity(intent);
                },
                error -> Log.e("AuthQuickstart", error.toString())
        );
    }

}