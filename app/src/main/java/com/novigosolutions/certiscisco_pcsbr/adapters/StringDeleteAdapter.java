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

public class StringDeleteAdapter extends RecyclerView.Adapter<StringDeleteAdapter.MyViewHolder> {
    List<String> values;
    RecyclerViewClickListener itemListener;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txt_string;
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

    public StringDeleteAdapter(List<String> values,RecyclerViewClickListener itemListener) {
        this.values = values;
        this.itemListener = itemListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.string_delete_item, parent, false);
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