package com.novigosolutions.certiscisco_pcsbr.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.novigosolutions.certiscisco_pcsbr.R;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class CoinAdapter extends RecyclerView.Adapter<CoinAdapter.MyViewHolder> {
    List<String> currancyNames;
    List<Integer> counts;
    List<String> coinseries;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txt_head, txt_count,txt_series;

        public MyViewHolder(View view) {
            super(view);
            txt_head = view.findViewById(R.id.txt_head);
            txt_count = view.findViewById(R.id.txt_count);
            txt_series=view.findViewById(R.id.txt_series);
        }
    }

    public CoinAdapter(List<String> currancyNames, List<Integer> counts,List<String> coinseries) {
        this.currancyNames = currancyNames;
        this.counts = counts;
        this.coinseries=coinseries;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.currency_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.txt_head.setText(currancyNames.get(position));
        holder.txt_count.setText(String.valueOf(counts.get(position)));
        if(coinseries.get(position).equals("null")){
            holder.txt_series.setText("");
        }else{
            holder.txt_series.setText(coinseries.get(position));}
    }

    @Override
    public int getItemCount() {
        return currancyNames.size();
    }
}