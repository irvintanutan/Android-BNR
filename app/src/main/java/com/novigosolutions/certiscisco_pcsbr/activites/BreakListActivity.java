package com.novigosolutions.certiscisco_pcsbr.activites;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.novigosolutions.certiscisco_pcsbr.R;
import com.novigosolutions.certiscisco_pcsbr.adapters.BreakListAdapter;
import com.novigosolutions.certiscisco_pcsbr.adapters.JobGridAdapter;
import com.novigosolutions.certiscisco_pcsbr.adapters.JobListAdapter;
import com.novigosolutions.certiscisco_pcsbr.interfaces.ApiCallback;
import com.novigosolutions.certiscisco_pcsbr.interfaces.NetworkChangekListener;
import com.novigosolutions.certiscisco_pcsbr.interfaces.RecyclerViewClickListener;
import com.novigosolutions.certiscisco_pcsbr.models.Branch;
import com.novigosolutions.certiscisco_pcsbr.models.Break;
import com.novigosolutions.certiscisco_pcsbr.models.Job;
import com.novigosolutions.certiscisco_pcsbr.recivers.NetworkChangeReceiver;
import com.novigosolutions.certiscisco_pcsbr.service.BreakService;
import com.novigosolutions.certiscisco_pcsbr.utils.CommonMethods;
import com.novigosolutions.certiscisco_pcsbr.utils.NetworkUtil;
import com.novigosolutions.certiscisco_pcsbr.utils.Preferences;
import com.novigosolutions.certiscisco_pcsbr.webservices.APICaller;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class BreakListActivity extends BaseActivity implements RecyclerViewClickListener, ApiCallback, NetworkChangekListener {
    TextView servertime;
    private RecyclerView recyclerView;
    private BreakListAdapter breakListAdapter;
    List<Break> breakList;
    ImageView imgnetwork;
    CardView cardnodata;
    Handler handler;
    Runnable runnable;

    int BreakId = -5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_break_list);
        setuptoolbar();
        servertime = findViewById(R.id.txt_server_time);
        recyclerView = findViewById(R.id.recyclerview);
        cardnodata = findViewById(R.id.cardviewnodata);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(CommonMethods.getServerTimeInms(this));
        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                servertime.setText(CommonMethods.getInFormate(cal.getTime()));
                cal.add(Calendar.MINUTE, 1);
                refresh();
                handler.postDelayed(this, 60000);
            }
        };
        handler.postDelayed(runnable, 0);
    }

    private void setuptoolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Breaks");
        TextView UserName = (TextView) toolbar.findViewById(R.id.UserName);
        UserName.setText(Preferences.getString("UserName", this));
        imgnetwork = (ImageView) toolbar.findViewById(R.id.imgnetwork);
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
        refresh();
    }

    private void refresh() {
        Break.setexpired(BreakListActivity.this);
        if (breakListAdapter == null) {
            breakList = Break.getAllBreak();
            breakListAdapter = new BreakListAdapter(breakList, this);
            recyclerView.setAdapter(breakListAdapter);
        } else {
            breakList.clear();
            breakList.addAll(Break.getAllBreak());
            breakListAdapter.notifyDataSetChanged();
        }
        if (breakList.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            cardnodata.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.GONE);
            cardnodata.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResult(int api_code, int result_code, String result_data) {
        hideProgressDialog();
        if (result_code == 409) {
            authalert(this);
        } else if (result_code == 200) {
            Log.e("break", result_data);
            Break.setConsumed(BreakId);
            Break aBreak = Break.getSingle(BreakId);
            Date EndTime = CommonMethods.getBreakTime(aBreak.EndTime);
            long serverTime = CommonMethods.getServerTimeInms(this);
            if (EndTime != null && EndTime.getTime() > serverTime) {
                Date StartTime = CommonMethods.getBreakTime(aBreak.StartTime);
                if (StartTime != null) {
                    long alarmTime = StartTime.getTime() - serverTime + System.currentTimeMillis();
                    AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    Intent serviceBreak = new Intent(this, BreakService.class);
                    serviceBreak.putExtra("BreakId", BreakId);
                    PendingIntent pi = PendingIntent.getService(this, BreakId, serviceBreak, PendingIntent.FLAG_UPDATE_CURRENT);
                    am.set(AlarmManager.RTC_WAKEUP, alarmTime, pi);

                }
            }
            refresh();
        } else {
            raiseSnakbar("Error" + result_code);
        }
    }

    @Override
    public void recyclerViewListClicked(int BreakId) {
        showProgressDialog("Loading...");
        this.BreakId = BreakId;
        APICaller.instance().ConsumeBreak(this, this, BreakId);
    }


    @Override
    public void onNetworkChanged() {
        if (NetworkUtil.getConnectivityStatusString(this))
            imgnetwork.setImageResource(R.drawable.network);
        else
            imgnetwork.setImageResource(R.drawable.no_network);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (runnable != null) handler.removeCallbacks(runnable);
        handler = null;
        runnable = null;
    }
}
