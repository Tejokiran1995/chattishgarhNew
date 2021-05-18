package com.visiontek.Mantra.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.visiontek.Mantra.Models.InspectionAuth;

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
import java.util.ArrayList;

public class Aadhaar_Parsing extends AsyncTask<String, Void, Void> {
    private final String xmlformat;
    private final int type;
    @SuppressLint("StaticFieldLeak")
    private final Context context;
    ArrayList<String> buffer = new ArrayList<>();
    private DatabaseHelper databaseHelper;
    private String code;
    private HttpURLConnection urlConnection;
    private OnResultListener onResultListener;
    private String msg;
    private String ref;

    public Aadhaar_Parsing(Context context, String xmlformat, int type) {
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
        String url ;
        url = "http://epos.nic.in/ePosServiceCTG/jdCommoneposServiceRes?wsdl";
        runRequest(url);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (onResultListener != null) {
            onResultListener.onCompleted(code, msg, ref, buffer);
        }
    }

    private void runRequest(String url) {
        databaseHelper = new DatabaseHelper(context);
        try {
            System.out.println("INPUT=" + xmlformat);
            URL Url = new URL(url);
            urlConnection = (HttpURLConnection) Url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "text/xml");
//            urlConnection.setRequestProperty("SOAPAction", "http://mdasol.com/MeterReading/CreateInvoice");
//            urlConnection.setRequestProperty("Authorization", "Basic " + org.kobjects.base64.Base64.encode(("Web" + ":" + "5bs2YO!)A8RMEhcS@ADj").getBytes()));
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            OutputStream outputStream = urlConnection.getOutputStream();
            outputStream.write(xmlformat.getBytes());
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
                    Util.generateNoteOnSD(context, "UIDSeedingRes.txt", result);
                    parseXml_UIDSEEDING(result);
                } else if (type == 2) {
                    Util.generateNoteOnSD(context, "UIDAuthRes.txt", result);
                    parseXml_UIDAuth(result);
                } else if (type == 3) {
                    Util.generateNoteOnSD(context, "BenVerificationRes.txt", result);
                    parseXml_BENVERIFICATION(result);
                } else if (type == 4) {
                    Util.generateNoteOnSD(context, "BenVerificationAuthRes.txt", result);
                    parseXml_BENVERIFICATIONLOGIN(result);
                } else if (type == 5) {
                    Util.generateNoteOnSD(context, "InspectionDetailsRes.txt", result);
                    parseXml_INSPECTION(result);
                } else if (type == 6) {
                    Util.generateNoteOnSD(context, "InspectionAuthRes.txt", result);
                    parseXml_INSPECTION_AUTH(result);
                } else if (type == 7) {
                    Util.generateNoteOnSD(context, "InspectionPushRes.txt", result);
                    parseXml_INSPECTION_PUSH(result);
                } else if (type == 8) {
                   /* parseXml_INSPECTION(result);
                    Util.generateNoteOnSD(context, "InspectionPushRes.txt", result);*/
                } else if (type == 9) {
                    Util.generateNoteOnSD(context, "StockUploadDetailsRes.txt", result);
                    parseXml_INSPECTION_AUTH(result);
                }else if (type == 10) {
                    Util.generateNoteOnSD(context, "LogoutRes.txt", result);
                    parseXml_Logout(result);
                } else {
                    System.out.println("1000000000000000000000000");
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

    private void parseXml_Logout(String result) {
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
                            System.out.println("respCode =================" + xpp.getText());
                        }
                    }

                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("respMessage")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                msg = (xpp.getText());
                                System.out.println("respMessage *****************************" + xpp.getText());
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

