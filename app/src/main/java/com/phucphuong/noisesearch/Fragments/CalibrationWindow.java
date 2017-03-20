package com.phucphuong.noisesearch.Fragments;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.phucphuong.noisesearch.R;

/**
 * Created by phucphuong on 3/21/17.
 */

public class CalibrationWindow {


    View view;

    public CalibrationWindow(View view){

        this.view = view;

    }

    public void getViewElements(){

        ImageButton btn_back = (ImageButton)view.findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(view.getContext(), "ok", Toast.LENGTH_SHORT).show();
            }
        });
    }





}
