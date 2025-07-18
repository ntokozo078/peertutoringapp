package com.niquewrld.tutorspace;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.niquewrld.tutorspace.models.Transaction;
import com.niquewrld.tutorspace.R;
import com.niquewrld.tutorspace.adapters.TransactionsAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class WalletActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private String currentUserId;
    private TextView creditBalanceTextView;
    private RecyclerView transactionsRecyclerView;
    private TransactionsAdapter adapter;
    private List<Transaction> transactionList = new ArrayList<>();
    private double creditBalance = 0.0;
    private List<Transaction> tutorTransactions = new ArrayList<>();
    private List<Transaction> userTransactions = new ArrayList<>();
    private ProgressBar loadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        loadingIndicator = findViewById(R.id.loadingIndicator);

        loadingIndicator.setVisibility(View.VISIBLE);


        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if(currentUserId == null){
            finish();
        }

        db = FirebaseFirestore.getInstance();
        creditBalanceTextView = findViewById(R.id.creditBalanceTextView);
        transactionsRecyclerView = findViewById(R.id.transactionsRecyclerView);
        Button withdrawButton = findViewById(R.id.withdrawButton);
        EditText withdrawAmountEditText = findViewById(R.id.withdrawAmountEditText);

        // Set up RecyclerView
        adapter = new TransactionsAdapter(transactionList);
        transactionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        transactionsRecyclerView.setAdapter(adapter);

        // Fetch transactions
        fetchTransactions();

        // Handle Withdraw Button
        withdrawButton.setOnClickListener(v -> {
            String amountStr = withdrawAmountEditText.getText().toString();
            if (!amountStr.isEmpty()) {
                double amount = Double.parseDouble(amountStr);
                if (amount > 0 && amount <= creditBalance) {
                    creditBalance -= amount;
                    updateBalanceUI();
                    Toast.makeText(WalletActivity.this, "Withdrawal Successful", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(WalletActivity.this, "Invalid Amount", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void fetchTransactions() {
        creditBalance = 0.0;
        transactionList.clear();

        Task<QuerySnapshot> tutorQuery = db.collection("transactions")
                .whereEqualTo("tutorId", currentUserId)
                .orderBy("createdAt", Query.Direction.ASCENDING)
                .get();

        Task<QuerySnapshot> userQuery = db.collection("transactions")
                .whereEqualTo("userId", currentUserId)
                .orderBy("createdAt", Query.Direction.ASCENDING)
                .get();

        Tasks.whenAllSuccess(tutorQuery, userQuery).addOnSuccessListener(results -> {
            int skipped = 0;
            int added = 0;
            transactionList.clear();
            creditBalance = 0.0;

            for (Object result : results) {
                QuerySnapshot snapshot = (QuerySnapshot) result;
                for (DocumentSnapshot document : snapshot.getDocuments()) {
                    Transaction transaction = document.toObject(Transaction.class);

                    if (transaction == null || transaction.getAmount() == null) {
                        skipped++;
                        continue;
                    }

                    try {
                        double amount = Double.parseDouble(transaction.getAmount());
                        Boolean isDebited = getBooleanFromDocument(document , "deposited");

                        transaction.setDeposited(isDebited);

                        if (currentUserId.equals(transaction.getTutorId())) {
                            if (isDebited != null && isDebited) {
                                creditBalance += amount;
                            }
                            transaction.setType("Incoming");
                        } else if (currentUserId.equals(transaction.getUserId())) {
                            creditBalance -= amount;
                            transaction.setType("Outgoing");
                        } else {
                            skipped++;
                            continue;
                        }

                        transactionList.add(transaction);
                        added++;

                    } catch (NumberFormatException e) {
                        skipped++;
                    }
                }
            }

            adapter.notifyDataSetChanged();
            updateBalanceUI();

            loadingIndicator.setVisibility(View.GONE);

        }).addOnFailureListener(e -> {
            Toast.makeText(WalletActivity.this, "Failed to load transactions: " + e.getMessage(), Toast.LENGTH_LONG).show();
        });
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


    private void updateBalanceUI() {
        creditBalanceTextView.setText(String.format("R%.2f", creditBalance));
    }
}