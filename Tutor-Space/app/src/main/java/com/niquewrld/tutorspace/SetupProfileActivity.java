package com.niquewrld.tutorspace;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.niquewrld.tutorspace.adapters.TagAdapter;
import com.niquewrld.tutorspace.models.Data;

import java.util.*;

public class SetupProfileActivity extends AppCompatActivity {

    private EditText searchTags;
    private RecyclerView recyclerTags;
    private TextView tagsCount;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private MaterialButton btnNext;
    private ChipGroup selectedTagsGroup;
    private TagAdapter tagAdapter;
    private final int MAX_TAGS = 6;
    // Track all selected tags across categories
    private final Set<String> selectedTags = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_profile);

        mAuth = FirebaseAuth.getInstance();

        user = mAuth.getCurrentUser();

        searchTags = findViewById(R.id.searchTags);
        recyclerTags = findViewById(R.id.recyclerTags);
        tagsCount = findViewById(R.id.tagsCount);
        btnNext = findViewById(R.id.btnNext);
        selectedTagsGroup = findViewById(R.id.selectedTagsGroup);

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, Data.MAIN_CATEGORIES
        );
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        recyclerTags.setLayoutManager(new LinearLayoutManager(this));

        searchTags.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (tagAdapter != null) {
                    tagAdapter.filter(s.toString());
                }
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        btnNext.setOnClickListener(v -> {
            // Replace with actual user ID logic (e.g., from FirebaseAuth)
            String userId = user.getUid();

            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            Map<String, Object> data = new HashMap<>();
            data.put("interests", new ArrayList<>(selectedTags));

            firestore.collection("users")
                    .document(userId)
                    .set(data, SetOptions.merge()) // merge to avoid overwriting other user data
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Modules saved successfully!", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Error saving interests: " + e.getMessage(), Toast.LENGTH_LONG).show()
                    );
        });

        // Initialize with first category
        listAllTags();
    }

    private void listAllTags() {
        // Assume subCategoriesMap contains all tags grouped by category
        List<String> allTags = new ArrayList<>();
        for (String category : Data.MODULES) {
            String[] tags = Data.MODULES;
            if (tags != null) {
                allTags.addAll(Arrays.asList(tags));
            }
        }

        // Remove duplicates, if any
        allTags = new ArrayList<>(new HashSet<>(allTags));

        // Set up the TagAdapter with all tags
        tagAdapter = new TagAdapter(allTags, selectedTags, MAX_TAGS, count -> {
            tagsCount.setText(count + "/6 selected");
            updateSelectedTagsChipGroup();
            if (count > 0) {
                btnNext.setVisibility(View.VISIBLE);
                btnNext.setEnabled(true);
            } else {
                btnNext.setVisibility(View.GONE);
                btnNext.setEnabled(false);
            }
        });
        recyclerTags.setAdapter(tagAdapter);

        // Set initial state for Next button and chips
        updateSelectedTagsChipGroup();
        if (selectedTags.size() == MAX_TAGS) {
            btnNext.setVisibility(View.VISIBLE);
            btnNext.setEnabled(true);
        } else {
            btnNext.setVisibility(View.GONE);
            btnNext.setEnabled(false);
        }
        tagsCount.setText(selectedTags.size() + "/6 selected");
    }

    private void updateSelectedTagsChipGroup() {
        selectedTagsGroup.removeAllViews();
        for (String tag : selectedTags) {
            Chip chip = new Chip(this);
            chip.setText(tag);
            chip.setCloseIconVisible(true);
            chip.setOnCloseIconClickListener(v -> {
                selectedTags.remove(tag);
                tagAdapter.notifyDataSetChanged();
                updateSelectedTagsChipGroup();
                tagsCount.setText(selectedTags.size() + "/6 selected");
                if (selectedTags.size() == MAX_TAGS) {
                    btnNext.setVisibility(View.VISIBLE);
                    btnNext.setEnabled(true);
                } else {
                    btnNext.setVisibility(View.GONE);
                    btnNext.setEnabled(false);
                }
            });
            selectedTagsGroup.addView(chip);
        }
    }
}