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
import android.graphics.Typeface;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.mantra.mTerminal100.MTerminal100API;
import com.mantra.mTerminal100.printer.PrinterCallBack;
import com.mantra.mTerminal100.printer.Prints;
import com.visiontek.chhattisgarhpds.Models.IssueModel.LastReceipt;
import com.visiontek.chhattisgarhpds.Models.IssueModel.MemberDetailsModel.GetURLDetails.Member;
import com.visiontek.chhattisgarhpds.Models.IssueModel.MemberDetailsModel.GetURLDetails.commDetails;
import com.visiontek.chhattisgarhpds.R;
import com.visiontek.chhattisgarhpds.Utils.DatabaseHelper;
import com.visiontek.chhattisgarhpds.Utils.TaskPrint;
import com.visiontek.chhattisgarhpds.Utils.Util;
import com.visiontek.chhattisgarhpds.Utils.XML_Parsing;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import static com.visiontek.chhattisgarhpds.Activities.DealerDetailsActivity.dealername;
import static com.visiontek.chhattisgarhpds.Activities.StartActivity.L;
import static com.visiontek.chhattisgarhpds.Activities.StartActivity.mp;
import static com.visiontek.chhattisgarhpds.Models.AppConstants.dealerConstants;
import static com.visiontek.chhattisgarhpds.Models.AppConstants.memberConstants;
import static com.visiontek.chhattisgarhpds.Models.AppConstants.menuConstants;
import static com.visiontek.chhattisgarhpds.Utils.Util.RDservice;
import static com.visiontek.chhattisgarhpds.Utils.Util.encrypt;
import static com.visiontek.chhattisgarhpds.Utils.Util.networkConnected;
import static com.visiontek.chhattisgarhpds.Utils.Util.releaseMediaPlayer;
import static com.visiontek.chhattisgarhpds.Utils.Veroeff.validateVerhoeff;


public class CashPDSActivity extends AppCompatActivity implements PrinterCallBack {
    public String Cash_ID;
    public static String ACTION_USB_PERMISSION;
    int select;
    String st;

    RadioGroup radioGroup;
    Context context;
    EditText id;
    Button home,last,get_details;
    RadioButton radiorc, radioaadhaar;
    ProgressDialog pd = null;
    TextView rd;
    String txnType;
    DatabaseHelper databaseHelper;

