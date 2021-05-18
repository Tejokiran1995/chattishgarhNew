package com.visiontek.Mantra.Activities;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.visiontek.Mantra.R;

import java.util.Locale;


import static com.visiontek.Mantra.Utils.Util.RDservice;
import static com.visiontek.Mantra.Utils.Util.diableMenu;
import static com.visiontek.Mantra.Utils.Util.toast;

public class Reports extends AppCompatActivity {

    Button daily_report, stock_report, back;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_reports);
        context = Reports.this;
        daily_report = findViewById(R.id.btn_dailysales_report);
        stock_report = findViewById(R.id.btn_stock_report);
        back = findViewById(R.id.btn_back);
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

        if (diableMenu(context, 6)) {
            daily_report.setVisibility(View.INVISIBLE);
            daily_report.setEnabled(false);
        }

        if (diableMenu(context, 5)) {
            stock_report.setVisibility(View.INVISIBLE);
            stock_report.setEnabled(false);
        }
        daily_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent daily = new Intent(context, Daily_Sales_Report.class);
                startActivity(daily);
            }
        });

        stock_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent stock = new Intent(context, Stock_Report.class);
                startActivity(stock);
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
