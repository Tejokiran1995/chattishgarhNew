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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static com.visiontek.Mantra.Activities.Member_Details.Mdealer;
import static com.visiontek.Mantra.Activities.Start.L;
import static com.visiontek.Mantra.Activities.Start.mp;
import static com.visiontek.Mantra.Utils.Util.ConsentForm;
import static com.visiontek.Mantra.Utils.Util.DEVICEID;
import static com.visiontek.Mantra.Utils.Util.RDservice;
import static com.visiontek.Mantra.Utils.Util.networkConnected;
import static com.visiontek.Mantra.Utils.Util.releaseMediaPlayer;

public class Dealer_Details extends AppCompatActivity {
    fpsPofflineToken fpsPofflineToken=new fpsPofflineToken();
    fpsCommonInfo fpsCommonInfo=new fpsCommonInfo();
    stateBean stateBean=new stateBean();
    fpsURLInfo fpsURLInfo=new fpsURLInfo();

    static String DUid, DName, Dtype, DAtype, Dfusion, Dnamell, Dwadh;

    String DEALER_AUTH_TYPE;

    RecyclerView.Adapter adapter;

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

    ProgressDialog pd = null;
    DatabaseHelper databaseHelper;
    ArrayList<String> dauthtype;
    ArrayList<String> dname;
    ArrayList<String> dtype;
    ArrayList<String> duid;
    ArrayList<String> dfusion;
    ArrayList<String> dnamell;
    ArrayList<String> dwadh;

    CheckBox checkBox;
    TextView rd;


    int RD_SERVICE = 0;

    boolean click = false;


    int wadhflag, FIRflag, Fusionflag;

    int fusionflag;
    String dealertype;


    String YouEditTextValue;
    Context context;

