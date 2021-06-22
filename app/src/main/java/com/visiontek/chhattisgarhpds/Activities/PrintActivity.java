package com.visiontek.chhattisgarhpds.Activities;

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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.versionedparcelable.ParcelImpl;

import com.mantra.mTerminal100.MTerminal100API;
import com.mantra.mTerminal100.printer.PrinterCallBack;
import com.mantra.mTerminal100.printer.Prints;
import com.visiontek.chhattisgarhpds.Adapters.CustomAdapter2;
import com.visiontek.chhattisgarhpds.Models.AppConstants;
import com.visiontek.chhattisgarhpds.Models.DATAModels.DataModel2;
import com.visiontek.chhattisgarhpds.Models.DealerDetailsModel.GetURLDetails.Dealer;
import com.visiontek.chhattisgarhpds.Models.IssueModel.MemberDetailsModel.Print;
import com.visiontek.chhattisgarhpds.Models.PartialOnlineData;
import com.visiontek.chhattisgarhpds.R;
import com.visiontek.chhattisgarhpds.Utils.DatabaseHelper;
import com.visiontek.chhattisgarhpds.Utils.TaskPrint;
import com.visiontek.chhattisgarhpds.Utils.Util;
import com.visiontek.chhattisgarhpds.Utils.XML_Parsing;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import static com.visiontek.chhattisgarhpds.Activities.DealerDetailsActivity.dealername;
import static com.visiontek.chhattisgarhpds.Activities.RationDetailsActivity.TOTALAMOUNT;


import static com.visiontek.chhattisgarhpds.Activities.StartActivity.L;
import static com.visiontek.chhattisgarhpds.Activities.StartActivity.mp;

import static com.visiontek.chhattisgarhpds.Models.AppConstants.dealerConstants;
import static com.visiontek.chhattisgarhpds.Models.AppConstants.memberConstants;
import static com.visiontek.chhattisgarhpds.Utils.Util.releaseMediaPlayer;


public class PrintActivity extends AppCompatActivity implements PrinterCallBack {

    private PrintActivity mActivity;
    private static String ACTION_USB_PERMISSION;

    Context context;
    ProgressDialog pd = null;
    Button print;
    TextView total;
    String txnType,rationCardNo;
    DatabaseHelper databaseHelper;


    private final ExecutorService es = Executors.newScheduledThreadPool(30);
    private MTerminal100API mTerminal100API;
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                probe();
                // btnConnect.performClick();
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
        context = PrintActivity.this;
        mActivity = this;
        ACTION_USB_PERMISSION = mActivity.getApplicationInfo().packageName;

        databaseHelper = new DatabaseHelper(context);

        txnType = getIntent().getStringExtra("txnType");
        rationCardNo = getIntent().getStringExtra("rationCardNo");


        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.enable();

        pd = new ProgressDialog(context);
        print = findViewById(R.id.print);
        total = findViewById(R.id.totalamount);

        Intent intent = getIntent();
        final String ration = intent.getStringExtra("key");

        Display();

