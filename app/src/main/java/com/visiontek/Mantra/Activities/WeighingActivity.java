
package com.visiontek.Mantra.Activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.visiontek.Mantra.Models.fpsCommonInfo;
import com.visiontek.Mantra.R;
import com.visiontek.Mantra.Utils.UsbService;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import static com.visiontek.Mantra.Activities.DeviceListActivity.address;
import static com.visiontek.Mantra.Activities.Ration_details.COMMPOSITION;
import static com.visiontek.Mantra.Activities.Ration_details.COMMQNTY;
import static com.visiontek.Mantra.Activities.Ration_details.Camount;
import static com.visiontek.Mantra.Activities.Ration_details.Cavlil;
import static com.visiontek.Mantra.Activities.Ration_details.Cbal;
import static com.visiontek.Mantra.Activities.Ration_details.Ccode;
import static com.visiontek.Mantra.Activities.Ration_details.Closebal;
import static com.visiontek.Mantra.Activities.Ration_details.Cmin;
import static com.visiontek.Mantra.Activities.Ration_details.Cmonth;
import static com.visiontek.Mantra.Activities.Ration_details.Cname;
import static com.visiontek.Mantra.Activities.Ration_details.Cprice;
import static com.visiontek.Mantra.Activities.Ration_details.Ctotal;
import static com.visiontek.Mantra.Activities.Ration_details.Ctype;
import static com.visiontek.Mantra.Activities.Ration_details.Cyear;
import static com.visiontek.Mantra.Activities.Ration_details.POSITION;
import static com.visiontek.Mantra.Activities.Ration_details.avlil;
import static com.visiontek.Mantra.Activities.Ration_details.bal;
import static com.visiontek.Mantra.Activities.Ration_details.close;
import static com.visiontek.Mantra.Activities.Ration_details.code;
import static com.visiontek.Mantra.Activities.Ration_details.min;
import static com.visiontek.Mantra.Activities.Ration_details.month;
import static com.visiontek.Mantra.Activities.Ration_details.name;
import static com.visiontek.Mantra.Activities.Ration_details.price;
import static com.visiontek.Mantra.Activities.Ration_details.total;
import static com.visiontek.Mantra.Activities.Ration_details.type;
import static com.visiontek.Mantra.Activities.Ration_details.wei;
import static com.visiontek.Mantra.Activities.Ration_details.year;
import static com.visiontek.Mantra.Utils.Util.RDservice;
import static com.visiontek.Mantra.Utils.Util.toast;

