package com.novigosolutions.certiscisco_pcsbr.adapters;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

public class JobListAdapter extends RecyclerView.Adapter<JobListAdapter.MyViewHolder> {
    List<Job> jobs;
    Context context;
    private static RecyclerViewClickListener itemListener;
    String colorGreen = "#2E7D32";
    String colorBrown = "#795548";
    String colorRed = "#C62828";
    String colorBlue = "#00245D";
    String colorYellow = "#FFEB3B";
    String colorOrange = "#FF9800";

    String colorLightGreen = "#C8E6C9";
    String colorLightRed = "#FFCDD2";
    String colorLightYellow = "#FFF9C4";
    String colorLightOrange = "#FFE0B2";
    String colorLightBrown = "#D7CCC8";
    String colorWhite = "#FFFFFF";
    long serverTime;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txt_customer_name, txt_functional_code,  txt_pos, txt_start_time,txt_break_time,
                txt_branch_name,txt_remarks,txt_drop_off_branch; //txt_street_tower, txt_town_pin,
        ImageView img_type;
        View llmain, llsub;

        public MyViewHolder(View view) {
            super(view);
            txt_customer_name = (TextView) view.findViewById(R.id.txt_customer_name);
            txt_functional_code = (TextView) view.findViewById(R.id.txt_functional_code);
            txt_remarks = (TextView) view.findViewById(R.id.txt_remarks);
//            txt_street_tower = (TextView) view.findViewById(R.id.txt_street_tower);
//            txt_town_pin = (TextView) view.findViewById(R.id.txt_town_pin);
            txt_branch_name = (TextView) view.findViewById(R.id.txt_branch_name);
            txt_pos = (TextView) view.findViewById(R.id.txt_pos);
            txt_start_time = (TextView) view.findViewById(R.id.txt_start_time);
            txt_break_time = (TextView) view.findViewById(R.id.txt_break_time);
            txt_drop_off_branch=(TextView)view.findViewById(R.id.drop_off_branch);
            img_type = view.findViewById(R.id.img_type);
            llmain = view.findViewById(R.id.llmain);
            llsub = view.findViewById(R.id.llsub);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemListener.recyclerViewListClicked(jobs.get(this.getLayoutPosition()).TransportMasterId);

        }
    }

    public JobListAdapter(List<Job> jobs, Context context, RecyclerViewClickListener itemListener) {
        this.jobs = jobs;
        this.context = context;
        this.itemListener = itemListener;
        serverTime = CommonMethods.getServerTimeInms(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.job_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Job job = jobs.get(position);
        Branch b = Branch.getSingle(job.GroupKey);
        if(!TextUtils.isEmpty(job.SequenceNo) && !job.SequenceNo.equalsIgnoreCase("null"))
        holder.txt_pos.setText(job.SequenceNo);
        holder.txt_customer_name.setText(job.CustomerName);
        if(job.IsFloatDeliveryOrder){
            List<Job> jjl = Job.getDeliveryJobsOfPoint(job.GroupKey, job.BranchCode , job.PFunctionalCode);
            if(jjl.size()>1) {
                String rem = null;
                for (Job jj : jjl) {
    //                if(!TextUtils.isEmpty(jj.OrderRemarks)){
                    if(rem == null){
                        if(!TextUtils.isEmpty(jj.OrderRemarks))
                            rem = jj.OrderRemarks;
                    }else{
                        if(!TextUtils.isEmpty(jj.OrderRemarks)) {
                            if (rem.equals(jj.OrderRemarks)) {
                                rem = jj.OrderRemarks;
                            } else {
                                rem = "";
                            }
                        }
                    }
    //                }
                }
                if (!TextUtils.isEmpty(rem)) {
                    holder.txt_remarks.setVisibility(View.VISIBLE);
                    holder.txt_remarks.setText("Remarks: "+rem);
                } else {
                    holder.txt_remarks.setVisibility(View.GONE);
                }
            }
        }else if(!TextUtils.isEmpty(job.OrderRemarks)){
            holder.txt_remarks.setVisibility(View.VISIBLE);
            holder.txt_remarks.setText("Remarks: "+job.OrderRemarks);
        }else{
            holder.txt_remarks.setVisibility(View.GONE);
        }
//        if(job.IsFloatDeliveryOrder){
//            holder.txt_functional_code.setText(Job.getAllDeliveryOrderNos(job.GroupKey));
//        } else{
//            holder.txt_functional_code.setText(Job.getAllOrderNos(job.GroupKey, job.Status));
//        }

        if (job.Status.equals("COMPLETED"))
            holder.txt_functional_code.setText(Job.getAllOrderNos(job.GroupKey, job.BranchCode , job.PFunctionalCode , "COMPLETED", job.PDFunctionalCode));
        else
            holder.txt_functional_code.setText(Job.getAllOrderNos(job.GroupKey, job.BranchCode , job.PFunctionalCode ,"PENDING", job.PDFunctionalCode));

        holder.txt_start_time.setText(CommonMethods.getStartTime(b.StartTime) + " - " + CommonMethods.getStartTime(b.EndTime));
        holder.txt_break_time.setText(job.ClientBreak);

        if(job.IsCollectionOrder) {
            holder.txt_branch_name.setText("Pick Up : " + b.BranchCode);//+" ("+job.FunctionalCode+")");
        }else if(job.IsFloatDeliveryOrder) {
            holder.txt_branch_name.setText("Drop Off : " + b.BranchCode);
        }

//        String street_tower = branches.get(position).StreetName;
//        if (!branches.get(position).Tower.isEmpty()) {
//            if (street_tower.isEmpty()) street_tower = branches.get(position).Tower;
//            else street_tower = street_tower + ", " + branches.get(position).Tower;
//        }
//        holder.txt_street_tower.setText(street_tower);
//
//        String town_pin = branches.get(position).Town;
//        if (!branches.get(position).PinCode.isEmpty()) {
//            if (town_pin.isEmpty()) town_pin = branches.get(position).PinCode;
//            else town_pin = town_pin + ", " + branches.get(position).PinCode;
//        }
//        holder.txt_town_pin.setText(town_pin);

        int jobtype = 0;
        if(job.IsCollectionOrder && job.IsFloatDeliveryOrder ) {
            jobtype = 3;
        } else if (job.IsFloatDeliveryOrder) {
            jobtype = 2;
        } else if (job.IsCollectionOrder){
            jobtype = 1;
        }

        switch (jobtype) {
            case 1:
                holder.img_type.setImageResource(R.drawable.ic_collection);
                break;
            case 2:
                holder.img_type.setImageResource(R.drawable.ic_delivery);
                break;
            case 3:
                holder.img_type.setImageResource(R.drawable.ic_collection_delivery);
                break;
        }
        boolean yellow = false;
        if(jobtype == 2) {
            List<Job> js = Job.getDeliveryJobsOfPoint(job.GroupKey, job.BranchCode , job.PFunctionalCode);
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



        if(jobtype==1){
            if(!TextUtils.isEmpty(job.BranchCode)) {
                holder.txt_drop_off_branch.setVisibility(View.VISIBLE);
                holder.txt_drop_off_branch.setText("Drop Off : "+job.BranchCode.toUpperCase());
            }
        }else {
            holder.txt_drop_off_branch.setVisibility(View.GONE);
        }

        if (job.isOfflineSaved) {
            holder.llmain.setBackgroundColor(Color.parseColor(colorBrown));
            holder.llsub.setBackgroundColor(Color.parseColor(colorLightBrown));
        } else if (job.Status.equals("COMPLETED")) {
            holder.llmain.setBackgroundColor(Color.parseColor(colorGreen));
            holder.llsub.setBackgroundColor(Color.parseColor(colorLightGreen));
//        } else if (Job.getPendingDeliveryJobsOfPoint(b.GroupKey).size() > 0 && !Delivery.hasPendingDeliveryItems(b.GroupKey)) {
//            holder.llmain.setBackgroundColor(Color.parseColor(colorYellow));
//            holder.llsub.setBackgroundColor(Color.parseColor(colorLightYellow));
        } else if (jobtype == 2 && yellow) {
            holder.llmain.setBackgroundColor(Color.parseColor(colorYellow));
            holder.llsub.setBackgroundColor(Color.parseColor(colorLightYellow));
//        } else if (jobtype == 2 && TextUtils.isEmpty(job.DependentOrderId) && Job.checkPendingDependentCollections(job.DependentOrderId).size()>0) {
//            holder.llmain.setBackgroundColor(Color.parseColor(colorYellow));
//            holder.llsub.setBackgroundColor(Color.parseColor(colorLightYellow));
//        } else if (jobtype == 2 && !Delivery.hasPendingDeliveryItems(job.GroupKey)){
//            holder.llmain.setBackgroundColor(Color.parseColor(colorYellow));
//            holder.llsub.setBackgroundColor(Color.parseColor(colorLightYellow));
        } else {
            Date EndTime = CommonMethods.getBreakTime(b.EndTime);
            if (EndTime != null && EndTime.getTime() < serverTime) {
                holder.llmain.setBackgroundColor(Color.parseColor(colorRed));
                holder.llsub.setBackgroundColor(Color.parseColor(colorLightRed));
            } else if (EndTime != null && EndTime.getTime()-(60*60*1000) < serverTime) {
                holder.llmain.setBackgroundColor(Color.parseColor(colorOrange));
                holder.llsub.setBackgroundColor(Color.parseColor(colorLightOrange));
            } else {
                holder.llmain.setBackgroundColor(Color.parseColor(colorBlue));
                holder.llsub.setBackgroundColor(Color.parseColor(colorWhite));
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