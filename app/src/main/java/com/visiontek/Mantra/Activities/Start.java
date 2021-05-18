package com.visiontek.Mantra.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.visiontek.Mantra.Models.OfflineAPIs;
import com.visiontek.Mantra.Models.fpsCommonInfo;
import com.visiontek.Mantra.Models.fpsURLInfo;
import com.visiontek.Mantra.Models.stateBean;
import com.visiontek.Mantra.R;
import com.visiontek.Mantra.Utils.DatabaseHelper;
import com.visiontek.Mantra.Utils.Json_Parsing;
import com.visiontek.Mantra.Utils.TelephonyInfo;
import com.visiontek.Mantra.Utils.Util;
import com.visiontek.Mantra.Utils.XML_Parsing;

import java.sql.SQLException;
import java.util.List;
import java.util.Locale;

import static com.visiontek.Mantra.Utils.Util.DEVICEID;
import static com.visiontek.Mantra.Utils.Util.KEY;
import static com.visiontek.Mantra.Utils.Util.RDservice;
import static com.visiontek.Mantra.Utils.Util.TOKEN;
import static com.visiontek.Mantra.Utils.Util.VERSION;
import static com.visiontek.Mantra.Utils.Util.releaseMediaPlayer;

public class Start extends AppCompatActivity {
    fpsURLInfo fpsURLInfo=new fpsURLInfo();
    fpsCommonInfo fpsCommonInfo=new fpsCommonInfo();
    stateBean stateBean=new stateBean();

    DatabaseHelper db;
    private static final int READ_PHONE_STATE = 1;
    private static final int REQUEST_ACCESS_COARSE_LOCATION = 2;
    private static final int REQUEST_STORAGE_WRITE_SDCARD = 3;
    static String L;
    Context context;
    Button start, quit, settings;

    ProgressDialog pd = null;

    LocationManager locationManager;
    int PS;
    TelephonyInfo telephonyInfo;
    String STATE, IMEI;
    TelephonyManager telephonyManager;
    SubscriptionManager subscriptionManager;
    List<SubscriptionInfo> subscriptionInfoList;
    String longitude;
    String latitude;

    OfflineAPIs offline = new OfflineAPIs();
    //String LANG;

    Button setlang;
    static MediaPlayer mp;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = Start.this;
        db = new DatabaseHelper(context);
        L=db.get_L("Language");
        if (L != null && !L.isEmpty()) {
            setLocal(L);
        }
        setContentView(R.layout.activity_start);

        context = Start.this;
        TextView rd = findViewById(R.id.rd);
        boolean  rd_fps;
        rd_fps = RDservice(context);
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.enable();
        if (rd_fps) {
            rd.setTextColor(context.getResources().getColor(R.color.green));
        } else {
           // toast(context, context.getResources().getString(R.string.No_RD_Service));
            show_error_box(context.getResources().getString(R.string.RD_Service_Msg),context.getResources().getString(R.string.RD_Service));
            rd.setTextColor(context.getResources().getColor(R.color.black));
        }

        mp = mp.create(context,R.raw.c100041);
        mp.start();

