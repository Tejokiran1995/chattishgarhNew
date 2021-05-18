package com.visiontek.Mantra.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Typeface;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.visiontek.Mantra.Adapters.CustomAdapter6;
import com.visiontek.Mantra.Models.DataModel2;
import com.visiontek.Mantra.Models.fpsCommonInfo;
import com.visiontek.Mantra.Models.fpsPofflineToken;
import com.visiontek.Mantra.Models.fpsURLInfo;
import com.visiontek.Mantra.Models.stateBean;
import com.visiontek.Mantra.R;
import com.visiontek.Mantra.Utils.Aadhaar_Parsing;
import com.visiontek.Mantra.Utils.DatabaseHelper;
import com.visiontek.Mantra.Utils.Json_Parsing;
import com.visiontek.Mantra.Utils.TaskPrint;
import com.visiontek.Mantra.Utils.Util;

import org.w3c.dom.Document;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static com.visiontek.Mantra.Activities.Start.L;
import static com.visiontek.Mantra.Activities.Start.mp;
import static com.visiontek.Mantra.Utils.Util.ConsentForm;
import static com.visiontek.Mantra.Utils.Util.DEVICEID;
import static com.visiontek.Mantra.Utils.Util.RDservice;
import static com.visiontek.Mantra.Utils.Util.encrypt;
import static com.visiontek.Mantra.Utils.Util.networkConnected;
import static com.visiontek.Mantra.Utils.Util.releaseMediaPlayer;
import static com.visiontek.Mantra.Utils.Util.toast;
import static com.visiontek.Mantra.Utils.Veroeff.validateVerhoeff;

public class Inspection extends AppCompatActivity implements PrinterCallBack {
    fpsURLInfo fpsURLInfo=new fpsURLInfo();
    fpsCommonInfo fpsCommonInfo=new fpsCommonInfo();
    stateBean stateBean=new stateBean();
    fpsPofflineToken fpsPofflineToken=new fpsPofflineToken();

    private static String ACTION_USB_PERMISSION;
    public int nPrintCount = 1;
    public int nPrintContent = 0;
    public int nPrintWidth = 500;
    public int nCompressMethod = 0;
    public boolean bAutoPrint = false;
    String st;
    String xmplpid;
    int RD_SERVICE = 0;
    String type = "X";
    String errcode = "1";
    String errinfo = null;
    String fcount = null;
    String ftype = null;
    String icount = null;
    String itype = null;
    String pid = null;
    String skey = null;
    String ci = null;
    String hmac = null;
    String rdsId = null;
    String rdsVer = null;
    String dpId = null;
    String dc = null;
    String mi = null;
    String mc = null;
    String nmpoint = null;
    String qscore = null;
    String fCount;
    Context context;
    DatabaseHelper databaseHelper;
    ProgressDialog pd = null;
    RadioGroup radioGroup;
    RadioButton ok, seized;
    int select;
    ArrayList<DataModel2> data;
    ArrayList<String> closingBalance;
    ArrayList<String> commCode;
    ArrayList<String> commNameEn;
    ArrayList<String> commNamell;
    String DATA;
    Float textdata;
    float cb;
    Float var;
    String AFTERDATA;
    ArrayList<String> entered = new ArrayList<>();
    ArrayList<String> variation = new ArrayList<>();
    ArrayList<String> key = new ArrayList<>();
    ArrayList<String> value = new ArrayList<>();
    Button next, back;
    RecyclerView.Adapter adapter;
    RecyclerView recyclerView;
    String Ivendor, Iname;
    String com;
    String approval;
    String Iref;
    String Aadhaar;

    CheckBox checkBox;
    String Enter_UID;
    String Check;
    private Inspection mActivity;
    private final ExecutorService es = Executors.newScheduledThreadPool(30);
    private MTerminal100API mTerminal100API;
    private final String iCount = "0";
    private final String fType = "0";
    private final String iType = "0";
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                probe();
                // btnConnect.performClick();
                Toast.makeText(context, context.getResources().getString(R.string.ConnectUSB), Toast.LENGTH_LONG).show();
                //last.setEnabled(true);
                synchronized (this) {

                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_inspection);
        context = Inspection.this;

