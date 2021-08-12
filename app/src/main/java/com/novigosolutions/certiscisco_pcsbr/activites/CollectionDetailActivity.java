package com.novigosolutions.certiscisco_pcsbr.activites;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.novigosolutions.certiscisco_pcsbr.R;
import com.novigosolutions.certiscisco_pcsbr.adapters.CollectionDetailAdapter;
import com.novigosolutions.certiscisco_pcsbr.interfaces.NetworkChangekListener;
import com.novigosolutions.certiscisco_pcsbr.interfaces.RecyclerViewClickListener;
import com.novigosolutions.certiscisco_pcsbr.models.Branch;
import com.novigosolutions.certiscisco_pcsbr.models.Job;
import com.novigosolutions.certiscisco_pcsbr.recivers.NetworkChangeReceiver;
import com.novigosolutions.certiscisco_pcsbr.utils.Constants;
import com.novigosolutions.certiscisco_pcsbr.utils.NetworkUtil;
import com.novigosolutions.certiscisco_pcsbr.utils.Preferences;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CollectionDetailActivity extends BaseActivity implements RecyclerViewClickListener, NetworkChangekListener {
    TextView txt_customer_name, txt_functional_code, txt_branch_name, txt_order_remarks; // txt_street_tower, txt_town_pin;
    //    int PointId;
    String GroupKey;
    int TransportMasterId;
    private RecyclerView recyclerView;
    private CollectionDetailAdapter mAdapter;
    Button btn_submit;
    ImageView imgnetwork;
    List<Job> jobs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_detail);
        setuptoolbar();
        txt_customer_name = (TextView) findViewById(R.id.txt_customer_name);
        txt_functional_code = (TextView) findViewById(R.id.txt_functional_code);
//        txt_street_tower = (TextView) findViewById(R.id.txt_street_tower);
//        txt_town_pin = (TextView) findViewById(R.id.txt_town_pin);
        txt_branch_name = (TextView) findViewById(R.id.txt_txt_branch_name);
        txt_order_remarks = (TextView) findViewById(R.id.txt_order_remarks);
        recyclerView = findViewById(R.id.recyclerview);
        btn_submit = findViewById(R.id.btn_submit);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
//            PointId = extras.getInt("PointId");
            GroupKey = extras.getString("GroupKey");
            TransportMasterId = extras.getInt("TransportMasterId");
        }
        Branch branch = Branch.getSingle(GroupKey);
        Job job = Job.getSingle(TransportMasterId);
        txt_customer_name.setText(branch.CustomerName);
//        txt_functional_code.setText(Job.getAllOrderNos(branch.PointId));
        txt_functional_code.setText(Job.getAllOrderNos(GroupKey, job.BranchCode , job.PFunctionalCode , "PENDING", job.PDFunctionalCode));
        txt_branch_name.setText(branch.BranchCode);
        if (!TextUtils.isEmpty(Job.getSingle(TransportMasterId).OrderRemarks)) {
            txt_order_remarks.setVisibility(View.VISIBLE);
            txt_order_remarks.setText("Remarks: " + Job.getSingle(TransportMasterId).OrderRemarks);
        } else {
            txt_order_remarks.setVisibility(View.GONE);
        }
//        String street_tower = branch.StreetName;
//        if (!branch.Tower.isEmpty()) {
//            if (street_tower.isEmpty()) street_tower = branch.Tower;
//            else street_tower = street_tower + ", " + branch.Tower;
//        }
//        txt_street_tower.setText(street_tower);
//        String town_pin = branch.Town;
//        if (!branch.PinCode.isEmpty()) {
//            if (town_pin.isEmpty()) town_pin = branch.PinCode;
//            else town_pin = town_pin + ", " + branch.PinCode;
//        }
//        txt_town_pin.setText(town_pin);

//        jobs = Job.getCollectionJobsOfPoint(GroupKey);
        jobs = new ArrayList<>();
        jobs.add(Job.getSingle(TransportMasterId));
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                List<Job> tempjobs = Job.getCollectionJobsOfPoint(GroupKey);
                Boolean iscomplete = true;
                Job j = Job.getSingle(TransportMasterId);
//                for (int i = 0; i < tempjobs.size(); i++) {
                if (!j.isNoCollection && !Job.isCollected(j.TransportMasterId) && !j.Status.equals("COMPLETED")) {
                    iscomplete = false;
//                        break;
                }
//                }
                if (iscomplete) {
                    Intent intent = new Intent(CollectionDetailActivity.this, SummaryActivity.class);
//                    intent.putExtra("PointId", PointId);
                    intent.putExtra("TransportMasterId", TransportMasterId);
                    intent.putExtra("GroupKey", GroupKey);
                    intent.putExtra("summaryType", Constants.COLLECTION);
                    intent.putExtra("isSummary", false);
                    startActivityForResult(intent, 000);
                } else {
                    Toast.makeText(CollectionDetailActivity.this, "Job is incomplete", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setuptoolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("COLLECTION");
        TextView UserName = (TextView) toolbar.findViewById(R.id.UserName);
        UserName.setText(Preferences.getString("UserName", this));
        imgnetwork = (ImageView) toolbar.findViewById(R.id.imgnetwork);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAdapter == null) {
            mAdapter = new CollectionDetailAdapter(jobs, this, this);
            recyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
        NetworkChangeReceiver.changekListener = this;
        onNetworkChanged();
    }

    @Override
    public void recyclerViewListClicked(int TransportMasterId) {
        Intent intent = new Intent(this, CollectionActivity.class);
        intent.putExtra("TransportMasterId", TransportMasterId);
        startActivityForResult(intent, 000);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        alert();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        Log.e("resultCode", ":" + resultCode);
        if (resultCode == Constants.FINISHONRESULT) //matches the result code passed from B
        {
            finish();
        }
    }

    @Override
    public void onNetworkChanged() {
        if (NetworkUtil.getConnectivityStatusString(this))
            imgnetwork.setImageResource(R.drawable.network);
        else
            imgnetwork.setImageResource(R.drawable.no_network);
    }

    private void alert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Confirm");
        alertDialog.setMessage("Are you sure you want to go back?\nCurrent progress will be lost.");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Job.clearBranchCollecion(GroupKey);
                finish();
            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.show();
    }
}
