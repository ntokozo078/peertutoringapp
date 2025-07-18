package com.niquewrld.tutorspace.models;

public class Tutorial {
    private String title;
    private String date;
    private String time;

    public Tutorial(String title, String date, String time) {
        this.title = title;
        this.date = date;
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }
}