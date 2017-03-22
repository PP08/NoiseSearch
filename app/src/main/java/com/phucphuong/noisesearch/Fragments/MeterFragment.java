package com.phucphuong.noisesearch.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ToggleButton;
import com.phucphuong.noisesearch.R;
import com.phucphuong.noisesearch.Utilities.AsyncTaskMap;
import com.phucphuong.noisesearch.Utilities.GPSTracker;
import com.phucphuong.noisesearch.Utilities.SoundMeter;

import org.osmdroid.api.IMapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;


/**
 * A simple {@link Fragment} subclass.
 */
public class MeterFragment extends Fragment {


    public MeterFragment() {
        // Required empty public constructor
    }


    //for map thread
    MapView map;
    IMapController iMapController;
    Marker startMarker;

    SoundMeter soundMeter;
    double spl = 0;
    public SettingsFragment settingsFragment;
    public GraphFragment graphFragment;
    public MapFragment mapFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View meterView = inflater.inflate(R.layout.fragment_meter, container, true);
        ToggleButton btn_start_stop = (ToggleButton)meterView.findViewById(R.id.btn_start_stop);

        //fragment settings
        settingsFragment = (SettingsFragment) getFragmentManager().findFragmentById(R.id.settingsFragment);

        //fragment graph
        graphFragment = (GraphFragment) getFragmentManager().findFragmentById(R.id.graphFragment);

        //fragment map
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);

        if (graphFragment != null){
            graphFragment.initializeLineChart();
        }


        btn_start_stop.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){

                    //disable settings button
                    settingsFragment.setStateOfSettingsButtons(false);
                    if (mapFragment != null){
                        //start asyncTask here
                        mapFragment.startGPSTracker();
                    }
                    float calibrationValue = settingsFragment.readPref();
                    soundMeter = new SoundMeter(handler, getActivity(), calibrationValue);
                    soundMeter.thread.start();

                }else {

                    soundMeter.terminate();
                    //myThread.interrupt();
                    soundMeter.thread.interrupt();
                    soundMeter.logThread.interrupt();

                    settingsFragment.setValuesText("NOISE SEARCH");

                    //enable settings button
                    settingsFragment.setStateOfSettingsButtons(true);

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
                settingsFragment.setValuesText(Double.toString(spl));
                if (graphFragment != null){
                    graphFragment.addEntry(spl);

                }
                if (mapFragment != null){
                    mapFragment.drawOnFrament(location[0], location[1]);
                }

            }else{
                if (mapFragment != null){
                    mapFragment.setEndMarker();
                }
            }
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (soundMeter != null) {
            if (soundMeter.thread.isAlive())
                soundMeter.terminate();
                soundMeter.thread.interrupt();
                soundMeter.logThread.interrupt();
        }
    }
}
