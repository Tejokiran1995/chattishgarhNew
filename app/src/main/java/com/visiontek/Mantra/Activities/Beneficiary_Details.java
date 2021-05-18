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
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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
import com.visiontek.Mantra.Adapters.CustomAdapter4;
import com.visiontek.Mantra.Models.DataModel5;
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

import static com.visiontek.Mantra.Activities.Beneficiary_Verification.BEN_ID;
import static com.visiontek.Mantra.Activities.Beneficiary_Verification.BEN_zWadh;
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

public class Beneficiary_Details extends AppCompatActivity implements PrinterCallBack {

    private static String ACTION_USB_PERMISSION;
    public int nPrintCount = 1;
    public int nPrintContent = 0;
    public int nPrintWidth = 500;
    public int nCompressMethod = 0;
    public boolean bAutoPrint = false;
    String st;
    Button back, Ekyc;
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
    CheckBox checkBox;
    Context context;
    DatabaseHelper databaseHelper;
    ArrayList<String> memberId;
    ArrayList<String> memberName;
    ArrayList<String> memberNamell;
    ArrayList<String> member_fusion;
    ArrayList<String> uid;
    ArrayList<String> verification;
    ArrayList<String> verifyStatus_en;
    ArrayList<String> verifyStatus_ll;
    ArrayList<String> w_uid_status;
    RecyclerView.Adapter adapter;
    ProgressDialog pd = null;
    String MemID, Mfusion, W_uidwadh, Muid, Mname, Mverification;
    boolean click;
    String Enter_UID;
    String Aadhaar;
    String details;
    fpsPofflineToken fpsPofflineToken=new fpsPofflineToken();
    fpsCommonInfo fpsCommonInfo=new fpsCommonInfo();
    stateBean stateBean=new stateBean();
    fpsURLInfo fpsURLInfo=new fpsURLInfo();


    ArrayList<String> arrayListbuffer = new ArrayList<>();
    private Beneficiary_Details mActivity;
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

        setContentView(R.layout.activity_beneficiary__details);
        context = Beneficiary_Details.this;
        System.out.println("+++++++++++++++++++++++++++++++++++++BEN1");
        databaseHelper = new DatabaseHelper(context);
        pd = new ProgressDialog(context);
        checkBox = findViewById(R.id.check);
        TextView rd = findViewById(R.id.rd);
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
        mActivity = this;
        ACTION_USB_PERMISSION = mActivity.getApplicationInfo().packageName;

        click = false;
        memberId = databaseHelper.get_BEN(0);
        memberName = databaseHelper.get_BEN(2);
        memberNamell = databaseHelper.get_BEN(1);
        member_fusion = databaseHelper.get_BEN(3);
        uid = databaseHelper.get_BEN(4);
        verification = databaseHelper.get_BEN(5);
        verifyStatus_en = databaseHelper.get_BEN(6);
        verifyStatus_ll = databaseHelper.get_BEN(7);
        w_uid_status = databaseHelper.get_BEN(8);

