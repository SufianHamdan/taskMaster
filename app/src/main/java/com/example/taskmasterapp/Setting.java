package com.example.taskmasterapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Objects;

public class Setting extends AppCompatActivity {

//    String theTeam;

    @SuppressLint("RestrictedApi")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_setting);
        Objects.requireNonNull(getSupportActionBar()).setDefaultDisplayHomeAsUpEnabled(true);

        Button saveUserName = findViewById(R.id.usernameSaveButton);
        saveUserName.setOnClickListener(updateUserName);

        // Obtain the FirebaseAnalytics instance.
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "MainActivity");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "MainActivity");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Page");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

    }

    private final View.OnClickListener updateUserName = v -> {

        @SuppressLint("CutPasteId") String theTeam = ((Spinner) findViewById(R.id.spinnerSetting)).getSelectedItem().toString();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor editor = preferences.edit();
        EditText usernameField = findViewById(R.id.usernameInput);
        String username = usernameField.getText().toString();
        editor.putString("username", username);
        editor.putString("team_name", theTeam);
        editor.apply();

        Intent goToHomePage = new Intent(getBaseContext(), MainActivity.class);
        startActivity(goToHomePage);
    };
}
