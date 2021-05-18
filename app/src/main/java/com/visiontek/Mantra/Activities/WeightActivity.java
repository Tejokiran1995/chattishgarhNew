package com.visiontek.Mantra.Activities;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.visiontek.Mantra.Models.fpsCommonInfo;
import com.visiontek.Mantra.R;

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
import static com.visiontek.Mantra.Activities.Ration_details.year;
import static com.visiontek.Mantra.Utils.Util.RDservice;
import static com.visiontek.Mantra.Utils.Util.toast;

public class WeightActivity extends AppCompatActivity {

    Button confirm, back;
    EditText weight;
    TextView weightstatus;
    String com_weight;


    Context context;
    float Camount1;
    float cmin, cprice, closebal,cbal,ctotal;
    String cname, ccode, ctype, cmonth, cyear,cavail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight);

        context = WeightActivity.this;
        confirm = findViewById(R.id.weight_confirm);
        back = findViewById(R.id.weight_back);
        weight = findViewById(R.id.weights);
        weightstatus = findViewById(R.id.weight_status);
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


        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                com_weight = weight.getText().toString();
                if (com_weight != null && !com_weight.isEmpty() && com_weight.length() > 0 && !com_weight.equals("null")) {

                    float vqnty = Float.parseFloat(com_weight);
                    /*float vqnty = verify_WEI(com_weight, 0);
                    if (vqnty != 0) {
                        System.out.println("------------------------Vqunty-" + vqnty);
                        int verify = cal(vqnty, POSITION);
                        if (verify == 1) {
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
                        System.out.println("----Failure Weight--------------------Vqunty-" + vqnty);
                    }*/
                    if (COMMPOSITION.size() == 0 || !replace(POSITION, vqnty)) {
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
                        COMMPOSITION.add(POSITION);
                        Camount.add(Camount1);
                    }
                    finish();
                } else {
                    show_error_box(context.getResources().getString(R.string.Please_Enter_the_Weight), context.getResources().getString(R.string.Enter_valid_weight));
                    System.out.println("----Failure Weight--------------------NO Weight");
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               finish();
            }
        });


    }
    fpsCommonInfo fpsCommonInfo=new fpsCommonInfo();
    private float verify_WEI(String com, int check) {
        String w8;
        float vmin, vw8, vmod, pl_mi;
        String m = "0.0" + fpsCommonInfo.getweighAccuracyValueInGms();
        System.out.println(fpsCommonInfo.getweighAccuracyValueInGms());
        pl_mi = Float.parseFloat(m);
        vmin = Float.parseFloat((min.get(POSITION)));
        if (check == 1) {
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
        cbal= Float.parseFloat(bal.get(place));
        ctotal= Float.parseFloat(total.get(place));
        cavail = avlil.get(place);
        cname = name.get(place);
        ccode = code.get(place);
        ctype = type.get(place);
        cmonth = month.get(place);
        cyear = year.get(place);

        if (vqnty != (float) 0) {

            Camount1 = (vqnty * cprice);
            System.out.println("Camount=" + Camount1);
            if (vqnty <= cbal) {
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
        for (int i = 0; i < COMMPOSITION.size(); i++) {
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

