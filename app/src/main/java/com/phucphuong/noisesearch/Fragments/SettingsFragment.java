package com.phucphuong.noisesearch.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.phucphuong.noisesearch.Activities.FileManager;
import com.phucphuong.noisesearch.R;
import com.phucphuong.noisesearch.Utilities.CalibrationWindow;
import com.phucphuong.noisesearch.Utilities.Login;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {


    public SettingsFragment() {
        // Required empty public constructor
    }
//    TODO: implement the calibration using AsyncTask

    ImageButton btn_settings, btn_info;
    TextView tv_values, tv_decibel;
    View settingsView, settingWindow, infoWindow, calibrationWindow, loginWindow;
    AlertDialog parentDialog, calibrationDialog, loginDialog;

    float calirationValue;
    SharedPreferences sharedPref;
    boolean speedMode, isMeasuring;

    CalibrationWindow calibrationClass;

    Typeface custom_font;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        settingsView = inflater.inflate(R.layout.fragment_settings, container, true);

        btn_settings = (ImageButton) settingsView.findViewById(R.id.btn_settings);
        btn_info = (ImageButton)settingsView.findViewById(R.id.btn_info);
        tv_values = (TextView)settingsView.findViewById(R.id.tv_values);
        tv_decibel = (TextView)settingsView.findViewById(R.id.tv_decibel);

        btn_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                settingWindow = inflater.inflate(R.layout.setting_window, container, true);
                calibrationWindow = inflater.inflate(R.layout.fragment_calibration, container, true);
                loginWindow = inflater.inflate(R.layout.login_window, container, true);
                showAlertDialog(settingWindow);

            }
        });

        isMeasuring = false;

        btn_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoWindow = inflater.inflate(R.layout.info_window, container, true);
                showAlertDialog(infoWindow);

            }
        });

        return settingsView;
    }

    public void showAlertDialog(final View mview){
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
//            btn_calibration.setTypeface(custom_font);
            btn_calibration.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    TODO : show the calibration fragment(done)
                    showCalibrationWindow(calibrationWindow);
                }
            });


            Button btn_filemanager = (Button)mview.findViewById(R.id.btn_fileManager);
//            btn_filemanager.setTypeface(custom_font);
            btn_filemanager.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    TODO: show the filemanager fragment  here (done)
                    showFileManagerDialog();
                }
            });


            final Button btn_login = (Button)mview.findViewById(R.id.btn_login);
            btn_login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    TODO: show the login form(done)
                    Log.e("button text", btn_login.getText().toString());
                    if (btn_login.getText().toString().equals("LOG IN")){
                        showLoginForm(loginWindow, mview);
                    }else {
                        Toast.makeText(mview.getContext(), "You have logout to the server!", Toast.LENGTH_SHORT).show();
                        btn_login.setText("LOG IN");
                    }
                }
            });


//            TODO: set the states of switch compat and login button
            //read pref file
            //set the states



            //TODO: disable calibration button while measuring(done)
            if (isMeasuring){
                btn_calibration.setEnabled(false);
                btn_calibration.setAlpha(0.5f);
            }else {
                btn_calibration.setEnabled(true);
                btn_calibration.setAlpha(1f);
            }
        }
    }

    private void showCalibrationWindow(View view){
        AlertDialog.Builder builder_cal = new AlertDialog.Builder(getActivity());
        builder_cal.setView(view);
        calibrationDialog = builder_cal.create();
        calibrationDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        calibrationDialog.setCanceledOnTouchOutside(false);

        //initial calibration window
        readPrefCal();
        calibrationClass = new CalibrationWindow(view, calirationValue, speedMode);
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

    public void setValuesText(String text, String text2){
        tv_values.setText(text);
        tv_decibel.setText(text2);
    }
    public void setStateOfSettingsButtons(boolean state){
//        btn_settings.setEnabled(state);
        isMeasuring = state;
    }

    public void backButtonHandle(){
        calibrationClass.terminateThread();
        calibrationDialog.dismiss();
        if (calibrationWindow.getParent() != null){
            ((ViewGroup) calibrationWindow.getParent()).removeView(calibrationWindow);
        }
        writePrefCal();
    }

    //for calibration
    public void writePrefCal(){
        sharedPref = getActivity().getSharedPreferences("calibration",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putFloat("calValue", calibrationClass.calibrationValue);
        editor.putBoolean("speedMode", calibrationClass.speedMode);
        editor.apply();
    }

    public void readPrefCal(){
        sharedPref = getActivity().getSharedPreferences("calibration", Context.MODE_PRIVATE);
        this.calirationValue = sharedPref.getFloat("calValue", 0f);
        this.speedMode = sharedPref.getBoolean("speedMode", false);
    }




    //for filemanager

    public void showFileManagerDialog(){
        Intent intent = new Intent(getContext(), FileManager.class);
        startActivity(intent);
    }


    //for login

    private void showLoginForm(final View view, final View parentView){
        AlertDialog.Builder builder_cal = new AlertDialog.Builder(getActivity());
        builder_cal.setView(view);

        loginDialog = builder_cal.create();
        loginDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        loginDialog.setCanceledOnTouchOutside(false);


        //get the views elements

        final EditText ed_username = (EditText)view.findViewById(R.id.ed_username);
        final EditText ed_password = (EditText)view.findViewById(R.id.ed_password);

        ed_password.setText("");
        Button btn_sign_in = (Button)view.findViewById(R.id.btn_sign_in);

        //TODO: post login form to server

        btn_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Editable username, password;
                username = ed_username.getText();
                password = ed_password.getText();

                final Login login = new Login(view, parentView, loginDialog, String.valueOf(username), String.valueOf(password));

                login.loginToServer();
            }
        });

        // handle system's back button
        loginDialog.setOnKeyListener(new AlertDialog.OnKeyListener(){
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK){
                    loginDialog.dismiss();
                    ed_password.setText("");
                    if (loginWindow.getParent() != null){
                        ((ViewGroup) loginWindow.getParent()).removeView(loginWindow);
                    }
                }
                return true;
            }
        });


        //handle touching outside the dialog
        loginDialog.setOnCancelListener(
                new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        dialog.dismiss();
                        ed_password.setText("");
                        if (loginWindow.getParent() != null){
                            ((ViewGroup) loginWindow.getParent()).removeView(loginWindow);
                        }
                        //When you touch outside of dialog bounds,
                        //the dialog gets canceled and this method executes.
                    }
                }
        );
        loginDialog.show();
    }
}
