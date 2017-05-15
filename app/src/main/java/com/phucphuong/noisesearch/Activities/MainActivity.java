package com.phucphuong.noisesearch.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.phucphuong.noisesearch.Fragments.SettingsFragment;
import com.phucphuong.noisesearch.R;
import com.phucphuong.noisesearch.Utilities.AsyncTaskGPS;
import com.phucphuong.noisesearch.Utilities.ReplaceFont;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ImageButton btn_function1, btn_function2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ReplaceFont.replaceDefaultFont(this, "DEFAULT", "fonts/Linotte_Regular.otf");
        ReplaceFont.replaceDefaultFont(this, "MONOSPACE", "fonts/Linotte_Regular.otf");
        ReplaceFont.replaceDefaultFont(this, "SERIF", "fonts/Linotte_Regular.otf");
        ReplaceFont.replaceDefaultFont(this, "SANS_SERIF", "fonts/Linotte_Regular.otf");

        setContentView(R.layout.activity_main);

        btn_function1 = (ImageButton) findViewById(R.id.btn_function1);
        btn_function2 = (ImageButton) findViewById(R.id.btn_function2);

        disableBtn();
        requestPer();

        btn_function1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent1 = new Intent(MainActivity.this, MeasureAtSinglePoint.class);
                startActivity(intent1);

            }
        });

        btn_function2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(MainActivity.this, MeasureAtMultiPoints.class);
                startActivity(intent2);
            }
        });

    }

    private void requestPer() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                String[] permissions = new String[arrayPermissions().size()];
                ActivityCompat.requestPermissions(this, arrayPermissions().toArray(permissions), 10);

            } else {
                enableBtn();
            }
        } else {
            enableBtn();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 10: {

                if (grantResults.length > 0) {
                    if (shouldEnableButton(grantResults)) {
                        enableBtn();
                    } else {
                        disableBtn();
                    }
                } else {
                    disableBtn();
                }
            }
        }
    }


    public void enableBtn() {
        btn_function1.setEnabled(true);
        btn_function2.setEnabled(true);
    }

    public void disableBtn() {
        btn_function1.setEnabled(false);
        btn_function2.setEnabled(false);
    }

    private boolean shouldEnableButton(int[] grantResults) {

        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }


    private ArrayList<String> arrayPermissions() {

        ArrayList<String> arrayPermission = new ArrayList<>();


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            arrayPermission.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            arrayPermission.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            arrayPermission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            arrayPermission.add(Manifest.permission.RECORD_AUDIO);
        }

        return arrayPermission;
    }

}


//TODO: rewrite function require permission, if permission has accepted then do not ask again!
// TODO: (done, but still duplicate when permission denied)
