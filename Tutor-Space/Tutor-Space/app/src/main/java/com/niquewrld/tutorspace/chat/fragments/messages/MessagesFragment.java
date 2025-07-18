package com.niquewrld.tutorspace.chat.fragments.messages;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.cometchat.chatuikit.messages.CometChatMessages;
import com.niquewrld.tutorspace.R;
import com.niquewrld.tutorspace.chat.AppUtils;


public class MessagesFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_messages, container, false);

        CometChatMessages messages = view.findViewById(R.id.messages);
        messages.setUser(AppUtils.getDefaultUser());

        return view;
    }
}