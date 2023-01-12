package com.novigosolutions.certiscisco_pcsbr.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.novigosolutions.certiscisco_pcsbr.activites.SplashActivity;
import com.novigosolutions.certiscisco_pcsbr.models.Break;
import com.novigosolutions.certiscisco_pcsbr.models.ChatMessage;
import com.novigosolutions.certiscisco_pcsbr.models.Job;
import com.novigosolutions.certiscisco_pcsbr.models.UserLogs;
import com.novigosolutions.certiscisco_pcsbr.objects.Download;
import com.novigosolutions.certiscisco_pcsbr.utils.CommonMethods;
import com.novigosolutions.certiscisco_pcsbr.utils.Constants;
import com.novigosolutions.certiscisco_pcsbr.utils.Preferences;
import com.novigosolutions.certiscisco_pcsbr.webservices.CertisCISCOServer;
import com.novigosolutions.certiscisco_pcsbr.webservices.CertisCISCOServices;
import com.novigosolutions.certiscisco_pcsbr.webservices.SyncDatabase;
import com.novigosolutions.certiscisco_pcsbr.webservices.UnsafeOkHttpClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Modifier;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by dhanrajk on 07-02-18.
 */

public class AuditService extends IntentService {

    public AuditService() {
        super("Audit Service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        uploadLogs();
    }

    private void uploadLogs() {
        UserLogs.removeUserLogs();
        List<UserLogs> userLogs = UserLogs.getUserLogs();
        Log.e("USER LOGS SIZE", Integer.toString(userLogs.size()));
        for (UserLogs userLog : userLogs) {
            Log.e("USER LOGS ", body(userLog).toString());
            Call<ResponseBody> call = getService().UserActivityLog(userLog.Entity, userLog.UserAction, userLog.Remarks,
                    Integer.parseInt(userLog.UserId), "APK", userLog.DateTime);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        if (response.code() == 200) {
                            UserLogs.updateUserLogs(userLog.DateTime);
                            Log.e("Success", "Audit Logs " + userLog.toString());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e("Failed", "Audit Logs " + userLog.toString());
                }
            });
        }
    }

    private JsonObject body(UserLogs userLogs) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("entity", userLogs.Entity);
        jsonObject.addProperty("userAction", userLogs.UserAction);
        jsonObject.addProperty("remarks", userLogs.Remarks);
        jsonObject.addProperty("userId", userLogs.UserId);
        jsonObject.addProperty("source", "Apk");
        jsonObject.addProperty("apkdatetime", userLogs.DateTime);
        return jsonObject;
    }


    private CertisCISCOServices getService() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.connectTimeout(200, TimeUnit.SECONDS);
        httpClient.readTimeout(200, TimeUnit.SECONDS);
        httpClient.writeTimeout(200, TimeUnit.SECONDS);
        httpClient.addInterceptor(logging);  // <-- this is the important line!

        Retrofit retrofit = new Retrofit.Builder()
                //.client(client)
                .baseUrl(CertisCISCOServer.getPATH())
                .addConverterFactory(GsonConverterFactory.create())
                .client(UnsafeOkHttpClient.getUnsafeOkHttpClient(httpClient))
                .build();
        return retrofit.create(CertisCISCOServices.class);
    }
}