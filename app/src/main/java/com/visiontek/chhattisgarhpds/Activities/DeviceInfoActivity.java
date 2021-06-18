package com.visiontek.chhattisgarhpds.Activities;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.visiontek.chhattisgarhpds.Adapters.CustomAdapter;
import com.visiontek.chhattisgarhpds.Models.DATAModels.DataModel;
import com.visiontek.chhattisgarhpds.R;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import static com.visiontek.chhattisgarhpds.Utils.Util.RDservice;


public class DeviceInfoActivity extends AppCompatActivity {
    RecyclerView.Adapter adapter;
    RecyclerView recyclerView;
    ArrayList<DataModel> arraydata;
    Context context;

    public static String readKernelVersion() {
        try {
            Process p = Runtime.getRuntime().exec("uname -a");
            InputStream is = null;
            if (p.waitFor() == 0) {
                is = p.getInputStream();
            } else {
                is = p.getErrorStream();
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line = br.readLine();
            br.close();
            return line;
        } catch (Exception ex) {
            System.out.println("Main : readKernelVersion : Exception : " + ex);
            return "ERROR: " + ex.getMessage();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device__info);

        context = DeviceInfoActivity.this;
        TextView rd = findViewById(R.id.rd);
        boolean rd_fps;
        rd_fps = RDservice(context);
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.enable();
        if (rd_fps) {
            rd.setTextColor(context.getResources().getColor(R.color.green));
        } else {
            show_error_box(context.getResources().getString(R.string.RD_Service_Msg), context.getResources().getString(R.string.RD_Service));
            rd.setTextColor(context.getResources().getColor(R.color.black));
        }
        saveInfo(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void saveInfo(Context context) {
        ArrayList<String> value = new ArrayList<>();
        value.add("Brand");
        value.add("Product");
        value.add("Hardware");
        value.add("Device");
        value.add("Manufacture");
        value.add("Model");
        value.add("SecurityPatch");
        value.add("Release");
        value.add("SDK");
        value.add("Build No");
        value.add("Baseband");
        value.add("Kernel");
        value.add("Application");

        ArrayList<String> info = new ArrayList<>();
        info.add(Build.BRAND);
        info.add(Build.PRODUCT);
        info.add(Build.HARDWARE);
        info.add(Build.DEVICE);
        info.add(Build.MANUFACTURER);
        info.add(Build.MODEL);
        info.add(Build.VERSION.SECURITY_PATCH);
        info.add(Build.VERSION.RELEASE);
        info.add(Build.VERSION.SDK);
        info.add(Build.DISPLAY);
        info.add(Build.getRadioVersion());
        info.add(readKernelVersion());
        info.add("UniPOS_PDS_Demo");

        recyclerView = findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        arraydata = new ArrayList<>();

        for (int i = 3; i < value.size(); i++) {
            arraydata.add(new DataModel(value.get(i), info.get(i).trim()));
            System.out.println("1111=" + info.get(i));
        }
        adapter = new CustomAdapter(context, arraydata, new OnClickListener() {
            @Override
            public void onClick_d(int p) {

            }
        });
        recyclerView.setAdapter(adapter);
    }

    public interface OnClickListener {
        void onClick_d(int p);
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