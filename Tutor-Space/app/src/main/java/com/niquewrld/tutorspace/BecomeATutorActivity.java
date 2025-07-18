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
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.niquewrld.tutorspace.auth.LoginActivity;
import com.niquewrld.tutorspace.helpers.RoleHelper;
import com.niquewrld.tutorspace.helpers.ToolbarHelper;
import com.niquewrld.tutorspace.models.Certificate;
import com.niquewrld.tutorspace.models.Data;
import com.niquewrld.tutorspace.models.Education;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class BecomeATutorActivity extends AppCompatActivity {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_becomeatutor);

        // Initialize Firebase components
        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        if (user == null) {
            Toast.makeText(this, "Login First!", Toast.LENGTH_SHORT).show();
            Intent becomeATutor = new Intent(this, LoginActivity.class);
            startActivity(becomeATutor);
            finish();
        }

        RoleHelper ropeHelper = new RoleHelper();
        ropeHelper.isUserTutor(user.getUid(), new RoleHelper.TutorCheckCallback() {
            @Override
            public void onResult(boolean isTutor) {
                if (isTutor) {
                    Toast.makeText(BecomeATutorActivity.this, "You Already a Tutor!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onError(Exception e) {
                // Handle the error here
                Log.e("TutorCheck", "Error checking user role", e);
            }
        });

        // Initialize UI components
        initializeUI();

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

        // Set up submit button
        btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(v -> validateAndSaveTutorData());

        // Set up add quality button
        btnAddQuality = findViewById(R.id.btnAddQuality);
        btnAddQuality.setOnClickListener(v -> addQuality());

        // Set up add education button
        btnAddEducation = findViewById(R.id.btnAddEducation);
        btnAddEducation.setOnClickListener(v -> showAddEducationDialog());

        // Set up add certificate button
        btnAddCertificate = findViewById(R.id.btnAddCertificate);
        btnAddCertificate.setOnClickListener(v -> showAddCertificateDialog());

        // Setup quality input to handle Enter key
        etQuality = findViewById(R.id.etQuality);
        etQuality.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                addQuality();
                return true;
            }
            return false;
        });
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

    private void showAddCertificateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this , R.style.BlackButtonDialogTheme);
        builder.setTitle("Add Certificate");

        // Inflate the dialog layout
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_certificate, null);
        EditText etCertificateName = view.findViewById(R.id.etCertificateName);
        MaterialButton btnSelectCertificate = view.findViewById(R.id.btnSelectCertificate);
        ImageView ivCertificatePreview = view.findViewById(R.id.ivCertificatePreview);

        // Initialize certificate data
        currentCertificateBase64 = null;

        btnSelectCertificate.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_CODE_SELECT_CERTIFICATE);
        });

        builder.setView(view);

        // Create the dialog first to be able to update the image preview
        AlertDialog dialog = builder.create();

        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Add", (dialogInterface, which) -> {
            String certificateName = etCertificateName.getText().toString().trim();

            if (TextUtils.isEmpty(certificateName)) {
                Toast.makeText(this, "Certificate name is required", Toast.LENGTH_SHORT).show();
                return;
            }

            if (currentCertificateBase64 == null) {
                Toast.makeText(this, "Please select a certificate image", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create certificate object and add to list
            Certificate certificate = new Certificate(currentCertificateBase64, certificateName);
            certificateList.add(certificate);

            // Add certificate to UI
            addCertificateToUI(certificate);
        });

        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", (dialogInterface, which) -> dialog.dismiss());

        dialog.show();
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

    private String encodeImageToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
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

    private void validateAndSaveTutorData() {
        // Get values from form
        String location = spinnerLocation.getSelectedItem().toString();
        String priceStr = etPrice.getText() != null ? etPrice.getText().toString().trim() : "";
        String subject = spinnerSubject.getSelectedItem().toString();
        boolean firstLessonFree = switchFirstLessonFree.isChecked();
        String bio = etBio.getText() != null ? etBio.getText().toString().trim() : "";

        // Validate profile picture
        if (base64ImageString == null) {
            Toast.makeText(this, "Profile picture is required", Toast.LENGTH_SHORT).show();
            btnUploadPic.setError("Required");
            btnUploadPic.requestFocus();
            return;
        }

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

        // Validate qualities (optional)
        if (qualitiesList.isEmpty()) {
            Toast.makeText(this, "Consider adding at least one teaching quality", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show loading state
        btnSubmit.setEnabled(false);
        btnSubmit.setText("Saving...");

        // Save tutor data
        saveTutorData(location, priceStr, subject, firstLessonFree, bio);
    }

    private void saveTutorData(String location, String price,
                               String subject, boolean firstLessonFree, String bio) {
        // Create tutor data map
        Map<String, Object> tutorData = new HashMap<>();
        tutorData.put("name", user.getDisplayName());
        tutorData.put("location", location);
        tutorData.put("price", price);
        tutorData.put("subject", subject);
        tutorData.put("firstLessonFree", firstLessonFree);
        tutorData.put("email", user.getEmail());
        tutorData.put("rating", 0.0);
        tutorData.put("createdAt", System.currentTimeMillis());
        tutorData.put("reviewCount", 0);
        tutorData.put("isAmbassador", "");
        tutorData.put("bio", bio);

        // Add qualities to the tutor data
        tutorData.put("qualities", qualitiesList);

        // Convert education list to Map format for Firestore
        List<Map<String, Object>> educationMaps = new ArrayList<>();
        for (Education education : educationList) {
            Map<String, Object> eduMap = new HashMap<>();
            eduMap.put("degree", education.getDegree());
            eduMap.put("institution", education.getInstitution());
            eduMap.put("yearRange", education.getYearRange());
            educationMaps.add(eduMap);
        }
        tutorData.put("educationList", educationMaps);

// Convert certificates list to Map format for Firestore
        List<Map<String, Object>> certificateMaps = new ArrayList<>();
        for (Certificate certificate : certificateList) {
            Map<String, Object> certMap = new HashMap<>();
            certMap.put("title", certificate.getTitle());
            certMap.put("imageUrl", certificate.getImageUrl());
            certificateMaps.add(certMap);
        }
        tutorData.put("certificates", certificateMaps);

        // Profile pic is validated as required in validateAndSaveTutorData()
        tutorData.put("profilePic", "data:image/png;base64," + base64ImageString);

        // Save to Firestore
        firestore.collection("tutors")
                .document(user.getUid())
                .set(tutorData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(BecomeATutorActivity.this,
                            "Successfully registered as a tutor!",
                            Toast.LENGTH_SHORT).show();

                    // Also update the user's role in the users collection
                    updateUserRole();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error saving tutor data", e);
                    Toast.makeText(BecomeATutorActivity.this,
                            "Failed to save your information: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                    btnSubmit.setEnabled(true);
                    btnSubmit.setText("Become a Tutor");
                });
    }

    private void updateUserRole() {
        // Update user document to mark them as a tutor
        Map<String, Object> userData = new HashMap<>();
        userData.put("isTutor", true);

        firestore.collection("users")
                .document(user.getUid())
                .update(userData)
                .addOnSuccessListener(aVoid -> {
                    // Navigate to appropriate screen after successful registration
                    Intent intent = new Intent(BecomeATutorActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error updating user role", e);
                    // Still navigate away even if this update fails
                    Intent intent = new Intent(BecomeATutorActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                Bitmap selectedImage = BitmapFactory.decodeStream(inputStream);

                // Compress to JPEG and check size
                ByteArrayOutputStream tempStream = new ByteArrayOutputStream();
                selectedImage.compress(Bitmap.CompressFormat.JPEG, 90, tempStream);
                byte[] imageBytes = tempStream.toByteArray();

                // Define the maximum size limit (e.g., 1 MB = 1,048,576 bytes)
                final int MAX_IMAGE_SIZE = 1048576;

                if (imageBytes.length > MAX_IMAGE_SIZE) {
                    Toast.makeText(this, "Image too large. Please select an image smaller than 1 MB.", Toast.LENGTH_LONG).show();
                    return;
                }

                if (requestCode == REQUEST_CODE_SELECT_IMAGE) {
                    ImageView ivProfilePic = findViewById(R.id.ivProfilePic);
                    ivProfilePic.setImageBitmap(selectedImage);
                    base64ImageString = encodeImageToBase64(selectedImage);
                    btnUploadPic.setError(null);
                } else if (requestCode == REQUEST_CODE_SELECT_CERTIFICATE) {
                    AlertDialog dialog = (AlertDialog) getWindow().getDecorView()
                            .findViewById(android.R.id.content)
                            .getRootView()
                            .getTag(R.id.dialog_tag);

                    if (dialog != null && dialog.isShowing()) {
                        ImageView ivCertificatePreview = dialog.findViewById(R.id.ivCertificatePreview);
                        if (ivCertificatePreview != null) {
                            ivCertificatePreview.setImageBitmap(selectedImage);
                            ivCertificatePreview.setVisibility(View.VISIBLE);
                        }
                    }

                    currentCertificateBase64 = "data:image/png;base64," + encodeImageToBase64(selectedImage);
                }

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to select image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}