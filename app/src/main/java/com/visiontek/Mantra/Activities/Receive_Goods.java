package com.visiontek.Mantra.Activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;

import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.visiontek.Mantra.Adapters.CustomAdapter1;
import com.visiontek.Mantra.Models.DataModel1;
import com.visiontek.Mantra.Models.fpsCommonInfo;
import com.visiontek.Mantra.Models.fpsURLInfo;
import com.visiontek.Mantra.Models.stateBean;
import com.visiontek.Mantra.R;
import com.visiontek.Mantra.Utils.DatabaseHelper;
import com.visiontek.Mantra.Utils.Util;

import java.util.ArrayList;
import java.util.Locale;

import static com.visiontek.Mantra.Activities.Start.L;
import static com.visiontek.Mantra.Activities.Start.mp;
import static com.visiontek.Mantra.Utils.Util.ConsentForm;
import static com.visiontek.Mantra.Utils.Util.RDservice;
import static com.visiontek.Mantra.Utils.Util.releaseMediaPlayer;
import static com.visiontek.Mantra.Utils.Util.toast;


public class Receive_Goods extends AppCompatActivity {


    static String  length, fps, month, year, chit, cid, orderno, truckno;
    static ArrayList<String> e_name = new ArrayList<>();
    static ArrayList<String> e_sname = new ArrayList<>();
    static ArrayList<String> e_allot = new ArrayList<>();
    static ArrayList<String> e_dispatch = new ArrayList<>();
    static ArrayList<String> e_code = new ArrayList<>();
    static ArrayList<String> e_sid = new ArrayList<>();
    static ArrayList<String> enter = new ArrayList<>();
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
    ProgressDialog pd;
    DatabaseHelper databaseHelper;
    Button back, scapfp;

    Context context;
    Spinner options;

    RecyclerView.Adapter adapter;
    RecyclerView recyclerView;

