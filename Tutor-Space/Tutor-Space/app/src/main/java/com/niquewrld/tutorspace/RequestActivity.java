package com.niquewrld.tutorspace;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.core.CometChat;
import com.cometchat.chat.exceptions.CometChatException;
import com.cometchat.chat.models.BaseMessage;
import com.cometchat.chat.models.TextMessage;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.niquewrld.tutorspace.auth.LoginActivity;
import com.niquewrld.tutorspace.models.ProfileManager;
import com.niquewrld.tutorspace.models.TutorProfile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class RequestActivity extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private TutorProfile profile;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    ImageButton btnBack;
    private FirebaseFirestore firestore;
    private FusedLocationProviderClient fusedLocationClient;
    private ImageView profileImage;
    private EditText addressField, phoneField, messageField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        user = mAuth.getCurrentUser();

        if (user == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        profileImage = findViewById(R.id.ivTutorProfile);
        addressField = findViewById(R.id.tvAddressValue);
        phoneField = findViewById(R.id.tvPhoneValue);
        messageField = findViewById(R.id.etMessage);
        btnBack = findViewById(R.id.btnBack);

        profile = ProfileManager.getTutorProfile();

        if (profile == null) {
            finish();
        }

        MaterialButton nextBtn = findViewById(R.id.btnNext);
        nextBtn.setOnClickListener(v -> {
            if (validateFields()) {
                saveRequestToFirestore();
            }
        });

        btnBack.setOnClickListener(v -> {
            finish();
        });

        loadData();
        fetchAddressFromFirestore();
    }

    @SuppressLint("SetTextI18n")
    private void loadData() {
        String firstname = Objects.requireNonNull(user.getDisplayName()).split(" ")[0];

        messageField.setText("Hello " + profile.getName() + ",\n" +
                "My name is " + firstname + " and I am looking for " + profile.getSubject() + " tutor.\n" +
                "I would like to take lessons by webcam.\n" +
                "Ideally, I would like to start lessons as soon as possible.\n" +
                "Would that work for you? Could you contact me so that we can discuss it further?\n" +
                "Have a great day,\n" +
                "See you soon, " + firstname);

        TextView name = findViewById(R.id.tvSubTitle);
        name.setText("Your first class with " + profile.getName());

        String profilePicUrl = profile.getProfilePic();

        if (profilePicUrl != null && !profilePicUrl.isEmpty()) {
            Glide.with(this)
                    .load(profilePicUrl)
                    .placeholder(R.drawable.ic_badge)
                    .error(R.drawable.ic_badge)
                    .into(profileImage);
        }

    }

    private void fetchAddressFromFirestore() {
        firestore.collection("users").document(user.getUid()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String address = documentSnapshot.getString("address");
                        if (!TextUtils.isEmpty(address)) {
                            addressField.setText(address);
                        } else {
                            fetchCurrentLocation();
                        }
                    } else {
                        fetchCurrentLocation();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to fetch address", Toast.LENGTH_SHORT).show();
                    fetchCurrentLocation();
                });
    }

    private void fetchCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        setAddressFromLocation(location);
                    } else {
                        requestNewLocationData();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to get current location", Toast.LENGTH_SHORT).show());
    }

    private void requestNewLocationData() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request the missing permissions from the user
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return; // Exit the method since permissions are not granted
        }

        // Proceed with requesting location updates if permissions are granted
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);

        fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        setAddressFromLocation(location);
                        fusedLocationClient.removeLocationUpdates(this);
                        break;
                    }
                }
            }
        }, getMainLooper());
    }

    private void setAddressFromLocation(Location location) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null && !addresses.isEmpty()) {
                String address = addresses.get(0).getAddressLine(0);
                addressField.setText(address);
            } else {
                Toast.makeText(this, "Unable to fetch address from location", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to convert location to address", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateFields() {
        String message = messageField.getText().toString().trim();
        String phone = phoneField.getText().toString().trim();
        String address = addressField.getText().toString().trim();

        if (TextUtils.isEmpty(message)) {
            Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(phone) || phone.length() < 9) {
            Toast.makeText(this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(address)) {
            Toast.makeText(this, "Please enter an address", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void saveRequestToFirestore() {
        String address = addressField.getText().toString().trim();
        String phone = phoneField.getText().toString().trim();
        String message = messageField.getText().toString().trim();
        String tutorId = profile.getId();
        String tutorName = profile.getName();
        String tutorSubject = profile.getSubject();

        // Query Firestore to check if a request already exists
        firestore.collection("requests")
                .whereEqualTo("userId", user.getUid()) // Filter requests by the current user
                .whereEqualTo("tutorId", tutorId) // Filter requests by the current tutor
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        // If a request already exists
                        Toast.makeText(this, "A request for this tutor already exists.", Toast.LENGTH_SHORT).show();
                    } else {
                        // If no existing request is found, create a new one
                        Map<String, Object> requestData = new HashMap<>();
                        requestData.put("userId", user.getUid());
                        requestData.put("userName", user.getDisplayName());
                        requestData.put("address", address);
                        requestData.put("phone", phone);
                        requestData.put("message", message);
                        requestData.put("tutorId", tutorId);
                        requestData.put("tutorName", tutorName);
                        requestData.put("isPayed", false);
                        requestData.put("isAccepted", false);
                        requestData.put("isDeclined", false);
                        requestData.put("isCompleted", false);
                        requestData.put("rating", 0);
                        requestData.put("tutorSubject", tutorSubject);
                        requestData.put("timestamp", System.currentTimeMillis());

                        String receiverUID = profile.getId();
                        String messageText = message;
                        String receiverType = CometChatConstants.RECEIVER_TYPE_USER;

                        TextMessage textMessage = new TextMessage(
                                receiverUID,
                                messageText,
                                receiverType
                        );

                        CometChat.sendMessage(textMessage, new CometChat.CallbackListener<TextMessage>() {
                            @Override
                            public void onSuccess(TextMessage message) {
                                Log.d("CometChat", "Message sent successfully: " + message.toString());
                            }

                            @Override
                            public void onError(CometChatException e) {
                                Log.e("CometChat", "Message sending failed with exception: " + e.getMessage());
                            }
                        });


                        firestore.collection("requests")
                                .add(requestData)
                                .addOnSuccessListener(documentReference -> {
                                    Toast.makeText(this, "Request created successfully", Toast.LENGTH_SHORT).show();
                                    proceedToNext(documentReference.getId()); // Navigate to the next activity
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Failed to create request in Firestore", Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to check existing requests in Firestore", Toast.LENGTH_SHORT).show();
                });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, fetch the current location
                fetchCurrentLocation();
            } else {
                // Permission denied, inform the user
                Toast.makeText(this, "Location permission is required to fetch the address", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void proceedToNext(String id) {
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra("request_id", id);
        startActivity(intent);
    }
}