package com.novigosolutions.certiscisco_pcsbr.recivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.novigosolutions.certiscisco_pcsbr.service.SignalRService;
import com.novigosolutions.certiscisco_pcsbr.service.SyncMessageService;
import com.novigosolutions.certiscisco_pcsbr.service.SyncService;

import java.util.Date;

/**
 * Created by dhanrajk on 22-06-17.
 */

public class IntervalChangedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context ctx, Intent intent) {
        //int EXEC_INTERVAL=Preferences.getInt("INTERVAL",ctx);
        //5 minute
        int EXEC_INTERVAL = 300;
        //int EXEC_INTERVAL = 60;

        //if(EXEC_INTERVAL>0) {
        try {
            Log.e("sync start: ", new Date().toString());
            Log.e("sync interval: ", ":" + EXEC_INTERVAL);
            AlarmManager alarmManager = (AlarmManager) ctx
                    .getSystemService(Context.ALARM_SERVICE);
            Intent i = new Intent(ctx, SyncService.class);
            PendingIntent intentExecuted = PendingIntent.getService(ctx, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
            alarmManager.cancel(intentExecuted);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + EXEC_INTERVAL * 1000, EXEC_INTERVAL * 1000, intentExecuted);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Log.e("msg start: ", new Date().toString());
            Log.e("msg interval: ", ":" + EXEC_INTERVAL);
            int MSG_SYNC_INTERVAL = 63;
            AlarmManager alarmManager = (AlarmManager) ctx
                    .getSystemService(Context.ALARM_SERVICE);
            Intent i = new Intent(ctx, SyncMessageService.class);
            PendingIntent intentExecuted = PendingIntent.getService(ctx, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
            alarmManager.cancel(intentExecuted);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + MSG_SYNC_INTERVAL * 1000, MSG_SYNC_INTERVAL * 1000, intentExecuted);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ctx.stopService(new Intent(ctx.getApplicationContext(), SignalRService.class));
        ctx.startService(new Intent(ctx.getApplicationContext(), SignalRService.class));
    }
}
