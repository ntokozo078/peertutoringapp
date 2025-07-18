package com.niquewrld.tutorspace.models;

import java.util.Date;
import java.util.List;

public class Request {
    private String id;
    private String userId;
    private String userName;
    private String address;
    private String phone;
    private String message;
    private String tutorId;
    private String tutorName;
    private String tutorSubject;
    private boolean isPayed;
    private boolean isAccepted;
    private boolean isDeclined;
    private boolean isCompleted;
    private int rating;
    private String tutorDate;
    List<String> dateTimeOptions;
    private long timestamp;

    public Request() {
        // No-arg constructor required for Firestore
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTutorId() {
        return tutorId;
    }

    public void setTutorId(String tutorId) {
        this.tutorId = tutorId;
    }

    public String getTutorName() {
        return tutorName;
    }

    public void setTutorName(String tutorName) {
        this.tutorName = tutorName;
    }

    public String getTutorSubject() {
        return tutorSubject;
    }

    public void setTutorSubject(String tutorSubject) {
        this.tutorSubject = tutorSubject;
    }

    public boolean isPayed() {
        return isPayed;
    }

    public void setPayed(boolean isPayed) {
        this.isPayed = isPayed;
    }

    public boolean isAccepted() {
        return isAccepted;
    }

    public void setAccepted(boolean isAccepted) {
        this.isAccepted = isAccepted;
    }

    public boolean isDeclined() {
        return isDeclined;
    }

    public void setDeclined(boolean isDeclined) {
        this.isDeclined = isDeclined;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getTutorDate() {
        return tutorDate;
    }

    public List<String> getDateTimeOptions() {
        return  dateTimeOptions;
    }


}