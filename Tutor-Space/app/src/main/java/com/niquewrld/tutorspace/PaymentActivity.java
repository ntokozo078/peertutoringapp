package com.niquewrld.tutorspace;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.niquewrld.tutorspace.helpers.FavoritesHelper;
import com.niquewrld.tutorspace.helpers.TutorDataHelper;
import com.niquewrld.tutorspace.models.TutorProfile;
import com.niquewrld.tutorspace.models.Transaction;

import java.util.Date;
import java.util.Random;

public class PaymentActivity extends AppCompatActivity {
    private TutorProfile profile;
    private String requestId;
    private FirebaseFirestore firestore;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private ImageView profileImage;
    private RadioGroup paymentMethodGroup;
    private MaterialCardView cardDetailsLayout;
    private MaterialCardView paypalDetailsLayout;
    private EditText cardNumberInput, expiryInput, cvvInput;
    private Button payButton;
    private TutorDataHelper tutorDataHelper;
    private FavoritesHelper favoritesHelper;
    private TextView resultText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        favoritesHelper = new FavoritesHelper(this);
        user = mAuth.getCurrentUser();

        tutorDataHelper = new TutorDataHelper(this, favoritesHelper, firestore, user, null);

        requestId = (String) getIntent().getSerializableExtra("request_id");

        if (requestId == null) {
            finish();
            return;
        }

        profileImage = findViewById(R.id.profile_image);
        paymentMethodGroup = findViewById(R.id.paymentMethodGroup);
        cardDetailsLayout = findViewById(R.id.cardDetailsLayout);
        paypalDetailsLayout = findViewById(R.id.paypalDetailsLayout);
        cardNumberInput = findViewById(R.id.cardNumberInput);
        expiryInput = findViewById(R.id.expiryInput);
        cvvInput = findViewById(R.id.cvvInput);
        payButton = findViewById(R.id.payButton);
        resultText = findViewById(R.id.resultText);

        paymentMethodGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.cardPayment) {
                cardDetailsLayout.setVisibility(View.VISIBLE);
                paypalDetailsLayout.setVisibility(View.GONE);
            } else {
                paypalDetailsLayout.setVisibility(View.VISIBLE);
                cardDetailsLayout.setVisibility(View.GONE);
            }
        });

        payButton.setOnClickListener(v -> processPayment());

        // Fetch the tutor profile asynchronously
        getTutorProfile(requestId, new OnProfileFetchedListener() {
            @Override
            public void onProfileFetched(TutorProfile fetchedProfile) {
                profile = fetchedProfile; // Assign the fetched profile
                loadData(); // Call loadData() only after the profile is fetched
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(PaymentActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                finish(); // End the activity if an error occurs
            }
        });
    }

    private void getTutorProfile(String requestId, OnProfileFetchedListener listener) {
        // Fetch the request document from Firestore
        firestore.collection("requests")
                .document(requestId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Extract the tutor ID from the request document
                        String tutorId = documentSnapshot.getString("tutorId");

                        if (tutorId != null) {
                            tutorDataHelper.getTutorById(tutorId).addOnCompleteListener(task -> {
                                if (task.isSuccessful() && task.getResult() != null) {
                                    TutorProfile fetchedProfile = task.getResult();
                                    listener.onProfileFetched(fetchedProfile); // Pass the profile to the listener
                                } else {
                                    listener.onError("Failed to fetch tutor profile.");
                                }
                            });
                        } else {
                            listener.onError("Tutor ID not found in the request.");
                        }
                    } else {
                        listener.onError("Request not found.");
                    }
                })
                .addOnFailureListener(e -> listener.onError("Error retrieving request: " + e.getMessage()));
    }

    private interface OnProfileFetchedListener {
        void onProfileFetched(TutorProfile profile);

        void onError(String errorMessage);
    }

    private void processPayment() {
        RadioButton selectedPayment = findViewById(paymentMethodGroup.getCheckedRadioButtonId());

        if (selectedPayment == null) {
            showError("Please select a payment method");
            return;
        }

        if (selectedPayment.getId() == R.id.cardPayment) {
            if (!validateCardDetails()) {
                return;
            }
            // Simulate card payment processing
            simulatePayment();
        } else {

            if (!validatePayPalEmail()) {
                return;
            }

            // Simulate PayPal payment processing
            simulatePayment();
        }
    }

    private boolean validatePayPalEmail() {
        // Get the entered email text
        TextInputEditText emailInput = findViewById(R.id.paypalEmailInput);
        String email = emailInput.getText().toString().trim();

        // Check if email is empty
        if (email.isEmpty()) {
            emailInput.setError("Email address is required");
            return false;
        }

        // Validate email format using a regex pattern
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (!email.matches(emailPattern)) {
            emailInput.setError("Please enter a valid email address");
            return false;
        }

        // Email is valid
        return true;
    }

    private boolean validateCardDetails() {
        String cardNumber = cardNumberInput.getText().toString();
        String expiry = expiryInput.getText().toString();
        String cvv = cvvInput.getText().toString();

        if (TextUtils.isEmpty(cardNumber) || cardNumber.length() != 16) {
            showError("Please enter a valid 16-digit card number");
            return false;
        }

        if (TextUtils.isEmpty(expiry) || !expiry.matches("\\d{2}/\\d{2}")) {
            showError("Please enter a valid expiry date (MM/YY)");
            return false;
        }

        if (TextUtils.isEmpty(cvv) || cvv.length() != 3) {
            showError("Please enter a valid 3-digit CVV");
            return false;
        }

        return true;
    }

    private void simulatePayment() {
        // Simulate payment processing
        payButton.setEnabled(false);
        payButton.setText("Processing...");

        // Simulate network delay
        new android.os.Handler().postDelayed(() -> {
            boolean isSuccess = Math.random() < 0.9;

            if (isSuccess) {

                Transaction transaction;
                if(profile.hasFirstLessonFree()){
                     transaction = new Transaction(user.getUid(), profile.getId() , user.getDisplayName(), profile.getName(), "0" , "Success", false ,  new Date() , null , "Free First Lesson");
                }
                else {
                     transaction = new Transaction(user.getUid(), profile.getId(),  user.getDisplayName() , profile.getName(), profile.getPrice() , "Success", false ,  new Date() , null , "Lesson");
                }

                saveTransaction(transaction, new OnTransactionSavedListener() {
                    @Override
                    public void onTransactionSaved(String transactionId) {
                        firestore.collection("requests")
                                .document(requestId)
                                .update("isPayed", true)
                                .addOnSuccessListener(aVoid -> {
                                    // Navigate to the payment success layout
                                    setContentView(R.layout.activity_payment_result);

                                    MaterialTextView etMessage = findViewById(R.id.etMessage);
                                    etMessage.setText("Payment Successful!");

                                    MaterialTextView etTransactionID = findViewById(R.id.transactionIdText);
                                    etTransactionID.setText("Transaction ID: " + transactionId);

                                    MaterialButton button = findViewById(R.id.backToHomeButton);
                                    button.setOnClickListener(v -> {
                                        Intent intent = new Intent(PaymentActivity.this , MainActivity.class);
                                        startActivity(intent);
                                    });

                                    Toast.makeText(PaymentActivity.this, "Payment Successful!", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    resultText.setText("Error updating payment status: " + e.getMessage());
                                    resultText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                                    payButton.setEnabled(true);
                                    payButton.setText("Pay Now");
                                });
                    }

                    @Override
                    public void onError(String errorMessage) {
                        resultText.setText(errorMessage);
                        resultText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                        payButton.setEnabled(true);
                        payButton.setText("Pay Now");
                    }

                });


            } else {
                Transaction transaction;
                if(profile.hasFirstLessonFree()){
                    transaction = new Transaction(user.getUid(), profile.getId(),  user.getDisplayName() ,profile.getName(), "0" , "Success", false ,  new Date() , null , "Free First Lesson");
                }
                else{
                    transaction = new Transaction(user.getUid(), profile.getId(), user.getDisplayName() ,profile.getName(), profile.getPrice() , "Success", false ,  new Date() , null , "Lesson");
                }
                saveTransaction(transaction, new OnTransactionSavedListener() {
                    @Override
                    public void onTransactionSaved(String transactionId) {
                        setContentView(R.layout.activity_payment_result);

                        ImageView resultLogo = findViewById(R.id.result_logo);
                        resultLogo.setImageResource(R.drawable.ic_declined);

                        MaterialTextView etMessage = findViewById(R.id.etMessage);
                        etMessage.setText("Payment Declined!");

                        MaterialTextView etTransactionID = findViewById(R.id.transactionIdText);
                        etTransactionID.setText("Transaction ID: " + transactionId);


                        MaterialButton button = findViewById(R.id.backToHomeButton);
                        button.setOnClickListener(v -> {
                            Intent intent = new Intent(PaymentActivity.this , MainActivity.class);
                            startActivity(intent);
                        });

                        Toast.makeText(PaymentActivity.this, "Payment Declined!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(String errorMessage) {
                        resultText.setText(errorMessage);
                        resultText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                        payButton.setEnabled(true);
                        payButton.setText("Pay Now");
                    }
                });

            }
        }, 2000);
    }

    private void saveTransaction(Transaction transaction, OnTransactionSavedListener listener) {
        firestore.collection("transactions")
                .add(transaction)
                .addOnSuccessListener(documentReference -> {
                    String transactionId = documentReference.getId(); // Get the transaction ID
                    listener.onTransactionSaved(transactionId); // Notify the listener with the transaction ID
                })
                .addOnFailureListener(e -> {
                    listener.onError("Failed to save transaction: " + e.getMessage());
                });
    }

    private interface OnTransactionSavedListener {
        void onTransactionSaved(String transactionId);

        void onError(String errorMessage);
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void loadData() {
        if (profile == null) {
            // Handle the null case, e.g., show an error message and return
            Toast.makeText(this, "Tutor profile is not available.", Toast.LENGTH_SHORT).show();
            return;
        }

        TextView profileMessage = findViewById(R.id.profile_message);

        if (profile.hasFirstLessonFree()) {
            profileMessage.setText(profile.getName() + " offers their first lesson for free in order to get to know you.");
        } else {
            Random random = new Random();
            int contactCount = random.nextInt(20) + 1;
            profileMessage.setText(profile.getName() + " is popular — contacted " + contactCount + " times this month!");
        }

        TextView rate = findViewById(R.id.rate);
        rate.setText("R" + profile.getPrice());

        String profilePicUrl = profile.getProfilePic();

        if (profilePicUrl != null && !profilePicUrl.isEmpty()) {
            Glide.with(this)
                    .load(profilePicUrl)
                    .placeholder(R.drawable.ic_badge)
                    .error(R.drawable.ic_badge)
                    .into(profileImage);
        }
    }
}

