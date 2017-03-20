package com.phucphuong.noisesearch.Fragments;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.phucphuong.noisesearch.R;
import com.phucphuong.noisesearch.Utilities.CalibrationHelper;

import java.util.zip.Inflater;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {


    public SettingsFragment() {
        // Required empty public constructor
    }

    double calValue = 0;

    ImageButton btn_settings, btn_info;
    TextView tv_values;
    View settingsView;
    AlertDialog diaglogSettings;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        settingsView = inflater.inflate(R.layout.fragment_settings, container, true);

        btn_settings = (ImageButton) settingsView.findViewById(R.id.btn_settings);
        btn_info = (ImageButton)settingsView.findViewById(R.id.btn_info);
        tv_values = (TextView)settingsView.findViewById(R.id.tv_values);
        btn_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View mview = inflater.inflate(R.layout.setting_window, container, true);
                showAlertDialog(mview);

            }
        });

        btn_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View mview = inflater.inflate(R.layout.info_window, container, true);
                showAlertDialog(mview);

            }
        });

        return settingsView;
    }

    private void showAlertDialog(View mview){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(mview);
        diaglogSettings = builder.create();

        diaglogSettings.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        diaglogSettings.show();

        // dialog.setCanceledOnTouchOutside(false);
        ImageButton btn_close = (ImageButton) mview.findViewById(R.id.btn_close);
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                diaglogSettings.dismiss();
            }
        });

    }

    public void setValuesText(String text){
        tv_values.setText(text);
    }
    public void setStateOfSettingsButtons(boolean state){
        btn_settings.setEnabled(state);
    }
}
