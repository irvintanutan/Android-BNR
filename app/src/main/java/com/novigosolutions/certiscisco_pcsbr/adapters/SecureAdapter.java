package com.novigosolutions.certiscisco_pcsbr.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.novigosolutions.certiscisco_pcsbr.R;
import com.novigosolutions.certiscisco_pcsbr.models.Branch;
import com.novigosolutions.certiscisco_pcsbr.models.Job;
import com.novigosolutions.certiscisco_pcsbr.models.MenuForm;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


/**
 * @author irvin
 */
public class SecureAdapter extends RecyclerView.Adapter<SecureAdapter.ViewHolder> {
    private List<Job> jobs;
    private Context context;

    public SecureAdapter(List<Job> jobs , Context context) {
        this.jobs = jobs;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.secure_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Job job = jobs.get(position);
        Branch branch = Branch.getSingle(job.GroupKey);
        holder.location.setText("Location : " + branch.BranchCode);
        holder.customer.setText("Customer : " + job.CustomerName);
        holder.jobId.setText("Job ID : " + job.OrderNo);
    }


    @Override
    public int getItemCount() {
        return jobs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView customer , jobId, location;

        public ViewHolder(View view) {
            super(view);

            location = view.findViewById(R.id.location);
            customer = view.findViewById(R.id.customer);
            jobId = view.findViewById(R.id.jobId);
        }
    }

}