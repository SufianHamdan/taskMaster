package com.example.taskmasterapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.amplifyframework.core.Amplify;
import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class TaskDetail extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "TaskDetails";
    private FusedLocationProviderClient mFusedLocationClient;

    int PERMISSION_ID = 44;

    private double latitude;
    private double longitude;

    GoogleMap googleMap;


    private String fileURL;
//    public static final String TAG = "TaskDetails";
//    AppDatabase database;
//    TaskDao taskDao;

    @SuppressLint("RestrictedApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // method to get the location
        getLastLocation();

        // Obtain the FirebaseAnalytics instance.
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "MainActivity");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "MainActivity");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Page");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);



//        TextView fileLinkDetail = findViewById(R.id.fileLinkDetail);
//        fileLinkDetail.setOnClickListener(view -> {
//            Intent i = new Intent(Intent.ACTION_VIEW);
//            i.setData(Uri.parse(fileURL));
//            startActivity(i);
//        });

        Intent intent = getIntent();

//        if (intent.getStringExtra("fileName") != null) {
//
//            Amplify.Storage.getUrl(
//                    intent.getStringExtra("fileName"),
//                    result -> {
//                        Log.i("MyAmplifyApp", "Successfully generated: " + result.getUrl());
//                        runOnUiThread(() -> {
//                            if (intent.getStringExtra("fileName").endsWith("png")
//                                    || intent.getStringExtra("fileName").endsWith("jpg")
//                                    || intent.getStringExtra("fileName").endsWith("jpeg")
//                                    || intent.getStringExtra("fileName").endsWith("gif")) {
//                                ImageView taskImageDetail = findViewById(R.id.taskImageDetail);
//
//                                Picasso.get().load(String.valueOf(result.getUrl())).into(taskImageDetail);
//
//                                taskImageDetail.setVisibility(View.VISIBLE);
//                            }else{
//                                fileURL = String.valueOf(result.getUrl());
////                                String link = "<a href=\""+ result.getUrl() + "\">Download the linked file</a>";
//                                fileLinkDetail.setVisibility(View.VISIBLE);
//                            }
//                        });
//                    },
//                    error -> Log.e("MyAmplifyApp", "URL generation failure", error)
//            );
//        }


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

        TextView taskStatusTextView = findViewById(R.id.singleTaskTitle);
        taskStatusTextView.setText(getTaskStatus);

//        TextView taskTeamTextView = findViewById(R.id.singleTaskTeam);
//        taskTeamTextView.setText(getTaskTeam);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
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

                        googleMap.addMarker(new MarkerOptions()
                                .position(new LatLng
                                        (Double.parseDouble(getIntent().getExtras().get(MainActivity.TASK_LATITUDE).toString())
                                                , Double.parseDouble(getIntent().getExtras().get(MainActivity.TASK_LONGITUDE).toString())))
                                .title("Marker"));
                        Log.i(TAG, "onCreate: latitude => " + latitude);
                        Log.i(TAG, "onCreate: longitude => " + longitude);
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
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
        }
    };

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }
}
