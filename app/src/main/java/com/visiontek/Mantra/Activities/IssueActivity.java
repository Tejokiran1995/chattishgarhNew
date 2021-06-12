package com.visiontek.Mantra.Activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import com.visiontek.Mantra.Utils.Util;

import static com.visiontek.Mantra.Utils.Util.RDservice;
import static com.visiontek.Mantra.Utils.Util.diableMenu;

public class IssueActivity extends AppCompatActivity {
    Button cash, cashless, impds, back;
    Context context;
    TextView rd;
    ProgressDialog pd = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue);
        context = IssueActivity.this;

        cash = findViewById(R.id.btn_cash_pds);
        cashless = findViewById(R.id.btn_cashless_pds);
        impds = findViewById(R.id.btn_impds);
        back = findViewById(R.id.btn_back);
        pd = new ProgressDialog(context);

        rd = findViewById(R.id.rd);

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


        if (diableMenu(context, 9)) {
            cash.setVisibility(View.INVISIBLE);
            cash.setEnabled(false);
        }

        if (diableMenu(context, 14)) {
            impds.setVisibility(View.INVISIBLE);
            impds.setEnabled(false);
        }
        cash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cash = new Intent(IssueActivity.this, CashPDSActivity.class);
                cash.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(cash);
                finish();

            }
        });
        cashless.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.toast(IssueActivity.this, "Cashless PDS function is Not Available");
            }
        });
        impds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.toast(IssueActivity.this, "IMPDS function is Not Available");
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
