package com.visiontek.Mantra.Models;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.visiontek.Mantra.Utils.DatabaseHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

public class OfflineAPIs {

    public void parsegetFpsOfflineData(String strJson, Context context) throws Exception {

        DatabaseHelper helper = new DatabaseHelper(context);


//String strJson="{ \"Employee\" :[{\"id\":\"101\",\"name\":\"Sonoo Jaiswal\",\"salary\":\"50000\"},{\"id\":\"102\",\"name\":\"Vimal Jaiswal\",\"salary\":\"60000\"}] }";


        /*JSONObject emp=(new JSONObject(strJson)).getJSONObject("keyRegister");
        String empname=emp.getString("name");
*/
        JSONObject jsonRootObject = new JSONObject(strJson);
        JSONArray jsonArray = jsonRootObject.optJSONArray("keyRegister");
        for(int i=0; i < jsonArray.length(); i++){
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


            String query = "insert into KeyRegister values('"+rcId+"','"+commNameEn+"','"+commNameLl+"','"+commCode+"','"+totalEntitlement+"','"+balanceEntitlement+"','"+schemeId+"','"+schemeName+"','"+commPrice+"','"+measurmentUnit+"','"+memberNameLl+"','"+memberNameEn+"','"+month+"','"+year+"','"+allocationType+"','"+allotedMonth+"','"+allotedYear+"');";
            System.out.println("Query: " +query);
            SQLiteDatabase db = helper.getWritableDatabase();
            db.execSQL(query);
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
            SQLiteDatabase db = helper.getWritableDatabase();
            db.execSQL(query);
        }

        JSONArray jsonArray2 = jsonRootObject.optJSONArray("schemeMaster");
        for(int i=0; i < jsonArray2.length(); i++) {
            JSONObject jsonObject = jsonArray2.getJSONObject(i);
            String schemeId = jsonObject.optString("schemeId").toString();
            String schemeName = jsonObject.optString("schemeName").toString();

            String query = "insert into schemeMaster values('" +schemeId +"','"+schemeName +"');";
            System.out.println("Query: " +query);
            SQLiteDatabase db = helper.getWritableDatabase();
            db.execSQL(query);
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
            SQLiteDatabase db = helper.getWritableDatabase();
            db.execSQL(query);
        }
    }

    public void parsegetCbUpdate(String strJson, Context context) throws Exception {

        DatabaseHelper helper = new DatabaseHelper(context);


//String strJson="{ \"Employee\" :[{\"id\":\"101\",\"name\":\"Sonoo Jaiswal\",\"salary\":\"50000\"},{\"id\":\"102\",\"name\":\"Vimal Jaiswal\",\"salary\":\"60000\"}] }";


        /*JSONObject emp=(new JSONObject(strJson)).getJSONObject("keyRegister");
        String empname=emp.getString("name");*/

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

    public String getOfflinePassword(Context context) throws SQLException {
        DatabaseHelper helper = new DatabaseHelper(context);
        String query = "select OffPassword from PartialOnlineData";
        SQLiteDatabase db = helper.getWritableDatabase();
        ResultSet res = (ResultSet) db.rawQuery(query,null);
        String password = "";
        while(res.next())
        {
            password = res.getString("OffPassword");
        }
        return password;
    }

}