    private String fCount = "0";
    private String fType = "0";
    private String iCount = "0";
    private String iType = "0";

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dealer__details);

        context = Dealer_Details.this;
        pd = new ProgressDialog(context);
        databaseHelper = new DatabaseHelper(context);


        scanfp = findViewById(R.id.dealer_scanFP);
        back = findViewById(R.id.dealer_back);
        checkBox = findViewById(R.id.check);

        rd = findViewById(R.id.rd);

        boolean rd_fps;
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
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        scanfp.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                if (click) {
                    if (Util.networkConnected(context)) {
                        if (checkBox.isChecked()) {

                            ConsentDialog(ConsentForm(context));
                        } else {
                            System.out.println("@@ In dealer details else case");
                            String currentDateTimeString = java.text.DateFormat.getDateTimeInstance().format(new Date());
                            currentDateTimeString="26032021114610";
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
                                    /*"   \"token\" : "+"\""+fpsURLInfo.token()+"\""+"\n" +*/
                                    "   \"token\" : "+"\"9f943748d8c1ff6ded5145c59d0b2ae7\""+"\n" +
                                    "}";
                            Util.generateNoteOnSD(context, "ConsentFormReq.txt", consentrequest);
                            ConsentformURL(consentrequest);
                            //show_error_box(context.getResources().getString(R.string.Please_check_Consent_Form), context.getResources().getString(R.string.Consent_Form), 0);
                        }
                    } else {
                        show_error_box(context.getResources().getString(R.string.Internet_Connection_Msg), context.getResources().getString(R.string.Internet_Connection), 0);
                    }
                } else {
                    if (mp!=null) {
                        releaseMediaPlayer(context,mp);
                    }
                    if (L.equals("hi")) {
                    } else {
                        mp = mp.create(context, R.raw.c100176);
                        mp.start();
                        show_error_box(context.getResources().getString(R.string.Please_Select_Dealer_Name), context.getResources().getString(R.string.Dealer), 0);
                    }
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
        dauthtype = databaseHelper.get_DD(0);
        dname = databaseHelper.get_DD(2);
        dtype = databaseHelper.get_DD(1);
        duid = databaseHelper.get_DD(3);
        dfusion = databaseHelper.get_DD(4);
        dnamell = databaseHelper.get_DD(5);
        dwadh = databaseHelper.get_DD(6);

        ArrayList<DataModel> data = new ArrayList<>();
        for (int i = 0; i < dname.size(); i++) {
            data.add(new DataModel(dname.get(i), dtype.get(i)));
        }
        adapter = new CustomAdapter(context, data, new OnClickListener() {
            @Override
            public void onClick_d(int p) {
                click = true;
                Fusionflag = 0;
                wadhflag = 0;
                FIRflag = 0;
                fusionflag = 0;
                DName = dname.get(p);
                DUid = duid.get(p);
                Dtype = dtype.get(p);
                DAtype = dauthtype.get(p);
                Dfusion = dfusion.get(p);
                Dnamell = dnamell.get(p);
                Dwadh = dwadh.get(p);

                switch (DAtype) {
                    case "F":
                        DEALER_AUTH_TYPE = "Bio";
                        break;
                    case "P":
                        DEALER_AUTH_TYPE = "P";
                        System.out.println("Password Request");
                        break;
                }
            }
        }, 1);
        recyclerView.setAdapter(adapter);
    }

    private void ConsentformURL(String consentrequest) {
        pd = ProgressDialog.show(context, context.getResources().getString(R.string.Dealer), context.getResources().getString(R.string.Consent_Form), true, false);
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

    private void password_Dialog() {
        if (mp!=null) {
            releaseMediaPlayer(context,mp);
        }
        if (L.equals("hi")) {
        } else {
            mp = mp.create(context, R.raw.c100074);
            mp.start();
        }
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        final EditText edittext = new EditText(context);
        alert.setMessage(context.getResources().getString(R.string.Please_Enter_Dealer_Authentication_Password));
        alert.setTitle(context.getResources().getString(R.string.Dealer_Password));
        edittext.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(12);
        edittext.setFilters(FilterArray);

        alert.setView(edittext);
        alert.setPositiveButton(context.getResources().getString(R.string.Ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                YouEditTextValue = edittext.getText().toString();

                if (!YouEditTextValue.isEmpty() && fpsCommonInfo.getdealer_password().equals(YouEditTextValue)) {
                    if (mp!=null) {
                        releaseMediaPlayer(context,mp);
                    }
                    if (L.equals("hi")) {
                    } else {
                        mp = mp.create(context, R.raw.c100178);
                        mp.start();
                    }
                    String pdealerlogin = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                            "<SOAP-ENV:Envelope\n" +
                            "    xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
                            "    xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n" +
                            "    xmlns:wsdl=\"http://schemas.xmlsoap.org/wsdl/\"\n" +
                            "    xmlns:tns=\"http://service.fetch.rationcard/\"\n" +
                            "    xmlns:soap=\"http://schemas.xmlsoap.org/wsdl/soap/\"\n" +
                            "    xmlns:ns1=\"http://schemas.xmlsoap.org/soap/http\"\n" +
                            "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" >\n" +
                            "    <SOAP-ENV:Body>\n" +
                            "        <mns1:ackRequest\n" +
                            "            xmlns:mns1=\"http://service.fetch.rationcard/\">\n" +
                            "            <fpsCard>" + stateBean.getstatefpsId() + "</fpsCard>\n" +
                            "            <terminalId>" + DEVICEID + "</terminalId>\n" +
                            "            <user_password>" + YouEditTextValue + "</user_password>\n" +
                            "            <fpsSessionId>" + fpsCommonInfo.getfpsSessionId() + "</fpsSessionId>\n" +
                            "            <uidNumber>" + DUid + "</uidNumber>\n" +
                            "            <token>" + fpsURLInfo.gettoken() + "</token>\n" +
                            "            <auth_type>" + DEALER_AUTH_TYPE + "</auth_type>\n" +
                            "            <user_type>" + Dtype + "</user_type>\n" +
                            "            <memberId>0</memberId>\n" +
                            "            <fpsId>" + fpsCommonInfo.getfpsId() + "</fpsId>\n" +
                            "            <stateCode>" + stateBean.getstateCode() + "</stateCode>\n" +
                            "        </mns1:ackRequest>\n" +
                            "    </SOAP-ENV:Body>\n" +
                            "</SOAP-ENV:Envelope> ";
                    Util.generateNoteOnSD(context, "DealerPasswordReq.txt", pdealerlogin);
                    hitURL1(pdealerlogin);
                } else {
                    show_error_box(context.getResources().getString(R.string.Please_Enter_a_valid_Password), context.getResources().getString(R.string.Invalid_Password), 0);
                }
            }
        });
        alert.setNegativeButton(context.getResources().getString(R.string.Cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        alert.show();
    }

    private void hitURL1(String xmlformat) {
        pd = ProgressDialog.show(context, context.getResources().getString(R.string.Dealer), context.getResources().getString(R.string.Authenticating), true, false);
        XML_Parsing request = new XML_Parsing(Dealer_Details.this, xmlformat, 2);
        request.setOnResultListener(new XML_Parsing.OnResultListener() {

            @Override
            public void onCompleted(String isError, String msg, String ref, String flow) {
                if (pd.isShowing()) {
                    pd.dismiss();
                }
                if (!isError.equals("00")) {

                    if (isError.equals("300") && flow.equals("F")) {
                        if (Dwadh.equals("Y") && wadhflag != 1) {
                            wadhflag = 1;
                            show_error_box(msg, context.getResources().getString(R.string.Dealer_Authentication) + isError, 1);
                            return;
                        } else if (fpsCommonInfo.getfirauthFlag().equals("Y") && FIRflag != 1) {
                            FIRflag = 1;
                            show_error_box(msg, context.getResources().getString(R.string.Dealer_FIR_Authentication) + isError, 1);
                            return;
                        } else if (Dfusion.equals("1") && Fusionflag != 1) {
                            Fusionflag = 1;
                            fCount = "2";
                            show_error_box(msg, context.getResources().getString(R.string.Dealer_Fusion_Authentication) + isError, 1);
                            return;
                        } else {
                            if (fCount.equals("1") && fusionflag != 1) {
                                fusionflag = 1;
                                fCount = "2";
                                show_error_box(msg, context.getResources().getString(R.string.Dealer_FP_Authentication) + isError, 1);
                                return;
                            }
                        }
                    }

                    fCount = "1";
                    show_error_box(msg, context.getResources().getString(R.string.Dealer_Authentication) + isError, 0);
                } else {
                    if (Mdealer == 1) {
                        Mdealer = 0;
                        finish();
                        return;
                    } else {
                        if (fusionflag == 1) {
                            fusionflag = 0;
                            if (Dtype.equals("N1")) {
                                dealertype = "REP1";
                            } else if (Dtype.equals("N2")) {
                                dealertype = "REP2";
                            } else {
                                dealertype = "DEL";
                            }
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
                                    "            <user_type>" + dealertype + "</user_type>\n" +
                                    "            <shop_no>" + stateBean.getstatefpsId() + "</shop_no>\n" +
                                    "            <uidNumber>" + DUid + "</uidNumber>\n" +
                                    "            <member_fusion>1</member_fusion>\n" +
                                    "            <member_id>" + dealertype + "</member_id>\n" +
                                    "        </ns1:getFusionRecord>\n" +
                                    "    </SOAP-ENV:Body>\n" +
                                    "</SOAP-ENV:Envelope>";
                            Util.generateNoteOnSD(context, "DealerFusionReq.txt", fusion);
                            hitURLfusion(fusion);
                        }

                        String menu = "<?xml version='1.0' encoding='UTF-8' standalone='no' ?>\n" +
                                "<SOAP-ENV:Envelope\n" +
                                "    xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
                                "    xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\"\n" +
                                "    xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n" +
                                "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                                "    xmlns:ns1=\"http://service.fetch.rationcard/\">\n" +
                                "    <SOAP-ENV:Body>\n" +
                                "        <ns1:menuDisplayService>\n" +
                                "            <shop_number>" + stateBean.getstatefpsId() + "</shop_number>\n" +
                                "            <fpsSessionId>" + fpsCommonInfo.getfpsSessionId() + "</fpsSessionId>\n" +
                                "            <stateCode>" + stateBean.getstateCode() + "</stateCode>\n" +
                                "        </ns1:menuDisplayService>\n" +
                                "    </SOAP-ENV:Body>\n" +
                                "</SOAP-ENV:Envelope>";
                        Util.generateNoteOnSD(context, "MenuReq.txt", menu);
                        hitURLMENU(menu);
                    }
                }
            }
        });
        request.execute();
    }

    private void hitURLfusion(String fusion) {
        pd = ProgressDialog.show(context, context.getResources().getString(R.string.Dealer), context.getResources().getString(R.string.Authenticating), true, false);
        XML_Parsing request = new XML_Parsing(Dealer_Details.this, fusion, 7);
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

    private void hitURLMENU(String menu) {
        pd = ProgressDialog.show(context, context.getResources().getString(R.string.Please_wait), context.getResources().getString(R.string.Downloading_Menus), true, false);
        XML_Parsing request = new XML_Parsing(Dealer_Details.this, menu, 7);
        request.setOnResultListener(new XML_Parsing.OnResultListener() {

            @Override
            public void onCompleted(String isError, String msg, String ref, String flow) {
                if (pd.isShowing()) {
                    pd.dismiss();
                }
                if (!isError.equals("00")) {
                    show_error_box(msg, context.getResources().getString(R.string.MenuList) + isError, 0);
                } else {

                    Intent home = new Intent(context, Home.class);
                    home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(home);
                    finish();
                }
            }
        });
        request.execute();

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
                            callScanFP();
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

    private void callScanFP() {
        switch (DAtype) {
            case "F":
                if (Dwadh.equals("Y")) {
                    connectRDserviceEKYC(fpsCommonInfo.getwadhValue());
                    System.out.println("WADH_EKYC Request");
                } else {
                    if (Dfusion.equals("1")) {
                        System.out.println("FUSION Request");
                        fCount = "2";
                    }
                    connectRDservice();
                    System.out.println("fingerPrint Request");
                }

                break;
            case "P":
                System.out.println("Password Request");
                password_Dialog();
                break;
            default:
                break;
        }
    }

    private void prep_Dlogin() {

        String dealerlogin = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<SOAP-ENV:Envelope\n" +
                "    xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
                "    xmlns:ns1=\"http://www.uidai.gov.in/authentication/uid-auth-request/2.0\"\n" +
                "    xmlns:ns2=\"http://service.fetch.rationcard/\">\n" +
                "    <SOAP-ENV:Body>\n" +
                "        <ns2:getAuthenticateNICAuaAuthRD2>\n" +
                "            <fpsSessionId>" + fpsCommonInfo.getfpsSessionId() + "</fpsSessionId>\n" +
                "            <stateCode>" + stateBean.getstateCode() + "</stateCode>\n" +
                "            <Shop_no>" + stateBean.getstatefpsId() + "</Shop_no>\n" +
                "            <uidNumber>" + DUid + "</uidNumber>\n" +
                "            <udc>" + DEVICEID + "</udc>\n" +
                "            <authMode>" + Dtype + "</authMode>\n" +
                "            <User_Id>" + fpsCommonInfo.getfpsId() + "</User_Id>\n" +
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
                "            <authType>" + DEALER_AUTH_TYPE + "</authType>\n" +
                "            <memberId>" + Dtype + "</memberId>\n" +
                "            <wadhStatus>" + Dwadh + "</wadhStatus>\n" +
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
                "                <qScore>0</qScore>\n" +
                "            </Resp>\n" +
                "        </ns2:getAuthenticateNICAuaAuthRD2>\n" +
                "    </SOAP-ENV:Body>\n" +
                "</SOAP-ENV:Envelope>";
        if (networkConnected(context)) {
            if (mp!=null) {
                releaseMediaPlayer(context,mp);
            }
            if (L.equals("hi")) {
            } else {
                mp = mp.create(context, R.raw.c100187);
                mp.start();
            }
            Util.generateNoteOnSD(context, "DealerAuthReq.txt", dealerlogin);
            hitURL1(dealerlogin);
        } else {
            show_error_box(context.getResources().getString(R.string.Internet_Connection_Msg),context.getResources().getString(R.string.Internet_Connection),0);
        }
    }

    private void connectRDservice() {
        try {
            if (mp!=null) {
                releaseMediaPlayer(context,mp);
            }
            if (L.equals("hi")) {
                mp =mp.create(context, R.raw.c200032);
                        mp.start();

            } else {
                mp =mp.create(context, R.raw.c100032);
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
                System.out.println("FIR Request");
            } else {
                fType = "0";
                xmplpid = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                        "<PidOptions ver =\"1.0\">\n" +
                        "    <Opts env=\"P\" fCount=\"" + fCount + "\" iCount=\"" + iCount + "\" iType=\"" + iType + "\" fType=\"" + fType + "\" pCount=\"0\" pType=\"0\" format=\"0\" pidVer=\"2.0\" timeout=\"10000\" otp=\"\" wadh=\"\" posh=\"UNKNOWN\"/>\n" +
                        "</PidOptions>";
            }
            System.out.println("*****************************" + fCount);
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
            if (L.equals("hi")) {
                mp =mp.create(context, R.raw.c200032);
                        mp.start();

            } else {
                mp =mp.create(context, R.raw.c100032);
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

    @SuppressLint("SetTextI18n")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("OnActivityResult");
        if (requestCode == RD_SERVICE) {
            if (resultCode == Activity.RESULT_OK) {
                String piddata = data.getStringExtra("PID_DATA");
                if (piddata != null && piddata.contains("errCode=\"0\"")) {
                    int code = createAuthXMLRegistered(piddata);
                    if (code == 0) {
                        System.out.println("PID DATA = " + piddata);
                        prep_Dlogin();
                    } else {
                        show_error_box(errinfo, context.getResources().getString(R.string.PID_Exception), 0);
                    }
                } else {
                    show_error_box(errinfo, context.getResources().getString(R.string.PID_Exception), 0);
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
                fcount = doc.getElementsByTagName("Resp").item(0).getAttributes().getNamedItem("fCount").getTextContent();

                ftype = doc.getElementsByTagName("Resp").item(0).getAttributes().getNamedItem("fType").getTextContent();

                nmpoint = doc.getElementsByTagName("Resp").item(0).getAttributes().getNamedItem("nmPoints").getTextContent();

                pid = doc.getElementsByTagName("Data").item(0).getTextContent();

                skey = doc.getElementsByTagName("Skey").item(0).getTextContent();

                ci = doc.getElementsByTagName("Skey").item(0).getAttributes().getNamedItem("ci").getTextContent();

                hmac = doc.getElementsByTagName("Hmac").item(0).getTextContent();

                type = doc.getElementsByTagName("Data").item(0).getAttributes().getNamedItem("type").getTextContent();

                dpId = doc.getElementsByTagName("DeviceInfo").item(0).getAttributes().getNamedItem("dpId").getTextContent();

                rdsId = doc.getElementsByTagName("DeviceInfo").item(0).getAttributes().getNamedItem("rdsId").getTextContent();

                rdsVer = doc.getElementsByTagName("DeviceInfo").item(0).getAttributes().getNamedItem("rdsVer").getTextContent();

                dc = doc.getElementsByTagName("DeviceInfo").item(0).getAttributes().getNamedItem("dc").getTextContent();

                mi = doc.getElementsByTagName("DeviceInfo").item(0).getAttributes().getNamedItem("mi").getTextContent();

                mc = doc.getElementsByTagName("DeviceInfo").item(0).getAttributes().getNamedItem("mc").getTextContent();

                skey = skey.replaceAll(" ", "\n");

            }
        } catch (Exception e) {
            e.printStackTrace();
            errinfo = String.valueOf(e);
            return 2;
        }
        return 0;
    }


    @Override
    protected void onDestroy() {
        //Util.checkData(context,1);
        super.onDestroy();
    }

    public interface OnClickListener {
        void onClick_d(int p);
    }
}