        back = findViewById(R.id.Ben_details_back);
        Ekyc = findViewById(R.id.Ben_details_Ekyc);
        Ekyc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (click) {

                    if (Mverification.equals("N")) {
                        AadhaarDialog();
                    } else {
                        show_error_box(context.getResources().getString(R.string.Member_is_already_Verified), context.getResources().getString(R.string.Already_Verified), 0);
                    }
                } else {
                    toast(context, context.getResources().getString(R.string.Please_Select_a_Member));
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        RecyclerView recyclerView = findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        ArrayList<DataModel5> data = new ArrayList<>();
        System.out.println("+++++++++++++++++++++++++++++++++++++BEN");
        for (int i = 0; i < memberId.size(); i++) {
            data.add(new DataModel5(memberName.get(i), uid.get(i), w_uid_status.get(i)));
            System.out.println("DONEEEEEEEEEEEEEEEE1" + memberName + "  --- " + uid + "  ---" + w_uid_status);
        }
        adapter = new CustomAdapter4(context, data, new Dealer_Details.OnClickListener() {
            @Override
            public void onClick_d(int p) {
                click = true;
                MemID = memberId.get(p);
                Muid = uid.get(p);
                Mfusion = member_fusion.get(p);
                W_uidwadh = w_uid_status.get(p);
                Mname = memberName.get(p);
                Mverification = verification.get(p);

            }
        }, 1);
        recyclerView.setAdapter(adapter);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            probe();
        } else {
            finish();
        }
    }

    private void ConsentformURL(String consentrequest) {
        pd = ProgressDialog.show(context, context.getResources().getString(R.string.Beneficiary_Details), context.getResources().getString(R.string.Consent_Form), true, false);
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

    private void AadhaarDialog() {

        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        final EditText edittext = new EditText(context);
        alert.setMessage(context.getResources().getString(R.string.Please_Enter_Your_Aadhaar_ID));
        alert.setTitle(context.getResources().getString(R.string.Enter_UID));
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
                                        "   \"token\" : " + "\"9f943748d8c1ff6ded5145c59d0b2ae7\"" + "\n" +
                                        "}";
                                Util.generateNoteOnSD(context, "ConsentFormReq.txt", consentrequest);
                                ConsentformURL(consentrequest);
                            }
                        }

                        //ConsentDialog(ConsentForm(context));
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

                } else {
                    if (mp != null) {
                        releaseMediaPlayer(context, mp);
                    }
                    if (L.equals("hi")) {
                    } else {
                        mp = mp.create(context, R.raw.c100047);
                        mp.start();
                        toast(context, context.getResources().getString(R.string.Invalid_UID));
                    }
                }
            }
        });
        alert.setNegativeButton(context.getResources().getString(R.string.Cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        alert.show();

    }

    private void ConsentDialog(String concent) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage(concent);
        alertDialogBuilder.setTitle(context.getResources().getString(R.string.Consent_Form));
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton(context.getResources().getString(R.string.Agree),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        connectRDserviceEKYC(BEN_zWadh);
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

    private void hitURL1(String uid) {

        pd = ProgressDialog.show(context, context.getResources().getString(R.string.Beneficiary_Verification), context.getResources().getString(R.string.Processing), true, false);
        Aadhaar_Parsing request = new Aadhaar_Parsing(context, uid, 4);
        request.setOnResultListener(new Aadhaar_Parsing.OnResultListener() {

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onCompleted(String error, String msg, String ref, ArrayList<String> buffer) {
                if (pd.isShowing()) {
                    pd.dismiss();
                }

                if (!error.equals("E00")) {
                    System.out.println("ERRORRRRRRRRRRRRRRRRRRRR");
                    show_error_box(msg, context.getResources().getString(R.string.Member_Details) + error, 0);
                } else {
                    arrayListbuffer=buffer;
                    String currentDateTimeString = java.text.DateFormat.getDateTimeInstance().format(new Date());
                    details = "\n" + context.getResources().getString(R.string.MemberName) + buffer.get(0) + "\n" +
                            context.getResources().getString(R.string.DOB) + buffer.get(2) + "\n" +
                            context.getResources().getString(R.string.PindCode) + buffer.get(3) + "\n" +
                            context.getResources().getString(R.string.Gender) + buffer.get(4) + "\n" +
                            context.getResources().getString(R.string.Date) + currentDateTimeString + "\n";

                    if (Util.batterylevel(context)) {
                        show_error_box(msg + details, context.getResources().getString(R.string.Beneficiary_Verification) + error, 1);
                    } else {
                        show_error_box(context.getResources().getString(R.string.Battery_Msg), context.getResources().getString(R.string.Battery), 0);
                    }
                    System.out.println("SUCCESSSSSSSSSSSS");
                }
            }


        });
        request.execute();
    }

    private void connectRDserviceEKYC(String wadhvalue) {
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
            ;
            fCount = "2";
            xmplpid = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                    "<PidOptions ver =\"1.0\">\n" +
                    "    <Opts env=\"P\" fCount=\"" + fCount + "\" iCount=\"" + iCount + "\" iType=\"" + iType + "\" fType=\"" + fType + "\" pCount=\"0\" pType=\"0\" format=\"0\" pidVer=\"2.0\" timeout=\"10000\" otp=\"\" wadh=\"" + wadhvalue + "\" posh=\"UNKNOWN\"/>\n" +
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

                    prep_Mlogin();

                } else {
                    show_error_box(errinfo, context.getResources().getString(R.string.PID_Exception), 0);
                    System.out.println("ERROR PID DATA = " + piddata);
                }
            }
        }
    }

    private void prep_Mlogin() {
        String UID = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<SOAP-ENV:Envelope\n" +
                "    xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
                "    xmlns:ns1=\"http://service.fetch.rationcard/\">\n" +
                "    <SOAP-ENV:Body>\n" +
                "        <ns1:getEKYCAuthenticateRD>\n" +
                "            <fpsSessionId>" + fpsCommonInfo.getfpsSessionId() + "</fpsSessionId>\n" +
                "            <stateCode>" + stateBean.getstateCode() + "</stateCode>\n" +
                "            <Shop_no>" + stateBean.getstatefpsId() + "</Shop_no>\n" +
                "            <terminal_id>" + DEVICEID + "</terminal_id>\n" +
                "            <existingRCNumber>" + BEN_ID + "</existingRCNumber>\n" +
                "            <rcMemberName>" + Mname + "</rcMemberName>\n" +
                "            <rcUid>" + Muid + "</rcUid>\n" +
                "            <memberId>" + MemID + "</memberId>\n" +
                "            <ekycresAuth>\n" +
                "                <dc>" + dc + "</dc>\n" +
                "                <dpId>" + dpId + "</dpId>\n" +
                "                <mc>" + mc + "</mc>\n" +
                "                <mid>" + mi + "</mid>\n" +
                "                <rdId>" + rdsId + "</rdId>\n" +
                "                <rdVer>" + rdsVer + "</rdVer>\n" +
                "                <res_Consent_POIandPOA>Y</res_Consent_POIandPOA>\n" +
                "                <res_Consent_mobileOREmail>Y</res_Consent_mobileOREmail>\n" +
                "                <res_certificateIdentifier>" + ci + "</res_certificateIdentifier>\n" +
                "                <res_encHmac>" + hmac + "</res_encHmac>\n" +
                "                <res_secure_pid>" + pid + "</res_secure_pid>\n" +
                "                <res_sessionKey>" + skey + "</res_sessionKey>\n" +
                "                <res_uid>" + Aadhaar + "</res_uid>\n" +
                "            </ekycresAuth>\n" +
                "            <password>" + fpsURLInfo.gettoken() + "</password>\n" +
                "            <eKYCType>eKYCV</eKYCType>\n" +
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
                "        </ns1:getEKYCAuthenticateRD>\n" +
                "    </SOAP-ENV:Body>\n" +
                "</SOAP-ENV:Envelope>";
        if (networkConnected(context)) {
            if (mp != null) {
                releaseMediaPlayer(context, mp);
            }
            if (L.equals("hi")) {
            } else {
                mp = mp.create(context, R.raw.c100187);
                mp.start();
            }
            Util.generateNoteOnSD(context, "BenVerificationAuthReq.txt", UID);
            hitURL1(UID);
        } else {

            show_error_box(context.getResources().getString(R.string.Internet_Connection_Msg), context.getResources().getString(R.string.Internet_Connection), 0);

        }

    }

    private void show_error_box(String msg, String title, final int type) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage(msg);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton(context.getResources().getString(R.string.Ok),
                new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        if (type == 1) {
                            print();
                        }
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

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
        String str1, str2, str3;
        String[] str = new String[4];
        if (L.equals("hi")) {
            str1 = context.getResources().getString(R.string.VERIFICATION_RECEIPT) + "\n";
            image(str1, "header.bmp", 1);
            str2 = context.getResources().getString(R.string.Date) +" : "+ currentDateTimeString +
                    context.getResources().getString(R.string.Time) + " : " + currentDateTimeString +" \n" +
                    context.getResources().getString(R.string.FPS_ID) + " : " + fpsCommonInfo.getfpsId() + "\n"
                    + context.getResources().getString(R.string.NAME) + " : " + arrayListbuffer.get(1) + "\n\n"
                    + context.getResources().getString(R.string.Gender) + " : " + arrayListbuffer.get(4)+ "\n"
                    + context.getResources().getString(R.string.Ration_Card_Number) + " : " + "\n"
                    + context.getResources().getString(R.string.Status) + " : " + "Success" + "\n";

            image(str2, "body.bmp", 0);

            str[0] = "1";
            str[1] = "1";
            str[2] = "1";
            str[3] = "0";
            checkandprint(str, 1);
        } else {

            str1 = context.getResources().getString(R.string.VERIFICATION_RECEIPT) + "\n"
                    + "-----------------------------\n";
            str2 = context.getResources().getString(R.string.Date) +" : "+ currentDateTimeString +
                    context.getResources().getString(R.string.Time) + " : " +currentDateTimeString+ " \n" +
                    context.getResources().getString(R.string.FPS_ID) + " : " + fpsCommonInfo.getfpsId() + "\n"
                    + context.getResources().getString(R.string.NAME) + " : "+ arrayListbuffer.get(1) + "\n\n"
                    + context.getResources().getString(R.string.Gender) + " : " + arrayListbuffer.get(4)+"\n"
                    + context.getResources().getString(R.string.Ration_Card_Number) + " : " + "\n"
                    + context.getResources().getString(R.string.Status) + " : " + "Success" +"\n";

            str[0] = "1";
            str[1] = str1;
            str[2] = str2;
            str[3] = "0";
            checkandprint(str, 0);

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
        if (mp != null) {
            releaseMediaPlayer(context, mp);
        }
        if (L.equals("hi")) {
        } else {
            mp = mp.create(context, R.raw.c100078);
            mp.start();
        }
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

            }
        });

    }

}
