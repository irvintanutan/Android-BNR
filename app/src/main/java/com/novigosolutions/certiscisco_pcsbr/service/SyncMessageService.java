package com.novigosolutions.certiscisco_pcsbr.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.novigosolutions.certiscisco_pcsbr.utils.NetworkUtil;
import com.novigosolutions.certiscisco_pcsbr.webservices.APICaller;

import java.util.Date;

/**
 * Created by dhanrajk on 22-06-17.
 */

public class SyncMessageService extends Service {
    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        if (NetworkUtil.getConnectivityStatusString(this)) {
            Log.e("message service: ", new Date().toString());
            APICaller.instance().GetMessages(getApplicationContext());
        }
        return Service.START_NOT_STICKY;
    }
}
