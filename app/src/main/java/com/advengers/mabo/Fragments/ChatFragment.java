package com.advengers.mabo.Fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
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

import com.advengers.mabo.Activity.SearchActivity;
import com.advengers.mabo.R;
import com.advengers.mabo.databinding.FragmentChatBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import screen.CometChatConversationListScreen;
import viewmodel.UserModel;

import static constant.StringContract.IntentStrings.ID;
import static constant.StringContract.IntentStrings.MYUID;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends MyFragment {

    FragmentChatBinding binding;
    FirebaseDatabase database;
    DatabaseReference myRef;
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
        // Set Tabs inside Toolbar
        binding.resultTabs.setupWithViewPager(binding.viewpager);
        return binding.getRoot();
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
        adapter.addFragment(new FriendsFragment(), getString(R.string.str_friends));
        adapter.addFragment(fragment, getString(R.string.str_followers));
       // adapter.addFragment(new RecentsFragment(), getString(R.string.str_followers));
    //    adapter.addFragment(new GroupListFragment(),getString(R.string.str_groups));
        viewPager.setAdapter(adapter);
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
}
