package com.phucphuong.noisesearch.Utilities;

import android.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.phucphuong.noisesearch.R;

import org.w3c.dom.Text;

/**
 * Created by phucphuong on 3/21/17.
 */

public class CalibrationWindow {


    View view;
    TextView tv_spl;
    ImageButton btn_minus, btn_plus;

    public float calibrationValue;
    AsyncTaskCalibration asyncTaskCalibration;


    String arr[]={
            "Slow",
            "Fast"};


    public CalibrationWindow(View view, float calibrationValue){

        this.view = view;
        this.calibrationValue = calibrationValue;
    }

    public void getViewElements(){

        tv_spl = (TextView)view.findViewById(R.id.tv_spl);
        tv_spl.setText("...dB");

        btn_minus = (ImageButton)view.findViewById(R.id.btn_decrease);
        btn_plus = (ImageButton)view.findViewById(R.id.btn_increase);


        asyncTaskCalibration = new AsyncTaskCalibration(tv_spl, calibrationValue);
        asyncTaskCalibration.execute();

        btn_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                asyncTaskCalibration.calibrationValue --;

            }
        });

        btn_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                asyncTaskCalibration.calibrationValue ++;
            }
        });

    }

    public void terminateThread(){
        calibrationValue = asyncTaskCalibration.calibrationValue;
        Log.e("new calibration value", Float.toString(asyncTaskCalibration.calibrationValue));
        asyncTaskCalibration.isRunning = false;
        asyncTaskCalibration.thread.interrupt();
        if (asyncTaskCalibration.recordInstance != null){
            asyncTaskCalibration.recordInstance.release();
        }
        //asyncTaskCalibration.thread.interrupt();
    }
}
