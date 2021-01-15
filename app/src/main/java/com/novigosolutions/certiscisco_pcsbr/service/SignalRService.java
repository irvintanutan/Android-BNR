package com.novigosolutions.certiscisco_pcsbr.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.novigosolutions.certiscisco_pcsbr.utils.Preferences;
import com.novigosolutions.certiscisco_pcsbr.webservices.APICaller;
import com.novigosolutions.certiscisco_pcsbr.webservices.CertisCISCOServer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ExecutionException;

import microsoft.aspnet.signalr.client.Platform;
import microsoft.aspnet.signalr.client.SignalRFuture;
import microsoft.aspnet.signalr.client.http.android.AndroidPlatformComponent;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler1;
import microsoft.aspnet.signalr.client.transport.ClientTransport;
import microsoft.aspnet.signalr.client.transport.ServerSentEventsTransport;

public class SignalRService extends Service {
    private HubConnection mHubConnection;
    private HubProxy mHubProxy;
    private Handler mHandler; // to display Toast message

    public SignalRService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startSignalR();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if(mHubConnection!=null)
        mHubConnection.stop();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private void startSignalR() {
        Platform.loadPlatformComponent(new AndroidPlatformComponent());
        mHubConnection = new HubConnection(CertisCISCOServer.SIGNALR_IP);
        String SERVER_HUB_CHAT = "certisHub";
        mHubProxy = mHubConnection.createHubProxy(SERVER_HUB_CHAT);
        ClientTransport clientTransport = new ServerSentEventsTransport(mHubConnection.getLogger());
        SignalRFuture<Void> signalRFuture = mHubConnection.start(clientTransport);

        try {
            signalRFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return;
        }

        String CLIENT_METHOD_BROADAST_MESSAGE = "SendAndroidMessage";
        mHubProxy.on(CLIENT_METHOD_BROADAST_MESSAGE,
                new SubscriptionHandler1<String>() {
                    @Override
                    public void run(final String object) {
                        //final String finalMsg = msg.senderName + " says " + msg.body;
                        // display Toast message
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Log.e("rmessage", object);
                                    JSONObject obj = new JSONObject(object);
                                    JSONArray teams = obj.getJSONArray("Team");
                                    GsonBuilder builder = new GsonBuilder();
                                    builder.excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC);
                                    Gson gson = builder.create();
                                    Type type = new TypeToken<List<Integer>>() {
                                    }.getType();
                                    List<Integer> teamList = gson.fromJson(teams.toString(), type);
                                    if (teamList.contains(Preferences.getInt("TeamId", getApplicationContext()))) {
                                        APICaller.instance().GetMessages(getApplicationContext());
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }, String.class);
    }
}