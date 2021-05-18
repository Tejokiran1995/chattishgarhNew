package com.visiontek.Mantra.Activities;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mantra.mTerminal100.MTerminal100API;
import com.mantra.mTerminal100.printer.PrinterCallBack;
import com.mantra.mTerminal100.printer.Prints;
import com.visiontek.Mantra.Adapters.CustomAdapter2;
import com.visiontek.Mantra.Models.DataModel2;
import com.visiontek.Mantra.R;
import com.visiontek.Mantra.Utils.DatabaseHelper;
import com.visiontek.Mantra.Utils.TaskPrint;
import com.visiontek.Mantra.Utils.Util;
import com.visiontek.Mantra.Utils.XML_Parsing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.visiontek.Mantra.Activities.Dealer_Details.DName;
import static com.visiontek.Mantra.Activities.Ration_details.COMMPOSITION;
import static com.visiontek.Mantra.Activities.Ration_details.COMMQNTY;
import static com.visiontek.Mantra.Activities.Ration_details.Camount;
import static com.visiontek.Mantra.Activities.Ration_details.Cavlil;
import static com.visiontek.Mantra.Activities.Ration_details.Cbal;
import static com.visiontek.Mantra.Activities.Ration_details.Ccode;
import static com.visiontek.Mantra.Activities.Ration_details.Closebal;
import static com.visiontek.Mantra.Activities.Ration_details.Cmin;
import static com.visiontek.Mantra.Activities.Ration_details.Cmonth;
import static com.visiontek.Mantra.Activities.Ration_details.Cname;
import static com.visiontek.Mantra.Activities.Ration_details.Cprice;
import static com.visiontek.Mantra.Activities.Ration_details.Ctotal;
import static com.visiontek.Mantra.Activities.Ration_details.Ctype;
import static com.visiontek.Mantra.Activities.Ration_details.Cyear;
import static com.visiontek.Mantra.Activities.Ration_details.TOTALAMOUNT;


import static com.visiontek.Mantra.Activities.Start.L;
import static com.visiontek.Mantra.Activities.Start.mp;
import static com.visiontek.Mantra.Utils.Util.releaseMediaPlayer;


public class Print extends AppCompatActivity implements PrinterCallBack {

    String ration;
    public static int getConnectionValue;
    private static String ACTION_USB_PERMISSION;

