package com.novigosolutions.certiscisco_pcsbr.recivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.novigosolutions.certiscisco_pcsbr.utils.Preferences;


/**
 * Created by dhanrajk on 22-06-17.
 */

public class ShutDownReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context ctx, Intent intent) {
        Preferences.saveBoolean("LoggedIn", false, ctx);
        Log.e("boot completed","yes");
    }
}
