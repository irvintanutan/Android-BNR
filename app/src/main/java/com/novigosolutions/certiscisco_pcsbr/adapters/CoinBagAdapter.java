package com.novigosolutions.certiscisco_pcsbr.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.novigosolutions.certiscisco_pcsbr.R;
import com.novigosolutions.certiscisco_pcsbr.interfaces.RecyclerViewClickListener;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class CoinBagAdapter extends RecyclerView.Adapter<CoinBagAdapter.MyViewHolder> {
    List<String> values;
    List<String> denominationList;
    List<String> coinSeriesList;
    RecyclerViewClickListener itemListener;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txt_string;
        TextView txt_denomination,txt_coinSeries;
        ImageView img_delete;

        public MyViewHolder(View view) {
            super(view);
            txt_string = view.findViewById(R.id.txt_string);
            img_delete=view.findViewById(R.id.img_delete);
            img_delete.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemListener.recyclerViewListClicked(this.getLayoutPosition());
        }
    }

    public CoinBagAdapter(List<String> values, List<String> denominationList, List<String> coinSeriesList, RecyclerViewClickListener itemListener) {
        this.values = values;
        this.itemListener = itemListener;
        this.coinSeriesList = coinSeriesList;
        this.denominationList = denominationList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.coin_bag_list, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        String denomination = denominationList.get(position).replaceAll("\\s+", "");
        holder.txt_string.setText(values.get(position)+" ("+denomination+") ("+coinSeriesList.get(position)+")");
    }

    @Override
    public int getItemCount() {
        return values.size();
    }
}
