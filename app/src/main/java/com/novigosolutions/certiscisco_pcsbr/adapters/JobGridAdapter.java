package com.novigosolutions.certiscisco_pcsbr.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.novigosolutions.certiscisco_pcsbr.R;
import com.novigosolutions.certiscisco_pcsbr.interfaces.RecyclerViewClickListener;
import com.novigosolutions.certiscisco_pcsbr.models.Branch;
import com.novigosolutions.certiscisco_pcsbr.models.Delivery;
import com.novigosolutions.certiscisco_pcsbr.models.Job;
import com.novigosolutions.certiscisco_pcsbr.utils.CommonMethods;

import java.util.Date;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class JobGridAdapter extends RecyclerView.Adapter<JobGridAdapter.MyViewHolder> {
    List<Job> jobs;
    private static RecyclerViewClickListener itemListener;
    Context context;
    long serverTime;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txt_pos;
        View llmain;

        public MyViewHolder(View view) {
            super(view);
            txt_pos = (TextView) view.findViewById(R.id.txt_pos);
            llmain = view.findViewById(R.id.llmain);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemListener.recyclerViewListClicked(jobs.get(this.getLayoutPosition()).TransportMasterId);

        }
    }

    public JobGridAdapter(List<Job> jobs, RecyclerViewClickListener itemListener, Context context) {
        this.jobs = jobs;
        this.itemListener = itemListener;
        this.context = context;
        serverTime = CommonMethods.getServerTimeInms(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.job_grid_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Job job = jobs.get(position);
        Branch b = Branch.getSingle(jobs.get(position).GroupKey);
        holder.txt_pos.setText(job.SequenceNo);

        boolean yellow = false;
        if(job.IsFloatDeliveryOrder) {
            List<Job> js = Job.getDeliveryJobsOfPoint(job.GroupKey, job.BranchCode, job.PFunctionalCode);
            for (Job jb : js) {
                if (TextUtils.isEmpty(jb.DependentOrderId)
                        && Job.checkPendingDependentCollections(jb.DependentOrderId).size() > 0) {
                    yellow = true;
                    break;
                } else if (!Delivery.hasPendingDeliveryItems(jb.TransportMasterId)) {
                    yellow = true;
                    break;
                }
            }
        }

        if (job.isOfflineSaved) {
            holder.llmain.setBackgroundResource(R.drawable.brown_border);
        } else if (jobs.get(position).Status.equals("COMPLETED")) {
            holder.llmain.setBackgroundResource(R.drawable.green_border);
        } else if (yellow) {
            holder.llmain.setBackgroundResource(R.drawable.yellow_border);
//        } else if (Job.getPendingDeliveryJobsOfPoint(b.GroupKey).size() > 0 && !Delivery.hasPendingDeliveryItems(b.GroupKey)) {
//            holder.llmain.setBackgroundResource(R.drawable.yellow_border);
//        } else if (job.IsFloatDeliveryOrder && TextUtils.isEmpty(job.DependentOrderId) && Job.checkPendingDependentCollections(job.DependentOrderId).size()>0) {
//            holder.llmain.setBackgroundResource(R.drawable.yellow_border);
//        } else if (job.IsFloatDeliveryOrder && !Delivery.hasPendingDeliveryItems(job.TransportMasterId)){
//            holder.llmain.setBackgroundResource(R.drawable.yellow_border);
        } else {
            Date EndTime = CommonMethods.getBreakTime(b.EndTime);
            if (EndTime != null && EndTime.getTime() < serverTime) {
                holder.llmain.setBackgroundResource(R.drawable.red_border);
            } else if (EndTime != null && EndTime.getTime()-(60*60*1000) < serverTime) {
                holder.llmain.setBackgroundResource(R.drawable.orange_border);
            } else {
                holder.llmain.setBackgroundResource(R.drawable.blue_border);
            }
        }
    }

    @Override
    public int getItemCount() {
        return jobs.size();
    }

    public void refresh() {
        serverTime = CommonMethods.getServerTimeInms(context);
        notifyDataSetChanged();
    }
}