package com.phucphuong.noisesearch.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CompoundButtonCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.phucphuong.noisesearch.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class MeterFragment extends Fragment {


    public MeterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View function1View = inflater.inflate(R.layout.fragment_meter, container, true);
        ToggleButton btn_start_stop = (ToggleButton)function1View.findViewById(R.id.btn_start_stop);

        //fragment settings

        final SettingsFragment settingsFragment = (SettingsFragment) getFragmentManager().findFragmentById(R.id.settingsFragment);


        btn_start_stop.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    Toast.makeText(getActivity(), "starting...", Toast.LENGTH_SHORT).show();

                    //disable settings button
                    settingsFragment.setStateOfSettingsButtons(false);



                }else {

                    Toast.makeText(getActivity(), "stopped...", Toast.LENGTH_SHORT).show();

                    //enable settings button
                    settingsFragment.setStateOfSettingsButtons(true);


                }
            }
        });

        return function1View;
    }

}
