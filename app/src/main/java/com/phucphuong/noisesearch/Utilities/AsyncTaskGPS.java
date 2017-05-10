package com.phucphuong.noisesearch.Utilities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.phucphuong.noisesearch.R;


/**
 * Created by phucphuong on 3/29/17.
 */

public class AsyncTaskGPS extends AsyncTask<Void, Void, Void> {


    private ProgressDialog progressDialog;
    private View view;
    private GPSTracker gpsTracker;
    private boolean shouldContinue = true;


    public AsyncTaskGPS(View view){
        this.view = view;
        this.gpsTracker = new GPSTracker(view.getContext());

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(view.getContext());
        progressDialog.setTitle("Getting your current location");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        progressDialog.setOnKeyListener(new AlertDialog.OnKeyListener(){
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK){
                    ToggleButton toggleButton = (ToggleButton)view.findViewById(R.id.btn_start_stop);
                    toggleButton.setEnabled(false);
                    toggleButton.setAlpha(0.5f);
                    progressDialog.dismiss();
//                    progressDialog.dismiss();
                    Toast.makeText(view.getContext(), "You can start measure after your location has created", Toast.LENGTH_LONG).show();
                }
                return true;
            }
        });

    }

    @Override
    protected Void doInBackground(Void... params) {


        while (shouldContinue){
            if (gpsTracker.lastLocation != null && (System.currentTimeMillis() - gpsTracker.lastLocation.getTime()) < 1000 * 60 * 1){
//                Log.e("time", Long.toString(gpsTracker.lastLocation.getTime()));
//                Log.e("system time", Long.toString(System.currentTimeMillis()));

                shouldContinue = false;
            }else {
                gpsTracker.lastLocation = gpsTracker.locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
        }
        gpsTracker.stopUsingGPS();
        return null;
    }
    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (progressDialog.isShowing()){
            progressDialog.dismiss();
        }
        ToggleButton toggleButton = (ToggleButton)view.findViewById(R.id.btn_start_stop);
        toggleButton.setEnabled(true);
        toggleButton.setAlpha(1f);
    }
}
