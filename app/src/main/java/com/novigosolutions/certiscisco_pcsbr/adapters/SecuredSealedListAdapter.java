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
        TextView txt_seal, txt_seal2, type;
        LinearLayout llmain;

        public MyViewHolder(View view) {
            super(view);
            type = view.findViewById(R.id.txtType);
            txt_seal2 = view.findViewById(R.id.txt_seal2);
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
                .inflate(R.layout.bag_list_item_secure, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {


        holder.type.setText(secureObjects.get(position).Type);
        if (secureObjects.get(position).Type.equals("BAG")) {
            holder.type.setText("Sealed " + secureObjects.get(position).Type);
        } else
            holder.type.setText(secureObjects.get(position).Type);

        holder.txt_seal.setText("(" + secureObjects.get(position).Barcode + ")");
        if (secureObjects.get(position).SecondBarcode != null && !secureObjects.get(position).SecondBarcode.equals("null"))
            holder.txt_seal2.setText("(" + secureObjects.get(position).SecondBarcode + ")");


        if (secureObjects.get(position).IsScanned && secureObjects.get(position).SecondBarcode == null) {
            holder.llmain.setBackgroundColor(Color.parseColor(colorGreen));
        } else {
            if (secureObjects.get(position).IsScannedSecond) {
                holder.txt_seal2.setBackgroundColor(Color.parseColor(colorGreen));
            }
            if (secureObjects.get(position).IsScanned) {
                holder.txt_seal.setBackgroundColor(Color.parseColor(colorGreen));
            }

            if (secureObjects.get(position).IsScanned && secureObjects.get(position).IsScannedSecond) {
                holder.llmain.setBackgroundColor(Color.parseColor(colorGreen));
            } else
                holder.llmain.setBackgroundColor(Color.parseColor(colorWhite));
        }
    }

    @Override
    public int getItemCount() {
        return secureObjects.size();
    }
}