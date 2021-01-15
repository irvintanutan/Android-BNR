package com.novigosolutions.certiscisco_pcsbr.activites;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.novigosolutions.certiscisco_pcsbr.R;
import com.novigosolutions.certiscisco_pcsbr.interfaces.NetworkChangekListener;
import com.novigosolutions.certiscisco_pcsbr.models.Branch;
import com.novigosolutions.certiscisco_pcsbr.models.Job;
import com.novigosolutions.certiscisco_pcsbr.recivers.NetworkChangeReceiver;
import com.novigosolutions.certiscisco_pcsbr.utils.CommonMethods;
import com.novigosolutions.certiscisco_pcsbr.utils.NetworkUtil;

public class ConfirmationActivity extends AppCompatActivity implements NetworkChangekListener {
    TextView txtJobId,txtCustomerName,txtBranchName,txtAddress,txtTime,txtDropOff,txtDropAddress;
    Button btnConfirm;
    int TransportMasterId,jobType = 0;
    Intent intent;
    String GroupKey;
    ImageView imgnetwork;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);
        setuptoolbar();
        initializer();
        fetchIntentData();
        checkJobType();
        clickLister();
    }

    private void setuptoolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("CONFIRMATION");
        imgnetwork = (ImageView) toolbar.findViewById(R.id.imgnetwork);
    }

    private void fetchIntentData() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            TransportMasterId = extras.getInt("TransportMasterId");
            GroupKey = extras.getString("GroupKey");
        }
    }

    private void initializer() {
        txtJobId = findViewById(R.id.txt_job_id);
        txtCustomerName = findViewById(R.id.txt_customer_name);
        txtBranchName = findViewById(R.id.txt_branch_name);
        txtAddress = findViewById(R.id.txt_address);
        txtTime = findViewById(R.id.txt_time);
        txtDropOff = findViewById(R.id.txt_drop_off);
        txtDropAddress = findViewById(R.id.txt_drop_adress);
        btnConfirm = findViewById(R.id.btn_confirm);
    }

    private void checkJobType(){
        Job job = Job.getSingle(TransportMasterId);
        Branch branch = Branch.getSingle(job.GroupKey);
        if(job.IsCollectionOrder)
            jobType = 1;
        else if (job.IsFloatDeliveryOrder)
            jobType = 2;
        else
            jobType = 0;


        if(jobType == 1){
            txtJobId.setText("Job ID:"+job.OrderNo);
            txtBranchName.setText(branch.BranchCode);
            String address = "";
            if (!TextUtils.isEmpty(job.BranchStreetName) && job.BranchStreetName != null){
                address += job.BranchStreetName+" , ";
            }
            if (!TextUtils.isEmpty(job.BranchTower) && job.BranchTower != null){
                address += job.BranchTower+", ";
            }
            if (!TextUtils.isEmpty(job.BranchTown) && job.BranchTown != null){
                address += job.BranchTown+" , ";
            }
            if (!TextUtils.isEmpty(job.BranchPinCode) && job.BranchPinCode != null){
                address += job.BranchPinCode;
            }
            txtAddress.setText("Address: "+address);

        }else if (jobType == 2){
            txtJobId.setText("Job ID: "+Job.getDeliveryOrderNos(GroupKey));
            txtBranchName.setVisibility(View.GONE);
            txtAddress.setVisibility(View.GONE);
        }


        if (!TextUtils.isEmpty(job.BranchCode)) {
            txtDropOff.setText("Drop Off :\n"+job.BranchCode);
        }
        String dropOffAddress = "";
        if (!TextUtils.isEmpty(job.StreetName) && job.StreetName != null){
            dropOffAddress += job.StreetName+", ";
        }
        if (!TextUtils.isEmpty(job.Tower) && job.Tower != null){
            dropOffAddress += job.Tower+",";
        }
        if (!TextUtils.isEmpty(job.Town) && job.Town != null){
            dropOffAddress += job.Town+",";
        }
        if (!TextUtils.isEmpty(job.PinCode) && job.PinCode != null){
            dropOffAddress += job.PinCode;
        }

        txtDropAddress.setText("Address: "+dropOffAddress);
        txtCustomerName.setText(branch.CustomerName);
        txtTime.setText("Window time :"+CommonMethods.getStartTime(branch.StartTime) + " - " + CommonMethods.getStartTime(branch.EndTime));
    }

    private void clickLister() {
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (jobType == 0){
                    showToast("Something went wrong");
                }else if (jobType == 1) {
                    intent = new Intent(ConfirmationActivity.this, CollectionDetailActivity.class);
                    intent.putExtra("TransportMasterId", TransportMasterId);
                    intent.putExtra("GroupKey", GroupKey);
                }else if(jobType == 2){
                    intent = new Intent(ConfirmationActivity.this, DeliveryActivity.class);
                    intent.putExtra("TransportMasterId", TransportMasterId);
                    intent.putExtra("GroupKey", GroupKey);
                }
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        NetworkChangeReceiver.changekListener = this;
        onNetworkChanged();
    }

    @Override
    public void onNetworkChanged() {
        if (NetworkUtil.getConnectivityStatusString(this))
            imgnetwork.setImageResource(R.drawable.network);
        else
            imgnetwork.setImageResource(R.drawable.no_network);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void showToast(String msg){
        Toast.makeText(ConfirmationActivity.this,""+msg,Toast.LENGTH_SHORT).show();
    }

}