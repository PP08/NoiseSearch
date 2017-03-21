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
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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
//    TODO: implement the calibration using AsyncTask

    ImageButton btn_settings, btn_info;
    TextView tv_values;
    View settingsView, settingWindow, infoWindow, calibrationWindow;

    AlertDialog parentDialog, calibrationDialog;

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

                settingWindow = inflater.inflate(R.layout.setting_window, container, true);
                calibrationWindow = inflater.inflate(R.layout.fragment_calibration, container, true);
                showAlertDialog(settingWindow);

            }
        });

        btn_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoWindow = inflater.inflate(R.layout.info_window, container, true);
                showAlertDialog(infoWindow);

            }
        });

        return settingsView;
    }

    public void showAlertDialog(View mview){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(mview);
        parentDialog = builder.create();

        parentDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        parentDialog.show();
        // dialog.setCanceledOnTouchOutside(false);
        ImageButton btn_close = (ImageButton) mview.findViewById(R.id.btn_close);
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parentDialog.dismiss();
            }
        });

        //calibrate btn
        if (mview == settingWindow){
            Button btn_calibration = (Button)mview.findViewById(R.id.btn_calibration);
            btn_calibration.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    TODO : show the fragment calibration
                    //parentDialog.dismiss();
                    showCalibrationWindow(calibrationWindow);
                }
            });

        }
    }

    private void showCalibrationWindow(View view){
        AlertDialog.Builder builder_cal = new AlertDialog.Builder(getActivity());
        builder_cal.setView(view);
        calibrationDialog = builder_cal.create();
        calibrationDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        calibrationDialog.setCanceledOnTouchOutside(false);
        calibrationDialog.show();
        CalibrationWindow calibrationClass = new CalibrationWindow(view);
        calibrationClass.getViewElements();

        ImageButton btn_back = (ImageButton)view.findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(view.getContext(), "ok", Toast.LENGTH_SHORT).show();
                calibrationDialog.dismiss();
                ((ViewGroup) calibrationWindow.getParent()).removeView(calibrationWindow);
                //calibrationDialog.
                //showAlertDialog(settingWindow);
            }
        });

        //calibrationWindow.getViewElements();

    }
    public void setValuesText(String text){
        tv_values.setText(text);
    }
    public void setStateOfSettingsButtons(boolean state){
        btn_settings.setEnabled(state);
    }

}
