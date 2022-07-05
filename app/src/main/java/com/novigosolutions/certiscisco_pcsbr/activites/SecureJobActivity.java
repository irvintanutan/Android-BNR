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

import com.google.android.material.tabs.TabLayout;
import com.novigosolutions.certiscisco_pcsbr.R;
import com.novigosolutions.certiscisco_pcsbr.adapters.GroupGridAdapter;
import com.novigosolutions.certiscisco_pcsbr.adapters.GroupListAdapter;
import com.novigosolutions.certiscisco_pcsbr.adapters.ViewPagerAdapterSecure;
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
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;

public class SecureJobActivity extends BaseActivity implements ApiCallback, OfflineCallback, NetworkChangekListener {

    String status = "";
    CardView cardnodata;
    protected MenuItem refreshItem = null;
    protected MenuItem gridItem = null;
    protected MenuItem print = null;

    ImageView imgnetwork;
    CoordinatorLayout cl;
    boolean isgridview = false;
    Handler handler;
    Runnable runnable;

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private ViewPagerAdapterSecure viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secure_job);
        initializeviews();
        setuptoolbar();
    }

    private void setuptoolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
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
        cardnodata = (CardView) findViewById(R.id.cardviewnodata);
        cl = (CoordinatorLayout) findViewById(R.id.cl);
        tabLayout = findViewById(R.id.tabs);
        viewPager = findViewById(R.id.viewpager);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            status = extras.getString("status");
        }
        isgridview = Preferences.getBoolean("isgridview", this);

        viewPagerAdapter = new ViewPagerAdapterSecure(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);


        final TabLayout.Tab secure = tabLayout.newTab();
        final TabLayout.Tab unsecure = tabLayout.newTab();

        secure.setText("Secured");
        unsecure.setText("Yet To Secure");

        tabLayout.addTab(secure, 0);
        tabLayout.addTab(unsecure, 1);

        tabLayout.setTabTextColors(ContextCompat.getColorStateList(this, R.color.colorPrimary));
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.white));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }

        });

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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
        gridItem = menu.findItem(R.id.action_grid);
        gridItem.setVisible(false);
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
