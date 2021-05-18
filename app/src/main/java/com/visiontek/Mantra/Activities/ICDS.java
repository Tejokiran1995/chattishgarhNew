package com.visiontek.Mantra.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.visiontek.Mantra.Models.fpsCommonInfo;
import com.visiontek.Mantra.Models.fpsURLInfo;
import com.visiontek.Mantra.Models.stateBean;
import com.visiontek.Mantra.R;

import java.util.Locale;



public class ICDS extends AppCompatActivity {

    Button back, details;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_i_c_d_s);
        context = ICDS.this;

        details = findViewById(R.id.btn_details);
        back = findViewById(R.id.btn_back);
        details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent uid = new Intent(context, ICDS_Details.class);
                startActivity(uid);
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
