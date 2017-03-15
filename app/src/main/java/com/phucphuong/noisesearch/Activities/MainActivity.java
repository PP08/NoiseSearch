package com.phucphuong.noisesearch.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.phucphuong.noisesearch.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton btn_function1 = (ImageButton)findViewById(R.id.btn_function1);
        ImageButton btn_function2 = (ImageButton)findViewById(R.id.btn_function1);




        btn_function1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MeasureAtSinglePoint.class);
                startActivity(intent);
            }
        });

    }
}
