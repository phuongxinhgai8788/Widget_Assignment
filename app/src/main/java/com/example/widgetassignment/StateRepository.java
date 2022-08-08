package com.example.widgetassignment;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class StateRepository {

    private static StateRepository INSTANCE;
    private Context context;

    private StateRepository(Context context) {
        this.context = context;
    }

    public static void initialize(Context context){
        INSTANCE = new StateRepository(context);
    }

    public static StateRepository get() {
        if(INSTANCE == null){
            throw new IllegalStateException("Repository must be initialized");
        }
        return INSTANCE;
    }

    public int getCurrentPlayedPosition() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt(Constants.PREF_SAVE_PLAYED_SONG_POSITION, 0);
    }

    public void savePlayedSongPosition(int position) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putInt(Constants.PREF_SAVE_PLAYED_SONG_POSITION, position)
                .apply();
    }

    public int getPlayedSongIndex() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt(Constants.PREF_SAVE_PLAYED_SONG_INDEX, 0);
    }

    public void savePlayedSongIndex(int index) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putInt(Constants.PREF_SAVE_PLAYED_SONG_INDEX, index)
                .apply();
    }
}
