package com.visiontek.Mantra.Activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.visiontek.Mantra.Adapters.CustomAdapter1;
import com.visiontek.Mantra.Models.DataModel1;
import com.visiontek.Mantra.Models.cardDetails;
import com.visiontek.Mantra.Models.fpsCommonInfo;
import com.visiontek.Mantra.Models.fpsPofflineToken;
import com.visiontek.Mantra.Models.fpsURLInfo;
import com.visiontek.Mantra.Models.stateBean;
import com.visiontek.Mantra.R;
import com.visiontek.Mantra.Utils.DatabaseHelper;
import com.visiontek.Mantra.Utils.Json_Parsing;
import com.visiontek.Mantra.Utils.Util;
import java.util.ArrayList;
import java.util.Date;

import static com.visiontek.Mantra.Activities.Member_Details.MName;
import static com.visiontek.Mantra.Activities.Member_Details.MUid;
import static com.visiontek.Mantra.Activities.Member_Details.Mmemid;
import static com.visiontek.Mantra.Activities.Start.L;
import static com.visiontek.Mantra.Activities.Start.mp;
import static com.visiontek.Mantra.Utils.Util.DEVICEID;
import static com.visiontek.Mantra.Utils.Util.RDservice;
import static com.visiontek.Mantra.Utils.Util.releaseMediaPlayer;
import static com.visiontek.Mantra.Utils.Util.toast;

public class Ration_details extends AppCompatActivity {
    fpsURLInfo fpsURLInfo=new fpsURLInfo();
    fpsCommonInfo fpsCommonInfo=new fpsCommonInfo();
    stateBean stateBean=new stateBean();



    public static ArrayList<String> avlil = new ArrayList<>();
    public static ArrayList<String> bal = new ArrayList<>();
    public static ArrayList<String> close = new ArrayList<>();
    public static ArrayList<String> name = new ArrayList<>();
    public static ArrayList<String> code = new ArrayList<>();
    public static ArrayList<String> unit = new ArrayList<>();
    public static ArrayList<String> min = new ArrayList<>();
    public static ArrayList<String> price = new ArrayList<>();
    public static ArrayList<String> req = new ArrayList<>();
    public static ArrayList<String> total = new ArrayList<>();
    public static ArrayList<String> type = new ArrayList<>();
    public static ArrayList<String> month = new ArrayList<>();
    public static ArrayList<String> year = new ArrayList<>();
    public static ArrayList<String> wei = new ArrayList<>();


    public static int POSITION;
    public static ArrayList<String> Cavlil = new ArrayList<>();
    public static ArrayList<String> Cbal = new ArrayList<>();
    public static ArrayList<Float> Camount = new ArrayList<>();
    static ArrayList<Float> Cmin = new ArrayList<>();
    static ArrayList<Float> Cprice = new ArrayList<>();
    static ArrayList<Float> Closebal = new ArrayList<>();
    static ArrayList<String> Cname = new ArrayList<>();
    static ArrayList<String> Ccode = new ArrayList<>();
    static ArrayList<String> Ctype = new ArrayList<>();
    static ArrayList<String> Cmonth = new ArrayList<>();
    static ArrayList<String> Cyear = new ArrayList<>();
    static ArrayList<String> Ctotal = new ArrayList<>();

    static ArrayList<Float> COMMQNTY = new ArrayList<>();
    static ArrayList<Integer> COMMPOSITION = new ArrayList<>();

    Button confirm, back;
    String ration;
    ArrayList<DataModel1> modeldata;
    Context context;
    ProgressDialog pd = null;
    DatabaseHelper databaseHelper;

    RecyclerView.Adapter adapter;
    RecyclerView recyclerView;
    TextView rd;

    static double TOTALAMOUNT;

