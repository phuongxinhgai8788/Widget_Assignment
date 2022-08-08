package com.example.widgetassignment;

import android.content.ContentUris;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.MediaController;

public class MyMediaPlayer implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, MediaController.MediaPlayerControl, AudioManager.OnAudioFocusChangeListener{

    private static MyMediaPlayer INSTANCE;
    private Context context;
    private MediaPlayer mediaPlayer;
    private StateRepository stateRepository;
    private SongRepository songRepository;
    private Song currentSong;

    private final String TAG = "MyMediaPlayer";

    private MyMediaPlayer(Context context, StateRepository stateRepository, SongRepository songRepository){
        this.context = context;
        initMediaPlayer();
        this.stateRepository = stateRepository;
        this.songRepository = songRepository;
    }

    public static MyMediaPlayer initialize(Context context, StateRepository stateRepository, SongRepository songRepository){
        if(INSTANCE==null){
            INSTANCE = new MyMediaPlayer(context, stateRepository, songRepository);
        }
        return INSTANCE;
    }
    public static MyMediaPlayer get(){
        return INSTANCE;
    }

    private void initMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setAudioAttributes(
                new AudioAttributes
                        .Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build());
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
    }

    public void prepareSong(){
        // Reset first because the user is playing subsequent songs
        mediaPlayer.reset();
        //Get currentSong
         currentSong = songRepository.getSongList().get(StateRepository.get().getPlayedSongIndex());
        // Get id
        long playedSongId = currentSong.getId();
        // set URI
        Uri trackUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, playedSongId);
        try {
            mediaPlayer.setDataSource(context, trackUri);
        } catch (Exception e) {
            Log.e(TAG, "Error setting data source", e);
        }
        mediaPlayer.prepareAsync();
    }

    @Override
    public void onAudioFocusChange(int i) {

    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        mediaPlayer.reset();
        stateRepository.savePlayedSongPosition(0);
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
    }

    @Override
    public void start() {
        mediaPlayer.start();
    }

    @Override
    public void pause() {
        mediaPlayer.pause();
        stateRepository.savePlayedSongPosition(getCurrentPosition());
    }

    @Override
    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    @Override
    public void seekTo(int i) {
        mediaPlayer.seekTo(i);
    }

    @Override
    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        stateRepository.savePlayedSongPosition(0);
    }

    public void resume() {
        mediaPlayer.seekTo(stateRepository.getCurrentPlayedPosition());
        mediaPlayer.start();
    }

    public void playPrev() {
        int currentSongIndex = stateRepository.getPlayedSongIndex();
        int songListSize = songRepository.getSongList().size();

        int updateCurrentSongIndex = currentSongIndex==0?songListSize:currentSongIndex-1;
        stateRepository.savePlayedSongIndex(updateCurrentSongIndex);

        prepareSong();
    }

    public void playNext() {
        int currentSongIndex = stateRepository.getPlayedSongIndex();
        int songListSize = songRepository.getSongList().size();

        int updateCurrentSongIndex = currentSongIndex==songListSize?0:currentSongIndex+1;
        stateRepository.savePlayedSongIndex(updateCurrentSongIndex);

        prepareSong();
    }
}
