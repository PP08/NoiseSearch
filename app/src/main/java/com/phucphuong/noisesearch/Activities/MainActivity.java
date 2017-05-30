package com.phucphuong.noisesearch.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.phucphuong.noisesearch.Fragments.MeterFragment;
import com.phucphuong.noisesearch.R;
import com.phucphuong.noisesearch.Utilities.ReplaceFont;
import com.phucphuong.noisesearch.Utilities.UploadFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

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


        //Toast.makeText(this, "hello there", Toast.LENGTH_LONG).show();

        //show the datetime dialog here!

        //get system date and time

        final Calendar c = Calendar.getInstance();
        String year = Integer.toString(c.get(Calendar.YEAR));
        String month = Integer.toString(c.get(Calendar.MONTH));
        String day = Integer.toString(c.get(Calendar.DAY_OF_MONTH));
        String hour = Integer.toString(c.get(Calendar.HOUR_OF_DAY));
        String sminute;
        int minute = c.get(Calendar.MINUTE);
        if (minute < 10){
            sminute = "0" + Integer.toString(minute);
        }else {
            sminute = Integer.toString(minute);
        }

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Please check your date and time");
        alert.setMessage("Date: " + day + " - " + month +
        " - " + year + "\n\nTime: "+ hour + " : " + sminute);
        alert.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alert.show();


    }

    private void requestPer() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

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

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            arrayPermission.add(Manifest.permission.READ_PHONE_STATE);
        }

        return arrayPermission;
    }

}


//TODO: rewrite function require permission, if permission has accepted then do not ask again!
// TODO: (done, but still duplicate when permission denied)