        PS = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, READ_PHONE_STATE);

        pd = new ProgressDialog(context);
        start = findViewById(R.id.button_start);
        quit = findViewById(R.id.button_quit);
        settings = findViewById(R.id.button_settings);



        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Util.networkConnected(context)) {


                    if (mp!=null) {
                        releaseMediaPlayer(context,mp);
                    }
                    if (L.equals("hi")) {

                        mp = mp.create(context, R.raw.c200175);
                        mp.start();
                    }else {
                        mp = mp.create(context, R.raw.c100175);
                        mp.start();
                    }
                    FramexmlforDealerDetails();





                    /*
                    1. Check if DB contains present month details

                     */
                } else {
                    /*
                    1. Check if DB contains details
                    2. Check if new month
                    3.
                     */
                    //toast(context, context.getResources().getString(R.string.Please_check_Internet_Connection));

                    //call key register here

                    //offline.parsegetFpsOfflineData(response,getApplicationContext());


                    show_error_box(context.getResources().getString(R.string.Internet_Connection_Msg),context.getResources().getString(R.string.Internet_Connection));
                }
            }
        });

        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show_box();
            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent settings = new Intent(context, Setting.class);
                //settings.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityForResult(settings,1);
            }
        });
    }

    private void keyregisterurl(String keyregister) {
        System.out.println("@@Executing: " +keyregister);
        pd = ProgressDialog.show(context, context.getResources().getString(R.string.Beneficiary_Details), context.getResources().getString(R.string.Consent_Form), true, false);
        Json_Parsing request = new Json_Parsing(context, keyregister, 5);
        request.setOnResultListener(new Json_Parsing.OnResultListener() {

            @Override
            public void onCompleted(String code, String msg) throws SQLException {
                if (pd.isShowing()) {
                    pd.dismiss();
                }
                if (!code.equals("00")) {
                    show_error_box(msg, code );
                } else {
                    String[] monthyear= db.getMonthYear(context);
                    System.out.println("@@Month and Year: " +monthyear[0]);
                    String CB="{\n" +
                            "\"fpsId\" : "+"\""+fpsCommonInfo.getfpsId()+"\""+",\n" +
                            "\"sessionId\" : "+"\""+fpsCommonInfo.getfpsSessionId()+"\""+",\n" +
                            "\"terminalId\" : "+"\""+DEVICEID+"\""+",\n" +
                            "\"token\" : "+"\""+fpsURLInfo.gettoken()+"\""+",\n" +
                            "\"stateCode\" : "+"\""+stateBean.getstateCode()+"\""+",\n" +
                            "\"allocationMonth\" : "+"\""+monthyear[0]+"\""+",\n" +
                            "\"allocationYear\"  : "+"\""+monthyear[1]+"\""+"\n" +
                            "}";

                    System.out.println("@@ Going to CBDownload");
                    CBDownload(CB);
                }
            }

        });

    }
    private void CBDownload(String keyregister) {
        System.out.println("@@ In CBDownload");
        pd = ProgressDialog.show(context, context.getResources().getString(R.string.Beneficiary_Details), context.getResources().getString(R.string.Consent_Form), true, false);
        Json_Parsing request = new Json_Parsing(context, keyregister, 6);
        request.setOnResultListener(new Json_Parsing.OnResultListener() {

            @Override
            public void onCompleted(String code, String msg) {
                if (pd.isShowing()) {
                    pd.dismiss();
                }
                if (!code.equals("00")) {
                    show_error_box(msg, code );
                } else {

                    Intent i = new Intent(Start.this, Dealer_Details.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);

                }
            }

        });

    }

    private void FramexmlforDealerDetails() {

        String dealers = "<?xml version='1.0' encoding='UTF-8' standalone='no' ?>\n" +
                "<SOAP-ENV:Envelope\n" +
                "    xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
                "    xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\"\n" +
                "    xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n" +
                "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "    xmlns:ns1=\"http://service.fetch.rationcard/\">\n" +
                "    <SOAP-ENV:Body>\n" +
                "        <ns1:getPDSFpsNoDetails>\n" +
                "            <VersionNo>" + VERSION + "</VersionNo>\n" +//static
                "            <deviceID>" + DEVICEID + "</deviceID>\n" +//dynamic
                "            <token>" + TOKEN + "</token>\n" +//static
                "            <key>" + KEY + "</key>\n" +//static
                "            <simID>" + IMEI + "</simID>\n" +//dynamic
                "            <checkSum></checkSum>\n" +//dynamic
                "            <longtude>" + longitude + "</longtude>\n" +//dynamic
                "            <latitude>" + latitude + "</latitude>\n" +//dynamic
                "            <vendorId></vendorId>\n" +//static
                "            <simStatus>" + STATE + "</simStatus>\n" +//dynamic
                "        </ns1:getPDSFpsNoDetails>\n" +
                "    </SOAP-ENV:Body>\n" +
                "</SOAP-ENV:Envelope>\n";
        Util.generateNoteOnSD(context, "DDetailsReq.txt", dealers);
        hitURL(dealers);
    }

    private void hitURL(String xmlformat) {
        pd = ProgressDialog.show(context, context.getResources().getString(R.string.Dealer_Details), context.getResources().getString(R.string.Fetching_Dealers), true, false);
        XML_Parsing request = new XML_Parsing(Start.this, xmlformat, 1);
        request.setOnResultListener(new XML_Parsing.OnResultListener() {

            @Override
            public void onCompleted(String isError, String msg, String ref, String flow) {
                if (pd.isShowing()) {
                    pd.dismiss();
                }
                if (!isError.equals("00")) {
                    show_error_box(msg, context.getResources().getString(R.string.Dealer_Details) + isError);
                } else {
                    int ret = 0;
                    try {

                        ret = db.isDataAvailable(context);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                    if(ret == 0)
                    {
                        //Data already available
                        System.out.println("@@Data already available");

                    }else{
                        if (fpsCommonInfo.partialOnlineOfflineStatus.equals("Y")){
                            System.out.println("@@Downling offline data");
                            String keyregister="{\n" +
                                    "\"fpsId\" : "+"\""+fpsCommonInfo.getfpsId()+"\""+",\n" +
                                    "\"sessionId\" : "+"\""+fpsCommonInfo.getfpsSessionId()+"\""+",\n" +
                                    "\"terminalId\" : "+"\""+DEVICEID+"\""+",\n" +
                                    "\"token\" : "+"\""+ fpsURLInfo.gettoken()+"\""+",\n" +
                                    "\"stateCode\" : "+"\""+ stateBean.getstateCode()+"\""+"\n" +
                                    "}";
                            keyregisterurl(keyregister);



                        }else {
                            //Data not available download
                        }
                        //Data not available download

                    }

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

    private void show_box() {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage(context.getResources().getString(R.string.Do_you_want_to_cancel_Session));
        alertDialogBuilder.setTitle(context.getResources().getString(R.string.CHHATISGARHPDS));
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1){
            recreate();
        }
    }
    public void setLocal(String lang) {
        if (lang != null) {
            Locale locale = new Locale(lang);
            System.out.println("++++++++++++++++++++++++++++++++SET" + lang);
            Locale.setDefault(locale);
            Configuration con = new Configuration();
            con.locale = locale;
            getBaseContext().getResources().updateConfiguration(con, getBaseContext().getResources().getDisplayMetrics());
        }
    }
    private boolean getLocation() {

        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        } else {
            locationProviders = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    private void DisplayGPS() {

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean st = getLocation();
        System.out.println("LOCATION = " + st);
        if (!st) {
            statusCheck();
        }
        LocationListener locationListener = new MyLocationListener();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener);

    }

    public void statusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(context.getResources().getString(R.string.Your_GPS_seems_to_be_disabled_do_you_want_to_enable_it))
                .setCancelable(false)
                .setPositiveButton(context.getResources().getString(R.string.Yes), new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton(context.getResources().getString(R.string.No), new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    private void get_method() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;

        }
        //MultiSimTelephonyManager multiSimTelephonyManager = new MultiSimTelephonyManager(this);
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        subscriptionManager = (SubscriptionManager) getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
        subscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();
        telephonyInfo = TelephonyInfo.getInstance(this);
        STATE = null;
        STATE = get_state();

        if (STATE.equals("No Sim")) {
            System.out.println("No Sims");
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            IMEI = get_Imei();

        }

    }

    @SuppressLint("HardwareIds")
    @RequiresApi(api = Build.VERSION_CODES.M)
    private String get_Imei() {
        String imei = null;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return null;
        }
        String sim1_imei = telephonyManager.getDeviceId(0);
        String sim2_imei = telephonyManager.getDeviceId(1);
        String SIM;
        if (sim2_imei.length() <= 0 || sim2_imei == null || sim2_imei.isEmpty()) {
            SIM = sim1_imei;
        } else {
            SIM = sim2_imei;
        }
        return SIM;
    }

    private String get_state() {

        boolean isSIM1Ready = telephonyInfo.isSIM1Ready();
        boolean isSIM2Ready = telephonyInfo.isSIM2Ready();

        if (isSIM1Ready && isSIM2Ready) {

            return "Y";
        } else if (isSIM1Ready) {
            return "Y";
        } else if (isSIM2Ready) {
            return "Y";
        } else {
            return "N";
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case READ_PHONE_STATE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_ACCESS_COARSE_LOCATION);
                    }
                } else {
                    show_error_box(context.getResources().getString(R.string.Please_allow_these_permissions_in_order_to_use_application), context.getResources().getString(R.string.Permissions));
                }
                break;
            case REQUEST_ACCESS_COARSE_LOCATION:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE_WRITE_SDCARD);

                    }
                } else {
                    show_error_box(context.getResources().getString(R.string.Please_allow_these_permissions_in_order_to_use_application), context.getResources().getString(R.string.Permissions));
                }
                break;
            case REQUEST_STORAGE_WRITE_SDCARD:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                        Startbutton();
                    }
                } else {
                    show_error_box(context.getResources().getString(R.string.Please_allow_these_permissions_in_order_to_use_application), context.getResources().getString(R.string.Permissions));
                }
                break;
            default:
                break;
        }
    }

    private void Startbutton() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            get_method();
            //DisplayGPS();
        }


        TelephonyManager telephonyManager = null;
        telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        String simSerialNo = telephonyManager.getSimSerialNumber();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            System.out.println("++++++++++++++++++++++++++++++" + Build.getSerial());
            String serial = Build.getSerial();
            serial = serial.substring(1, serial.length() - 1);
            System.out.println("++++++++++++++++++++++++++++++" + serial);

        }

    }

    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {


            longitude = String.valueOf(loc.getLongitude());
            System.out.println("-------------------" + longitude);
            latitude = String.valueOf(loc.getLatitude());
            System.out.println("-------------------" + latitude);

            locationManager.removeUpdates(this);
            locationManager = null;
        }

        @Override
        public void onProviderDisabled(String provider) {
            System.out.println("*********Disabled");
        }

        @Override
        public void onProviderEnabled(String provider) {
            System.out.println("*********Enabled");
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            System.out.println("*********Status Changed");
        }
    }

}