package com.visiontek.Mantra.Activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.visiontek.Mantra.Models.fpsCommonInfo;
import com.visiontek.Mantra.Models.fpsPofflineToken;
import com.visiontek.Mantra.Models.fpsURLInfo;
import com.visiontek.Mantra.Models.stateBean;
import com.visiontek.Mantra.R;
import com.visiontek.Mantra.Utils.Aadhaar_Parsing;
import com.visiontek.Mantra.Utils.Util;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;


import static com.visiontek.Mantra.Activities.Start.L;
import static com.visiontek.Mantra.Activities.Start.mp;
import static com.visiontek.Mantra.Utils.Util.RDservice;
import static com.visiontek.Mantra.Utils.Util.encrypt;
import static com.visiontek.Mantra.Utils.Util.networkConnected;
import static com.visiontek.Mantra.Utils.Util.releaseMediaPlayer;
import static com.visiontek.Mantra.Utils.Veroeff.validateVerhoeff;

public class Aadhaar_Seeding extends AppCompatActivity {
    public static String UID_ID, UID_Aadhaar;
    static String UID_zWadh;
    Button back, details;
    Context context;
    RadioButton radiorc, radioaadhaar;
    EditText id;
    int select;
    ProgressDialog pd = null;
    RadioGroup radioGroup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_u_i_d__seeding);
        context = Aadhaar_Seeding.this;
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
        details = findViewById(R.id.button_ok);
        back = findViewById(R.id.button_back);
        id = findViewById(R.id.et_id);
        radioGroup = findViewById(R.id.groupradio);
        details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UID_ID = id.getText().toString().trim();
                if (UID_ID.length() == 12) {
                    UidSeeding();
                } else {if(select==2){
                    show_error_box(context.getResources().getString(R.string.Please_Enter_a_Valid_Number_UID), context.getResources().getString(R.string.Invalid_UID));
                }else {
                    show_error_box(context.getResources().getString(R.string.Please_Enter_a_Valid_Number_RC), context.getResources().getString(R.string.Invalid_ID));
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
    }

    public void onRadioButtonClicked(View v) {
        radiorc = findViewById(R.id.radio_rc_no);
        radioaadhaar = findViewById(R.id.radio_aadhaar);
        boolean checked = ((RadioButton) v).isChecked();
        if (checked) {
            switch (v.getId()) {
                case R.id.radio_rc_no:
                    select = 1;
                    radiorc.setTypeface(null, Typeface.BOLD_ITALIC);
                    radioaadhaar.setTypeface(null, Typeface.NORMAL);

                    id.setText("");
                    if (mp!=null) {
                        releaseMediaPlayer(context,mp);
                    }
                    if(L.equals("hi")) {
                       mp = mp.create(context, R.raw.c200043);
                               mp.start();
                    }

                    else {
                       mp = mp.create(context, R.raw.c100043);
                               mp.start();
                    }

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
    fpsPofflineToken fpsPofflineToken=new fpsPofflineToken();
    fpsCommonInfo fpsCommonInfo=new fpsCommonInfo();
    stateBean stateBean=new stateBean();
    fpsURLInfo fpsURLInfo=new fpsURLInfo();
    private void UidSeeding() {
        String uidseeding = null;
        UID_Aadhaar = UID_ID;
        if (select == 2) {
            if (validateVerhoeff(UID_Aadhaar)) {
                try {
                    UID_Aadhaar = encrypt(UID_Aadhaar, fpsPofflineToken.getskey());
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
                uidseeding = "<?xml version='1.0' encoding='UTF-8' standalone='no' ?>\n" +
                        "<SOAP-ENV:Envelope\n" +
                        "    xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
                        "    xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\"\n" +
                        "    xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n" +
                        "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                        "    xmlns:ns1=\"http://service.fetch.rationcard/\">\n" +
                        "    <SOAP-ENV:Body>\n" +
                        "        <ns1:getEKYCMemberDetailsRD>\n" +
                        "            <fpsSessionId>" + fpsCommonInfo.getfpsSessionId() + "</fpsSessionId>\n" +
                        "            <stateCode>" + stateBean.getstateCode() + "</stateCode>\n" +
                        "            <id>" + UID_Aadhaar + "</id>\n" +
                        "            <idType>U</idType>\n" +
                        "            <fpsID>" + stateBean.getstatefpsId() + "</fpsID>\n" +
                        "            <password>" + fpsURLInfo.gettoken() + "</password>\n" +
                        "            <hts></hts>\n" +
                        "        </ns1:getEKYCMemberDetailsRD>\n" +
                        "    </SOAP-ENV:Body>\n" +
                        "</SOAP-ENV:Envelope>";
                Util.generateNoteOnSD(context, "UidSeedingwithUIDReq.txt", uidseeding);
            }else {
                if (mp!=null) {
                    releaseMediaPlayer(context,mp);
                }
               mp = mp.create(context,R.raw.c100047);
                       mp.start();
                show_error_box(context.getResources().getString(R.string.Please_Enter_Valid_Number), context.getResources().getString(R.string.Invalid_UID));
            }
        } else {

            uidseeding = "<?xml version='1.0' encoding='UTF-8' standalone='no' ?>\n" +
                    "<SOAP-ENV:Envelope\n" +
                    "    xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
                    "    xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\"\n" +
                    "    xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n" +
                    "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                    "    xmlns:ns1=\"http://service.fetch.rationcard/\">\n" +
                    "    <SOAP-ENV:Body>\n" +
                    "        <ns1:getEKYCMemberDetailsRD>\n" +
                    "            <fpsSessionId>" + fpsCommonInfo.getfpsSessionId() + "</fpsSessionId>\n" +
                    "            <stateCode>" + stateBean.getstateCode() + "</stateCode>\n" +
                    "            <id>" + UID_Aadhaar + "</id>\n" +
                    "            <idType>R</idType>\n" +
                    "            <fpsID>" + stateBean.getstatefpsId() + "</fpsID>\n" +
                    "            <password>" + fpsURLInfo.gettoken() + "</password>\n" +
                    "            <hts></hts>\n" +
                    "        </ns1:getEKYCMemberDetailsRD>\n" +
                    "    </SOAP-ENV:Body>\n" +
                    "</SOAP-ENV:Envelope>";
            Util.generateNoteOnSD(context, "UidSeedingwithRCReq.txt", uidseeding);
        }
        if (networkConnected(context)) {
            hit_uidseeding(uidseeding);
        } else {
            show_error_box(context.getResources().getString(R.string.Internet_Connection_Msg),context.getResources().getString(R.string.Internet_Connection));
        }
    }

    private void hit_uidseeding(String uiddetails) {
        if (mp!=null) {
            releaseMediaPlayer(context,mp);
        }
        if(L.equals("hi"))
        {
        }else {
            mp = mp.create(context, R.raw.c100075);
            mp.start();
        }
        pd = ProgressDialog.show(context, context.getResources().getString(R.string.Aadhaar_Seeding), context.getResources().getString(R.string.Fetching_Members), true, false);
        Aadhaar_Parsing request = new Aadhaar_Parsing(context, uiddetails, 1);
        request.setOnResultListener(new Aadhaar_Parsing.OnResultListener() {

            @Override
            public void onCompleted(String error, String msg, String ref, ArrayList<String> buffer) {
                if (pd.isShowing()) {
                    pd.dismiss();
                }
                if (!error.equals("00")) {
                    System.out.println("ERRORRRRRRRRRRRRRRRRRRRR");
                    show_error_box(msg, context.getResources().getString(R.string.Error_Code) + error);
                } else {
                    UID_zWadh = ref;
                    Intent uid = new Intent(context, UID_Details.class);
                    startActivity(uid);
                    System.out.println("SUCCESSSSSSSSSSSS");
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

}
