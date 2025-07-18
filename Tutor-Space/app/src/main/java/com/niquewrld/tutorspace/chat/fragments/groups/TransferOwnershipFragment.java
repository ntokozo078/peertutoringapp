package com.niquewrld.tutorspace.chat.fragments.groups;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.cometchat.chatuikit.transferownership.CometChatTransferOwnership;
import com.niquewrld.tutorspace.R;
import com.niquewrld.tutorspace.chat.AppUtils;

public class TransferOwnershipFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_transfer_ownership, container, false);

        CometChatTransferOwnership transferOwnership = view.findViewById(R.id.transfer_ownership);
        transferOwnership.setGroup(AppUtils.getDefaultGroup());
        return view;

    }
}