package com.niquewrld.tutorspace;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.niquewrld.tutorspace.adapters.TutorAdapter;
import com.niquewrld.tutorspace.models.TutorProfile;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private static final String TAG = "SearchActivity";
    private FirebaseFirestore db;
    private RecyclerView tutorRecyclerView;
    private TutorAdapter tutorAdapter;
    private ImageButton btnClose;
    private EditText searchInput;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private List<TutorProfile> allTutors = new ArrayList<>(); // Keeps all tutors for filtering

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        tutorRecyclerView = findViewById(R.id.tutorRecyclerView);
        searchInput = findViewById(R.id.searchInput);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        // Set up RecyclerView
        tutorRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        tutorAdapter = new TutorAdapter(this , new ArrayList<>());
        tutorRecyclerView.setAdapter(tutorAdapter);
        btnClose = findViewById(R.id.btnClose);
        btnClose.setOnClickListener(v -> finish());

        fetchTutorsFromFirestore();

        // Add TextWatcher to filter tutors as the user types
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterTutors(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed
            }
        });
    }

    private void fetchTutorsFromFirestore() {
        db.collection("tutors")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        allTutors.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {

                            if (document.getId().equals(user.getUid())) {
                                continue;
                            }

                            TutorProfile tutor = document.toObject(TutorProfile.class);

                            getAverageRating(document.getId(), average -> {
                                tutor.setRating(average);

                                allTutors.add(tutor);

                                tutorAdapter.updateTutors(allTutors);
                            });

                        } // Display all tutors initially
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                });
    }

    private void getAverageRating(String id, OnSuccessListener<Float> listener) {
        db.collection("requests")
                .whereEqualTo("tutorId", id)
                .whereGreaterThan("rating", 0)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    float sum = 0f;
                    int count = 0;
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Number ratingNum = doc.getDouble("rating");
                        if (ratingNum != null) {
                            sum += ratingNum.floatValue();
                            count++;
                        }
                    }
                    float average = count > 0 ? sum / count : 0f;
                    listener.onSuccess(average);
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error getting ratings", e);
                    listener.onSuccess(0f); // Return 0 on failure
                });
    }


    private void filterTutors(String query) {
        if (query.isEmpty()) {
            // If the query is empty, show all tutors
            tutorAdapter.updateTutors(allTutors);
        } else {
            // Filter tutors based on the query
            List<TutorProfile> filteredTutors = new ArrayList<>();
            for (TutorProfile tutor : allTutors) {
                if (tutor.getName().toLowerCase().contains(query.toLowerCase()) ||
                        tutor.getSubject().toLowerCase().contains(query.toLowerCase()) ||
                        tutor.getLocation().toLowerCase().contains(query.toLowerCase())) {
                    filteredTutors.add(tutor);
                }
            }
            tutorAdapter.updateTutors(filteredTutors);
        }
    }
}