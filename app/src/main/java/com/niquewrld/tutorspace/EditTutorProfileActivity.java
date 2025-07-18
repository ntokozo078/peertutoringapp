package com.niquewrld.tutorspace;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.*;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.*;
import com.niquewrld.tutorspace.helpers.ToolbarHelper;
import com.niquewrld.tutorspace.models.Certificate;
import com.niquewrld.tutorspace.models.Data;
import com.niquewrld.tutorspace.models.Education;

import java.util.*;

public class EditTutorProfileActivity extends AppCompatActivity {
    private static final String TAG = "BecomeATutorActivity";
    private static final int REQUEST_CODE_SELECT_IMAGE = 1;
    private static final int REQUEST_CODE_SELECT_CERTIFICATE = 2;
    private DrawerLayout drawerLayout;
    private FirebaseFirestore firestore;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private String base64ImageString;
    private TextInputEditText etPrice;
    private TextInputEditText etQuality;
    private TextInputEditText etBio;
    private Spinner spinnerSubject, spinnerLocation;
    private SwitchMaterial switchFirstLessonFree;
    private MaterialButton btnSubmit;
    private Button btnAddQuality;
    private Button btnUploadPic;
    private Button btnAddEducation;
    private Button btnAddCertificate;
    private ChipGroup qualitiesChipGroup;
    private LinearLayout educationContainer;
    private LinearLayout certificatesContainer;
    private List<String> qualitiesList = new ArrayList<>();
    private List<Education> educationList = new ArrayList<>();
    private List<Certificate> certificateList = new ArrayList<>();
    private String currentCertificateBase64;


    private boolean isEditMode = true; // Always true for this activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_becomeatutor);

