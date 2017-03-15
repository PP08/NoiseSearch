package com.phucphuong.noisesearch.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.phucphuong.noisesearch.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {


    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View settingsView = inflater.inflate(R.layout.fragment_settings, container, true);

        ImageButton btn_settings = (ImageButton) settingsView.findViewById(R.id.btn_settings);
        ImageButton btn_info = (ImageButton)settingsView.findViewById(R.id.btn_info);

        btn_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getActivity(), "You've clicked settings button", Toast.LENGTH_SHORT).show();

            }
        });

        btn_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "You've clicked info button", Toast.LENGTH_SHORT).show();
            }
        });



        return settingsView;
    }

}
