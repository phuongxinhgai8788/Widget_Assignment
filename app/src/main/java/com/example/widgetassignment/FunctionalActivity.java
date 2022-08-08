package com.example.widgetassignment;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

public class FunctionalActivity extends AppCompatActivity {

    private final String TAG = "FunctionalActivity";

    private final int MAX_ATTEMPTS = 20;
    private int counter = 0;
    
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(null);
        setResult(Activity.RESULT_CANCELED);
        checkPermission();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void checkPermission() {
        Log.i(TAG, "Checking permissions");
        if (needsToRequestPermissions()) {
            // Show an explanation to the user
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Log.i(TAG, "showing dialog");
                new MaterialAlertDialogBuilder(this)
                        .setTitle(R.string.app_name)
                        .setMessage(R.string.request_permission_message)
                        .setPositiveButton(R.string.request_permission_button, (dialogInterface, i) -> requestPermission())
                        .show();
            } else {
                // No explanation needed; request the permission
                requestPermission();
            }
        } else {
            // Permission has already been granted
            Log.i(TAG, "permission already granted");
            finishConfigOK();
        }
    }

    private void finishConfigOK() {
        int appWidgetId = getIntent().getExtras().getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
        );
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        setResult(Activity.RESULT_OK, resultValue);

        finish();
    }

    private void requestPermission() {
        Log.i(TAG, "Requesting permission");
        counter++;
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                Constants.MY_PERMISSION_REQUEST_READ_MEDIA);
    }

    private boolean needsToRequestPermissions() {
        boolean supportsDynamicPermissions = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
        return supportsDynamicPermissions &&
                (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED);
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == Constants.MY_PERMISSION_REQUEST_READ_MEDIA){
            // If request is cancelled, the result arrays are empty
            if((grantResults.length>0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)){
                finishConfigOK();

            } else{
                if(counter<MAX_ATTEMPTS){
                    checkPermission();
                }else{
                    // give up
                    new MaterialAlertDialogBuilder(this)
                            .setTitle(R.string.app_name)
                            .setMessage(R.string.denied_permission_messsage)
                            .setPositiveButton(R.string.request_permission_button, (dialogInterface, i) -> finish())
                                .show();
                }
            }
            return;
        }
    }

    }
