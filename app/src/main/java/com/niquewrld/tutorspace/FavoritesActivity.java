package com.niquewrld.tutorspace;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.niquewrld.tutorspace.auth.LoginActivity;
import com.niquewrld.tutorspace.auth.RegisterActivity;
import com.niquewrld.tutorspace.helpers.CategoryViewHelper;
import com.niquewrld.tutorspace.helpers.FavoritesHelper;
import com.niquewrld.tutorspace.helpers.ToolbarHelper;
import com.niquewrld.tutorspace.helpers.TutorDataHelper;
import com.niquewrld.tutorspace.models.Category;
import com.niquewrld.tutorspace.models.TutorProfile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FavoritesActivity extends AppCompatActivity {
    private FavoritesHelper favoritesHelper;
    private TutorDataHelper tutorDataHelper;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    FirebaseFirestore firestore;
    private LinearLayout tutorCardsContainer;
    private List<TutorProfile> tutorProfiles;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        favoritesHelper = new FavoritesHelper(this);
        tutorCardsContainer = findViewById(R.id.tutor_cards_container);

        Toolbar toolbar = findViewById(R.id.custom_toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        ToolbarHelper.setupToolbarWithNavigation(
                this,
                R.id.custom_toolbar,
                drawerLayout,
                navigationView,
                false // or true if you want back button
        );

        tutorDataHelper = new TutorDataHelper(this , favoritesHelper , firestore , user , tutorCardsContainer);

        tutorDataHelper.loadFavTutorData();
        tutorDataHelper.loadTutorCards();

    }



}