package com.novigosolutions.certiscisco_pcsbr.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.novigosolutions.certiscisco_pcsbr.R;
import com.novigosolutions.certiscisco_pcsbr.interfaces.RecyclerViewClickListener2;
import com.novigosolutions.certiscisco_pcsbr.models.Branch;
import com.novigosolutions.certiscisco_pcsbr.models.Delivery;
import com.novigosolutions.certiscisco_pcsbr.models.Job;
import com.novigosolutions.certiscisco_pcsbr.utils.CommonMethods;

import java.util.Date;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class GroupGridAdapter extends RecyclerView.Adapter<GroupGridAdapter.MyViewHolder> {
    List<Branch> branches;
    private static RecyclerViewClickListener2 itemListener;
    Context context;
    long serverTime;
    String status;

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
            itemListener.recyclerViewListClicked(branches.get(this.getLayoutPosition()).GroupKey);

        }
    }

    public GroupGridAdapter(List<Branch> branches, String status, RecyclerViewClickListener2 itemListener, Context context) {
        this.branches = branches;
        this.itemListener = itemListener;
        this.context = context;
        this.status = status;
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
        boolean singleJob = false;
        Job j = null;
        List<Job> js = Job.getByGroupKey(branches.get(position).GroupKey);
        if (js.size() == 1) {
            singleJob = true;
            j = js.get(0);
        }

        String BranchCode = js.get(0).BranchCode;
        String PFunctionalCode = js.get(0).PFunctionalCode;

        if (singleJob) {
            if (!TextUtils.isEmpty(j.SequenceNo) && !j.SequenceNo.equalsIgnoreCase("null"))
                holder.txt_pos.setText(j.SequenceNo);
            else
                holder.txt_pos.setText("");
        } else {
            holder.txt_pos.setText(Job.getMinimumSequenceNo(branches.get(position).GroupKey, status));
        }
        int jobtype = 0;
        if (!singleJob) {
            if ("ALL".equals(status)) {
                jobtype = branches.get(position).getBranchJobType();
            } else {
                jobtype = branches.get(position).getBranchJobTypeByStatus(status);
            }
        }
        boolean yellow = false;
        if (!singleJob && (jobtype == 2 || jobtype == 3)) {
            Log.d("TAG", "onBindViewHolder: ");
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

        if (!"COMPLETED".equals(status)) {
            if (j != null && j.isOfflineSaved) {
                holder.llmain.setBackgroundResource(R.drawable.brown_border);
            } else if (Job.getPendingJobsOfPoint(branches.get(position).GroupKey).size() == 0) {
                holder.llmain.setBackgroundResource(R.drawable.green_border);
            } else if (Job.getPendingDeliveryJobsOfPoint(branches.get(position).GroupKey, BranchCode, PFunctionalCode).size() > 0 && !Delivery.hasPendingDeliveryItems(branches.get(position).GroupKey, BranchCode , PFunctionalCode)) {
                holder.llmain.setBackgroundResource(R.drawable.yellow_border);
            } else if (singleJob && j.IsFloatDeliveryOrder && TextUtils.isEmpty(j.DependentOrderId) && Job.checkPendingDependentCollections(j.DependentOrderId).size() > 0) {
                holder.llmain.setBackgroundResource(R.drawable.yellow_border);
            } else if (singleJob && j.IsFloatDeliveryOrder && !Delivery.hasPendingDeliveryItems(j.TransportMasterId)) {
                holder.llmain.setBackgroundResource(R.drawable.yellow_border);
            } else if (yellow) {
                holder.llmain.setBackgroundResource(R.drawable.yellow_border);
            } else {
                Date EndTime = CommonMethods.getBreakTime(branches.get(position).EndTime);
                if (EndTime != null && EndTime.getTime() < serverTime) {
                    holder.llmain.setBackgroundResource(R.drawable.red_border);
                } else if (EndTime != null && EndTime.getTime() - (60 * 60 * 1000) < serverTime) {
                    holder.llmain.setBackgroundResource(R.drawable.orange_border);
                } else {
                    holder.llmain.setBackgroundResource(R.drawable.blue_border);
                }
            }
        } else {
            holder.llmain.setBackgroundResource(R.drawable.green_border);
        }
    }

    @Override
    public int getItemCount() {
        return branches.size();
    }

    public void refresh() {
        serverTime = CommonMethods.getServerTimeInms(context);
        notifyDataSetChanged();
    }
}