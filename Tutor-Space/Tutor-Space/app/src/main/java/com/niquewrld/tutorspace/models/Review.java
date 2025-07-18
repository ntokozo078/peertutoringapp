package com.niquewrld.tutorspace.models;

public class Review {
    private String reviewerImageUrl;
    private String reviewerName;
    private String formattedDate;
    private float rating;
    private String reviewText;

    public Review() {
        // No-arg constructor required for Firestore
    }

    public Review(String reviewerImageUrl, String reviewerName, String formattedDate,
                  float rating, String reviewText) {
        this.reviewerImageUrl = reviewerImageUrl;
        this.reviewerName = reviewerName;
        this.formattedDate = formattedDate;
        this.rating = rating;
        this.reviewText = reviewText;
    }

    public String getReviewerImageUrl() {
        return reviewerImageUrl;
    }

    public String getReviewerName() {
        return reviewerName;
    }

    public String getFormattedDate() {
        return formattedDate;
    }

    public float getRating() {
        return rating;
    }

    public String getReviewText() {
        return reviewText;
    }
}
