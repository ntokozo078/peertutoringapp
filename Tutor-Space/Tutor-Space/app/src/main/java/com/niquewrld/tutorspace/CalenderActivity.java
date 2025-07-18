package com.niquewrld.tutorspace;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.niquewrld.tutorspace.helpers.ToolbarHelper;
import com.niquewrld.tutorspace.models.Tutorial;
import com.niquewrld.tutorspace.adapters.TutorialAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class CalenderActivity extends AppCompatActivity {
    private static final String TAG = "CalenderActivity";
    private DrawerLayout drawerLayout;
    private CalendarView calendarView;
    private RecyclerView recyclerView;
    private TutorialAdapter adapter;
    private List<Tutorial> allTutorials;
    private List<Tutorial> filteredTutorials;

    private FirebaseFirestore firestore;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);

        calendarView = findViewById(R.id.calendar_view);
        recyclerView = findViewById(R.id.upcoming_tutorials_list);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        filteredTutorials = new ArrayList<>();
        adapter = new TutorialAdapter(filteredTutorials);
        recyclerView.setAdapter(adapter);

        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

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

        // Load tutorials from Firestore
        loadTutorialsFromFirestore();

        // Set up CalendarView
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            // Use a Calendar object to handle the date
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, dayOfMonth + 1); // No need to add 1 to the month here

            // Format the date as "yyyy-MM-dd"
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String selectedDate = dateFormat.format(calendar.getTime());

            // Update the tutorial list for the selected date
            updateTutorialList(selectedDate);
        });

        // Show tutorials for the current date by default
        long currentDateMillis = calendarView.getDate();
        String currentDate = android.text.format.DateFormat.format("yyyy-MM-dd", currentDateMillis).toString();
        updateTutorialList(currentDate);
    }

    private void loadTutorialsFromFirestore() {
        allTutorials = new ArrayList<>();

        firestore.collection("requests")
                .whereNotEqualTo("tutorDate", null)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        SimpleDateFormat firestoreDateFormat = new SimpleDateFormat("dd MMM yyyy hh:mm a", Locale.ENGLISH);
                        SimpleDateFormat requiredDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH); // For comparison

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String userId = document.getString("userId");
                            String tutorId = document.getString("tutorId");
                            String tutorDate = document.getString("tutorDate");
                            String userName = document.getString("tutorName");
                            String tutorName = document.getString("tutorName");

                            String tutorSubject = document.getString("tutorSubject");

                            if (user.getUid().equals(userId) || user.getUid().equals(tutorId)) {
                                try {
                                    String formattedDate = requiredDateFormat.format(firestoreDateFormat.parse(tutorDate));

                                    // Parse Firestore date and reformat for filtering
                                    if(user.getUid().equals(userId)){
                                        allTutorials.add(new Tutorial(tutorSubject + " With " + tutorName , formattedDate , tutorDate.split(" ")[3]));

                                    }
                                    else{
                                        allTutorials.add(new Tutorial(tutorSubject + " With " + userName , formattedDate , tutorDate.split(" ")[3]));
                                    }
                                    // Add the tutorial to the list

                                } catch (ParseException e) {
                                    Log.e(TAG, "Error parsing date: " + tutorDate, e);
                                }
                            }

                        }

                        // Update the list for the current date
                        long currentDateMillis = calendarView.getDate();
                        String currentDate = android.text.format.DateFormat.format("yyyy-MM-dd", currentDateMillis).toString();
                        updateTutorialList(currentDate);
                    } else {
                        Log.e(TAG, "Error fetching tutorials from Firestore: ", task.getException());
                    }
                });
    }

    private void updateTutorialList(String date) {
        // Filter tutorials based on the selected date
        filteredTutorials.clear();
        for (Tutorial tutorial : allTutorials) {
            if (tutorial.getDate().equals(date)) {
                filteredTutorials.add(tutorial);
            }
        }
        adapter.notifyDataSetChanged();
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