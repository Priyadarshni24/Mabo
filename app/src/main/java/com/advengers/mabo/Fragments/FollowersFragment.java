package com.advengers.mabo.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.advengers.mabo.R;
import com.advengers.mabo.databinding.FragmentsFollowersBinding;

public class FollowersFragment extends Fragment {
    FragmentsFollowersBinding binding;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragments_followers, container, false);

        return binding.getRoot();
    }
}
