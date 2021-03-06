package com.phucphuong.noisesearch.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
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
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View settingsView = inflater.inflate(R.layout.fragment_settings, container, true);

        ImageButton btn_settings = (ImageButton) settingsView.findViewById(R.id.btn_settings);
        ImageButton btn_info = (ImageButton)settingsView.findViewById(R.id.btn_info);

        btn_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View mview = inflater.inflate(R.layout.setting_window, container, true);
                showAlertDialog(inflater, container, mview);

            }
        });

        btn_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View mview = inflater.inflate(R.layout.info_window, container, true);
                showAlertDialog(inflater, container, mview);
            }
        });



        return settingsView;
    }

    private void showAlertDialog(final LayoutInflater inflater, final ViewGroup container, View mview){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //View mview = inflater.inflate(R.layout.setting_window, container, true);

        builder.setView(mview);
        final AlertDialog dialog = builder.create();
        //1
        // dialog.setCanceledOnTouchOutside(false);
        dialog.show();
//        TextView title = (TextView)mview.findViewById(R.id.titleSettings);
//        Button btn_calinrate = (Button) mview.findViewById(R.id.btn_calibrate);
//        Button btn_filemanager = (Button) mview.findViewById(R.id.btn_file);
        ImageButton btn_close = (ImageButton) mview.findViewById(R.id.btn_close);

        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

}
