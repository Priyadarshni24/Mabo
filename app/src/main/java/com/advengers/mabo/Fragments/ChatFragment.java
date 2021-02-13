package com.advengers.mabo.Fragments;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.emoji.text.EmojiCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.advengers.mabo.Activity.SearchActivity;
import com.advengers.mabo.R;
import com.advengers.mabo.databinding.FragmentChatBinding;
import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.Conversation;
import com.cometchat.pro.models.Group;
import com.cometchat.pro.models.User;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import constant.StringContract;
import listeners.CometChatCallListener;
import listeners.CustomAlertDialogHelper;
import listeners.OnAlertDialogButtonClickListener;
import listeners.OnItemClickListener;
import screen.CometChatConversationListScreen;
import screen.CometChatGroupListScreen;
import screen.CometChatUserListScreen;
import screen.messagelist.CometChatMessageListActivity;
import screen.unified.CometChatUnified;
import utils.Utils;
import viewmodel.UserModel;

import static android.content.ContentValues.TAG;
import static constant.StringContract.IntentStrings.ID;
import static constant.StringContract.IntentStrings.MYUID;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends MyFragment implements OnAlertDialogButtonClickListener
{

    FragmentChatBinding binding;
    FirebaseDatabase database;
    DatabaseReference myRef;
    private Group group;
    private String groupPassword;
    private ProgressDialog progressDialog;
    String myuid;
    @Override
    public String getTagManager() {
        return null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat, container, false);
        binding.mToolbar.inflateMenu(R.menu.menu_search);
        binding.mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.action_search:
                        startActivity(new Intent(getActivity(), SearchActivity.class));
                      break;
                    case R.id.action_group:
                    //    startActivity(new Intent(getActivity(), CreateGroupActivity.class));
                        break;
                }
                return true;
            }
        });
        setupViewPager(binding.viewpager);
        if (!CometChatCallListener.isInitialized)
            CometChatCallListener.addCallListener(TAG,getContext());

      /*  EmojiCompat.Config config = new BundledEmojiCompatConfig(getApplicationContext());
        EmojiCompat.init(config);*/
        initViewComponent();
        // It performs action on click of user item in CometChatUserListScreen.
        setUserClickListener();
        if (getArguments() != null) {
            myuid = getArguments().getString(MYUID);
        }


        //It performs action on click of group item in CometChatGroupListScreen.
        //It checks whether the logged-In user is already a joined a group or not and based on it perform actions.
        setGroupClickListener();

        //It performs action on click of conversation item in CometChatConversationListScreen
        //Based on conversation item type it will perform the actions like open message screen for user and groups..
        setConversationClickListener();

        // Set Tabs inside Toolbar
        binding.resultTabs.setupWithViewPager(binding.viewpager);
        return binding.getRoot();
    }
    private void setUserClickListener() {
        CometChatUserListScreen.setItemClickListener(new OnItemClickListener<User>() {
            @Override
            public void OnItemClick(User user, int position) {
                startUserIntent(user);
            }
        });
    }
    private void initViewComponent() {

        if (!Utils.hasPermissions(getContext(), new String[]{Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE})) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO,
                                Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        StringContract.RequestCode.RECORD);
            }
        }

    }

    private void setConversationClickListener() {
        CometChatConversationListScreen.setItemClickListener(new OnItemClickListener<Conversation>() {
            @Override
            public void OnItemClick(Conversation conversation, int position) {
                if (conversation.getConversationType().equals(CometChatConstants.CONVERSATION_TYPE_GROUP))
                    startGroupIntent(((Group) conversation.getConversationWith()));
                else
                    startUserIntent(((User) conversation.getConversationWith()));
            }
        });
    }

    private void setGroupClickListener() {
        CometChatGroupListScreen.setItemClickListener(new OnItemClickListener<Group>() {
            @Override
            public void OnItemClick(Group g, int position) {
                group = g;
                if (group.isJoined()) {
                    startGroupIntent(group);
                } else {
                    if (group.getGroupType().equals(CometChatConstants.GROUP_TYPE_PASSWORD)) {
                        View dialogview = getLayoutInflater().inflate(com.cometchat.pro.uikit.R.layout.cc_dialog, null);
                        TextView tvTitle = dialogview.findViewById(com.cometchat.pro.uikit.R.id.textViewDialogueTitle);
                        tvTitle.setText(String.format(getResources().getString(com.cometchat.pro.uikit.R.string.enter_password_to_join),group.getName()));
                        new CustomAlertDialogHelper(getContext(), getResources().getString(com.cometchat.pro.uikit.R.string.password), dialogview, getResources().getString(com.cometchat.pro.uikit.R.string.join),
                                "", getResources().getString(com.cometchat.pro.uikit.R.string.cancel),ChatFragment.this, 1, false);
                    } else if (group.getGroupType().equals(CometChatConstants.GROUP_TYPE_PUBLIC)) {
                   //     joinGroup(group);
                    }
                }
            }
        });
    }

    private void startUserIntent(User user) {
        Intent intent = new Intent(getActivity(), CometChatMessageListActivity.class);
        intent.putExtra(StringContract.IntentStrings.UID, user.getUid());
        intent.putExtra(MYUID, myuid);
        intent.putExtra(StringContract.IntentStrings.AVATAR, user.getAvatar());
        intent.putExtra(StringContract.IntentStrings.STATUS, user.getStatus());
        intent.putExtra(StringContract.IntentStrings.NAME, user.getName());
        intent.putExtra(StringContract.IntentStrings.TYPE, CometChatConstants.RECEIVER_TYPE_USER);
        startActivity(intent);
    }

    /**
     * Open Message Screen for group using <b>CometChatMessageListActivity.class</b>
     *
     * @param group
     * @see CometChatMessageListActivity
     */
    private void startGroupIntent(Group group) {

        Intent intent = new Intent(getActivity(), CometChatMessageListActivity.class);
        intent.putExtra(StringContract.IntentStrings.GUID, group.getGuid());
        intent.putExtra(MYUID, myuid);
        intent.putExtra(StringContract.IntentStrings.AVATAR, group.getIcon());
        intent.putExtra(StringContract.IntentStrings.GROUP_OWNER,group.getOwner());
        intent.putExtra(StringContract.IntentStrings.NAME, group.getName());
        intent.putExtra(StringContract.IntentStrings.GROUP_TYPE,group.getGroupType());
        intent.putExtra(StringContract.IntentStrings.TYPE, CometChatConstants.RECEIVER_TYPE_GROUP);
        intent.putExtra(StringContract.IntentStrings.MEMBER_COUNT,group.getMembersCount());
        intent.putExtra(StringContract.IntentStrings.GROUP_DESC,group.getDescription());
        intent.putExtra(StringContract.IntentStrings.GROUP_PASSWORD,group.getPassword());
        startActivity(intent);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_search, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    // Add Fragments to Tabs
    private void setupViewPager(ViewPager viewPager) {


     //   setUser();
        getUser();
        Log.e("Mabo","MYUID "+user.getRoomid());
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Mabo");

        UserModel usermodel = new UserModel(user.getRoomid(),user.getUsername(),user.getprofile_imagename(),
                user.getLatitude(),user.getLongitude(),"0","0");

        myRef.child("users").child(user.getRoomid()).setValue(usermodel);


        Fragment fragment = new CometChatConversationListScreen();
        Bundle bundle = new Bundle();
        bundle.putString(MYUID,user.getRoomid());
        fragment.setArguments(bundle);

        Adapter adapter = new Adapter(getChildFragmentManager());
        adapter.addFragment(new FriendsFragment(), getString(R.string.str_contacts));
        adapter.addFragment(fragment, getString(R.string.str_chats));
       // adapter.addFragment(new RecentsFragment(), getString(R.string.str_followers));
    //    adapter.addFragment(new GroupListFragment(),getString(R.string.str_groups));
        viewPager.setAdapter(adapter);
 }

    @Override
    public void onButtonClick(AlertDialog alertDialog, View v, int which, int popupId) {
        EditText groupPasswordInput = (EditText) v.findViewById(com.cometchat.pro.uikit.R.id.edittextDialogueInput);
        if (which == DialogInterface.BUTTON_NEGATIVE) { // Cancel
            alertDialog.dismiss();
        } else if (which == DialogInterface.BUTTON_POSITIVE) { // Join
            try {
                groupPassword = groupPasswordInput.getText().toString();
                if (groupPassword.length() == 0) {
                    groupPasswordInput.setText("");
                    groupPasswordInput.setError(getResources().getString(com.cometchat.pro.uikit.R.string.incorrect));

                } else {
                    try {
                        alertDialog.dismiss();
                        joinGroup(group);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
    /**
     * This methods joins the logged-In user in a group.
     *
     * @param group  The Group user will join.
     * @see Group
     * @see CometChat#joinGroup(String, String, String, CometChat.CallbackListener)
     *
     */
    private void joinGroup(Group group) {
        progressDialog = ProgressDialog.show(getContext(), "", getResources().getString(com.cometchat.pro.uikit.R.string.joining));
        progressDialog.setCancelable(false);
        CometChat.joinGroup(group.getGuid(), group.getGroupType(), groupPassword, new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group group) {
                if (progressDialog!=null)
                    progressDialog.dismiss();

                if(group!=null)
                    startGroupIntent(group);
            }

            @Override
            public void onError(CometChatException e) {
                if (progressDialog!=null)
                    progressDialog.dismiss();

                /*Snackbar.make(activityCometChatUnifiedBinding.bottomNavigation,getResources().getString(com.cometchat.pro.uikit.R.string.unabl_to_join_message)+e.getMessage(),
                        Snackbar.LENGTH_SHORT).show();
*/
            }
        });
    }
}
