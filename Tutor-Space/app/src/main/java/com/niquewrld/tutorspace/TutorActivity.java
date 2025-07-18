package com.niquewrld.tutorspace;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.navigation.NavigationView;
import com.niquewrld.tutorspace.helpers.ToolbarHelper;
import com.niquewrld.tutorspace.models.Certificate;
import com.niquewrld.tutorspace.models.Education;
import com.niquewrld.tutorspace.models.ProfileManager;
import com.niquewrld.tutorspace.models.Review;
import com.niquewrld.tutorspace.models.TutorProfile;

import java.util.List;

public class TutorActivity extends AppCompatActivity {
    private TutorProfile profile;
    private ImageView profileImage;
    private ChipGroup chipGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor);

        profileImage = findViewById(R.id.imageView);
        Toolbar toolbar = findViewById(R.id.custom_toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorSecondaryVariant));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorSecondaryVariant));
        }

        ToolbarHelper.setupToolbarWithNavigation(
                this,
                R.id.custom_toolbar,
                drawerLayout,
                navigationView,
                false // or true if you want back button
        );

        MaterialButton contactBtn = findViewById(R.id.btnContact);

        contactBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, RequestActivity.class);
            ProfileManager.setTutorProfile(profile);
            startActivity(intent);
        });

        profile = ProfileManager.getTutorProfile();

        if (profile == null) {
            finish();
        }

        loadData();
    }

    private void loadData() {

        TextView name = findViewById(R.id.profile_name);
        ImageView badge = findViewById(R.id.badge);
        ChipGroup chipGroup = findViewById(R.id.dynamicChipGroup);
        TextView bioText = findViewById(R.id.bioTextView);

        name.setText(profile.getName());

        if (profile.isAmbassador()) {
            badge.setVisibility(View.VISIBLE);
        }

        TextView rate = findViewById(R.id.rate);
        rate.setText("R" + profile.getPrice());

        TextView rating = findViewById(R.id.txt_rating);
        TextView reviewCount = findViewById(R.id.txt_review_count);
        rating.setText(String.valueOf(profile.getRating()));
        reviewCount.setText(String.format("(%d reviews)", profile.getReviewCount()));

        String profilePicUrl = profile.getProfilePic();

        if (profilePicUrl != null && !profilePicUrl.isEmpty()) {
            Glide.with(this)
                    .load(profilePicUrl)
                    .placeholder(R.drawable.ic_badge)
                    .error(R.drawable.ic_badge)
                    .into(profileImage);
        }

        List<String> qualities = profile.getQualities();

        if (qualities != null) {
            chipGroup.removeAllViews();
            for (String quality : qualities) {
                Chip chip = new Chip(this);
                chip.setText(quality);
                chip.setCheckable(false);
                chip.setClickable(false);
                chip.setChipBackgroundColorResource(R.color.colorSecondaryVariant);
                chipGroup.addView(chip);
            }
        } else {
            chipGroup.setVisibility(View.GONE);
        }

        String bio = profile.getBio();

        if (bio != null) {
            bioText.setText(bio);
        } else {
            bioText.setVisibility(View.GONE);
        }

        loadCertificates();

        loadEducation();

        loadReviews();

    }

    private void loadReviews() {
        // Find the reviews section view
        ConstraintLayout reviewsSection = findViewById(R.id.reviewsSection);

        List<String> reviews = profile.getReviews();

        // Hide the section if no reviews
        if (reviews == null || reviews.isEmpty()) {
            reviewsSection.setVisibility(View.GONE);
            return;
        }

        // Get header view
        TextView headerView = findViewById(R.id.reviewsHeader);

        // Get review container
        CardView reviewCard = findViewById(R.id.review1);

        // Show only the first review in the list
        String firstReview = reviews.get(0);

        // Get the linear layout inside the card
        LinearLayout reviewLayout = (LinearLayout) reviewCard.getChildAt(0);

        // Get the reviewer info layout
        LinearLayout reviewerInfoLayout = (LinearLayout) reviewLayout.getChildAt(0);

        // Set reviewer image
        ImageView reviewerImage = (ImageView) reviewerInfoLayout.getChildAt(0);

        /*
        Glide.with(this)
                .load(firstReview.getReviewerImageUrl())
                .placeholder(R.drawable.avatar_ic)
                .error(R.drawable.avatar_ic)
                .into(reviewerImage);
*/
        // Get reviewer details layout
        LinearLayout reviewerDetails = (LinearLayout) reviewerInfoLayout.getChildAt(1);

        // Set reviewer name
        //TextView reviewerName = (TextView) reviewerDetails.getChildAt(0);
        //reviewerName.setText(firstReview.getReviewerName());

        // Set review date
        //TextView reviewDate = (TextView) reviewerDetails.getChildAt(1);
        //reviewDate.setText(firstReview.getFormattedDate());

        // Get rating layout
        LinearLayout ratingLayout = (LinearLayout) reviewerInfoLayout.getChildAt(2);

        // Set review rating
        TextView reviewRating = (TextView) ratingLayout.getChildAt(1);
        //reviewRating.setText(String.valueOf(firstReview.getRating()));

        // Set review text
        TextView reviewText = (TextView) reviewLayout.getChildAt(1);
        //reviewText.setText(firstReview.getReviewText());

        // Configure "Show all reviews" button
        TextView showAllReviews = (TextView) ((ViewGroup) reviewsSection).getChildAt(2);
        showAllReviews.setOnClickListener(v -> {
            // Navigate to all reviews screen
            Intent intent = new Intent(TutorActivity.this, ReviewsActivity.class);
            intent.putExtra("profileId", profile.getId());
            startActivity(intent);
        });

        // Hide "Show all reviews" if there's only one review
        if (reviews.size() <= 1) {
            showAllReviews.setVisibility(View.GONE);
        }
    }

    private void loadEducation() {
        // Find the education section view
        ConstraintLayout clEducation = findViewById(R.id.educationSection);
        ConstraintLayout educationSection = findViewById(R.id.educationSection);

        List<Education> educationList = profile.getEducationList();

        // Hide the section if no education info
        if (educationList == null || educationList.isEmpty()) {
            //Toast.makeText(this, educationLis , Toast.LENGTH_SHORT).show();
            clEducation.setVisibility(View.GONE);
            return;
        }

        // Clear existing education cards and add new ones
        ViewGroup container = (ViewGroup) educationSection;

        // Remove all views except the header
        int childCount = container.getChildCount();
        if (childCount > 1) {
            container.removeViews(1, childCount - 1);
        }

        // Get the header view
        View headerView = container.getChildAt(0);

        // Add each education item
        int index = 1;
        for (Education edu : educationList) {
            CardView cardView = new CardView(this);

            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT);

            params.topToBottom = (index == 1) ?
                    headerView.getId() : container.getChildAt(container.getChildCount() - 1).getId();
            params.topMargin = dpToPx(12);

            cardView.setLayoutParams(params);
            cardView.setRadius(dpToPx(8));
            cardView.setCardElevation(dpToPx(2));
            cardView.setId(View.generateViewId());

            LinearLayout layout = new LinearLayout(this);
            layout.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16));

            // Degree TextView
            TextView degreeText = new TextView(this);
            degreeText.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            degreeText.setText(edu.getDegree());
            degreeText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            degreeText.setTextColor(Color.BLACK);
            degreeText.setTypeface(null, Typeface.BOLD);

            // Institution TextView
            TextView institutionText = new TextView(this);
            institutionText.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            institutionText.setText(edu.getInstitution());
            institutionText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            institutionText.setTextColor(Color.parseColor("#333333"));

            // Year TextView
            TextView yearText = new TextView(this);
            yearText.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            yearText.setText(edu.getYearRange());
            yearText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            yearText.setTextColor(Color.parseColor("#666666"));

            layout.addView(degreeText);
            layout.addView(institutionText);
            layout.addView(yearText);

            cardView.addView(layout);
            container.addView(cardView);

            index++;
        }
    }

    private void loadCertificates() {
        ConstraintLayout clCertificates = findViewById(R.id.clCertificates);
        LinearLayout llCertificates = findViewById(R.id.llCertificates);
        List<Certificate> certificates = profile.getCertificates();

        if (certificates == null || certificates.isEmpty()) {
            clCertificates.setVisibility(View.GONE);
            return;
        }

        int index = 1;
        for (Certificate cert : certificates) {
            // Create CardView
            CardView cardView = new CardView(this);
            LinearLayout.LayoutParams cardLayoutParams = new LinearLayout.LayoutParams(
                    dpToPx(180), dpToPx(120)
            );
            cardLayoutParams.setMarginEnd(dpToPx(12));
            cardView.setLayoutParams(cardLayoutParams);
            cardView.setRadius(dpToPx(8));
            cardView.setCardElevation(dpToPx(4));
            cardView.setPreventCornerOverlap(true);
            cardView.setUseCompatPadding(true);

            // Create FrameLayout to hold ImageView and TextView
            FrameLayout frameLayout = new FrameLayout(this);
            frameLayout.setLayoutParams(new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
            ));

            // Create ImageView
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
            ));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            String profilePicUrl = cert.getImageUrl();
            if (profilePicUrl != null && !profilePicUrl.isEmpty()) {
                Glide.with(this)
                        .load(profilePicUrl)
                        .placeholder(R.drawable.ic_badge)
                        .error(R.drawable.ic_badge)
                        .into(imageView);
            }

            // Create TextView overlay
            TextView textView = new TextView(this);
            FrameLayout.LayoutParams textLayoutParams = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    Gravity.END | Gravity.BOTTOM
            );
            textLayoutParams.setMargins(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8));
            textView.setLayoutParams(textLayoutParams);
            textView.setBackgroundColor(Color.parseColor("#80000000")); // semi-transparent black
            textView.setTextColor(Color.WHITE);
            textView.setPadding(dpToPx(6), dpToPx(4), dpToPx(6), dpToPx(4));
            textView.setText(cert.getTitle());
            textView.setTextSize(12);

            // Add ImageView and TextView to FrameLayout
            frameLayout.addView(imageView);
            frameLayout.addView(textView);

            // Add FrameLayout to CardView
            cardView.addView(frameLayout);

            cardView.setOnClickListener(v -> {
                Dialog dialog = new Dialog(this);
                // Set dialog window background to transparent
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                ImageView dialogImage = new ImageView(this);
                dialogImage.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                ));
                dialogImage.setAdjustViewBounds(true);
                dialogImage.setScaleType(ImageView.ScaleType.FIT_CENTER);

                Glide.with(this)
                        .load(profilePicUrl)
                        .placeholder(R.drawable.ic_badge)
                        .error(R.drawable.ic_badge)
                        .into(dialogImage);

                dialog.setContentView(dialogImage);

                // Set the dialog window size explicitly
                Window window = dialog.getWindow();
                if (window != null) {
                    WindowManager.LayoutParams params = window.getAttributes();
                    params.width = WindowManager.LayoutParams.MATCH_PARENT;
                    params.height = WindowManager.LayoutParams.WRAP_CONTENT;
                    window.setAttributes(params);
                }

                dialog.show();
            });

            // Add CardView to parent layout
            llCertificates.addView(cardView);
        }


    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

}


