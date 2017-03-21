package com.phucphuong.noisesearch.Fragments;

import android.app.FragmentManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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

    double spl = 0;


    public CalibrationWindow(View view){

        this.view = view;

    }

    public void initialView(){

//        TODO : add asyncTask thread here

    }

    public void getViewElements(){

        tv_spl = (TextView)view.findViewById(R.id.tv_spl);
        tv_spl.setText(Double.toString(spl));

        btn_minus = (ImageButton)view.findViewById(R.id.btn_decrease);
        btn_plus = (ImageButton)view.findViewById(R.id.btn_increase);

        btn_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spl -= 10;
                tv_spl.setText(Double.toString(spl));
            }
        });

        btn_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                spl += 10;
                tv_spl.setText(Double.toString(spl));

            }
        });





    }
}
