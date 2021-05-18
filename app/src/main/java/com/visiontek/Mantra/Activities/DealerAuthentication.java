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
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.visiontek.Mantra.Utils.Aadhaar_Parsing;
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

import static com.visiontek.Mantra.Activities.Receive_Goods.chit;
import static com.visiontek.Mantra.Activities.Receive_Goods.cid;
import static com.visiontek.Mantra.Activities.Receive_Goods.e_allot;
import static com.visiontek.Mantra.Activities.Receive_Goods.e_code;
import static com.visiontek.Mantra.Activities.Receive_Goods.e_dispatch;
import static com.visiontek.Mantra.Activities.Receive_Goods.e_name;
import static com.visiontek.Mantra.Activities.Receive_Goods.e_sid;
import static com.visiontek.Mantra.Activities.Receive_Goods.enter;
import static com.visiontek.Mantra.Activities.Receive_Goods.month;
import static com.visiontek.Mantra.Activities.Receive_Goods.orderno;
import static com.visiontek.Mantra.Activities.Receive_Goods.truckno;
import static com.visiontek.Mantra.Activities.Receive_Goods.year;
import static com.visiontek.Mantra.Activities.Start.L;
import static com.visiontek.Mantra.Activities.Start.mp;
import static com.visiontek.Mantra.Utils.Util.ConsentForm;
import static com.visiontek.Mantra.Utils.Util.DEVICEID;
import static com.visiontek.Mantra.Utils.Util.RDservice;
import static com.visiontek.Mantra.Utils.Util.releaseMediaPlayer;
import static com.visiontek.Mantra.Utils.Util.toast;

public class DealerAuthentication extends AppCompatActivity {

    fpsCommonInfo fpsCommonInfo=new fpsCommonInfo();
    stateBean stateBean=new stateBean();
    fpsURLInfo fpsURLInfo=new fpsURLInfo();


    String MEMBER_AUTH_TYPE;
    String scnid;

    RecyclerView.Adapter adapter;
    Button scanfp, back;
    String DUid, DName, Dtype, DAtype, Dfusion, Dnamell, Dwadh;

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
    String wadh = null;
    String wadhvalue = null;
    CheckBox checkBox;
    ArrayList<String> permissions = new ArrayList<>();
    boolean click = false;
    Context context;
    ProgressDialog pd = null;
    DatabaseHelper databaseHelper;
    ArrayList<String> dauthtype;
    ArrayList<String> dname;
    ArrayList<String> dtype;
    ArrayList<String> duid;
    ArrayList<String> dfusion;
    ArrayList<String> dnamell;
    ArrayList<String> dwadh;
    TextView rd;
    int RD_SERVICE = 0;

