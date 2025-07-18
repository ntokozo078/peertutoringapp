package com.niquewrld.tutorspace.helpers;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FavoritesHelper {
    private FirebaseFirestore firestore;
    private FirebaseUser user;
    private Context context;

    public FavoritesHelper(Context context) {
        this.firestore = FirebaseFirestore.getInstance();
        this.user = FirebaseAuth.getInstance().getCurrentUser();
        this.context = context;
    }

    public void addToFavorites(String id) {
        if (user == null) {
            Toast.makeText(context, "Please log in to add favorites.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = user.getUid();
        DocumentReference userDocRef = firestore.collection("users").document(userId);

        userDocRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<String> favorites = (List<String>) documentSnapshot.get("favorites");
                if (favorites == null) {
                    favorites = new ArrayList<>();
                }

                if (!favorites.contains(id)) {
                    favorites.add(id);
                    userDocRef.update("favorites", favorites)
                            .addOnSuccessListener(aVoid -> Toast.makeText(context, "Added to favorites successfully.", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(context, "Failed to add to favorites.", Toast.LENGTH_SHORT).show());
                } else {
                    Toast.makeText(context, "This item is already in your favorites.", Toast.LENGTH_SHORT).show();
                }
            } else {
                // If the user's document doesn't exist, create it with the favorites field
                Map<String, Object> data = new HashMap<>();
                data.put("favorites", Arrays.asList(id));

                userDocRef.set(data, SetOptions.merge())
                        .addOnSuccessListener(aVoid -> Toast.makeText(context, "Added to favorites successfully.", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(context, "Failed to add to favorites.", Toast.LENGTH_SHORT).show());
            }
        }).addOnFailureListener(e -> Toast.makeText(context, "Error retrieving user data.", Toast.LENGTH_SHORT).show());
    }

    public void removeFromFavorites(String id) {
        if (user == null) {
            Toast.makeText(context, "Please log in to remove favorites.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = user.getUid();
        DocumentReference userDocRef = firestore.collection("users").document(userId);

        userDocRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<String> favorites = (List<String>) documentSnapshot.get("favorites");
                if (favorites != null && favorites.contains(id)) {
                    favorites.remove(id);
                    userDocRef.update("favorites", favorites)
                            .addOnSuccessListener(aVoid -> Toast.makeText(context, "Removed from favorites successfully.", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(context, "Failed to remove from favorites.", Toast.LENGTH_SHORT).show());
                } else {
                    Toast.makeText(context, "This item is not in your favorites.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "User data does not exist.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(context, "Error retrieving user data.", Toast.LENGTH_SHORT).show());
    }
}