package com.visiontek.Mantra.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.visiontek.Mantra.R;

import java.util.Locale;



public class Others extends AppCompatActivity {
    Button icds, mdm, madad, wellfare, back;
    Intent i;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_others);

        context = Others.this;
        icds = findViewById(R.id.btn_icds);
        mdm = findViewById(R.id.btn_mdm);
        madad = findViewById(R.id.btn_madad);
        wellfare = findViewById(R.id.btn_wellfare);
        back = findViewById(R.id.btn_back);


        icds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i = new Intent(context, ICDS.class);
                startActivity(i);
            }
        });
        mdm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i = new Intent(context, MDM.class);
                startActivity(i);
            }
        });
        madad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i = new Intent(context, Madad.class);
                startActivity(i);
            }
        });
        wellfare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i = new Intent(context, Wellfare.class);
                startActivity(i);
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


