package com.visiontek.Mantra.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.visiontek.Mantra.Models.DATAModels.DataModel2;
import com.visiontek.Mantra.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CustomAdapter2 extends RecyclerView.Adapter<CustomAdapter2.MyViewHolder> {
    private final List<DataModel2> dataSet;

    public CustomAdapter2(Context context, List<DataModel2> data) {
        this.dataSet = data;

    }

    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item4_row, parent, false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        DataModel2 data2 = dataSet.get(position);
        holder.A.setText(data2.getTot());
        holder.B.setText(data2.getBal());
        holder.C.setText(data2.getReq());
        holder.D.setText(data2.getRate());
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView A;
        TextView B;
        TextView C;
        TextView D;

        MyViewHolder(View itemView) {
            super(itemView);
            this.A = itemView.findViewById(R.id.total);
            this.B = itemView.findViewById(R.id.bal);
            this.C = itemView.findViewById(R.id.rate);
            this.D = itemView.findViewById(R.id.close);

        }
    }
}
