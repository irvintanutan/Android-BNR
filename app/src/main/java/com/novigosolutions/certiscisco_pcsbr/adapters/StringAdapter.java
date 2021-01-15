package com.novigosolutions.certiscisco_pcsbr.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.novigosolutions.certiscisco_pcsbr.R;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class StringAdapter extends RecyclerView.Adapter<StringAdapter.MyViewHolder> {
    List<String> values;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txt_string;

        public MyViewHolder(View view) {
            super(view);
            txt_string = (TextView) view.findViewById(R.id.txt_string);
        }
    }

    public StringAdapter(List<String> values) {
        this.values = values;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.string_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.txt_string.setText(values.get(position));
    }

    @Override
    public int getItemCount() {
        return values.size();
    }
}