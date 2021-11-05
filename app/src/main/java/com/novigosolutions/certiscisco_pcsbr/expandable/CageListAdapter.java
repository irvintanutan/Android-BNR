package com.novigosolutions.certiscisco_pcsbr.expandable;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.novigosolutions.certiscisco_pcsbr.R;
import com.novigosolutions.certiscisco_pcsbr.models.Cage;
import com.novigosolutions.certiscisco_pcsbr.models.Delivery;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class CageListAdapter extends RecyclerView.Adapter<CageListAdapter.MyViewHolder> {
    List<Cage> cages;
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

    public CageListAdapter(List<Cage> cages) {
        this.cages = cages;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bag_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Cage cage = cages.get(position);

        holder.txt_seal.setText(cage.CageNo + " / " + cage.CageSeal);


//        if (deliveries.get(position).IsScanned) {
//            holder.llmain.setBackgroundColor(Color.parseColor(colorGreen));
//        } else {
//            holder.llmain.setBackgroundColor(Color.parseColor(colorWhite));
//        }
    }

    @Override
    public int getItemCount() {
        return cages.size();
    }
}