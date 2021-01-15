package com.novigosolutions.certiscisco_pcsbr.adapters;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.novigosolutions.certiscisco_pcsbr.R;
import com.novigosolutions.certiscisco_pcsbr.interfaces.RecyclerViewClickListner3;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class BluetoothPairedListAdapter extends RecyclerView.Adapter<BluetoothPairedListAdapter.ViewHolder> {
    Context context;
    private List<BluetoothDevice> mpairedlist = new ArrayList<BluetoothDevice>();
    public RecyclerViewClickListner3 listner3;
    public BluetoothPairedListAdapter(Context context, List<BluetoothDevice> mpairedlist,RecyclerViewClickListner3 listner3){
        this.context=context;
        this.mpairedlist=mpairedlist;
        this.listner3=listner3;
    }

    @NonNull
    @Override
    public BluetoothPairedListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bluetooth_paired_device_list, parent, false);
        return new BluetoothPairedListAdapter.ViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull BluetoothPairedListAdapter.ViewHolder viewHolder,  int i) {
        viewHolder.txt_device_name.setText(mpairedlist.get(i).getName());
        viewHolder.txt_mac_address.setText(mpairedlist.get(i).getAddress());

        if (i == mpairedlist.size()-1){
            viewHolder.txt_line_break.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mpairedlist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt_device_name,txt_mac_address,bt_unpair,txt_line_break;
        ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_device_name=itemView.findViewById(R.id.txt_device_name);
            txt_mac_address=itemView.findViewById(R.id.txt_mac_address);
            txt_line_break=itemView.findViewById(R.id.txt_line_break);
            imageView=itemView.findViewById(R.id.device_status);
            bt_unpair=itemView.findViewById(R.id.bt_unpair);
            imageView.setVisibility(View.GONE);

            txt_mac_address.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listner3.onClick(mpairedlist.get(getLayoutPosition()),getLayoutPosition(),"connect");
                }
            });
            txt_device_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listner3.onClick(mpairedlist.get(getLayoutPosition()),getLayoutPosition(),"connect");
                }
            });
            bt_unpair.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listner3.onClick(mpairedlist.get(getLayoutPosition()),getLayoutPosition(),"unpair");
                }
            });

        }


    }
}
