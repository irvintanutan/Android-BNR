package com.novigosolutions.certiscisco_pcsbr.adapters;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.MyViewHolder> {
    List<Branch> branches;
    Context context;
    String status;
    private static RecyclerViewClickListener2 itemListener;
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
                txt_branch_name,txt_remarks; //txt_street_tower, txt_town_pin,
        TextView txt_drop_off_branch;
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
            itemListener.recyclerViewListClicked(branches.get(this.getLayoutPosition()).GroupKey);

        }
    }

    public GroupListAdapter(List<Branch> branches,String status, Context context, RecyclerViewClickListener2 itemListener) {
        this.branches = branches;
        this.context = context;
        this.status = status;
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
        boolean singleJob = false;
        Job j = null;
        int jobtype = 0;
        List<Job> js = Job.getByGroupKey(branches.get(position).GroupKey);
        if(js.size()==1){
            singleJob = true;
            j = js.get(0);
            if(j.IsCollectionOrder && j.IsFloatDeliveryOrder ) {
                jobtype = 3;
            } else if (j.IsFloatDeliveryOrder) {
                jobtype = 2;
            } else if (j.IsCollectionOrder){
                jobtype = 1;
            }
        }


        holder.txt_customer_name.setText(branches.get(position).CustomerName);
        holder.txt_functional_code.setText(Job.getAllOrderNos(branches.get(position).GroupKey,status));
        holder.txt_start_time.setText(CommonMethods.getStartTime(branches.get(position).StartTime) + " - " + CommonMethods.getStartTime(branches.get(position).EndTime));


        if(singleJob) {
            if(!TextUtils.isEmpty(j.SequenceNo) && !j.SequenceNo.equalsIgnoreCase("null"))
                holder.txt_pos.setText(j.SequenceNo);
            else
                holder.txt_pos.setText("");
            holder.txt_break_time.setText(j.ClientBreak);
            if(jobtype==1) {
                holder.txt_branch_name.setText("Pick Up : " + branches.get(position).BranchCode);// + " (" + j.FunctionalCode + ")");
            }else  if(jobtype==2){
                holder.txt_branch_name.setText("Drop Off : " + branches.get(position).BranchCode);
            }
        }
        else {

            holder.txt_pos.setText(Job.getMinimumSequenceNo(branches.get(position).GroupKey,status));
            if(jobtype==1) {
                holder.txt_branch_name.setText("Pick Up : " + branches.get(position).BranchCode);// + " (" + j.FunctionalCode + ")");
            }else  if(jobtype==2){
                holder.txt_branch_name.setText("Drop Off : " + branches.get(position).BranchCode);
            }else {
                holder.txt_branch_name.setText(branches.get(position).BranchCode);
            }
        }

        if(singleJob && !TextUtils.isEmpty(j.OrderRemarks)){
            holder.txt_remarks.setVisibility(View.VISIBLE);
            holder.txt_remarks.setText("Remarks: "+j.OrderRemarks);
        }else{
            holder.txt_remarks.setVisibility(View.GONE);
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
        if(!singleJob) {
            if ("ALL".equals(status)) {
                jobtype = branches.get(position).getBranchJobType();
            } else {
                jobtype = branches.get(position).getBranchJobTypeByStatus(status);
            }
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
        if(!singleJob && (jobtype == 2 || jobtype == 3)){
            Log.d("TAG", "onBindViewHolder: ");
            List<Job> jl = Job.getDeliveryJobsOfPoint(branches.get(position).GroupKey);
            for(Job jb:jl){
                if(TextUtils.isEmpty(jb.DependentOrderId)
                        && Job.checkPendingDependentCollections(jb.DependentOrderId).size()>0) {
                    yellow = true;
                    break;
                } else if(!Delivery.hasPendingDeliveryItems(jb.TransportMasterId)){
                    yellow = true;
                    break;
                }
            }
        }

        if(singleJob){
            if(jobtype==1){
                if(!TextUtils.isEmpty(j.BranchCode)) {
                    holder.txt_drop_off_branch.setVisibility(View.VISIBLE);
                    holder.txt_drop_off_branch.setText("Drop Off : "+j.BranchCode.toUpperCase());
                }
            }else {
                holder.txt_drop_off_branch.setVisibility(View.GONE);
            }
        }else {
            holder.txt_drop_off_branch.setVisibility(View.GONE);
        }

        if (!"COMPLETED".equals(status)) {
            if (j!=null && j.isOfflineSaved) {
                holder.llmain.setBackgroundColor(Color.parseColor(colorBrown));
                holder.llsub.setBackgroundColor(Color.parseColor(colorLightBrown));
            } else if (Job.getPendingJobsOfPoint(branches.get(position).GroupKey).size() == 0) {
                holder.llmain.setBackgroundColor(Color.parseColor(colorGreen));
                holder.llsub.setBackgroundColor(Color.parseColor(colorLightGreen));
            } else if (Job.getPendingDeliveryJobsOfPoint(branches.get(position).GroupKey).size() > 0 && !Delivery.hasPendingDeliveryItems(branches.get(position).GroupKey)) {
                holder.llmain.setBackgroundColor(Color.parseColor(colorYellow));
                holder.llsub.setBackgroundColor(Color.parseColor(colorLightYellow));
            } else if (singleJob && jobtype == 2 && TextUtils.isEmpty(j.DependentOrderId) && Job.checkPendingDependentCollections(j.DependentOrderId).size()>0) {
                holder.llmain.setBackgroundColor(Color.parseColor(colorYellow));
                holder.llsub.setBackgroundColor(Color.parseColor(colorLightYellow));
            } else if (singleJob && jobtype == 2 && !Delivery.hasPendingDeliveryItems(j.TransportMasterId)){
                holder.llmain.setBackgroundColor(Color.parseColor(colorYellow));
                holder.llsub.setBackgroundColor(Color.parseColor(colorLightYellow));
            } else if(yellow){
                holder.llmain.setBackgroundColor(Color.parseColor(colorYellow));
                holder.llsub.setBackgroundColor(Color.parseColor(colorLightYellow));
            }
            else {
                Date endTime = CommonMethods.getBreakTime(branches.get(position).EndTime);
                if (endTime != null && endTime.getTime() < serverTime) {
                    holder.llmain.setBackgroundColor(Color.parseColor(colorRed));
                    holder.llsub.setBackgroundColor(Color.parseColor(colorLightRed));
                } else if (endTime != null && endTime.getTime() - (60 * 60 * 1000) < serverTime) {
                    holder.llmain.setBackgroundColor(Color.parseColor(colorOrange));
                    holder.llsub.setBackgroundColor(Color.parseColor(colorLightOrange));
                } else {
                    holder.llmain.setBackgroundColor(Color.parseColor(colorBlue));
                    holder.llsub.setBackgroundColor(Color.parseColor(colorWhite));
                }
            }
        }else {
            holder.llmain.setBackgroundColor(Color.parseColor(colorGreen));
            holder.llsub.setBackgroundColor(Color.parseColor(colorLightGreen));
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