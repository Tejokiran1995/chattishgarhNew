package com.visiontek.Mantra.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.visiontek.Mantra.R;


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


