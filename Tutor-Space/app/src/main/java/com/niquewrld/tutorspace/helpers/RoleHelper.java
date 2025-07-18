package com.niquewrld.tutorspace.helpers;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class RoleHelper {

    private final FirebaseFirestore firestore;

    public RoleHelper() {
        this.firestore = FirebaseFirestore.getInstance();
    }

    public void isUserTutor(String userId, TutorCheckCallback callback) {
        firestore.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && documentSnapshot.contains("isTutor")) {
                        Boolean isTutor = documentSnapshot.getBoolean("isTutor");
                        callback.onResult(isTutor != null && isTutor);
                    } else {
                        callback.onResult(false);
                    }
                })
                .addOnFailureListener(e -> {
                    callback.onError(e);
                });
    }


    public interface TutorCheckCallback {
        void onResult(boolean isTutor);

        void onError(Exception e);
    }
}
