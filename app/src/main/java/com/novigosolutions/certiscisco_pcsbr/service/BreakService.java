package com.novigosolutions.certiscisco_pcsbr.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.novigosolutions.certiscisco_pcsbr.activites.BreakActivity;

/**
 * Created by dhanrajk on 22-06-17.
 */

public class BreakService extends Service {
    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        int BreakId=-5;

        if (intent != null && intent.getExtras() != null)
            BreakId = intent.getExtras().getInt("BreakId");
        if(BreakId>0) {
            Log.e("icon_break started ",":"+ BreakId);
            Intent breakActivity = new Intent(getApplicationContext(), BreakActivity.class);
            breakActivity.putExtra("BreakId",BreakId);
            breakActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().startActivity(breakActivity);
        }
        return Service.START_NOT_STICKY;
    }
}
