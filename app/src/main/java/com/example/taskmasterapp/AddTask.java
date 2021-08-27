package com.example.taskmasterapp;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.annotation.SuppressLint;
import android.os.FileUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.AWSDataStorePlugin;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.Team;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

public class AddTask extends AppCompatActivity {

    AppDatabase database;
    TaskDao taskDao;
//    String theTeam;
    private String uploadedFileName;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_task);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

//         upload file
        ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        try {
                            onChooseFile(result);
                        } catch (IOException e) {
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

        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText taskTitle = findViewById(R.id.newTaskName);
                EditText taskBody = findViewById(R.id.taskBodyFromAddTask);

                @SuppressLint("CutPasteId") String theTeam = ((Spinner) findViewById(R.id.spinner)).getSelectedItem().toString();

                Team team = Team
                        .builder()
                        .name(theTeam)
                        .build();

                Task newTask = Task
                        .builder()
                        .title(taskTitle.getText().toString())
                        .body(taskBody.getText().toString())
                        .state("run")
                        .team(team).build();


                Amplify.API.mutate(ModelMutation.create(newTask),
                        response -> Log.i("response", "task has been created" + response.getData()),
                        error -> Log.e("error", "something went wrong" + error.toString()));

//                Amplify.API.mutate(ModelMutation.create(team),
//                        response -> Log.i("response", "team has been created" + response.getData()),
//                        error -> Log.e("error", "something went wrong" + error.toString()));
//                Amplify.DataStore.save(newTask,
//                        success -> Log.i("aws", "saved" + success.item().getTaskName()),
//                        error -> Log.e("aws", "failed" + error.toString()));


                Toast.makeText(AddTask.this, "Task Has Been Added", Toast.LENGTH_SHORT).show();
//                taskDao.addOne(newTask);

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void onChooseFile(ActivityResult activityResult) throws IOException {

        Uri uri = null;
        if (activityResult.getData() != null) {
            uri = activityResult.getData().getData();
        }
        assert uri != null;
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
                success -> Log.i("onChooseFile", "uploadFileToS3: succeeded " + success.getKey()),
                error -> Log.e("onChooseFile", "uploadFileToS3: failed " + error.toString())
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

}
