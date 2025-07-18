package com.niquewrld.tutorspace;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.niquewrld.tutorspace.adapters.RequestAdapter;
import com.niquewrld.tutorspace.helpers.RoleHelper;
import com.niquewrld.tutorspace.helpers.ToolbarHelper;
import com.niquewrld.tutorspace.models.Request;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MyRequestsActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private FirebaseFirestore firestore;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;
    private RequestAdapter requestAdapter;
    private List<Request> requestList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_requests);

        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        if (user == null) {
            Toast.makeText(this, "Please log in to view your requests.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        requestList = new ArrayList<>();
        requestAdapter = new RequestAdapter(this , requestList);
        recyclerView.setAdapter(requestAdapter);

        fetchRequests();

        //NavBar Code
        drawerLayout = findViewById(R.id.drawer_layout);

        Toolbar toolbar = findViewById(R.id.custom_toolbar);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        NavigationView navigationView = findViewById(R.id.nav_view);

        ToolbarHelper.setupToolbarWithNavigation(
                this,
                R.id.custom_toolbar,
                drawerLayout,
                navigationView,
                false
        );
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
        return false;
    }

    private void fetchRequests() {

        RoleHelper ropeHelper = new RoleHelper();
        ropeHelper.isUserTutor(user.getUid(), new RoleHelper.TutorCheckCallback() {
            @Override
            public void onResult(boolean isTutor) {
                if (isTutor) {
                    TextView title = findViewById(R.id.header_title);
                    title.setText("Requests");
                    firestore.collection("requests")
                            .whereEqualTo("tutorId", user.getUid())
                            .orderBy("timestamp", Query.Direction.DESCENDING)
                            .addSnapshotListener((querySnapshot , e) -> {
                                if (querySnapshot.isEmpty()) {
                                    Toast.makeText(MyRequestsActivity.this, "You have no requests.", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                requestList.clear();
                                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                    Request request = document.toObject(Request.class);
                                    if (request != null) {
                                        request.setId(document.getId());
                                        request.setPayed(getBooleanFromDocument(document , "isPayed"));
                                        request.setAccepted(getBooleanFromDocument(document , "isAccepted"));
                                        request.setDeclined(getBooleanFromDocument(document , "isDeclined"));
                                        request.setCompleted(getBooleanFromDocument(document , "isCompleted"));
                                        requestList.add(request);
                                    }
                                }
                                requestAdapter.notifyDataSetChanged();
                            });
                }
                else{

                    firestore.collection("requests")
                            .whereEqualTo("userId", user.getUid())
                            .orderBy("timestamp", Query.Direction.DESCENDING)
                            .addSnapshotListener((querySnapshot , e) -> {
                                if (querySnapshot.isEmpty()) {
                                    Toast.makeText(MyRequestsActivity.this, "You have no requests.", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                requestList.clear();
                                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                    Request request = document.toObject(Request.class);
                                    if (request != null) {
                                        request.setId(document.getId());
                                        request.setPayed(getBooleanFromDocument(document , "isPayed"));
                                        request.setAccepted(getBooleanFromDocument(document , "isAccepted"));
                                        request.setDeclined(getBooleanFromDocument(document , "isDeclined"));
                                        request.setCompleted(getBooleanFromDocument(document , "isCompleted"));

                                        requestList.add(request);
                                    }
                                }
                                requestAdapter.notifyDataSetChanged();
                            });
                }
            }

            @Override
            public void onError(Exception e) {
                // Handle the error here
                Log.e("TutorCheck", "Error checking user role", e);
            }
        });

    }
}