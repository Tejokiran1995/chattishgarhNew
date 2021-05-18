package com.visiontek.Mantra.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.visiontek.Mantra.Models.Ekyc;
import com.visiontek.Mantra.Models.cardDetails;
import com.visiontek.Mantra.Models.fpsCommonInfo;
import com.visiontek.Mantra.Models.fpsPofflineToken;
import com.visiontek.Mantra.Models.fpsURLInfo;
import com.visiontek.Mantra.Models.stateBean;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;


public class XML_Parsing extends AsyncTask<String, Void, Void> {
    private final String xmlformat;
    private final int type;
    @SuppressLint("StaticFieldLeak")
    private final Context context;
    private DatabaseHelper databaseHelper;
    private String code;
    private HttpURLConnection urlConnection;
    private OnResultListener onResultListener;
    private String msg;
    private String ref;
    private String flow;

    public XML_Parsing(Context context, String xmlformat, int type) {
        this.context = context;
        this.xmlformat = xmlformat;
        this.type = type;
    }

    public void setOnResultListener(OnResultListener onResultListener) {
        this.onResultListener = onResultListener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(String... param) {
        String url = "http://epos.nic.in/ePosServiceCTG/jdCommoneposServiceRes?wsdl";
        runRequest(xmlformat, url, type);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (onResultListener != null) {
            onResultListener.onCompleted(code, msg, ref, flow);
        }
    }

    private void runRequest(String hit, String url, int type) {
        databaseHelper = new DatabaseHelper(context);
        try {
            System.out.println("INPUT=" + hit);
            URL Url = new URL(url);
            urlConnection = (HttpURLConnection) Url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "text/xml");
//            urlConnection.setRequestProperty("SOAPAction", "http://mdasol.com/MeterReading/CreateInvoice");
//            urlConnection.setRequestProperty("Authorization", "Basic " + org.kobjects.base64.Base64.encode(("Web" + ":" + "5bs2YO!)A8RMEhcS@ADj").getBytes()));
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            OutputStream outputStream = urlConnection.getOutputStream();
            outputStream.write(hit.getBytes());
            outputStream.flush();
            outputStream.close();
            urlConnection.connect();
            Log.e(getClass().getName(), String.valueOf(urlConnection.getResponseCode()));
            String result = null;
            if (urlConnection.getResponseCode() == 200) {
                BufferedInputStream bis = new BufferedInputStream(urlConnection.getInputStream());
                ByteArrayOutputStream buf = new ByteArrayOutputStream();
                int result2 = bis.read();
                while (result2 != -1) {
                    buf.write((byte) result2);
                    result2 = bis.read();
                }
                result = buf.toString();
            }
            if (result != null && result.length() > 0) {
                System.out.println("OUTPU = " + result);
                if (type == 1) {
                    Util.generateNoteOnSD(context, "DDetailsRes.txt", result);
                    parseXml_dealer(result);
                    parseXml_dealerURLdetails(result);
                    parseXml_dealerstateBean(result);
                    parseXml_cancelrequest(result);
                } else if (type == 2) {
                    Util.generateNoteOnSD(context, "DealerAuthRes.txt", result);
                    parseXml_dealer_login(result);
                } else if (type == 3) {
                    Util.generateNoteOnSD(context, "MDetailsRes.txt", result);
                    parseXml_member(result);
                    parseXml_ration(result);
                    parseXml_rationCardDetails(result);
                } else if (type == 4) {
                    Util.generateNoteOnSD(context, "MemberAuthRes.txt", result);
                    parseXml_member_login(result);
                } else if (type == 5) {
                    Util.generateNoteOnSD(context, "SaleDetailsRes.txt", result);
                    parseXml_sale_details(result);
                } else if (type == 6) {
                    Util.generateNoteOnSD(context, "StockDetailsRes.txt", result);
                    parseXml_stock_details(result);
                } else if (type == 7) {
                    Util.generateNoteOnSD(context, "MenuRes.txt", result);
                    parseXml_menu_details(result);
                    parseXml_menu_details1(result);
                    parseXml_partial_onlinedata(result);
                } else if (type == 8) {
                    Util.generateNoteOnSD(context, "MembereKycRes.txt", result);
                    parseXml_eKyc(result);
                } else if (type == 9) {
                    Util.generateNoteOnSD(context, "LastReciptRes.txt", result);
                    parseXml_LastRecipt(result);
                } else if (type == 10) {
                    Util.generateNoteOnSD(context, "ManualRes.txt", result);
                    parseXml_Manual(result);
                } else if (type == 11) {
                    Util.generateNoteOnSD(context, "RationRes.txt", result);
                    parseXml_printer(result);
                } else if (type == 15) {
                    Util.generateNoteOnSD(context, "RGDealerAuthRes.txt", result);
                    parseXml_RCDealer(result);
                } else {
                    Util.generateNoteOnSD(context, "ERRORR.txt", result);
                }
            } else {
                code = "error";
                msg = "PARSING Error";
            }
        } catch (Exception e) {
            e.printStackTrace();
            code = "1";
            msg = String.valueOf(e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    private void parseXml_RCDealer(String result) {

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(result));
            int eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {

                    if (xpp.getName().equals("respCode")) {
                        eventType = xpp.next();
                        if (eventType == XmlPullParser.TEXT) {
                            code = (xpp.getText());
                            System.out.println("Resp CODE *****************************" + xpp.getText());
                        }
                    }

                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("respMessage")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                msg = (xpp.getText());
                                System.out.println("Resp MSG *****************************" + xpp.getText());
                            }
                        }
                    }

                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("transaction_flow")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                flow = (xpp.getText());
                                System.out.println("transaction_flow *****************************" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("auth_transaction_code")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                ref = (xpp.getText());
                                System.out.println("auth_transaction_code *****************************" + xpp.getText());
                            }
                        }
                    }
                }
                eventType = xpp.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            code = "1";
            msg = String.valueOf(e);
        } catch (IOException e) {
            e.printStackTrace();
            code = "1";
            msg = String.valueOf(e);
        }
    }

    private void parseXml_Manual(String result) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(result));
            int eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {

                    if (xpp.getName().equals("respCode")) {
                        eventType = xpp.next();
                        if (eventType == XmlPullParser.TEXT) {
                            code = (xpp.getText());
                            System.out.println("Resp CODE *****************************" + xpp.getText());
                        }
                    }

                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("respMessage")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                msg = (xpp.getText());
                                System.out.println("Resp MSG *****************************" + xpp.getText());
                            }
                        }
                    }

                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("transaction_flow")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                flow = (xpp.getText());
                                System.out.println("transaction_flow *****************************" + xpp.getText());
                            }
                        }
                    }
                }
                eventType = xpp.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            code = "1";
            msg = String.valueOf(e);
        } catch (IOException e) {
            e.printStackTrace();
            code = "1";
            msg = String.valueOf(e);
        }
    }

    private void parseXml_LastRecipt(String result) {
        ArrayList<String> bal_qty = new ArrayList<>();
        ArrayList<String> carry_over = new ArrayList<>();
        ArrayList<String> commIndividualAmount = new ArrayList<>();
        ArrayList<String> comm_name = new ArrayList<>();
        ArrayList<String> comm_name_ll = new ArrayList<>();
        ArrayList<String> member_name = new ArrayList<>();
        ArrayList<String> member_name_ll = new ArrayList<>();
        ArrayList<String> reciept_id = new ArrayList<>();
        ArrayList<String> retail_price = new ArrayList<>();
        ArrayList<String> scheme_desc_en = new ArrayList<>();
        ArrayList<String> scheme_desc_ll = new ArrayList<>();
        ArrayList<String> tot_amount = new ArrayList<>();
        ArrayList<String> total_quantity = new ArrayList<>();
        ArrayList<String> transaction_time = new ArrayList<>();
        ArrayList<String> uid_refer_no = new ArrayList<>();

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(result));
            int eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("availedFps")) {
                        eventType = xpp.next();
                        if (eventType == XmlPullParser.TEXT) {

                            flow = (xpp.getText());
                            System.out.println("availedFps 1 =================" + xpp.getText());
                        }
                    }

                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("bal_qty")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                bal_qty.add(xpp.getText());
                                System.out.println("bal_qty 2 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("carry_over")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                carry_over.add(xpp.getText());
                                System.out.println("carry_over 3 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("commIndividualAmount")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                commIndividualAmount.add(xpp.getText());
                                System.out.println("commIndividualAmount 4 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("comm_name")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                comm_name.add(xpp.getText());
                                System.out.println("comm_name 5 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("comm_name_ll")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                comm_name_ll.add(xpp.getText());
                                System.out.println("comm_name_ll 6 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("member_name")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                member_name.add(xpp.getText());
                                System.out.println("member_name 7 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("member_name_ll")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                member_name_ll.add(xpp.getText());
                                System.out.println("member_name_ll 8 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("rcId")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                ref = (xpp.getText());
                                System.out.println("rcId 9 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("reciept_id")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                reciept_id.add(xpp.getText());
                                System.out.println("reciept_id 10 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("retail_price")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                retail_price.add(xpp.getText());
                                System.out.println("retail_price 11 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("scheme_desc_en")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                scheme_desc_en.add(xpp.getText());
                                System.out.println("scheme_desc_en 12 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("scheme_desc_ll")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                scheme_desc_ll.add(xpp.getText());
                                System.out.println("scheme_desc_ll 13 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("tot_amount")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                tot_amount.add(xpp.getText());
                                System.out.println("tot_amount 14 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("total_quantity")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                total_quantity.add(xpp.getText());
                                System.out.println("total_quantity 15 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("transaction_time")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                transaction_time.add(xpp.getText());
                                System.out.println("transaction_time 16 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("uid_refer_no")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                uid_refer_no.add(xpp.getText());
                                System.out.println("uid_refer_no 17 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("rcId")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                flow = (xpp.getText());
                                System.out.println("rcId *****************************" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("respCode")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                code = (xpp.getText());
                                System.out.println("Resp CODE*****************************" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("respMessage")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                msg = (xpp.getText());
                                System.out.println("Resp MSG*****************************" + xpp.getText());
                            }
                        }
                    }
                }
                eventType = xpp.next();
            }
            databaseHelper.insert_PD(
                    bal_qty,
                    carry_over,
                    commIndividualAmount,
                    comm_name,
                    comm_name_ll,
                    member_name,
                    member_name_ll,
                    reciept_id,
                    retail_price,
                    scheme_desc_en,
                    scheme_desc_ll,
                    tot_amount,
                    total_quantity,
                    transaction_time,
                    uid_refer_no);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            code = "1";
            msg = String.valueOf(e);
        } catch (IOException e) {
            e.printStackTrace();
            code = "1";
            msg = String.valueOf(e);
        }
    }

    private void parseXml_eKyc(String result) {
        Ekyc Ekyc=new Ekyc();
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(result));
            int eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("respCode")) {
                        eventType = xpp.next();
                        if (eventType == XmlPullParser.TEXT) {
                            code = (xpp.getText());
                            System.out.println("Resp CODE *****************************" + xpp.getText());
                        }
                    }

                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("respMessage")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                msg = (xpp.getText());
                                System.out.println("Resp MSG *****************************" + xpp.getText());
                            }
                        }
                    }

                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("transaction_flow")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                flow = (xpp.getText());
                                System.out.println("transaction_flow *****************************" + xpp.getText());
                            }
                        }
                    }

                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("zdistrTxnId")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                Ekyc.setzdistrTxnId (xpp.getText());
                                System.out.println("zdistrTxnId *****************************" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("eKYCDOB")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                Ekyc.seteKYCDOB  (xpp.getText());
                                System.out.println("eKYCDOB *****************************" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("eKYCGeneder")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                Ekyc.seteKYCGeneder  (xpp.getText());
                                System.out.println("eKYCGeneder *****************************" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("eKYCMemberName")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                Ekyc.seteKYCMemberName (xpp.getText());
                                System.out.println("eKYCMemberName *****************************" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("eKYCPindCode")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                Ekyc.seteKYCPindCode (xpp.getText());
                                System.out.println("eKYCPindCode *****************************" + xpp.getText());
                            }
                        }
                    }
                }
                eventType = xpp.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            code = "1";
            msg = String.valueOf(e);
        } catch (IOException e) {
            e.printStackTrace();
            code = "1";
            msg = String.valueOf(e);
        }

    }
    public void parseXml_partial_onlinedata(String response)
    {

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
    }
    private void parseXml_menu_details1(String result) {
        fpsPofflineToken fpsPofflineToken=new fpsPofflineToken();
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(result));
            int eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("allocationMonth")) {
                        eventType = xpp.next();
                        if (eventType == XmlPullParser.TEXT) {

                            fpsPofflineToken.setallocationMonth (xpp.getText());
                            System.out.println("allocationMonth 1 =================" + xpp.getText());
                        }
                    }

                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("allocationYear")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                fpsPofflineToken.setallocationYear (xpp.getText());
                                System.out.println("allocationYear 2 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("fpsToken")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                fpsPofflineToken.setfpsToken  (xpp.getText());
                                System.out.println("fpsToken 3 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("fpsTokenAllowdOrnotStatus")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                fpsPofflineToken.setfpsTokenAllowdOrnotStatus (xpp.getText());
                                System.out.println("fpsTokenAllowdOrnotStatus 4 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("pOfflineDurationTimeInaDay")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                fpsPofflineToken.setpOfflineDurationTimeInaDay (xpp.getText());
                                System.out.println("pOfflineDurationTimeInaDay 5 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("pOfflineStoppedDate")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                fpsPofflineToken.setpOfflineStoppedDate  (xpp.getText());
                                System.out.println("pOfflineStoppedDate 6 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("pOfflineTransactionTime")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                fpsPofflineToken.setpOfflineTransactionTime  (xpp.getText());
                                System.out.println("pOfflineTransactionTime 7 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("skey")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                fpsPofflineToken.setskey (xpp.getText());
                                System.out.println("skey 8 =================" + xpp.getText());
                            }
                        }
                    }
                }
                eventType = xpp.next();
            }

        } catch (XmlPullParserException e) {
            e.printStackTrace();
            code = "1";
            msg = String.valueOf(e);
        } catch (IOException e) {
            e.printStackTrace();
            code = "1";
            msg = String.valueOf(e);
        }
    }

    private void parseXml_menu_details(String result) {

        ArrayList<String> mainMenu = new ArrayList<>();
        ArrayList<String> menuName = new ArrayList<>();
        ArrayList<String> slno = new ArrayList<>();
        ArrayList<String> status = new ArrayList<>();

        databaseHelper.deletesaletable();
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(result));
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("mainMenu")) {
                        eventType = xpp.next();
                        if (eventType == XmlPullParser.TEXT) {
                            mainMenu.add(xpp.getText());
                            System.out.println("mainMenu 1 =================" + xpp.getText());
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("menuName")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                menuName.add(xpp.getText());
                                System.out.println("menuName 2 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("slno")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                slno.add(xpp.getText());
                                System.out.println("slno 3 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("status")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                status.add(xpp.getText());
                                System.out.println("status 4 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("respCode")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                code = (xpp.getText());
                                System.out.println("Resp CODE*****************************" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("respMessage")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                msg = (xpp.getText());
                                System.out.println("Resp MSG*****************************" + xpp.getText());
                            }
                        }
                    }
                }
                eventType = xpp.next();
            }
            databaseHelper.insert_MENU(mainMenu, menuName, slno, status);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            code = "1";
            msg = String.valueOf(e);
        } catch (IOException e) {
            e.printStackTrace();
            code = "1";
            msg = String.valueOf(e);
        }
    }

    private void parseXml_dealer(String xmlString) {
        ArrayList<String> authType = new ArrayList<>();
        ArrayList<String> dealerFusion = new ArrayList<>();
        ArrayList<String> dealer_type = new ArrayList<>();
        ArrayList<String> delName = new ArrayList<>();
        ArrayList<String> delNamell = new ArrayList<>();
        ArrayList<String> delUid = new ArrayList<>();
        ArrayList<String> wadhStatus = new ArrayList<>();

        fpsCommonInfo fpsCommonInfo=new fpsCommonInfo();
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(xmlString));
            int eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("authType")) {
                        eventType = xpp.next();
                        if (eventType == XmlPullParser.TEXT) {
                            authType.add(xpp.getText());
                            System.out.println("authType =================" + xpp.getText());
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("dealerFusion")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                dealerFusion.add(xpp.getText());
                                System.out.println("dealerFusion =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("dealer_type")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                dealer_type.add(xpp.getText());
                                System.out.println("dealer_type =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("delName")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                delName.add(xpp.getText());
                                System.out.println("delName =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("delNamell")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                delNamell.add(xpp.getText());
                                System.out.println("delNamell =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("delUid")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                delUid.add(xpp.getText());
                                System.out.println("delUid =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("wadhStatus")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                wadhStatus.add(xpp.getText());
                                System.out.println("wadhStatus =================" + xpp.getText());
                            }
                        }
                    }
//------------------------------COMMON INFO

                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("dealer_password")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                fpsCommonInfo.setdealer_password (xpp.getText());
                                System.out.println("Dealer Password 1 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("distCode")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                fpsCommonInfo.setdistCode (xpp.getText());
                                System.out.println("distCode 2 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("flasMessage1")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                fpsCommonInfo.setflasMessage1 (xpp.getText());
                                System.out.println("FlasMessage1 3 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("flasMessage2")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                fpsCommonInfo.setflasMessage2  (xpp.getText());
                                System.out.println("FlasMessage2 4 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("fpsId")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                fpsCommonInfo.setfpsId  (xpp.getText());
                                System.out.println("fpsId 5 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("fpsSessionId")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                fpsCommonInfo.setfpsSessionId  (xpp.getText());
                                System.out.println("Session ID 6 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("keyregisterDataDeleteStatus")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                fpsCommonInfo.setkeyregisterDataDeleteStatus  (xpp.getText());
                                System.out.println("keyregisterDataDeleteStatus 7 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("keyregisterDownloadStatus")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                fpsCommonInfo.setkeyregisterDownloadStatus  (xpp.getText());
                                System.out.println("keyregisterDownloadStatus 8 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("latitude")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                fpsCommonInfo.setlatitude  (xpp.getText());
                                System.out.println("latitude 9 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("loginRequestTime")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                fpsCommonInfo.setloginRequestTime  (xpp.getText());
                                System.out.println("loginRequestTime 10 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("logoutTime")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                fpsCommonInfo.setlogoutTime  (xpp.getText());
                                System.out.println("logoutTime 11 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("longtude")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                fpsCommonInfo.setlongtude  (xpp.getText());
                                System.out.println("longtude 12 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("minQty")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                fpsCommonInfo.setminQty  (xpp.getText());
                                System.out.println("minQty  13 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("partialOnlineOfflineStatus")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                fpsCommonInfo.setpartialOnlineOfflineStatus  (xpp.getText());
                                System.out.println("partialOnlineOfflineStatus 14 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("responseTimedOutTimeInSec")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                fpsCommonInfo.setresponseTimedOutTimeInSec  (xpp.getText());
                                System.out.println("responseTimedOutTimeInSec 15 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("routeOffEnable")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                fpsCommonInfo.setrouteOffEnable  (xpp.getText());
                                System.out.println("routeOffEnable 16 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("versionUpdateRequired")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                fpsCommonInfo.setversionUpdateRequired  (xpp.getText());
                                System.out.println("versionUpdateRequired 17 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("wadhValue")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                fpsCommonInfo.setwadhValue  (xpp.getText());
                                System.out.println("wadhValue 18 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("weighAccuracyValueInGms")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                fpsCommonInfo.setweighAccuracyValueInGms  (xpp.getText());
                                System.out.println("weighAccuracyValueInGms 19 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("weighingStatus")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                fpsCommonInfo.setweighingStatus  (xpp.getText());
                                System.out.println("weighingStatus 20 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("eKYCPrompt")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                fpsCommonInfo.seteKYCPrompt  (xpp.getText());
                                System.out.println("eKYCPrompt 21 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("firauthFlag")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                fpsCommonInfo.setfirauthFlag  (xpp.getText());
                                System.out.println("firauthFlag 22 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("firauthCount")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                fpsCommonInfo.setfirauthCount  (xpp.getText());
                                System.out.println("firauthCount 23 =================" + xpp.getText());
                            }
                        }
                    }
                    //----------------------------------------------------------------------------------------------
                    //RESULT
                    //-------------------------------------------------------------------------------------------------
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("respCode")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                code = (xpp.getText());
                                System.out.println("Resp CODE*****************************" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("respMessage")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                msg = (xpp.getText());
                                System.out.println("Resp MSG*****************************" + xpp.getText());
                            }
                        }
                    }
                }
                eventType = xpp.next();
            }
            databaseHelper.insert_DD(authType, dealerFusion, dealer_type, delName, delNamell, delUid, wadhStatus);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            code = "1";
            msg = String.valueOf(e);
        } catch (IOException e) {
            e.printStackTrace();
            code = "1";
            msg = String.valueOf(e);
        }
    }

    private void parseXml_dealerfpsCommInfo(String xmlString) {
        String dealer_password = null,
                distCode = null,
                flasMessage1 = null,
                flasMessage2 = null,
                fpsId = null,
                fpsSessionId = null,
                keyregisterDataDeleteStatus = null,
                keyregisterDownloadStatus = null,
                latitude = null,
                loginRequestTime = null,
                logoutTime = null,
                longtude = null,
                minQty = null,
                partialOnlineOfflineStatus = null,
                responseTimedOutTimeInSec = null,
                routeOffEnable = null,
                versionUpdateRequired = null,
                wadhValue = null,
                weighAccuracyValueInGms = null,
                weighingStatus = null,
                eKYCPrompt = null,
                firauthFlag = null,
                firauthCount = null;

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(xmlString));
            int eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("dealer_password")) {
                        eventType = xpp.next();
                        if (eventType == XmlPullParser.TEXT) {

                            dealer_password = (xpp.getText());
                            System.out.println("Dealer Password 1 =================" + xpp.getText());
                        }
                    }

                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("distCode")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                distCode = (xpp.getText());
                                System.out.println("distCode 2 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("flasMessage1")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                flasMessage1 = (xpp.getText());
                                System.out.println("FlasMessage1 3 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("flasMessage2")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                flasMessage2 = (xpp.getText());
                                System.out.println("FlasMessage2 4 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("fpsId")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                fpsId = (xpp.getText());
                                System.out.println("fpsId 5 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("fpsSessionId")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                fpsSessionId = (xpp.getText());
                                System.out.println("Session ID 6 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("keyregisterDataDeleteStatus")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                keyregisterDataDeleteStatus = (xpp.getText());
                                System.out.println("keyregisterDataDeleteStatus 7 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("keyregisterDownloadStatus")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                keyregisterDownloadStatus = (xpp.getText());
                                System.out.println("keyregisterDownloadStatus 8 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("latitude")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                latitude = (xpp.getText());
                                System.out.println("latitude 9 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("loginRequestTime")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                loginRequestTime = (xpp.getText());
                                System.out.println("loginRequestTime 10 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("logoutTime")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                logoutTime = (xpp.getText());
                                System.out.println("logoutTime 11 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("longtude")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                longtude = (xpp.getText());
                                System.out.println("longtude 12 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("minQty")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                minQty = (xpp.getText());
                                System.out.println("minQty  13 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("partialOnlineOfflineStatus")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                partialOnlineOfflineStatus = (xpp.getText());
                                System.out.println("partialOnlineOfflineStatus 14 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("responseTimedOutTimeInSec")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                responseTimedOutTimeInSec = (xpp.getText());
                                System.out.println("responseTimedOutTimeInSec 15 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("routeOffEnable")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                routeOffEnable = (xpp.getText());
                                System.out.println("routeOffEnable 16 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("versionUpdateRequired")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                versionUpdateRequired = (xpp.getText());
                                System.out.println("versionUpdateRequired 17 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("wadhValue")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                wadhValue = (xpp.getText());
                                System.out.println("wadhValue 18 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("weighAccuracyValueInGms")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                weighAccuracyValueInGms = (xpp.getText());
                                System.out.println("weighAccuracyValueInGms 19 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("weighingStatus")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                weighingStatus = (xpp.getText());
                                System.out.println("weighingStatus 20 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("eKYCPrompt")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                eKYCPrompt = (xpp.getText());
                                System.out.println("eKYCPrompt 21 =================" + xpp.getText());
                            }
                        }
                    }

                }
                if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("firauthFlag")) {
                        eventType = xpp.next();
                        if (eventType == XmlPullParser.TEXT) {

                            firauthFlag = (xpp.getText());
                            System.out.println("firauthFlag 22 =================" + xpp.getText());
                        }
                    }
                }
                if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("firauthCount")) {
                        eventType = xpp.next();
                        if (eventType == XmlPullParser.TEXT) {

                            firauthCount = (xpp.getText());
                            System.out.println("firauthCount 23 =================" + xpp.getText());
                        }
                    }
                }
                eventType = xpp.next();
            }

        } catch (XmlPullParserException e) {
            e.printStackTrace();
            code = "1";
            msg = String.valueOf(e);
        } catch (IOException e) {
            e.printStackTrace();
            code = "1";
            msg = String.valueOf(e);
        }
    }

    private void parseXml_dealerURLdetails(String xmlString) {
        fpsURLInfo fpsURLInfo=new fpsURLInfo();


        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(xmlString));
            int eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("authURL")) {
                        eventType = xpp.next();
                        if (eventType == XmlPullParser.TEXT) {

                            fpsURLInfo.setauthURL(xpp.getText());
                            System.out.println("authURL 1 =================" + xpp.getText());
                        }
                    }

                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("ceritificatePath")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                fpsURLInfo.setceritificatePath (xpp.getText());
                                System.out.println("ceritificatePath 2 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("commonCommodityFlag")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                fpsURLInfo.setcommonCommodityFlag (xpp.getText());
                                System.out.println("commonCommodityFlag 3 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("exEKYCURL")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                fpsURLInfo.setexEKYCURL (xpp.getText());
                                System.out.println("exEKYCURL 4 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("exclusionURL")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                fpsURLInfo.setexclusionURL (xpp.getText());
                                System.out.println("exclusionURL 5 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("fpsCbDownloadStatus")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                fpsURLInfo.setfpsCbDownloadStatus (xpp.getText());
                                System.out.println("fpsCbDownloadStatus 6 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("fusionAttempts")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                fpsURLInfo.setfusionAttempts (xpp.getText());
                                System.out.println("fusionAttempts 7 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("helplineNumber")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                fpsURLInfo.sethelplineNumber (xpp.getText());
                                System.out.println("helplineNumber 8 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("impdsURL")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                fpsURLInfo.setimpdsURL (xpp.getText());
                                System.out.println("impdsURL 9 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("irisStatus")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                fpsURLInfo.setirisStatus (xpp.getText());
                                System.out.println("irisStatus 10 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("messageEng")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                fpsURLInfo.setmessageEng (xpp.getText());
                                System.out.println("messageEng 11 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("messageLl")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                fpsURLInfo.setmessageLl (xpp.getText());
                                System.out.println("messageLl 12 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("paperRequiredFlag")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                fpsURLInfo.setpaperRequiredFlag (xpp.getText());
                                System.out.println("paperRequiredFlag  13 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("pdsClTranEng")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                fpsURLInfo.setpdsClTranEng (xpp.getText());
                                System.out.println("pdsClTranEng 14 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("pdsClTranLl")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                fpsURLInfo.setpdsClTranLl (xpp.getText());
                                System.out.println("pdsClTranLl 15 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("pdsTranEng")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                fpsURLInfo.setpdsTranEng (xpp.getText());
                                System.out.println("pdsTranEng 16 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("pdsTransLl")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                fpsURLInfo.setpdsTransLl (xpp.getText());
                                System.out.println("pdsTransLl 17 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("pmsMenuNameEn")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                fpsURLInfo.setpmsMenuNameEn  (xpp.getText());
                                System.out.println("pmsMenuNameEn 18 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("pmsMenuNameLL")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                fpsURLInfo.setpmsMenuNameLL (xpp.getText());
                                System.out.println("pmsMenuNameLL 19 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("pmsURL")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                fpsURLInfo.setpmsURL (xpp.getText());
                                System.out.println("pmsURL 20 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("pmsWadh")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                fpsURLInfo.setpmsWadh (xpp.getText());
                                System.out.println("pmsWadh 21 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("token")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                fpsURLInfo.settoken (xpp.getText());
                                System.out.println("token 22 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("utilURL")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                fpsURLInfo.setutilURL (xpp.getText());
                                System.out.println("utilURL 23 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("wsdlOffline")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                fpsURLInfo.setwsdlOffline (xpp.getText());
                                System.out.println("wsdlOffline 24 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("wsdlURLAuth")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                fpsURLInfo.setwsdlURLAuth (xpp.getText());
                                System.out.println("wsdlURLAuth 25 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("wsdlURLPDS")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                fpsURLInfo.setwsdlURLPDS (xpp.getText());
                                System.out.println("wsdlURLPDS 26 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("wsdlURLReceive")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                fpsURLInfo.setwsdlURLReceive (xpp.getText());
                                System.out.println("wsdlURLReceive 27 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("dTransURL")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                fpsURLInfo.setdTransURL (xpp.getText());
                                System.out.println("dTransURL 28 =================" + xpp.getText());
                            }
                        }
                    }

                }
                eventType = xpp.next();
            }

        } catch (XmlPullParserException e) {
            e.printStackTrace();
            code = "1";
            msg = String.valueOf(e);
        } catch (IOException e) {
            e.printStackTrace();
            code = "1";
            msg = String.valueOf(e);
        }
    }

    private void parseXml_dealerstateBean(String xmlString) {

        stateBean stateBean=new stateBean();

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(xmlString));
            int eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("consentHeader")) {
                        eventType = xpp.next();
                        if (eventType == XmlPullParser.TEXT) {

                            stateBean.setconsentHeader (xpp.getText());
                            System.out.println("consentHeader 1 =================" + xpp.getText());
                        }
                    }

                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("stateCode")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                stateBean.setstateCode (xpp.getText());
                                System.out.println("stateCode 2 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("stateNameEn")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                stateBean.setstateNameEn (xpp.getText());
                                System.out.println("stateNameEn 3 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("stateNameLl")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                stateBean.setstateNameLl  (xpp.getText());
                                System.out.println("stateNameLl 4 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("stateProfile")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                stateBean.setstateProfile (xpp.getText());
                                System.out.println("stateProfile 5 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("stateReceiptHeaderEn")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                stateBean.setstateReceiptHeaderEn  (xpp.getText());
                                System.out.println("stateReceiptHeaderEn 6 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("stateReceiptHeaderLl")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                stateBean.setstateReceiptHeaderLl  (xpp.getText());
                                System.out.println("stateReceiptHeaderLl 7 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("statefpsId")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                stateBean.setstatefpsId  (xpp.getText());
                                System.out.println("statefpsId 8 =================" + xpp.getText());
                            }
                        }
                    }


                }
                eventType = xpp.next();
            }

        } catch (XmlPullParserException e) {
            e.printStackTrace();
            code = "1";
            msg = String.valueOf(e);
        } catch (IOException e) {
            e.printStackTrace();
            code = "1";
            msg = String.valueOf(e);
        }
    }

    private void parseXml_cancelrequest(String xmlString) {
        ArrayList<String> reasonId = new ArrayList<>();
        ArrayList<String> reasonValue = new ArrayList<>();


        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(xmlString));
            int eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("reasonId")) {
                        eventType = xpp.next();
                        if (eventType == XmlPullParser.TEXT) {

                            reasonId.add(xpp.getText());
                            System.out.println("reasonId 0 =================" + xpp.getText());
                        }
                    }

                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("reasonValue")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {

                                reasonValue.add(xpp.getText());
                                System.out.println("reasonValue 2 =================" + xpp.getText());
                            }
                        }
                    }

                }
                eventType = xpp.next();
            }

            databaseHelper.insert_Cancel_Request(reasonId, reasonValue);

        } catch (XmlPullParserException e) {
            e.printStackTrace();
            code = "1";
            msg = String.valueOf(e);
        } catch (IOException e) {
            e.printStackTrace();
            code = "1";
            msg = String.valueOf(e);
        }
    }

    private void parseXml_dealer_login(String xmlString) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(xmlString));
            int eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {

                    if (xpp.getName().equals("respCode")) {
                        eventType = xpp.next();
                        if (eventType == XmlPullParser.TEXT) {
                            code = (xpp.getText());
                            System.out.println("Resp CODE *****************************" + xpp.getText());
                        }
                    }

                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("respMessage")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                msg = (xpp.getText());
                                System.out.println("Resp MSG *****************************" + xpp.getText());
                            }
                        }
                    }

                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("transaction_flow")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                flow = (xpp.getText());
                                System.out.println("transaction_flow *****************************" + xpp.getText());
                            }
                        }
                    }
                }
                eventType = xpp.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            code = "1";
            msg = String.valueOf(e);
        } catch (IOException e) {
            e.printStackTrace();
            code = "1";
            msg = String.valueOf(e);
        }
    }

    private void parseXml_member(String xmlString) {

        ArrayList<String> memberName = new ArrayList<>();
        ArrayList<String> memberNamell = new ArrayList<>();
        ArrayList<String> member_fusion = new ArrayList<>();
        ArrayList<String> uid = new ArrayList<>();
        ArrayList<String> w_uid_status = new ArrayList<>();
        ArrayList<String> xfinger = new ArrayList<>();
        ArrayList<String> yiris = new ArrayList<>();
        ArrayList<String> zmanual = new ArrayList<>();
        ArrayList<String> zmemberId = new ArrayList<>();
        ArrayList<String> zotp = new ArrayList<>();
        ArrayList<String> zwgenWadhAuth = new ArrayList<>();

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(xmlString));
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("memberName")) {
                        eventType = xpp.next();
                        if (eventType == XmlPullParser.TEXT) {
                            memberName.add(xpp.getText());
                            System.out.println("memberName 1 =================" + xpp.getText());
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("memberNamell")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                memberNamell.add(xpp.getText());
                                System.out.println("memberNamell 2 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("member_fusion")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                member_fusion.add(xpp.getText());
                                System.out.println("member_fusion 3 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("uid")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                uid.add(xpp.getText());
                                System.out.println("uid 4 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("w_uid_status")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                w_uid_status.add(xpp.getText());
                                System.out.println("w_uid_status 5 =================" + xpp.getText());
                            }
                        }
                    }

                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("xfinger")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                xfinger.add(xpp.getText());
                                System.out.println("xfinger 6 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("yiris")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                yiris.add(xpp.getText());
                                System.out.println("yiris 7 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("zmanual")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                zmanual.add(xpp.getText());
                                System.out.println("zmanual 8 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("zmemberId")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                zmemberId.add(xpp.getText());
                                System.out.println("zmemberId 9 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("zotp")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                zotp.add(xpp.getText());
                                System.out.println("zotp 10 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("zwgenWadhAuth")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                zwgenWadhAuth.add(xpp.getText());
                                System.out.println("zwgenWadhAuth 11 =================" + xpp.getText());
                            }
                        }
                    }
                    //-------------------------------------------------------------------------------------------
                    //---------------------------------------------------------------------------------------------

                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("respCode")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                code = (xpp.getText());
                                System.out.println("Resp CODE*****************************" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("respMessage")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                msg = (xpp.getText());
                                System.out.println("Resp MSG*****************************" + xpp.getText());
                            }
                        }
                    }
                }
                eventType = xpp.next();
            }

            databaseHelper.insert_MD(memberName,
                    memberNamell,
                    member_fusion,
                    uid,
                    w_uid_status,
                    xfinger,
                    yiris,
                    zmanual,
                    zmemberId,
                    zotp,
                    zwgenWadhAuth);

        } catch (XmlPullParserException e) {
            e.printStackTrace();
            code = "1";
            msg = String.valueOf(e);
        } catch (IOException e) {
            e.printStackTrace();
            code = "1";
            msg = String.valueOf(e);
        }
    }

    private void parseXml_member_login(String xmlString) {

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(xmlString));
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {

                    if (xpp.getName().equals("respCode")) {
                        eventType = xpp.next();
                        if (eventType == XmlPullParser.TEXT) {
                            code = (xpp.getText());
                            System.out.println("Resp CODE*****************************" + xpp.getText());
                        }
                    }

                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("respMessage")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                msg = (xpp.getText());
                                System.out.println("Resp MSG*****************************" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("auth_transaction_code")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                ref = xpp.getText();
                                System.out.println("Ref =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("transaction_flow")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                flow = (xpp.getText());
                                System.out.println("transaction_flow*****************************" + xpp.getText());
                            }
                        }
                    }
                }
                eventType = xpp.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            code = "1";
            msg = String.valueOf(e);
        } catch (IOException e) {
            msg = String.valueOf(e);
            code = "1";
            msg = String.valueOf(e);
        }
    }

    private void parseXml_ration(String xmlString) {
        ArrayList<String> allocationType = new ArrayList<>();
        ArrayList<String> allotedMonth = new ArrayList<>();
        ArrayList<String> allotedYear = new ArrayList<>();
        ArrayList<String> availedQty = new ArrayList<>();
        ArrayList<String> balQty = new ArrayList<>();
        ArrayList<String> closingBal = new ArrayList<>();
        ArrayList<String> commName = new ArrayList<>();
        ArrayList<String> commNamell = new ArrayList<>();
        ArrayList<String> commcode = new ArrayList<>();
        ArrayList<String> measureUnit = new ArrayList<>();
        ArrayList<String> minQty = new ArrayList<>();
        ArrayList<String> price = new ArrayList<>();
        ArrayList<String> requiredQty = new ArrayList<>();
        ArrayList<String> totQty = new ArrayList<>();
        ArrayList<String> weighing = new ArrayList<>();
        try {

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(xmlString));
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {

                    if (xpp.getName().equals("allocationType")) {
                        eventType = xpp.next();
                        if (eventType == XmlPullParser.TEXT) {
                            allocationType.add(xpp.getText());
                            System.out.println("allocationType =================" + xpp.getText());
                        }
                    }

                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("allotedMonth")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                allotedMonth.add(xpp.getText());
                                System.out.println("allotedMonth =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("allotedYear")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                allotedYear.add(xpp.getText());
                                System.out.println("allotedYear =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("availedQty")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                availedQty.add(xpp.getText());
                                System.out.println("availedQty =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("balQty")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                balQty.add(xpp.getText());
                                System.out.println("balQty =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("closingBal")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                closingBal.add(xpp.getText());
                                System.out.println("closingBal =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("commName")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                commName.add(xpp.getText());
                                System.out.println("ComName =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("commNamell")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                commNamell.add(xpp.getText());
                                System.out.println("commNamell =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("commcode")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                commcode.add(xpp.getText());
                                System.out.println("ComCode =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("measureUnit")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                measureUnit.add(xpp.getText());
                                System.out.println("MeasureUnit =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("minQty")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                minQty.add(xpp.getText());
                                System.out.println("minQty =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("price")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                price.add(xpp.getText());
                                System.out.println("price =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("requiredQty")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                requiredQty.add(xpp.getText());
                                System.out.println("requiredQty =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("totQty")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                totQty.add(xpp.getText());
                                System.out.println("totQty =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("weighing")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                weighing.add(xpp.getText());
                                System.out.println("weighing =================" + xpp.getText());
                            }
                        }
                    }
                }
                eventType = xpp.next();
            }
            databaseHelper.insert_RD(allocationType, allotedMonth, allotedYear, availedQty, balQty, closingBal, commName, commNamell, commcode, measureUnit, minQty, price, requiredQty, totQty, weighing);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            code = "1";
            msg = String.valueOf(e);
        } catch (IOException e) {
            e.printStackTrace();
            code = "1";
            msg = String.valueOf(e);
        }
    }

    private void parseXml_rationCardDetails(String xmlString) {
    cardDetails cardDetails=new cardDetails();
        try {

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(xmlString));
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {

                    if (xpp.getName().equals("address")) {
                        eventType = xpp.next();
                        if (eventType == XmlPullParser.TEXT) {
                            cardDetails.setaddress (xpp.getText());
                            System.out.println("address 1 =================" + xpp.getText());
                        }
                    }

                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("cardHolderName")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                cardDetails.setcardHolderName (xpp.getText());
                                System.out.println("cardHolderName 2 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("cardType")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                cardDetails.setcardType  (xpp.getText());
                                System.out.println("cardType 3 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("familyMemCount")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                cardDetails.setfamilyMemCount (xpp.getText());
                                System.out.println("familyMemCount 4 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("houseHoldCardNo")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                cardDetails.sethouseHoldCardNo (xpp.getText());
                                System.out.println("houseHoldCardNo 5 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("lpgStatus")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                cardDetails.setlpgStatus (xpp.getText());
                                System.out.println("lpgStatus 6 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("lpgtype")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                cardDetails.setlpgtype (xpp.getText());
                                System.out.println("lpgtype 7 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("mobileNoUpdate")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                cardDetails.setmobileNoUpdate (xpp.getText());
                                System.out.println("mobileNoUpdate 8 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("officeName")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                cardDetails.setofficeName (xpp.getText());
                                System.out.println("officeName 9 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("rcId")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                cardDetails.setRcId (xpp.getText());
                                System.out.println("rcId 10 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("schemeId")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                cardDetails.setschemeId (xpp.getText());
                                System.out.println("MeasureUnit 11 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("surveyMessageEN")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                cardDetails.setsurveyMessageEN (xpp.getText());
                                System.out.println("surveyMessageEN 12 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("surveyMessageLL")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                cardDetails.setsurveyMessageLL (xpp.getText());
                                System.out.println("surveyMessageLL 13 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("surveyMinQuantity")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                cardDetails.setsurveyMinQuantity (xpp.getText());
                                System.out.println("surveyMinQuantity 14 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("surveyStaus")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                cardDetails.setsurveyStaus (xpp.getText());
                                System.out.println("surveyStaus 15 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("type_card")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                cardDetails.settype_card (xpp.getText());
                                System.out.println("type_card 16 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("zcommboCommCode")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                cardDetails.setzcommboCommCode (xpp.getText());
                                System.out.println("zcommboCommCode 17 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("zcommboStatus")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                cardDetails.setzcommboStatus (xpp.getText());
                                System.out.println("zcommboStatus 18 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("zheadmobileno")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                cardDetails.setzheadmobileno (xpp.getText());
                                System.out.println("zheadmobileno 19 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("znpnsBalance")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                cardDetails.setznpnsBalance (xpp.getText());
                                System.out.println("znpnsBalance 20 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("zwadh")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                cardDetails.setzwadh (xpp.getText());
                                System.out.println("zwadh 21 =================" + xpp.getText());
                            }
                        }
                    }

                }
                eventType = xpp.next();
            }

        } catch (XmlPullParserException e) {
            e.printStackTrace();
            code = "1";
            msg = String.valueOf(e);
        } catch (IOException e) {
            e.printStackTrace();
            code = "1";
            msg = String.valueOf(e);
        }
    }

    private void parseXml_printer(String xmlString) {

        ArrayList<String> bal_qty = new ArrayList<>();
        ArrayList<String> carry_over = new ArrayList<>();
        ArrayList<String> commIndividualAmount = new ArrayList<>();
        ArrayList<String> comm_name = new ArrayList<>();
        ArrayList<String> comm_name_ll = new ArrayList<>();
        ArrayList<String> member_name = new ArrayList<>();
        ArrayList<String> member_name_ll = new ArrayList<>();
        ArrayList<String> reciept_id = new ArrayList<>();
        ArrayList<String> retail_price = new ArrayList<>();
        ArrayList<String> scheme_desc_en = new ArrayList<>();
        ArrayList<String> scheme_desc_ll = new ArrayList<>();
        ArrayList<String> tot_amount = new ArrayList<>();
        ArrayList<String> total_quantity = new ArrayList<>();
        ArrayList<String> transaction_time = new ArrayList<>();
        ArrayList<String> uid_refer_no = new ArrayList<>();

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(xmlString));
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("bal_qty")) {
                        eventType = xpp.next();
                        if (eventType == XmlPullParser.TEXT) {
                            bal_qty.add(xpp.getText());
                            System.out.println("Bal Qty 0=================" + xpp.getText());
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("carry_over")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                carry_over.add(xpp.getText());
                                System.out.println("carry_over 1=================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("commIndividualAmount")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                commIndividualAmount.add(xpp.getText());
                                System.out.println("commIndividualAmount 2=================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("comm_name")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                comm_name.add(xpp.getText());
                                System.out.println("comm_name  3=================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("comm_name_ll")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                comm_name_ll.add(xpp.getText());
                                System.out.println("comm_name_ll 4=================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("member_name")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                member_name.add(xpp.getText());
                                System.out.println("member_name 5=================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("member_name_ll")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                member_name_ll.add(xpp.getText());
                                System.out.println("retail_price  6=================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("reciept_id")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                reciept_id.add(xpp.getText());
                                System.out.println("reciept_id 7=================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("retail_price")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                retail_price.add(xpp.getText());
                                System.out.println("retail_price 8=================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("scheme_desc_en")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                scheme_desc_en.add(xpp.getText());
                                System.out.println("scheme_desc_en 9 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("scheme_desc_ll")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                scheme_desc_ll.add(xpp.getText());
                                System.out.println("scheme_desc_ll 10 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("tot_amount")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                tot_amount.add(xpp.getText());
                                System.out.println("tot_amount 11 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("total_quantity")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                total_quantity.add(xpp.getText());
                                System.out.println("total_quantity 12 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("transaction_time")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                transaction_time.add(xpp.getText());
                                System.out.println("transaction_time 13 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("uid_refer_no")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                uid_refer_no.add(xpp.getText());
                                System.out.println("uid_refer_no 14 =================" + xpp.getText());
                            }
                        }
                    }
                    //-----------------------------------------------------------------

                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("respCode")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                code = (xpp.getText());
                                System.out.println("Resp CODE*****************************" + xpp.getText());
                            }
                        }
                    }

                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("respMessage")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                msg = (xpp.getText());
                                System.out.println("Resp MSG*****************************" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("receiptId")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                ref = (xpp.getText());
                                System.out.println("receiptId *****************************" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("rcId")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                flow = (xpp.getText());
                                System.out.println("rcId *****************************" + xpp.getText());
                            }
                        }
                    }
                }
                eventType = xpp.next();
            }

            databaseHelper.insert_PD(bal_qty, carry_over, commIndividualAmount, comm_name, comm_name_ll, member_name, member_name_ll,
                    reciept_id, retail_price, scheme_desc_en, scheme_desc_ll, tot_amount, total_quantity, transaction_time, uid_refer_no);

        } catch (XmlPullParserException e) {
            e.printStackTrace();
            code = "1";
            msg = String.valueOf(e);
        } catch (IOException e) {
            e.printStackTrace();
            code = "1";
            msg = String.valueOf(e);
        }

    }

    private void parseXml_sale_details(String xmlString) {
        ArrayList<String> sale = new ArrayList<>();
        databaseHelper.deletesaletable();
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(xmlString));
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("comm_name")) {
                        eventType = xpp.next();
                        if (eventType == XmlPullParser.TEXT) {
                            sale.add(xpp.getText());
                            System.out.println("comm_name 0 =================" + xpp.getText());
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("sale")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                sale.add(xpp.getText());
                                System.out.println("sale 1 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("schemeName")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                sale.add(xpp.getText());
                                System.out.println("schemeName 2 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("total_cards")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                sale.add(xpp.getText());
                                databaseHelper.insert_DSD(sale);
                                sale.clear();
                                System.out.println("total_cards 3 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("respCode")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                code = (xpp.getText());
                                System.out.println("Resp CODE*****************************" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("respMessage")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                msg = (xpp.getText());
                                System.out.println("Resp MSG*****************************" + xpp.getText());
                            }
                        }
                    }
                }
                eventType = xpp.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            code = "1";
            msg = String.valueOf(e);
        } catch (IOException e) {
            e.printStackTrace();
            code = "1";
            msg = String.valueOf(e);
        }
    }

    private void parseXml_stock_details(String xmlString) {
        ArrayList<String> stock = new ArrayList<>();
        databaseHelper.deletestocktable();
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(xmlString));
            int eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("closing_balance")) {
                        eventType = xpp.next();
                        if (eventType == XmlPullParser.TEXT) {
                            stock.add(xpp.getText());
                            System.out.println("closing_balance 0 =================" + xpp.getText());
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("comm_name")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                stock.add(xpp.getText());
                                System.out.println("comm_name 1 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("issued_qty")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                stock.add(xpp.getText());
                                System.out.println("issued_qty 2 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("received_qty")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                stock.add(xpp.getText());
                                System.out.println("received_qty 3 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("scheme_desc_en")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                stock.add(xpp.getText());
                                databaseHelper.insert_SD(stock);
                                stock.clear();
                                System.out.println(stock.size() + "==========================");
                                System.out.println("scheme_desc_en 4 =================" + xpp.getText());
                            }
                        }
                    }
                    /*if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("dealer_password")) {
                        eventType = xpp.next();
                        if (eventType == XmlPullParser.TEXT) {
                            ddetails.add(xpp.getText());
                            System.out.println("Dealer Password 0 =================" + xpp.getText());
                        }
                    }
                }*/


                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("respCode")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                code = (xpp.getText());
                                System.out.println("Resp CODE*****************************" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("respMessage")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                msg = (xpp.getText());
                                System.out.println("Resp MSG*****************************" + xpp.getText());
                            }
                        }
                    }
                }
                eventType = xpp.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            code = "1";
            msg = String.valueOf(e);
        } catch (IOException e) {
            e.printStackTrace();
            code = "1";
            msg = String.valueOf(e);
        }
    }

    public interface OnResultListener {
        void onCompleted(String error, String msg, String ref, String flow);
    }


   /* private DatabaseHelper databaseHelper;
    private String xmlformat, code;
    private HttpURLConnection urlConnection;
    private OnResultListener onResultListener;
    private ArrayList<String> ddetails = new ArrayList<>();
    private String msg;
    private int type;
    private String ref;
    @SuppressLint("StaticFieldLeak")
    private Context context;

    public XML_Parsing(Context context, String xmlformat, int type) {
        this.context = context;
        this.xmlformat = xmlformat;
        this.type = type;
    }


    public void setOnResultListener(OnResultListener onResultListener) {
        this.onResultListener = onResultListener;
    }

    public interface OnResultListener {
        void onCompleted(String error, String msg, ArrayList<String> ddetails, String ref);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(String... param) {
        String url = "http://aepds.dnh.gov.in/ePosServiceJDN2_1Test/jdCommoneposServiceRes?wsdl";
        runRequest(xmlformat, url, type);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (onResultListener != null) {
            onResultListener.onCompleted(code, msg, ddetails, ref);
        }
    }

    private void runRequest(String hit, String url, int type) {
        databaseHelper = new DatabaseHelper(context);
        try {
            System.out.println("INPUT=" + hit);
            java.net.URL Url = new URL(url);
            urlConnection = (HttpURLConnection) Url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "text/xml");
//            urlConnection.setRequestProperty("SOAPAction", "http://mdasol.com/MeterReading/CreateInvoice");
//            urlConnection.setRequestProperty("Authorization", "Basic " + org.kobjects.base64.Base64.encode(("Web" + ":" + "5bs2YO!)A8RMEhcS@ADj").getBytes()));
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            OutputStream outputStream = urlConnection.getOutputStream();
            outputStream.write(hit.getBytes());
            outputStream.flush();
            outputStream.close();
            urlConnection.connect();
            Log.e(getClass().getName(), String.valueOf(urlConnection.getResponseCode()));
            String result = null;
            if (urlConnection.getResponseCode() == 200) {
                BufferedInputStream bis = new BufferedInputStream(urlConnection.getInputStream());
                ByteArrayOutputStream buf = new ByteArrayOutputStream();
                int result2 = bis.read();
                while (result2 != -1) {
                    buf.write((byte) result2);
                    result2 = bis.read();
                }
                result = buf.toString();
            }
            if (result != null && result.length() > 0) {
                System.out.println("OUTPU = " + result);
                if (type == 1) {
                    parseXml_dealer(result);
                } else if (type == 2) {
                    parseXml_dealer_login(result);
                } else if (type == 3) {
                    parseXml_member(result);
                    parseXml_ration(result);
                } else if (type == 4) {
                    parseXml_member_login(result);
                } else if (type == 5){
                    parseXml_sale_details(result);
                } else if (type == 6){
                    parseXml_stock_details(result);
                }else {
                    System.out.println("1000000000000000000000000");
                    parseXml_printer(result);

                }
            } else {
                code = "error";
                msg = "PARSING Error";
            }
        } catch (Exception e) {
            e.printStackTrace();
            msg = String.valueOf(e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    private void parseXml_dealer(String xmlString) {
        ArrayList<String> dauthtypes = new ArrayList<>();
        ArrayList<String> dtypes = new ArrayList<>();
        ArrayList<String> dnames = new ArrayList<>();
        ArrayList<String> duids = new ArrayList<>();
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(xmlString));
            int eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("authType")) {
                        eventType = xpp.next();
                        if (eventType == XmlPullParser.TEXT) {
                            dauthtypes.add(xpp.getText());
                            System.out.println("Dealer Auth Type =================" + xpp.getText());
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("dealer_type")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                dtypes.add(xpp.getText());
                                System.out.println("Dealer Type =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("delName")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                dnames.add(xpp.getText());
                                System.out.println("Dealer Name =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("delUid")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                duids.add(xpp.getText());
                                System.out.println("Dealer UID =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("dealer_password")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                ddetails.add(xpp.getText());
                                System.out.println("Dealer Password 0 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("flasMessage1")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                ddetails.add(xpp.getText());
                                System.out.println("FlasMessage1 1 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("fpsId")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                ddetails.add(xpp.getText());
                                System.out.println("fpsId 2 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("fpsSessionId")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                ddetails.add(xpp.getText());
                                System.out.println("Session ID 3 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("weighAccuracyValueInGms")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                ddetails.add(xpp.getText());
                                System.out.println("weighAccuracyValueInGms 4 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("token")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                ddetails.add(xpp.getText());
                                System.out.println("Token pass 5 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("stateCode")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                ddetails.add(xpp.getText());
                                System.out.println("stateCode 6 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("statefpsId")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                ddetails.add(xpp.getText());
                                System.out.println("Shopid 7 =================" + xpp.getText());
                            }
                        }
                    }

                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("respCode")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                code = (xpp.getText());
                                System.out.println("Resp CODE*****************************" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("respMessage")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                msg = (xpp.getText());
                                System.out.println("Resp MSG*****************************" + xpp.getText());
                            }
                        }
                    }
                }
                eventType = xpp.next();
            }
            databaseHelper.insert_DD(dauthtypes, dtypes, dnames, duids);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            msg = String.valueOf(e);
        } catch (IOException e) {
            e.printStackTrace();
            msg = String.valueOf(e);
        }
    }
    private void parseXml_sale_details(String xmlString) {

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(xmlString));
            int eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("comm_name")) {
                        eventType = xpp.next();
                        if (eventType == XmlPullParser.TEXT) {
                            ddetails.add(xpp.getText());
                            System.out.println("comm_name 0 =================" + xpp.getText());
                        }
                    }

                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("sale")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                ddetails.add(xpp.getText());
                                System.out.println("sale 1 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("schemeName")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                ddetails.add(xpp.getText());
                                System.out.println("schemeName 2 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("total_cards")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                ddetails.add(xpp.getText());
                                System.out.println("total_cards 3 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("respCode")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                code = (xpp.getText());
                                System.out.println("Resp CODE*****************************" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("respMessage")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                msg = (xpp.getText());
                                System.out.println("Resp MSG*****************************" + xpp.getText());
                            }
                        }
                    }
                }
                eventType = xpp.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            msg = String.valueOf(e);
        } catch (IOException e) {
            e.printStackTrace();
            msg = String.valueOf(e);
        }
    }
    private void parseXml_stock_details(String xmlString) {
        ArrayList<String> stock = new ArrayList<>();
        databaseHelper.deletestocktable();
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(xmlString));
            int eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("closing_balance")) {
                        eventType = xpp.next();
                        if (eventType == XmlPullParser.TEXT) {
                            stock.add(xpp.getText());
                            System.out.println("closing_balance 0 =================" + xpp.getText());
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("comm_name")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                stock.add(xpp.getText());
                                System.out.println("comm_name 1 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("issued_qty")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                stock.add(xpp.getText());
                                System.out.println("issued_qty 2 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("received_qty")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                stock.add(xpp.getText());
                                System.out.println("received_qty 3 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("scheme_desc_en")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                stock.add(xpp.getText());
                                databaseHelper.insert_S(stock);
                                stock.clear();
                                System.out.println(stock.size()+"==========================");
                                System.out.println("scheme_desc_en 4 =================" + xpp.getText());
                            }
                        }
                    }
                    *//*if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("dealer_password")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                ddetails.add(xpp.getText());
                                System.out.println("Dealer Password 0 =================" + xpp.getText());
                            }
                        }
                    }*//*



                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("respCode")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                code = (xpp.getText());
                                System.out.println("Resp CODE*****************************" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("respMessage")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                msg = (xpp.getText());
                                System.out.println("Resp MSG*****************************" + xpp.getText());
                            }
                        }
                    }
                }
                eventType = xpp.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            msg = String.valueOf(e);
        } catch (IOException e) {
            e.printStackTrace();
            msg = String.valueOf(e);
        }
    }
    private void parseXml_dealer_login(String xmlString) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(xmlString));
            int eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {

                    if (xpp.getName().equals("respCode")) {
                        eventType = xpp.next();
                        if (eventType == XmlPullParser.TEXT) {
                            code = (xpp.getText());
                            System.out.println("Resp CODE*****************************" + xpp.getText());
                        }
                    }

                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("respMessage")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                msg = (xpp.getText());
                                System.out.println("Resp MSG*****************************" + xpp.getText());
                            }
                        }
                    }
                }
                eventType = xpp.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            msg = String.valueOf(e);
        } catch (IOException e) {
            e.printStackTrace();
            msg = String.valueOf(e);
        }
    }

    private void parseXml_member(String xmlString) {
        ArrayList<String> mnames = new ArrayList<>();
        ArrayList<String> muids = new ArrayList<>();
        ArrayList<String> mFingerauthtypes = new ArrayList<>();
        ArrayList<String> mmemberid = new ArrayList<>();
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(xmlString));
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("memberName")) {
                        eventType = xpp.next();
                        if (eventType == XmlPullParser.TEXT) {
                            mnames.add(xpp.getText());
                            System.out.println("Mem Name =================" + xpp.getText());
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("uid")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                muids.add(xpp.getText());
                                System.out.println("Mem UID =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("xfinger")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                mFingerauthtypes.add(xpp.getText());
                                System.out.println("Finger Auth =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("zmemberId")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                mmemberid.add(xpp.getText());
                                System.out.println("Mem ID =================" + xpp.getText());
                            }
                        }
                    }

                    *//*if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("auth_transaction_code")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                ref = xpp.getText();
                                System.out.println("Ref =================" + xpp.getText());
                            }
                        }
                    }*//*
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("respCode")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                code = (xpp.getText());
                                System.out.println("Resp CODE*****************************" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("respMessage")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                msg = (xpp.getText());
                                System.out.println("Resp MSG*****************************" + xpp.getText());
                            }
                        }
                    }
                }
                eventType = xpp.next();
            }
            for (int i = 0; i < mmemberid.size(); i++) {
                System.out.println(mmemberid.get(i));
            }
            databaseHelper.insert_MD(mnames, muids, mFingerauthtypes, mmemberid);
        } catch (XmlPullParserException e) {
            msg = String.valueOf(e);
            e.printStackTrace();
        } catch (IOException e) {
            msg = String.valueOf(e);
            e.printStackTrace();
        }
    }

    private void parseXml_member_login(String xmlString) {

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(xmlString));
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("auth_transaction_code")) {
                        eventType = xpp.next();
                        if (eventType == XmlPullParser.TEXT) {
                            ref = xpp.getText();
                            System.out.println("Ref =================" + xpp.getText());
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("respCode")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                code = (xpp.getText());
                                System.out.println("Resp CODE*****************************" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("respMessage")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                msg = (xpp.getText());
                                System.out.println("Resp MSG*****************************" + xpp.getText());
                            }
                        }
                    }
                }
                eventType = xpp.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            msg = String.valueOf(e);
        } catch (IOException e) {
            msg = String.valueOf(e);
            e.printStackTrace();
        }
    }

    private void parseXml_ration(String xmlString) {
        ArrayList<String> availedQty = new ArrayList<>();
        ArrayList<String> balQty = new ArrayList<>();
        ArrayList<String> closingBal = new ArrayList<>();
        ArrayList<String> commName = new ArrayList<>();
        ArrayList<String> commcode = new ArrayList<>();
        ArrayList<String> measureUnit = new ArrayList<>();
        ArrayList<String> minQty = new ArrayList<>();
        ArrayList<String> price = new ArrayList<>();
        ArrayList<String> requiredQty = new ArrayList<>();
        ArrayList<String> totQty = new ArrayList<>();
        ArrayList<String> weighing = new ArrayList<>();
        try {

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(xmlString));
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("availedQty")) {
                        eventType = xpp.next();
                        if (eventType == XmlPullParser.TEXT) {
                            availedQty.add(xpp.getText());
                            System.out.println("availedQty =================" + xpp.getText());
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("balQty")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                balQty.add(xpp.getText());
                                System.out.println("balQty =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("closingBal")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                closingBal.add(xpp.getText());
                                System.out.println("closingBal =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("commName")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                commName.add(xpp.getText());
                                System.out.println("ComName =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("commcode")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                commcode.add(xpp.getText());
                                System.out.println("ComCode =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("measureUnit")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                measureUnit.add(xpp.getText());
                                System.out.println("MeasureUnit =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("minQty")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                minQty.add(xpp.getText());
                                System.out.println("minQty =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("price")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                price.add(xpp.getText());
                                System.out.println("price =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("requiredQty")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                requiredQty.add(xpp.getText());
                                System.out.println("requiredQty =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("totQty")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                totQty.add(xpp.getText());
                                System.out.println("totQty =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("weighing")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                weighing.add(xpp.getText());

                                System.out.println("weighing =================" + xpp.getText());
                            }
                        }
                    }
                }
                eventType = xpp.next();
            }
            databaseHelper.insert_RD(availedQty, balQty, closingBal, commName, commcode, measureUnit, minQty, price, requiredQty, totQty, weighing);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            msg = String.valueOf(e);
        } catch (IOException e) {
            e.printStackTrace();
            msg = String.valueOf(e);
        }
    }

    private void parseXml_printer(String xmlString) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(xmlString));
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("bal_qty")) {
                        eventType = xpp.next();
                        if (eventType == XmlPullParser.TEXT) {
                            ddetails.add(xpp.getText());
                            System.out.println("Bal Qty 0=================" + xpp.getText());
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("carry_over")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                ddetails.add(xpp.getText());
                                System.out.println("carry_over 1=================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("commIndividualAmount")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                ddetails.add(xpp.getText());
                                System.out.println("commIndividualAmount 2=================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("comm_name")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                ddetails.add(xpp.getText());
                                System.out.println("comm_name  3=================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("member_name")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                ddetails.add(xpp.getText());
                                System.out.println("member_name 4=================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("reciept_id")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                ddetails.add(xpp.getText());
                                System.out.println("reciept_id 5=================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("retail_price")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                ddetails.add(xpp.getText());
                                System.out.println("retail_price  6=================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("tot_amount")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                ddetails.add(xpp.getText());
                                System.out.println("tot_amount 7=================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("total_quantity")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                ddetails.add(xpp.getText());
                                System.out.println("total_quantity 8=================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("transaction_time")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                ddetails.add(xpp.getText());
                                System.out.println("transaction_time 9 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("rcId")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                ddetails.add(xpp.getText());
                                System.out.println("rcId  10=================" + xpp.getText());
                            }
                        }
                    }

                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("respCode")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                code = (xpp.getText());
                                System.out.println("Resp CODE*****************************" + xpp.getText());
                            }
                        }
                    }

                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("respMessage")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                msg = (xpp.getText());
                                System.out.println("Resp MSG*****************************" + xpp.getText());
                            }
                        }
                    }
                }
                eventType = xpp.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            msg = String.valueOf(e);
        } catch (IOException e) {
            e.printStackTrace();
            msg = String.valueOf(e);
        }
    }*/




    /*public void parseXml(){
        try {

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();

            xpp.setInput( new StringReader( "<foo>Hello World!</foo>" ) ); // pass input whatever xml you have
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if(eventType == XmlPullParser.START_DOCUMENT) {
                    Log.d(TAG,"Start document");
                } else if(eventType == XmlPullParser.START_TAG) {
                    Log.d(TAG,"Start tag "+xpp.getName());
                } else if(eventType == XmlPullParser.END_TAG) {
                    Log.d(TAG,"End tag "+xpp.getName());
                } else if(eventType == XmlPullParser.TEXT) {
                    Log.d(TAG,"Text "+xpp.getText()); // here you get the text from xml
                }
                eventType = xpp.next();
            }
            Log.d(TAG,"End document");

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
}