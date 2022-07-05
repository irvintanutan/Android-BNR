package com.novigosolutions.certiscisco_pcsbr.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.novigosolutions.certiscisco_pcsbr.R;
import com.novigosolutions.certiscisco_pcsbr.models.Delivery;
import com.novigosolutions.certiscisco_pcsbr.objects.SecureObject;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class SecuredSealedListAdapter extends RecyclerView.Adapter<SecuredSealedListAdapter.MyViewHolder> {
    List<SecureObject> secureObjects;
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

    public SecuredSealedListAdapter(List<SecureObject> secureObjects) {
        this.secureObjects = secureObjects;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bag_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        if (secureObjects.get(position).Type.equals("BAG")) {
            holder.txt_seal.setText("Sealed " + secureObjects.get(position).Type + "(" + secureObjects.get(position).Barcode + ")");
        } else
            holder.txt_seal.setText(secureObjects.get(position).Type + "(" + secureObjects.get(position).Barcode + ")");


        if (secureObjects.get(position).IsScanned) {
            holder.llmain.setBackgroundColor(Color.parseColor(colorGreen));
        } else {
            holder.llmain.setBackgroundColor(Color.parseColor(colorWhite));
        }
    }

    @Override
    public int getItemCount() {
        return secureObjects.size();
    }
}