public class WeighingActivity extends AppCompatActivity {
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static int MESSAGE_FROM_SERIAL_PORT = 0;
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (Objects.requireNonNull(intent.getAction())) {
                case UsbService.ACTION_USB_PERMISSION_GRANTED: // USB PERMISSION GRANTED
                    Toast.makeText(context, context.getResources().getString(R.string.USB_Ready), Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_PERMISSION_NOT_GRANTED: // USB PERMISSION NOT GRANTED
                    Toast.makeText(context, context.getResources().getString(R.string.USB_Permission_not_granted), Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_NO_USB: // NO USB CONNECTED
                    Toast.makeText(context, context.getResources().getString(R.string.No_USB_connected), Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_DISCONNECTED: // USB DISCONNECTED
                    Toast.makeText(context, context.getResources().getString(R.string.USB_disconnected), Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_NOT_SUPPORTED: // USB NOT SUPPORTED
                    Toast.makeText(context, context.getResources().getString(R.string.USB_device_not_supported), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    Spinner options;
    int choice;
    Button get, confirm, back;
    TextView weighingstatus, weighing;
    String bt = null, usb = null;
    StringBuilder storeUSBdata = new StringBuilder();
    StringBuilder storeBTdata = new StringBuilder();
    Handler bluetoothIn;
    Context context;
    ProgressDialog pd = null;
    String com_weight;
    float Camount1;
    float cmin, cprice, closebal,cbal,ctotal;
    String cname, ccode, ctype, cmonth, cyear,cavail;
    private BluetoothSocket btSocket = null;
    private BluetoothAdapter mBluetoothAdapter;
    private UsbService usbService;
    private MyHandler mHandler;

    private final ServiceConnection usbConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            usbService = ((UsbService.UsbBinder) arg1).getService();
            usbService.setHandler(mHandler);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            usbService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weighing);
        options = findViewById(R.id.options);
        confirm = findViewById(R.id.weighing_confirm);
        back = findViewById(R.id.weighing_back);
        get = findViewById(R.id.weighing_get);
        weighingstatus = findViewById(R.id.weighing_status);
        weighing = findViewById(R.id.weighing);


        context = WeighingActivity.this;

        TextView rd = findViewById(R.id.rd);
        boolean  rd_fps;
        rd_fps = RDservice(context);
       mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.enable();
        if (rd_fps) {
            rd.setTextColor(context.getResources().getColor(R.color.green));
        } else {
            show_error_box(context.getResources().getString(R.string.RD_Service_Msg),context.getResources().getString(R.string.RD_Service));

            rd.setTextColor(context.getResources().getColor(R.color.black));
        }


        mHandler = new MyHandler(this);
        pd = new ProgressDialog(context);
        MESSAGE_FROM_SERIAL_PORT = 0;

        get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*if (wei.get(POSITION).equals("Y")) {
                    MESSAGE_FROM_SERIAL_PORT = 1;
                    weighingstatus.setTextColor(context.getResources().getColor(R.color.button));
                    weighingstatus.setText(context.getResources().getString(R.string.Please_Enter_the_required_Weight));
                } else {*/
                    MESSAGE_FROM_SERIAL_PORT = 0;
                    weighingstatus.setTextColor(context.getResources().getColor(R.color.button));
                    weighingstatus.setText(context.getResources().getString(R.string.Please_get_Weight_from_Weighing_Machine));
                    if (choice == 2) {
                        setFilters();
                        startService(UsbService.class, usbConnection, null);
                    } else if (choice == 1) {
                        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                        if (mBluetoothAdapter.isEnabled()) {
                            if (address == null) {
                                BTList();
                            } else {
                                checkBTState();
                            }
                        } else {
                            show_error_box(context.getResources().getString(R.string.Enable_Bluetooth_and_pair_your_Device_Manually), context.getResources().getString(R.string.Bluetooth));
                        }
                    } else {
                        toast(context, context.getResources().getString(R.string.Please_Select_USB_or_Bluetooth));
                    }
               // }
            }

        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                com_weight = weighing.getText().toString();
                if (com_weight != null && !com_weight.isEmpty() && com_weight.length() > 0 && !com_weight.equals("0") && !com_weight.equals("null")) {
                    float vqnty = verify_WEI(com_weight, 1);
                    if (vqnty != 0) {
                        System.out.println("------------------------Vqunty-" + vqnty);
                        int verify = cal(vqnty, POSITION);
                        if (verify == 1) {
                            MESSAGE_FROM_SERIAL_PORT = 1;
                            finish();
                        } else if (verify == -1) {
                            show_error_box(context.getResources().getString(R.string.Please_give_Quantity_between_Min_bal_and_Close_bal), context.getResources().getString(R.string.Not_a_Valid_Weight));
                        }  else if (verify == -3) {
                            show_error_box(context.getResources().getString(R.string.Please_give_Quantity_between_Min_bal_and_Close_bal), context.getResources().getString(R.string.Not_a_Valid_Weight));
                        } else {
                            show_error_box(context.getResources().getString(R.string.Please_enter_a_valid_Value), context.getResources().getString(R.string.Not_a_Valid_Weight));
                        }
                    } else {
                        show_error_box(context.getResources().getString(R.string.Please_Enter_the_Weight), context.getResources().getString(R.string.Enter_valid_weight));
                    }
                } else {
                    show_error_box(context.getResources().getString(R.string.Please_Enter_the_Weight), context.getResources().getString(R.string.Enter_valid_weight));
                }
            }
        });
        String[] items = new String[]{"Select", "Bluetooth", "USB"};
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, items);
        options.setAdapter(adapter1);
        options.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapter, View v, int position, long id) {
                choice = position;
                System.out.println("SELETED=" + position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

    }

    private void BTList() {

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices == null || pairedDevices.size() == 0) {
            show_error_box(context.getResources().getString(R.string.No_Paired_Devices), context.getResources().getString(R.string.Bluetooth));
        } else {
            ArrayList<BluetoothDevice> list = new ArrayList<BluetoothDevice>(pairedDevices);
            Intent intent = new Intent(WeighingActivity.this, DeviceListActivity.class);
            intent.putParcelableArrayListExtra("device.list", list);
            startActivityForResult(intent, 1);
        }
    }

