package com.example.widgetassignment;

import java.io.Serializable;

public class Song implements Serializable {

    private long id;
    private String title;
    private String author;
    private int duration;

    public Song(){

    }

    public Song(long id, String title, String author, int duration){
        this.id = id;
        this.title = title;
        this.author = author;
        this.duration = duration;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

}
