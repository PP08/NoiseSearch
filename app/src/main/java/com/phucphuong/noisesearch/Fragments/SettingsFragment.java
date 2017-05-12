package com.phucphuong.noisesearch.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.phucphuong.noisesearch.Activities.AppInfo;
import com.phucphuong.noisesearch.Activities.FileManager;
import com.phucphuong.noisesearch.R;
import com.phucphuong.noisesearch.Utilities.AsyncTaskCalibration;
import com.phucphuong.noisesearch.Utilities.CalibrationWindow;
import com.phucphuong.noisesearch.Utilities.Login;
import com.phucphuong.noisesearch.Utilities.ReplaceFont;

import java.io.UnsupportedEncodingException;

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
    SharedPreferences sharedPrefCal, sharedPrefSettings;
    boolean speedMode, isMeasuring;

    boolean privateMode;
    String auth_token, user_name;
    CalibrationWindow calibrationClass;

    //switch private
    boolean isTouched = false;

    TextView sign_up_tv;


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
//                infoWindow = inflater.inflate(R.layout.info_window, container, true);
//                showAlertDialog(infoWindow);
                Intent intent = new Intent(getContext(), AppInfo.class);
                startActivity(intent);
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

            sign_up_tv = (TextView)mview.findViewById(R.id.sign_up_tv);
            sign_up_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = "https://noisesearch.herokuapp.com/signup/";
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
            });


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

            final SwitchCompat sw_private = (SwitchCompat)mview.findViewById(R.id.sw_private);

            final Button btn_login = (Button)mview.findViewById(R.id.btn_login);
            btn_login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (btn_login.getText().toString().equals("Log in")){
                        showLoginForm(loginWindow, mview);
                    }else {
                        delete_token();
                        sw_private.setEnabled(false);
                        Toast.makeText(mview.getContext(), "You have logout to the server!", Toast.LENGTH_SHORT).show();
                        btn_login.setText("Log in");
                    }
                }
            });


            sw_private.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    isTouched = true;
                    return false;
                }
            });

            sw_private.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                {
                    if (isTouched) {
                        isTouched = false;
                        if (isChecked) {
                            privateMode = true;
                            saveStateOfSwitch(privateMode);
                        }
                        else {
                            privateMode = false;
                            saveStateOfSwitch(privateMode);
                        }
                    }
                }
            });

//            TODO: set the states of switch compat and login button
            //read pref file
            //set the states
            readPrefSettings();

            if (user_name.length() != 0){
                btn_login.setText("Logout (login as " + user_name + ")");
                sw_private.setEnabled(true);
                sw_private.setChecked(privateMode);
            }

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
        sharedPrefCal = getActivity().getSharedPreferences("calibration",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefCal.edit();
        editor.putFloat("calValue", calibrationClass.calibrationValue);
        editor.putBoolean("speedMode", calibrationClass.speedMode);
        editor.apply();
    }

    public void readPrefCal(){
        sharedPrefCal = getActivity().getSharedPreferences("calibration", Context.MODE_PRIVATE);
        this.calirationValue = sharedPrefCal.getFloat("calValue", 0f);
        this.speedMode = sharedPrefCal.getBoolean("speedMode", false);
    }

    //setting windows statements
    private void readPrefSettings(){

        sharedPrefSettings = getActivity().getSharedPreferences("settings", Context.MODE_PRIVATE);
        user_name = sharedPrefSettings.getString("username", "");

        auth_token = sharedPrefSettings.getString("token", "");
        privateMode = sharedPrefSettings.getBoolean("private_mode", false);
    }

    private void delete_token(){
        sharedPrefSettings = getActivity().getSharedPreferences("settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefSettings.edit();
        editor.putString("username", "");
        editor.putString("token", "");
        editor.putBoolean("private_mode", false);
        editor.apply();
    }

    private void saveStateOfSwitch(boolean state){
        sharedPrefSettings = getActivity().getSharedPreferences("settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefSettings.edit();
        editor.putBoolean("private_mode", state);
        editor.apply();
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

                String username, password;
                username = ed_username.getText().toString();
                password = ed_password.getText().toString();

                final Login login = new Login(view, parentView, loginDialog, username, password);
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
