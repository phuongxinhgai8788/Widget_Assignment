package com.example.widgetassignment;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.RequiresApi;

/**
 * Implementation of App Widget functionality.
 */
public class MusicWidget extends AppWidgetProvider {

    private String TAG = "MusicWidget";


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        Log.i(TAG, "Widget's onUpdate()");
        associateIntents(context);
    }

    private void associateIntents(Context context) {
        try {
            RemoteViews remoteViews = getRemoteViews(context);
            ComponentName thisWidget = new ComponentName(context, MusicWidget.class);
            AppWidgetManager.getInstance(context).updateAppWidget(thisWidget, remoteViews);

        }catch (Exception ignored){

        }
    }

    private RemoteViews getRemoteViews(Context context) {

        Log.i(TAG, "getRemoteViews()");

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.music_widget);

        // For Play/Pause button
        remoteViews.setOnClickPendingIntent(R.id.pause, getPendingIntent(context, Constants.PLAY_PAUSE_RESUME_ACTION));

        // For Previous button
        remoteViews.setOnClickPendingIntent(R.id.play_prev_btn, getPendingIntent(context, Constants.PLAY_PREV_ACTION));

        // For Next button
        remoteViews.setOnClickPendingIntent(R.id.play_next_btn, getPendingIntent(context, Constants.PLAY_NEXT_ACTION));

        return remoteViews;
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.i(TAG, "onReceive");
        if(intent != null){
            String action = intent.getAction();
            Log.i(TAG, "Intent action: "+action);
            if(Constants.PLAY_PREV_ACTION.equals(action)
            || Constants.PLAY_NEXT_ACTION.equals(action)
            || Constants.PLAY_PAUSE_RESUME_ACTION.equals(action)){

                Intent serviceIntent = new Intent(context, MusicService.class);
                serviceIntent.setAction(action);
//                if(Build.VERSION_CODES.O > Build.VERSION.SDK_INT ){
                    Log.i(TAG, "Service is started");
                    context.startForegroundService(serviceIntent);
//                }else{
//                    context.startService(serviceIntent);
//                }
            } else{
                super.onReceive(context, intent);
            }
        }
    }

    private static PendingIntent getPendingIntent(Context context, String action){
        Intent intent = new Intent(context, MusicWidget.class);
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }
}