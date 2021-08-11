package com.example.taskmasterapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Setting extends AppCompatActivity {

    @SuppressLint("RestrictedApi")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_setting);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        Button saveUserName = findViewById(R.id.usernameSaveButton);
        saveUserName.setOnClickListener(updateUserName);
    }

    private View.OnClickListener updateUserName =  new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            SharedPreferences.Editor editor = preferences.edit();
            EditText usernameField = findViewById(R.id.usernameInput);
            String username = usernameField.getText().toString();
            editor.putString("username", username);
            editor.apply();

            Intent goToHomePage = new Intent(getBaseContext(), MainActivity.class);
            startActivity(goToHomePage);
        }
    };
}