    String xmplpid;
    boolean wadhverify;
    int ekycflag;
    int fusionflag;
    String refno;
    private String fCount;
    private final String iCount = "0";
    private final String fType = "0";
    private final String iType = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dealer_authentication);

        context = DealerAuthentication.this;
        pd = new ProgressDialog(context);
        databaseHelper = new DatabaseHelper(context);

        scanfp = findViewById(R.id.dealer_scanFP);
        back = findViewById(R.id.dealer_back);
        checkBox = findViewById(R.id.check);
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
                                    /*"   \"token\" : "+"\""+fpsURLInfo.token()+"\""+",\n" +*/
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
                    show_error_box(context.getResources().getString(R.string.Please_Select_Another_Dealer), context.getResources().getString(R.string.Dealer), 0);
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

                DName = dname.get(p);
                DUid = duid.get(p);
                Dtype = dtype.get(p);
                DAtype = dauthtype.get(p);
                Dfusion=dfusion.get(p);
                Dnamell = dnamell.get(p);
                Dwadh = dwadh.get(p);

                if ("F".equals(DAtype)) {
                    click = true;
                    System.out.println("-----------------" + Dfusion);
                    if (Dfusion.equals("1")) {
                        fCount = "2";
                    } else {
                        fCount = "1";
                    }
                    toast(context, context.getResources().getString(R.string.Biometric_login));
                }else {
                    click = false;
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
    private void hitURL1(String xmlformat) {
        pd = ProgressDialog.show(context, context.getResources().getString(R.string.Dealer), context.getResources().getString(R.string.Authenticating), true, false);
        XML_Parsing request = new XML_Parsing(DealerAuthentication.this, xmlformat, 15);
        request.setOnResultListener(new XML_Parsing.OnResultListener() {

            @Override
            public void onCompleted(String isError, String msg, String ref, String flow) {
                if (pd.isShowing()) {
                    pd.dismiss();
                }
                if (!isError.equals("00")) {
                    fusionflag = 0;

                    if (isError.equals("300")) {
                        if (flow.equals("F")) {
                            if (fcount.equals("1")) {
                                fCount = "2";
                                fusionflag = 1;
                                show_error_box(msg, context.getResources().getString(R.string.Dealer_Authentication) + isError, 1);
                                return;
                            }
                        }
                    }
                    fCount = "1";
                    show_error_box(msg, context.getResources().getString(R.string.Dealer_Authentication) + isError, 0);
                } else {
                    refno = ref;
                    Upload();

                }
            }
        });
        request.execute();
    }

    private void Upload() {
        String com = addComm();
        if (!com.equals("1")) {
            String stockupdate = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<SOAP-ENV:Envelope\n" +
                    "    xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
                    "    xmlns:ns1=\"http://service.fetch.rationcard/\">\n" +
                    "    <SOAP-ENV:Body>\n" +
                    "        <ns1:stockInfoUpdate>\n" +
                    "            <Stock_Entry>\n" +
                    "                <deviceId>" + DEVICEID + "</deviceId>\n" +
                    "                <dispatchId>" + cid + "</dispatchId>\n" +
                    "                <do_ro_no>" + orderno + "</do_ro_no>\n" +
                    "                <noOfComm>0</noOfComm>\n" +
                    "                <route_off_auth>" + refno + "</route_off_auth>\n" +
                    "                <route_uid>" + DUid + "</route_uid>\n" +
                    "                <shopNo>" + stateBean.getstatefpsId() + "</shopNo>\n" +
                    com +
                    "                <truckChitNo>" + chit + "</truckChitNo>\n" +
                    "                <truckNo>" + truckno + "</truckNo>\n" +
                    "            </Stock_Entry>\n" +
                    "            <password>" + fpsURLInfo.gettoken() + "</password>\n" +
                    "            <fpsSessionId>" + fpsCommonInfo.getfpsSessionId() + "</fpsSessionId>\n" +
                    "            <stateCode>" + stateBean.getstateCode() + "</stateCode>\n" +
                    "        </ns1:stockInfoUpdate>\n" +
                    "    </SOAP-ENV:Body>\n" +
                    "</SOAP-ENV:Envelope>";
            Util.generateNoteOnSD(context, "StockUploadDetailsReq.txt", stockupdate);
            hitUploading(stockupdate);

        }
    }

    private void hitUploading(String stockupdate) {

        pd = ProgressDialog.show(context, context.getResources().getString(R.string.Uploading_Stock), context.getResources().getString(R.string.Processing), true, false);
        Aadhaar_Parsing request = new Aadhaar_Parsing(DealerAuthentication.this, stockupdate, 9);
        request.setOnResultListener(new Aadhaar_Parsing.OnResultListener() {

            @Override
            public void onCompleted(String error, String msg, String ref, ArrayList<String> buffer) {
                if (pd.isShowing()) {
                    pd.dismiss();
                }
                if (!error.equals("00")) {

                    show_error_box(msg, context.getResources().getString(R.string.Uploading_Stock) + error, 0);
                } else {

                    show_error_box(msg, context.getResources().getString(R.string.Uploading_Stock) + error, 0);

                }
            }

        });
        request.execute();
    }

    private String addComm() {
        StringBuilder add = new StringBuilder();
        String str;
        if (enter.size() > 0) {
            for (int i = 0; i < enter.size(); i++) {
                str = "                <stockNewBean>\n" +
                        "                    <commCode>" + e_code.get(i) + "</commCode>\n" +
                        "                    <commName>" + e_name.get(i) + "</commName>\n" +
                        "                    <KRA>" + e_allot.get(i) + "</KRA>\n" +
                        "                    <receiveQty>" + enter.get(i) + "</receiveQty>\n" +
                        "                    <releasedQty>" + e_dispatch.get(i) + "</releasedQty>\n" +
                        "                    <shemeId>" + e_sid.get(i) + "</shemeId>\n" +
                        "                    <allotedMonth>" + month + "</allotedMonth>\n" +
                        "                    <allotedYear>" + year + "</allotedYear>\n" +
                        "                </stockNewBean>\n";
                add.append(str);

            }
            return String.valueOf(add);
        } else {
            return "0";
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

        if ("F".equals(DAtype)) {
            scnid = "1740I022882";
            MEMBER_AUTH_TYPE = "Bio";
            wadhverify = false;
            connectRDservice();
        }
    }

    private void prep_Dlogin() {
        if (mp!=null) {
            releaseMediaPlayer(context,mp);
        }
        if (L.equals("hi")) {
        } else {
            mp = mp.create(context, R.raw.c100187);
            mp.start();
        }

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
                "            <scannerId>" + scnid + "</scannerId>\n" +
                /*"            <authType>FIR</authType>\n"+*/
                "            <authType>" + MEMBER_AUTH_TYPE + "</authType>\n" +/*" + DAtype + "*/
                "            <memberId>" + Dtype + "</memberId>\n" +
                "            <wadhStatus>" + Dwadh + "</wadhStatus>\n" +
                /*"            <wadhStatus>Y</wadhStatus>\n" +*/
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

        Util.generateNoteOnSD(context, "RGDealerAuthReq.txt", dealerlogin);
        hitURL1(dealerlogin);
    }

    private void connectRDservice() {
        try {
            if (mp!=null) {
                releaseMediaPlayer(context,mp);
            }
            if(L.equals("hi")) {
                mp = mp.create(context, R.raw.c200032);
                mp.start();
            }
            else {
                mp = mp.create(context, R.raw.c100032);
                mp.start();
            }

            ftype = "0";
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
                    prep_Dlogin();
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


    public interface OnClickListener {
        void onClick_d(int p);


    }
}