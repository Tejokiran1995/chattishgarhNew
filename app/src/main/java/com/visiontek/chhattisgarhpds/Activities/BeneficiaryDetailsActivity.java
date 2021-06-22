package com.visiontek.chhattisgarhpds.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.view.Window;
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
import com.visiontek.chhattisgarhpds.Adapters.CustomAdapter4;
import com.visiontek.chhattisgarhpds.Models.AadhaarServicesModel.BeneficiaryVerification.GetURLDetails.BeneficiaryDetails;
import com.visiontek.chhattisgarhpds.Models.AadhaarServicesModel.BeneficiaryVerification.GetUserDetails.BeneficiaryModel;
import com.visiontek.chhattisgarhpds.Models.AadhaarServicesModel.UIDSeeding.GetURLDetails.UIDAuth;
import com.visiontek.chhattisgarhpds.Models.DATAModels.DataModel5;
import com.visiontek.chhattisgarhpds.R;
import com.visiontek.chhattisgarhpds.Utils.Aadhaar_Parsing;
import com.visiontek.chhattisgarhpds.Utils.Json_Parsing;
import com.visiontek.chhattisgarhpds.Utils.TaskPrint;
import com.visiontek.chhattisgarhpds.Utils.Util;

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

import static com.visiontek.chhattisgarhpds.Activities.StartActivity.L;
import static com.visiontek.chhattisgarhpds.Activities.StartActivity.mp;
import static com.visiontek.chhattisgarhpds.Models.AppConstants.DEVICEID;

import static com.visiontek.chhattisgarhpds.Models.AppConstants.dealerConstants;
import static com.visiontek.chhattisgarhpds.Models.AppConstants.menuConstants;
import static com.visiontek.chhattisgarhpds.Utils.Util.ConsentForm;
import static com.visiontek.chhattisgarhpds.Utils.Util.RDservice;
import static com.visiontek.chhattisgarhpds.Utils.Util.encrypt;
import static com.visiontek.chhattisgarhpds.Utils.Util.networkConnected;
import static com.visiontek.chhattisgarhpds.Utils.Util.releaseMediaPlayer;
import static com.visiontek.chhattisgarhpds.Utils.Util.toast;
import static com.visiontek.chhattisgarhpds.Utils.Veroeff.validateVerhoeff;

public class BeneficiaryDetailsActivity extends AppCompatActivity implements PrinterCallBack {

    private static String ACTION_USB_PERMISSION;
    Button back, Ekyc;

    CheckBox checkBox;
    Context context;

    RecyclerView.Adapter adapter;
    ProgressDialog pd = null;
    String details;
    TextView Ben_cardnum;

    BeneficiaryModel beneficiaryModel=new BeneficiaryModel();
    BeneficiaryDetails beneficiaryDetails;
    private BeneficiaryDetailsActivity mActivity;
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
        context = BeneficiaryDetailsActivity.this;
        pd = new ProgressDialog(context);
        checkBox = findViewById(R.id.check);
        TextView rd = findViewById(R.id.rd);

        beneficiaryDetails = (BeneficiaryDetails) getIntent().getSerializableExtra("OBJ");
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
        Ben_cardnum=findViewById(R.id.Ben_cardnum);
        Ben_cardnum.setText(beneficiaryDetails.rationCardId);
        beneficiaryModel.click = false;

