package com.phucphuong.noisesearch.Fragments;


import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CompoundButtonCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineData;
import com.phucphuong.noisesearch.R;
import com.phucphuong.noisesearch.Utilities.SoundMeter;


/**
 * A simple {@link Fragment} subclass.
 */
public class MeterFragment extends Fragment {


    public MeterFragment() {
        // Required empty public constructor
    }


    SoundMeter soundMeter;
    double spl = 0;
    public SettingsFragment settingsFragment;
    public GraphFragment graphFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View function1View = inflater.inflate(R.layout.fragment_meter, container, true);
        ToggleButton btn_start_stop = (ToggleButton)function1View.findViewById(R.id.btn_start_stop);

        //fragment settings
        settingsFragment = (SettingsFragment) getFragmentManager().findFragmentById(R.id.settingsFragment);

        //fragment graph
        graphFragment = (GraphFragment) getFragmentManager().findFragmentById(R.id.graphFragment);
        graphFragment.initializeLineChart();


        btn_start_stop.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    //Toast.makeText(getActivity(), "starting...", Toast.LENGTH_SHORT).show();

                    //disable settings button
                    settingsFragment.setStateOfSettingsButtons(false);
                    soundMeter = new SoundMeter(handler);
                    soundMeter.thread.start();

                }else {

                    //Toast.makeText(getActivity(), "stopped...", Toast.LENGTH_SHORT).show();

                    soundMeter.terminate();
                    soundMeter.thread.interrupt();

                    settingsFragment.setValuesText("Noise Search");

                    //enable settings button
                    settingsFragment.setStateOfSettingsButtons(true);


                }
            }
        });

        return function1View;
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            handler.obtainMessage();
            boolean isRunning = msg.getData().getBoolean("isRunning");
            if(isRunning){
                spl = msg.getData().getDouble("spl");
                settingsFragment.setValuesText(Double.toString(spl));
                graphFragment.addEntry(spl);
            }

        }
    };





}
