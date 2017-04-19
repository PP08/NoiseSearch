package com.phucphuong.noisesearch.Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.phucphuong.noisesearch.R;
import com.phucphuong.noisesearch.Utilities.AsyncTaskGPS;
import com.phucphuong.noisesearch.Utilities.SoundMeter;
import com.phucphuong.noisesearch.Utilities.UploadFile;
import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 */
public class MeterFragment extends Fragment {


    public MeterFragment() {
        // Required empty public constructor
    }

    SoundMeter soundMeter;
    double spl = 0;

    float start = -90;
    float end = 0;
    public SettingsFragment settingsFragment;
    public GraphFragment graphFragment;
    public MapFragment mapFragment;

    private String prefix;
    View meterView;
    UploadFile uploadFile;
    final ProgressDialog[] progressDialog = new ProgressDialog[1];
    boolean shouldContinue = true;


    //
    Animation animation;
    ImageView clockwise;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        meterView = inflater.inflate(R.layout.fragment_meter, container, true);
        ToggleButton btn_start_stop = (ToggleButton)meterView.findViewById(R.id.btn_start_stop);

        btn_start_stop.setEnabled(false);
        btn_start_stop.setAlpha(0.8f);
        //clockwise
        clockwise = (ImageView)meterView.findViewById(R.id.clockwise);

        animation = createAnimation(90f, -90);
        animation.setDuration(1500);
        clockwise.startAnimation(animation);

        //fragment settings
        settingsFragment = (SettingsFragment) getFragmentManager().findFragmentById(R.id.settingsFragment);

        //fragment graph
        graphFragment = (GraphFragment) getFragmentManager().findFragmentById(R.id.graphFragment);

        //fragment map
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);

        if (graphFragment != null){
            graphFragment.initializeLineChart();
            prefix = "single";
        }else {
            prefix = "multiple";
        }

        AsyncTaskGPS asyncTaskGPS = new AsyncTaskGPS(meterView.getRootView());
        asyncTaskGPS.execute();

        btn_start_stop.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    AsyncTaskGPS asyncTaskGPS = new AsyncTaskGPS(meterView.getRootView());
                    asyncTaskGPS.execute();

//                    TODO: merge this asyncTask thread to this thread.

                    //disable calibration button
                    settingsFragment.setStateOfSettingsButtons(true);

                    if (mapFragment != null){
                        //start asyncTask here
                        mapFragment.startGPSTracker();
                    }
                    settingsFragment.readPrefCal();
                    soundMeter = new SoundMeter(handler, getActivity(), settingsFragment.calirationValue, settingsFragment.speedMode, prefix);
                    soundMeter.thread.start();

                }else {
                    handleDestroyView();
                    sentFileToServer();
                }
            }
        });

        return meterView;
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            handler.obtainMessage();
            boolean isRunning = msg.getData().getBoolean("isRunning");
            double [] location = msg.getData().getDoubleArray("location");

            if(isRunning){
                spl = msg.getData().getDouble("spl");
                settingsFragment.setValuesText(Double.toString(spl)," dB");

                end = ((float) spl - 67.5f) * 4/3;

                animation = createAnimation(start,end);
                animation.setDuration(500);
                clockwise.startAnimation(animation);
                start = end;

                if (graphFragment != null){
                    graphFragment.addEntry(spl);
                }
                if (mapFragment != null){
                    assert location != null;
                    mapFragment.drawOnFrament(location[0], location[1]);
                }

            }else{
                if (mapFragment != null){
                    mapFragment.setEndMarker();
                }
            }
        }
    };

    private void sentFileToServer(){

        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle("Send File To Server");
        alert.setMessage("Do you want to send this file to server?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

                progressDialog[0] = new ProgressDialog(getContext());
                progressDialog[0].setTitle("Uploading");
                progressDialog[0].setMessage("Please wait...");
                progressDialog[0].show();
                progressDialog[0].setCanceledOnTouchOutside(false);

                File fileDirUnsent = new File(getContext().getFilesDir() + "/Unsent Files");
                fileDirUnsent.mkdirs();
                File fileDirSent = new File(getContext().getFilesDir() + "/Sent Files");
                fileDirSent.mkdirs();
                File src = new File(fileDirUnsent, soundMeter.FILENAME);
                File dst = new File(fileDirSent, soundMeter.FILENAME);

                uploadFile = new UploadFile(src, dst, getView(), prefix);
                uploadFile.uploadFileToserver();

                AlertDialogAsyncTask alertDialogAsyncTask = new AlertDialogAsyncTask();
                alertDialogAsyncTask.execute();
            }
        });
        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alert.show();
    }

    public boolean isTaskFinished(){
        if (uploadFile.finish){
            return true;
        }
        return false;
    }

    public class AlertDialogAsyncTask extends AsyncTask<Void, Void, Void> {

        boolean success;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            shouldContinue = true;
        }

        @Override
        protected Void doInBackground(Void... params) {
            while(shouldContinue) {
                if (isTaskFinished()) {
                    success =  uploadFile.success;
                    shouldContinue = false;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog[0].dismiss();
            if (!success){
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setTitle("Cannot upload files");
                alert.setMessage("Cannot connect to the server at this moment, please try again in the next time");
                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alert.show();
            }else {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setTitle("Files uploaded successful");
                alert.setMessage("Your files have uploaded to our server. Thank you for your help!");
                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alert.show();
            }
        }
    }

    public Animation createAnimation(float A, float B){
        RotateAnimation rotateAnimation = new RotateAnimation(A, B, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 1f);
        rotateAnimation.setDuration(500);
        rotateAnimation.setFillAfter(true);
        return  rotateAnimation;
    }

    public void handleDestroyView(){
        soundMeter.terminate();
        soundMeter.logThread.interrupt();
        soundMeter.thread.interrupt();
        settingsFragment.setValuesText("NOISE SEARCH", "");
        start = -90;
        animation = createAnimation(end, start);
        animation.setDuration(1000);
        clockwise.startAnimation(animation);
        settingsFragment.setStateOfSettingsButtons(false);
    }

    public void handleBackPress(){
        if (soundMeter != null){
            if (soundMeter.thread.isAlive()){
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setTitle("Stop Measuring");
                alert.setMessage("Do you want to stop the measuring?");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        handleDestroyView();
                        dialog.dismiss();

                        Toast.makeText(getContext(),"Your log have saved.", Toast.LENGTH_SHORT).show();

                        getActivity().finish();
                    }
                });
                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alert.show();
            }else {
                getActivity().finish();
            }
        }else {
            getActivity().finish();
        }
    }


}