    String Ref;
    cardDetails cardDetails=new cardDetails();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ration_details);
        POSITION = 0;
        context = Ration_details.this;
        pd = new ProgressDialog(context);

        databaseHelper = new DatabaseHelper(context);

        confirm = findViewById(R.id.confirm);
        back = findViewById(R.id.ration_back);

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
        Ref = getIntent().getStringExtra("REF");
        avlil = databaseHelper.get_RD(0);
        bal = databaseHelper.get_RD(1);
        close = databaseHelper.get_RD(2);
        name = databaseHelper.get_RD(3);
        code = databaseHelper.get_RD(4);
        unit = databaseHelper.get_RD(5);
        min = databaseHelper.get_RD(6);
        price = databaseHelper.get_RD(7);
        req = databaseHelper.get_RD(8);
        total = databaseHelper.get_RD(9);
        wei = databaseHelper.get_RD(10);
        type = databaseHelper.get_RD(11);
        month = databaseHelper.get_RD(12);
        year = databaseHelper.get_RD(13);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        recyclerView = findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        modeldata = new ArrayList<>();

        for (int i = 0; i < name.size(); i++) {
            modeldata.add(new DataModel1(name.get(i) + "\n(" + total.get(i) + ")", bal.get(i), price.get(i), "0.0", close.get(i)));
        }

        Display();

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                conformRation(Camount, COMMQNTY, COMMPOSITION);

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog();
               /* AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setMessage(context.getResources().getString(R.string.Do_you_want_to_cancel_Session));
                alertDialogBuilder.setCancelable(false);
                alertDialogBuilder.setPositiveButton(context.getResources().getString(R.string.Yes),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                System.out.println("++++++++++1");

                            }
                        });
                alertDialogBuilder.setNegativeButton(context.getResources().getString(R.string.No),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();*/
            }
        });
    }
    String rsnName,rsnid;
    private void dialog() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(Ration_details.this);
        builderSingle.setTitle("Please Selcet below Reason");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(Ration_details.this, android.R.layout.select_dialog_singlechoice);
        final ArrayList<String> reasons = databaseHelper.get_CancelRequest(1);
        final ArrayList<String> reasonsid = databaseHelper.get_CancelRequest(0);
        for (int i=0;i<reasons.size();i++){
            arrayAdapter.add(reasons.get(i));
        }
        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                 rsnName = arrayAdapter.getItem(which);
                 rsnid=reasonsid.get(which);
                AlertDialog.Builder builderInner = new AlertDialog.Builder(Ration_details.this);
                builderInner.setMessage(rsnName);
                builderInner.setTitle("Your Selected Item is");
                builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int which) {
                        System.out.println("++++++++++1");
                        dialog.dismiss();
                        cancelRequest();
                    }
                });
                builderInner.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int which) {

                    }
                });
                builderInner.show();
            }
        });
        builderSingle.show();
    }

    private void cancelRequest() {
        String mos="P";
        String mt="R";
        String currentDateTimeString = java.text.DateFormat.getDateTimeInstance().format(new Date());
        currentDateTimeString="23032021163452";

        String reasons="{\n" +
                "   \"fpsId\" :"+ "\"" +stateBean.getstatefpsId()+ "\""+",\n" +
                "   \"modeOfService\" : "+"\""+mos+"\""+",\n" +
                "   \"moduleType\" :"+"\""+mt+"\""+",\n" +
                "   \"rcId\" : "+"\""+cardDetails.getRcId()+"\""+",\n" +
                "   \"requestId\" :"+ "\"" + rsnid+ "\"" +",\n" +
                "   \"requestValue\" :"+"\""+rsnName+"\""+",\n" +
                "   \"sessionId\" : "+"\""+fpsCommonInfo.getfpsSessionId()+"\""+",\n" +
                "   \"stateCode\" : "+"\""+stateBean.getstateCode()+"\""+",\n" +
                "   \"terminalId\" : "+"\""+DEVICEID+"\""+",\n" +
                "   \"timeStamp\" : "+"\""+currentDateTimeString+"\""+",\n" +
                "   \"token\" : "+"\""+fpsURLInfo.gettoken()+"\""+"\n" +
                "}";
        Util.generateNoteOnSD(context, "CancelRequestReq.txt", ration);
        pd = ProgressDialog.show(context, "Please Wait ", context.getResources().getString(R.string.Processing), true, false);
        Json_Parsing request = new Json_Parsing(context, reasons, 2);
        request.setOnResultListener(new Json_Parsing.OnResultListener() {

            @Override
            public void onCompleted(String code, String msg) {
                if (pd.isShowing()) {
                    pd.dismiss();
                }

                if (!code.equals("00")) {
                   // show_error_box(msg, context.getResources().getString(R.string.Commodities_Error) + code);
                } else {
                    clearArray();
                    finish();
                }
            }

        });
    }

    private void Display() {
        adapter = new CustomAdapter1(context, modeldata, new OnClickListener() {
            @Override
            public void onClick_d(int p) {
                POSITION = p;
                if (wei.get(POSITION).equals("N")) {

                    Intent weight = new Intent(context, WeightActivity.class);
                    //weight.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivityForResult(weight, 1);
                } else {

                    Intent Weighing = new Intent(context, WeighingActivity.class);
                    // Weighing.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivityForResult(Weighing, 1);
                }
            }
        }, 1);
        recyclerView.setAdapter(adapter);
    }

    private String add_comm(ArrayList<Float> camount1, ArrayList<Float> vqnty, ArrayList<Integer> place) {
        TOTALAMOUNT = 0.0;
        StringBuilder add = new StringBuilder();
        String str;
        System.out.println("PLACE SIZE = " + place.size() + "CAMOUNT SIZE = " + camount1.size() + "VQUNTY SIZE = " + vqnty.size());
        if (place.size() > 0) {
            for (int i = 0; i < place.size(); i++) {
                TOTALAMOUNT = TOTALAMOUNT + camount1.get(i);
                System.out.println("PLACE VALUE = " + place.get(i) + "CAMOUNT VALUE = " + camount1.get(i) + "VQUNTY VALUE = " + vqnty.get(i));
                str = "<commodityDetail>\n" +
                        "<allocationType>" + Ctype.get(i) + "</allocationType>\n" +
                        "<allotedMonth>" + Cmonth.get(i) + "</allotedMonth>\n" +
                        "<allotedYear>" + Cyear.get(i) + "</allotedYear>\n" +
                        "<commCode>" + Ccode.get(i) + "</commCode>\n" +
                        "<commName>" + Cname.get(i) + "</commName>\n" +
                        "<requiredQuantity>" + vqnty.get(i) + "</requiredQuantity>\n" +
                        "<commodityAmount>" + camount1.get(i) + "</commodityAmount>\n" +
                        "<price>" + Cprice.get(i) + "</price>\n" +
                        "</commodityDetail>\n";
                add.append(str);
            }
            return String.valueOf(add);
        }
        return "0";
    }

    private void conformRation(ArrayList<Float> camount, ArrayList<Float> vqnty, ArrayList<Integer> place) {
        String com = add_comm(camount, vqnty, place);
        if (!com.equals("0")) {
            ration = "<?xml version='1.0' encoding='UTF-8' standalone='no' ?>\n" +
                    "<SOAP-ENV:Envelope\n" +
                    "    xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
                    "    xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\"\n" +
                    "    xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n" +
                    "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                    "    xmlns:ns1=\"http://service.fetch.rationcard/\">\n" +
                    "    <SOAP-ENV:Body>\n" +
                    "        <ns1:getCommodityTransaction>\n" +
                    "            <fpsSessionId>" + fpsCommonInfo.getfpsSessionId() + "</fpsSessionId>\n" +
                    "            <stateCode>" + stateBean.getstateCode() + "</stateCode>\n" +
                    "            <exRCNumber>" + cardDetails.getRcId() + "</exRCNumber>\n" +
                    "            <shop_no>" + stateBean.getstatefpsId() + "</shop_no>\n" +
                    "            <deviceId>" + DEVICEID + "</deviceId>\n" +
                    "            <rationCardId>" + cardDetails.getRcId() + "</rationCardId>\n" +
                    "            <schemeId>"+cardDetails.getschemeId()+"</schemeId>\n" +
                    com + "\n" +
                    "            <recieptId>3863389061819</recieptId>\n" +
                    "            <totAmount>" + TOTALAMOUNT + "</totAmount>\n" +
                    "            <uid_no>" + MUid + "</uid_no>\n" +
                    "            <uid_ref_no>" + Ref + "</uid_ref_no>\n" +
                    "            <card_type></card_type>\n" +
                    "            <password>" + fpsURLInfo.gettoken() + "</password>\n" +
                    "            <memberId>" + Mmemid + "</memberId>\n" +
                    "            <surveyEntryQuantity>0.0</surveyEntryQuantity>\n" +
                    "            <surveyStatus>N</surveyStatus>\n" +
                    "            <trans_type>F</trans_type>\n" +
                    "            <availedBenfName>" + MName + "</availedBenfName>\n" +
                    "        </ns1:getCommodityTransaction>\n" +
                    "    </SOAP-ENV:Body>\n" +
                    "</SOAP-ENV:Envelope>";
            if (Util.networkConnected(context)) {
                Util.generateNoteOnSD(context, "RationReq.txt", ration);
                hitURL(ration);
            } else {


                show_error_box(context.getResources().getString(R.string.Internet_Connection_Msg),context.getResources().getString(R.string.Internet_Connection));
            }
        } else {
            if (mp!=null) {
                releaseMediaPlayer(context,mp);
            }
            if (L.equals("hi")) {
            } else {
           mp =mp.create(context,R.raw.c100189);
                   mp.start();
            toast(context, context.getResources().getString(R.string.Invalid_Inputs));
        }}
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    private void hitURL(String xmlformat) {

       /* avlil.clear();
        bal.clear();
        close.clear();
        name.clear();
        code.clear();
        unit.clear();
        min.clear();
        price.clear();
        req.clear();
        total.clear();
        type.clear();
        month.clear();
        year.clear();
        wei.clear();*/
        Intent print = new Intent(getApplicationContext(), Print.class);
        print.putExtra("key",xmlformat);
        print.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(print);


       /* mp = MediaPlayer.create(context,R.raw.c100076);
        mp.start();
        pd = ProgressDialog.show(context, context.getResources().getString(R.string.Confirm), context.getResources().getString(R.string.Please_Wait), true, false);
        XML_Parsing request = new XML_Parsing(Ration_details.this, xmlformat, 11);
        request.setOnResultListener(new XML_Parsing.OnResultListener() {
            @Override
            public void onCompleted(String isError, String msg, String ref, String flow) {

                if (pd.isShowing()) {
                    pd.dismiss();
                }

                if (!isError.equals("00")) {
                    System.out.println("SESSION TIMED OUT");
                    show_error_box(msg, "Commodity : " + isError);

                } else {

                    pref = ref;
                    prcid = flow;
                    COMMPOSITION.clear();
                    COMMQNTY.clear();

                    avlil.clear();
                    bal.clear();
                    close.clear();
                    name.clear();
                    code.clear();
                    unit.clear();
                    min.clear();
                    price.clear();
                    req.clear();
                    total.clear();
                    type.clear();
                    month.clear();
                    year.clear();
                    wei.clear();

                    Camount.clear();
                    Cmin.clear();
                    Cprice.clear();
                    Closebal.clear();
                    Cmonth.clear();
                    Ccode.clear();
                    Ctype.clear();
                    Cyear.clear();

                    Intent print = new Intent(getApplicationContext(), Print.class);
                    print.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(print);
                    finish();
                }
            }
        });
        request.execute();*/
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            modeldata.clear();
            for (int i = 0; i < name.size(); i++) {
                if (COMMQNTY.size() >= 1) {
                    if (!check(i)) {
                        modeldata.add(new DataModel1(name.get(i) + "\n(" + total.get(i) + ")", bal.get(i), price.get(i), "0.0", close.get(i)));
                    }
                } else {
                    modeldata.add(new DataModel1(name.get(i) + "\n(" + total.get(i) + ")", bal.get(i), price.get(i), "0.0", close.get(i)));
                }
            }
            Display();
        }
    }

    private boolean check(int i) {
        String req;
        for (int j = 0; j < COMMPOSITION.size(); j++) {
            if (i == COMMPOSITION.get(j)) {
                req = String.valueOf(COMMQNTY.get(j));
                modeldata.add(new DataModel1(name.get(i) + "\n(" + total.get(i) + ")", bal.get(i), price.get(i), req, close.get(i)));
                return true;
            }
        }
        return false;
    }

    public interface OnClickListener {
        void onClick_d(int p);
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


    @Override
    public void onBackPressed() {
        clearArray();
        super.onBackPressed();
    }

    private void clearArray() {
        COMMPOSITION.clear();
        COMMQNTY.clear();
        Cavlil.clear();
        Cbal.clear();
        Camount.clear();
        Cmin.clear();
        Cprice.clear();
        Closebal.clear();
        Cmonth.clear();
        Cname.clear();
        Ccode.clear();
        Ctype.clear();
        Cyear.clear();
        Ctotal.clear();

    }
}
