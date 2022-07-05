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

public class SecureCageListAdapter extends RecyclerView.Adapter<SecureCageListAdapter.MyViewHolder> {
    List<Cage> cages;
    String colorGreen = "#43A047";
    String colorYellow = "#FFEB3B";

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView cageNo , cageSeal;
        LinearLayout llmain;

        public MyViewHolder(View view) {
            super(view);
            cageNo = view.findViewById(R.id.cageNo);
            cageSeal = view.findViewById(R.id.cageSeal);
            llmain = view.findViewById(R.id.ll_main);
        }

    }

    public SecureCageListAdapter(List<Cage> cages) {
        this.cages = cages;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cage_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Cage cage = cages.get(position);

        holder.cageNo.setText(cage.CageNo);
        if (cage.IsCageNoScanned) holder.cageNo.setBackgroundColor(Color.parseColor(colorGreen));

        holder.cageSeal.setText(cage.CageSeal);
        if (cage.IsCageSealScanned) holder.cageSeal.setBackgroundColor(Color.parseColor(colorGreen));

        if (cage.IsCageSealScanned && cage.IsCageNoScanned) {
            holder.llmain.setBackgroundColor(Color.parseColor(colorGreen));
        } else {
            holder.llmain.setBackgroundColor(Color.parseColor(colorYellow));
        }
    }

    @Override
    public int getItemCount() {
        return cages.size();
    }
}