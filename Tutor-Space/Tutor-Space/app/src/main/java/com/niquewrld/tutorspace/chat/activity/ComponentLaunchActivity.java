package com.niquewrld.tutorspace.chat.activity;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.cometchat.chatuikit.shared.resources.utils.Utils;
import com.niquewrld.tutorspace.chat.AppUtils;
import com.niquewrld.tutorspace.R;
import com.niquewrld.tutorspace.chat.fragments.calls.CallButtonFragment;
import com.niquewrld.tutorspace.chat.fragments.calls.CallLogDetailsFragment;
import com.niquewrld.tutorspace.chat.fragments.calls.CallLogHistoryFragment;
import com.niquewrld.tutorspace.chat.fragments.calls.CallLogParticipantsFragment;
import com.niquewrld.tutorspace.chat.fragments.calls.CallLogRecordingFragment;
import com.niquewrld.tutorspace.chat.fragments.calls.CallLogWithDetailsFragment;
import com.niquewrld.tutorspace.chat.fragments.calls.CallLogsFragment;
import com.niquewrld.tutorspace.chat.fragments.conversations.ConversationsWithMessagesFragment;
import com.niquewrld.tutorspace.chat.fragments.groups.AddMemberFragment;
import com.niquewrld.tutorspace.chat.fragments.groups.BannedMembersFragment;
import com.niquewrld.tutorspace.chat.fragments.groups.CreateGroupFragment;
import com.niquewrld.tutorspace.chat.fragments.groups.GroupDetailsFragment;
import com.niquewrld.tutorspace.chat.fragments.groups.GroupMembersFragment;
import com.niquewrld.tutorspace.chat.fragments.groups.GroupsFragment;
import com.niquewrld.tutorspace.chat.fragments.groups.GroupsWithMessagesFragment;
import com.niquewrld.tutorspace.chat.fragments.groups.JoinProtectedGroupFragment;
import com.niquewrld.tutorspace.chat.fragments.groups.TransferOwnershipFragment;
import com.niquewrld.tutorspace.chat.fragments.messages.MessageComposerFragment;
import com.niquewrld.tutorspace.chat.fragments.messages.MessageHeaderFragment;
import com.niquewrld.tutorspace.chat.fragments.messages.MessageInformationFragment;
import com.niquewrld.tutorspace.chat.fragments.messages.MessageListFragment;
import com.niquewrld.tutorspace.chat.fragments.messages.MessagesFragment;
import com.niquewrld.tutorspace.chat.fragments.shared.resources.LocalizeFragment;
import com.niquewrld.tutorspace.chat.fragments.shared.resources.SoundManagerFragment;
import com.niquewrld.tutorspace.chat.fragments.shared.resources.ThemeFragment;
import com.niquewrld.tutorspace.chat.fragments.shared.views.AudioBubbleFragment;
import com.niquewrld.tutorspace.chat.fragments.shared.views.AvatarFragment;
import com.niquewrld.tutorspace.chat.fragments.shared.views.CardBubbleFragment;
import com.niquewrld.tutorspace.chat.fragments.shared.views.FileBubbleFragment;
import com.niquewrld.tutorspace.chat.fragments.shared.views.FormBubbleFragment;
import com.niquewrld.tutorspace.chat.fragments.shared.views.ImageBubbleFragment;
import com.niquewrld.tutorspace.chat.fragments.shared.views.ListItemFragment;
import com.niquewrld.tutorspace.chat.fragments.shared.views.MediaRecorderFragment;
import com.niquewrld.tutorspace.chat.fragments.shared.views.MessageReceiptFragment;
import com.niquewrld.tutorspace.chat.fragments.shared.views.SchedulerBubbleFragment;
import com.niquewrld.tutorspace.chat.fragments.shared.views.StatusIndicatorFragment;
import com.niquewrld.tutorspace.chat.fragments.shared.views.TextBubbleFragment;
import com.niquewrld.tutorspace.chat.fragments.shared.views.VideoBubbleFragment;
import com.niquewrld.tutorspace.chat.fragments.users.UsersDetailsFragment;
import com.niquewrld.tutorspace.chat.fragments.users.UsersFragment;
import com.niquewrld.tutorspace.chat.fragments.users.UsersWithMessagesFragment;

