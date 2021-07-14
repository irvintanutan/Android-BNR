package com.novigosolutions.certiscisco_pcsbr.adapters;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.novigosolutions.certiscisco_pcsbr.R;
import com.novigosolutions.certiscisco_pcsbr.activites.PrintActivity;
import com.novigosolutions.certiscisco_pcsbr.activites.PrintSelectedJobActivity;
import com.novigosolutions.certiscisco_pcsbr.models.Branch;
import com.novigosolutions.certiscisco_pcsbr.models.Delivery;
import com.novigosolutions.certiscisco_pcsbr.models.Job;
import com.novigosolutions.certiscisco_pcsbr.utils.CommonMethods;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PrintSelectedJobListAdapter extends RecyclerView.Adapter<PrintSelectedJobListAdapter.MyViewHolder> {

    CheckBoxListnerCallBack checkBoxListnerCallBack;
    List<Job> branches;
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
    int isDelivered;

    public PrintSelectedJobListAdapter(List<Job> branches, String status, Context context, CheckBoxListnerCallBack checkBoxListnerCallBack, int isDelivered) {
        this.branches = branches;
        this.context = context;
        this.status = status;
        serverTime = CommonMethods.getServerTimeInms(context);
        this.checkBoxListnerCallBack = checkBoxListnerCallBack;
        this.isDelivered = isDelivered;

        PrintSelectedJobActivity.list.clear();
    }

    @NonNull
    @Override
    public PrintSelectedJobListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.print_job_list, parent, false);
        return new PrintSelectedJobListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final PrintSelectedJobListAdapter.MyViewHolder holder, int position) {

        boolean singleJob = true;
        Job j = branches.get(position);
        Branch branch = Branch.getSingle(j.GroupKey);
        int jobtype;
        if (this.isDelivered == 1) {
            jobtype = 2;
        } else  {
            jobtype = 1;
        } 

        holder.txt_customer_name.setText(j.CustomerName);

        if(j.IsFloatDeliveryOrder){
            holder.txt_functional_code.setText(Job.getAllDeliveryOrderNos(j.GroupKey));
        } else{
            holder.txt_functional_code.setText(Job.getSingle(j.TransportMasterId).OrderNo);
        }

        if (singleJob) {
            if (!TextUtils.isEmpty(j.SequenceNo) && !j.SequenceNo.equalsIgnoreCase("null"))
                holder.txt_pos.setText(j.SequenceNo);
            else
                holder.txt_pos.setText("");
            holder.txt_break_time.setText(j.ClientBreak);
            if (jobtype == 1) {
                holder.txt_branch_name.setText("Pick Up : " + branch.BranchCode);// + " (" + j.FunctionalCode + ")");
            } else if (jobtype == 2) {
                holder.txt_branch_name.setText("Drop Off : " + branch.BranchCode);
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
                //  jobtype = j.getBranchJobType();
            } else {
                //   jobtype = j.getBranchJobTypeByStatus(status);
            }
        }

        switch (jobtype) {
            case 1:
                holder.img_type.setImageResource(R.drawable.ic_collection);
                break;
            case 2:
                holder.img_type.setImageResource(R.drawable.ic_delivery);
                break;
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
        }

        holder.llmain.setBackgroundColor(Color.parseColor(colorGreen));
        holder.llsub.setBackgroundColor(Color.parseColor(colorLightGreen));

        final Job b = branches.get(holder.getAdapterPosition());

        holder.checkBox.setChecked(b.isSelected());

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                b.setSelected(!b.isSelected());
                holder.checkBox.setChecked(b.isSelected());

                if(b.isSelected()) {
                    PrintSelectedJobActivity.list.add(j);
                } else {
                    PrintSelectedJobActivity.list.remove(j);
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