    ArrayList<DataModel1> modeldata;
    int select;
    TextView trucknumber;
    String YouEditTextValue;
    String AFTERDATA;
    Float textdata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive__goods);

        context = Receive_Goods.this;
        TextView rd = findViewById(R.id.rd);
        boolean  rd_fps;
        rd_fps = RDservice(context);
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.enable();
        if (rd_fps) {
            rd.setTextColor(context.getResources().getColor(R.color.green));
        } else {
            show_error_box(context.getResources().getString(R.string.RD_Service_Msg),context.getResources().getString(R.string.RD_Service),0);
            rd.setTextColor(context.getResources().getColor(R.color.black));
        }
        pd = new ProgressDialog(context);
        databaseHelper = new DatabaseHelper(context);

        trucknumber = findViewById(R.id.tv_truckno);
        scapfp = findViewById(R.id.rc_confirm);
        back = findViewById(R.id.rc_back);

        Truck_fps = databaseHelper.get_RC_Truck(0);
        Truck_allotmonth = databaseHelper.get_RC_Truck(1);
        Truck_allotyear = databaseHelper.get_RC_Truck(2);
        Truck_truckChit = databaseHelper.get_RC_Truck(3);
        Truck_challanId = databaseHelper.get_RC_Truck(4);
        Truck_allocationOrderNo = databaseHelper.get_RC_Truck(5);
        Truck_truckNo = databaseHelper.get_RC_Truck(6);
        CommLength = databaseHelper.get_RC_Truck(7);


        Truck_rel_Quantity = databaseHelper.get_RC_Comm(0);
        Truck_Allotment = databaseHelper.get_RC_Comm(1);
        Truck_Code = databaseHelper.get_RC_Comm(2);
        Truck_Name = databaseHelper.get_RC_Comm(3);
        Truck_SId = databaseHelper.get_RC_Comm(4);
        Truck_SName = databaseHelper.get_RC_Comm(5);


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        recyclerView = findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        modeldata = new ArrayList<>();
        options = findViewById(R.id.truckchit);

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, Truck_truckChit);
        options.setAdapter(adapter1);
        options.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapter, View v, int position, long id) {
                select = position;
                System.out.println("SELETED=" + position);
                enter.clear();
                e_name.clear();
                e_sname.clear();
                e_allot.clear();
                e_dispatch.clear();
                e_code.clear();
                e_sid.clear();


                length = CommLength.get(position);
                fps = Truck_fps.get(position);
                month = Truck_allotmonth.get(position);
                year = Truck_allotyear.get(position);
                chit = Truck_truckChit.get(position);
                cid = Truck_challanId.get(position);
                orderno = Truck_allocationOrderNo.get(position);
                truckno = Truck_truckNo.get(position);
                trucknumber.setText(context.getResources().getString(R.string.Truck_No) + truckno);
                DisplayTruck(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        scapfp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (check()) {
                    if (Util.networkConnected(context)) {
                        Intent i = new Intent(Receive_Goods.this, DealerAuthentication.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                    } else {
                        show_error_box(context.getResources().getString(R.string.Please_check_Consent_Form), context.getResources().getString(R.string.Consent_Form), 0);
                    }
                } else {
                    if (mp!=null) {
                        releaseMediaPlayer(context,mp);
                    }  if (L.equals("hi")) {
                    } else {
                   mp=mp.create(context, R.raw.c100051);
                           mp.start();
                    show_error_box( context.getResources().getString(R.string.Please_Select_Dealer_Name),  context.getResources().getString(R.string.Dealer), 0);
                }}
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
    }

    private boolean check() {
        for (int i = 0; i < enter.size(); i++) {
            if (!enter.get(i).equals("0.0")) {
                return true;
            }
        }
        return false;
    }

    private void DisplayTruck(int position) {
        int j, i;
        for (i = 0; i < position; i++) {
            i = Integer.parseInt(i + CommLength.get(i));
        }

        int len = Integer.parseInt(CommLength.get(position));
        for (j = i; j < len; j++) {


            enter.add("0.0");
            e_name.add(Truck_Name.get(j));
            e_sname.add(Truck_SName.get(j));
            e_allot.add(Truck_Allotment.get(j));
            e_dispatch.add(Truck_rel_Quantity.get(j));
            e_code.add(Truck_Code.get(j));
            e_sid.add(Truck_SId.get(j));
        }

        for (int k = 0; k < enter.size(); k++) {
            modeldata.add(new DataModel1(Truck_Name.get(k), Truck_SName.get(k), Truck_Allotment.get(k), Truck_rel_Quantity.get(k), enter.get(k)));
        }

        Display();
    }

    private void Display() {
        adapter = new CustomAdapter1(context, modeldata, new OnClickListener() {
            @Override
            public void onClick_d(int p) {
                EnterComm(p);
            }
        }, 1);
        recyclerView.setAdapter(adapter);
    }

    private void EnterComm(final int p) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        final EditText edittext = new EditText(context);
        alert.setMessage(context.getResources().getString(R.string.Please_Enter_the_required_quantity));
        alert.setTitle(context.getResources().getString(R.string.Enter_Quantity));
        edittext.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(12);
        edittext.setFilters(FilterArray);
        alert.setView(edittext);
        alert.setPositiveButton(context.getResources().getString(R.string.Ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                YouEditTextValue = edittext.getText().toString();

                if (!YouEditTextValue.isEmpty()) {
                    textdata = Float.parseFloat(YouEditTextValue);
                    AFTERDATA = String.valueOf(textdata);
                    enter.set(p, AFTERDATA);

                    modeldata.clear();

                    for (int k = 0; k < enter.size(); k++) {
                        modeldata.add(new DataModel1(Truck_Name.get(k), Truck_SName.get(k), Truck_Allotment.get(k), Truck_rel_Quantity.get(k), enter.get(k)));
                    }
                    Display();

                } else {
                    show_error_box(context.getResources().getString(R.string.Please_enter_a_valid_Value), context.getResources().getString(R.string.Invalid_Quantity), 0);
                }
            }
        });
        alert.setNegativeButton(context.getResources().getString(R.string.Cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        alert.show();
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
                           //    print();
                        }
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }



    public interface OnClickListener {
        void onClick_d(int p);
    }
}
