package com.visiontek.Mantra.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

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

import com.visiontek.Mantra.Adapters.CustomAdapter;
import com.visiontek.Mantra.Models.DataModel;
import com.visiontek.Mantra.Models.Ekyc;
import com.visiontek.Mantra.Models.cardDetails;
import com.visiontek.Mantra.Models.fpsCommonInfo;
import com.visiontek.Mantra.Models.fpsPofflineToken;
import com.visiontek.Mantra.Models.fpsURLInfo;
import com.visiontek.Mantra.Models.stateBean;
import com.visiontek.Mantra.R;
import com.visiontek.Mantra.Utils.DatabaseHelper;
import com.visiontek.Mantra.Utils.Json_Parsing;
import com.visiontek.Mantra.Utils.Util;
import com.visiontek.Mantra.Utils.XML_Parsing;

import org.w3c.dom.Document;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import static com.visiontek.Mantra.Activities.Cash_PDS.Cash_Aadhaar;
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

public class Member_Details extends AppCompatActivity {
    fpsURLInfo fpsURLInfo=new fpsURLInfo();
    fpsCommonInfo fpsCommonInfo=new fpsCommonInfo();
    stateBean stateBean=new stateBean();
    fpsPofflineToken fpsPofflineToken=new fpsPofflineToken();
    public static String MUid, MName, Mmemid, Mfinger, Mwadh, Mmanual, Mfusion;
    public static int Mdealer;


    Button scanfp, back;

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

    CheckBox checkBox;
    boolean mclick;

    ProgressDialog pd = null;
    ArrayList<String> mname;
    ArrayList<String> muid;
    ArrayList<String> mfinger;
    ArrayList<String> miris;
    ArrayList<String> mmemid;
    ArrayList<String> mwadh;
    ArrayList<String> mmanual;
    ArrayList<String> mfusion;
    DatabaseHelper databaseHelper;
    TextView rd;
    Context context;
    int RD_SERVICE = 0;
    boolean mMan, mBIO, mDeal;

    String MEMBER_AUTH_TYPE;

    boolean wadhverify;

    int wadhflag, FIRflag, Fusionflag;
    //int ekycflag = 0;
    int fusionflag;
    int EKYC;
    String Enter_UID;
    String Aadhaar;

