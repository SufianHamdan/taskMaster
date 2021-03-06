package com.example.taskmasterapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.FileUtils;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.Team;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class AddTask extends AppCompatActivity {

    private FusedLocationProviderClient mFusedLocationClient;
    int PERMISSION_ID = 44;

    private double latitude;
    private double longitude;

    private static final String TAG = "AddTask";
    //    AppDatabase database;
//    TaskDao taskDao;
    private String uploadedFileName;
    private Team teamData = null;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // location
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();

        setContentView(R.layout.activity_add_task);
        Objects.requireNonNull(getSupportActionBar()).setDefaultDisplayHomeAsUpEnabled(true);


        // Obtain the FirebaseAnalytics instance.
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "MainActivity");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "MainActivity");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Page");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

//         upload file
        ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        try {
                            onChooseFile(result);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

        findViewById(R.id.btnUploadFile).setOnClickListener(view -> {
            Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
            chooseFile.setType("*/*");
            chooseFile = Intent.createChooser(chooseFile, "Choose a file");
            someActivityResultLauncher.launch(chooseFile);
        });



        Button addTask = findViewById(R.id.newTaskSubmit);

        addTask.setOnClickListener(v -> {
            EditText taskTitle = findViewById(R.id.newTaskName);
            EditText taskBody = findViewById(R.id.taskBodyFromAddTask);

            @SuppressLint("CutPasteId") String theTeam = ((Spinner) findViewById(R.id.spinner)).getSelectedItem().toString();

            getTeamDetailFromAPIByName(theTeam);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Log.i(TAG, "onCreate: teamData -> " + teamData);
            Task newTask = Task
                    .builder()
                    .title(taskTitle.getText().toString())
                    .description(taskBody.getText().toString())
                    .status("run")
                    .latitude(latitude)
                    .longitude(longitude)
                    .team(teamData).build();

            addTaskTpAmplify(newTask);
            Toast.makeText(AddTask.this, "Task Has Been Added", Toast.LENGTH_SHORT).show();
        });


        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        Log.i("getIntent", "onCreate: " + type);
        Log.i("getIntent", "onCreate: " + (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM));
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                try {
                    onChooseFile((Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM)); // Handle single image being sent
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void onChooseFile(ActivityResult activityResult) {

        Uri uri = null;
        if (activityResult.getData() != null) {
            uri = activityResult.getData().getData();
        }
        assert uri != null;
        //    String theTeam;
        String uploadedFileName = new Date().toString() + "." + getMimeType(getApplicationContext(), uri);

        File uploadFile = new File(getApplicationContext().getFilesDir(), "uploadFile");

        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            FileUtils.copy(inputStream, new FileOutputStream(uploadFile));
        } catch (Exception exception) {
            Log.e("onChooseFile", "onActivityResult: file upload failed" + exception.toString());
        }

        Amplify.Storage.uploadFile(
                uploadedFileName,
                uploadFile,
                success -> Log.i("onChooseFile", "uploadFileToS3: succeeded " + success.getKey()),
                error -> Log.e("onChooseFile", "uploadFileToS3: failed " + error.toString())
        );
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void onChooseFile(Uri uri) throws IOException {

        uploadedFileName = new Date().toString() + "." + getMimeType(getApplicationContext(),uri);

        File uploadFile = new File(getApplicationContext().getFilesDir(), "uploadFile");

        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            FileUtils.copy(inputStream, new FileOutputStream(uploadFile));
        } catch (Exception exception) {
            Log.e("onChooseFile", "onActivityResult: file upload failed" + exception.toString());
        }

        Amplify.Storage.uploadFile(
                uploadedFileName,
                uploadFile,
                success -> {
                    Log.i("onChooseFile", "uploadFileToS3: succeeded " + success.getKey());
                    Toast.makeText(getApplicationContext(), "Image Successfully Uploaded", Toast.LENGTH_SHORT).show();

                },
                error -> {
                    Log.e("onChooseFile", "uploadFileToS3: failed " + error.toString());
                    Toast.makeText(getApplicationContext(), "Image Upload failed", Toast.LENGTH_SHORT).show();

                }
        );
    }

    public static String getMimeType(Context context, Uri uri) {
        String extension;

        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(context.getContentResolver().getType(uri));
        } else {
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());

        }

        return extension;
    }

    public void getTeamDetailFromAPIByName(String name) {
        Amplify.API.query(
                ModelQuery.list(Team.class, Team.NAME.contains(name)),
                response -> {
                    for (Team teamDetail : response.getData()) {
                        Log.i(TAG, "the team name is =>" + teamDetail.getName());
                        teamData = teamDetail;
                    }
                },
                error -> Log.e(TAG, "Query failure", error)
        );
    }

    public void addTaskTpAmplify(Task newTask){
        Log.i(TAG, "addTaskTpAmplify: in add mutation method");
        Amplify.API.mutate(ModelMutation.create(newTask),
                response -> Log.i(TAG, "task has been created -> " + response.getData()),
                error -> Log.e(TAG, "something went wrong -> " + error.toString()));
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        // check if permissions are given
        if (checkPermissions()) {

            // check if location is enabled
            if (isLocationEnabled()) {

                // getting last
                // location from
                // FusedLocationClient
                // object
                mFusedLocationClient.getLastLocation().addOnCompleteListener(task -> {
                    Location location = task.getResult();
                    if (location == null) {
                        requestNewLocationData();
                    } else {

                        latitude = location.getLatitude();
                        longitude = location.getLongitude();

                        Log.i(TAG, "onCreate: latitude => "+ latitude);
                        Log.i(TAG, "onCreate: longitude => "+ longitude);
                    }
                });
            } else {
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            // if permissions aren't available,
            // request for permissions
            requestPermissions();
        }
    }

    private boolean checkPermissions() {
        return ActivityCompat
                .checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
                &&
                ActivityCompat
                        .checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED;

        // If we want background location
        // on Android 10.0 and higher,
        // use:
        // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }
    private final LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
        }
    };
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

}
