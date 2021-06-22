package com.visiontek.chhattisgarhpds.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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

import com.visiontek.chhattisgarhpds.Adapters.CustomAdapter;
import com.visiontek.chhattisgarhpds.Models.DATAModels.DataModel;
import com.visiontek.chhattisgarhpds.Models.DealerDetailsModel.GetUserDetails.DealerModel;
import com.visiontek.chhattisgarhpds.Models.PartialOnlineData;
import com.visiontek.chhattisgarhpds.R;
import com.visiontek.chhattisgarhpds.Utils.DatabaseHelper;
import com.visiontek.chhattisgarhpds.Utils.Json_Parsing;
import com.visiontek.chhattisgarhpds.Utils.Util;
import com.visiontek.chhattisgarhpds.Utils.XML_Parsing;

import org.w3c.dom.Document;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static com.visiontek.chhattisgarhpds.Activities.MemberDetailsActivity.Mdealer;
import static com.visiontek.chhattisgarhpds.Activities.StartActivity.L;
import static com.visiontek.chhattisgarhpds.Activities.StartActivity.mp;
import static com.visiontek.chhattisgarhpds.Models.AppConstants.DEVICEID;

import static com.visiontek.chhattisgarhpds.Models.AppConstants.dealerConstants;
import static com.visiontek.chhattisgarhpds.Utils.Util.ConsentForm;
import static com.visiontek.chhattisgarhpds.Utils.Util.RDservice;
import static com.visiontek.chhattisgarhpds.Utils.Util.networkConnected;
import static com.visiontek.chhattisgarhpds.Utils.Util.releaseMediaPlayer;

