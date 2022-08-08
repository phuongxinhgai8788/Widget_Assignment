package com.example.widgetassignment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MusicService extends Service {

    private final String TAG = "MusicService";

    private MyMediaPlayer myMediaPlayer = MyMediaPlayer.get();
    private StateRepository repository = StateRepository.get();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();
        String channelId =
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel("music_widget_service", "Music playback controls");
//        }
        startForeground(Constants.ONGOING_NOTIFICATION_ID, new NotificationCompat.Builder(this, channelId)
                .setContentTitle("")
                .setContentText("").build());

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String createNotificationChannel(String channelId, String channelName) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW);
        channel.setLightColor(Color.BLUE);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        notificationManager.createNotificationChannel(channel);
        return channelId;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i(TAG, "onStartCommand");

        if(intent==null || intent.getAction() == null) {
            return Service.START_STICKY;
        }
        if(needToRequestPermission()){
            openFunctionalActivity();
        }else {
            queryMusic(intent);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void queryMusic(Intent intent) {
        Log.i(TAG, "queryMusic()");
        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        List<Song> songList = new ArrayList<>();
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(
                ()->{
                    Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
                    if (musicCursor != null && musicCursor.moveToFirst()) {

                        Song song = new Song();

                        //get columns
                        int titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                        int idColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID);
                        int artistColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);

                        //add songs to list
                        do {
                            long id = musicCursor.getLong(idColumn);
                            String title = musicCursor.getString(titleColumn);
                            String artist = musicCursor.getString(artistColumn);
                            int duration = 0;
                            @SuppressLint("Range") String songDuration = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                            if(String.valueOf(songDuration)!=null){
                                try{
                                    duration = Integer.valueOf(songDuration);
                                    song.setDuration(duration);
                                }catch(NumberFormatException e){

                                }
                            }
                            songList.add(new Song(id, title, artist, duration));
                        } while (musicCursor.moveToNext());
                    }
                    Collections.sort(songList, Comparator.comparing(Song::getTitle));
                    Log.i(TAG, "Song list size: "+songList.size());
                    SongRepository.getINSTANCE().setSongList(songList);
                    processStartCommand(intent);
                }
        );
    }

    private void processStartCommand(Intent intent) {
            switch (intent.getAction()) {
                case Constants.PLAY_NEXT_ACTION:
                    handlePlayNext();
                    break;
                case Constants.PLAY_PREV_ACTION:
                    handlePlayPrev();
                    break;
                case Constants.PLAY_PAUSE_RESUME_ACTION:
                    handlePlayPauseResume();
                    break;
                default:
                    break;
            }
        }

    private void openFunctionalActivity() {
        startActivity(new Intent(this, FunctionalActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

    }

    private boolean needToRequestPermission() {
        boolean supportsDynamicPermissions = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
        return supportsDynamicPermissions && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED;
    }

    private void handlePlayPauseResume() {
        if(myMediaPlayer.isPlaying()){
            handlePause();
        }else if(repository.getCurrentPlayedPosition()==0){
            handlePlay();
        }else if(repository.getCurrentPlayedPosition()!=0){
            handleResume();
        }
    }

    private void handleResume() {
        myMediaPlayer.resume();
    }

    private void handlePlay() {
        myMediaPlayer.prepareSong();
    }

    private void handlePause() {
        myMediaPlayer.pause();
    }

    private void handlePlayPrev() {
        myMediaPlayer.playPrev();
    }

    private void handlePlayNext() {
        myMediaPlayer.playNext();
    }

    @Override
    public void onDestroy() {
        Log.d("MusicService", "DESTROY SERVICE");
        super.onDestroy();
    }
}
