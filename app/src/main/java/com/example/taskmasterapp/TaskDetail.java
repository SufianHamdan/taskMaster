package com.example.taskmasterapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.amplifyframework.core.Amplify;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class TaskDetail extends AppCompatActivity {

    private String fileURL;
//    public static final String TAG = "TaskDetails";
//    AppDatabase database;
//    TaskDao taskDao;

    @SuppressLint("RestrictedApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Obtain the FirebaseAnalytics instance.
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "MainActivity");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "MainActivity");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Page");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);



        TextView fileLinkDetail = findViewById(R.id.fileLinkDetail);
        fileLinkDetail.setOnClickListener(view -> {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(fileURL));
            startActivity(i);
        });

        Intent intent = getIntent();

        if (intent.getStringExtra("fileName") != null) {

            Amplify.Storage.getUrl(
                    intent.getStringExtra("fileName"),
                    result -> {
                        Log.i("MyAmplifyApp", "Successfully generated: " + result.getUrl());
                        runOnUiThread(() -> {
                            if (intent.getStringExtra("fileName").endsWith("png")
                                    || intent.getStringExtra("fileName").endsWith("jpg")
                                    || intent.getStringExtra("fileName").endsWith("jpeg")
                                    || intent.getStringExtra("fileName").endsWith("gif")) {
                                ImageView taskImageDetail = findViewById(R.id.taskImageDetail);

                                Picasso.get().load(String.valueOf(result.getUrl())).into(taskImageDetail);

                                taskImageDetail.setVisibility(View.VISIBLE);
                            }else{
                                fileURL = String.valueOf(result.getUrl());
//                                String link = "<a href=\""+ result.getUrl() + "\">Download the linked file</a>";
                                fileLinkDetail.setVisibility(View.VISIBLE);
                            }
                        });
                    },
                    error -> Log.e("MyAmplifyApp", "URL generation failure", error)
            );
        }


        setContentView(R.layout.activity_task_detail);
        Objects.requireNonNull(getSupportActionBar()).setDefaultDisplayHomeAsUpEnabled(true);

        String getTaskTitle = getIntent().getStringExtra(MainActivity.TASK_TITLE);
        String getTaskBody = getIntent().getStringExtra(MainActivity.TASK_BODY);
        String getTaskStatus = getIntent().getStringExtra(MainActivity.TASK_STATUS);
        String getTaskTeam = getIntent().getStringExtra(MainActivity.TASK_TEAM);


        TextView taskTitleTextView = findViewById(R.id.singleTaskTitle);
        taskTitleTextView.setText(getTaskTitle);

        TextView taskBodyTextView = findViewById(R.id.task_body);
        taskBodyTextView.setText(getTaskBody);

        TextView taskStatusTextView = findViewById(R.id.singleTaskStatus);
        taskStatusTextView.setText(getTaskStatus);

        TextView taskTeamTextView = findViewById(R.id.singleTaskTeam);
        taskTeamTextView.setText(getTaskTeam);

    }
}
