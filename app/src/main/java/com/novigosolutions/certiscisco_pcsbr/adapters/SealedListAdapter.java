package com.novigosolutions.certiscisco_pcsbr.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.novigosolutions.certiscisco_pcsbr.R;
import com.novigosolutions.certiscisco_pcsbr.models.Delivery;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class SealedListAdapter extends RecyclerView.Adapter<SealedListAdapter.MyViewHolder> {
    List<Delivery> deliveries;
    String colorGreen = "#43A047";
    String colorWhite = "#FFFFFF";

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txt_seal;
        LinearLayout llmain;

        public MyViewHolder(View view) {
            super(view);
            txt_seal = view.findViewById(R.id.txt_seal);
            llmain = view.findViewById(R.id.ll_main);
        }
    }

    public SealedListAdapter(List<Delivery> deliveries) {
        this.deliveries = deliveries;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bag_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if (deliveries.get(position).ItemType.equals("Coin Box") || deliveries.get(position).ItemType.equals("BOX")) {
            if (deliveries.get(position).CoinSeriesId == 0)
                holder.txt_seal.setText(deliveries.get(position).ItemType + "(" + deliveries.get(position).SealNo + ")");
            else
                holder.txt_seal.setText(deliveries.get(position).ItemType + "(" + deliveries.get(position).SealNo + ")(" + deliveries.get(position).CoinSeries + ")");
        } else {
            if (deliveries.get(position).ItemType.equals("BAG")) {
                holder.txt_seal.setText("Sealed " + deliveries.get(position).ItemType + "(" + deliveries.get(position).SealNo + ")");
            } else
                holder.txt_seal.setText(deliveries.get(position).ItemType + "(" + deliveries.get(position).SealNo + ")");
        }

        if (deliveries.get(position).IsScanned) {
            holder.llmain.setBackgroundColor(Color.parseColor(colorGreen));
        } else {
            holder.llmain.setBackgroundColor(Color.parseColor(colorWhite));
        }
    }

    @Override
    public int getItemCount() {
        return deliveries.size();
    }
}