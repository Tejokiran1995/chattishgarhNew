package com.visiontek.Mantra.Activities;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;

import com.visiontek.Mantra.BuildConfig;
import com.visiontek.Mantra.R;
import com.visiontek.Mantra.Utils.MyFTPClientFunctions;

import java.io.File;
import java.util.Locale;

import static com.visiontek.Mantra.Utils.Util.RDservice;
import static com.visiontek.Mantra.Utils.Util.toast;


public class Device_Update extends AppCompatActivity {

    Button usb, gprs;
    Context context;
    String something;
    MyFTPClientFunctions ftpclient = new MyFTPClientFunctions();
    NotificationCompat.Builder mBuilder;
    ProgressDialog pd, progressDialog;
    Long fsize, file_size, freeInternalValue;
    String

            fHostName = "115.111.229.10",
            fUserName = "rnd",
            fPassword = "rnd123",
            Source = "/rnd/",
            FTP_file = "sample.apk",
            Download = "/rnd/" + FTP_file,

    Destination,
            Device_Download_path;
    AlertDialog falert;
    int fcancel, flag, fpercentage, Fflag;
    boolean fstatus;
    File Deletefile;
    String J;
    private NotificationManager mNotifyManager;
    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            if (msg.what == 2) {
                if (ftpclient.isOnline(Device_Update.this)) {
                    fdownload();
                } else {
                    show_error_box(context.getResources().getString(R.string.Internet_Connection_Msg),context.getResources().getString(R.string.Internet_Connection));
                }
            } else if (msg.what == 3) {
                Fflag = 0;
                install();
            } else if (msg.what == 4) {
                something = context.getResources().getString(R.string.Something_went_Wrong_Please_Try_Again);
                System.out.println(something);
                Toast.makeText(context, something, Toast.LENGTH_SHORT).show();

            } else if (msg.what == 5) {
                something = context.getResources().getString(R.string.No_Storage_Space_to_Download_this_File);
                System.out.println(something);
                Toast.makeText(context, something, Toast.LENGTH_SHORT).show();
            } else if (msg.what == 6) {
                something =context.getResources().getString(R.string.Connection_Problem_or_Server_Down);
                System.out.println(something);
                Toast.makeText(context, something, Toast.LENGTH_SHORT).show();
            } else if (msg.what == 8) {
                something = context.getResources().getString(R.string.Server_Reply_code_is_Negetive_Please_try_again);
                System.out.println(something);
                Toast.makeText(context, something, Toast.LENGTH_SHORT).show();
            } else if (msg.what == 9) {
                progressDialog.setProgress(fpercentage);
            } else if (msg.what == 10) {

                something = context.getResources().getString(R.string.Download_Failed);
                System.out.println(something);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(context.getResources().getString(R.string.Something_went_Wrong_Please_Try_Again))
                        .setCancelable(false)
                        .setNegativeButton(context.getResources().getString(R.string.Ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });
                falert = builder.create();
                falert.setTitle(something);
                falert.show();
                falert.setCancelable(false);
            } else if (msg.what == 11) {
                something = context.getResources().getString(R.string.Authentication_Problem);
                System.out.println(something);
                Toast.makeText(context, something, Toast.LENGTH_SHORT).show();
            } else {
                something = context.getResources().getString(R.string.UNKNOWN_ERROR);
                System.out.println(something);
                Toast.makeText(context, something, Toast.LENGTH_SHORT).show();
            }

        }
    };

    public static long getAvailableInternalMemorySize() {

        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_device__update);
        context = Device_Update.this;
        gprs = findViewById(R.id.gprs);
        usb = findViewById(R.id.usb);

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
        Device_Download_path = (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/");
        Destination = Device_Download_path + "sample.apk";
        usb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //J="USb/download/";
                show_error_box(context.getResources().getString(R.string.Connect_your_device_to_the_usb_Port), context.getResources().getString(R.string.USB_Connection));
            }
        });
        gprs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //J="/sdcard/";
                filesize();
            }
        });

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

    private void filesize() {
        try {
            if (ftpclient.isOnline(this)) {
                pd = ProgressDialog.show(context, "", context.getResources().getString(R.string.Processing), false, false);

                new Thread(new Runnable() {
                    public void run() {
                        fsize = (ftpclient.Ffinding(fHostName, fUserName, fPassword, Source, FTP_file));
                        pd.dismiss();
                        if (ftpclient.Toknow == 40) {
                            freeInternalValue = getAvailableInternalMemorySize();
                            if (fsize == null) {
                                handler.sendEmptyMessage(2);
                            } else {
                                if (freeInternalValue >= fsize) {
                                    handler.sendEmptyMessage(2);
                                } else {
                                    handler.sendEmptyMessage(5);
                                }
                            }
                        } else if (ftpclient.Toknow == 41) {
                            handler.sendEmptyMessage(4);
                        } else if (ftpclient.Toknow == 42) {
                            handler.sendEmptyMessage(11);
                        } else if (ftpclient.Toknow == 43) {
                            handler.sendEmptyMessage(8);
                        } else if (ftpclient.Toknow == 44) {
                            handler.sendEmptyMessage(6);
                        } else if (ftpclient.Toknow == 45) {
                            handler.sendEmptyMessage(6);
                        } else {
                            handler.sendEmptyMessage(-1);
                        }
                    }
                }).start();
            } else {

                show_error_box(context.getResources().getString(R.string.Internet_Connection_Msg),context.getResources().getString(R.string.Internet_Connection));

            }
        } catch (Exception e) {
            something = context.getResources().getString(R.string.PROGRAMMATICAL_ERROR_IN_GETTING_FILE_SIZE);
            System.out.println(something);
            Toast.makeText(this, something,
                    Toast.LENGTH_SHORT).show();

        }
    }

    private void fdownload() {
        try {
            if (ftpclient.isOnline(this)) {
                Deletefile = new File(Destination);
                mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mBuilder = new NotificationCompat.Builder(this, "");
                mBuilder.setContentTitle("APK")
                        .setContentText(context.getResources().getString(R.string.Download_in_progress))
                        .setSmallIcon(R.drawable.eye9);
                mNotifyManager.notify(0, mBuilder.build());
                mBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
                mBuilder.setAutoCancel(true);
                System.out.println("-------------111111");
                progressDialog = new ProgressDialog(context);
                progressDialog.setMax(100);
                long fmb = fsize / (1024 * 1024);
                progressDialog.setTitle(FTP_file + ": " + fmb + "MB");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, context.getResources().getString(R.string.Cancel), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            fcancel = 1;
                            ftpclient.desFileStream.close();
                            mBuilder.setContentText(context.getResources().getString(R.string.Download_Cancelled));
                            mNotifyManager.notify(0, mBuilder.build());
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage(context.getResources().getString(R.string.Download_Cancelled))
                                    .setCancelable(false)
                                    .setNegativeButton(context.getResources().getString(R.string.Ok), new DialogInterface.OnClickListener() {
                                        @TargetApi(Build.VERSION_CODES.KITKAT)
                                        public void onClick(DialogInterface dialog, int id) {
                                            something =context.getResources().getString(R.string.Download_Cancelled);

                                        }
                                    });
                            falert = builder.create();
                            falert.setTitle(FTP_file);
                            falert.show();
                            falert.setCancelable(false);
                        } catch (Exception e) {
                            System.out.println("FTP....Download....000000000000000000000000...EXCEPTION..NOT DISCONNECTED");
                            e.printStackTrace();
                        }
                    }
                });
                progressDialog.show();
                progressDialog.setCancelable(false);
                System.out.println("000000000000000000000000...Download in Progress");

                new Thread(new Runnable() {
                    public void run() {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {

                                    fpercentage = 0;
                                    flag = 0;
                                    while (fpercentage < progressDialog.getMax()) {
                                        file_size = Long.valueOf(((String.valueOf(Deletefile.length()))));
                                        fpercentage = (int) ((file_size * 100) / fsize);
                                        //System.out.println("LLLLLLLLLL : " + Long.valueOf((String.valueOf(Dfile.length()))) + " percentage = " + fpercentage + "kkkk = " + fsize);
                                        handler.sendEmptyMessage(9);
                                        if (fpercentage == progressDialog.getMax() || flag == 1) {
                                            fpercentage = 0;
                                            flag = 0;
                                            return;
                                        }
                                    }
                                } catch (Exception e) {
                                    System.out.println("FTP....Download....000000000000000000000000...Exception unknown");
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                fstatus = false;
                                fstatus = ftpclient.ftpDownload(Download, Destination);
                                System.out.println("000000000000000000000000...Download Status" + fstatus);
                                flag = 1;

                                if (!fstatus) {
                                    if (Deletefile.exists()) {
                                        Deletefile.delete();
                                    }
                                    if (ftpclient.Toknow == 30) {
                                        progressDialog.dismiss();
                                        mBuilder.setContentText(context.getResources().getString(R.string.Download_Failed));
                                        mNotifyManager.notify(0, mBuilder.build());
                                        handler.sendEmptyMessage(10);

                                    } else if (ftpclient.Toknow == 31) {
                                        if (fcancel == 1) {
                                            fcancel = 0;
                                        } else {
                                            progressDialog.dismiss();
                                            mBuilder.setContentText(context.getResources().getString(R.string.Download_Failed));
                                            mNotifyManager.notify(0, mBuilder.build());
                                            handler.sendEmptyMessage(10);
                                        }
                                    } else {
                                        mBuilder.setContentText(context.getResources().getString(R.string.Unable_to_Download));
                                        mNotifyManager.notify(0, mBuilder.build());
                                        progressDialog.dismiss();
                                        handler.sendEmptyMessage(-1);
                                    }
                                } else {
                                    progressDialog.setProgress(100);
                                    progressDialog.dismiss();
                                    mBuilder.setContentText(context.getResources().getString(R.string.Download_Completed));
                                    mNotifyManager.notify(0, mBuilder.build());
                                    handler.sendEmptyMessage(3);
                                }
                            }
                        }).start();
                    }
                }).start();
            } else {

                show_error_box(context.getResources().getString(R.string.Internet_Connection_Msg),context.getResources().getString(R.string.Internet_Connection));
            }
        } catch (Exception e) {

            something = context.getResources().getString(R.string.PROGRAMMATICAL_ERROR_IN_DOWNLOAD) + e.toString();
            System.out.println(something);
            Toast.makeText(this, something,
                    Toast.LENGTH_SHORT).show();

        }
    }



    private void install() {

        boolean isNonPlayAppAllowed = false;
        try {
            isNonPlayAppAllowed = Settings.Secure.getInt(getContentResolver(), Settings.Secure.INSTALL_NON_MARKET_APPS) == 1;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        if (!isNonPlayAppAllowed) {
            System.out.println("Unknown Resource file is not allowed to install");
            startActivity(new Intent(android.provider.Settings.ACTION_SECURITY_SETTINGS));
        }

        File file = new File("/sdcard/Download/", FTP_file); // assume refers to "sdcard/myapp_folder/myapp.apk"
        Uri fileUri = Uri.fromFile(file); //for Build.VERSION.SDK_INT <= 2
        if (Build.VERSION.SDK_INT >= 24) {

            fileUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
        }
        System.out.println(fileUri);
        Intent intent = new Intent(Intent.ACTION_VIEW, fileUri);
        intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
        intent.setDataAndType(fileUri, "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //dont forget add this line
        context.startActivity(intent);

    }
}
