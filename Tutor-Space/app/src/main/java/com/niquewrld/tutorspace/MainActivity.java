package com.niquewrld.tutorspace;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.cometchat.chat.core.CometChat;
import com.cometchat.chat.exceptions.CometChatException;
import com.cometchat.chat.models.User;
import com.cometchat.chatuikit.shared.cometchatuikit.CometChatUIKit;
import com.cometchat.chatuikit.shared.cometchatuikit.UIKitSettings;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.niquewrld.tutorspace.chat.AppConstants;
import com.niquewrld.tutorspace.helpers.FavoritesHelper;
import com.niquewrld.tutorspace.helpers.RoleHelper;
import com.niquewrld.tutorspace.helpers.ToolbarHelper;
import com.niquewrld.tutorspace.helpers.TutorDataHelper;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    FirebaseFirestore firestore;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private LinearLayout tutorCardsContainer;
    private FavoritesHelper favoritesHelper;
    private TutorDataHelper tutorDataHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        favoritesHelper = new FavoritesHelper(this);
        tutorCardsContainer = findViewById(R.id.tutor_cards_container);

        tutorDataHelper = new TutorDataHelper(this , favoritesHelper , firestore , user , tutorCardsContainer);

        if (user != null) {
            String userId = user.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            DocumentReference docRef = db.collection("users").document(userId);
            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (!document.exists()) {
                        Intent intent = new Intent(this, SetupProfileActivity.class);
                        startActivity(intent);
                        Toast.makeText(this, "Please choose your modules", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.w("Firestore", "Failed to retrieve user data", task.getException());
                }
            });

            UIKitSettings uiKitSettings = new UIKitSettings.UIKitSettingsBuilder().setRegion(AppConstants.REGION).setAppId(AppConstants.APP_ID).setAuthKey(AppConstants.AUTH_KEY).subscribePresenceForAllUsers().build();

            CometChatUIKit.init(this, uiKitSettings, new CometChat.CallbackListener<String>() {
                @Override
                public void onSuccess(String s) {

                }
                @Override
                public void onError(CometChatException e) {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            CometChatUIKit.login(user.getUid(), new CometChat.CallbackListener<User>() {
                @Override
                public void onSuccess(User user) {
                }

                @Override
                public void onError(CometChatException e) {
                    User newuser = new User();
                    newuser.setUid(user.getUid());
                    newuser.setName(user.getDisplayName());
                    CometChatUIKit.createUser(newuser, new CometChat.CallbackListener<User>() {
                        @Override
                        public void onSuccess(User user) {
                            CometChatUIKit.login(user.getUid(), new CometChat.CallbackListener<User>() {
                                @Override
                                public void onSuccess(User user) {
                                }
                                @Override
                                public void onError(CometChatException e) {
                                    Toast.makeText(MainActivity.this, "Failed to create user", Toast.LENGTH_LONG).show();
                                }

                            });
                        }

                        @Override
                        public void onError(CometChatException e) {
                            Toast.makeText(MainActivity.this, "Failed to create user", Toast.LENGTH_LONG).show();
                        }
                    });


                }
            });

        }

        tutorDataHelper.loadTutorData();

        TextView subtitle = findViewById(R.id.navbar_subtitle);

        String text = "Find an online or face-to-face tutor<br>amongst our <b>32 million members</b>";
        subtitle.setText(Html.fromHtml(text));

        MaterialButton createAdBtn = findViewById(R.id.btnBecomeATutor);
        createAdBtn.setOnClickListener(v ->{
            Intent becomeATutor = new Intent(this ,BecomeATutorActivity.class);
            startActivity(becomeATutor);
        });

        ConstraintLayout clSearchBar = findViewById(R.id.clSearchBar);
        clSearchBar.setOnClickListener(v -> {
            Intent search = new Intent(this ,SearchActivity.class);
            startActivity(search);
        });

        //NavBar Code

        drawerLayout = findViewById(R.id.drawer_layout);

        Toolbar toolbar = findViewById(R.id.custom_toolbar);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);

        ToolbarHelper.setupToolbarWithNavigation(
                this,
                R.id.custom_toolbar,
                drawerLayout,
                navigationView,
                false
        );

        // End of NavBar Code

        RoleHelper ropeHelper = new RoleHelper();
        CardView cvBecomeATutor = findViewById(R.id.cvBecomeaTutor);
        if(user != null){
            ropeHelper.isUserTutor(user.getUid(), new RoleHelper.TutorCheckCallback() {
                @Override
                public void onResult(boolean isTutor) {
                    if (!isTutor) {
                        cvBecomeATutor.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onError(Exception e) {
                    // Handle the error here
                    Log.e("TutorCheck", "Error checking user role", e);
                }
            });
        }
        else{
            cvBecomeATutor.setVisibility(View.VISIBLE);
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

    public interface FavoriteCallback {
        void onResult(boolean isFavorite);
    }

}