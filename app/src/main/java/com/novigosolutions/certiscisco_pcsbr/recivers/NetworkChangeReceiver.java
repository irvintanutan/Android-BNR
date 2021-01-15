package com.novigosolutions.certiscisco_pcsbr.recivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.novigosolutions.certiscisco_pcsbr.interfaces.NetworkChangekListener;
import com.novigosolutions.certiscisco_pcsbr.models.Branch;
import com.novigosolutions.certiscisco_pcsbr.service.OfflineUpdateService;
import com.novigosolutions.certiscisco_pcsbr.utils.NetworkUtil;


public class NetworkChangeReceiver extends BroadcastReceiver {
    public static NetworkChangekListener changekListener;

    @Override
    public void onReceive(final Context context, final Intent intent) {
        Log.e("Network changed", ":" + NetworkUtil.getConnectivityStatusString(context));
        if (changekListener != null)
            changekListener.onNetworkChanged();
        if (NetworkUtil.getConnectivityStatusString(context)) {
            Intent eventService = new Intent(context, OfflineUpdateService.class);
            context.startService(eventService);
            Log.e("updating offline", ":");
        }
    }
}