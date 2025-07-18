package com.niquewrld.tutorspace.models;
import java.io.Serializable;
import java.util.List;

public class TutorProfile implements Serializable {
    private String profilePic;
    private String id;
    private String name;
    private String location;
    private float rating;
    private int reviewCount;
    private boolean isAmbassador;
    private String subject;
    private String price;
    private String bio;
    List<String> qualities;
    List<Education> educationList;
    List<String> review;
    List<Certificate> certificates;
    private boolean firstLessonFree;
    private boolean isFavorite;



    public TutorProfile() {}

    public TutorProfile(String uid , String profilePic , String name, String location, float rating, int reviewCount,
                        boolean isAmbassador, String subject, String price,
                        boolean firstLessonFree  , List<String> qualities , List<Certificate> certificates , String bio , List<Education> educationList , List<String> review) {
        this.id = uid;
        this.name = name;
        this.profilePic = profilePic;
        this.location = location;
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.isAmbassador = isAmbassador;
        this.subject = subject;
        this.price = price;
        this.bio = bio;
        this.review = review;
        this.firstLessonFree = firstLessonFree;
        this.qualities = qualities;
        this.certificates = certificates;
        this.isFavorite = false;
        this.educationList = educationList;
    }

    //GET
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public String getLocation() {
        return location;
    }
    public float getRating() {
        return rating;
    }
    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }
    public boolean isAmbassador() {
        return isAmbassador;
    }
    public void setIsAmbassador(Boolean isAmbassador) {
        this.isAmbassador = isAmbassador;
    }
    public void setFirstLessonFree(Boolean firstLessonFree) {
        this.firstLessonFree = firstLessonFree;
    }
    public void setIsFavorite(Boolean isFavorite) {
        this.isFavorite = isFavorite;
    }
    public void setID(String id) {
        this.id = id;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }
    public String getSubject() {
        return subject;
    }
    public String getPrice() {
        return price;
    }
    public boolean hasFirstLessonFree() {
        return firstLessonFree;
    }
    public boolean isFavorite() {
        return isFavorite;
    }
    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
    public List<String> getQualities() {
        return qualities;
    }
    public void setQualities(List<String> qualities) {
        this.qualities = qualities;
    }
    public String getBio() {
        return bio;
    }
    public List<Education> getEducationList() {
        return educationList;
    }
    public List<String> getReviews() {
        return review;
    }

    public void setEducationList(List<Education> educationList) {
        this.educationList = educationList;
    }

    public List<Certificate> getCertificates() {
        return certificates;
    }
}
