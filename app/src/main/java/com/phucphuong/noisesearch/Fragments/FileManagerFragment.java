package com.phucphuong.noisesearch.Fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.phucphuong.noisesearch.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */

// TODO: 3/24/17 check this class link in readlist 
public class FileManagerFragment extends DialogFragment {


    private TabLayout tabLayout;
    private ViewPager viewPager;

    public FileManagerFragment() {
        // Required empty public constructor
    }

    public static FileManagerFragment newInstance() {
        FileManagerFragment frag = new FileManagerFragment();
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_file_manager, container, true);

        return view;

    }




}
