package com.niquewrld.tutorspace.chat.fragments.groups;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.cometchat.chatuikit.addmembers.CometChatAddMembers;
import com.niquewrld.tutorspace.R;
import com.niquewrld.tutorspace.chat.AppUtils;

public class AddMemberFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_member, container, false);

        CometChatAddMembers addMembers = view.findViewById(R.id.add_members);
        addMembers.setGroup(AppUtils.getDefaultGroup());
        return view;
    }
}