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

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.visiontek.Mantra.Utils.Util;

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

import static com.visiontek.Mantra.Activities.Aadhaar_Seeding.UID_ID;
import static com.visiontek.Mantra.Activities.Aadhaar_Seeding.UID_zWadh;

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

public class UID_Details extends AppCompatActivity {
    fpsPofflineToken fpsPofflineToken=new fpsPofflineToken();
    fpsURLInfo fpsURLInfo=new fpsURLInfo();
    fpsCommonInfo fpsCommonInfo=new fpsCommonInfo();
    stateBean stateBean=new stateBean();
    static String UID_Details_Aadhaar;
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

    Button back, Ekyc;
    Context context;
    DatabaseHelper databaseHelper;
    ArrayList<String> bfd_1;
    ArrayList<String> bfd_2;
    ArrayList<String> bfd_3;
    ArrayList<String> memberId;
    ArrayList<String> memberName;
    ArrayList<String> memberNamell;
    ArrayList<String> member_fusion;
    ArrayList<String> uid;
    ArrayList<String> w_uid_status;
    RecyclerView.Adapter adapter;
    ProgressDialog pd = null;
    String MemID, Mfusion, W_uidwadh, Muid, Mname, Mverification;
    TextView UID_details_cardnum;
    boolean click;
    String Enter_UID;
    private final String iCount = "0";
    private final String fType = "0";
    private final String iType = "0";
    CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_u_i_d__details);

        context = UID_Details.this;

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
        pd = new ProgressDialog(context);
        databaseHelper = new DatabaseHelper(context);

        click = false;

        bfd_1 = databaseHelper.get_UID(0);
        bfd_2 = databaseHelper.get_UID(2);
        bfd_3 = databaseHelper.get_UID(1);
        memberId = databaseHelper.get_UID(3);
        memberName = databaseHelper.get_UID(4);
        memberNamell = databaseHelper.get_UID(5);
        member_fusion = databaseHelper.get_UID(6);
        uid = databaseHelper.get_UID(7);
        w_uid_status = databaseHelper.get_UID(8);

        UID_details_cardnum = findViewById(R.id.UID_details_cardnum);
        UID_details_cardnum.setText(UID_ID);
        Ekyc = findViewById(R.id.UID_details_Ekyc);
        back = findViewById(R.id.UID_details_back);

        checkBox=findViewById(R.id.check);

        Ekyc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (click) {

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
                                   /* "   \"token\" : " + "\"" + fpsURLInfo.token() + "\"" + ",\n" +*/
                                    "   \"token\" : "+"\"9f943748d8c1ff6ded5145c59d0b2ae7\""+"\n" +
                                    "}";
                            Util.generateNoteOnSD(context, "ConsentFormReq.txt", consentrequest);
                            ConsentformURL(consentrequest);
                        }
                    }

                    if (Mverification.equals("Y")) {
                        ConsentDialog(ConsentForm(context));

                    } else {
                        show_error_box(context.getResources().getString(R.string.Member_is_already_Verified), context.getResources().getString(R.string.Already_Verified));
                    }
                } else {
                    if (mp != null) {
                        releaseMediaPlayer(context, mp);
                    }
                    if (L.equals("hi")) {
                    } else {
                        mp = mp.create(context, R.raw.c100065);
                        mp.start();
                        toast(context, context.getResources().getString(R.string.Please_Select_a_Member));
                    }
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

        for (int i = 0; i < memberName.size(); i++) {
            data.add(new DataModel5(memberName.get(i), uid.get(i), w_uid_status.get(i)));
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
                Mverification = bfd_1.get(p);
            }
        }, 1);
        recyclerView.setAdapter(adapter);
    }

    private void ConsentformURL(String consentrequest) {
        pd = ProgressDialog.show(context, context.getResources().getString(R.string.UID_DETAILS), context.getResources().getString(R.string.Consent_Form), true, false);
        Json_Parsing request = new Json_Parsing(context, consentrequest, 3);
        request.setOnResultListener(new Json_Parsing.OnResultListener() {

            @Override
            public void onCompleted(String code, String msg) {
                if (pd.isShowing()) {
                    pd.dismiss();
                }
                if (!code.equals("00")) {
                    show_error_box(msg,  code);
                } else {
                    show_error_box(msg,  code);
                }
            }

        });

    }

    private void AadhaarDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        final EditText edittext = new EditText(context);
        alert.setMessage(context.getResources().getString(R.string.Please_Enter_Member_UID_Number));
        alert.setTitle(context.getResources().getString(R.string.UID_Number));
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
                        UID_Details_Aadhaar = encrypt(Enter_UID, fpsPofflineToken.getskey());
                        connectRDserviceEKYC(UID_zWadh);
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
                    if (mp!=null) {
                        releaseMediaPlayer(context,mp);
                    }
                    if (L.equals("hi")) {
                    } else {
                        mp = mp.create(context, R.raw.c100047);
                        mp.start();
                        toast(context, context.getResources().getString(R.string.Invalid_Aadhaar));
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
                        AadhaarDialog();

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

        pd = ProgressDialog.show(context, context.getResources().getString(R.string.Members), context.getResources().getString(R.string.Fetching_Members), true, false);
        Aadhaar_Parsing request = new Aadhaar_Parsing(context, uid, 2);
        request.setOnResultListener(new Aadhaar_Parsing.OnResultListener() {

            @Override
            public void onCompleted(String error, String msg, String ref, ArrayList<String> buffer) {
                if (pd.isShowing()) {
                    pd.dismiss();
                }
                if (!error.equals("E00")) {
                    System.out.println("ERRORRRRRRRRRRRRRRRRRRRR");
                    show_error_box(msg, context.getResources().getString(R.string.Member_Details) + error);
                } else {
                    String currentDateTimeString = java.text.DateFormat.getDateTimeInstance().format(new Date());
                    String details = "\n"+context.getResources().getString(R.string.MemberName) + buffer.get(0) + "\n" +
                            context.getResources().getString(R.string.DOB) + buffer.get(2) + "\n" +
                            context.getResources().getString(R.string.PindCode) + buffer.get(3) + "\n" +
                            context.getResources().getString(R.string.Gender) + buffer.get(4) + "\n" +
                            context.getResources().getString(R.string.Date) + currentDateTimeString + "\n";
                    show_error_box(msg + details, context.getResources().getString(R.string.UID_Seeding) + error);
                    System.out.println("SUCCESSSSSSSSSSSS");
                }
            }

        });
        request.execute();
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
                show_error_box(msg, context.getResources().getString(R.string.RD_SERVICE_ERROR));
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
                    show_error_box(errinfo, context.getResources().getString(R.string.PID_Exception));
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
                "            <existingRCNumber>" + UID_ID + "</existingRCNumber>\n" +
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
                "                <res_uid>" + UID_Details_Aadhaar + "</res_uid>\n" +
                "            </ekycresAuth>\n" +
                "            <password>" + fpsURLInfo.gettoken() + "</password>\n" +
                "            <eKYCType>eKYCS</eKYCType>\n" +
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

        Util.generateNoteOnSD(context, "UIDAuthReq.txt", UID);
        if (networkConnected(context)) {
            if (mp!=null) {
                releaseMediaPlayer(context,mp);
            }
            if (L.equals("hi")) {
            } else {
                mp = mp.create(context, R.raw.c100187);
                mp.start();
            }
            hitURL1(UID);
        } else {
            show_error_box(context.getResources().getString(R.string.Internet_Connection_Msg),context.getResources().getString(R.string.Internet_Connection));
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


}
