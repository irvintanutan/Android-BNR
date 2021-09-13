package com.novigosolutions.certiscisco_pcsbr.adapters;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.novigosolutions.certiscisco_pcsbr.R;
import com.novigosolutions.certiscisco_pcsbr.activites.PrintActivity;
import com.novigosolutions.certiscisco_pcsbr.interfaces.RecyclerViewClickListener;
import com.novigosolutions.certiscisco_pcsbr.interfaces.RecyclerViewClickListener2;
import com.novigosolutions.certiscisco_pcsbr.models.Branch;
import com.novigosolutions.certiscisco_pcsbr.models.Delivery;
import com.novigosolutions.certiscisco_pcsbr.models.Job;
import com.novigosolutions.certiscisco_pcsbr.utils.CommonMethods;

import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PrintJobListAdapter extends RecyclerView.Adapter<PrintJobListAdapter.MyViewHolder> {

    CheckBoxListnerCallBack checkBoxListnerCallBack;
    List<Branch> branches;
    Context context;
    String status;
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

    public PrintJobListAdapter(List<Branch> branches, String status, Context context, CheckBoxListnerCallBack checkBoxListnerCallBack) {
        this.branches = branches;
        this.context = context;
        this.status = status;
        serverTime = CommonMethods.getServerTimeInms(context);
        this.checkBoxListnerCallBack = checkBoxListnerCallBack;
    }

    @NonNull
    @Override
    public PrintJobListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.print_job_list, parent, false);
        return new PrintJobListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final PrintJobListAdapter.MyViewHolder holder, int position) {

        boolean singleJob = false;
        Job j = null;
        int jobtype = 0;
        List<Job> js = Job.getByGroupKey(branches.get(position).GroupKey);
        if (js.size() == 1) {
            singleJob = true;
            j = js.get(0);
            if (j.IsCollectionOrder && j.IsFloatDeliveryOrder) {
                jobtype = 3;
            } else if (j.IsFloatDeliveryOrder) {
                jobtype = 2;
            } else if (j.IsCollectionOrder) {
                jobtype = 1;
            }
        }


        String BranchCode = js.get(0).BranchCode;
        String PFunctionalCode = js.get(0).PFunctionalCode;
        String PDFunctionalCode = js.get(0).PDFunctionalCode;
        String actualFromTime = js.get(0).ActualFromTime;
        String actualToTime = js.get(0).ActualToTime;

        holder.txt_customer_name.setText(branches.get(position).CustomerName);
        holder.txt_functional_code.setText(Job.getAllOrderNos(branches.get(0).GroupKey, BranchCode, PFunctionalCode, status, PDFunctionalCode, actualFromTime, actualToTime));
        holder.txt_start_time.setText(CommonMethods.getStartTime(branches.get(position).StartTime) + " - " + CommonMethods.getStartTime(branches.get(position).EndTime));


        if (singleJob) {
            if (!TextUtils.isEmpty(j.SequenceNo) && !j.SequenceNo.equalsIgnoreCase("null"))
                holder.txt_pos.setText(j.SequenceNo);
            else
                holder.txt_pos.setText("");
            holder.txt_break_time.setText(j.ClientBreak);
            if (jobtype == 1) {
                holder.txt_branch_name.setText("Pick Up : " + branches.get(position).BranchCode);// + " (" + j.FunctionalCode + ")");
            } else if (jobtype == 2) {
                holder.txt_branch_name.setText("Drop Off : " + branches.get(position).BranchCode);
            }
        } else {

            holder.txt_pos.setText(Job.getMinimumSequenceNo(branches.get(position).GroupKey, status));
            if (jobtype == 1) {
                holder.txt_branch_name.setText("Pick Up : " + branches.get(position).BranchCode);// + " (" + j.FunctionalCode + ")");
            } else if (jobtype == 2) {
                holder.txt_branch_name.setText("Drop Off : " + branches.get(position).BranchCode);
            } else {
                holder.txt_branch_name.setText(branches.get(position).BranchCode);
            }
        }

        if (singleJob && !TextUtils.isEmpty(j.OrderRemarks)) {
            holder.txt_remarks.setVisibility(View.VISIBLE);
            holder.txt_remarks.setText("Remarks: " + j.OrderRemarks);
        } else {
            holder.txt_remarks.setVisibility(View.GONE);
        }

        if (!singleJob) {
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
        if (!singleJob && (jobtype == 2 || jobtype == 3)) {
            Log.d("TAG", "onBindViewHolder: ");
            List<Job> jl = Job.getDeliveryJobsOfPoint(branches.get(position).GroupKey, js.get(0).BranchCode, js.get(0).PFunctionalCode, js.get(0).ActualFromTime, js.get(0).ActualToTime);
            for (Job jb : jl) {
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


        if (singleJob) {
            if (jobtype == 1) {
                if (!TextUtils.isEmpty(j.BranchCode)) {
                    holder.txt_drop_off_branch.setVisibility(View.VISIBLE);
                    holder.txt_drop_off_branch.setText("Drop Off : " + j.BranchCode.toUpperCase());
                }
            } else {
                holder.txt_drop_off_branch.setVisibility(View.GONE);
            }
        } else {
            holder.txt_drop_off_branch.setVisibility(View.GONE);
        }

        if (!"COMPLETED".equals(status)) {
            if (j != null && j.isOfflineSaved) {
                holder.llmain.setBackgroundColor(Color.parseColor(colorBrown));
                holder.llsub.setBackgroundColor(Color.parseColor(colorLightBrown));
            } else if (Job.getPendingJobsOfPoint(branches.get(position).GroupKey).size() == 0) {
                holder.llmain.setBackgroundColor(Color.parseColor(colorGreen));
                holder.llsub.setBackgroundColor(Color.parseColor(colorLightGreen));
            } else if (Job.getPendingDeliveryJobsOfPoint(branches.get(position).GroupKey, BranchCode, PFunctionalCode).size() > 0 && !Delivery.hasPendingDeliveryItems(branches.get(position).GroupKey, BranchCode, PFunctionalCode)) {
                holder.llmain.setBackgroundColor(Color.parseColor(colorYellow));
                holder.llsub.setBackgroundColor(Color.parseColor(colorLightYellow));
            } else if (singleJob && jobtype == 2 && TextUtils.isEmpty(j.DependentOrderId) && Job.checkPendingDependentCollections(j.DependentOrderId).size() > 0) {
                holder.llmain.setBackgroundColor(Color.parseColor(colorYellow));
                holder.llsub.setBackgroundColor(Color.parseColor(colorLightYellow));
            } else if (singleJob && jobtype == 2 && !Delivery.hasPendingDeliveryItems(j.TransportMasterId)) {
                holder.llmain.setBackgroundColor(Color.parseColor(colorYellow));
                holder.llsub.setBackgroundColor(Color.parseColor(colorLightYellow));
            } else if (yellow) {
                holder.llmain.setBackgroundColor(Color.parseColor(colorYellow));
                holder.llsub.setBackgroundColor(Color.parseColor(colorLightYellow));
            } else {
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
        } else {
            holder.llmain.setBackgroundColor(Color.parseColor(colorGreen));
            holder.llsub.setBackgroundColor(Color.parseColor(colorLightGreen));
        }

        final Branch b = branches.get(holder.getAdapterPosition());

        holder.checkBox.setChecked(b.isSelected());

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                b.setSelected(!b.isSelected());
                holder.checkBox.setChecked(b.isSelected());

                if (b.isSelected()) {
                    PrintActivity.list.add(branches.get(holder.getAdapterPosition()).GroupKey);
                } else {
                    PrintActivity.list.remove(branches.get(holder.getAdapterPosition()).GroupKey);
                }
                callBack();
            }
        });
    }

    public void callBack() {
        if (checkBoxListnerCallBack != null) {
            checkBoxListnerCallBack.onChange();
        }
    }


    @Override
    public int getItemCount() {
        return branches.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txt_customer_name, txt_functional_code, txt_pos, txt_start_time, txt_break_time,
                txt_branch_name, txt_remarks, txt_drop_off_branch; //txt_street_tower, txt_town_pin,
        ImageView img_type;
        View llmain, llsub;
        CheckBox checkBox;

        public MyViewHolder(@NonNull View view) {
            super(view);
            checkBox = (CheckBox) view.findViewById(R.id.checkbox_job_list);
            txt_customer_name = (TextView) view.findViewById(R.id.txt_customer_name);
            txt_functional_code = (TextView) view.findViewById(R.id.txt_functional_code);
            txt_remarks = (TextView) view.findViewById(R.id.txt_remarks);
//            txt_street_tower = (TextView) view.findViewById(R.id.txt_street_tower);
//            txt_town_pin = (TextView) view.findViewById(R.id.txt_town_pin);
            txt_branch_name = (TextView) view.findViewById(R.id.txt_branch_name);
            txt_pos = (TextView) view.findViewById(R.id.txt_pos);
            txt_start_time = (TextView) view.findViewById(R.id.txt_start_time);
            txt_break_time = (TextView) view.findViewById(R.id.txt_break_time);
            txt_drop_off_branch = (TextView) view.findViewById(R.id.drop_off_branch);
            img_type = view.findViewById(R.id.img_type);
            llmain = view.findViewById(R.id.llmain);
            llsub = view.findViewById(R.id.llsub);
        }
    }

    public interface CheckBoxListnerCallBack {
        void onChange();
    }

}