        TextView rd = findViewById(R.id.rd);
        checkBox = findViewById(R.id.check);
        boolean rd_fps;
        rd_fps = RDservice(context);
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.enable();
        if (rd_fps) {
            rd.setTextColor(context.getResources().getColor(R.color.green));
        } else {
            show_error_box(context.getResources().getString(R.string.RD_Service_Msg), context.getResources().getString(R.string.RD_Service), 0);
            rd.setTextColor(context.getResources().getColor(R.color.black));
        }
        pd = new ProgressDialog(context);

        databaseHelper = new DatabaseHelper(context);

        mActivity = this;
        ACTION_USB_PERMISSION = mActivity.getApplicationInfo().packageName;

        radioGroup = findViewById(R.id.groupradio);
        recyclerView = findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        closingBalance = databaseHelper.get_INSPECTION(0);
        commCode = databaseHelper.get_INSPECTION(1);
        commNameEn = databaseHelper.get_INSPECTION(2);
        commNamell = databaseHelper.get_INSPECTION(3);
        value = databaseHelper.get_APP(1);
        key = databaseHelper.get_APP(0);

        data = new ArrayList<>();

        for (int i = 0; i < commNameEn.size(); i++) {
            entered.add("0.0");
            variation.add("0.0");
            data.add(new DataModel2(commNameEn.get(i), closingBalance.get(i), entered.get(i), variation.get(i)));
        }

        Display();
        next = findViewById(R.id.inspection_next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fCount = "1";
                Enter_UID();
            }
        });

        back = findViewById(R.id.inspection_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            probe();
        } else {
            finish();
        }


    }


    private void hit_inspectioAuth(String inspectionAuth) {
        if (select == 2) {
            app("Seized");
        } else {
            app("OK");
        }
        pd = ProgressDialog.show(context, context.getResources().getString(R.string.COMMODITIES), context.getResources().getString(R.string.Commodity_details_are_updating), true, false);
        Aadhaar_Parsing request = new Aadhaar_Parsing(context, inspectionAuth, 6);
        request.setOnResultListener(new Aadhaar_Parsing.OnResultListener() {

            @Override
            public void onCompleted(String error, String msg, String ref, ArrayList<String> buffer) {
                if (pd.isShowing()) {
                    pd.dismiss();
                }
                if (!error.equals("00")) {
                    System.out.println("ERRORRRRRRRRRRRRRRRRRRRR");
                    show_error_box(msg, "Member Details: " + error, 0);
                } else {
                    Ivendor = buffer.get(1);
                    Iname = buffer.get(2);

                    System.out.println("SUCCESSSSSSSSSSSSS");

                    com = addComm();
                    if (!com.equals("1")) {
                        String Inspectionpush = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                                "<SOAP-ENV:Envelope\n" +
                                "    xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
                                "    xmlns:ns1=\"http://service.fetch.rationcard/\">\n" +
                                "    <SOAP-ENV:Body>\n" +
                                "        <ns1:inspPushCBData>\n" +
                                "            <fpsId>" + fpsCommonInfo.getfpsId() + "</fpsId>\n" +
                                "            <fpsSessionId>" + fpsCommonInfo.getfpsSessionId() + "</fpsSessionId>\n" +
                                "            <stateCode>" + stateBean.getstateCode() + "</stateCode>\n" +
                                "            <password>" + fpsURLInfo.gettoken() + "</password>\n" +
                                "            <approvalStatus>" + approval + "</approvalStatus>\n" +
                                com +
                                "            <inspUid>" + Enter_UID + "</inspUid>\n" +
                                "        </ns1:inspPushCBData>\n" +
                                "    </SOAP-ENV:Body>\n" +
                                "</SOAP-ENV:Envelope>";
                        if (Util.networkConnected(context)) {
                            hitpush(Inspectionpush);
                            Util.generateNoteOnSD(context, "InspectionPushReq.txt", Inspectionpush);
                        } else {
                            show_error_box(context.getResources().getString(R.string.Internet_Connection_Msg), context.getResources().getString(R.string.Internet_Connection), 0);
                        }
                    } else {
                        toast(context, context.getResources().getString(R.string.Invalid_Inputs));
                    }
                }
            }

        });
        request.execute();

    }

