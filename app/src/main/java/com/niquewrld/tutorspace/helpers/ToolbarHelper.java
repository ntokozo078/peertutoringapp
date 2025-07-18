package com.niquewrld.tutorspace.helpers;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.niquewrld.tutorspace.BecomeATutorActivity;
import com.niquewrld.tutorspace.CalenderActivity;
import com.niquewrld.tutorspace.ChatActivity;
import com.niquewrld.tutorspace.FavoritesActivity;
import com.niquewrld.tutorspace.MainActivity;
import com.niquewrld.tutorspace.MyRequestsActivity;
import com.niquewrld.tutorspace.ProfileActivity;
import com.niquewrld.tutorspace.R;
import com.niquewrld.tutorspace.SearchActivity;
import com.niquewrld.tutorspace.WalletActivity;
import com.niquewrld.tutorspace.ai.AIActivity;
import com.niquewrld.tutorspace.auth.LoginActivity;
import com.niquewrld.tutorspace.auth.RegisterActivity;


import java.util.Objects;

public class ToolbarHelper {

    public static void setupToolbarWithNavigation(AppCompatActivity activity,
                                                  int toolbarId,
                                                  DrawerLayout drawerLayout,
                                                  NavigationView navigationView,
                                                  boolean showBackButton) {

        Toolbar toolbar = activity.findViewById(toolbarId);
        if (toolbar != null) {
            activity.setSupportActionBar(toolbar);
            Objects.requireNonNull(activity.getSupportActionBar()).setDisplayShowTitleEnabled(false);
        }

        if (!showBackButton) {

        }

        RoleHelper ropeHelper = new RoleHelper();

        FirebaseUser user =  FirebaseAuth.getInstance().getCurrentUser();
        Menu menu = navigationView.getMenu();

        if(user != null){

            MenuItem walletItem = menu.findItem(R.id.nav_wallet);
            walletItem.setVisible(true);

            ropeHelper.isUserTutor(user.getUid(), new RoleHelper.TutorCheckCallback() {
                @Override
                public void onResult(boolean isTutor) {
                    if (isTutor) {

                        MenuItem becomeItem = menu.findItem(R.id.nav_become_tutor);
                        MenuItem requestsItem = menu.findItem(R.id.nav_requests);

                        requestsItem.setVisible(true);
                        becomeItem.setVisible(false);
                    }
                }

                @Override
                public void onError(Exception e) {
                    // Handle the error here
                    Log.e("TutorCheck", "Error checking user role", e);
                }
            });
        }

        // Handle menu button toggle
        ImageButton menuButton = activity.findViewById(R.id.menu_button);
        if (menuButton != null) {
            menuButton.setOnClickListener(v -> {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            });
        }
        Context context = activity.getApplicationContext();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();


        View headerView = navigationView.getHeaderView(0);

        TextView tvUserName = headerView.findViewById(R.id.tvUserName);


        ImageButton ivArrowIcon = headerView.findViewById(R.id.ivArrowIcon);
        ImageView ivProfileAvatar = headerView.findViewById(R.id.ivProfileAvatar);

        LinearLayout llAuth = headerView.findViewById(R.id.llAuth);
        ConstraintLayout clProfile = headerView.findViewById(R.id.clProfile);

        MaterialButton loginBtn = headerView.findViewById(R.id.btn_login);
        MaterialButton signupBtn = headerView.findViewById(R.id.btn_signup);

        MenuItem logoutItem = menu.findItem(R.id.nav_logout);
        MenuItem favItem = menu.findItem(R.id.nav_fav);
        MenuItem chatItem = menu.findItem(R.id.nav_chat);
        MenuItem calenderItem = menu.findItem(R.id.nav_calender);

        if (user == null) {
            logoutItem.setVisible(false);
            favItem.setVisible(false);
            chatItem.setVisible(false);
            llAuth.setVisibility(View.VISIBLE);
        } else {
            tvUserName.setText(user.getDisplayName().split(" ")[0]);

            Glide.with(context)
                    .load(user.getPhotoUrl())
                    .into(ivProfileAvatar);

            ivArrowIcon.setOnClickListener(v -> {
                if (!(activity instanceof ProfileActivity)) {
                    Intent intent = new Intent(activity, ProfileActivity.class);
                    activity.startActivity(intent);
                }
            });


            clProfile.setVisibility(View.VISIBLE);
            calenderItem.setVisible(true);
        }

        loginBtn.setOnClickListener(v -> {
            Intent intent = new Intent(activity, LoginActivity.class);
            activity.startActivity(intent);
        });

        signupBtn.setOnClickListener(v -> {
            Intent intent = new Intent(activity, RegisterActivity.class);
            activity.startActivity(intent);
        });

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_logout) {
                mAuth.signOut();
                Toast.makeText(context, "Logged out successfully!", Toast.LENGTH_SHORT).show();
                activity.startActivity(new Intent(activity, MainActivity.class));
                activity.finish();
            } else if (id == R.id.nav_fav) {
                if (!(activity instanceof FavoritesActivity)) {
                    activity.startActivity(new Intent(activity, FavoritesActivity.class));
                }
            } else if (id == R.id.nav_chat) {
                if (!(activity instanceof ChatActivity)) {
                    activity.startActivity(new Intent(activity, ChatActivity.class));
                }
            } else if (id == R.id.nav_become_tutor) {
                if (!(activity instanceof BecomeATutorActivity)) {
                    activity.startActivity(new Intent(activity, BecomeATutorActivity.class));
                }
            } else if (id == R.id.nav_requests) {
                if (!(activity instanceof MyRequestsActivity)) {
                    activity.startActivity(new Intent(activity, MyRequestsActivity.class));
                }
            } else if (id == R.id.nav_wallet) {
                if (!(activity instanceof WalletActivity)) {
                    activity.startActivity(new Intent(activity, WalletActivity.class));
                }
            } else if (id == R.id.nav_calender) {
                if (!(activity instanceof CalenderActivity)) {
                    activity.startActivity(new Intent(activity, CalenderActivity.class));
                }
            } else if (id == R.id.nav_ai) {
                if (!(activity instanceof CalenderActivity)) {
                    activity.startActivity(new Intent(activity, AIActivity.class));
                }
            }else if (id == R.id.nav_search) {
                if (!(activity instanceof SearchActivity)) {
                    activity.startActivity(new Intent(activity, SearchActivity.class));
                }
            }


            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }
}