public class DealerDetailsActivity extends AppCompatActivity {
    static String dealername;
    DealerModel dealerModel=new DealerModel();
    RecyclerView.Adapter adapter;
    Button scanfp, back;
    ProgressDialog pd = null;
    CheckBox checkBox;
    TextView rd;
    Context context;
    DatabaseHelper databaseHelper;
    String txntype;
    RecyclerView recyclerView;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dealer__details);
        context = DealerDetailsActivity.this;
        txntype = getIntent().getStringExtra("txnType");
        pd = new ProgressDialog(context);
        scanfp = findViewById(R.id.dealer_scanFP);
        back = findViewById(R.id.dealer_back);
        checkBox = findViewById(R.id.check);
        rd = findViewById(R.id.rd);
        boolean rd_fps;
        rd_fps = RDservice(context);
        databaseHelper = new DatabaseHelper(this);
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.enable();
        if (rd_fps) {
            rd.setTextColor(context.getResources().getColor(R.color.green));
        } else {
            show_error_box(context.getResources().getString(R.string.RD_Service_Msg),context.getResources().getString(R.string.RD_Service),0);
            rd.setTextColor(context.getResources().getColor(R.color.black));
        }

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        recyclerView = findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        scanfp.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                if (dealerModel.click) {
                    if (txntype.equals("O") && Util.networkConnected(context)) {
                        if (checkBox.isChecked()) {
                            ConsentDialog(ConsentForm(context));
                        } else {
                            System.out.println("@@ In dealer details else case");
                            String currentDateTimeString = java.text.DateFormat.getDateTimeInstance().format(new Date());
                            currentDateTimeString="26032021114610";
                            String consentrequest="{\n" +
                                    "   \"fpsId\" : "+"\""+dealerConstants.stateBean.statefpsId+"\""+",\n" +
                                    "   \"modeOfService\" : \"D\",\n" +
                                    "   \"moduleType\" : \"C\",\n" +
                                    "   \"rcId\" : "+"\""+dealerConstants.stateBean.statefpsId+"\""+",\n" +
                                    "   \"requestId\" : \"0\",\n" +
                                    "   \"requestValue\" : \"N\",\n" +
                                    "   \"sessionId\" : "+"\""+dealerConstants.fpsCommonInfo.fpsSessionId+"\""+",\n" +
                                    "   \"stateCode\" : "+"\""+dealerConstants.stateBean.stateCode+"\""+",\n" +
                                    "   \"terminalId\" : "+"\""+DEVICEID+"\""+",\n" +
                                    "   \"timeStamp\" : "+"\""+currentDateTimeString+"\""+",\n" +
                                    /*"   \"token\" : "+"\""+fpsURLInfo.token()+"\""+"\n" +*/
                                    "   \"token\" : "+"\"9f943748d8c1ff6ded5145c59d0b2ae7\""+"\n" +
                                    "}";
                            Util.generateNoteOnSD(context, "ConsentFormReq.txt", consentrequest);
                            ConsentformURL(consentrequest);
                        }
                    } else {
                            PartialOnlineData partialOnlineData = databaseHelper.getPartialOnlineData();
                            if(partialOnlineData != null && partialOnlineData.getOfflineLogin() != null && partialOnlineData.getOfflineLogin().equals("Y"))
                            {
                                password_Dialog(txntype);
                            }
                            else
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

        if(txntype.equals("O"))
            setAdapterData();
        else
        {
            PartialOnlineData partialOnlineData = databaseHelper.getPartialOnlineData();
            if(partialOnlineData != null && partialOnlineData.getOfflineLogin() != null && partialOnlineData.getOfflineLogin().equals("Y"))
            {
                password_Dialog(txntype);
            }
        }
    }

    public void setAdapterData()
    {
        ArrayList<DataModel> data = new ArrayList<>();
        int dealerlistsize =dealerConstants.fpsCommonInfo.fpsDetails.size();
        for (int i = 0; i < dealerlistsize; i++) {
            System.out.println(dealerConstants.fpsCommonInfo.fpsDetails.get(i).delName);
            data.add(new DataModel(dealerConstants.fpsCommonInfo.fpsDetails.get(i).delName,
                    dealerConstants.fpsCommonInfo.fpsDetails.get(i).dealer_type));
        }
        adapter = new CustomAdapter(context, data, new OnClickListener() {
            @Override
            public void onClick_d(int p) {
                dealerModel.click = true;
                dealerModel.Fusionflag = 0;
                dealerModel.wadhflag = 0;
                dealerModel.FIRflag = 0;
                dealerModel.fusionflag = 0;
                dealerModel.DName = dealerConstants.fpsCommonInfo.fpsDetails.get(p).delName;
                dealerModel.DUid = dealerConstants.fpsCommonInfo.fpsDetails.get(p).delUid;
                dealerModel.Dtype = dealerConstants.fpsCommonInfo.fpsDetails.get(p).dealer_type;
                dealerModel.DAtype = dealerConstants.fpsCommonInfo.fpsDetails.get(p).authType;
                dealerModel.Dfusion = dealerConstants.fpsCommonInfo.fpsDetails.get(p).dealerFusion;
                dealerModel.Dnamell = dealerConstants.fpsCommonInfo.fpsDetails.get(p).delNamell;
                dealerModel.Dwadh = dealerConstants.fpsCommonInfo.fpsDetails.get(p).wadhStatus;
                dealername=dealerModel.DName;
                switch (dealerModel.DAtype) {
                    case "F":
                        dealerModel.DEALER_AUTH_TYPE = "Bio";
                        break;
                    case "P":
                        dealerModel.DEALER_AUTH_TYPE = "P";
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
            public void onCompleted(String code, String msg, Object object) {
                if (pd.isShowing()) {
                    pd.dismiss();
                }
                if (code == null || code.isEmpty()){
                    show_error_box("Invalid Out put from Server","No Response",0);
                    return;
                }
                if (!code.equals("00")) {
                    show_error_box(msg,  code,0);
                } else {
                    show_error_box(msg,  code,0);
                }
            }

        });

    }

    private void password_Dialog(final String txnType)
    {
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

                dealerModel.EnterPassword = edittext.getText().toString();

                if(!dealerModel.EnterPassword.isEmpty() && dealerConstants.fpsCommonInfo.dealer_password.equals(dealerModel.EnterPassword)) {


                    if (mp != null) {
                        releaseMediaPlayer(context, mp);
                    }
                    if (L.equals("hi")) {
                    } else {
                        mp = mp.create(context, R.raw.c100178);
                        mp.start();
                    }
                    if (txnType.equals("Q")) {
                        proceedinOffline(txnType, dealerModel.EnterPassword);
                    } else {
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
                                "            <fpsCard>" + dealerConstants.stateBean.statefpsId + "</fpsCard>\n" +
                                "            <terminalId>" + DEVICEID + "</terminalId>\n" +
                                "            <user_password>" + dealerModel.EnterPassword + "</user_password>\n" +
                                "            <fpsSessionId>" + dealerConstants.fpsCommonInfo.fpsSessionId + "</fpsSessionId>\n" +
                                "            <uidNumber>" + dealerModel.DUid + "</uidNumber>\n" +
                                "            <token>" + dealerConstants.fpsURLInfo.token + "</token>\n" +
                                "            <auth_type>" + dealerModel.DEALER_AUTH_TYPE + "</auth_type>\n" +
                                "            <user_type>" + dealerModel.Dtype + "</user_type>\n" +
                                "            <memberId>0</memberId>\n" +
                                "            <fpsId>" + dealerConstants.fpsCommonInfo.fpsId + "</fpsId>\n" +
                                "            <stateCode>" + dealerConstants.stateBean.stateCode + "</stateCode>\n" +
                                "        </mns1:ackRequest>\n" +
                                "    </SOAP-ENV:Body>\n" +
                                "</SOAP-ENV:Envelope> ";
                        Util.generateNoteOnSD(context, "DealerPasswordReq.txt", pdealerlogin);
                        hitURLDealerAuthentication(pdealerlogin);
                    }
                }else
                {
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
    public void proceedinOffline(String txnType,String password)
    {
        String errorMessage = databaseHelper.loginByPassword(this,password);
        if(errorMessage.isEmpty())
        {
            Intent home = new Intent(context, HomeActivity.class);
            home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            home.putExtra("txnType",txnType);
            startActivity(home);
            finish();
        }
        else
        {
            show_error_box("Login Response",errorMessage,-1);
        }
    }

    private void hitURLDealerAuthentication(String dealerlogin) {
        pd = ProgressDialog.show(context, context.getResources().getString(R.string.Dealer), context.getResources().getString(R.string.Authenticating), true, false);
        XML_Parsing request = new XML_Parsing(DealerDetailsActivity.this, dealerlogin, 2);
        request.setOnResultListener(new XML_Parsing.OnResultListener() {

            @Override
            public void onCompleted(String isError, String msg, String ref, String flow, Object object) {
                if (pd.isShowing()) {
                    pd.dismiss();
                }
                if (isError == null || isError.isEmpty()){
                    show_error_box("Invalid Out put from Server","No Response",0);
                    return;
                }
                if (!isError.equals("00")) {
                    if (isError.equals("300") && flow.equals("F")) {
                        if (dealerModel.Dwadh.equals("Y") && dealerModel.wadhflag != 1) {
                            dealerModel.wadhflag = 1;
                            show_error_box(msg, context.getResources().getString(R.string.Dealer_Authentication) + isError, 1);
                            return;
                        } /*else if (dealerConstants.fpsCommonInfo.firauthFlag.equals("Y") && dealerModel.FIRflag != 1) {
                            dealerModel.FIRflag = 1;
                            show_error_box(msg, context.getResources().getString(R.string.Dealer_FIR_Authentication) + isError, 1);
                            return;
                        }*/ else if (dealerModel.Dfusion.equals("1") && dealerModel.Fusionflag != 1) {
                            dealerModel. Fusionflag = 1;
                            dealerModel.fCount = "2";
                            show_error_box(msg, context.getResources().getString(R.string.Dealer_Fusion_Authentication) + isError, 1);
                            return;
                        } else {
                            if (dealerModel.fCount.equals("1") && dealerModel.fusionflag != 1) {
                                dealerModel.fusionflag = 1;
                                dealerModel.fCount = "2";
                                show_error_box(msg, context.getResources().getString(R.string.Dealer_FP_Authentication) + isError, 1);
                                return;
                            }
                        }
                    }

                    dealerModel.fCount = "1";
                    show_error_box(msg, context.getResources().getString(R.string.Dealer_Authentication) + isError, 0);
                } else {
                    if (Mdealer == 1) {
                        Mdealer = 0;
                        finish();
                        return;
                    } else {
                        if (dealerModel.fusionflag == 1) {
                            dealerModel.fusionflag = 0;
                            if (dealerModel.Dtype.equals("N1")) {
                                dealerModel.dealertype = "REP1";
                            } else if (dealerModel.Dtype.equals("N2")) {
                                dealerModel.dealertype = "REP2";
                            } else {
                                dealerModel.dealertype = "DEL";
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
                                    "            <fpsSessionId>" + dealerConstants.fpsCommonInfo.fpsSessionId + "</fpsSessionId>\n" +
                                    "            <stateCode>" + dealerConstants.stateBean.stateCode + "</stateCode>\n" +
                                    "            <user_type>" + dealerModel.dealertype + "</user_type>\n" +
                                    "            <shop_no>" + dealerConstants.stateBean.statefpsId + "</shop_no>\n" +
                                    "            <uidNumber>" + dealerModel.DUid + "</uidNumber>\n" +
                                    "            <member_fusion>1</member_fusion>\n" +
                                    "            <member_id>" + dealerModel.dealertype + "</member_id>\n" +
                                    "        </ns1:getFusionRecord>\n" +
                                    "   </SOAP-ENV:Body>\n" +
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
                                "            <shop_number>" + dealerConstants.stateBean.statefpsId + "</shop_number>\n" +
                                "            <fpsSessionId>" + dealerConstants.fpsCommonInfo.fpsSessionId + "</fpsSessionId>\n" +
                                "            <stateCode>" + dealerConstants.stateBean.stateCode + "</stateCode>\n" +
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
        XML_Parsing request = new XML_Parsing(DealerDetailsActivity.this, fusion, 2);
        request.setOnResultListener(new XML_Parsing.OnResultListener() {

            @Override
            public void onCompleted(String isError, String msg, String ref, String flow, Object object) {
                if (pd.isShowing()) {
                    pd.dismiss();
                }
            }
        });
        request.execute();
    }

    private void hitURLMENU(String menu) {
        pd = ProgressDialog.show(context, context.getResources().getString(R.string.Please_wait), context.getResources().getString(R.string.Downloading_Menus), true, false);
        XML_Parsing request = new XML_Parsing(DealerDetailsActivity.this, menu, 7);
        request.setOnResultListener(new XML_Parsing.OnResultListener() {

            @Override
            public void onCompleted(String isError, String msg, String ref, String flow, Object object) {
                if (pd.isShowing()) {
                    pd.dismiss();
                }
                if (isError == null || isError.isEmpty()){
                    show_error_box("Invalid Out put from Server","No Response",0);
                    return;
                }
                if (!isError.equals("00")) {
                    show_error_box(msg, context.getResources().getString(R.string.MenuList) + isError, 0);
                } else {
                    Intent home = new Intent(context, HomeActivity.class);
                    home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    home.putExtra("txnType","O");
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
                dealerModel.Fusionflag = 0;
                dealerModel.wadhflag = 0;
                dealerModel.FIRflag = 0;
                dealerModel.fusionflag = 0;
                dealerModel.fCount="1";
                dialog.dismiss();
                callScanFP();

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

    private void callScanFP() {
        switch (dealerModel.DAtype) {
            case "F":
                if (dealerModel.Dwadh.equals("Y")) {
                    connectRDservice(dealerConstants.fpsCommonInfo.wadhValue,1);
                    System.out.println("WADH_EKYC Request");
                } else {
                    if (dealerModel.Dfusion.equals("1")) {
                        System.out.println("FUSION Request");
                        dealerModel.fCount = "2";
                    }
                    connectRDservice("",0);
                    System.out.println("fingerPrint Request");
                }

                break;
            case "P":
                System.out.println("Password Request");
                password_Dialog("O");
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
                "            <fpsSessionId>" + dealerConstants.fpsCommonInfo.fpsSessionId + "</fpsSessionId>\n" +
                "            <stateCode>" + dealerConstants.stateBean.stateCode + "</stateCode>\n" +
                "            <Shop_no>" + dealerConstants.stateBean.statefpsId + "</Shop_no>\n" +
                "            <uidNumber>" + dealerModel.DUid + "</uidNumber>\n" +
                "            <udc>" + DEVICEID + "</udc>\n" +
                "            <authMode>" +dealerModel. Dtype + "</authMode>\n" +
                "            <User_Id>" + dealerConstants.fpsCommonInfo.fpsId + "</User_Id>\n" +
                "            <auth_packet>\n" +
                "                <ns1:certificateIdentifier>" + dealerModel.rdModel.ci + "</ns1:certificateIdentifier>\n" +
                "                <ns1:dataType>" + dealerModel.rdModel.type + "</ns1:dataType>\n" +
                "                <ns1:dc>" + dealerModel.rdModel.dc + "</ns1:dc>\n" +
                "                <ns1:dpId>" +dealerModel. rdModel.dpId + "</ns1:dpId>\n" +
                "                <ns1:encHmac>" + dealerModel.rdModel.hmac + "</ns1:encHmac>\n" +
                "                <ns1:mc>" + dealerModel.rdModel.mc + "</ns1:mc>\n" +
                "                <ns1:mid>" + dealerModel.rdModel.mi + "</ns1:mid>\n" +
                "                <ns1:rdId>" + dealerModel.rdModel.rdsId + "</ns1:rdId>\n" +
                "                <ns1:rdVer>" + dealerModel.rdModel.rdsVer + "</ns1:rdVer>\n" +
                "                <ns1:secure_pid>" + dealerModel.rdModel.pid + "</ns1:secure_pid>\n" +
                "                <ns1:sessionKey>" + dealerModel.rdModel.skey + "</ns1:sessionKey>\n" +
                "            </auth_packet>\n" +
                "            <password>" + dealerConstants.fpsURLInfo.token + "</password>\n" +
                "            <scannerId></scannerId>\n" +
                "            <authType>" + dealerModel.DEALER_AUTH_TYPE + "</authType>\n" +
                "            <memberId>" + dealerModel.Dtype + "</memberId>\n" +
                "            <wadhStatus>" + dealerModel.Dwadh + "</wadhStatus>\n" +
                "            <Resp>\n" +
                "                <errCode>0</errCode>\n" +
                "                <errInfo>y</errInfo>\n" +
                "                <nmPoints>" + dealerModel.rdModel.nmpoint + "</nmPoints>\n" +
                "                <fCount>" + dealerModel.rdModel.fcount + "</fCount>\n" +
                "                <fType>" + dealerModel.rdModel.ftype + "</fType>\n" +
                "                <iCount>" + dealerModel.rdModel.icount + "</iCount>\n" +
                "                <iType>" +dealerModel. rdModel.itype + "</iType>\n" +
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
            hitURLDealerAuthentication(dealerlogin);
        } else {
            show_error_box(context.getResources().getString(R.string.Internet_Connection_Msg),context.getResources().getString(R.string.Internet_Connection),0);
        }
    }

    private void connectRDservice(String wadhvalue, int ekyc) {
        try {
            String xmplpid=PIDFormat(wadhvalue,ekyc);
            Intent act = new Intent("in.gov.uidai.rdservice.fp.CAPTURE");
            PackageManager packageManager = getPackageManager();
            List<ResolveInfo> activities = packageManager.queryIntentActivities(act, PackageManager.MATCH_DEFAULT_ONLY);
            final boolean isIntentSafe = activities.size() > 0;
            if (!isIntentSafe) {
                Toast.makeText(getApplicationContext(), context.getResources().getString(R.string.No_RD_Service_Available), Toast.LENGTH_SHORT).show();
            }
            act.putExtra("PID_OPTIONS", xmplpid);
            startActivityForResult(act, dealerModel.RD_SERVICE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String PIDFormat(String wadhvalue, int ekyc) {
        String xmplpid;
        if (ekyc==1) {
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

            dealerModel.fCount = "1";
            dealerModel.fType = "0";
            xmplpid = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                    "<PidOptions ver =\"1.0\">\n" +
                    "    <Opts env=\"P\" fCount=\"" + dealerModel.fCount + "\" iCount=\"" + dealerModel.iCount + "\" iType=\"" + dealerModel.iType + "\" fType=\"" + dealerModel.fType + "\" pCount=\"0\" pType=\"0\" format=\"0\" pidVer=\"2.0\" timeout=\"10000\" otp=\"\" wadh=\"" + wadhvalue + "\" posh=\"UNKNOWN\"/>\n" +
                    "</PidOptions>";
        }else {
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

            /*if (dealerConstants.fpsCommonInfo.firauthFlag.equals("Y")) {
                dealerModel.fCount = dealerConstants.fpsCommonInfo.firauthCount;
                dealerModel.fType = "1";
                xmplpid = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                        "<PidOptions ver =\"1.0\">\n" +
                        "    <Opts env=\"P\" fCount=\"" + dealerModel.fCount + "\" iCount=\"" +dealerModel. iCount + "\" iType=\"" +dealerModel. iType + "\" fType=\"" + dealerModel.fType + "\" pCount=\"0\" pType=\"0\" format=\"0\" pidVer=\"2.0\" timeout=\"10000\" otp=\"\" wadh=\"\" posh=\"UNKNOWN\"/>\n" +
                        "</PidOptions>";
                System.out.println("FIR Request");
            } else {*/
                dealerModel.fType = "0";
                xmplpid = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                        "<PidOptions ver =\"1.0\">\n" +
                        "    <Opts env=\"P\" fCount=\"" + dealerModel.fCount + "\" iCount=\"" +dealerModel. iCount + "\" iType=\"" + dealerModel.iType + "\" fType=\"" +dealerModel. fType + "\" pCount=\"0\" pType=\"0\" format=\"0\" pidVer=\"2.0\" timeout=\"10000\" otp=\"\" wadh=\"\" posh=\"UNKNOWN\"/>\n" +
                        "</PidOptions>";
           // }
        }
        return xmplpid;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("OnActivityResult");
        if (requestCode == dealerModel.RD_SERVICE) {
            if (resultCode == Activity.RESULT_OK) {
                String piddata = data.getStringExtra("PID_DATA");
                if (piddata != null && piddata.contains("errCode=\"0\"")) {
                    int code = createAuthXMLRegistered(piddata);
                    if (code == 0) {
                        System.out.println("PID DATA = " + piddata);
                        prep_Dlogin();
                    } else {
                        show_error_box(dealerModel.rdModel.errinfo, context.getResources().getString(R.string.PID_Exception), 0);
                    }
                } else {
                    show_error_box(dealerModel.rdModel.errinfo, context.getResources().getString(R.string.PID_Exception), 0);
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

            dealerModel.err_code = doc.getElementsByTagName("Resp").item(0).getAttributes().getNamedItem("errCode").getTextContent();
            if (dealerModel.err_code.equals("1")) {
                dealerModel.rdModel.errinfo = doc.getElementsByTagName("Resp").item(0).getAttributes().getNamedItem("errInfo").getTextContent();
                return 1;
            } else {
                dealerModel.rdModel.icount = "0";
                dealerModel.rdModel.itype = "0";
                dealerModel.rdModel.fcount = doc.getElementsByTagName("Resp").item(0).getAttributes().getNamedItem("fCount").getTextContent();

                dealerModel.rdModel.ftype = doc.getElementsByTagName("Resp").item(0).getAttributes().getNamedItem("fType").getTextContent();

                dealerModel.rdModel.nmpoint = doc.getElementsByTagName("Resp").item(0).getAttributes().getNamedItem("nmPoints").getTextContent();

                dealerModel.rdModel.pid = doc.getElementsByTagName("Data").item(0).getTextContent();

                dealerModel.rdModel.skey = doc.getElementsByTagName("Skey").item(0).getTextContent();

                dealerModel. rdModel.ci = doc.getElementsByTagName("Skey").item(0).getAttributes().getNamedItem("ci").getTextContent();

                dealerModel.rdModel.hmac = doc.getElementsByTagName("Hmac").item(0).getTextContent();

                dealerModel. rdModel.type = doc.getElementsByTagName("Data").item(0).getAttributes().getNamedItem("type").getTextContent();

                dealerModel. rdModel.dpId = doc.getElementsByTagName("DeviceInfo").item(0).getAttributes().getNamedItem("dpId").getTextContent();

                dealerModel. rdModel.rdsId = doc.getElementsByTagName("DeviceInfo").item(0).getAttributes().getNamedItem("rdsId").getTextContent();

                dealerModel. rdModel.rdsVer = doc.getElementsByTagName("DeviceInfo").item(0).getAttributes().getNamedItem("rdsVer").getTextContent();

                dealerModel. rdModel.dc = doc.getElementsByTagName("DeviceInfo").item(0).getAttributes().getNamedItem("dc").getTextContent();

                dealerModel.rdModel.mi = doc.getElementsByTagName("DeviceInfo").item(0).getAttributes().getNamedItem("mi").getTextContent();

                dealerModel.rdModel.mc = doc.getElementsByTagName("DeviceInfo").item(0).getAttributes().getNamedItem("mc").getTextContent();

                dealerModel.rdModel.skey = dealerModel.rdModel.skey.replaceAll(" ", "\n");

            }
        } catch (Exception e) {
            e.printStackTrace();
            dealerModel.rdModel.errinfo = String.valueOf(e);
            return 2;
        }
        return 0;
    }

    public interface OnClickListener {
        void onClick_d(int p);
    }
}