/*
    private boolean edited_com(int val) {

        if (place.size() > 0) {
            for (int i = 0; i < place.size(); i++) {
                if (val == place.get(i)) {

                    str = "<inspCBUpdate>\n" +
                            "                <closingBalance>" + updatedcb.get(i) + "</closingBalance>\n" +
                            "                <commCode>" + updatedcode.get(i) + "</commCode>\n" +
                            "                <observedClosingBalance>" + entered.get(i) + "</observedClosingBalance>\n" +
                            "                <variation>" + variation.get(i) + "</variation>\n" +
                            "</inspCBUpdate>\n";
                    add.append(str);
                }
            }
        }
        return false;
    }
*/

    private void ConsentDialog(String concent) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage(concent);
        alertDialogBuilder.setTitle(context.getResources().getString(R.string.Consent_Form));
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton(context.getResources().getString(R.string.Agree),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        connectRDservice();
                    }
                });
        alertDialogBuilder.setNegativeButton(context.getResources().getString(R.string.Disagree), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void ConsentformURL(String consentrequest) {
        pd = ProgressDialog.show(context, context.getResources().getString(R.string.Inspection), context.getResources().getString(R.string.Consent_Form), true, false);
        Json_Parsing request = new Json_Parsing(context, consentrequest, 3);
        request.setOnResultListener(new Json_Parsing.OnResultListener() {

            @Override
            public void onCompleted(String code, String msg) {
                if (pd.isShowing()) {
                    pd.dismiss();
                }
                if (!code.equals("00")) {
                    show_error_box(msg, code, 0);
                } else {
                    show_error_box(msg, code, 0);
                }
            }

        });

    }

    private void app(String ok) {
        for (int i = 0; i < value.size(); i++) {
            if (value.get(i).equals(ok)) {
                approval = key.get(i);
            }
        }
    }

    public void onRadioButtonClicked(View v) {
        ok = findViewById(R.id.ok);
        seized = findViewById(R.id.seized);
        boolean checked = ((RadioButton) v).isChecked();
        if (checked) {


            switch (v.getId()) {
                case R.id.ok:
                    select = 1;
                    ok.setTypeface(null, Typeface.BOLD_ITALIC);
                    seized.setTypeface(null, Typeface.NORMAL);

                    break;

                case R.id.seized:
                    select = 2;
                    ok.setTypeface(null, Typeface.NORMAL);
                    seized.setTypeface(null, Typeface.BOLD_ITALIC);

                    break;

            }
        }
    }

    private void hitpush(String inspectionpush) {
        pd = ProgressDialog.show(context, context.getResources().getString(R.string.COMMODITIES), context.getResources().getString(R.string.Commodity_details_are_updating), true, false);
        Aadhaar_Parsing request = new Aadhaar_Parsing(context, inspectionpush, 7);
        request.setOnResultListener(new Aadhaar_Parsing.OnResultListener() {

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onCompleted(String error, String msg, String ref, ArrayList<String> buffer) {
                if (pd.isShowing()) {
                    pd.dismiss();
                }
                if (!error.equals("00")) {
                    System.out.println("ERRORRRRRRRRRRRRRRRRRRRR");
                    show_error_box(msg, context.getResources().getString(R.string.Commodities_Error) + error, 0);
                } else {
                    Iref = ref;
                    if (Util.batterylevel(context)) {
                        show_error_box(msg, context.getResources().getString(R.string.Transaction_Success_Do_you_want_to_Print_Receipt), 1);
                    } else {
                        show_error_box(context.getResources().getString(R.string.Battery_Msg), context.getResources().getString(R.string.Battery), 0);
                    }
                }
            }


        });
        request.execute();
    }

    private String addComm() {
        StringBuilder add = new StringBuilder();
        String str;
        if (commNameEn.size() > 0) {
            for (int i = 0; i < commNameEn.size(); i++) {
                str = "<inspCBUpdate>\n" +
                        "                <closingBalance>" + closingBalance.get(i) + "</closingBalance>\n" +
                        "                <commCode>" + commCode.get(i) + "</commCode>\n" +
                        "                <observedClosingBalance>" + entered.get(i) + "</observedClosingBalance>\n" +
                        "                <variation>" + variation.get(i) + "</variation>\n" +
                        "</inspCBUpdate>\n";
                add.append(str);
            }
            return String.valueOf(add);
        } else {
            return "0";
        }
    }

    private void Enter_UID() {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        final EditText edittext = new EditText(context);
        alert.setMessage(context.getResources().getString(R.string.Please_Enter_a_Valid_Number_UID));
        alert.setTitle(context.getResources().getString(R.string.Enter_UID));
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        edittext.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(12);
        edittext.setFilters(FilterArray);
        alert.setView(edittext);
        alert.setPositiveButton(context.getResources().getString(R.string.Ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Enter_UID = edittext.getText().toString();
                if (Enter_UID.length() == 12 && validateVerhoeff(Enter_UID)) {
                    try {
                        Aadhaar = encrypt(Enter_UID, fpsPofflineToken.getskey());

                        if (Util.networkConnected(context)) {
                            if (checkBox.isChecked()) {

                                ConsentDialog(ConsentForm(context));
                            } else {
                                String currentDateTimeString = java.text.DateFormat.getDateTimeInstance().format(new Date());
                                currentDateTimeString = "23032021163452";
                                String consentrequest = "{\n" +
                                        "   \"fpsId\" : " + "\"" + stateBean.getstatefpsId() + "\"" + ",\n" +
                                        "   \"modeOfService\" : \"D\",\n" +
                                        "   \"moduleType\" : \"C\",\n" +
                                        "   \"rcId\" : " + "\"" + stateBean.getstatefpsId() + "\"" + ",\n" +
                                        "   \"requestId\" : \"0\",\n" +
                                        "   \"requestValue\" : \"N\",\n" +
                                        "   \"sessionId\" : " + "\"" + fpsCommonInfo.getfpsSessionId() + "\"" + ",\n" +
                                        "   \"stateCode\" : " + "\"" + stateBean.getstateCode() + "\"" + ",\n" +
                                        "   \"terminalId\" : " + "\"" + DEVICEID + "\"" + ",\n" +
                                        "   \"timeStamp\" : " + "\"" + currentDateTimeString + "\"" + ",\n" +
                                        /*"   \"token\" : " + "\"" + fpsURLInfo.token() + "\"" + ",\n" +*/
                                        "   \"token\" : "+"\"9f943748d8c1ff6ded5145c59d0b2ae7\""+"\n" +
                                        "}";
                                Util.generateNoteOnSD(context, "ConsentFormReq.txt", consentrequest);
                                ConsentformURL(consentrequest);
                            }
                        }

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
                    // connectRDservice();


                } else {
                    if (mp != null) {
                        releaseMediaPlayer(context, mp);
                    }
                    if (L.equals("hi")) {
                    } else {
                        mp = mp.create(context, R.raw.c100047);
                        mp.start();
                    }
                    show_error_box(context.getResources().getString(R.string.Please_enter_a_valid_Value), context.getResources().getString(R.string.Invalid_UID), 0);

                }
            }
        });
        alert.setNegativeButton(context.getResources().getString(R.string.Cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        alert.show();

    }

    private void connectRDservice() {
        try {
            if (mp != null) {
                releaseMediaPlayer(context, mp);
            }
            if (L.equals("hi")) {
                mp = mp.create(context, R.raw.c200032);
                mp.start();

            } else {
                mp = mp.create(context, R.raw.c100032);
                mp.start();

            }
            xmplpid = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                    "<PidOptions ver =\"1.0\">\n" +
                    "    <Opts env=\"P\" fCount=\"" + fCount + "\" iCount=\"" + iCount + "\" iType=\"" + iType + "\" fType=\"" + fType + "\" pCount=\"0\" pType=\"0\" format=\"0\" pidVer=\"2.0\" timeout=\"10000\" otp=\"\" wadh=\"\" posh=\"UNKNOWN\"/>\n" +
                    "</PidOptions>";
            Intent act = new Intent("in.gov.uidai.rdservice.fp.CAPTURE");
            PackageManager packageManager = getPackageManager();
            List<ResolveInfo> activities = packageManager.queryIntentActivities(act, PackageManager.MATCH_DEFAULT_ONLY);
            try {
                System.out.println(activities);
                for (int i = 0; i < activities.size(); i++) {
                    System.out.println(">  >>>>>>> i=" + i + "," + activities.get(i));
                }
            } catch (Exception e) {
                e.printStackTrace();
                String msg = String.valueOf(e);
                show_error_box(msg, context.getResources().getString(R.string.RD_SERVICE_ERROR), 0);
            }
            final boolean isIntentSafe = activities.size() > 0;
            System.out.println("Boolean check for activities = " + isIntentSafe);
            if (!isIntentSafe) {
                Toast.makeText(getApplicationContext(), context.getResources().getString(R.string.No_RD_Service_Available), Toast.LENGTH_SHORT).show();
            }
            System.out.println("No of activities = " + activities.size());
            act.putExtra("PID_OPTIONS", xmplpid);
            startActivityForResult(act, RD_SERVICE);
        } catch (Exception e) {
            System.out.println("Error while connecting to RDService");
            e.printStackTrace();
        }
    }

    private void xml_Frame() {
        String InspectionAuth = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<SOAP-ENV:Envelope\n" +
                "    xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
                "    xmlns:ns1=\"http://www.uidai.gov.in/authentication/uid-auth-request/2.0\"\n" +
                "    xmlns:ns2=\"http://service.fetch.rationcard/\">\n" +
                "    <SOAP-ENV:Body>\n" +
                "        <ns2:getAuthenticateNICAuaInspectionRD2>\n" +
                "            <fpsSessionId>" + fpsCommonInfo.getfpsSessionId() + "</fpsSessionId>\n" +
                "            <stateCode>" + stateBean.getstateCode() + "</stateCode>\n" +
                "            <Shop_no>" + stateBean.getstatefpsId() + "</Shop_no>\n" +
                "            <User_Id>" + fpsCommonInfo.getfpsId() + "</User_Id>\n" +
                "            <uidNumber>" + Aadhaar + "</uidNumber>\n" +
                "            <udc>" + DEVICEID + "</udc>\n" +
                "            <authMode>V</authMode>\n" +
                "            <auth_packet>\n" +
                "                <ns1:certificateIdentifier>" + ci + "</ns1:certificateIdentifier>\n" +
                "                <ns1:dataType>X</ns1:dataType>\n" +
                "                <ns1:dc>" + dc + "</ns1:dc>\n" +
                "                <ns1:dpId>" + dpId + "</ns1:dpId>\n" +
                "                <ns1:encHmac>" + hmac + "</ns1:encHmac>\n" +
                "                <ns1:mc>" + mc + "</ns1:mc>\n" +
                "                <ns1:mid>" + mi + "</ns1:mid>\n" +
                "                <ns1:rdId>" + rdsId + "</ns1:rdId>\n" +
                "                <ns1:rdVer>" + rdsVer + "</ns1:rdVer>\n" +
                "                <ns1:secure_pid>" + pid + "</ns1:secure_pid>\n" +
                "                <ns1:sessionKey>" + skey + "</ns1:sessionKey>\n" +
                "            </auth_packet>\n" +
                "            <password>" + fpsURLInfo.gettoken() + "</password>\n" +
                "            <Resp>\n" +
                "                <errCode>0</errCode>\n" +
                "                <errInfo>y</errInfo>\n" +
                "                <nmPoints>" + nmpoint + "</nmPoints>\n" +
                "                <fCount>" + fcount + "</fCount>\n" +
                "                <fType>" + ftype + "</fType>\n" +
                "                <iCount>" + icount + "</iCount>\n" +
                "                <iType>" + itype + "</iType>\n" +
                "                <pCount>0</pCount>\n" +
                "                <pType>0</pType>\n" +
                "                <qScore>" + qscore + "</qScore>\n" +
                "            </Resp>\n" +
                "        </ns2:getAuthenticateNICAuaInspectionRD2>\n" +
                "    </SOAP-ENV:Body>\n" +
                "</SOAP-ENV:Envelope>";


        Util.generateNoteOnSD(context, "InspectionAuthReq.txt", InspectionAuth);
        if (networkConnected(context)) {

            hit_inspectioAuth(InspectionAuth);
        } else {
            show_error_box(context.getResources().getString(R.string.Internet_Connection_Msg), context.getResources().getString(R.string.Internet_Connection), 0);
        }
    }

   /* private boolean replcae(int p) {

        System.out.println("p value = " + p);
        for (int i = 0; i < place.size(); i++) {
            System.out.println("I value = " + i);
            System.out.println("PLACE = " + place.get(i));
            System.out.println("PLACE SIZE = " + place.size());
            if (p == place.get(i)) {
                System.out.println("ENTERED SIZE = " + entered.size());
                System.out.println("REPLACING VALUES at " + i);
                entered.set(i, DATA);
                variation.set(i, AFTERDATA);
                updatedcb.set(i, closingBalance.get(p));
                updatedname.set(i, commNameEn.get(p));
                updatedcode.set(i, commNameEn.get(p));
                return true;
            }
        }
        return false;
    }*/

    private void EnterComm(final int p) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        final EditText edittext = new EditText(context);
        alert.setMessage(context.getResources().getString(R.string.Please_Enter_the_required_quantity));
        alert.setTitle(context.getResources().getString(R.string.Enter_Quantity));
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        edittext.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(12);
        edittext.setFilters(FilterArray);
        alert.setView(edittext);
        alert.setPositiveButton(context.getResources().getString(R.string.Ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Check = edittext.getText().toString();
                textdata = Float.parseFloat(Check);
                cb = Float.parseFloat(closingBalance.get(p));
                if (!Check.isEmpty() && textdata < cb && textdata > 0) {
                    var = (Float) (cb - textdata);
                    if (var > 0 && var < cb) {
                        System.out.println("----------------0");
                        AFTERDATA = String.valueOf(var);
                        DATA = String.valueOf(textdata);

                        entered.set(p, DATA);
                        variation.set(p, AFTERDATA);

                      /*  if (place.size() == 0 || !replcae(p)) {
                            System.out.println("-----------Place size great than 0-----1");
                            System.out.println("PLACE SIZE = " + place.size());
                            System.out.println("ENTERED SIZE = " + entered.size());
                            System.out.println("INSERTING VALUES ");
                            entered.add(DATA);
                            variation.add(AFTERDATA);
                            updatedcb.add(closingBalance.get(p));
                            updatedname.add(commNameEn.get(p));
                            updatedcode.add(commCode.get(p));
                            place.add(p);
                        }*/
                        data.clear();
                        for (int i = 0; i < commNameEn.size(); i++) {
                            data.add(new DataModel2(commNameEn.get(i), closingBalance.get(i), entered.get(i), variation.get(i)));

                        }
                        Display();
                    }
                } else {
                    show_error_box(context.getResources().getString(R.string.Please_enter_a_valid_Value), context.getResources().getString(R.string.Invalid_Quantity), 0);
                }
            }
        });
        alert.setNegativeButton(context.getResources().getString(R.string.Cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        alert.show();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("OnActivityResult");
        if (requestCode == RD_SERVICE) {
            if (resultCode == Activity.RESULT_OK) {
                System.out.println(data.getStringExtra("PID_DATA"));
                String piddata = data.getStringExtra("PID_DATA");
                int code = createAuthXMLRegistered(piddata);
                if (piddata != null && piddata.contains("errCode=\"0\"") && code == 0) {
                    System.out.println("PID DATA = " + piddata);
                    xml_Frame();
                } else {
                    show_error_box(errinfo, context.getResources().getString(R.string.PID_Exception), 0);
                    System.out.println("ERROR PID DATA = " + piddata);
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    public int createAuthXMLRegistered(String piddataxml) {

        try {
            InputStream is = new ByteArrayInputStream(piddataxml.getBytes());
            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            domFactory.setIgnoringComments(true);
            DocumentBuilder builder = domFactory.newDocumentBuilder();
            Document doc = builder.parse(is);

            errcode = doc.getElementsByTagName("Resp").item(0).getAttributes().getNamedItem("errCode").getTextContent();
            if (errcode.equals("1")) {
                errinfo = doc.getElementsByTagName("Resp").item(0).getAttributes().getNamedItem("errInfo").getTextContent();
                return 1;
            } else {
                icount = "0";
                itype = "0";
                qscore = "0";
                fcount = doc.getElementsByTagName("Resp").item(0).getAttributes().getNamedItem("fCount").getTextContent();
                System.out.println("FCOUNT = " + fcount);
                ftype = doc.getElementsByTagName("Resp").item(0).getAttributes().getNamedItem("fType").getTextContent();
                System.out.println("FTYPE = " + ftype);
                nmpoint = doc.getElementsByTagName("Resp").item(0).getAttributes().getNamedItem("nmPoints").getTextContent();
                System.out.println("NMPOINT = " + nmpoint);

                pid = doc.getElementsByTagName("Data").item(0).getTextContent();
                System.out.println("PID = " + pid);
                skey = doc.getElementsByTagName("Skey").item(0).getTextContent();
                System.out.println("SKEY = " + skey);
                ci = doc.getElementsByTagName("Skey").item(0).getAttributes().getNamedItem("ci").getTextContent();
                System.out.println("CI = " + ci);
                hmac = doc.getElementsByTagName("Hmac").item(0).getTextContent();
                System.out.println("HMAC = " + hmac);
                type = doc.getElementsByTagName("Data").item(0).getAttributes().getNamedItem("type").getTextContent();
                System.out.println("TYPE = " + type);
                dpId = doc.getElementsByTagName("DeviceInfo").item(0).getAttributes().getNamedItem("dpId").getTextContent();
                System.out.println("DPID = " + dpId);
                rdsId = doc.getElementsByTagName("DeviceInfo").item(0).getAttributes().getNamedItem("rdsId").getTextContent();
                System.out.println("RDSID = " + rdsId);
                rdsVer = doc.getElementsByTagName("DeviceInfo").item(0).getAttributes().getNamedItem("rdsVer").getTextContent();
                System.out.println("RDSVER = " + rdsVer);
                dc = doc.getElementsByTagName("DeviceInfo").item(0).getAttributes().getNamedItem("dc").getTextContent();
                System.out.println("DC = " + dc);
                mi = doc.getElementsByTagName("DeviceInfo").item(0).getAttributes().getNamedItem("mi").getTextContent();
                System.out.println("MI = " + mi);
                mc = doc.getElementsByTagName("DeviceInfo").item(0).getAttributes().getNamedItem("mc").getTextContent();
                System.out.println("MC = " + mc);
                skey = skey.replaceAll(" ", "\n");
                System.out.println("skey = " + skey);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("createAuthXMLRegistered error= " + e);
            errinfo = String.valueOf(e);
            return 2;
        }
        return 0;
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
                        if (i == 1) {
                            // print();
                        }
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }



   /* private void refresh(int p) {
        data.clear();
        for (int i = 0; i < commNameEn.size(); i++) {
            if (!check( i)) {
                data.add(new DataModel2(commNameEn.get(i), closingBalance.get(i), "ENTER", "--"));
            }
        }
        Display();
    }*/

   /* private boolean check(int i) {
        for (int j = 0; j < entered.size(); j++) {
            if (i == place.get(j)) {
                data.add(new DataModel2(commNameEn.get(i), closingBalance.get(i), entered.get(j), variation.get(j)));
                return true;
            }
        }
        return false;
    }*/

    private void image(String content, String name, int align) {
        try {
            Util.image(content, name, align);
        } catch (IOException e) {
            e.printStackTrace();
            show_error_box(e.toString(), "Image formation Error", 0);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void checkandprint(String[] str, int i) {
        if (Util.batterylevel(context) || Util.adapter(context)) {
            if (mp != null) {
                releaseMediaPlayer(context, mp);
            }
            if (L.equals("hi")) {
            } else {
                mp = mp.create(context, R.raw.c100191);
                mp.start();
            }
            es.submit(new TaskPrint(mTerminal100API, str, mActivity, context, i));

        } else {
            show_error_box(context.getResources().getString(R.string.Battery_Msg), context.getResources().getString(R.string.Battery), 0);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void print() {
        String currentDateTimeString = java.text.DateFormat.getDateTimeInstance().format(new Date());
        StringBuilder add = new StringBuilder();
        String app;
        for (int i = 0; i < commNameEn.size(); i++) {
            app = commNameEn.get(i) + "    " + closingBalance.get(i) + "    " + entered.get(i) + "    " + variation.get(i);
            add.append(app);

        }
        String str1, str2, str3, str4, str5;
        String[] str = new String[4];
        if (L.equals("hi")) {
            str1 = context.getResources().getString(R.string.Inspection_Receipt) + "\n";
            image(str1, "header.bmp", 1);
            str2 =    context.getResources().getString(R.string.FPS_ID) + fpsCommonInfo.getfpsId() + "\n"
                    + context.getResources().getString(R.string.TransactionID) + Iref + "\n\n"
                     + context.getResources().getString(R.string.Inspected_By) + Iname + "\n"
                    + context.getResources().getString(R.string.Designation) + Ivendor + "\n";
            str3 = context.getResources().getString(R.string.Date) + currentDateTimeString + "\n\n"
                    + context.getResources().getString(R.string.Status) + approval + " \n"
                    + context.getResources().getString(R.string.Commidity_CB_Obsevn_Varitn) + "\n";

            str4 = add + "";
                    image(str2 + str3 + str4 , "body.bmp", 0);
            str5 = context.getResources().getString(R.string.Note_Qualitys_in_KgsLtrs) + "\n";

                    image(str5, "tail.bmp", 1);
            str[0] = "1";
            str[1] = "1";
            str[2] = "1";
            str[3] = "1";
            checkandprint(str, 1);
        } else {

            str1 = context.getResources().getString(R.string.Inspection_Receipt) + "\n"
                    + "------------------------------------\n";
            str2 =     context.getResources().getString(R.string.FPS_ID) + fpsCommonInfo.getfpsId() + "\n"
                    + context.getResources().getString(R.string.TransactionID) + Iref + "\n\n"
           +context.getResources().getString(R.string.Inspected_By) + Iname + "\n"
                    + context.getResources().getString(R.string.Designation) + Ivendor + "\n";
            str3 = context.getResources().getString(R.string.Date) + currentDateTimeString + "\n\n"
                    + context.getResources().getString(R.string.Status) + approval + " \n"
                    + context.getResources().getString(R.string.Commidity_CB_Obsevn_Varitn) + "\n"
                    + "----------------------------\n";
            str4 = add
                    + "-----------------------------\n";
            str5 = context.getResources().getString(R.string.Note_Qualitys_in_KgsLtrs) + "\n";

            str[0] = "1";
            str[1] = str1;
            str[2] = str2+str3+str4;
            str[3] = str5;
            checkandprint(str, 0);

        }
    }


    private void Display() {
        adapter = new CustomAdapter6(this, data, new OnClickListener() {
            @Override
            public void onClick_d(int p) {
                EnterComm(p);
            }
        });
        recyclerView.setAdapter(adapter);
    }


    @Override
    public void OnOpen() {
        //last.setEnabled(true);
        // btnConnect.setEnabled(false);
        Toast.makeText(context, context.getResources().getString(R.string.CONNECTED), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void OnOpenFailed() {
        //last.setEnabled(false);
        //btnConnect.setEnabled(true);

        Toast.makeText(context, context.getResources().getString(R.string.Connection_Failed), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void OnClose() {
        //  last.setEnabled(false);
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
                //mActivity.last.setEnabled(bIsOpened);
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

                        //last.setEnabled(false);
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

    public interface OnClickListener {
        void onClick_d(int p);
    }


}
