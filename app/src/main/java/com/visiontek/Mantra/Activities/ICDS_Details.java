package com.visiontek.Mantra.Activities;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.visiontek.Mantra.R;

import java.util.Locale;


import static com.visiontek.Mantra.Utils.Util.toast;

public class ICDS_Details extends AppCompatActivity {
    Button iris, scanfp, back;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_i_c_d_s__details);
        context = ICDS_Details.this;

        iris = findViewById(R.id.btn_iris);
        scanfp = findViewById(R.id.btn_scan_fp);
        back = findViewById(R.id.btn_back);

        iris.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toast(context, "This function is not available yet");
            }
        });

        scanfp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toast(context, "This function is not available yet");
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


}
