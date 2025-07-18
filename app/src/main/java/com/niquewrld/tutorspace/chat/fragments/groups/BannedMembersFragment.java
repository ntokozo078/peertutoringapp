package com.niquewrld.tutorspace.chat.fragments.groups;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.cometchat.chatuikit.bannedmembers.CometChatBannedMembers;
import com.niquewrld.tutorspace.R;
import com.niquewrld.tutorspace.chat.AppUtils;


public class BannedMembersFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_banned_members, container, false);

        CometChatBannedMembers bannedMembers = view.findViewById(R.id.banned_members);
        bannedMembers.setGroup(AppUtils.getDefaultGroup());
        return view;
    }
}