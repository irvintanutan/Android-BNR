package com.novigosolutions.certiscisco_pcsbr.applications;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;




import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;
import com.novigosolutions.certiscisco_pcsbr.models.Bags;
import com.novigosolutions.certiscisco_pcsbr.models.Box;
import com.novigosolutions.certiscisco_pcsbr.models.BoxBag;
import com.novigosolutions.certiscisco_pcsbr.models.Branch;
import com.novigosolutions.certiscisco_pcsbr.models.Break;
import com.novigosolutions.certiscisco_pcsbr.models.ChatMessage;
import com.novigosolutions.certiscisco_pcsbr.models.CoinSeries;
import com.novigosolutions.certiscisco_pcsbr.models.Currency;
import com.novigosolutions.certiscisco_pcsbr.models.Delivery;
import com.novigosolutions.certiscisco_pcsbr.models.Envelope;
import com.novigosolutions.certiscisco_pcsbr.models.EnvelopeBag;
import com.novigosolutions.certiscisco_pcsbr.models.Job;
import com.novigosolutions.certiscisco_pcsbr.models.Reschedule;
import com.novigosolutions.certiscisco_pcsbr.utils.Preferences;

import androidx.multidex.MultiDex;

/**
 * Created by dhanrajk on 23-06-17.
 */

public class CertisCISCO extends Application {
    private static CertisCISCO mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        Configuration dbConfiguration = new Configuration.Builder(this).setDatabaseName("certiscisco.db").setDatabaseVersion(3).addModelClasses(Job.class, Bags.class, Box.class, Envelope.class, EnvelopeBag.class, Currency.class, BoxBag.class, Branch.class, Delivery.class,ChatMessage.class,Break.class,Reschedule.class, CoinSeries.class).create();
        ActiveAndroid.initialize(dbConfiguration);
        mContext = this;
        if(TextUtils.isEmpty(Preferences.getString("API_URL",mContext))){
          //  Preferences.saveString("API_URL", "http://10.8.8.158", mContext);
            Preferences.saveString("API_URL", "https://pcsbnr-api.certiscslops.local", mContext);
        }
    }

    @Override
    public void onTerminate() {

        super.onTerminate();
        ActiveAndroid.dispose();
    }

    private static boolean activityVisible = false;

    public static boolean isActivityVisible() {
        return activityVisible;
    }

    public static void activityResumed() {
        activityVisible = true;
    }

    public static void activityPaused() {
        activityVisible = false;
    }

    private static Context currentactvity = null;

    public static Context getCurrentactvity() {
        return currentactvity;
    }

    public static void setCurrentactvity(Context pcurrentactvity) {
        currentactvity = pcurrentactvity;
    }

    public static CertisCISCO instance() {
        return new CertisCISCO();
    }


    public static CertisCISCO getContext() {
        return mContext;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
