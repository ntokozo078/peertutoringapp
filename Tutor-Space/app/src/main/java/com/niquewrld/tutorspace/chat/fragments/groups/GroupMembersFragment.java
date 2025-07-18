package com.niquewrld.tutorspace.chat.fragments.groups;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.cometchat.chatuikit.groupmembers.CometChatGroupMembers;
import com.niquewrld.tutorspace.R;
import com.niquewrld.tutorspace.chat.AppUtils;

public class GroupMembersFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group_members, container, false);
        CometChatGroupMembers groupMembers = view.findViewById(R.id.members);
        groupMembers.setGroup(AppUtils.getDefaultGroup());
        return view;
    }
}