        print.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                if (txnType.equals("O") && Util.networkConnected(context)) {
                    Util.generateNoteOnSD(context, "RationReq.txt", ration);
                    hitURL(ration);
                } else {
                    proceedForOfflineTransaction(false,new Print());
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

    public void proceedForOfflineTransaction(boolean isOnlineTransaction,Print printData )
    {
        int isOfflineOk = databaseHelper.txnAllotedBetweenTime();
        if(isOfflineOk == 0)
        {
            Date now = new Date();
            String deviceTxnId,orderDateTime;
            PartialOnlineData partialOnlineData = databaseHelper.getPartialOnlineData();
            if(!isOnlineTransaction)
            {
                dealername = databaseHelper.getOfflineDealerName();
                dealerConstants = new Dealer();
                dealerConstants.stateBean.statefpsId = partialOnlineData.getOffPassword();
                DateFormat dateFormat = new SimpleDateFormat("hhmmss");
                DateFormat orderdateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

                Calendar calendar = Calendar.getInstance();
                int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
                deviceTxnId = String.format("%s%03d%s", AppConstants.DEVICEID,dayOfYear,dateFormat.format(now));
                orderDateTime = orderdateFormat.format(now);
            }
            else
            {
                orderDateTime = printData.printBeans.get(0).transaction_time;
                deviceTxnId = printData.receiptId;
            }


            Print offlinePrintData = databaseHelper.getPrintDataFromLocal(rationCardNo);

            boolean isSuccessful = databaseHelper.updateOfflineData(getApplicationContext(), offlinePrintData,rationCardNo,txnType,deviceTxnId,orderDateTime);
            if(isSuccessful)
            {
                if(!isOnlineTransaction) {
                    setUpNProceedToPrint(offlinePrintData,isOnlineTransaction);
                }
                else
                {
                    setUpNProceedToPrint(printData,isOnlineTransaction);
                }
            }
            else
            {
                //Transaction Failed...Show error Message
                show_error_box("Distribution Response","Distribution Failed,Please try again",0);
            }
        }
        else
            show_error_box(context.getResources().getString(R.string.Internet_Connection_Msg),context.getResources().getString(R.string.Internet_Connection), 0);
    }

    private void hitURL(String ration) {
        if (mp!=null) {
            releaseMediaPlayer(context,mp);
        }
        if (L.equals("hi")) {
        } else {
            mp = mp.create(context, R.raw.c100076);
            mp.start();
        }

        pd = ProgressDialog.show(context, context.getResources().getString(R.string.Confirm), context.getResources().getString(R.string.Please_Wait), true, false);
        XML_Parsing request = new XML_Parsing(PrintActivity.this, ration, 11);
        request.setOnResultListener(new XML_Parsing.OnResultListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onCompleted(String isError, String msg, String ref, String flow, Object object) {
                if (pd.isShowing()) {
                    pd.dismiss();
                }
                if (!isError.equals("00")) {
                    System.out.println("SESSION TIMED OUT");
                    show_error_box(msg, "Commodity : " + isError, 0);
                } else {
                    Print printReceipt= (Print) object;
                    if(databaseHelper.checkForOfflineDistribution() == 0)
                        proceedForOfflineTransaction(true,printReceipt);
                    else
                        setUpNProceedToPrint(printReceipt,true);
                }
            }
        });
        request.execute();
    }

    public void setUpNProceedToPrint(Print printReceipt,boolean isOnlineTransaction)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String app;
            StringBuilder add = new StringBuilder();
            int printReceiptsize= printReceipt.printBeans.size();
            Double totalAmount = 0.0;
            if(isOnlineTransaction)
                totalAmount = Double.parseDouble(printReceipt.printBeans.get(0).tot_amount);
            for (int i = 0; i <printReceiptsize ; i++) {
                app = "  " + printReceipt.printBeans.get(i).comm_name
                        + "\n" + printReceipt.printBeans.get(i).total_quantity+ "  " + "  "
                        + printReceipt.printBeans.get(i).carry_over + "  " + "  "
                        + printReceipt.printBeans.get(i).retail_price + "  " + "  "
                        + printReceipt.printBeans.get(i).commIndividualAmount + "  " + "\n";
                if(!isOnlineTransaction)
                    totalAmount += Double.parseDouble(printReceipt.printBeans.get(i).tot_amount);
                add.append(app);
            }

            String date = printReceipt.printBeans.get(0).transaction_time.substring(0, 19);

