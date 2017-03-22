package com.phucphuong.noisesearch.Fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.phucphuong.noisesearch.R;
import com.phucphuong.noisesearch.Utilities.CalibrationWindow;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {


    public SettingsFragment() {
        // Required empty public constructor
    }
//    TODO: implement the calibration using AsyncTask

    ImageButton btn_settings, btn_info;
    TextView tv_values;
    View settingsView, settingWindow, infoWindow, calibrationWindow;
    AlertDialog parentDialog, calibrationDialog;

    float calirationValue;
    SharedPreferences sharedPref;

    CalibrationWindow calibrationClass;

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


        //initial calibration window
        calirationValue = readPref();
        calibrationClass = new CalibrationWindow(view, calirationValue);
        calibrationClass.getViewElements();


        // handle system's back button
        calibrationDialog.setOnKeyListener(new AlertDialog.OnKeyListener(){
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK){
                    backButtonHandle();
                }
                return true;
            }
        });

        // handle dialog's back button
        ImageButton btn_back = (ImageButton)view.findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backButtonHandle();
            }
        });

        //show dialog when everything's done
        calibrationDialog.show();


    }


    public void setValuesText(String text){
        tv_values.setText(text);
    }
    public void setStateOfSettingsButtons(boolean state){
        btn_settings.setEnabled(state);
    }

    public void backButtonHandle(){
        calibrationClass.terminateThread();
        calibrationDialog.dismiss();
        if (calibrationWindow.getParent() != null){
            ((ViewGroup) calibrationWindow.getParent()).removeView(calibrationWindow);
        }
        writePref();
    }

    //for calibration
    public void writePref(){
        sharedPref = getActivity().getSharedPreferences("calibration_value",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putFloat("calValue", calibrationClass.calibrationValue);
        editor.commit();
    }

    public float readPref(){
        sharedPref = getActivity().getSharedPreferences("calibration_value", Context.MODE_PRIVATE);
        return sharedPref.getFloat("calValue", 0f);
    }
}
