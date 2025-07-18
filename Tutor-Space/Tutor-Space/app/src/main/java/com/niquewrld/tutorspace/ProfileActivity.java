package com.niquewrld.tutorspace;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.*;
import com.niquewrld.tutorspace.auth.RegisterActivity;
import com.niquewrld.tutorspace.helpers.ToolbarHelper;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvInterests;
    private EditText etDisplayName;
    private DrawerLayout drawerLayout;

    // Tutor-specific fields
    private CardView tutorSection;

    private Button btnChangeInterests, btnDeleteTutor, btnSaveName , btnEditTutorProfile;
    private ProgressBar progressBar;

    private FirebaseFirestore db;
    private String uid;
    private List<String> interests;
    private boolean isTutor = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        etDisplayName = findViewById(R.id.et_display_name);
        tvInterests = findViewById(R.id.tv_interests);
        btnChangeInterests = findViewById(R.id.btn_change_interests);
        btnSaveName = findViewById(R.id.btn_save_displayname);
        btnDeleteTutor = findViewById(R.id.btn_delete_tutor);
        progressBar = findViewById(R.id.profile_simple_progress);

        // NavBar Code
        drawerLayout = findViewById(R.id.drawer_layout);
        Toolbar toolbar = findViewById(R.id.custom_toolbar);
        setSupportActionBar(toolbar);
        //Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        NavigationView navigationView = findViewById(R.id.nav_view);

        // Setup navigation
        ToolbarHelper.setupToolbarWithNavigation(
                this,
                R.id.custom_toolbar,
                drawerLayout,
                navigationView,
                false
        );

        // Tutor Section
        tutorSection = findViewById(R.id.tutor_section);
        btnEditTutorProfile = findViewById(R.id.btn_edit_tutor_profile);

        db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            finish();
            return;
        }
        uid = user.getUid();
        etDisplayName.setText(user.getDisplayName() != null ? user.getDisplayName() : "No display name");

        loadProfile();

        btnChangeInterests.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, SetupProfileActivity.class);
            intent.putStringArrayListExtra("interests", interests != null ? new java.util.ArrayList<>(interests) : new java.util.ArrayList<>());
            startActivity(intent);
        });

        btnEditTutorProfile.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, EditTutorProfileActivity.class);
            startActivity(intent);
        });

        btnDeleteTutor.setOnClickListener(v -> deleteTutorProfile());
        btnSaveName.setOnClickListener(v -> updateDisplayName());
    }

    public void updateDisplayName(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(String.valueOf(etDisplayName.getText()))
                    .build();

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> profileTask) {
                            if (profileTask.isSuccessful()) {
                                Toast.makeText(ProfileActivity.this, "Name updated Successful", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ProfileActivity.this, "Name update failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void loadProfile() {
        progressBar.setVisibility(View.VISIBLE);
        db.collection("users").document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    interests = (List<String>) documentSnapshot.get("interests");
                    isTutor = documentSnapshot.contains("isTutor") && Boolean.TRUE.equals(documentSnapshot.getBoolean("isTutor"));

                    if (interests != null && !interests.isEmpty()) {
                        tvInterests.setText(TextUtils.join(", ", interests));
                    } else {
                        tvInterests.setText("No interests set.");
                    }
                    tutorSection.setVisibility(isTutor ? View.VISIBLE : View.GONE);

                    progressBar.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(ProfileActivity.this, "Failed to load profile.", Toast.LENGTH_SHORT).show();
                });
    }


    private void deleteTutorProfile() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Confirm Deletion")
                .setMessage("Are you sure you want to delete your tutor profile? This action cannot be undone.")
                .setPositiveButton("Yes", (dialogInterface, which) -> {
                    progressBar.setVisibility(View.VISIBLE);
                    // Delete tutor doc
                    db.collection("tutor").document(uid)
                            .delete()
                            .addOnSuccessListener(aVoid -> {
                                // Remove isTutor from user doc
                                db.collection("users").document(uid)
                                        .update("isTutor", FieldValue.delete())
                                        .addOnSuccessListener(aVoid2 -> {
                                            progressBar.setVisibility(View.GONE);
                                            Toast.makeText(this, "Tutor profile deleted.", Toast.LENGTH_SHORT).show();
                                            btnDeleteTutor.setVisibility(View.GONE);
                                            tutorSection.setVisibility(View.GONE);
                                        })
                                        .addOnFailureListener(e -> {
                                            progressBar.setVisibility(View.GONE);
                                            Toast.makeText(this, "Tutor profile deleted, but failed to update user.", Toast.LENGTH_SHORT).show();
                                        });
                            })
                            .addOnFailureListener(e -> {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(this, "Failed to delete tutor profile.", Toast.LENGTH_SHORT).show();
                            });
                })
                .setNegativeButton("No", null)
                .create();

        dialog.setOnShowListener(d -> {
            // Set button colors here
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
        });

        dialog.show();
    }



}