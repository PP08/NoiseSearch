package com.phucphuong.noisesearch.Utilities;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import com.phucphuong.noisesearch.R;

/**
 * Created by phucphuong on 3/21/17.
 */

public class CalibrationWindow {

    View view;
    TextView tv_spl;
    ImageButton btn_minus, btn_plus;
    SwitchCompat sw_fastMode;
    static Boolean isTouched = false;

    public float calibrationValue;
    public boolean speedMode;
    AsyncTaskCalibration asyncTaskCalibration;

    public CalibrationWindow(View view, float calibrationValue, boolean speedMode) {

        this.view = view;
        this.calibrationValue = calibrationValue;
        this.speedMode = speedMode;
    }

    public void getViewElements() {

        tv_spl = (TextView) view.findViewById(R.id.tv_spl);
        tv_spl.setText("...dB");

        btn_minus = (ImageButton) view.findViewById(R.id.btn_decrease);
        btn_plus = (ImageButton) view.findViewById(R.id.btn_increase);

        sw_fastMode = (SwitchCompat) view.findViewById(R.id.sw_fastMode);

        asyncTaskCalibration = new AsyncTaskCalibration(tv_spl, calibrationValue, speedMode);

        asyncTaskCalibration.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

//        asyncTaskCalibration.execute();

        btn_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                asyncTaskCalibration.calibrationValue--;

            }
        });

        btn_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                asyncTaskCalibration.calibrationValue++;
            }
        });

        if (speedMode) {
            sw_fastMode.setChecked(true);
        }

        //switchcompat
        sw_fastMode.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                isTouched = true;
                return false;
            }
        });

        sw_fastMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isTouched) {
                    isTouched = false;
                    if (isChecked) {
                        terminateThread();
                        asyncTaskCalibration.cancel(true);
                        speedMode = true;
                        asyncTaskCalibration = new AsyncTaskCalibration(tv_spl, calibrationValue, speedMode);
//                        asyncTaskCalibration.execute();
                        asyncTaskCalibration.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    } else {

                        terminateThread();
                        asyncTaskCalibration.cancel(true);
                        speedMode = false;
                        asyncTaskCalibration = new AsyncTaskCalibration(tv_spl, calibrationValue, speedMode);
//                        asyncTaskCalibration.execute();
                        asyncTaskCalibration.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }
                }
            }
        });
    }

    public void terminateThread() {
        calibrationValue = asyncTaskCalibration.calibrationValue;
        speedMode = asyncTaskCalibration.speedMode;
        asyncTaskCalibration.isRunning = false;
        if (asyncTaskCalibration.thread != null){
            if (asyncTaskCalibration.thread.isAlive()){
                asyncTaskCalibration.thread.interrupt();
            }
        }

        if (asyncTaskCalibration.recordInstance != null) {
            asyncTaskCalibration.recordInstance.release();
        }
    }
}
