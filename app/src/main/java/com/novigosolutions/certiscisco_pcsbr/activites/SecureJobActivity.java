package com.novigosolutions.certiscisco_pcsbr.activites;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.novigosolutions.certiscisco_pcsbr.R;
import com.novigosolutions.certiscisco_pcsbr.adapters.GroupGridAdapter;
import com.novigosolutions.certiscisco_pcsbr.adapters.GroupListAdapter;
import com.novigosolutions.certiscisco_pcsbr.interfaces.ApiCallback;
import com.novigosolutions.certiscisco_pcsbr.interfaces.NetworkChangekListener;
import com.novigosolutions.certiscisco_pcsbr.interfaces.OfflineCallback;
import com.novigosolutions.certiscisco_pcsbr.interfaces.RecyclerViewClickListener2;
import com.novigosolutions.certiscisco_pcsbr.models.Branch;
import com.novigosolutions.certiscisco_pcsbr.models.Delivery;
import com.novigosolutions.certiscisco_pcsbr.models.Job;
import com.novigosolutions.certiscisco_pcsbr.models.Reschedule;
import com.novigosolutions.certiscisco_pcsbr.recivers.NetworkChangeReceiver;
import com.novigosolutions.certiscisco_pcsbr.utils.CommonMethods;
import com.novigosolutions.certiscisco_pcsbr.utils.NetworkUtil;
import com.novigosolutions.certiscisco_pcsbr.utils.Preferences;
import com.novigosolutions.certiscisco_pcsbr.webservices.APICaller;

import java.util.List;

import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;

