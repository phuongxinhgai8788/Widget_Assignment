package com.example.widgetassignment;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class SongRepository {

    private static SongRepository INSTANCE;
    private static List<Song> songList = new ArrayList<>();

    private SongRepository(){

    }

    public static SongRepository getINSTANCE(){
        if(INSTANCE == null){
            synchronized (SongRepository.class){
                if(INSTANCE == null){
                    INSTANCE = new SongRepository();
                }
            }
        }
        return INSTANCE;
    }

    public void setSongList(List<Song> songList){
        this.songList = songList;
        Log.i("SongRepository", "Size of list in set: "+this.songList.size());

    }

    public List<Song> getSongList(){
        Log.i("SongRepository", "Size of list in get: "+this.songList.size());
        return this.songList;
    }
}
