package com.niquewrld.tutorspace;

import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.niquewrld.tutorspace.chat.fragments.conversations.ConversationsWithMessagesFragment;

public class ChatActivity extends AppCompatActivity {
    private LinearLayout parentView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_component_launch);

        parentView = findViewById(R.id.container);
        loadFragment(new ConversationsWithMessagesFragment());
    }

    private void loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
        }
    }

}
