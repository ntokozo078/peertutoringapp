package com.niquewrld.tutorspace.chat.fragments.users;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.niquewrld.tutorspace.R;
import com.niquewrld.tutorspace.chat.activity.ComponentLaunchActivity;
import com.niquewrld.tutorspace.chat.AppUtils;


public class UsersWithMessagesFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_users_with_messages, container, false);
    }
}