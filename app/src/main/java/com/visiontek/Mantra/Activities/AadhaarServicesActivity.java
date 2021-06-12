package com.visiontek.Mantra.Activities;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.visiontek.Mantra.R;


import static com.visiontek.Mantra.Utils.Util.RDservice;
import static com.visiontek.Mantra.Utils.Util.diableMenu;

public class AadhaarServicesActivity extends AppCompatActivity {
    Button back, uid, beneficiary;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_aadhar__services);
        context = AadhaarServicesActivity.this;
        TextView rd = findViewById(R.id.rd);
        boolean  rd_fps;
        rd_fps = RDservice(context);
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.enable();
        if (rd_fps) {
            rd.setTextColor(context.getResources().getColor(R.color.green));
        } else {
            show_error_box(context.getResources().getString(R.string.RD_Service_Msg),context.getResources().getString(R.string.RD_Service));
            rd.setTextColor(context.getResources().getColor(R.color.black));
        }

        if (diableMenu(context, 4)) {
            uid.setVisibility(View.INVISIBLE);
            uid.setEnabled(false);
        }

        if (diableMenu(context, 2)) {
            beneficiary.setVisibility(View.INVISIBLE);
            beneficiary.setEnabled(false);
        }
        uid = findViewById(R.id.btn_uid_seeding);
        beneficiary = findViewById(R.id.btn_beneficiaryverification);
        back = findViewById(R.id.btn_back);

        uid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent uid = new Intent(context, AadhaarSeedingActivity.class);
                startActivity(uid);
            }
        });

        beneficiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ben = new Intent(context, BeneficiaryVerificationActivity.class);
                startActivity(ben);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void show_error_box(String msg, String title) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage(msg);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton(context.getResources().getString(R.string.Ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


}
