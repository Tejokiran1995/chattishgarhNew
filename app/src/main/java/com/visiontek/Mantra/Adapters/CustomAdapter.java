package com.visiontek.Mantra.Adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.visiontek.Mantra.Activities.DealerAuthentication;
import com.visiontek.Mantra.Activities.Dealer_Details;
import com.visiontek.Mantra.Activities.Device_Info;
import com.visiontek.Mantra.Activities.Member_Details;
import com.visiontek.Mantra.Models.DataModel;
import com.visiontek.Mantra.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    int type;
    int type2;
    Context context;
    int type1;
    private final ArrayList<DataModel> dataSet;
    private Member_Details.OnClickListener onClickListener;
    private Dealer_Details.OnClickListener onClick;
    private DealerAuthentication.OnClickListener Click;

    public CustomAdapter(Context context, ArrayList<DataModel> data, Member_Details.OnClickListener onClickListener, int type) {
        this.dataSet = data;
        this.context = context;
        this.type = type;
        this.onClickListener = onClickListener;
    }

    public CustomAdapter(Context context, ArrayList<DataModel> data, Dealer_Details.OnClickListener onClickListener, int type1) {
        this.dataSet = data;
        this.context = context;
        this.type1 = type1;
        this.onClick = onClickListener;
    }

    public CustomAdapter(Context context, ArrayList<DataModel> arraydata, Device_Info.OnClickListener onClickListener) {
        this.dataSet = arraydata;
        this.context = context;
    }

    public CustomAdapter(Context context, ArrayList<DataModel> data, DealerAuthentication.OnClickListener onClickListener, int type2) {

        this.dataSet = data;
        this.context = context;
        this.Click = onClickListener;
        this.type2 = type2;
    }

    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item2_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {
        final DataModel model = dataSet.get(listPosition);
        TextView textViewName = holder.textName;
        TextView textViewVersion = holder.textType;
        LinearLayout lin = holder.linearLayout;

        textViewName.setText(dataSet.get(listPosition).getName());
        textViewVersion.setText(dataSet.get(listPosition).getType());

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
                if (type2 == 1) {
                    Click.onClick_d(listPosition);
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
        TextView textType;
        LinearLayout linearLayout;

        MyViewHolder(View itemView) {
            super(itemView);
            this.linearLayout = itemView.findViewById(R.id.linear);
            this.textName = itemView.findViewById(R.id.txtName);
            this.textType = itemView.findViewById(R.id.txtType);

        }
    }
}



