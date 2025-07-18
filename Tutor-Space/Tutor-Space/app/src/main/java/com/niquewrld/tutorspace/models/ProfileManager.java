package com.niquewrld.tutorspace.models;

public class ProfileManager {
    private static TutorProfile tutorProfile;

    public static void setTutorProfile(TutorProfile profile) {
        tutorProfile = profile;
    }

    public static TutorProfile getTutorProfile() {
        return tutorProfile;
    }
}