    public int nPrintContent = 2;
    public int nPrintWidth = 500;
    public int nCompressMethod = 0;
    public boolean bAutoPrint = false;
    ArrayList<DataModel2> data;
    Context context;
    ProgressDialog pd = null;
    Button print;
    byte[] array;
    private Print mActivity;
    private final ExecutorService es = Executors.newScheduledThreadPool(30);
    private MTerminal100API mTerminal100API;
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                probe();
                // btnConnect.performClick();
                Toast.makeText(context, context.getResources().getString(R.string.ConnectUSB), Toast.LENGTH_LONG).show();
                print.setEnabled(true);
                synchronized (this) {

                }
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print);

        context = Print.this;
        mActivity = this;


        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.enable();
       /* TextView rd = findViewById(R.id.rd);
        boolean  rd_fps;
        rd_fps = RDservice(context);

        if (rd_fps) {
            rd.setTextColor(context.getResources().getColor(R.color.green));
        } else {
            toast(context, context.getResources().getString(R.string.No_RD_Service));
            rd.setTextColor(context.getResources().getColor(R.color.black));
        }*/

        ACTION_USB_PERMISSION = mActivity.getApplicationInfo().packageName;
        pd = new ProgressDialog(context);
        print = findViewById(R.id.print);

        Intent intent = getIntent();
        ration = intent.getStringExtra("key");

        TextView total = findViewById(R.id.totalamount);
        RecyclerView recyclerView = findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        data = new ArrayList<>();
        String j,k,l;
        float prev;
        for (int i = 0; i < Cavlil.size(); i++) {
            j= String.valueOf(Camount.get(i));
            k= String.valueOf(COMMQNTY.get(i));
            prev=(Float.parseFloat(Cbal.get(i))-COMMQNTY.get(i));
            l= String.valueOf(prev);
            data.add(new DataModel2(Cname.get(i) + "\n(" + Ctotal.get(i) + ")", l,k,j));
        }

        RecyclerView.Adapter adapter = new CustomAdapter2(this, data);
        recyclerView.setAdapter(adapter);

        String t= String.valueOf(TOTALAMOUNT);
        total.setText(t);


        print.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                if (Util.networkConnected(context)) {
                    Util.generateNoteOnSD(context, "RationReq.txt", ration);
                    hitURL(ration);
                } else {


                    show_error_box(context.getResources().getString(R.string.Internet_Connection_Msg),context.getResources().getString(R.string.Internet_Connection));
                }

            }
        });


        mTerminal100API = new MTerminal100API();
        mTerminal100API.initPrinterAPI(this, this);
        print.setEnabled(false);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            probe();
        } else {
            finish();
        }

    }

    private void hitURL(String xmlformat) {
        if (mp!=null) {
            releaseMediaPlayer(context,mp);
        }
        if (L.equals("hi")) {
        } else {
            mp = mp.create(context, R.raw.c100076);
            mp.start();
        }

        pd = ProgressDialog.show(context, context.getResources().getString(R.string.Confirm), context.getResources().getString(R.string.Please_Wait), true, false);
        XML_Parsing request = new XML_Parsing(Print.this, xmlformat, 11);
        request.setOnResultListener(new XML_Parsing.OnResultListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onCompleted(String isError, String msg, String ref, String flow) {

                if (pd.isShowing()) {
                    pd.dismiss();
                }

                if (!isError.equals("00")) {
                    System.out.println("SESSION TIMED OUT");
                    show_error_box(msg, "Commodity : " + isError);

                } else {
                    COMMPOSITION.clear();
                    COMMQNTY.clear();

                    Cavlil.clear();
                    Cbal.clear();
                    Camount.clear();
                    Cmin.clear();
                    Cprice.clear();
                    Closebal.clear();
                    Cmonth.clear();
                    Cname.clear();
                    Ccode.clear();
                    Ctype.clear();
                    Cyear.clear();
                    Ctotal.clear();


                   /* avlil.clear();
                    bal.clear();
                    close.clear();
                    name.clear();
                    code.clear();
                    unit.clear();
                    min.clear();
                    price.clear();
                    req.clear();
                    total.clear();
                    type.clear();
                    month.clear();
                    year.clear();
                    wei.clear();
*/



                    DatabaseHelper databaseHelper = new DatabaseHelper(context);

                    ArrayList<String> pbal = databaseHelper.get_PD(0);
                    ArrayList<String> carry = databaseHelper.get_PD(1);
                    ArrayList<String> individual = databaseHelper.get_PD(2);
                    ArrayList<String> cnmae = databaseHelper.get_PD(3);
                    //ArrayList<String> rcid = new ArrayList<>();
                    ArrayList<String> mname = databaseHelper.get_PD(5);
                    ArrayList<String> recp = databaseHelper.get_PD(7);
                    ArrayList<String> retail = databaseHelper.get_PD(8);
                    ArrayList<String> tot = databaseHelper.get_PD(11);
                    ArrayList<String> totq = databaseHelper.get_PD(12);
                    ArrayList<String> ttime = databaseHelper.get_PD(13);

                    String app;
                    StringBuilder add = new StringBuilder();
                    for (int i = 0; i < pbal.size(); i++) {
                        app = "  " + cnmae.get(i) + "\n" + totq.get(i) + "  " + "  " + carry.get(i) + "  " + "  " + retail.get(i) + "  " + "  " + individual.get(i) + "  " + "\n";
                        add.append(app);
                    }

                    String date = ttime.get(0).substring(0, 19);

                    String str1,str2,str3,str4,str5;
                    String[] str = new String[4];
                    if (L.equals("hi")) {

                         str1 = context.getResources().getString(R.string.Chhattisgarh) + "\n"
                                + context.getResources().getString(R.string.Department) + "\n"
                                + context.getResources().getString(R.string.RECEIPT) + "\n";

                         image(str1,"header.bmp",1);
                         str2 = context.getResources().getString(R.string.FPS_Owner_Name) + " :" + DName + "\n"
                                + context.getResources().getString(R.string.FPS_No) + " :" + flow + "\n"
                                + context.getResources().getString(R.string.Name_of_Consumer) + " :" + mname.get(0) + "\n"
                                + context.getResources().getString(R.string.Card_No) + " :" + ref + "\n"
                                + context.getResources().getString(R.string.TransactionID) + " :" + recp.get(0) + "\n"
                                + context.getResources().getString(R.string.Date) + " :" + date + "\n"
                                + context.getResources().getString(R.string.commodity) + " " + context.getResources().getString(R.string.lifted) + "   " + context.getResources().getString(R.string.rate) + "    " + context.getResources().getString(R.string.price) ;


                         str3 = (add)+"";


                         str4 = context.getResources().getString(R.string.Total_Amount) + " :" + tot.get(0) ;

                        image(str2+str3+str4,"body.bmp",0);

                         str5 = context.getResources().getString(R.string.Public_Distribution_Dept) + "\n"
                                + context.getResources().getString(R.string.Note_Qualitys_in_KgsLtrs) + "\n\n";
                        image(str5,"tail.bmp",1);

                        str[0] = "1";
                        str[1] = "1";
                        str[2] = "1";
                        str[3] = "1";
                        dialog(str,1);
                    }else {
                        str1 = context.getResources().getString(R.string.Chhattisgarh) + "\n"
                                + context.getResources().getString(R.string.Department) + "\n"
                                + context.getResources().getString(R.string.RECEIPT) + "\n";

                        str2 = "\n________________________________\n"
                                + context.getResources().getString(R.string.FPS_Owner_Name) + "  :" + DName + "\n"
                                + context.getResources().getString(R.string.FPS_No) + "          :" + flow + "\n"
                                + context.getResources().getString(R.string.Name_of_Consumer) + ":" + mname.get(0) + "\n"
                                + context.getResources().getString(R.string.Card_No) + "        :" + ref + "\n"
                                + context.getResources().getString(R.string.TransactionID) + "   :" + recp.get(0) + "\n"
                                + context.getResources().getString(R.string.Date) + "            :" + date + "\n"
                                + context.getResources().getString(R.string.commodity) + " " + context.getResources().getString(R.string.lifted) + "   " + context.getResources().getString(R.string.rate) + "    " + context.getResources().getString(R.string.price) + "\n"
                                + "________________________________\n";

                        str3 = (add)
                                + "________________________________\n";

                        str4 = context.getResources().getString(R.string.Total_Amount) + "    :" + tot.get(0) + "\n"
                                + "________________________________\n";


                        str5 = context.getResources().getString(R.string.Public_Distribution_Dept) + "\n"
                                + context.getResources().getString(R.string.Note_Qualitys_in_KgsLtrs) + "\n\n\n\n";

                        str[0] = "1";
                        str[1] = str1;
                        str[2] = str2 + str3 + str4;
                        str[3] = str5;
                        dialog(str,0);
                    }
                }
            }
        });
        request.execute();
    }
    private void image(String content, String name,int align) {
        try {
            Util.image(content,name,align);
        } catch (IOException e) {
            e.printStackTrace();
            show_error_box(e.toString(),"Image formation Error");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void dialog(final String[] str, final int i) {

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage(context.getResources().getString(R.string.Printing));
        alertDialogBuilder.setTitle(context.getResources().getString(R.string.Transaction));
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton(context.getResources().getString(R.string.Ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        if (Util.batterylevel(context)|| Util.adapter(context)) {
                            if (mp!=null) {
                                releaseMediaPlayer(context,mp);
                            }
                            if (L.equals("hi")) {
                            } else {
                                mp = mp.create(context, R.raw.c100191);
                                mp.start();
                            }
                            es.submit(new TaskPrint(mTerminal100API,str,mActivity,context,i));
                        }else {
                            show_error_box(context.getResources().getString(R.string.Battery_Msg),context.getResources().getString(R.string.Battery));
                        }
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void probe() {
        final UsbManager mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        if (deviceList.size() > 0) {

            while (deviceIterator.hasNext()) {
// Here is if not while, indicating that I only want to support a device
                final UsbDevice device = deviceIterator.next();
                if ((device.getProductId() == 22304) && (device.getVendorId() == 1155)) {
                    // TODO Auto-generated method stub
                    PendingIntent mPermissionIntent = PendingIntent
                            .getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION), 0);
                    if (!mUsbManager.hasPermission(device)) {
                        mUsbManager.requestPermission(device, mPermissionIntent);
                        IntentFilter filter = new IntentFilter();
                        filter.addAction(ACTION_USB_PERMISSION);
                        context.registerReceiver(mUsbReceiver, filter);
                        Toast.makeText(getApplicationContext(),
                                context.getResources().getString(R.string.Permission_denied), Toast.LENGTH_LONG)
                                .show();
                    } else {
                        Toast.makeText(context, context.getResources().getString(R.string.Connecting), Toast.LENGTH_SHORT).show();

                        print.setEnabled(false);
                        es.submit(new Runnable() {
                            @Override
                            public void run() {
                                mTerminal100API.printerOpenTask(mUsbManager, device, context);
                            }
                        });
                    }
                    //  });
                } else {
                    //  Toast.makeText(ConnectUSBActivity.this, "Connection Failed", Toast.LENGTH_SHORT).show();
                }
            }
        }
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

    @Override
    public void OnOpen() {
        // TODO Auto-generated method stub
        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {

                print.setEnabled(true);
                // btnConnect.setEnabled(false);
                Toast.makeText(context, context.getResources().getString(R.string.CONNECTED), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void OnOpenFailed() {
        // TODO Auto-generated method stub
        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {

                print.setEnabled(false);
                //btnConnect.setEnabled(true);

                Toast.makeText(context, context.getResources().getString(R.string.Connection_Failed), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void OnClose() {
        // TODO Auto-generated method stub
        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {

                print.setEnabled(false);
                // btnConnect.setEnabled(true);
                if (mUsbReceiver != null) {
                    context.unregisterReceiver(mUsbReceiver);
                }

                // If Close is caused because the printer is turned off. Then you need to re-enumerate it here.
                probe();
            }
        });
    }

    @Override
    public void OnPrint(final int bPrintResult, final boolean bIsOpened) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                Toast.makeText(context.getApplicationContext(), (bPrintResult == 0) ? getResources().getString(R.string.printsuccess) : getResources().getString(R.string.printfailed) + " " + Prints.ResultCodeToString(bPrintResult), Toast.LENGTH_SHORT).show();
                mActivity.print.setEnabled(bIsOpened);
            }
        });

    }
}
