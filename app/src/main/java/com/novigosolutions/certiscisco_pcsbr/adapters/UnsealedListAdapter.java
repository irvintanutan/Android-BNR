package com.novigosolutions.certiscisco_pcsbr.adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.novigosolutions.certiscisco_pcsbr.R;
import com.novigosolutions.certiscisco_pcsbr.models.Delivery;

import java.util.List;

public class UnsealedListAdapter extends RecyclerView.Adapter<UnsealedListAdapter.MyViewHolder> {
    List<Delivery> deliveries;
    String colorGreen = "#43A047";
    String colorWhite = "#FFFFFF";
    UnsealedClickCallback unsealedClickCallback;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txt_seal;
        LinearLayout llmain;
        ImageView img_add;

        public MyViewHolder(View view) {
            super(view);
            txt_seal = view.findViewById(R.id.txt_seal);
            llmain = view.findViewById(R.id.ll_main);
            img_add = view.findViewById(R.id.img_add);
            img_add.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Delivery.setScanned(deliveries.get(this.getLayoutPosition()).getId());
            deliveries.get(this.getLayoutPosition()).IsScanned = true;
            if(unsealedClickCallback!=null) {
                unsealedClickCallback.onSelect();
            }
            notifyDataSetChanged();
        }
    }

    public UnsealedListAdapter(List<Delivery> deliveries) {
        this.deliveries = deliveries;
    }

    public void setUnsealedClickCallback(UnsealedClickCallback unsealedClickCallback) {
        this.unsealedClickCallback = unsealedClickCallback;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.box_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        String type = deliveries.get(position).ItemType;
//        if (type.equals("BOX"))
//            holder.txt_seal.setText(type + "(" + deliveries.get(position).Denomination + " * " + deliveries.get(position).Qty + ")");
//        else
//            holder.txt_seal.setText(type + "(" + deliveries.get(position).Qty + ")");
        if (type.equals("BOX")){
            if(deliveries.get(position).CoinSeriesId==0){
                holder.txt_seal.setText(type + "(" + deliveries.get(position).Denomination + " * " + deliveries.get(position).Qty + ")");
            }else{
                holder.txt_seal.setText(type + "(" + deliveries.get(position).Denomination + " * " + deliveries.get(position).Qty + ")("+ deliveries.get(position).CoinSeries+")");
            }
        }
        else{
            holder.txt_seal.setText(type + "(" + deliveries.get(position).Qty + ")");
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

   public interface UnsealedClickCallback {
        void onSelect();
   }

}

