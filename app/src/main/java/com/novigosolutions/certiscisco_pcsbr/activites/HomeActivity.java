package com.novigosolutions.certiscisco_pcsbr.activites;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.novigosolutions.certiscisco_pcsbr.R;
import com.novigosolutions.certiscisco_pcsbr.adapters.GridAdapter;
import com.novigosolutions.certiscisco_pcsbr.applications.CertisCISCO;
import com.novigosolutions.certiscisco_pcsbr.interfaces.ApiCallback;
import com.novigosolutions.certiscisco_pcsbr.interfaces.NetworkChangekListener;
import com.novigosolutions.certiscisco_pcsbr.models.Branch;
import com.novigosolutions.certiscisco_pcsbr.models.Break;
import com.novigosolutions.certiscisco_pcsbr.models.ChatMessage;
import com.novigosolutions.certiscisco_pcsbr.models.Job;
import com.novigosolutions.certiscisco_pcsbr.models.Reschedule;
import com.novigosolutions.certiscisco_pcsbr.recivers.NetworkChangeReceiver;
import com.novigosolutions.certiscisco_pcsbr.service.AuditService;
import com.novigosolutions.certiscisco_pcsbr.service.BreakService;
import com.novigosolutions.certiscisco_pcsbr.service.SignalRService;
import com.novigosolutions.certiscisco_pcsbr.utils.CommonMethods;
import com.novigosolutions.certiscisco_pcsbr.utils.Constants;
import com.novigosolutions.certiscisco_pcsbr.utils.NetworkUtil;
import com.novigosolutions.certiscisco_pcsbr.utils.Preferences;
import com.novigosolutions.certiscisco_pcsbr.webservices.APICaller;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;
import static com.novigosolutions.certiscisco_pcsbr.utils.Constants.ISCHANGEPASSWORD;
import static com.novigosolutions.certiscisco_pcsbr.utils.Constants.MIN_PASSWORD_LENGTH;
import static com.novigosolutions.certiscisco_pcsbr.utils.Constants.SYSTEMCONFIG;

