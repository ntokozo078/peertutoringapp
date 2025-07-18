package com.niquewrld.tutorspace.models;

import java.util.Date;

import javax.annotation.Nullable;

public class Transaction {
    private String userId;
    private String tutorId;
    private String tutorName;
    private String amount;
    private String status;
    private String type;
    private String ref;
    private String studentName;
    private Boolean isDebited;
    private  Date createdAt;

    public Transaction() {
        // Empty constructor required for Firestore
    }

    public Transaction(String userId, String tutorId, String tutorName, String studentName, String amount, String status, boolean isDebited, Date createdAt , @Nullable String type , String ref) {
        this.userId = userId;
        this.tutorId = tutorId;
        this.studentName = studentName;
        this.tutorName = tutorName;
        this.amount = amount;
        this.status = status;
        this.isDebited = isDebited;
        this.type = type;
        this.createdAt = createdAt;
        this.ref = ref;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getAmount() {
        return amount;
    }

    public Boolean isDeposited() {
        return isDebited;
    }
    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRef() {
        return ref;
    }

    public void setDeposited(Boolean isDebited) {
        this.isDebited = isDebited;
    }

    public String getType() {
        return type;
    }

    public String getStudentName() {
        return studentName;
    }
    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

}
