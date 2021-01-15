package com.novigosolutions.certiscisco_pcsbr.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.novigosolutions.certiscisco_pcsbr.R;
import com.novigosolutions.certiscisco_pcsbr.interfaces.RecyclerViewClickListener;
import com.novigosolutions.certiscisco_pcsbr.models.Branch;
import com.novigosolutions.certiscisco_pcsbr.models.Break;
import com.novigosolutions.certiscisco_pcsbr.utils.CommonMethods;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class BreakListAdapter extends RecyclerView.Adapter<BreakListAdapter.MyViewHolder> {
    List<Break> breakList;
    String colorLightGreen = "#C8E6C9";
    String colorLightRed = "#FFCDD2";
    String colorWhite = "#FFFFFF";
    RecyclerViewClickListener itemListener;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txt_from_to, txt_duration, txt_remarks;
        View view;
        Button btn_consume;

        public MyViewHolder(View view) {
            super(view);
            txt_from_to = view.findViewById(R.id.txt_from_to);
            txt_duration = view.findViewById(R.id.txt_duration);
            txt_remarks = view.findViewById(R.id.txt_remarks);
            btn_consume = view.findViewById(R.id.btn_consume);
            btn_consume.setOnClickListener(this);
            this.view = view;
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.btn_consume) {
                itemListener.recyclerViewListClicked(breakList.get(this.getLayoutPosition()).BreakId);
            }
        }
    }

    public BreakListAdapter(List<Break> breakList, RecyclerViewClickListener itemListener) {
        this.breakList = breakList;
        this.itemListener = itemListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.break_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.txt_from_to.setText(CommonMethods.getHourMinitue(breakList.get(position).StartTime) + " to " + CommonMethods.getHourMinitue(breakList.get(position).EndTime));
        holder.txt_duration.setText(breakList.get(position).Duration);
        holder.txt_remarks.setText(breakList.get(position).Remarks);
        if (breakList.get(position).completed) {
            holder.view.setBackgroundColor(Color.parseColor(colorLightGreen));
            holder.btn_consume.setVisibility(View.GONE);
        } else if (breakList.get(position).Expired) {
            holder.view.setBackgroundColor(Color.parseColor(colorLightRed));
            holder.btn_consume.setVisibility(View.GONE);
        } else {
            holder.view.setBackgroundColor(Color.parseColor(colorWhite));
            if (breakList.get(position).Consumed) {
                holder.btn_consume.setVisibility(View.GONE);
            } else {
                holder.btn_consume.setVisibility(View.VISIBLE);
            }
        }


    }

    @Override
    public int getItemCount() {
        return breakList.size();
    }
}