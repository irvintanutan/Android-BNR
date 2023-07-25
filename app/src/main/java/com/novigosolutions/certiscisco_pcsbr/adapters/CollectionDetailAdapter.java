package com.novigosolutions.certiscisco_pcsbr.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.novigosolutions.certiscisco_pcsbr.R;
import com.novigosolutions.certiscisco_pcsbr.interfaces.RecyclerViewClickListener;
import com.novigosolutions.certiscisco_pcsbr.models.Job;
import com.novigosolutions.certiscisco_pcsbr.service.UserLogService;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

import static com.novigosolutions.certiscisco_pcsbr.constant.UserLog.COLLECTION;

public class CollectionDetailAdapter extends RecyclerView.Adapter<CollectionDetailAdapter.MyViewHolder> {
    List<Job> jobs;
    private static RecyclerViewClickListener itemListener;
    Context context;


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txt_branch_code, txt_pin,txt_order_no;
        Button btn_start, btn_no_coll;
        View llbuttons;
        View view;

        public MyViewHolder(View view) {
            super(view);
            this.view = view;
            txt_branch_code = view.findViewById(R.id.txt_branch_code);
            txt_order_no = view.findViewById(R.id.txt_order_no);
            txt_pin = view.findViewById(R.id.txt_pin);
            btn_start = view.findViewById(R.id.btn_start);
            llbuttons = view.findViewById(R.id.llbuttons);
            btn_no_coll = view.findViewById(R.id.btn_no_coll);
            btn_start.setOnClickListener(this);
            btn_no_coll.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.btn_start)
                itemListener.recyclerViewListClicked(jobs.get(this.getLayoutPosition()).TransportMasterId);
            else if (id == R.id.btn_no_coll) alert(this.getLayoutPosition());

        }
    }

    public CollectionDetailAdapter(List<Job> jobs, RecyclerViewClickListener itemListener, Context context) {
        this.jobs = jobs;
        this.itemListener = itemListener;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.collection_detail_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.txt_branch_code.setText(String.valueOf(jobs.get(position).BranchCode));
        holder.txt_order_no.setText(String.valueOf(jobs.get(position).OrderNo));
        holder.txt_pin.setText(String.valueOf(jobs.get(position).PinCode));
        if (jobs.get(position).Status != null && jobs.get(position).Status.equals("COMPLETED")) {
            holder.view.setBackgroundColor(Color.parseColor("#C8E6C9"));
            holder.llbuttons.setVisibility(View.GONE);
        } else {
            holder.llbuttons.setVisibility(View.VISIBLE);
            holder.view.setBackgroundColor(Color.parseColor("#FFFFFF"));
            if (jobs.get(position).isNoCollection) {
                holder.btn_start.setVisibility(View.GONE);
                holder.btn_no_coll.setEnabled(false);
                holder.btn_no_coll.setBackgroundColor(Color.parseColor("#546E7A"));
            } else {
                holder.btn_start.setVisibility(View.VISIBLE);
                holder.btn_no_coll.setEnabled(true);
                holder.btn_no_coll.setBackgroundColor(Color.parseColor("#EF6C00"));
            }
            if (Job.isCollected(jobs.get(position).TransportMasterId))
                holder.btn_no_coll.setVisibility(View.GONE);
            else
                holder.btn_no_coll.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return jobs.size();
    }

    private void alert(final int pos) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Confirm");
        alertDialog.setMessage("Are you sure you want to make this Drop Off Point as 'No Collection'?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Job.setNoCollection(jobs.get(pos).TransportMasterId);
                jobs.get(pos).isNoCollection = true;
                notifyDataSetChanged();
                UserLogService.save(COLLECTION.toString(), "JOB (" +jobs.get(pos).TransportMasterId+")", "NO COLLECTION", context);
            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.show();
    }
}