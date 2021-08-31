package com.example.taskmasterapp;

import android.app.Activity;
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

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.Team;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static PinpointManager pinpointManager;

    public static final String TASK_TITLE = "task_title";
    public static final String TASK_BODY = "task_body";
    public static final String TASK_STATUS = "task_status";
    public static final String TASK_TEAM = "task_team";
    private ArrayList<Task> tasks = new ArrayList<>();
    private String selectedTeam = null;
    private Team teamData = null;


    Handler handler;
    TaskAdapter taskAdapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize PinpointManager
        getPinpointManager(getApplicationContext());

        tasks.clear();
        getTaskDataFromAPI();

        handler = new Handler(Looper.getMainLooper(), msg -> {
            Objects.requireNonNull(recyclerView.getAdapter()).notifyDataSetChanged();
            return false;
        });

        // Obtain the FirebaseAnalytics instance.
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "MainActivity");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "MainActivity");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Page");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);


        // Setting page

        Button settingBtn = findViewById(R.id.settingBtn);
        settingBtn.setOnClickListener(getSettingPage);

        Button addTaskBtn = findViewById(R.id.addTaskButton);
        addTaskBtn.setOnClickListener(getViewAddTask);

        // intent filter
        ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // There are no request codes
                            Intent data = result.getData();

                            Log.i(TAG, "onActivityResult: Merry Christmas" + result.getData());
                        }
                    }
                });



        recyclerView = findViewById(R.id.tasks_recycleView);
        taskAdapter = new TaskAdapter(tasks, new TaskAdapter.OnTaskClickListener() {
            @Override
            public void onTaskClicked(int position) {
                Intent goToDetailsIntent = new Intent(getApplicationContext(), TaskDetail.class);
                goToDetailsIntent.putExtra(TASK_TITLE, tasks.get(position).getTitle());
                goToDetailsIntent.putExtra(TASK_BODY, tasks.get(position).getDescription());
                goToDetailsIntent.putExtra(TASK_STATUS, tasks.get(position).getStatus());
                goToDetailsIntent.putExtra(TASK_TEAM, tasks.get(position).getTeam().getName());

                startActivity(goToDetailsIntent);
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                this,
                LinearLayoutManager.VERTICAL,
                false);

        runOnUiThread(() -> {
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setAdapter(taskAdapter);
        });


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

        selectedTeam = preference.getString("team_name",null);
        if (selectedTeam != null){
            Log.i(TAG, "onResume: selected team is " + selectedTeam);
            getTeamFromApiByName();

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            tasks.clear();
            Log.i(TAG, "-----selected team true-------- ");
            Log.i(TAG, selectedTeam);
            getTaskDataFromAPIByTeam();
        }
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


    public void getTeamFromApiByName() {
        Amplify.API.query(
                ModelQuery.list(Team.class, Team.NAME.contains(selectedTeam)),
                response -> {
                    for (Team teamDetail : response.getData()) {
                        Log.i(TAG, "selected team is  =>"+teamDetail);
                        teamData=teamDetail;
                    }
                },
                error -> Log.e(TAG, "Query failure", error)
        );
    }

    public  void  getTaskDataFromAPIByTeam(){
        Log.i(TAG, "getTaskDataFromAPIByTeam: get task by team");

        Amplify.API.query(ModelQuery.list(Task.class, Task.TEAM.contains(teamData.getId())),
                response -> {
                    for (Task task : response.getData()) {

                        Log.i(TAG, "task-team-id: " + task.getTeam().getId());
                        Log.i(TAG, "team-id: "+ teamData.getId());
                        tasks.add(task);

                        Log.i(TAG, "getFrom api by team: the Task from api are => " + task);
                    }
                    handler.sendEmptyMessage(1);
                },
                error -> Log.e(TAG, "getFrom api: Failed to get Task from api => " + error.toString())
        );
    }

    public  void getTaskDataFromAPI()  {
        Log.i(TAG, "getTaskDataFromAPI: get all data");
        Amplify.API.query(ModelQuery.list(Task.class),
                response -> {
                    for (Task task : response.getData()) {
                        tasks.add(task);
                        Log.i(TAG, "getFrom api: the Task from api are => " + task);
//                            Log.i(TAG, "getFrom api: the Task from api are => " + task.getTeam());
                    }
                    handler.sendEmptyMessage(1);
                },
                error -> Log.e(TAG, "getFrom api: Failed to get Task from api => " + error.toString())
        );
    }

}