    private void parseXml_UIDAuth(String result) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(result));
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("eKYCMemberName")) {
                        eventType = xpp.next();
                        if (eventType == XmlPullParser.TEXT) {
                            buffer.add(xpp.getText());
                            System.out.println("eKYCMemberName 0 =================" + xpp.getText());
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("eKYCMemberFatherName")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                buffer.add(xpp.getText());
                                System.out.println("eKYCMemberFatherName 1*****************************" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("eKYCDOB")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                buffer.add(xpp.getText());
                                System.out.println("eKYCDOB 2*****************************" + xpp.getText());
                            }
                        }
                    }

                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("eKYCPindCode")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                buffer.add(xpp.getText());
                                System.out.println("eKYCPindCode 3*****************************" + xpp.getText());
                            }
                        }
                    }

                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("eKYCGeneder")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                buffer.add(xpp.getText());
                                System.out.println("eKYCGeneder 4*****************************" + xpp.getText());
                            }
                        }
                    }

                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("location")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                buffer.add(xpp.getText());
                                System.out.println("location 5 *****************************" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("village")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                buffer.add(xpp.getText());
                                System.out.println("village 6 *****************************" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("dist")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                buffer.add(xpp.getText());
                                System.out.println("dist 7 *****************************" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("state")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                buffer.add(xpp.getText());
                                System.out.println("state 8 *****************************" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("pincode")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                buffer.add(xpp.getText());
                                System.out.println("pincode  9*****************************" + xpp.getText());
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

                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("transEntitlementFlow")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                ref = (xpp.getText());
                                System.out.println("transEntitlementFlow *****************************" + xpp.getText());
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

    private void parseXml_INSPECTION_PUSH(String result) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(result));
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("receiptId")) {
                        eventType = xpp.next();
                        if (eventType == XmlPullParser.TEXT) {
                            ref = (xpp.getText());
                            System.out.println("receiptId 1 =================" + xpp.getText());
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

    private void parseXml_BENVERIFICATIONLOGIN(String result) {

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(result));
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("eKYCMemberName")) {
                        eventType = xpp.next();
                        if (eventType == XmlPullParser.TEXT) {
                            buffer.add(xpp.getText());
                            System.out.println("eKYCMemberName 0 =================" + xpp.getText());
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("eKYCMemberFatherName")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                buffer.add(xpp.getText());
                                System.out.println("eKYCMemberFatherName 1*****************************" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("eKYCDOB")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                buffer.add(xpp.getText());
                                System.out.println("eKYCDOB 2*****************************" + xpp.getText());
                            }
                        }
                    }

                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("eKYCPindCode")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                buffer.add(xpp.getText());
                                System.out.println("eKYCPindCode 3*****************************" + xpp.getText());
                            }
                        }
                    }

                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("eKYCGeneder")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                buffer.add(xpp.getText());
                                System.out.println("eKYCGeneder 4*****************************" + xpp.getText());
                            }
                        }
                    }

                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("location")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                buffer.add(xpp.getText());
                                System.out.println("location 5 *****************************" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("village")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                buffer.add(xpp.getText());
                                System.out.println("village 6 *****************************" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("dist")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                buffer.add(xpp.getText());
                                System.out.println("dist 7 *****************************" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("state")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                buffer.add(xpp.getText());
                                System.out.println("state 8 *****************************" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("pincode")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                buffer.add(xpp.getText());
                                System.out.println("pincode  9*****************************" + xpp.getText());
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

                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("transEntitlementFlow")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                ref = (xpp.getText());
                                System.out.println("transEntitlementFlow *****************************" + xpp.getText());
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

    private void parseXml_INSPECTION_AUTH(String result) {
        InspectionAuth inspectionAuth=new InspectionAuth();

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(result));
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("auth_transaction_code")) {
                        eventType = xpp.next();
                        if (eventType == XmlPullParser.TEXT) {
                            buffer.add(xpp.getText());
                            System.out.println("auth_transaction_code 0 =================" + xpp.getText());
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("inspectorDesignation")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                buffer.add(xpp.getText());
                                System.out.println("inspectorDesignation 1 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("inspectorName")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                buffer.add(xpp.getText());
                                System.out.println("inspectorName 2 =====================" + xpp.getText());
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

    private void parseXml_UIDSEEDING(String xmlString) {
        ArrayList<String> bfd_1 = new ArrayList<>();
        ArrayList<String> bfd_2 = new ArrayList<>();
        ArrayList<String> bfd_3 = new ArrayList<>();
        ArrayList<String> memberId = new ArrayList<>();
        ArrayList<String> memberName = new ArrayList<>();
        ArrayList<String> memberNamell = new ArrayList<>();
        ArrayList<String> member_fusion = new ArrayList<>();
        ArrayList<String> uid = new ArrayList<>();
        ArrayList<String> w_uid_status = new ArrayList<>();


        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(xmlString));
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("bfd_1")) {
                        eventType = xpp.next();
                        if (eventType == XmlPullParser.TEXT) {
                            bfd_1.add(xpp.getText());
                            System.out.println("bfd_1 1 =================" + xpp.getText());
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("bfd_2")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                bfd_2.add(xpp.getText());
                                System.out.println("bfd_2 2 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("bfd_3")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                bfd_3.add(xpp.getText());
                                System.out.println("bfd_3 3 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("memberId")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                memberId.add(xpp.getText());
                                System.out.println("memberId 4 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("memberName")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                memberName.add(xpp.getText());
                                System.out.println("memberName 5 =================" + xpp.getText());
                            }
                        }
                    }

                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("memberNamell")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                memberNamell.add(xpp.getText());
                                System.out.println("memberNamell 6 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("member_fusion")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                member_fusion.add(xpp.getText());
                                System.out.println("member_fusion 7 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("uid")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                uid.add(xpp.getText());
                                System.out.println("uid 8 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("w_uid_status")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                w_uid_status.add(xpp.getText());
                                System.out.println("w_uid_status 9 =================" + xpp.getText());
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
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("zwadh")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                ref = (xpp.getText());
                                System.out.println("zwadh *****************************" + xpp.getText());
                            }
                        }
                    }
                }
                eventType = xpp.next();
            }

            databaseHelper.insert_UID(bfd_1,
                    bfd_2,
                    bfd_3,
                    memberId,
                    memberName,
                    memberNamell,
                    member_fusion,
                    uid,
                    w_uid_status);

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

    private void parseXml_INSPECTION(String xmlString) {
        ArrayList<String> closingBalance = new ArrayList<>();
        ArrayList<String> commCode = new ArrayList<>();
        ArrayList<String> commNameEn = new ArrayList<>();
        ArrayList<String> commNamell = new ArrayList<>();
        ArrayList<String> approveKey = new ArrayList<>();
        ArrayList<String> approveValue = new ArrayList<>();

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(xmlString));
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("closingBalance")) {
                        eventType = xpp.next();
                        if (eventType == XmlPullParser.TEXT) {
                            closingBalance.add(xpp.getText());
                            System.out.println("closingBalance 1 =================" + xpp.getText());
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("commCode")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                commCode.add(xpp.getText());
                                System.out.println("commCode 2 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("commNameEn")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                commNameEn.add(xpp.getText());
                                System.out.println("commNameEn 3 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("commNamell")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                commNamell.add(xpp.getText());
                                System.out.println("commNamell 4 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("approveKey")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                approveKey.add(xpp.getText());
                                System.out.println("uid 5 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("approveValue")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                approveValue.add(xpp.getText());
                                System.out.println("approveValue 6 =================" + xpp.getText());
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
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("zwadh")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                ref = (xpp.getText());
                                System.out.println("zwadh *****************************" + xpp.getText());
                            }
                        }
                    }
                }
                eventType = xpp.next();
            }

            databaseHelper.insert_INSPECTION(
                    closingBalance,
                    commCode,
                    commNameEn,
                    commNamell
            );

            databaseHelper.insert_INSPECTION_app(
                    approveKey, approveValue
            );

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

    private void parseXml_BENVERIFICATION(String xmlString) {
        ArrayList<String> memberId = new ArrayList<>();
        ArrayList<String> memberName = new ArrayList<>();
        ArrayList<String> memberNamell = new ArrayList<>();
        ArrayList<String> member_fusion = new ArrayList<>();
        ArrayList<String> uid = new ArrayList<>();
        ArrayList<String> verification = new ArrayList<>();
        ArrayList<String> verifyStatus_en = new ArrayList<>();
        ArrayList<String> verifyStatus_ll = new ArrayList<>();
        ArrayList<String> w_uid_status = new ArrayList<>();


        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(xmlString));
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("memberId")) {
                        eventType = xpp.next();
                        if (eventType == XmlPullParser.TEXT) {
                            memberId.add(xpp.getText());
                            System.out.println("memberId 1 =================" + xpp.getText());
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("memberName")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                memberName.add(xpp.getText());
                                System.out.println("memberName 2 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("memberNamell")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                memberNamell.add(xpp.getText());
                                System.out.println("bfd_3 3 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("member_fusion")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                member_fusion.add(xpp.getText());
                                System.out.println("member_fusion 4 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("uid")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                uid.add(xpp.getText());
                                System.out.println("uid 5 =================" + xpp.getText());
                            }
                        }
                    }

                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("verification")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                verification.add(xpp.getText());
                                System.out.println("verification 6 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("verifyStatus_en")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                verifyStatus_en.add(xpp.getText());
                                System.out.println("verifyStatus_en 7 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("verifyStatus_ll")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                verifyStatus_ll.add(xpp.getText());
                                System.out.println("verifyStatus_ll 8 =================" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("w_uid_status")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                w_uid_status.add(xpp.getText());
                                System.out.println("w_uid_status 9 =================" + xpp.getText());
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
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("zwadh")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                ref = (xpp.getText());
                                System.out.println("zwadh *****************************" + xpp.getText());
                            }
                        }
                    }
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("rationCardId")) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                buffer.add (xpp.getText());
                                System.out.println("zwadh *****************************" + xpp.getText());
                            }
                        }
                    }
                }
                eventType = xpp.next();
            }

            databaseHelper.insert_BEN(memberId,
                    memberName,
                    memberNamell,
                    member_fusion,
                    uid,
                    verification,
                    verifyStatus_en,
                    verifyStatus_ll,
                    w_uid_status);

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
        void onCompleted(String error, String msg, String ref, ArrayList<String> buffer);
    }
}