public class ComponentLaunchActivity extends AppCompatActivity {
    private LinearLayout parentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_component_launch);
        int id = getIntent().getIntExtra("component", 0);
        parentView = findViewById(R.id.container);
        setUpUI();
        if (id == R.id.conversationWithMessages) {
            loadFragment(new ConversationsWithMessagesFragment());
        } else if (id == R.id.userWithMessages) {
            loadFragment(new UsersWithMessagesFragment());
        } else if (id == R.id.users) {
            loadFragment(new UsersFragment());
        } else if (id == R.id.user_details) {
            loadFragment(new UsersDetailsFragment());
        } else if (id == R.id.groupWithMessages) {
            loadFragment(new GroupsWithMessagesFragment());
        } else if (id == R.id.call_button) {
            loadFragment(new CallButtonFragment());
        } else if (id == R.id.groups) {
            loadFragment(new GroupsFragment());
        } else if (id == R.id.create_group) {
            loadFragment(new CreateGroupFragment());
        } else if (id == R.id.join_protected_group) {
            loadFragment(new JoinProtectedGroupFragment());
        } else if (id == R.id.group_member) {
            loadFragment(new GroupMembersFragment());
        } else if (id == R.id.add_member) {
            loadFragment(new AddMemberFragment());
        } else if (id == R.id.transfer_ownership) {
            loadFragment(new TransferOwnershipFragment());
        } else if (id == R.id.banned_members) {
            loadFragment(new BannedMembersFragment());
        } else if (id == R.id.group_details) {
            loadFragment(new GroupDetailsFragment());
        } else if (id == R.id.messages) {
            loadFragment(new MessagesFragment());
        } else if (id == R.id.messageList) {
            loadFragment(new MessageListFragment());
        } else if (id == R.id.messageHeader) {
            loadFragment(new MessageHeaderFragment());
        } else if (id == R.id.messageComposer) {
            loadFragment(new MessageComposerFragment());
        } else if (id == R.id.avatar) {
            loadFragment(new AvatarFragment());
        } else if (id == R.id.messageReceipt) {
            loadFragment(new MessageReceiptFragment());
        } else if (id == R.id.statusIndicator) {
            loadFragment(new StatusIndicatorFragment());
        } else if (id == R.id.soundManager) {
            loadFragment(new SoundManagerFragment());
        } else if (id == R.id.theme) {
            loadFragment(new ThemeFragment());
        } else if (id == R.id.localize) {
            loadFragment(new LocalizeFragment());
        } else if (id == R.id.list_item) {
            loadFragment(new ListItemFragment());
        } else if (id == R.id.text_bubble) {
            loadFragment(new TextBubbleFragment());
        } else if (id == R.id.image_bubble) {
            loadFragment(new ImageBubbleFragment());
        } else if (id == R.id.video_bubble) {
            loadFragment(new VideoBubbleFragment());
        } else if (id == R.id.audio_bubble) {
            loadFragment(new AudioBubbleFragment());
        } else if (id == R.id.files_bubble) {
            loadFragment(new FileBubbleFragment());
        } else if (id == R.id.form_bubble) {
            loadFragment(new FormBubbleFragment());
        } else if (id == R.id.card_bubble) {
            loadFragment(new CardBubbleFragment());
        } else if (id == R.id.scheduler_bubble) {
            loadFragment(new SchedulerBubbleFragment());
        } else if (id == R.id.media_recorder) {
            loadFragment(new MediaRecorderFragment());
        } else if (id == R.id.messageInformation) {
            loadFragment(new MessageInformationFragment());
        } else if (id == R.id.call_logs) {
            loadFragment(new CallLogsFragment());
        } else if (id == R.id.call_logs_details) {
            loadFragment(new CallLogDetailsFragment());
        } else if (id == R.id.call_logs_with_details) {
            loadFragment(new CallLogWithDetailsFragment());
        } else if (id == R.id.call_log_participants) {
            loadFragment(new CallLogParticipantsFragment());
        } else if (id == R.id.call_log_recording) {
            loadFragment(new CallLogRecordingFragment());
        } else if (id == R.id.call_log_history) {
            loadFragment(new CallLogHistoryFragment());
        }
    }

    private void setUpUI() {
        if (AppUtils.isNightMode(this)) {
            Utils.setStatusBarColor(this, ContextCompat.getColor(this, R.color.colorSecondaryVariant));
            parentView.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorSecondaryVariant)));
        } else {
            Utils.setStatusBarColor(this, getResources().getColor(R.color.colorSecondaryVariant));
            parentView.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorSecondaryVariant)));
        }
    }

    private void loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
        }
    }

}