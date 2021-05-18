package com.visiontek.Mantra.Utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;


import com.visiontek.Mantra.Models.fpsURLInfo;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Json_Parsing {

    fpsURLInfo fpsURLInfo=new fpsURLInfo();


    String myResponse;
    String msg, code;
    private final DatabaseHelper databaseHelper;
    private OnResultListener onResultListener;
    Context context;
    public Json_Parsing(Context context, String frame, int type) {

        System.out.println("@@In Json_Parsing constructor");
        context=context;
        databaseHelper = new DatabaseHelper(context);
        Parsing(frame, type);
    }

    public void setOnResultListener(OnResultListener onResultListener) {
        this.onResultListener = onResultListener;
    }

    public void Parsing(String frame, final int type) {
        System.out.println("@@ Parsing Data..." + frame);
        OkHttpClient okHttpClient;
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        RequestBody body = null;
        body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), frame);
        System.out.println("BOdy.." + body);
        String url;

        System.out.println("@@ Type: " +type);

        if (type==2 || type==3){
            url= fpsURLInfo.getwsdlOffline()+"reasonConsent";
            System.out.println(url);
        }else if (type==5){
            url="http://epos.nic.in/ePosCommonServiceCTG/eposCommon/getFpsOfflineData";
        }else if (type==6){
            url="http://epos.nic.in/ePosCommonServiceCTG/eposCommon/getCbUpdate";
        }else  {
            url="http://epos.nic.in/ePosCommonServiceCTG/eposCommon/getFpsStockDetails";
        }

        final Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                final String myResponse = e.toString();
                System.out.println("@@ Failure Response...." + myResponse);
                JSONObject reader;
                try {
                    reader = new JSONObject(myResponse);
                    msg = reader.getString("respMessage");
                    code = reader.getString("respCode");
                } catch (JSONException jsonException) {
                    jsonException.printStackTrace();
                }
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            callback();
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                    }
                });

                System.out.println("error " + e);
            }

            @Override
            public void onResponse(@NotNull okhttp3.Call call, @NotNull Response response) throws IOException {

                //myResponse = response.body().string();


                myResponse="{\n" +
                        "    \"keyRegister\": [\n" +
                        "        {\n" +
                        "            \"rcId\": \"123456789123\",\n" +
                        "            \"commNameEn\": \"Sugar-8\",\n" +
                        "            \"commNameLl\": \"Sugar-8\",\n" +
                        "            \"commCode\": \"3\",\n" +
                        "            \"totalEntitlement\": 200.0,\n" +
                        "            \"balanceEntitlement\": 10.0,\n" +
                        "            \"schemeId\": \"31\",\n" +
                        "            \"schemeName\": \"PHH\",\n" +
                        "            \"commPrice\": 3.0,\n" +
                        "            \"measurmentUnit\": \"Kgs\",\n" +
                        "            \"memberNameLl\": \"Jayanth\",\n" +
                        "            \"memberNameEn\": \"Jayanth\",\n" +
                        "            \"month\": \"8\",\n" +
                        "            \"year\": \"2020\",\n" +
                        "            \"allocationType\": \"R\",\n" +
                        "            \"allotedMonth\": \"8\",\n" +
                        "            \"allotedYear\": \"2020\",\n" +
                        "            \"memberId\": \"1234567891233\"\n" +
                        "        }\n" +
                        "    ],\n" +
                        "    \"fpsCb\": [\n" +
                        "        {\n" +
                        "            \"commCode\": \"2\",\n" +
                        "            \"commNameEn\": \"Rice\",\n" +
                        "            \"commNameLl\": \"Rice\",\n" +
                        "            \"closingBalance\": 2198.0\n" +
                        "        },\n" +
                        "        {\n" +
                        "            \"commCode\": \"3\",\n" +
                        "            \"commNameEn\": \"Sugar\",\n" +
                        "            \"commNameLl\": \"Sugar\",\n" +
                        "            \"closingBalance\": 4836.0\n" +
                        "        }\n" +
                        "    ],\n" +
                        "    \"schemeMaster\": [\n" +
                        "        {\n" +
                        "            \"schemeId\": \"2\",\n" +
                        "            \"schemeName\": \"AAY\"\n" +
                        "        },\n" +
                        "        {\n" +
                        "            \"schemeId\": \"31\",\n" +
                        "            \"schemeName\": \"PHH\"\n" +
                        "        }\n" +
                        "    ],\n" +
                        "    \"commodityMaster\": [\n" +
                        "        {\n" +
                        "            \"commCode\": \"2\",\n" +
                        "            \"commNameLl\": \"Rice\",\n" +
                        "            \"commNameEn\": \"Rice\",\n" +
                        "            \"measurmentUnit\": \"Kgs\",\n" +
                        "            \"commonCommCode\": \"2\"\n" +
                        "        },\n" +
                        "        {\n" +
                        "            \"commCode\": \"3\",\n" +
                        "            \"commNameLl\": \"Sugar\",\n" +
                        "            \"commNameEn\": \"Sugar\",\n" +
                        "            \"measurmentUnit\": \"Kgs\",\n" +
                        "            \"commonCommCode\": \"3\"\n" +
                        "        }\n" +
                        "    ],\n" +
                        "    \"challanReceived\": null,\n" +
                        "    \"respCode\": \"00\",\n" +
                        "    \"respMessage\": \"Success\"\n" +
                        "}";
                System.out.println("response : " + myResponse);
                if (type == 1) {
                    Util.generateNoteOnSD(context, "RecivegoodsRes.txt", myResponse);
                    parse_recivegoods(myResponse);
                }else if(type==2){
                    Util.generateNoteOnSD(context, "CancelRequestRes.txt", myResponse);
                    parse_cancelReq(myResponse);
                }else if (type==3){
                    Util.generateNoteOnSD(context, "ConsentFormRes.txt", myResponse);
                    parse_consentform(myResponse);
                }else if (type==4){
                    Util.generateNoteOnSD(context, "LogoutRes.txt", myResponse);
                    parse_logout(myResponse);
                }else if (type==5){
                    System.out.println("@@ generateNoteOnSD");
                    Util.generateNoteOnSD(context, "KeyRegestration.txt", myResponse);
                    try {
                        parsegetFpsOfflineData(myResponse,context);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else if (type==6){
                    Util.generateNoteOnSD(context, "CBDownload.txt", myResponse);
                    try {
                        parsegetCbUpdate(myResponse,context);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else if (type==7){
                    Util.generateNoteOnSD(context, "PartialOnlineData.txt", myResponse);
                    try {
                        parsePartialOnlineData(myResponse,context);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        });
    }
    public void parsePartialOnlineData(String response,Context context)
    {
        DatabaseHelper helper = new DatabaseHelper(context);
        String OffPassword = "";
        String OfflineLogin = "";
        String OfflineTxnTime = "";
        String Duration = "";
        String leftOfflineTime = "";
        String lastlogindate = "";
        String lastlogintime = "";
        String lastlogoutdate = "";
        String lastlogouttime = "";
        String AllotMonth = "";
        String AllotYear = "";
        String pOfflineStoppedDate = "";

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(response));
            int eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("fpsToken")) {
                        eventType = xpp.next();
                        if (eventType == XmlPullParser.TEXT) {

                            OffPassword = (xpp.getText());
                            System.out.println("dealer_password 1 =================" + xpp.getText());
                        }
                    }

                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("OfflineLogin")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                OfflineLogin = (xpp.getText());
                                System.out.println("OfflineLogin 2 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("OfflineTxnTime")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                OfflineTxnTime = (xpp.getText());
                                System.out.println("OfflineTxnTime 3 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("pOfflineDurationTimeInaDay")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                Duration = (xpp.getText());
                                System.out.println("Duration 4 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("leftOfflineTime")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                leftOfflineTime = (xpp.getText());
                                System.out.println("leftOfflineTime 5 =================" + xpp.getText());
                            }
                        }
                    }

                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("allocationMonth")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                AllotMonth = (xpp.getText());
                                System.out.println("AllotMonth 8 =================" + xpp.getText());
                            }
                        }
                    }

                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("allocationYear")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                AllotYear = (xpp.getText());
                                System.out.println("AllotYear 8 =================" + xpp.getText());
                            }
                        }
                    }

                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("pOfflineStoppedDate")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                pOfflineStoppedDate = (xpp.getText());
                                System.out.println("pOfflineStoppedDate 8 =================" + xpp.getText());
                            }
                        }
                    }


                }
                eventType = xpp.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Date currentTime = (Date) Calendar.getInstance().getTime();
        lastlogindate = currentTime.toString();
        lastlogintime = currentTime.toString();
        lastlogoutdate = currentTime.toString();
        lastlogouttime = currentTime.toString();
        leftOfflineTime = "NODATAFOUND";
        String query = "insert into PartialOnlineData values ('" +OffPassword +"','" +OfflineLogin +"','" +OfflineTxnTime +"','" +Duration +"','" +leftOfflineTime +"','" +lastlogindate +"'','" +lastlogintime +"','" +lastlogoutdate +"','" +lastlogouttime +"','" +AllotMonth +"','" +AllotYear +"','" +pOfflineStoppedDate +"');";
        System.out.println("Query: " +query);
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL(query);
    }
    public void parsegetCbUpdate(String strJson, Context context) throws Exception {

        DatabaseHelper helper = new DatabaseHelper(context);

        JSONObject jsonRootObject = new JSONObject(strJson);

        JSONArray jsonArray1 = jsonRootObject.optJSONArray("fpsCb");
        for(int i=0; i < jsonArray1.length(); i++) {
            JSONObject jsonObject = jsonArray1.getJSONObject(i);
            String commCode = jsonObject.optString("commCode").toString();
            String commNameEn = jsonObject.optString("commNameEn").toString();
            String commNameLl = jsonObject.optString("commNameLl").toString();
            String closingBalance = jsonObject.optString("closingBalance").toString();

            String query = "insert into Pos_Ob values('" +commCode +"','"+commNameEn +"','" +commNameLl +"','" +closingBalance +"');";
            System.out.println("Query: " +query);
            SQLiteDatabase db = helper.getWritableDatabase();
            db.execSQL(query);
        }
    }



    public void parsegetFpsOfflineData(String strJson, Context context) throws Exception {
        System.out.println("@@ parsegetFpsOfflineData");
        JSONObject jsonRootObject = new JSONObject(strJson);
        JSONArray jsonArray = jsonRootObject.optJSONArray("keyRegister");
        System.out.println(jsonArray.length());
        for(int i=0; i < jsonArray.length(); i++){
            System.out.println("----------"+i);
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String rcId = jsonObject.optString("rcId").toString();
            String commNameEn = jsonObject.optString("commNameEn").toString();
            String commNameLl = jsonObject.optString("commNameLl").toString();
            String commCode = jsonObject.optString("commCode").toString();
            String totalEntitlement = jsonObject.optString("totalEntitlement").toString();
            String balanceEntitlement = jsonObject.optString("balanceEntitlement").toString();
            String schemeId = jsonObject.optString("schemeId").toString();
            String schemeName = jsonObject.optString("schemeName").toString();
            String commPrice = jsonObject.optString("commPrice").toString();
            String measurmentUnit = jsonObject.optString("measurmentUnit").toString();
            String memberNameLl = jsonObject.optString("memberNameLl").toString();
            String memberNameEn = jsonObject.optString("memberNameEn").toString();
            String month = jsonObject.optString("month").toString();
            String year = jsonObject.optString("year").toString();
            String allocationType = jsonObject.optString("allocationType").toString();
            String allotedMonth = jsonObject.optString("allotedMonth").toString();
            String allotedYear = jsonObject.optString("allotedYear").toString();
            String memberId = jsonObject.optString("memberId").toString();

            System.out.println("@@ parsegetFpsOfflineDataQ");
            String query = "insert into KeyRegister values('"+rcId+"','"+commNameEn+"','"+commNameLl+"','"+commCode+"','"+totalEntitlement+"','"+balanceEntitlement+"','"+schemeId+"','"+schemeName+"','"+commPrice+"','"+measurmentUnit+"','"+memberNameLl+"','"+memberNameEn+"','"+month+"','"+year+"','"+allocationType+"','"+allotedMonth+"','"+allotedYear+"');";
            System.out.println("Query: " +query);
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            db.execSQL(query);
            db.close();
        }

        JSONArray jsonArray1 = jsonRootObject.optJSONArray("fpsCb");
        for(int i=0; i < jsonArray1.length(); i++) {
            JSONObject jsonObject = jsonArray1.getJSONObject(i);
            String commCode = jsonObject.optString("commCode").toString();
            String commNameEn = jsonObject.optString("commNameEn").toString();
            String commNameLl = jsonObject.optString("commNameLl").toString();
            String closingBalance = jsonObject.optString("closingBalance").toString();

            String query = "insert into Pos_Ob values('" +commCode +"','"+commNameEn +"','" +commNameLl +"','" +closingBalance +"');";
            System.out.println("Query: " +query);
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            db.execSQL(query);
            db.close();
        }

        JSONArray jsonArray2 = jsonRootObject.optJSONArray("schemeMaster");
        for(int i=0; i < jsonArray2.length(); i++) {
            JSONObject jsonObject = jsonArray2.getJSONObject(i);
            String schemeId = jsonObject.optString("schemeId").toString();
            String schemeName = jsonObject.optString("schemeName").toString();

            String query = "insert into schemeMaster values('" +schemeId +"','"+schemeName +"');";
            System.out.println("Query: " +query);
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            db.execSQL(query);
            db.close();
        }

        JSONArray jsonArray3 = jsonRootObject.optJSONArray("commodityMaster");
        for(int i=0; i < jsonArray3.length(); i++) {
            JSONObject jsonObject = jsonArray3.getJSONObject(i);
            String commCode = jsonObject.optString("commCode").toString();
            String commNameLl = jsonObject.optString("commNameLl").toString();
            String commNameEn = jsonObject.optString("commNameEn").toString();
            String measurmentUnit = jsonObject.optString("measurmentUnit").toString();
            String commonCommCode = jsonObject.optString("commonCommCode").toString();
            String query = "insert into commodityMaster values('" +commCode +"','"+commNameLl +"','" +commNameEn +"','" +measurmentUnit +"','" +commonCommCode +"');";
            System.out.println("Query: " +query);
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            db.execSQL(query);
            db.close();
        }

        System.out.println("@@Done!!!! fps download");
    }

    private void parse_consentform(String myResponse) {

        try {
            JSONObject reader;
            reader = new JSONObject(myResponse);

            msg = reader.getString("respMessage");
            code = reader.getString("respCode");
            /*if (code.equals("00")) {

            }*/
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                try {
                    callback();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        });

    }

    private void parse_logout(String myResponse) {
        try {
            JSONObject reader;
            reader = new JSONObject(myResponse);

            msg = reader.getString("respMessage");
            code = reader.getString("respCode");
            /*if (code.equals("00")) {

            }*/
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                try {
                    callback();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        });

    }

    private void parse_cancelReq(String myResponse) {
        try {
            JSONObject reader;
            reader = new JSONObject(myResponse);

            msg = reader.getString("respMessage");
            code = reader.getString("respCode");
            /*if (code.equals("00")) {

            }*/
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                try {
                    callback();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        });
    }



    private void parse_recivegoods(String myResponse) {

        try {
            JSONObject reader;
            String fps, allotmonth, allotyear, truckChit, challanId, allocationOrderNo, truckNo;
            String rel_Quantity, Allotment, Code, Name, SId, SName;

            ArrayList<String> CommLength = new ArrayList<>();
            ArrayList<String> Truck_fps = new ArrayList<>();
            ArrayList<String> Truck_allotmonth = new ArrayList<>();
            ArrayList<String> Truck_allotyear = new ArrayList<>();
            ArrayList<String> Truck_truckChit = new ArrayList<>();
            ArrayList<String> Truck_challanId = new ArrayList<>();
            ArrayList<String> Truck_allocationOrderNo = new ArrayList<>();
            ArrayList<String> Truck_truckNo = new ArrayList<>();

            ArrayList<String> Truck_rel_Quantity = new ArrayList<>();
            ArrayList<String> Truck_Allotment = new ArrayList<>();
            ArrayList<String> Truck_Code = new ArrayList<>();
            ArrayList<String> Truck_Name = new ArrayList<>();
            ArrayList<String> Truck_SId = new ArrayList<>();
            ArrayList<String> Truck_SName = new ArrayList<>();

            reader = new JSONObject(myResponse);

            msg = reader.getString("respMessage");
            code = reader.getString("respCode");
            if (code.equals("00")) {
                JSONArray jsonArray = reader.getJSONArray("infoTCDetails");
                System.out.println("------------" + jsonArray.length());
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jb = jsonArray.getJSONObject(i);
                    fps = jb.getString("fpsId");
                    allotmonth = jb.getString("allotedMonth");
                    allotyear = jb.getString("allotedYear");
                    truckChit = jb.getString("truckChitNo");
                    challanId = jb.getString("challanId");
                    allocationOrderNo = jb.getString("allocationOrderNo");
                    truckNo = jb.getString("truckNo");
                    Truck_fps.add(fps);
                    Truck_allotmonth.add(allotmonth);
                    Truck_allotyear.add(allotyear);
                    Truck_truckChit.add(truckChit);
                    Truck_challanId.add(challanId);
                    Truck_allocationOrderNo.add(allocationOrderNo);
                    Truck_truckNo.add(truckNo);
                    System.out.println("+++++++++++++++++++++++++JSON ");
                    JSONArray CommDetails = jb.getJSONArray("tcCommDetails");

                    CommLength.add(String.valueOf(CommDetails.length()));
                    for (int j = 0; j < CommDetails.length(); j++) {
                        JSONObject commDetails = CommDetails.getJSONObject(j);
                        rel_Quantity = commDetails.getString("releasedQuantity");
                        Allotment = commDetails.getString("allotment");
                        Code = commDetails.getString("commCode");
                        Name = commDetails.getString("commName");
                        SId = commDetails.getString("schemeId");
                        SName = commDetails.getString("schemeName");
                        Truck_rel_Quantity.add(rel_Quantity);
                        Truck_Allotment.add(Allotment);
                        Truck_Code.add(Code);
                        Truck_Name.add(Name);
                        Truck_SId.add(SId);
                        Truck_SName.add(SName);
                        System.out.println("+++++++++++++++++++++++++JSON 1");
                    }

                }
            }
            databaseHelper.insert_RECIVED_TRUCK(Truck_fps, Truck_allotmonth, Truck_allotyear, Truck_truckChit, Truck_challanId,
                    Truck_allocationOrderNo, Truck_truckNo, CommLength);
            databaseHelper.insert_RECIVED_COMM(Truck_rel_Quantity, Truck_Allotment, Truck_Code, Truck_Name, Truck_SId,
                    Truck_SName);
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    try {
                        callback();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    private void callback() throws SQLException {
        System.out.println("@@ In callback()");

        if (onResultListener != null) {
            onResultListener.onCompleted(code, msg);
        }
    }
    public interface OnResultListener {
        void onCompleted(String code, String msg) throws SQLException;
    }
}