public class HomeActivity extends BaseActivity implements ApiCallback, NetworkChangekListener {
    GridView gridview;
    GridAdapter gridadapter;
    List<Integer> countList;
    protected MenuItem refreshItem = null, menulegend;
    CoordinatorLayout cl;
    LinearLayout legend;
    ImageView imgnetwork;
    Boolean legendisopen = false;
    TextView mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setuptoolbar();
        initializeviews();
        setactions();
        startBreaks();
        startSignaRifNotRunning();
    }

    private void calculateSecureTime() {
        long seconds = (System.currentTimeMillis() - Constants.secureTimer) / 1000;
        Log.e("TIME", Long.toString(seconds));
        if (seconds > 360 && Job.getUnSecuredJobs().size() > 0) {
            Constants.secureTimer = System.currentTimeMillis();
            Constants.showSecureAlert = true;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void secureVehicleAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("You have " + Job.getAllJobsSecureVehicle().stream().filter(job -> job.IsSecured == false).collect(Collectors.toList()).size()
                + " jobs pending to be secured in Vehicle!!!");
        alertDialog.setPositiveButton("Done", (dialog, which) -> Constants.showSecureAlert = false);

        alertDialog.show();
    }

    private void startBreaks() {
        int breakId = Break.getOnGoingBreak(this);
        if (breakId > 0) {
            Break aBreak = Break.getSingle(breakId);
            Date StartTime = CommonMethods.getBreakTime(aBreak.StartTime);
            if (StartTime != null) {
                long alarmTime = StartTime.getTime() - CommonMethods.getServerTimeInms(this) + System.currentTimeMillis();
                AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                Intent serviceBreak = new Intent(this, BreakService.class);
                serviceBreak.putExtra("BreakId", breakId);
                PendingIntent pi = PendingIntent.getService(this, breakId, serviceBreak, PendingIntent.FLAG_UPDATE_CURRENT);
                am.set(AlarmManager.RTC_WAKEUP, alarmTime, pi);

            }
        }
    }

    private void startSignaRifNotRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (SignalRService.class.getName().equals(service.service.getClassName())) {
                return;
            }
        }
        startService(new Intent(getApplicationContext(), SignalRService.class));
        startService(new Intent(getApplicationContext(), AuditService.class));
    }

    private void setuptoolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mTitle = toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("HOME");
        TextView UserName = toolbar.findViewById(R.id.UserName);
        UserName.setText(Preferences.getString("UserName", HomeActivity.this));
        imgnetwork = toolbar.findViewById(R.id.imgnetwork);
    }

    private void initializeviews() {
        cl = findViewById(R.id.cl);
        gridview = findViewById(R.id.grid_view);
        legend = findViewById(R.id.legend);
        if (NetworkUtil.getConnectivityStatusString(HomeActivity.this))
            APICaller.instance().GetMessages(this);
    }

    private void setactions() {
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Intent intent = null;
                switch (position) {
                    case 0:
                        intent = new Intent(HomeActivity.this, SelectedJobListActivity.class);
                        intent.putExtra("isCollection", 1);
                        intent.putExtra("isDelivered", 0);
                        intent.putExtra("status", "ALL");
                        Constants.BackDestination = "ALL";
                        Constants.isAll = true;
                        break;
                    case 1:
                        intent = new Intent(HomeActivity.this, GroupJobActivity.class);
                        intent.putExtra("status", "PENDING");
                        Constants.BackDestination = "PENDING";
                        Constants.isAll = false;
                        break;
                    case 2:
                        intent = new Intent(HomeActivity.this, SelectionActivity.class);
                        intent.putExtra("status", "COMPLETED");
                        Constants.isAll = false;
                        break;
                    case 3:
                        intent = new Intent(HomeActivity.this, ChatActivity.class);
                        break;
                    case 4:
                        intent = new Intent(HomeActivity.this, SecureJobActivity.class);
                        Constants.BackDestination = "PENDING";
                        Constants.isAll = false;
                        break;
                    case 5:
                        intent = new Intent(HomeActivity.this, BreakListActivity.class);
                        break;

                    case 6:
                        intent = new Intent(HomeActivity.this, PrinterConfigurationActivity.class);
                        break;

                    default:
                        break;
                }
                if (intent != null) startActivity(intent);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onResume() {
        super.onResume();
        CertisCISCO.setCurrentactvity(this);
        refresh();
        NetworkChangeReceiver.changekListener = this;
        onNetworkChanged();
        IntentFilter filter = new IntentFilter(CONNECTIVITY_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        this.registerReceiver(offlineupdateReceiver, filter);
        LocalBroadcastManager.getInstance(this).registerReceiver(syncReceiver,
                new IntentFilter("syncreciverevent"));
        LocalBroadcastManager.getInstance(this).registerReceiver(syncReceiver,
                new IntentFilter("breakreciverevent"));
//        LocalBroadcastManager.getInstance(this).registerReceiver(offlineupdateReceiver,
//                new IntentFilter("offlinereciverevent"));
        LocalBroadcastManager.getInstance(this).registerReceiver(msgReceiver,
                new IntentFilter("mesagereciverevent"));

        calculateSecureTime();
        if (Constants.showSecureAlert)
            secureVehicleAlert();
        startService(new Intent(getApplicationContext(), AuditService.class));
        APICaller.instance().getSystemConfig(this);
        APICaller.instance().getPasswordDate(this, getApplicationContext());
    }

    @Override
    protected void onPause() {
        super.onPause();
        CertisCISCO.setCurrentactvity(null);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(syncReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(breakReceiver);
        this.unregisterReceiver(offlineupdateReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(msgReceiver);
    }

    private void closeLegend() {
        TranslateAnimation animate = new TranslateAnimation(0, legend.getWidth(), 0, -legend.getHeight());
        animate.setDuration(100);
        legend.startAnimation(animate);
        legend.setVisibility(View.GONE);
        menulegend.setIcon(R.drawable.ic_baseline_more_horiz_24);
        mTitle.setText("Home");
        legendisopen = false;
    }

    private void openLegend() {
        legend.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(legend.getWidth(), 0, -legend.getHeight(), 0);
        animate.setDuration(100);
        legend.startAnimation(animate);
        menulegend.setIcon(R.drawable.ic_close);
        mTitle.setText("Legend");
        legendisopen = true;
    }

    private BroadcastReceiver syncReceiver = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onReceive(Context context, Intent intent) {
            refresh();
        }
    };
    private BroadcastReceiver offlineupdateReceiver = new NetworkChangeReceiver();

    private BroadcastReceiver msgReceiver = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onReceive(Context context, Intent intent) {
            refresh();
        }
    };

    private BroadcastReceiver breakReceiver = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onReceive(Context context, Intent intent) {
            refresh();
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void refresh() {
        if (gridadapter == null) {
            countList = new ArrayList<Integer>();
            countList.add(Branch.getAllCount());
            countList.add(Branch.getCountByStatus("PENDING"));
            countList.add(Branch.getCountByStatus("COMPLETED"));
            countList.add(ChatMessage.getUnreadMessages().size());
            countList.add(Job.getAllJobsSecureVehicle().stream().filter(job -> job.IsSecured == false).collect(Collectors.toList()).size());
            countList.add(Break.getPendingBreak().size());
            countList.add(0);
            gridadapter = new GridAdapter(HomeActivity.this, countList);
            gridview.setAdapter(gridadapter);
        } else {
            countList.clear();
            countList.add(Branch.getAllCount());
            countList.add(Branch.getCountByStatus("PENDING"));
            countList.add(Branch.getCountByStatus("COMPLETED"));
            countList.add(ChatMessage.getUnreadMessages().size());
            countList.add(Job.getAllJobsSecureVehicle().stream().filter(job -> job.IsSecured == false).collect(Collectors.toList()).size());
            countList.add(Break.getPendingBreak().size());
            countList.add(0);
            gridadapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        this.menulegend = menu.findItem(R.id.action_settings);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logout_sync, menu);
        setRefreshItem(menu.findItem(R.id.action_sync));
        refreshItem.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(refreshItem);
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_sync) {
            if (NetworkUtil.getConnectivityStatusString(this)) {
                refreshItem.setVisible(false);
                APICaller.instance().sync(this, this);
                runRefresh();
            } else {
                raiseInternetSnakbar();
            }
        } else if (item.getItemId() == R.id.action_settings) {
            if (legendisopen) closeLegend();
        } else if (item.getItemId() == R.id.action_legend) {
            openLegend();
        } else if (item.getItemId() == R.id.action_logout) {
            logout();
        } else if (item.getItemId() == R.id.action_change_password) {
            changePassword();
        }
        return super.onOptionsItemSelected(item);
    }

    private void changePassword() {
        startActivity(new Intent(HomeActivity.this, ChangePasswordActivity.class));
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (legendisopen)
            closeLegend();
        else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, getResources().getString(R.string.pressback), Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
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

    private void logout() {
        if (Job.isOfflineExist() || Reschedule.getSingle() != null) {
            raiseSnakbar("Offline job exist, internet is reqired");
        } else {
            alert();
        }
    }

    private void alert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Confirm");
        alertDialog.setMessage("Are you sure you want to logout?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Preferences.clearAll(HomeActivity.this);
                Branch.clearAllTables();
                finish();
                Intent i = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.show();
    }

    @Override
    public void onNetworkChanged() {
        if (NetworkUtil.getConnectivityStatusString(HomeActivity.this))
            imgnetwork.setImageResource(R.drawable.network);
        else
            imgnetwork.setImageResource(R.drawable.no_network);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onResult(int api_code, int result_code, String result_data) {
        stopRefresh();
        refreshItem.setVisible(true);
        if (result_code == 409) {
            authalert(this);
        } else if (result_code == 200) {
            Log.e("RESULT DATE", result_data);
            if (api_code == SYSTEMCONFIG)
                saveSystemConfig(result_data);
            else if (api_code == ISCHANGEPASSWORD) {
                if (saveIsChangePassword(result_data)) {
                    Toast.makeText(getApplicationContext(), "Your password has expired. Need to update password", Toast.LENGTH_SHORT).show();
                    changePassword();
                }
            }
            refresh();
        } else {
            raiseSnakbar("Error");
        }
    }

    private boolean saveIsChangePassword(String result) {
        Preferences.saveInt("isChangePassword", Integer.parseInt(result), getApplicationContext());
        try {
            int data = Preferences.getInt("isChangePassword", getApplicationContext());
            if (data == 1) return true;
        } catch (Exception e) {
        }
        return false;
    }

    private void saveSystemConfig(String result) {
        try {
            JSONArray jsonArray = new JSONArray(result);

            for (int a = 0; a < jsonArray.length(); a++) {
                JSONObject jsonObject = jsonArray.getJSONObject(a);
                String accessKey = jsonObject.getString("AccessKey");
                String value = jsonObject.getString("Value");
                Preferences.saveString(accessKey, value, this);
            }

            Preferences.saveString(MIN_PASSWORD_LENGTH, "7", this);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
