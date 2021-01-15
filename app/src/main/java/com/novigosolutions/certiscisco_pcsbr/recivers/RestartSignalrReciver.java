package com.novigosolutions.certiscisco_pcsbr.recivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.novigosolutions.certiscisco_pcsbr.service.SignalRService;

public class RestartSignalrReciver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, SignalRService.class));
    }
}
