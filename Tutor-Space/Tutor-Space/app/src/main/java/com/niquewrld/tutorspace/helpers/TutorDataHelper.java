package com.niquewrld.tutorspace.helpers;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.niquewrld.tutorspace.MainActivity;
import com.niquewrld.tutorspace.R;
import com.niquewrld.tutorspace.TutorActivity;
import com.niquewrld.tutorspace.models.ProfileManager;
import com.niquewrld.tutorspace.models.TutorProfile;

import java.util.ArrayList;
import java.util.List;

public class TutorDataHelper {
    private final Context context;
    private final FirebaseFirestore firestore;
    private FavoritesHelper favoritesHelper;
    private final FirebaseUser user;
    private final LinearLayout tutorCardsContainer;
    private final List<TutorProfile> tutorProfiles;

    public TutorDataHelper(Context context, @Nullable FavoritesHelper favoritesHelper, FirebaseFirestore firestore, FirebaseUser user, @Nullable LinearLayout tutorCardsContainer) {
        this.context = context;
        this.firestore = firestore;
        this.user = user;
        this.favoritesHelper = favoritesHelper;
        this.tutorCardsContainer = tutorCardsContainer;
        this.tutorProfiles = new ArrayList<>();
    }

    public void loadTutorData() {
        firestore.collection("tutors")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        QuerySnapshot snapshot = task.getResult();
                        List<Task<TutorProfile>> tutorTasks = new ArrayList<>();

                        for (QueryDocumentSnapshot document : snapshot) {
                            if (user != null && document.getId().equals(user.getUid())) {
                                continue;
                            }

                            TaskCompletionSource<TutorProfile> profileTaskSource = new TaskCompletionSource<>();
                            tutorTasks.add(profileTaskSource.getTask());

                            // Create the base profile
                            TutorProfile profile = document.toObject(TutorProfile.class);
                            profile.setID(document.getId());
                            profile.setIsAmbassador(getBooleanFromDocument(document, "isAmbassador"));
                            profile.setFirstLessonFree(getBooleanFromDocument(document, "firstLessonFree"));

                            String tutorId = document.getId();

                            // Create parallel tasks for ratings and favorites
                            Task<Boolean> favoriteTask = getFavoriteTask(tutorId);
                            Task<float[]> ratingTask = getRatingTask(tutorId);

                            // When both tasks complete, update the profile and resolve
                            Tasks.whenAll(favoriteTask, ratingTask)
                                    .addOnSuccessListener(v -> {
                                        try {
                                            // Get results from completed tasks
                                            boolean isFavorite = favoriteTask.getResult();
                                            float[] ratingData = ratingTask.getResult();
                                            float average = ratingData[0];
                                            int count = (int) ratingData[1];

                                            // Update profile with results
                                            profile.setIsFavorite(isFavorite);
                                            profile.setRating(average);
                                            profile.setReviewCount(count);

                                            // Resolve this profile task
                                            profileTaskSource.setResult(profile);
                                        } catch (Exception e) {
                                            profileTaskSource.setException(e);
                                        }
                                    })
                                    .addOnFailureListener(profileTaskSource::setException);
                        }

                        // When all tutor profile tasks complete, update the list
                        Tasks.whenAll(tutorTasks)
                                .addOnSuccessListener(v -> {
                                    tutorProfiles.clear();
                                    for (Task<TutorProfile> profileTask : tutorTasks) {
                                        if (profileTask.isSuccessful()) {
                                            tutorProfiles.add(profileTask.getResult());
                                        }
                                    }

                                    // Now that we have all profiles, load the cards if needed
                                    if (tutorCardsContainer != null) {
                                        loadTutorCards();
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(context, "Error loading tutors: " + e.getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Toast.makeText(context, "Error fetching tutors", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Helper method to get favorite status as a Task
    private Task<Boolean> getFavoriteTask(String tutorId) {
        TaskCompletionSource<Boolean> favoriteSource = new TaskCompletionSource<>();

        if (user != null) {
            String userId = user.getUid();
            firestore.collection("users")
                    .document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            List<String> favorites = (List<String>) documentSnapshot.get("favorites");
                            boolean isFavorite = favorites != null && favorites.contains(tutorId);
                            favoriteSource.setResult(isFavorite);
                        } else {
                            favoriteSource.setResult(false);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("TutorDataHelper", "Error retrieving favorites", e);
                        favoriteSource.setResult(false);
                    });
        } else {
            favoriteSource.setResult(false);
        }

        return favoriteSource.getTask();
    }

    // Helper method to get rating as a Task
    private Task<float[]> getRatingTask(String tutorId) {
        TaskCompletionSource<float[]> ratingSource = new TaskCompletionSource<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("requests")
                .whereEqualTo("tutorId", tutorId)
                .whereGreaterThan("rating", 0)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    float sum = 0f;
                    int count = 0;
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Double rating = doc.getDouble("rating");
                        if (rating != null) {
                            sum += rating.floatValue();
                            count++;
                        }
                    }
                    float average = count > 0 ? sum / count : 0f;
                    ratingSource.setResult(new float[]{average, count});
                })
                .addOnFailureListener(e -> {
                    Log.e("TutorDataHelper", "Error getting ratings", e);
                    ratingSource.setResult(new float[]{0f, 0});
                });

        return ratingSource.getTask();
    }


    public void loadFavTutorData() {
        firestore.collection("tutors")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        QuerySnapshot snapshot = task.getResult();

                        List<TutorProfile> tempProfiles = new ArrayList<>();
                        List<Task<Void>> favoriteTasks = new ArrayList<>();

                        for (QueryDocumentSnapshot document : snapshot) {
                            TutorProfile profile = document.toObject(TutorProfile.class);
                            profile.setID(document.getId());
                            profile.setIsAmbassador(getBooleanFromDocument(document, "isAmbassador"));
                            profile.setFirstLessonFree(getBooleanFromDocument(document, "firstLessonFree"));


                            TaskCompletionSource<Void> completionSource = new TaskCompletionSource<>();
                            getIsFavorite(document.getId(), isFavorite -> {
                                if (isFavorite) { // Only add profiles with isFavorite == true
                                    profile.setIsFavorite(isFavorite);
                                    tempProfiles.add(profile);
                                }
                                completionSource.setResult(null);
                            });
                            favoriteTasks.add(completionSource.getTask());
                        }

                        // Wait for all favorite tasks to complete
                        Tasks.whenAll(favoriteTasks).addOnCompleteListener(favoriteTask -> {
                            if (favoriteTask.isSuccessful()) {
                                tutorProfiles.clear();
                                tutorProfiles.addAll(tempProfiles);
                                loadTutorCards();
                            } else {
                                Toast.makeText(context, "Error fetching favorite statuses", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(context, "Error fetching tutors", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public Task<TutorProfile> getTutorById(String tutorId) {
        TaskCompletionSource<TutorProfile> completionSource = new TaskCompletionSource<>();

        firestore.collection("tutors").document(tutorId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        TutorProfile profile = document.toObject(TutorProfile.class);

                        if (profile != null) {
                            profile.setID(document.getId());
                            profile.setIsAmbassador(getBooleanFromDocument(document, "isAmbassador"));
                            profile.setFirstLessonFree(getBooleanFromDocument(document, "firstLessonFree"));

                            getIsFavorite(document.getId(), isFavorite -> {
                                profile.setIsFavorite(isFavorite);
                                // Set the result of the task
                                completionSource.setResult(profile);
                            });

                        } else {
                            // Handle tutor not found
                            completionSource.setResult(null);
                        }
                    } else {
                        // Handle error
                        completionSource.setException(task.getException());
                    }
                });

        return completionSource.getTask();
    }

    public void loadTutorCards() {
        LayoutInflater inflater = LayoutInflater.from(context);

        // Clear any existing views
        tutorCardsContainer.removeAllViews();

        // Add each tutor card to the container
        for (int i = 0; i < tutorProfiles.size(); i++) {
            TutorProfile profile = tutorProfiles.get(i);

            // Inflate the card layout
            CardView cardView = (CardView) inflater.inflate(R.layout.tutor_card_item, tutorCardsContainer, false);

            // Configure the card with tutor data
            configureTutorCard(cardView, profile);

            // Add the card to the container
            tutorCardsContainer.addView(cardView);
        }
    }

    private void configureTutorCard(CardView cardView, TutorProfile profile) {
        // Find all views within the card
        ImageView profileImage = cardView.findViewById(R.id.profile_image);
        ImageButton favoriteButton = cardView.findViewById(R.id.btn_favorite);
        TextView tutorName = cardView.findViewById(R.id.txt_tutor_name);
        TextView location = cardView.findViewById(R.id.txt_location);
        TextView rating = cardView.findViewById(R.id.txt_rating);
        TextView reviewCount = cardView.findViewById(R.id.txt_review_count);
        CardView ambassadorBadge = cardView.findViewById(R.id.badge_ambassador);
        TextView subject = cardView.findViewById(R.id.txt_subject);
        TextView price = cardView.findViewById(R.id.txt_price);
        TextView freeLesson = cardView.findViewById(R.id.txt_free_lesson);

        // Set tutor data to views
        tutorName.setText(profile.getName());
        location.setText(profile.getLocation());
        rating.setText(String.format("%.1f", profile.getRating())); // Display rating with 1 decimal place
        reviewCount.setText(String.format("(%d reviews)", profile.getReviewCount()));
        subject.setText(profile.getSubject());
        price.setText("R" + profile.getPrice() + "/h");

        // Set profile image
        String profilePicUrl = profile.getProfilePic();
        if (profilePicUrl != null && !profilePicUrl.isEmpty()) {
            Glide.with(context)
                    .load(profilePicUrl)
                    .placeholder(R.drawable.ic_badge)
                    .error(R.drawable.ic_badge)
                    .into(profileImage);
        }

        // Show/hide ambassador badge
        if (profile.isAmbassador()) {
            ambassadorBadge.setVisibility(View.VISIBLE);
        } else {
            ambassadorBadge.setVisibility(View.GONE);
        }

        // Show/hide free lesson badge
        if (profile.hasFirstLessonFree()) {
            freeLesson.setVisibility(View.VISIBLE);
            freeLesson.setText("1st lesson free");
        } else {
            freeLesson.setVisibility(View.GONE);
        }

        // Set click listener for the favorite button
        favoriteButton.setOnClickListener(v -> {
            profile.setFavorite(!profile.isFavorite());
            updateFavoriteButton(favoriteButton, profile.isFavorite());

            if (profile.isFavorite()) {
                favoritesHelper.addToFavorites(profile.getId());
            } else {
                favoritesHelper.removeFromFavorites(profile.getId());
            }
        });

        // Initialize favorite button state
        updateFavoriteButton(favoriteButton, profile.isFavorite());

        // Set click listener for the entire card
        cardView.setOnClickListener(v -> {
            Intent intent = new Intent(context, TutorActivity.class);
            ProfileManager.setTutorProfile(profile);
            context.startActivity(intent);
        });
    }

    private void updateFavoriteButton(ImageButton button, boolean isFavorite) {
        int iconResId = isFavorite ? R.drawable.ic_favorite_filled : R.drawable.ic_favorite_border;
        button.setImageResource(iconResId);
    }

    private boolean getBooleanFromDocument(DocumentSnapshot document, String fieldName) {
        Object value = document.get(fieldName);

        if (value instanceof Boolean) {
            return (Boolean) value;
        } else if (value instanceof String) {
            return Boolean.parseBoolean((String) value);
        } else if (value instanceof Number) {
            return ((Number) value).intValue() != 0;
        }

        return false; // Default if null or unrecognized type
    }

    public void getIsFavorite(String id, MainActivity.FavoriteCallback callback) {
        if (user != null) {
            String userId = user.getUid();

            firestore.collection("users")
                    .document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Retrieve the favorites array from the user's document
                            List<String> favorites = (List<String>) documentSnapshot.get("favorites");

                            boolean isFavorite = favorites != null && favorites.contains(id);

                            callback.onResult(isFavorite);
                        } else {
                            callback.onResult(false);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Error retrieving favorites: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        callback.onResult(false);
                    });
        } else {
            callback.onResult(false);
        }
    }
}