    private void setFilters() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbService.ACTION_USB_PERMISSION_GRANTED);
        filter.addAction(UsbService.ACTION_NO_USB);
        filter.addAction(UsbService.ACTION_USB_DISCONNECTED);
        filter.addAction(UsbService.ACTION_USB_NOT_SUPPORTED);
        filter.addAction(UsbService.ACTION_USB_PERMISSION_NOT_GRANTED);
        registerReceiver(mUsbReceiver, filter);
    }

    private void startService(Class<?> service, ServiceConnection serviceConnection, Bundle
            extras) {
        if (!UsbService.SERVICE_CONNECTED) {
            Intent startService = new Intent(this, service);
            if (extras != null && !extras.isEmpty()) {
                Set<String> keys = extras.keySet();
                for (String key : keys) {
                    String extra = extras.getString(key);
                    startService.putExtra(key, extra);
                }
            }
            startService(startService);
        }
        Intent bindingIntent = new Intent(this, service);
        bindService(bindingIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }
    fpsCommonInfo fpsCommonInfo=new fpsCommonInfo();
    private float verify_WEI(String com, int check) {
        String w8;
        float vmin, vw8, vmod, pl_mi;
        String m = "0.0" + fpsCommonInfo.getweighAccuracyValueInGms();
        System.out.println(fpsCommonInfo.getweighAccuracyValueInGms());
        pl_mi = Float.parseFloat(m);
        vmin = Float.parseFloat((min.get(0)));
        if (check == 1) {
            if (com.length() > 10) {
                w8 = com.substring(1, 8);
                vw8 = Float.parseFloat(w8);
                vmod = vw8 % vmin;
                if (vmod == (float) 0) {
                    return vw8;
                }
                float ky = (vmod - pl_mi);
                float kx = (vmod + pl_mi);
                System.out.println("ky=" + ky + " kx=" + kx + " pl_mi=" + pl_mi);
                if (kx >= vmin) {
                    vw8 = vw8 - vmod;
                    vw8 = vw8 + vmin;
                    return vw8;
                }
                if (ky <= (float) 0) {
                    vw8 = vw8 - vmod;
                    return vw8;
                }
            } else {
                show_error_box(context.getResources().getString(R.string.Not_a_Valid_Weight) + com, context.getResources().getString(R.string.Please_try_again));
            }
        } else {
            vw8 = Float.parseFloat((com));
            vmod = vw8 % vmin;
            if (vmod == (float) 0) {
                return vw8;
            }
        }
        System.out.println("Failure Weight");
        return -1;
    }

    private int cal(float vqnty, int place) {
        cmin = Float.parseFloat((min.get(place)));
        cprice = Float.parseFloat((price.get(place)));
        closebal = Float.parseFloat((close.get(place)));
        cbal = Float.parseFloat(bal.get(place));
        ctotal = Float.parseFloat(total.get(place));
        cavail = avlil.get(place);
        cname = name.get(place);
        ccode = code.get(place);
        ctype = type.get(place);
        cmonth = month.get(place);
        cyear = year.get(place);

        if (vqnty != (float) 0) {

            Camount1 = (vqnty * cprice);
            System.out.println("Camount=" + Camount1);
            if (vqnty<=cbal) {
                if (vqnty >= cmin && vqnty <= closebal) {
                    System.out.println("PLACE=" + place);
                    System.out.println("CMIN =" + Cmin.size());
                    if (COMMPOSITION.size() == 0 || !replace(place, vqnty)) {

                        System.out.println("INSERTING VALUES");
                        Cavlil.add(cavail);
                        Cbal.add(String.valueOf(cbal));
                        Cmin.add(cmin);
                        Cprice.add(cprice);
                        Closebal.add(closebal);
                        Cname.add(cname);
                        Ccode.add(ccode);
                        Ctype.add(ctype);
                        Cmonth.add(cmonth);
                        Cyear.add(cyear);
                        Ctotal.add(String.valueOf(ctotal));
                        COMMQNTY.add(vqnty);
                        COMMPOSITION.add(place);
                        Camount.add(Camount1);
                    }

                    return 1;
                } else {
                    return -1;
                }
            }else {
                return -3;
            }
        }
        return -2;
    }

    private boolean replace(int place, float vqnty) {
        for (int i = 0; i <= COMMPOSITION.size(); i++) {
            if (place == COMMPOSITION.get(i)) {
                System.out.println("REPLACING VALUES");
                Cavlil.set(i,cavail);
                Cbal.set(i, String.valueOf(cbal));
                Cmin.set(i, cmin);
                Cprice.set(i, cprice);
                Closebal.set(i, closebal);
                Cname.set(i, cname);
                Ccode.set(i, ccode);
                Ctype.set(i, ctype);
                Cmonth.set(i, cmonth);
                Cyear.set(i, cyear);
                Ctotal.set(i, String.valueOf(ctotal));
                COMMQNTY.set(i, vqnty);
                Camount.set(i, Camount1);
                return true;
            }
        }
        return false;
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint("HandlerLeak")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (data == null) {
                System.out.println("NULLLLLLLLLLLLLLLLL");
                return;
            }

            checkBTState();
        }
    }

    private void checkBTState() {
        handler();
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        /*String address1 = (String) Objects.requireNonNull(data.getExtras()).get("device_name");*/
        BluetoothDevice device = btAdapter.getRemoteDevice(address);
        try {
            System.out.println("*********************************9");
            btSocket = createBluetoothSocket(device);
            System.out.println("*********************************8");
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), context.getResources().getString(R.string.Socket_creation_failed), Toast.LENGTH_LONG).show();
            finish();
        }
        try {
            btSocket.connect();

        } catch (IOException e) {
            String msg;
            msg = String.valueOf(e);
            try {
                btSocket.close();
            } catch (IOException ignored) {
                msg = String.valueOf(ignored);
            }
            Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
            if (pd.isShowing()) {
                pd.dismiss();
            }
            weighing.setText(context.getResources().getString(R.string.Please_Wait));
            pd = ProgressDialog.show(context, context.getResources().getString(R.string.Connecting), context.getResources().getString(R.string.Please_Wait), true, false);
            checkBTState();
            return;
            //show_error_box(msg, "Socket Exception Please try again");
        }

        ConnectedThread mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();
    }

    private void handler() {
        bluetoothIn = new Handler() {
            @SuppressLint("HandlerLeak")
            public void handleMessage(android.os.Message msg) {
                if (msg.what == MESSAGE_FROM_SERIAL_PORT) {
                    String data = (String) msg.obj;
                    storeBTdata.append(data);
                    if (storeBTdata.toString().contains("g")) {
                        if (pd.isShowing()) {
                            pd.dismiss();
                        }
                        bt = String.valueOf(storeBTdata).trim();
                        storeBTdata.setLength(0);
                        System.out.println("BLUETOOTH====" + bt);
                        weighing.setText(bt);
                    }
                }
            }
        };
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
                        if (pd.isShowing()) {
                            pd.dismiss();
                        }
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @SuppressLint("HandlerLeak")
    private class MyHandler extends Handler {
        private final WeakReference<WeighingActivity> mActivity;

        MyHandler(WeighingActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MESSAGE_FROM_SERIAL_PORT) {
                String data = (String) msg.obj;
                storeUSBdata.append(data);
                if (storeUSBdata.toString().contains("g")) {
                    if (pd.isShowing()) {
                        pd.dismiss();
                    }
                    usb = String.valueOf(storeUSBdata).trim();
                    storeUSBdata.setLength(0);
                    System.out.println("USB====" + usb);
                    mActivity.get().weighing.setText(usb);
                }
            }
        }
    }

    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;

        ConnectedThread(BluetoothSocket socket) {


            InputStream tmpIn = null;
            try {
                tmpIn = socket.getInputStream();
            } catch (IOException ignored) {
            }
            mmInStream = tmpIn;
        }

        public void run() {
            byte[] buffer = new byte[2048];
            int bytes;
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);
                    String readMessage = new String(buffer, 0, bytes);
                    if (MESSAGE_FROM_SERIAL_PORT == 0) {
                        bluetoothIn.obtainMessage(WeighingActivity.MESSAGE_FROM_SERIAL_PORT, bytes, -1, readMessage).sendToTarget();
                    } else {
                        break;
                    }
                } catch (IOException e) {
                    break;
                }
            }
        }
    }


}