            String str1,str2,str3,str4,str5;
            String[] str = new String[4];
            if (L.equals("hi")) {

                str1 = context.getResources().getString(R.string.Chhattisgarh) + "\n"
                        + context.getResources().getString(R.string.Department) + "\n"
                        + context.getResources().getString(R.string.RECEIPT) + "\n";

                image(str1,"header.bmp",1);
                str2 = context.getResources().getString(R.string.FPS_Owner_Name) + " :" + dealername + "\n"
                        + context.getResources().getString(R.string.FPS_No) + " :" +dealerConstants.stateBean.statefpsId + "\n"
                        + context.getResources().getString(R.string.Name_of_Consumer) + " :" +printReceipt.printBeans.get(0).comm_name+ "\n"
                        + context.getResources().getString(R.string.Card_No) + " :" + printReceipt.rcId + "\n"
                        + context.getResources().getString(R.string.TransactionID) + " :" + printReceipt.receiptId + "\n"
                        + context.getResources().getString(R.string.Date) + " :" + date + "\n"
                        + context.getResources().getString(R.string.commodity) + " " + context.getResources().getString(R.string.lifted) + "   " + context.getResources().getString(R.string.rate) + "    " + context.getResources().getString(R.string.price) ;


                str3 = (add)+"";


                str4 = context.getResources().getString(R.string.Total_Amount) + " :" + totalAmount ;

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
                        + context.getResources().getString(R.string.FPS_Owner_Name) + "  :" + dealername + "\n"
                        + context.getResources().getString(R.string.FPS_No) + "          :" + dealerConstants.stateBean.statefpsId + "\n"//dealerConstants.stateBean.statefpsId
                        + context.getResources().getString(R.string.Name_of_Consumer) + ":" + printReceipt.printBeans.get(0).comm_name + "\n"
                        + context.getResources().getString(R.string.Card_No) + "        :" + printReceipt.rcId  + "\n"
                        + context.getResources().getString(R.string.TransactionID) + "   :" + printReceipt.receiptId + "\n"
                        + context.getResources().getString(R.string.Date) + "            :" + date + "\n"
                        + context.getResources().getString(R.string.commodity) + " " + context.getResources().getString(R.string.lifted) + "   " + context.getResources().getString(R.string.rate) + "    " + context.getResources().getString(R.string.price) + "\n"
                        + "________________________________\n";

                str3 = (add)
                        + "________________________________\n";

                str4 = context.getResources().getString(R.string.Total_Amount) + "    :" + totalAmount+ "\n"
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
    private void image(String content, String name,int align) {
        try {
            Util.image(content,name,align);
        } catch (IOException e) {
            e.printStackTrace();
            show_error_box(e.toString(),"Image formation Error", 0);
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
                            /*Intent i = new Intent(PrintActivity.this,CashPDSActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                            */
                            finish();

                        }else {
                            show_error_box(context.getResources().getString(R.string.Battery_Msg),context.getResources().getString(R.string.Battery),1);
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
                            public void run()
                            {
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

    private void show_error_box(String msg, String title, final int i) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage(msg);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton(context.getResources().getString(R.string.Ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        if (i==1) {
                            Intent home = new Intent(context, HomeActivity.class);
                            home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(home);
                            finish();
                        }
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
                if (bPrintResult==0){
                    Intent home = new Intent(context, HomeActivity.class);
                    home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(home);
                    finish();
                }
            }
        });

    }

    private void Display() {
        RecyclerView recyclerView;
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        recyclerView = findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        ArrayList<DataModel2> data= new ArrayList<>();
        int commDetailssize = memberConstants.commDetails.size();

        float commbal,commqty;
        String required;

        for (int i = 0; i < commDetailssize; i++) {
            commbal= Float.parseFloat((memberConstants.commDetails.get(i).balQty));
            commqty= Float.parseFloat((memberConstants.commDetails.get(i).requiredQty));
            required= String.valueOf(commbal-commqty);
            if (commqty>0.0) {
                data.add(new DataModel2(memberConstants.commDetails.get(i).commName +
                        "\n(" + memberConstants.commDetails.get(i).totQty + ")",
                        required,
                        memberConstants.commDetails.get(i).requiredQty,
                        memberConstants.commDetails.get(i).totalPrice));
            }
        }
        RecyclerView.Adapter adapter = new CustomAdapter2(this, data);
        recyclerView.setAdapter(adapter);

        String t= String.valueOf(TOTALAMOUNT);
        total.setText(t);
    }
}
