package com.phucphuong.noisesearch.Fragments;


import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.phucphuong.noisesearch.R;
import com.phucphuong.noisesearch.Utilities.ReplaceFont;


/**
 * A simple {@link Fragment} subclass.
 */
public class GraphFragment extends Fragment {


    public GraphFragment() {
        // Required empty public constructor
    }

    LineChart lineChart;
    Typeface mTfLight = Typeface.DEFAULT;
    View graphView;
    Chronometer chronometer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        graphView = inflater.inflate(R.layout.fragment_graph, container, true);
        chronometer = (Chronometer)graphView.findViewById(R.id.chronometer);
        return graphView;
    }


    public void initializeLineChart() {

        //graph
        lineChart = (LineChart) graphView.findViewById(R.id.line_chart);

        //enable description text
        lineChart.getDescription().setEnabled(false);

        //enable touch gesture
        lineChart.setTouchEnabled(true);

        //enable scaling and dragging
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(false);
        lineChart.setDrawGridBackground(false);

        //if disabled, scaling can be done on x and y axis separately
        lineChart.setPinchZoom(false);

        //set an alternative background color
        lineChart.setBackgroundColor(Color.TRANSPARENT);

        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);

        //add empty data
        lineChart.setData(data);

        //get the legend (only possible after setting data)
        Legend l = lineChart.getLegend();

        //modify the legend
        l.setForm(Legend.LegendForm.LINE);
        l.setTypeface(mTfLight);
        l.setTextColor(Color.WHITE);

        //
//        lineChart.setKeepPositionOnRotation(true);
//        lineChart.setAutoScaleMinMaxEnabled(true);

        //axis
        XAxis xl = lineChart.getXAxis();
        xl.setTypeface(mTfLight);
        xl.setTextColor(Color.WHITE);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(false);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setTypeface(mTfLight);
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setAxisMaximum(140f);
        leftAxis.setAxisMinimum(0f);

        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setTypeface(mTfLight);
        rightAxis.setTextColor(Color.WHITE);
        rightAxis.setAxisMaximum(140f);
        rightAxis.setAxisMinimum(0f);


    }


    public void addEntry(double val) {
        LineData data = lineChart.getData();

        if (data != null) {
            ILineDataSet set = data.getDataSetByIndex(0);
            //set.addEntry(...) //can be called as well
            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }

            data.addEntry(new Entry(set.getEntryCount(), (float) val), 0);
            data.notifyDataChanged();

            //let the chart know it's data has changed
            lineChart.notifyDataSetChanged();

            //limit the number of visible entries
            lineChart.setVisibleXRangeMaximum(10);

            //move to the latest entry
            lineChart.moveViewToX(data.getEntryCount());

        }

    }

    private LineDataSet createSet() {

        LineDataSet set = new LineDataSet(null, "SPL");
        //set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);

        set.setColor(ColorTemplate.getHoloBlue());
        set.setCircleColor(Color.WHITE);
        set.setLineWidth(2f);
        set.setCircleRadius(4f);
        set.setFillAlpha(65);
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(9f);
        set.setDrawValues(false);
        return set;
    }


    public void startChronometer(){

        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener(){
            @Override
            public void onChronometerTick(Chronometer cArg) {
                long time = SystemClock.elapsedRealtime() - cArg.getBase();
                int h   = (int)(time /3600000);
                int m = (int)(time - h*3600000)/60000;
                int s= (int)(time - h*3600000- m*60000)/1000 ;
                String hh = h < 10 ? "0"+h: h+"";
                String mm = m < 10 ? "0"+m: m+"";
                String ss = s < 10 ? "0"+s: s+"";
                cArg.setText(hh+":"+mm+":"+ss);
            }
        });
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
    }

}
