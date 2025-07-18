package com.niquewrld.tutorspace.chat.fragments.groups;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.cometchat.chatuikit.details.CometChatDetails;
import com.niquewrld.tutorspace.R;
import com.niquewrld.tutorspace.chat.AppUtils;

public class GroupDetailsFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_groups_list_data_item, container, false);

        CometChatDetails details = view.findViewById(R.id.group_details);
        details.setGroup(AppUtils.getDefaultGroup());
        return view;
    }
}