public class SecureJobActivity extends BaseActivity implements RecyclerViewClickListener2, ApiCallback, OfflineCallback, NetworkChangekListener {
    private RecyclerView recyclerView;
    private GroupListAdapter listAdapter;
    private GroupGridAdapter gridAdapter;
    String status = "";
    List<Branch> jobList;
    CardView cardnodata;
    protected MenuItem refreshItem = null;
    protected MenuItem print = null;
    ImageView imgnetwork;
    CoordinatorLayout cl;
    boolean isgridview = false;
    Handler handler;
    Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secure_job);
        initializeviews();
        setuptoolbar();
    }

    private void setuptoolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Scan to Secure");
        TextView UserName = (TextView) toolbar.findViewById(R.id.UserName);
        UserName.setText(Preferences.getString("UserName", this));
        imgnetwork = (ImageView) toolbar.findViewById(R.id.imgnetwork);
    }

    private void initializeviews() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        cardnodata = (CardView) findViewById(R.id.cardviewnodata);
        cl = (CoordinatorLayout) findViewById(R.id.cl);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            status = extras.getString("status");
        }
        isgridview = Preferences.getBoolean("isgridview", this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void recyclerViewListClicked(String GroupKey) {
        gotoprocessjob(GroupKey);
    }

    private void gotoprocessjob(String GroupKey) {
        Branch branch = Branch.getSingle(GroupKey);
        int branchType = 0;
        if ("ALL".equals(status)) {
            branchType = branch.getBranchJobType();
        } else {
            branchType = branch.getBranchJobTypeByStatus(status);
        }
        branch.updateJobStartTime(CommonMethods.getCurrentDateTime(this));
        Intent intent = null;
        List<Job> jobsList;
        if ("ALL".equals(status)) {
            jobsList = Job.getByGroupKey(GroupKey);
        } else {
            jobsList = Job.getByGroupKeyAndStatus(GroupKey, status);
        }
        Job j = jobsList.get(0);
        int jobtype = 0;
        if (j.IsCollectionOrder && j.IsFloatDeliveryOrder) {
            jobtype = 3;
        } else if (j.IsFloatDeliveryOrder) {
            jobtype = 2;
        } else if (j.IsCollectionOrder) {
            jobtype = 1;
        }

        Log.e("GROUPKEY", GroupKey);

        if (branchType != 2 && jobsList.size() > 1) {
            intent = new Intent(SecureJobActivity.this, JobListActivity.class);
            intent.putExtra("GroupKey", GroupKey);
            intent.putExtra("status", status);
        } else if (j.Status.equals("COMPLETED") || (jobtype == 1 && j.isOfflineSaved) || (jobtype == 2 && (j.isOfflineSaved || Reschedule.isOfflineRescheduled(GroupKey))) || (branchType == 3 && j.isOfflineSaved && Reschedule.isOfflineRescheduled(GroupKey))) {
            intent = new Intent(SecureJobActivity.this, SummaryActivity.class);
            intent.putExtra("TransportMasterId", j.TransportMasterId);
            intent.putExtra("GroupKey", GroupKey);
            intent.putExtra("summaryType", jobtype);
            intent.putExtra("isDelivery", j.IsFloatDeliveryOrder ? 1 : 0);
            intent.putExtra("isCollection", j.IsCollectionOrder ? 1 : 0);
            intent.putExtra("isSummary", true);
        } else if (jobtype == 1) {
//            || (jobtype == 3 && (Job.getPendingDeliveryJobsOfPoint(GroupKey).size() == 0 || branch.isDelOffline))

            Job.updateJobStartTime(j.TransportMasterId, CommonMethods.getCurrentDateTime(this));
            intent = new Intent(SecureJobActivity.this, ConfirmationActivity.class);
            intent.putExtra("TransportMasterId", j.TransportMasterId);
            intent.putExtra("GroupKey", GroupKey);
            Preferences.saveString("PROGRESSGROUPKEY", GroupKey, this);
        } else if (jobtype == 2 || jobtype == 3) {
            int tmp = 0;
            for (Job jo : jobsList) {
                if (!TextUtils.isEmpty(jo.DependentOrderId) && Job.checkPendingDependentCollections(jo.DependentOrderId).size() > 0) {
                    tmp = 1;
                } else if (!Delivery.hasPendingDeliveryItems(jo.TransportMasterId)) {
                    tmp = 2;
                } else {
                    tmp = 0;
                    break;
                }
            }

            if (tmp == 1) {
                alert("Can not perform delivery since item(s) are not collected from pick-up location");
            } else if (tmp == 2) {
                alert("No items to deliver");
            } else {
                Job.updateDeliveryJobsStartTime(GroupKey, CommonMethods.getCurrentDateTime(this));
                intent = new Intent(SecureJobActivity.this, ConfirmationActivity.class);
                intent.putExtra("TransportMasterId", j.TransportMasterId);
                intent.putExtra("GroupKey", GroupKey);
            }

            Preferences.saveString("PROGRESSGROUPKEY", GroupKey, this);
        }
        if (intent != null) {
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        NetworkChangeReceiver.changekListener = this;
        onNetworkChanged();
        IntentFilter filter = new IntentFilter(CONNECTIVITY_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        this.registerReceiver(offlineupdateReceiver, filter);
        LocalBroadcastManager.getInstance(this).registerReceiver(syncReceiver,
                new IntentFilter("syncreciverevent"));
        Preferences.saveInt("PROGRESSPOINTID", -5, this);
        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                refresh();
                handler.postDelayed(this, 60000);
            }
        };
        handler.postDelayed(runnable, 0);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Preferences.saveInt("PROGRESSPOINTID", -5, this);
    }


    private BroadcastReceiver offlineupdateReceiver = new NetworkChangeReceiver();

    @Override
    protected void onPause() {
        // Unregister since the activity is not visible
        super.onPause();
        Preferences.saveBoolean("isgridview", isgridview, this);
        this.unregisterReceiver(offlineupdateReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(syncReceiver);
        if (runnable != null) handler.removeCallbacks(runnable);
        handler = null;
        runnable = null;
    }


    private BroadcastReceiver syncReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refresh();
        }
    };

    private List<Branch> getJobList() {
        switch (status) {
            case "PENDING":
//                return Branch.getBranchesByStatus("PENDING");
                return Branch.getPendingBranches();
            case "COMPLETED":
//                return Branch.getBranchesByStatus("COMPLETED");
                return Branch.getCompletedBranches();
            default:
                return Branch.getAllBranches();
        }
    }

    private void refresh() {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.grid_sync, menu);
        setRefreshItem(menu.findItem(R.id.action_sync));
        refreshItem.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(refreshItem);
            }
        });
        print = menu.findItem(R.id.action_print);
        if (status.equals("COMPLETED"))
            print.setVisible(true);
        else
            print.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_sync) {
            if (NetworkUtil.getConnectivityStatusString(this)) {
                APICaller.instance().sync(this, this);
                refreshItem.setVisible(false);
                runRefresh();
            } else {
                raiseInternetSnakbar();
            }
        } else if (item.getItemId() == R.id.action_grid) {
            if (isgridview) {
                item.setIcon(R.drawable.icon_grid);
                listAdapter = null;
            } else {
                item.setIcon(R.drawable.icon_list);
                gridAdapter = null;
            }
            isgridview = !isgridview;
            refresh();
        } else if (item.getItemId() == R.id.action_print) {
            printAll();
        }
        return super.onOptionsItemSelected(item);
    }

    protected void setRefreshItem(MenuItem item) {
        refreshItem = item;
    }

    protected void stopRefresh() {
        if (refreshItem != null) {
            refreshItem.getActionView().clearAnimation();
        }
    }

    protected void runRefresh() {
        if (refreshItem != null) {
            Animation rotation = AnimationUtils.loadAnimation(this, R.anim.clockwise_refresh);
            rotation.setRepeatCount(Animation.INFINITE);
            refreshItem.getActionView().startAnimation(rotation);
        }
    }

    private void printAll() {
        // List<Job> competedJobCount = Job.getCompletedJobsByStatus();
        List<Branch> completedBranchCount = Branch.getCompletedBranches();
        if (completedBranchCount.size() == 0) {
            Toast.makeText(SecureJobActivity.this, "No completed jobs to print", Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(SecureJobActivity.this, PrintActivity.class);
            //  intent.putExtra("status"," SINGLE JOBS");
            intent.putExtra("status", "COMPLETED");
            intent.putExtra("transporterMasterId", 123);
            startActivity(intent);
        }
    }


    @Override
    public void onNetworkChanged() {
        if (NetworkUtil.getConnectivityStatusString(this))
            imgnetwork.setImageResource(R.drawable.network);
        else
            imgnetwork.setImageResource(R.drawable.no_network);
    }

    @Override
    public void onOfflineUpdated(int result, String resultdata) {
        hideProgressDialog();
        Toast.makeText(SecureJobActivity.this, resultdata, Toast.LENGTH_SHORT).show();
        refresh();
    }

    @Override
    public void onResult(int api_code, int result_code, String result_data) {
        stopRefresh();
        refreshItem.setVisible(true);
        if (result_code == 409) {
            authalert(this);
        } else if (result_code == 200) {
            refresh();
        } else {
            raiseSnakbar("Error");
        }
    }

    private void alert(String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setCancelable(false);
        alertDialog.setTitle("No Items");
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.show();
    }
}
