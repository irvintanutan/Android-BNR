package com.novigosolutions.certiscisco_pcsbr.adapters;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.novigosolutions.certiscisco_pcsbr.R;
import com.novigosolutions.certiscisco_pcsbr.interfaces.RecyclerViewClickListener2;
import com.novigosolutions.certiscisco_pcsbr.interfaces.RecyclerViewClickListner3;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BluetoothScannedListAdapter extends RecyclerView.Adapter<BluetoothScannedListAdapter.ViewHolder> {

    Context context;
    public  RecyclerViewClickListner3 listner3;
    private List<BluetoothDevice> mScannedList = new ArrayList<>();
    //  private Set<BluetoothDevice> mScannedList = new HashSet<>();

    public BluetoothScannedListAdapter(Context context, RecyclerViewClickListner3 listner3)
    {
        this.context=context;
        this.listner3=listner3;
    }

    public void addDevice(BluetoothDevice device) {
        mScannedList.add(device);
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public BluetoothScannedListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bluetooth_scan_device_list, parent, false);
        return new BluetoothScannedListAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BluetoothScannedListAdapter.ViewHolder viewHolder, int i) {
        viewHolder.txt_device_name.setText(mScannedList.get(i).getName());
        viewHolder.txt_mac_address.setText(mScannedList.get(i).getAddress());
        if (i == mScannedList.size()-1){
            viewHolder.txt_line_break.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mScannedList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView txt_device_name,txt_mac_address,bt_unpair,txt_line_break;
        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_device_name=itemView.findViewById(R.id.txt_device_name);
            txt_mac_address=itemView.findViewById(R.id.txt_mac_address);
            txt_line_break=itemView.findViewById(R.id.txt_line_break);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listner3.onClick(mScannedList.get(this.getLayoutPosition()),getLayoutPosition(),"pair");
        }
    }
}
