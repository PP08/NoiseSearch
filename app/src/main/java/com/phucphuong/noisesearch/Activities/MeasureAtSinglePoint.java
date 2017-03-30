package com.phucphuong.noisesearch.Activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;

import com.phucphuong.noisesearch.Fragments.MeterFragment;
import com.phucphuong.noisesearch.R;
import com.phucphuong.noisesearch.Utilities.UploadFile;

import java.io.File;
import java.util.List;

public class MeasureAtSinglePoint extends FragmentActivity {

    MeterFragment meterFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure_at_single_point);

    }

    @Override
    public void onBackPressed() {
        if (getVisibleFragment()!=null) {
            meterFragment = getVisibleFragment();
            meterFragment.handleBackPress();
        }
//        finish();
    }


    public MeterFragment getVisibleFragment(){
        FragmentManager fragmentManager = MeasureAtSinglePoint.this.getSupportFragmentManager();
        return (MeterFragment) fragmentManager.findFragmentById(R.id.meterFragment);
    }

}
