package com.niquewrld.tutorspace.models;

public class Certificate {
    private String imageUrl;
    private String title;

    // Empty constructor (required for Firestore)
    public Certificate() {}

    public Certificate(String imageUrl, String title) {
        this.imageUrl = imageUrl;
        this.title = title;
    }

    // Getters and setters
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