        // Initialize Firebase and UI components (same as BecomeATutorActivity)
        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Login First!", Toast.LENGTH_SHORT).show();
            finish();
        }

        initializeUI();

        // Load existing tutor data
        loadTutorData();

        btnUploadPic = findViewById(R.id.btnUploadPic);
        btnUploadPic.setOnClickListener(v -> openImageSelector());

        // NavBar Code
        drawerLayout = findViewById(R.id.drawer_layout);
        Toolbar toolbar = findViewById(R.id.custom_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        NavigationView navigationView = findViewById(R.id.nav_view);

        // Setup navigation
        ToolbarHelper.setupToolbarWithNavigation(
                this,
                R.id.custom_toolbar,
                drawerLayout,
                navigationView,
                false
        );

        // Set up click listeners (same as BecomeATutorActivity, but call updateTutorData() instead of saveTutorData())
        btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setText("Save Changes");
        btnSubmit.setOnClickListener(v -> validateAndUpdateTutorData());

        btnAddQuality = findViewById(R.id.btnAddQuality);
        btnAddQuality.setOnClickListener(v -> addQuality());

        btnAddEducation = findViewById(R.id.btnAddEducation);
        btnAddEducation.setOnClickListener(v -> showAddEducationDialog());

        etQuality = findViewById(R.id.etQuality);
        etQuality.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                addQuality();
                return true;
            }
            return false;
        });

        // ... rest of your listeners (btnAddQuality, btnAddEducation, etc.)
    }

    private void showAddEducationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this , R.style.BlackButtonDialogTheme);
        builder.setTitle("Add Education");

        // Inflate the dialog layout
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_education, null);
        EditText etDegree = view.findViewById(R.id.etDegree);
        EditText etInstitution = view.findViewById(R.id.etInstitution);
        EditText etStartYear = view.findViewById(R.id.etStartYear);
        Spinner etEndYear = view.findViewById(R.id.spinnerEndYear);

        List<String> endYearOptions = new ArrayList<>();
        endYearOptions.add("Present");

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        for (int i = currentYear; i <= currentYear + 10; i++) {
            endYearOptions.add(String.valueOf(i));
        }

        ArrayAdapter<String> endYearAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                endYearOptions
        );

        endYearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        etEndYear.setAdapter(endYearAdapter);

        builder.setView(view);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String degree = etDegree.getText().toString().trim();
            String institution = etInstitution.getText().toString().trim();
            String startYear = etStartYear.getText().toString().trim();
            String endYear = (String) etEndYear.getSelectedItem();

            if (TextUtils.isEmpty(degree) || TextUtils.isEmpty(institution) ||
                    TextUtils.isEmpty(startYear) || TextUtils.isEmpty(endYear)) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create education object and add to list
            String yearRange = startYear + " - " + endYear;
            Education education = new Education(degree, institution, yearRange);
            educationList.add(education);

            // Add education to UI
            addEducationToUI(education);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());


        builder.create().show();
    }

    private void addQuality() {
        String quality = etQuality.getText().toString().trim();
        if (!TextUtils.isEmpty(quality)) {
            // Add to list if it doesn't already exist
            if (!qualitiesList.contains(quality)) {
                qualitiesList.add(quality);
                addQualityChip(quality);
                etQuality.setText("");
            } else {
                Toast.makeText(this, "Quality already added", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Please enter a quality", Toast.LENGTH_SHORT).show();
        }
    }

    private void openImageSelector() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
    }

    private void initializeUI() {
        // Find all UI components
        spinnerLocation = findViewById(R.id.spinnerLocation);
        etPrice = findViewById(R.id.etPrice);
        etQuality = findViewById(R.id.etQuality);
        etBio = findViewById(R.id.etBio);
        spinnerSubject = findViewById(R.id.spinnerSubject);
        switchFirstLessonFree = findViewById(R.id.switchFirstLessonFree);
        btnSubmit = findViewById(R.id.btnSubmit);
        qualitiesChipGroup = findViewById(R.id.qualitiesChipGroup);
        btnAddQuality = findViewById(R.id.btnAddQuality);
        btnAddEducation = findViewById(R.id.btnAddEducation);
        btnAddCertificate = findViewById(R.id.btnAddCertificate);
        educationContainer = findViewById(R.id.educationContainer);
        certificatesContainer = findViewById(R.id.certificatesContainer);

        // Set up subject spinner with sample data
        String[] locations = {"Durban", "Pietermaritzburg"};

        ArrayAdapter<String> subjectAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, Data.MODULES);
        subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSubject.setAdapter(subjectAdapter);

        ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, locations);
        locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLocation.setAdapter(locationAdapter);
    }


    private void loadTutorData() {
        firestore.collection("tutors").document(user.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Populate all fields
                        etBio.setText(documentSnapshot.getString("bio"));
                        etPrice.setText(documentSnapshot.getString("price"));
                        switchFirstLessonFree.setChecked(Boolean.TRUE.equals(documentSnapshot.getBoolean("firstLessonFree")));

                        // Set spinner values for subject/location
                        setSpinnerToValue(spinnerSubject, documentSnapshot.getString("subject"));
                        setSpinnerToValue(spinnerLocation, documentSnapshot.getString("location"));

                        // Load profilePic
                        String profilePicData = documentSnapshot.getString("profilePic");
                        if (profilePicData != null && profilePicData.startsWith("data:image")) {
                            String base64 = profilePicData.substring(profilePicData.indexOf(",") + 1);
                            byte[] decoded = Base64.decode(base64, Base64.DEFAULT);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(decoded, 0, decoded.length);
                            ImageView ivProfilePic = findViewById(R.id.ivProfilePic);
                            ivProfilePic.setImageBitmap(bitmap);
                            base64ImageString = base64;
                        }

                        // Load qualities
                        qualitiesList.clear();
                        qualitiesChipGroup.removeAllViews();
                        List<String> loadedQualities = (List<String>) documentSnapshot.get("qualities");
                        if (loadedQualities != null) {
                            for (String q : loadedQualities) {
                                qualitiesList.add(q);
                                addQualityChip(q);
                            }
                        }

                        // Load education
                        educationList.clear();
                        educationContainer.removeAllViews();
                        List<Map<String, Object>> loadedEducation = (List<Map<String, Object>>) documentSnapshot.get("educationList");
                        if (loadedEducation != null) {
                            for (Map<String, Object> edu : loadedEducation) {
                                String degree = (String) edu.get("degree");
                                String institution = (String) edu.get("institution");
                                String yearRange = (String) edu.get("yearRange");
                                Education education = new Education(degree, institution, yearRange);
                                educationList.add(education);
                                addEducationToUI(education);
                            }
                        }

                        // Load certificates
                        certificateList.clear();
                        certificatesContainer.removeAllViews();
                        List<Map<String, Object>> loadedCertificates = (List<Map<String, Object>>) documentSnapshot.get("certificates");
                        if (loadedCertificates != null) {
                            for (Map<String, Object> cert : loadedCertificates) {
                                String title = (String) cert.get("title");
                                String imageUrl = (String) cert.get("imageUrl");
                                Certificate certificate = new Certificate(imageUrl, title);
                                certificateList.add(certificate);
                                addCertificateToUI(certificate);
                            }
                        }

                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load tutor data.", Toast.LENGTH_SHORT).show());
    }

    private void addEducationToUI(Education education) {
        View educationView = LayoutInflater.from(this).inflate(R.layout.item_education, null);

        TextView tvDegree = educationView.findViewById(R.id.tvDegree);
        TextView tvInstitution = educationView.findViewById(R.id.tvInstitution);
        TextView tvYearRange = educationView.findViewById(R.id.tvYearRange);
        ImageView btnRemove = educationView.findViewById(R.id.btnRemove);

        tvDegree.setText(education.getDegree());
        tvInstitution.setText(education.getInstitution());
        tvYearRange.setText(education.getYearRange());

        btnRemove.setOnClickListener(v -> {
            educationContainer.removeView(educationView);
            educationList.remove(education);
        });

        educationContainer.addView(educationView);
    }

    private void addCertificateToUI(Certificate certificate) {
        View certificateView = LayoutInflater.from(this).inflate(R.layout.item_certificate, null);

        ImageView ivCertificate = certificateView.findViewById(R.id.ivCertificate);
        TextView tvCertificateName = certificateView.findViewById(R.id.tvCertificateName);
        ImageView btnRemove = certificateView.findViewById(R.id.btnRemove);

        // Decode base64 image
        byte[] decodedString = Base64.decode(certificate.getImageUrl(), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        ivCertificate.setImageBitmap(bitmap);

        tvCertificateName.setText(certificate.getTitle());

        btnRemove.setOnClickListener(v -> {
            certificatesContainer.removeView(certificateView);
            certificateList.remove(certificate);
        });

        certificatesContainer.addView(certificateView);
    }

    private void addQualityChip(String quality) {
        Chip chip = new Chip(this);
        chip.setText(quality);
        chip.setCloseIconVisible(true);
        chip.setCheckable(false);
        chip.setClickable(false);
        chip.setChipBackgroundColorResource(R.color.colorSecondaryVariant);

        // Handle close icon click (remove quality)
        chip.setOnCloseIconClickListener(v -> {
            qualitiesChipGroup.removeView(chip);
            qualitiesList.remove(quality);
        });

        qualitiesChipGroup.addView(chip);
    }

    private void setSpinnerToValue(Spinner spinner, String value) {
        if (value == null) return;
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).equals(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    private void validateAndUpdateTutorData() {
        // Same validation as your create page
        String location = spinnerLocation.getSelectedItem().toString();
        String priceStr = etPrice.getText() != null ? etPrice.getText().toString().trim() : "";
        String subject = spinnerSubject.getSelectedItem().toString();
        boolean firstLessonFree = switchFirstLessonFree.isChecked();
        String bio = etBio.getText() != null ? etBio.getText().toString().trim() : "";

        if (TextUtils.isEmpty(priceStr)) {
            etPrice.setError("Hourly rate is required");
            etPrice.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(bio)) {
            etBio.setError("Bio is required");
            etBio.requestFocus();
            return;
        }

        btnSubmit.setEnabled(false);
        btnSubmit.setText("Saving...");

        // Update tutor data
        updateTutorData(location, priceStr, subject, firstLessonFree, bio);
    }

    private void updateTutorData(String location, String price,
                                 String subject, boolean firstLessonFree, String bio) {
        Map<String, Object> tutorData = new HashMap<>();
        tutorData.put("location", location);
        tutorData.put("price", price);
        tutorData.put("subject", subject);
        tutorData.put("firstLessonFree", firstLessonFree);
        tutorData.put("bio", bio);
        tutorData.put("qualities", qualitiesList);

        List<Map<String, Object>> educationMaps = new ArrayList<>();
        for (Education education : educationList) {
            Map<String, Object> eduMap = new HashMap<>();
            eduMap.put("degree", education.getDegree());
            eduMap.put("institution", education.getInstitution());
            eduMap.put("yearRange", education.getYearRange());
            educationMaps.add(eduMap);
        }
        tutorData.put("educationList", educationMaps);

        List<Map<String, Object>> certificateMaps = new ArrayList<>();
        for (Certificate certificate : certificateList) {
            Map<String, Object> certMap = new HashMap<>();
            certMap.put("title", certificate.getTitle());
            certMap.put("imageUrl", certificate.getImageUrl());
            certificateMaps.add(certMap);
        }
        tutorData.put("certificates", certificateMaps);

        if (base64ImageString != null) {
            tutorData.put("profilePic", "data:image/png;base64," + base64ImageString);
        }

        firestore.collection("tutors")
                .document(user.getUid())
                .update(tutorData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditTutorProfileActivity.this,
                            "Profile updated successfully!",
                            Toast.LENGTH_SHORT).show();
                    btnSubmit.setEnabled(true);
                    btnSubmit.setText("Save Changes");
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error updating tutor data", e);
                    Toast.makeText(EditTutorProfileActivity.this,
                            "Failed to update information: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                    btnSubmit.setEnabled(true);
                    btnSubmit.setText("Save Changes");
                });
    }
}