        back = findViewById(R.id.Ben_details_back);
        Ekyc = findViewById(R.id.Ben_details_Ekyc);
        Ekyc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (beneficiaryModel.click) {
                    if (beneficiaryModel.verification.equals("N")) {
                        AadhaarDialog();
                    } else {
                        show_error_box("", context.getResources().getString(R.string.Member_is_already_Verified), 0);
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
        int rcMemberDetVerifysize=beneficiaryDetails.rcMemberDetVerify.size();
        for (int i = 0; i < rcMemberDetVerifysize; i++) {
            data.add(new DataModel5(beneficiaryDetails.rcMemberDetVerify.get(i).memberName,
                    beneficiaryDetails.rcMemberDetVerify.get(i).uid,
                    beneficiaryDetails.rcMemberDetVerify.get(i).verifyStatus_en));
        }
        adapter = new CustomAdapter4(context, data, new DealerDetailsActivity.OnClickListener() {
            @Override
            public void onClick_d(int p) {
                beneficiaryModel.click = true;
                beneficiaryModel.memberId = beneficiaryDetails.rcMemberDetVerify.get(p).memberId;
                beneficiaryModel.memberName =beneficiaryDetails.rcMemberDetVerify.get(p).memberName;
                beneficiaryModel.memberNamell =beneficiaryDetails.rcMemberDetVerify.get(p).memberNamell;
                beneficiaryModel.member_fusion = beneficiaryDetails.rcMemberDetVerify.get(p).member_fusion;
                beneficiaryModel.uid = beneficiaryDetails.rcMemberDetVerify.get(p).uid;
                beneficiaryModel.verification = beneficiaryDetails.rcMemberDetVerify.get(p).verification;
                beneficiaryModel.verifyStatus_en = beneficiaryDetails.rcMemberDetVerify.get(p).verifyStatus_en;
                beneficiaryModel.verifyStatus_ll = beneficiaryDetails.rcMemberDetVerify.get(p).verifyStatus_ll;
                beneficiaryModel.w_uid_status = beneficiaryDetails.rcMemberDetVerify.get(p).w_uid_status;
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
            public void onCompleted(String code, String msg, Object object) {
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
                beneficiaryModel.Enter_UID = edittext.getText().toString();

                if (beneficiaryModel.Enter_UID.length() == 12 && validateVerhoeff(beneficiaryModel.Enter_UID)) {
                    try {
                        beneficiaryModel.Enter_UID = encrypt(beneficiaryModel.Enter_UID, menuConstants.skey);

                        if (Util.networkConnected(context)) {
                            if (checkBox.isChecked()) {

                                ConsentDialog(ConsentForm(context));
                            } else {
                                String currentDateTimeString = java.text.DateFormat.getDateTimeInstance().format(new Date());
                                currentDateTimeString = "23032021163452";
                                String consentrequest = "{\n" +
                                        "   \"fpsId\" : " + "\"" + dealerConstants.stateBean.statefpsId + "\"" + ",\n" +
                                        "   \"modeOfService\" : \"D\",\n" +
                                        "   \"moduleType\" : \"C\",\n" +
                                        "   \"rcId\" : " + "\"" + dealerConstants.stateBean.statefpsId + "\"" + ",\n" +
                                        "   \"requestId\" : \"0\",\n" +
                                        "   \"requestValue\" : \"N\",\n" +
                                        "   \"sessionId\" : " + "\"" +dealerConstants. fpsCommonInfo.fpsSessionId + "\"" + ",\n" +
                                        "   \"stateCode\" : " + "\"" + dealerConstants.stateBean.stateCode + "\"" + ",\n" +
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
        final Dialog dialog = new Dialog(context, android.R.style.Theme_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.consent);
        Button confirm = (Button) dialog.findViewById(R.id.agree);
        Button back = (Button) dialog.findViewById(R.id.cancel);
        TextView tv=(TextView)dialog.findViewById(R.id.consent);
        tv.setText(concent);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                connectRDserviceEKYC(beneficiaryDetails.wadh);

            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }

    UIDAuth uidAuth;
    private void hitURL1(String BenAuth) {

        pd = ProgressDialog.show(context, context.getResources().getString(R.string.Beneficiary_Verification), context.getResources().getString(R.string.Processing), true, false);
        Aadhaar_Parsing request = new Aadhaar_Parsing(context, BenAuth,4 );
        request.setOnResultListener(new Aadhaar_Parsing.OnResultListener() {

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onCompleted(String error, String msg, String ref, String flow, Object object) {
                if (pd.isShowing()) {
                    pd.dismiss();
                }
                if (!error.equals("E00")) {
                    System.out.println("ERRORRRRRRRRRRRRRRRRRRRR");
                    show_error_box(msg, context.getResources().getString(R.string.Member_Details) + error, 0);
                } else {
                    uidAuth= (UIDAuth) object;
                    String currentDateTimeString = java.text.DateFormat.getDateTimeInstance().format(new Date());
                    details = "\n" + context.getResources().getString(R.string.MemberName) + uidAuth.eKYCMemberName + "\n" +
                            context.getResources().getString(R.string.DOB) +uidAuth.eKYCDOB+ "\n" +
                            context.getResources().getString(R.string.PindCode) + uidAuth.eKYCPindCode + "\n" +
                            context.getResources().getString(R.string.Gender) + uidAuth.eKYCGeneder+ "\n" +
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

            beneficiaryModel.fCount = "2";
            String xmplpid = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                    "<PidOptions ver =\"1.0\">\n" +
                    "    <Opts env=\"P\" fCount=\"" + beneficiaryModel.fCount + "\" iCount=\"" + beneficiaryModel.iCount + "\" iType=\"" + beneficiaryModel.iType + "\" fType=\"" + beneficiaryModel.fType + "\" pCount=\"0\" pType=\"0\" format=\"0\" pidVer=\"2.0\" timeout=\"10000\" otp=\"\" wadh=\"" + wadhvalue + "\" posh=\"UNKNOWN\"/>\n" +
                    "</PidOptions>";

            Intent act = new Intent("in.gov.uidai.rdservice.fp.CAPTURE");
            PackageManager packageManager = getPackageManager();
            List<ResolveInfo> activities = packageManager.queryIntentActivities(act, PackageManager.MATCH_DEFAULT_ONLY);
            final boolean isIntentSafe = activities.size() > 0;
            System.out.println("Boolean check for activities = " + isIntentSafe);
            if (!isIntentSafe) {
                Toast.makeText(getApplicationContext(), context.getResources().getString(R.string.No_RD_Service_Available), Toast.LENGTH_SHORT).show();
            }
            System.out.println("No of activities = " + activities.size());
            act.putExtra("PID_OPTIONS", xmplpid);
            startActivityForResult(act,beneficiaryModel. RD_SERVICE);
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
        if (requestCode == beneficiaryModel.RD_SERVICE) {
            if (resultCode == Activity.RESULT_OK) {
                System.out.println(data.getStringExtra("PID_DATA"));
                String piddata = data.getStringExtra("PID_DATA");
                int code = createAuthXMLRegistered(piddata);
                if (piddata != null && piddata.contains("errCode=\"0\"") && code == 0) {
                    System.out.println("PID DATA = " + piddata);

                    prep_Mlogin();

                } else {
                    show_error_box(beneficiaryModel.rdModel.errinfo, context.getResources().getString(R.string.PID_Exception), 0);
                    System.out.println("ERROR PID DATA = " + piddata);
                }
            }
        }
    }

    private void prep_Mlogin() {
        String BenAuth = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<SOAP-ENV:Envelope\n" +
                "    xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
                "    xmlns:ns1=\"http://service.fetch.rationcard/\">\n" +
                "    <SOAP-ENV:Body>\n" +
                "        <ns1:getEKYCAuthenticateRD>\n" +
                "            <fpsSessionId>" + dealerConstants.fpsCommonInfo.fpsSessionId + "</fpsSessionId>\n" +
                "            <stateCode>" + dealerConstants.stateBean.stateCode + "</stateCode>\n" +
                "            <Shop_no>" + dealerConstants.stateBean.statefpsId + "</Shop_no>\n" +
                "            <terminal_id>" + DEVICEID + "</terminal_id>\n" +
                "            <existingRCNumber>" + beneficiaryDetails.rationCardId + "</existingRCNumber>\n" +
                "            <rcMemberName>" + beneficiaryModel.memberName + "</rcMemberName>\n" +
                "            <rcUid>" + beneficiaryModel.uid + "</rcUid>\n" +
                "            <memberId>" + beneficiaryModel.memberId + "</memberId>\n" +
                "            <ekycresAuth>\n" +
                "                <dc>" +  beneficiaryModel.rdModel.dc + "</dc>\n" +
                "                <dpId>" +  beneficiaryModel.rdModel.dpId + "</dpId>\n" +
                "                <mc>" +  beneficiaryModel.rdModel.mc + "</mc>\n" +
                "                <mid>" +  beneficiaryModel.rdModel.mi + "</mid>\n" +
                "                <rdId>" +  beneficiaryModel.rdModel.rdsId + "</rdId>\n" +
                "                <rdVer>" + beneficiaryModel.rdModel. rdsVer + "</rdVer>\n" +
                "                <res_Consent_POIandPOA>Y</res_Consent_POIandPOA>\n" +
                "                <res_Consent_mobileOREmail>Y</res_Consent_mobileOREmail>\n" +
                "                <res_certificateIdentifier>" +  beneficiaryModel.rdModel.ci + "</res_certificateIdentifier>\n" +
                "                <res_encHmac>" + beneficiaryModel.rdModel. hmac + "</res_encHmac>\n" +
                "                <res_secure_pid>" +  beneficiaryModel.rdModel.pid + "</res_secure_pid>\n" +
                "                <res_sessionKey>" +  beneficiaryModel.rdModel.skey + "</res_sessionKey>\n" +
                "                <res_uid>" + beneficiaryModel.Enter_UID + "</res_uid>\n" +
                "            </ekycresAuth>\n" +
                "            <password>" + dealerConstants.fpsURLInfo.token + "</password>\n" +
                "            <eKYCType>eKYCV</eKYCType>\n" +
                "            <Resp>\n" +
                "                <errCode>0</errCode>\n" +
                "                <errInfo>y</errInfo>\n" +
                "                <nmPoints>" +  beneficiaryModel.rdModel.nmpoint + "</nmPoints>\n" +
                "                <fCount>" + beneficiaryModel.rdModel. fcount + "</fCount>\n" +
                "                <fType>" +  beneficiaryModel.rdModel.ftype + "</fType>\n" +
                "                <iCount>" +  beneficiaryModel.rdModel.icount + "</iCount>\n" +
                "                <iType>" +  beneficiaryModel.rdModel.itype + "</iType>\n" +
                "                <pCount>0</pCount>\n" +
                "                <pType>0</pType>\n" +
                "                <qScore>0</qScore>\n" +
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
            Util.generateNoteOnSD(context, "BenVerificationAuthReq.txt", BenAuth);
            hitURL1(BenAuth);
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
                    context.getResources().getString(R.string.FPS_ID) + " : " + dealerConstants.fpsCommonInfo.fpsId + "\n"
                    + context.getResources().getString(R.string.NAME) + " : " + uidAuth.eKYCMemberName + "\n\n"
                    + context.getResources().getString(R.string.Gender) + " : " + uidAuth.eKYCGeneder+ "\n"
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
                    context.getResources().getString(R.string.FPS_ID) + " : " + dealerConstants.fpsCommonInfo.fpsId + "\n"
                    + context.getResources().getString(R.string.NAME) + " : "+ uidAuth.eKYCMemberName + "\n\n"
                    + context.getResources().getString(R.string.Gender) + " : " + uidAuth.eKYCGeneder+"\n"
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

            beneficiaryModel.err_code = doc.getElementsByTagName("Resp").item(0).getAttributes().getNamedItem("errCode").getTextContent();
            if (beneficiaryModel.err_code.equals("1")) {
                beneficiaryModel.rdModel. errinfo = doc.getElementsByTagName("Resp").item(0).getAttributes().getNamedItem("errInfo").getTextContent();
                return 1;
            } else {
                beneficiaryModel.rdModel.fcount = doc.getElementsByTagName("Resp").item(0).getAttributes().getNamedItem("fCount").getTextContent();
                beneficiaryModel.rdModel.ftype = doc.getElementsByTagName("Resp").item(0).getAttributes().getNamedItem("fType").getTextContent();
                beneficiaryModel.rdModel.nmpoint = doc.getElementsByTagName("Resp").item(0).getAttributes().getNamedItem("nmPoints").getTextContent();
                beneficiaryModel.rdModel.pid = doc.getElementsByTagName("Data").item(0).getTextContent();
                beneficiaryModel.rdModel.skey = doc.getElementsByTagName("Skey").item(0).getTextContent();
                beneficiaryModel.rdModel.ci = doc.getElementsByTagName("Skey").item(0).getAttributes().getNamedItem("ci").getTextContent();
                beneficiaryModel.rdModel. hmac = doc.getElementsByTagName("Hmac").item(0).getTextContent();
                beneficiaryModel.rdModel.type = doc.getElementsByTagName("Data").item(0).getAttributes().getNamedItem("type").getTextContent();
                beneficiaryModel.rdModel.dpId = doc.getElementsByTagName("DeviceInfo").item(0).getAttributes().getNamedItem("dpId").getTextContent();
                beneficiaryModel.rdModel. rdsId = doc.getElementsByTagName("DeviceInfo").item(0).getAttributes().getNamedItem("rdsId").getTextContent();
                beneficiaryModel.rdModel.rdsVer = doc.getElementsByTagName("DeviceInfo").item(0).getAttributes().getNamedItem("rdsVer").getTextContent();
                beneficiaryModel.rdModel.dc = doc.getElementsByTagName("DeviceInfo").item(0).getAttributes().getNamedItem("dc").getTextContent();
                beneficiaryModel.rdModel. mi = doc.getElementsByTagName("DeviceInfo").item(0).getAttributes().getNamedItem("mi").getTextContent();
                beneficiaryModel.rdModel.mc = doc.getElementsByTagName("DeviceInfo").item(0).getAttributes().getNamedItem("mc").getTextContent();
                beneficiaryModel.rdModel.skey =  beneficiaryModel.rdModel.skey.replaceAll(" ", "\n");

            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("createAuthXMLRegistered error= " + e);
            beneficiaryModel.rdModel.errinfo = String.valueOf(e);
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
