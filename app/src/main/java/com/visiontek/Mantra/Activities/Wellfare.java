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

public class Wellfare extends AppCompatActivity {
    Button back, details;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_wellfare);

        context = Wellfare.this;

        details = findViewById(R.id.btn_details);
        back = findViewById(R.id.btn_back);
        details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toast(context, "This function is not available yet");
               /* Intent uid=new Intent(context,UID_Seeding_Aadhar.class);
                startActivity(uid);*/
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
