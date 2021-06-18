package com.visiontek.chhattisgarhpds.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.visiontek.chhattisgarhpds.R;


public class OthersActivity extends AppCompatActivity {
    Button icds, mdm, madad, wellfare, back;
    Intent i;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_others);
    }
}


