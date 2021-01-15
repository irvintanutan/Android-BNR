package com.novigosolutions.certiscisco_pcsbr.activites;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;


import com.novigosolutions.certiscisco_pcsbr.BuildConfig;
import com.novigosolutions.certiscisco_pcsbr.R;
import com.novigosolutions.certiscisco_pcsbr.applications.CertisCISCO;
import com.novigosolutions.certiscisco_pcsbr.interfaces.ApiCallback;
import com.novigosolutions.certiscisco_pcsbr.objects.Download;
import com.novigosolutions.certiscisco_pcsbr.service.DownloadService;
import com.novigosolutions.certiscisco_pcsbr.utils.NetworkUtil;
import com.novigosolutions.certiscisco_pcsbr.utils.Preferences;
import com.novigosolutions.certiscisco_pcsbr.webservices.APICaller;

import java.io.File;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

/**
 * SplashActivity.java - class that loads first.
 *
 * @author dhanrajk
 * @version 1.0
 * @compmany novigosolutions
 */
public class SplashActivity extends BaseActivity implements ApiCallback {
    /**
     * @param savedInstanceState
     */
    ProgressDialog bar;
    public static final String DOWNLOAD_PROGRESS = "download_progress";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setContentView(R.layout.activity_splash);
            if (NetworkUtil.getConnectivityStatusString(SplashActivity.this)
                    && !TextUtils.isEmpty(Preferences.getString("API_URL",CertisCISCO.getContext()))) {
                checkVersionUpdate();
            } else {
                delay();
            }
        } catch (Exception e) {
            e.printStackTrace();
            crashalert(SplashActivity.this);
        }
    }

    private void delay() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                openapp();//go to activity after 2s
            }
        }, 2000);
    }

    private void openapp() {
        finish();
        if (CertisCISCO.isActivityVisible()) {

            if (Preferences.getBoolean("LoggedIn", SplashActivity.this)) {
                startActivity(new Intent(SplashActivity.this, HomeActivity.class));//if logged in
            } else {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));//if not
            }
        }
        //  startActivity(new Intent(SplashActivity.this, HomeActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        CertisCISCO.activityResumed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        CertisCISCO
                .activityPaused();
    }

    private void checkVersionUpdate() {
        APICaller.instance().GetAppVersion(this);
    }

    private void startDownload() {
        registerReceiver();
        Intent intent = new Intent(this, DownloadService.class);
        startService(intent);
    }

    private void registerReceiver() {

        LocalBroadcastManager bManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DOWNLOAD_PROGRESS);
        bManager.registerReceiver(onDownloadComplete, intentFilter);

    }

    private BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(DOWNLOAD_PROGRESS)) {

                Download download = intent.getParcelableExtra("download");
                if (download.getProgress() == -5) {
                    bar.dismiss();
                    delay();
                } else {
                    bar.setIndeterminate(false);
                    bar.setMax(100);
                    bar.setProgress(download.getProgress());
                    if (download.getProgress() == 100) {
                        bar.dismiss();
                        alert();
                    } else {
                        bar.setMessage(String.format("Downloaded (%d/%d) MB", download.getCurrentFileSize(), download.getTotalFileSize()));
                    }
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(onDownloadComplete);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void alert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Update...");
        alertDialog.setMessage("New version is availabe.");
        alertDialog.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                UpdateNewVersion();
            }
        });
        alertDialog.show();
    }


    private void UpdateNewVersion() {
        Preferences.saveBoolean("LoggedIn", false, SplashActivity.this);
        finish();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/CERTIS_CISCO/ATM.apk")),
                "application/vnd.android.package-archive");
        Log.e("path", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/CERTIS_CISCO//ATM.apk");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onResult(int api_code, int result_code, String result_data) {
        if (result_code == 200) {
            String version_name = result_data.replaceAll("[^.0-9]", "");
            if (BuildConfig.VERSION_CODE < Integer.parseInt(version_name)) {
                startDownload();
                bar = new ProgressDialog(SplashActivity.this);
                bar.setCancelable(false);
                bar.setMessage("Downloading...");
                bar.setIndeterminate(true);
                bar.setCanceledOnTouchOutside(false);
                bar.show();
            } else {
                delay();
            }
        } else {
            delay();
        }
    }
}
