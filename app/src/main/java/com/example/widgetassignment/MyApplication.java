package com.example.widgetassignment;

import android.Manifest;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("MyApplication", "onCreate: ");
        StateRepository.initialize(this);
        MyMediaPlayer.initialize(this, StateRepository.get(), SongRepository.getINSTANCE());
    }

}