    private String fCount = "0";
    private String fType = "0";
    private String iCount = "0";
    private String iType = "0";



    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member__details);

        context = Member_Details.this;
        pd = new ProgressDialog(context);
        scanfp = findViewById(R.id.member_scanFP);
        back = findViewById(R.id.member_back);
        checkBox = findViewById(R.id.mcheck);


        rd = findViewById(R.id.rd);

        boolean  rd_fps;
        rd_fps = RDservice(context);
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.enable();
        if (rd_fps) {
            rd.setTextColor(context.getResources().getColor(R.color.green));
        } else {
            show_error_box(context.getResources().getString(R.string.RD_Service_Msg),context.getResources().getString(R.string.RD_Service),0);
            rd.setTextColor(context.getResources().getColor(R.color.black));
        }


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        RecyclerView recyclerView = findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(Member_Details.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        scanfp.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                if (MUid.equals("NA")){
                    show_error_box(context.getResources().getString(R.string.NA_MSG), context.getResources().getString(R.string.NA), 0);
                    return;
                }
                if (mclick) {
                    if (networkConnected(context)) {
                        if (checkBox.isChecked()) {
                            ConsentDialog(ConsentForm(context));
                        } else {
                            String currentDateTimeString = java.text.DateFormat.getDateTimeInstance().format(new Date());
                            currentDateTimeString="23032021163452";
                            String consentrequest="{\n" +
                                    "   \"fpsId\" : "+"\""+stateBean.getstatefpsId()+"\""+",\n" +
                                    "   \"modeOfService\" : \"D\",\n" +
                                    "   \"moduleType\" : \"C\",\n" +
                                    "   \"rcId\" : "+"\""+stateBean.getstatefpsId()+"\""+",\n" +
                                    "   \"requestId\" : \"0\",\n" +
                                    "   \"requestValue\" : \"N\",\n" +
                                    "   \"sessionId\" : "+"\""+fpsCommonInfo.getfpsSessionId()+"\""+",\n" +
                                    "   \"stateCode\" : "+"\""+stateBean.getstateCode()+"\""+",\n" +
                                    "   \"terminalId\" : "+"\""+DEVICEID+"\""+",\n" +
                                    "   \"timeStamp\" : "+"\""+currentDateTimeString+"\""+",\n" +
                                   /* "   \"token\" : "+"\""+fpsURLInfo.token()+"\""+"\n" +*/
                                    "   \"token\" : "+"\"9f943748d8c1ff6ded5145c59d0b2ae7\""+"\n" +
                                    "}";
                            Util.generateNoteOnSD(context, "ConsentFormReq.txt", consentrequest);
                            ConsentformURL(consentrequest);
                            //show_error_box(context.getResources().getString(R.string.Please_check_Consent_Form), context.getResources().getString(R.string.Consent_Form), 0);
                        }
                    } else {
                        show_error_box(context.getResources().getString(R.string.Internet_Connection_Msg),context.getResources().getString(R.string.Internet_Connection),0);
                    }
                } else {
                    if (mp!=null) {
                        releaseMediaPlayer(context,mp);
                    }
                    if (L.equals("hi")) {
                    } else {
                        mp = mp.create(context, R.raw.c100065);
                        mp.start();
                    }
                    show_error_box(context.getResources().getString(R.string.Please_Select_Member_Name), context.getResources().getString(R.string.Member), 0);
                }
            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setMessage(context.getResources().getString(R.string.Do_you_want_to_cancel_Session));
                alertDialogBuilder.setCancelable(false);
                alertDialogBuilder.setPositiveButton(context.getResources().getString(R.string.Yes),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                finish();
                            }
                        });
                alertDialogBuilder.setNegativeButton(context.getResources().getString(R.string.No),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {

                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        databaseHelper = new DatabaseHelper(context);
        mname = databaseHelper.get_MD(0);
        muid = databaseHelper.get_MD(1);
        mfinger = databaseHelper.get_MD(2);
        miris = databaseHelper.get_MD(3);
        mmemid = databaseHelper.get_MD(4);
        mwadh = databaseHelper.get_MD(10);
        mmanual = databaseHelper.get_MD(8);
        mfusion = databaseHelper.get_MD(6);

        ArrayList<DataModel> data = new ArrayList<>();
        for (int i = 0; i < mname.size(); i++) {
            data.add(new DataModel(mname.get(i), muid.get(i)));
        }
        RecyclerView.Adapter adapter = new CustomAdapter(context, data, new OnClickListener() {
            @Override
            public void onClick_d(int p) {
                mclick = true;
                Fusionflag = 0;
                wadhflag = 0;
                FIRflag = 0;
                fusionflag = 0;

                MName = mname.get(p);
                MUid = muid.get(p);
                Mmemid = mmemid.get(p);

                Mfinger = mfinger.get(p);
                Mwadh = mwadh.get(p);
                Mmanual = mmanual.get(p);
                Mfusion = mfusion.get(p);

                if (Mfinger.equals("Y")) {
                    mBIO = true;
                    mMan = false;
                    mDeal = false;
                } else if (Mmanual.equals("M")) {
                    mMan = true;
                    mBIO = false;
                    mDeal = false;
                } else if (Mmanual.equals("D")) {
                    mDeal = true;
                    mBIO = false;
                    mMan = false;
                }
            }
        }, 1);
        recyclerView.setAdapter(adapter);
    }

    private void ConsentformURL(String consentrequest) {
        pd = ProgressDialog.show(context, context.getResources().getString(R.string.Member), context.getResources().getString(R.string.Consent_Form), true, false);
        Json_Parsing request = new Json_Parsing(context, consentrequest, 3);
        request.setOnResultListener(new Json_Parsing.OnResultListener() {

            @Override
            public void onCompleted(String code, String msg) {
                if (pd.isShowing()) {
                    pd.dismiss();
                }
                if (!code.equals("00")) {
                    show_error_box(msg,  code,0);
                } else {
                    show_error_box(msg,  code,0);
                }
            }

        });

    }

    private void show_error_box(String msg, String title, final int type) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage(msg);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton(context.getResources().getString(R.string.Ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        if (type == 1) {
                            callScanFP();
                        }else if (type==2){
                            AadhaarDialog();
                        }else if (type==3){
                            Intent ration = new Intent(context, Ration_details.class);
                            ration.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(ration);
                            finish();
                        }

                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
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
                        Fusionflag = 0;
                        wadhflag = 0;
                        FIRflag = 0;
                        fusionflag = 0;

                        fCount="1";
                        callScanFP();
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
    cardDetails cardDetails=new cardDetails();
    private void callScanFP() {
        if (mBIO) {
            MEMBER_AUTH_TYPE = "Bio";
            if (Mwadh.equals("Y")) {
                System.out.println("In MEMBER AUTH WADH_EKYC Request");
                connectRDserviceEKYC(cardDetails.getzwadh());
            } else {
                if (Mfusion.equals("0")) {
                    fCount = "2";
                }
                System.out.println("In MEMBER AUTH NORMAL Request");
                connectRDservice();
            }
        } else if (mMan) {
            System.out.println("Requesting Manual Authentication");
            ManualAuth();
        } else if (mDeal) {
            System.out.println("Requesting Dealer Authentication");
            DealerAuth();
        } else {
            show_error_box(context.getResources().getString(R.string.Authentication_Type_Not_Specified), context.getResources().getString(R.string.Authentication_Type), 0);
        }
    }

    private void DealerAuth() {
        Mdealer = 1;
        Intent dealer = new Intent(getApplicationContext(), Dealer_Details.class);
        dealer.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityForResult(dealer, 2);
    }

    private void ManualAuth() {
        String manual = "<soapenv:Envelope\n" +
                "    xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
                "    xmlns:ser=\"http://service.fetch.rationcard/\">\n" +
                "    <soapenv:Header/>\n" +
                "    <soapenv:Body>\n" +
                "        <ser:ackRequest>\n" +
                "            <fpsCard>" + cardDetails.getRcId() + "</fpsCard >\n" +
                "            <terminalId>" + DEVICEID + "</terminalId>\n" +
                "            <user_password>" + fpsCommonInfo.getdealer_password() + "</user_password>\n" +
                "            <fpsSessionId>" + fpsCommonInfo.getfpsSessionId() + "</fpsSessionId>\n" +
                "            <uidNumber>" + MUid + "</uidNumber>\n" +
                "            <token>" + fpsURLInfo.gettoken() + "</token>\n" +
                "            <auth_type>M</auth_type>\n" +
                "            <user_type>B</user_type>\n" +
                "            <memberId>" + Mmemid + "</memberId>\n" +
                "            <fpsId>" + fpsCommonInfo.getfpsId() + "</fpsId>\n" +
                "            <stateCode>" + stateBean.getstateCode() + "</stateCode>\n" +
                "        </ser:ackRequest>\n" +
                "    </soapenv:Body>\n" +
                "</soapenv:Envelope>";
        Util.generateNoteOnSD(context, "ManualRes.txt", manual);
        if (networkConnected(context)) {
            hitManual(manual);
        } else {
            show_error_box(context.getResources().getString(R.string.Internet_Connection_Msg),context.getResources().getString(R.string.Internet_Connection),0);
        }
    }

    private void hitManual(String manual) {
        pd = ProgressDialog.show(context, context.getResources().getString(R.string.Dealer), context.getResources().getString(R.string.Authenticating), true, false);
        XML_Parsing request = new XML_Parsing(Member_Details.this, manual, 7);
        request.setOnResultListener(new XML_Parsing.OnResultListener() {

            @Override
            public void onCompleted(String isError, String msg, String ref, String flow) {
                if (pd.isShowing()) {
                    pd.dismiss();
                }

                if (!isError.equals("00")) {
                    show_error_box(msg, context.getResources().getString(R.string.Member_EKYC) + isError, 0);
                } else {
                    Intent ration = new Intent(context, Ration_details.class);
                    ration.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(ration);
                    finish();

                }

            }
        });
        request.execute();

    }

    private void hitURL1(String xmlformat) {
        pd = ProgressDialog.show(context, context.getResources().getString(R.string.Member_Authentication), context.getResources().getString(R.string.Processing), true, false);
        XML_Parsing request = new XML_Parsing(Member_Details.this, xmlformat, 4);
        request.setOnResultListener(new XML_Parsing.OnResultListener() {

            @Override
            public void onCompleted(String isError, String msg, String ref, String flow) {
                if (pd.isShowing()) {
                    pd.dismiss();
                }
                if (!isError.equals("00")) {
                    if (isError.equals("300") && flow.equals("F")) {
                        if (Mwadh.equals("Y")) {
                            if (wadhflag != 1) {
                                wadhflag = 1;
                                show_error_box(msg, context.getResources().getString(R.string.Member_Authentication) + isError, 1);
                            } else {
                                EKYC = 1;
                                fCount = "1";
                                show_error_box(msg, context.getResources().getString(R.string.Member_Authentication) + isError, 2);
                            }
                        } else if (fpsCommonInfo.getfirauthFlag().equals("Y")) {
                            if (FIRflag != 1) {
                                FIRflag = 1;
                                show_error_box(msg, context.getResources().getString(R.string.Dealer_FIR_Authentication) + isError, 1);
                            } else {
                                EKYC = 1;
                                fCount = "1";
                                show_error_box(msg, context.getResources().getString(R.string.Member_Authentication) + isError, 2);

                            }
                        } else if (Mfusion.equals("1")) {
                            if (Fusionflag != 1) {
                                fCount = "2";
                                Fusionflag = 1;
                                show_error_box(msg, context.getResources().getString(R.string.Dealer_Fusion_Authentication)+ isError, 1);
                            } else {
                                EKYC = 1;
                                fCount = "1";
                                show_error_box(msg, context.getResources().getString(R.string.Member_Authentication) + isError, 2);
                            }
                        } else {
                            if (fusionflag != 1) {
                                fCount = "2";
                                fusionflag = 1;
                                show_error_box(msg, context.getResources().getString(R.string.Dealer_FP_Authentication) + isError, 1);
                            } else {
                                EKYC = 1;
                                fCount = "1";
                                show_error_box(msg, context.getResources().getString(R.string.Member_Authentication) + isError, 2);
                            }
                        }
                        return;
                    }
                    fCount = "1";
                    show_error_box(msg, context.getResources().getString(R.string.Member_Authentication) + isError, 0);
                } else {
                    if (fusionflag == 1) {
                        fusionflag = 0;
                        String fusion = "<?xml version='1.0' encoding='UTF-8' standalone='no' ?>\n" +
                                "<SOAP-ENV:Envelope\n" +
                                "    xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
                                "    xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\"\n" +
                                "    xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n" +
                                "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                                "    xmlns:ns1=\"http://service.fetch.rationcard/\">\n" +
                                "    <SOAP-ENV:Body>\n" +
                                "        <ns1:getFusionRecord>\n" +
                                "            <fpsSessionId>" + fpsCommonInfo.getfpsSessionId() + "</fpsSessionId>\n" +
                                "            <stateCode>" + stateBean.getstateCode() + "</stateCode>\n" +
                                "            <user_type>MEM</user_type>\n" +
                                "            <shop_no>" + stateBean.getstatefpsId() + "</shop_no>\n" +
                                "            <uidNumber>" + MUid + "</uidNumber>\n" +
                                "            <member_fusion>1</member_fusion>\n" +
                                "            <member_id>MEM</member_id>\n" +
                                "        </ns1:getFusionRecord>\n" +
                                "    </SOAP-ENV:Body>\n" +
                                "</SOAP-ENV:Envelope>";
                        Util.generateNoteOnSD(context, "MemberFusionReq.txt", fusion);
                        if (networkConnected(context)) {
                            hitURLfusion(fusion);
                        } else {
                            show_error_box(context.getResources().getString(R.string.Internet_Connection_Msg),context.getResources().getString(R.string.Internet_Connection),0);
                        }
                    }
                    Intent ration = new Intent(getApplicationContext(), Ration_details.class);
                    ration.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    ration.putExtra("REF",ref);
                    startActivity(ration);
                    finish();
                }
            }
        });
        request.execute();
    }

    private void hitURLfusion(String fusion) {
        pd = ProgressDialog.show(context, context.getResources().getString(R.string.Dealer), context.getResources().getString(R.string.Authenticating), true, false);
        XML_Parsing request = new XML_Parsing(Member_Details.this, fusion, 7);
        request.setOnResultListener(new XML_Parsing.OnResultListener() {

            @Override
            public void onCompleted(String isError, String msg, String ref, String flow) {
                if (pd.isShowing()) {
                    pd.dismiss();
                }
            }
        });
        request.execute();
    }

    private void EKYC() {

        String memeKyc = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<SOAP-ENV:Envelope\n" +
                "    xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
                "    xmlns:ns1=\"http://service.fetch.rationcard/\">\n" +
                "    <SOAP-ENV:Body>\n" +
                "        <ns1:getEKYCAuthenticateRD>\n" +
                "            <fpsSessionId>" + fpsCommonInfo.getfpsSessionId() + "</fpsSessionId>\n" +
                "            <stateCode>" + stateBean.getstateCode() + "</stateCode>\n" +
                "            <Shop_no>" + stateBean.getstatefpsId() + "</Shop_no>\n" +
                "            <terminal_id>" + DEVICEID + "</terminal_id>\n" +
                "            <existingRCNumber>" + Cash_Aadhaar + "</existingRCNumber>\n" +
                "            <rcMemberName>" + MName + "</rcMemberName>\n" +
                "            <rcUid>" + MUid + "</rcUid>\n" +
                "            <memberId>" + Mmemid + "</memberId>\n" +
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
                "            <eKYCType>eKYCN</eKYCType>\n" +
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

        Util.generateNoteOnSD(context, "MembereKycReq.txt", memeKyc);
        if (networkConnected(context)) {
            hiteKyc(memeKyc);
        } else {
            show_error_box(context.getResources().getString(R.string.Internet_Connection_Msg),context.getResources().getString(R.string.Internet_Connection),0);
        }

    }
    Ekyc Ekyc=new Ekyc();
    private void hiteKyc(String memeKyc) {
        pd = ProgressDialog.show(context, context.getResources().getString(R.string.Member), context.getResources().getString(R.string.Authenticating_EKYC), true, false);
        XML_Parsing request = new XML_Parsing(Member_Details.this, memeKyc, 8);
        request.setOnResultListener(new XML_Parsing.OnResultListener() {

            @Override
            public void onCompleted(String isError, String msg, String ref, String flow) {
                if (pd.isShowing()) {
                    pd.dismiss();
                }
                if (isError.equals("E00") && flow.equals("F")) {
                    String currentDateTimeString = java.text.DateFormat.getDateTimeInstance().format(new Date());
                    String details = "\n"+context.getResources().getString(R.string.MemberName) + Ekyc.geteKYCMemberName() + "\n" +
                            context.getResources().getString(R.string.DOB) + Ekyc.geteKYCDOB() + "\n" +
                            context.getResources().getString(R.string.PindCode) + Ekyc.geteKYCPindCode()+ "\n" +
                            context.getResources().getString(R.string.Gender) + Ekyc.geteKYCGeneder()+ "\n" +
                            context.getResources().getString(R.string.Date) +  currentDateTimeString + "\n";
                    show_error_box(msg + details, isError,3 );
                } else {
                    if (flow.equals("D")) {
                        DealerAuth();
                    } else if (flow.equals("M")) {
                        ManualAuth();
                    }
                    String currentDateTimeString = java.text.DateFormat.getDateTimeInstance().format(new Date());
                    String details = "\n"+context.getResources().getString(R.string.MemberName) + Ekyc.geteKYCMemberName() + "\n" +
                            context.getResources().getString(R.string.DOB) + Ekyc.geteKYCDOB() + "\n" +
                            context.getResources().getString(R.string.PindCode) + Ekyc.geteKYCPindCode()+ "\n" +
                            context.getResources().getString(R.string.Gender) + Ekyc.geteKYCGeneder()+ "\n" +
                            context.getResources().getString(R.string.Date) +  currentDateTimeString + "\n";
                    show_error_box(msg + details, isError,3 );
                    show_error_box(msg, context.getResources().getString(R.string.Member_EKYC) + isError, 0);
                }
            }
        });
        request.execute();
    }

    private void prep_Mlogin() {

        String memberlogin = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<SOAP-ENV:Envelope\n" +
                "    xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
                "    xmlns:ns1=\"http://www.uidai.gov.in/authentication/uid-auth-request/2.0\"\n" +
                "    xmlns:ns2=\"http://service.fetch.rationcard/\">\n" +
                "    <SOAP-ENV:Body>\n" +
                "        <ns2:getAuthenticateNICAuaAuthRD2>\n" +
                "            <fpsSessionId>" + fpsCommonInfo.getfpsSessionId() + "</fpsSessionId>\n" +
                "            <stateCode>" + stateBean.getstateCode() + "</stateCode>\n" +
                "            <Shop_no>" + stateBean.getstatefpsId() + "</Shop_no>\n" +
                "            <uidNumber>" + MUid + "</uidNumber>\n" +
                "            <udc>" + DEVICEID + "</udc>\n" +
                "            <authMode>B</authMode>\n" +
                "            <User_Id>" + cardDetails.getRcId() + "</User_Id>\n" +
                "            <auth_packet>\n" +
                "                <ns1:certificateIdentifier>" + ci + "</ns1:certificateIdentifier>\n" +
                "                <ns1:dataType>" + type + "</ns1:dataType>\n" +
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
                "            <scannerId></scannerId>\n" +
                "            <authType>" + MEMBER_AUTH_TYPE + "</authType>\n" +
                "            <memberId>" + Mmemid + "</memberId>\n" +
                "            <wadhStatus>" + Mwadh + "</wadhStatus>\n" +
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
                "        </ns2:getAuthenticateNICAuaAuthRD2>\n" +
                "    </SOAP-ENV:Body>\n" +
                "</SOAP-ENV:Envelope>";
        Util.generateNoteOnSD(context, "MemberAuthReq.txt", memberlogin);
        hitURL1(memberlogin);
    }

    private void connectRDservice() {
        try {
            if (mp!=null) {
                releaseMediaPlayer(context,mp);
            }
            if(L.equals("hi") ) {
                mp=mp.create(context,R.raw.c200032);
                        mp.start();
            }
            else {
                mp=mp.create(context,R.raw.c100032);
                        mp.start();
            }
            String xmplpid;
            if (fpsCommonInfo.getfirauthFlag().equals("Y")) {
                fCount = fpsCommonInfo.getfirauthCount();
                fType = "1";
                xmplpid = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                        "<PidOptions ver =\"1.0\">\n" +
                        "    <Opts env=\"P\" fCount=\"" + fCount + "\" iCount=\"" + iCount + "\" iType=\"" + iType + "\" fType=\"" + fType + "\" pCount=\"0\" pType=\"0\" format=\"0\" pidVer=\"2.0\" timeout=\"10000\" otp=\"\" wadh=\"\" posh=\"UNKNOWN\"/>\n" +
                        "</PidOptions>";
                System.out.println("FingerPrint Request");
            } else {
                fType = "0";
                xmplpid = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                        "<PidOptions ver =\"1.0\">\n" +
                        "    <Opts env=\"P\" fCount=\"" + fCount + "\" iCount=\"" + iCount + "\" iType=\"" + iType + "\" fType=\"" + fType + "\" pCount=\"0\" pType=\"0\" format=\"0\" pidVer=\"2.0\" timeout=\"10000\" otp=\"\" wadh=\"\" posh=\"UNKNOWN\"/>\n" +
                        "</PidOptions>";
            }

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

    private void connectRDserviceEKYC(String wadhvalue) {
        try {
            if (mp!=null) {
                releaseMediaPlayer(context,mp);
            }
            if(L.equals("hi")) {
                mp=mp.create(context, R.raw.c200032);
                        mp.start();
            }

            else {
                mp=mp.create(context, R.raw.c100032);
                        mp.start();
            }

            fCount = "1";
            fType = "0";
            String xmplpid = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
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

    private void AadhaarDialog() {

        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        final EditText edittext = new EditText(context);
        alert.setMessage(context.getResources().getString(R.string.Please_Enter_Member_UID_Number));

        edittext.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(12);
        edittext.setFilters(FilterArray);
        alert.setCancelable(false);
        alert.setTitle(context.getResources().getString(R.string.UID_Number));
        alert.setView(edittext);
        alert.setPositiveButton(context.getResources().getString(R.string.Ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Enter_UID = edittext.getText().toString();
                if (Enter_UID.length() == 12 && validateVerhoeff(Enter_UID)) {
                    try {

                        Aadhaar = encrypt(Enter_UID, fpsPofflineToken.getskey());
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
                    connectRDserviceEKYC(cardDetails.getzwadh());

                } else {
                    EKYC = 0;
                    toast(context, context.getResources().getString(R.string.Please_enter_a_valid_Value));
                }
            }
        });
        alert.setNegativeButton(context.getResources().getString(R.string.Cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                EKYC = 0;
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
                    if (EKYC == 1) {
                        EKYC = 0;
                        EKYC();
                    } else {
                        prep_Mlogin();
                    }
                } else {
                    show_error_box(errinfo, context.getResources().getString(R.string.PID_Exception), 0);
                    System.out.println("ERROR PID DATA = " + piddata);
                }
            }
        } else {
            Intent ration = new Intent(getApplicationContext(), Ration_details.class);
            ration.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(ration);
            finish();
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

    private void connectIRISRDservice(String xmplpid) {
        Intent intent = new Intent("in.gov.uidai.rdservice.iris.CAPTURE");
        intent.setClassName("com.iritech.rdservice", "com.iritech.rdservice.irishield.IriShieldRDActivity");
        intent.putExtra("PID_OPTIONS", xmplpid);
        startActivityForResult(intent, RD_SERVICE);
    }


    @Override
    protected void onDestroy() {
        // Util.checkData(context,2);
        super.onDestroy();
    }

    public interface OnClickListener {
        void onClick_d(int p);
    }
}