    private CashPDSActivity mActivity;
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
                last.setEnabled(true);
                synchronized (this) {
                }
            }
        }
    };

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash__p_d_s);
        context = CashPDSActivity.this;
        txnType = getIntent().getStringExtra("txnType");
        pd = new ProgressDialog(context);
        radioaadhaar = findViewById(R.id.radio_aadhaar);
        if(!txnType.equals("O")){
            radioaadhaar.setEnabled(false);
        }

        id = findViewById(R.id.id);
        home = findViewById(R.id.cash_pds_home);
        last = findViewById(R.id.cash_pds_lastreciept);
        get_details = findViewById(R.id.cash_pds_getdetails);

        radioGroup = findViewById(R.id.groupradio);
        databaseHelper = new DatabaseHelper(this);

        mActivity = this;
        ACTION_USB_PERMISSION = mActivity.getApplicationInfo().packageName;

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

        get_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cash_ID = id.getText().toString().trim();
                if (Cash_ID.length() == 12) {
                    if (txnType.equals("O") && Util.networkConnected(context))
                        member_details();
                    else {
                        DatabaseHelper databaseHelper = new DatabaseHelper(context);
                        int offlineEligibleFlag = databaseHelper.checkForOfflineDistribution();
                        if (!txnType.equals("O") || offlineEligibleFlag == 0) {
                            tryInOfflineMode(Cash_ID);
                        }
                        else
                            show_error_box(context.getResources().getString(R.string.Internet_Connection_Msg), context.getResources().getString(R.string.Internet_Connection));

                    }
                }else {
                    if (select==2){
                        show_error_box(context.getResources().getString(R.string.Please_Enter_a_Valid_Number_UID), context.getResources().getString(R.string.Invalid_UID));
                    }else {
                        show_error_box(context.getResources().getString(R.string.Please_Enter_a_Valid_Number_RC), context.getResources().getString(R.string.Invalid_ID));
                    }
                }
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Toast.makeText(getApplicationContext(), context.getResources().getString(R.string.Going_Back), Toast.LENGTH_SHORT).show();
            }
        });
        last.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                Cash_ID = id.getText().toString().trim();
                if (Cash_ID.length() == 12) {
                    lastReceipt_frame();
                } else {
                    if (select==2){
                        show_error_box(context.getResources().getString(R.string.Please_Enter_a_Valid_Number_UID), context.getResources().getString(R.string.Invalid_UID));
                    }else {
                        show_error_box(context.getResources().getString(R.string.Please_Enter_a_Valid_Number_RC), context.getResources().getString(R.string.Invalid_ID));
                    }
                }
            }
        });

        mTerminal100API = new MTerminal100API();
        mTerminal100API.initPrinterAPI(this, this);
        last.setEnabled(false);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            probe();
        } else {
            finish();
        }
    }


    public void tryInOfflineMode(final String rationCardNo)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                memberConstants = new Member();
                final List<commDetails> commDetails = databaseHelper.getCommodityDetails(rationCardNo);

                CashPDSActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(commDetails.size() > 0)
                        {
                            memberConstants.commDetails  = commDetails;
                            Intent intent = new Intent(getApplicationContext(), RationDetailsActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("txnType", txnType);
                            intent.putExtra("rationCardNo", Cash_ID);
                            startActivity(intent);
                            id.setText("");
                        }
                        else
                            show_error_box("Rc Number","Card not available");

                    }
                });

            }
        }).start();
    }

    private void hitURL_LastRecipt(String lastRecipt) {
        if (mp!=null) {
            releaseMediaPlayer(context,mp);
        }
        if(L.equals("hi")){}else {
            mp = mp.create(context, R.raw.c100075);
            mp.start();
        }
        pd = ProgressDialog.show(context, context.getResources().getString(R.string.Processing), context.getResources().getString(R.string.Fetching_Details), true, false);
        XML_Parsing request = new XML_Parsing(context, lastRecipt, 9);
        request.setOnResultListener(new XML_Parsing.OnResultListener() {



            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onCompleted(String isError, String msg, String ref, String flow, Object object) {
                if (pd.isShowing()) {
                    pd.dismiss();
                }

                if (isError == null || isError.isEmpty()){
                    show_error_box("Invalid Out put from Server","No Response");
                    return;
                }
                if (!isError.equals("00")) {
                    show_error_box(msg, context.getResources().getString(R.string.Member_Details) + isError);
                } else {
                    LastReceipt lastReceipt= (LastReceipt) object;

                    String app;
                    StringBuilder add = new StringBuilder();
                    int lastReceiptCommsize=lastReceipt.lastReceiptComm.size();
                    for (int i = 0; i < lastReceiptCommsize; i++) {
                        app = lastReceipt.lastReceiptComm.get(i).comm_name + "  " + "  " +
                                lastReceipt.lastReceiptComm.get(i).carry_over + "  " + "  " +
                                lastReceipt.lastReceiptComm.get(i).retail_price + "  " + "  " +
                                lastReceipt.lastReceiptComm.get(i).commIndividualAmount + "  " + "\n";
                        add.append(app);
                    }
                    System.out.println(lastReceipt.lastReceiptComm.get(0).transaction_time);
                    String date = lastReceipt.lastReceiptComm.get(0).transaction_time.substring(0, 19);
                    String month = lastReceipt.lastReceiptComm.get(0).transaction_time.substring(0, 19);
                    String year = lastReceipt.lastReceiptComm.get(0).transaction_time.substring(0, 19);
                    String str1,str2,str3,str4,str5;
                    String[] str = new String[4];
                    if (L.equals("hi")){
                        str1 =context.getResources().getString(R.string.LAST_RECEIPT);
                        image(str1,"header.bmp",1);
                        str2 =context.getResources().getString(R.string.FPS_Owner_Name) +" : "+dealername  + "\n"
                                + context.getResources().getString(R.string.FPS_No) +" : "+ dealerConstants.fpsCommonInfo.fpsId+ "\n"
                                + context.getResources().getString(R.string.Availed_FPS_No) + " : "+lastReceipt.lastReceiptComm.get(0).availedFps +"\n"
                                + context.getResources().getString(R.string.Name_of_Consumer)+" : " +lastReceipt.lastReceiptComm.get(0).member_name  + "\n"
                                + context.getResources().getString(R.string.Card_No)+" :" + lastReceipt.rcId + "\n"
                                + context.getResources().getString(R.string.TransactionID) +" :"+ lastReceipt.lastReceiptComm.get(0).reciept_id + "\n"
                                +context.getResources().getString(R.string.Date)+" :" + date + "\n"
                                + context.getResources().getString(R.string.AllotmentMonth)+ " : "+month +"\n"
                                + context.getResources().getString(R.string.AllotmentYear) + " : "+year +"\n"
                                +context.getResources().getString(R.string.commodity)+" "+context.getResources().getString(R.string.lifted)+"   "+context.getResources().getString(R.string.rate)+"    "+context.getResources().getString(R.string.price)+"\n";
                        str3 = (add)+"";
                        str4 = context.getResources().getString(R.string.Total_Amount)+"    :" + lastReceipt.lastReceiptComm.get(0).tot_amount ;
                        image(str2+str3+str4,"body.bmp",0);

                        str5 = context.getResources().getString(R.string.Public_Distribution_Dept)+"\n"
                                + context.getResources().getString(R.string.Note_Qualitys_in_KgsLtrs)+"\n\n";

                        image(str5,"tail.bmp",1);
                        str[0]="1";
                        str[1]="1";
                        str[2]="1";
                        str[3]="1";
                        checkandprint(str,1);
                    }else {
                        str1 =context.getResources().getString(R.string.LAST_RECEIPT)+"\n";

                        str2 ="\n________________________________\n"
                                + context.getResources().getString(R.string.FPS_Owner_Name) +"  :"+dealername  + "\n"
                                + context.getResources().getString(R.string.FPS_No) +"          :"+ dealerConstants.fpsCommonInfo.fpsId+ "\n"
                                + context.getResources().getString(R.string.Availed_FPS_No) + " : "+lastReceipt.lastReceiptComm.get(0).availedFps +"\n"
                                + context.getResources().getString(R.string.Name_of_Consumer)+":" + lastReceipt.lastReceiptComm.get(0).member_name + "\n"
                                + context.getResources().getString(R.string.Card_No)+"        :" + lastReceipt.rcId + "\n"
                                + context.getResources().getString(R.string.TransactionID) +"   :"+ lastReceipt.lastReceiptComm.get(0).reciept_id + "\n"
                                +context.getResources().getString(R.string.Date)+"            :" + date + "\n"
                                + context.getResources().getString(R.string.AllotmentMonth)+ " : "+month +"\n"
                                + context.getResources().getString(R.string.AllotmentYear) + " : "+year +"\n"
                                +context.getResources().getString(R.string.commodity)+" "+context.getResources().getString(R.string.lifted)+"   "+context.getResources().getString(R.string.rate)+"    "+context.getResources().getString(R.string.price)+"\n"
                                + "________________________________\n";

                        str3 = (add)
                                + "________________________________\n";

                        str4 = context.getResources().getString(R.string.Total_Amount)+"    :" + lastReceipt.lastReceiptComm.get(0).tot_amount + "\n"
                                + "________________________________\n";


                        str5 = context.getResources().getString(R.string.Public_Distribution_Dept)+"\n"
                                + context.getResources().getString(R.string.Note_Qualitys_in_KgsLtrs)+"\n\n\n\n";

                        str[0]="1";
                        str[1]=str1;
                        str[2]=str2+str3+str4;
                        str[3]=str5;
                        checkandprint(str,0);
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
    private void checkandprint(String[] str, int i) {
        if (Util.batterylevel(context)|| Util.adapter(context)) {
            if (mp!=null) {
                releaseMediaPlayer(context,mp);
            }
            if(L.equals("hi")){}else {
                mp = mp.create(context, R.raw.c100191);
                mp.start();
            }
            es.submit(new TaskPrint(mTerminal100API,str,mActivity,context,i));
            id.setText("");
        }else {
            show_error_box(context.getResources().getString(R.string.Battery_Msg),context.getResources().getString(R.string.Battery));
        }
    }

    private void member_details() {

        String members = null;

        if (select == 2) {
            String Cash_Aadhaar = null;
            if (validateVerhoeff(Cash_ID)) {
                try {
                    Cash_Aadhaar = encrypt(Cash_ID, menuConstants.skey);
                } catch (BadPaddingException e) {
                    e.printStackTrace();
                } catch (IllegalBlockSizeException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                } catch (NoSuchPaddingException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                members = "<SOAP-ENV:Envelope\n" +
                        "    xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
                        "    xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\"\n" +
                        "    xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n" +
                        "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                        "    xmlns:ns1=\"http://service.fetch.rationcard/\">\n" +
                        "    <SOAP-ENV:Body>\n" +
                        "        <ns1:getePDSRationCardDetails>\n" +
                        "            <fpsSessionId>" + dealerConstants.fpsCommonInfo.fpsSessionId + "</fpsSessionId>\n" +
                        "            <stateCode>" + dealerConstants.stateBean.stateCode + "</stateCode>\n" +
                        "            <shop_no>" + dealerConstants.stateBean.statefpsId + "</shop_no>\n" +
                        "            <password>" +dealerConstants. fpsURLInfo.token + "</password>\n" +
                        "            <hts></hts>\n" +
                        "            <id>" + Cash_Aadhaar + "</id>\n" +
                        "            <idType>U</idType>\n" +
                        "            <mode>CA</mode>\n" +
                        "        </ns1:getePDSRationCardDetails>\n" +
                        "    </SOAP-ENV:Body>\n" +
                        "</SOAP-ENV:Envelope>";
                Util.generateNoteOnSD(context, "MemberDetailswithUID.txt", members);
            }else {
                if (mp!=null) {
                    releaseMediaPlayer(context,mp);
                }
                if(L.equals("hi")){}else {
                    mp = mp.create(context, R.raw.c100047);
                    mp.start();
                }
                show_error_box(context.getResources().getString(R.string.Please_Enter_Valid_Number), context.getResources().getString(R.string.Invalid_UID));
            }
        } else {
            members = "<?xml version='1.0' encoding='UTF-8' standalone='no' ?>\n" +
                    "<SOAP-ENV:Envelope\n" +
                    "    xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
                    "    xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\"\n" +
                    "    xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n" +
                    "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                    "    xmlns:ns1=\"http://service.fetch.rationcard/\">\n" +
                    "    <SOAP-ENV:Body>\n" +
                    "        <ns1:getePDSRationCardDetails>\n" +
                    "            <fpsSessionId>" + dealerConstants.fpsCommonInfo.fpsSessionId + "</fpsSessionId>\n" +
                    "            <stateCode>" + dealerConstants.stateBean.stateCode + "</stateCode>\n" +
                    "            <shop_no>" + dealerConstants.stateBean.statefpsId + "</shop_no>\n" +
                    "            <password>" + dealerConstants.fpsURLInfo.token + "</password>\n" +
                    "            <hts></hts>\n" +
                    "            <id>" +Cash_ID + "</id>\n" +
                    "            <idType>R</idType>\n" +
                    "            <mode>CL</mode>\n" +
                    "        </ns1:getePDSRationCardDetails>\n" +
                    "    </SOAP-ENV:Body>\n" +
                    "</SOAP-ENV:Envelope>";
            Util.generateNoteOnSD(context, "MemberDetailswithRC.txt", members);
        }
        if (networkConnected(context)) {
            hitURL(members);
        } else {
            show_error_box(context.getResources().getString(R.string.Internet_Connection_Msg),context.getResources().getString(R.string.Internet_Connection));
        }
    }

    private void hitURL(String members) {
        if (mp!=null) {
            releaseMediaPlayer(context,mp);
        }
       if(L.equals("hi")) {
          mp = mp.create(context, R.raw.c200183);
                  mp.start();
       }
        else {
           mp = mp.create(context, R.raw.c100183);
                   mp.start();
       }
        pd = ProgressDialog.show(context, context.getResources().getString(R.string.Members), context.getResources().getString(R.string.Fetching_Members), true, false);
        XML_Parsing request = new XML_Parsing(context, members, 3);
        request.setOnResultListener(new XML_Parsing.OnResultListener() {

            @Override
            public void onCompleted(String isError, String msg, String ref, String flow, Object object) {
                if (pd.isShowing()) {
                    pd.dismiss();
                }
                if (isError == null || isError.isEmpty()){
                    show_error_box("Invalid Out put from Server","No Response");
                    return;
                }
                if (!isError.equals("00")) {
                    show_error_box(msg, context.getResources().getString(R.string.Member_Details) + isError);
                } else {
                    Intent members = new Intent(getApplicationContext(), MemberDetailsActivity.class);
                    members.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    members.putExtra("txnType",txnType);
                    startActivity(members);
                    id.setText("");
                }
            }
        });
        request.execute();
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

    public void onRadioButtonClicked(View v) {
        radiorc = findViewById(R.id.radio_rc_no);
        radioaadhaar = findViewById(R.id.radio_aadhaar);
        boolean checked = ((RadioButton) v).isChecked();
        if (checked) {
            switch (v.getId()) {
                case R.id.radio_rc_no:
                    if (mp!=null) {
                        releaseMediaPlayer(context,mp);
                    }
                    if(L.equals( "hi")) {
                       mp=mp.create(context, R.raw.c200043);
                               mp.start();
                    }
                    else {
                       mp=mp.create(context, R.raw.c100043);
                               mp.start();
                    }
                    select = 1;
                    radiorc.setTypeface(null, Typeface.BOLD_ITALIC);
                    radioaadhaar.setTypeface(null, Typeface.NORMAL);
                    id.setText("");
                    Toast.makeText(context, context.getResources().getString(R.string.Please_Enter_Your_Ration_ID), Toast.LENGTH_SHORT).show();
                    break;

                case R.id.radio_aadhaar:
                    select = 2;
                    radiorc.setTypeface(null, Typeface.NORMAL);
                    radioaadhaar.setTypeface(null, Typeface.BOLD_ITALIC);
                    id.setText("");
                    Toast.makeText(context, context.getResources().getString(R.string.Please_Enter_Your_Aadhaar_ID), Toast.LENGTH_SHORT).show();
                    break;

            }
        }
    }

    private void lastReceipt_frame() {
        String lastRecipt = null;

        if (select == 2) {
            String Cash_Aadhaar=null;
            if (validateVerhoeff(Cash_ID)) {
                try {
                    Cash_Aadhaar = encrypt(Cash_ID, menuConstants.skey);
                } catch (BadPaddingException e) {
                    e.printStackTrace();
                } catch (IllegalBlockSizeException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                } catch (NoSuchPaddingException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }

                lastRecipt = "<?xml version='1.0' encoding='UTF-8' standalone='no' ?>\n" +
                        "<SOAP-ENV:Envelope\n" +
                        "    xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
                        "    xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\"\n" +
                        "    xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n" +
                        "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                        "    xmlns:ns1=\"http://service.fetch.rationcard/\">\n" +
                        "    <SOAP-ENV:Body>\n" +
                        "        <ns1:getReprintDetails>\n" +
                        "            <exiting_ration_card>" + Cash_Aadhaar + "</exiting_ration_card>\n" +
                        "            <Shop_no>" + dealerConstants.stateBean.statefpsId + "</Shop_no>\n" +
                        "            <idType>U</idType>\n" +
                        "            <token>" + dealerConstants.fpsURLInfo.token + "</token>\n" +
                        "            <fpsSessionId>" + dealerConstants.fpsCommonInfo.fpsSessionId + "</fpsSessionId>\n" +
                        "            <stateCode>" + dealerConstants.stateBean.stateCode + "</stateCode>\n" +
                        "        </ns1:getReprintDetails>\n" +
                        "    </SOAP-ENV:Body>\n" +
                        "</SOAP-ENV:Envelope>";
            }else {
                if (mp!=null) {
                    releaseMediaPlayer(context,mp);
                }
                if(L.equals("hi")){}else {
                    mp = mp.create(context, R.raw.c100047);
                    mp.start();
                }
                show_error_box(context.getResources().getString(R.string.Please_Enter_Valid_Number), context.getResources().getString(R.string.Invalid_UID));
            }
        } else {
            lastRecipt = "<?xml version='1.0' encoding='UTF-8' standalone='no' ?>\n" +
                    "<SOAP-ENV:Envelope\n" +
                    "    xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
                    "    xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\"\n" +
                    "    xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n" +
                    "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                    "    xmlns:ns1=\"http://service.fetch.rationcard/\">\n" +
                    "    <SOAP-ENV:Body>\n" +
                    "        <ns1:getReprintDetails>\n" +
                    "            <exiting_ration_card>" + Cash_ID + "</exiting_ration_card>\n" +
                    "            <Shop_no>" + dealerConstants.stateBean.statefpsId + "</Shop_no>\n" +
                    "            <idType>R</idType>\n" +
                    "            <token>" +dealerConstants. fpsURLInfo.token + "</token>\n" +
                    "            <fpsSessionId>" + dealerConstants.fpsCommonInfo.fpsSessionId + "</fpsSessionId>\n" +
                    "            <stateCode>" + dealerConstants.stateBean.stateCode + "</stateCode>\n" +
                    "        </ns1:getReprintDetails>\n" +
                    "    </SOAP-ENV:Body>\n" +
                    "</SOAP-ENV:Envelope>";

        }
        if (networkConnected(context)) {
            Util.generateNoteOnSD(context, "LastReceiptReq.txt", lastRecipt);
            hitURL_LastRecipt(lastRecipt);
        } else {
            show_error_box(context.getResources().getString(R.string.Internet_Connection_Msg),context.getResources().getString(R.string.Internet_Connection));
        }
    }

    @Override
    public void OnOpen() {
        last.setEnabled(true);
        // btnConnect.setEnabled(false);
        Toast.makeText(context, context.getResources().getString(R.string.CONNECTED), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void OnOpenFailed() {
        last.setEnabled(false);
        //btnConnect.setEnabled(true);
        if (mp!=null) {
            releaseMediaPlayer(context,mp);
        }
        if(L.equals("hi")){}else {
            mp = mp.create(context, R.raw.c100078);
            mp.start();
        }
       Toast.makeText(context, context.getResources().getString(R.string.Connection_Failed), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void OnClose() {
        last.setEnabled(false);
        // btnConnect.setEnabled(true);
        if (mUsbReceiver != null) {
            context.unregisterReceiver(mUsbReceiver);
        }
        // If Close is caused because the printer is turned off. Then you need to re-enumerate it here.
        probe();
    }

    @Override
    public void OnPrint(final int bPrintResult, final boolean bIsOpened) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                Toast.makeText(context.getApplicationContext(), (bPrintResult == 0) ? getResources().getString(R.string.printsuccess) : getResources().getString(R.string.printfailed) + " " + Prints.ResultCodeToString(bPrintResult), Toast.LENGTH_SHORT).show();
                mActivity.last.setEnabled(bIsOpened);
            }
        });

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

                        last.setEnabled(false);
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
}

