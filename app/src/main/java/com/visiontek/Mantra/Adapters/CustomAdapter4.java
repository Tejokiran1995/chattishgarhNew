package com.visiontek.Mantra.Adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.visiontek.Mantra.Activities.DealerDetailsActivity;
import com.visiontek.Mantra.Activities.MemberDetailsActivity;
import com.visiontek.Mantra.Models.DATAModels.DataModel5;
import com.visiontek.Mantra.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class CustomAdapter4 extends RecyclerView.Adapter<CustomAdapter4.MyViewHolder> {

    int type;
    Context context;
    int type1;
    private final ArrayList<DataModel5> dataSet;
    private MemberDetailsActivity.OnClickListener onClickListener;
    private final DealerDetailsActivity.OnClickListener onClick;

    public CustomAdapter4(Context context, ArrayList<DataModel5> data, DealerDetailsActivity.OnClickListener onClickListener, int type1) {
        this.dataSet = data;
        this.context = context;
        this.type1 = type1;
        this.onClick = onClickListener;
    }

    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item3_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {
        final DataModel5 model = dataSet.get(listPosition);
        TextView textViewName = holder.textName;
        TextView textViewUid = holder.textUid;
        TextView textViewStatus = holder.textStatus;
        LinearLayout lin = holder.linearLayout;

        textViewName.setText(dataSet.get(listPosition).getName());
        textViewUid.setText(dataSet.get(listPosition).getUid());
        System.out.println("DONEEEEEEEEEEEEEEEE");
        textViewStatus.setText(dataSet.get(listPosition).getStatus());

        if (model.isSelected) {
            lin.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));

        } else {
            lin.setBackgroundColor(context.getResources().getColor(R.color.background));

        }

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (int i = 0; i < dataSet.size(); i++) {

                    dataSet.get(i).isSelected = false;
                }

                model.isSelected = true;
                if (type == 1) {
                    onClickListener.onClick_d(listPosition);
                }
                if (type1 == 1) {
                    onClick.onClick_d(listPosition);
                }
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textName;
        TextView textUid;
        TextView textStatus;
        LinearLayout linearLayout;

        MyViewHolder(View itemView) {
            super(itemView);
            this.linearLayout = itemView.findViewById(R.id.linear);
            this.textName = itemView.findViewById(R.id.txtName);
            this.textUid = itemView.findViewById(R.id.txtUid);
            this.textStatus = itemView.findViewById(R.id.txtStatus);

        }
    }
}










  /* if (type1==1) {
            lin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (type == 0) {
                        dclick = true;
                        DName = dname.get(listPosition);
                        DUid = duid.get(listPosition);
                        Dtype = dtype.get(listPosition);
                        DAtype = dauthtype.get(listPosition);
                        switch (DAtype) {
                            case "I":
                                dIRIS = true;
                                dfCount = "0";
                                diCount = "1";
                                toast(context, "IRIS login");
                                break;
                            case "F":
                                dfCount = "1";
                                diCount = "0";
                                toast(context, "Biometric  login");
                                break;
                            case "P":
                                toast(context, "Password login");
                                break;
                        }
                        System.out.println("***DealerAUTH*** =" + DAtype);
                        System.out.println("***DealerName*** =" + DName);
                        System.out.println("***DealerUID*** =" + DUid);
                        System.out.println("***DealerUID*** =" + Dtype);

                    } else {
                        mclick = true;
                        MName = mname.get(listPosition);
                        MUid = muid.get(listPosition);
                        Mmemid = mmemid.get(listPosition);
                        Mfinger = mfinger.get(listPosition);
                        Miris = miris.get(listPosition);
                        if (Mfinger.equals("Y")) {
                            mBIO = true;
                        *//*scanfp.setEnabled(true);
                        scanfp.setVisibility(View.VISIBLE);*//*
                        } else {
                        *//*scanfp.setEnabled(false);
                        scanfp.setVisibility(View.INVISIBLE);*//*
                            toast(context, "Iris for Authentication");
                        }
                        if (Miris.equals("Y")) {
                            mIRIS = true;
                        *//*Iris.setEnabled(true);
                        Iris.setVisibility(View.VISIBLE);*//*
                        } else {
                        *//*Iris.setEnabled(false);
                        Iris.setVisibility(View.INVISIBLE);*//*
                            toast(context, "ScanFP for Authentication");
                        }
                        System.out.println("***Member Name*** =" + MName);
                        System.out.println("***Member UID*** =" + MUid);
                        System.out.println("***Memeber ID*** =" + Mmemid);
                        System.out.println("***Memeber FP*** =" + Mfinger);
                        System.out.println("***Memeber IRIS*** =" + Miris);
                    }
                    for (int i = 0; i < dataSet.size(); i++) {
                        System.out.println("++++++++++++++++++++3");
                        dataSet.get(i).isSelected = false;
                    }
                    System.out.println("++++++++++++++++++++4");
                    model.isSelected = true;
                    notifyDataSetChanged();

                }
            });
        }*/