package com.novigosolutions.certiscisco_pcsbr.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.novigosolutions.certiscisco_pcsbr.models.Branch;
import com.novigosolutions.certiscisco_pcsbr.models.Job;
import com.novigosolutions.certiscisco_pcsbr.models.Reschedule;
import com.novigosolutions.certiscisco_pcsbr.utils.Preferences;
import com.novigosolutions.certiscisco_pcsbr.webservices.APICaller;
import com.novigosolutions.certiscisco_pcsbr.webservices.OfflineListUpdateCaller;
import com.novigosolutions.certiscisco_pcsbr.webservices.OfflineRescheduleUpdateCaller;

import java.util.Date;

/**
 * Created by dhanrajk on 22-06-17.
 */

public class OfflineUpdateService extends Service {
    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        Log.e("offline service: ", new Date().toString());
//        if (Branch.isOfflineExist()) {
        if (Job.isOfflineExist()) {
            OfflineListUpdateCaller.instance().UpdateOfflineList(getApplicationContext());
        }
        if (Reschedule.getSingle()!=null) {
            OfflineRescheduleUpdateCaller.instance().UpdateOfflineReschedule(getApplicationContext());
        }
        return Service.START_NOT_STICKY;
    }
}
