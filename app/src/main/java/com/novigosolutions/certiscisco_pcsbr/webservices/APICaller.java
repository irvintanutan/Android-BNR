package com.novigosolutions.certiscisco_pcsbr.webservices;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;

import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.novigosolutions.certiscisco_pcsbr.R;
import com.novigosolutions.certiscisco_pcsbr.activites.ChatActivity;
import com.novigosolutions.certiscisco_pcsbr.interfaces.ApiCallback;
import com.novigosolutions.certiscisco_pcsbr.models.Branch;
import com.novigosolutions.certiscisco_pcsbr.models.Break;
import com.novigosolutions.certiscisco_pcsbr.models.ChatMessage;
import com.novigosolutions.certiscisco_pcsbr.models.Job;
import com.novigosolutions.certiscisco_pcsbr.service.BreakService;
import com.novigosolutions.certiscisco_pcsbr.utils.CommonMethods;
import com.novigosolutions.certiscisco_pcsbr.utils.Constants;
import com.novigosolutions.certiscisco_pcsbr.utils.Preferences;

import org.json.JSONArray;
import org.json.JSONObject;

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

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * Created by dhanrajk on 21-07-17.
 */

public class APICaller {
    Context context;

    private CertisCISCOServices getService() {
//        ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
//                .tlsVersions(TlsVersion.TLS_1_2)
//                .cipherSuites(
//                        CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
//                        CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
//                        CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256)
//                .build();
//        OkHttpClient client = new OkHttpClient.Builder()
//                //.connectionSpecs(Collections.singletonList(spec))
//                .build();
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
// set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        // add your other interceptors …
        // add logging as last interceptor
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

    public void GetAppVersion(ApiCallback callback) {
        Call<ResponseBody> call = getService().GetAppVersion();
        process(Constants.GETAPPVERSION, call, callback);
    }

    public void login(ApiCallback callback, JsonObject jsonObject) {
        Call<ResponseBody> call = getService().signin(jsonObject);
        process(Constants.SIGNIN, call, callback);
    }

    public void sync(ApiCallback callback, Context context) {
        this.context = context;
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("UserId", Preferences.getInt("UserId", context));
        jsonObject.addProperty("TeamId", Preferences.getInt("TeamId", context));
        jsonObject.addProperty("SyncDate", Preferences.getString("LoggedInDate", context));
        JsonArray jsonArray = new JsonArray();
        List<Job> jobs = Job.getAll();
        for (int i = 0; i < jobs.size(); i++) {
            JsonObject jobObject = new JsonObject();
            jobObject.addProperty("TransportId", jobs.get(i).TransportMasterId);
            jobObject.addProperty("Version", jobs.get(i).VersionNo);
            jobObject.addProperty("GroupKey", jobs.get(i).GroupKey);
            jsonArray.add(jobObject);
        }
        jsonObject.add("Orders", jsonArray);
        //Log.e("sync param", jsonObject.toString());
        Call<ResponseBody> call = getService().Sync(Preferences.getString("AuthToken", context), jsonObject);
        process(Constants.SYNC, call, callback);
    }

    public void SubmitBulkCollection(ApiCallback callback, Context context, String GroupKey, String BranchCode, String PFunctionalCode, String actualFromTime, String actualToTime) {
        this.context = context;
        //Branch.getCollection(GroupKey, context, BranchCode, PFunctionalCode, actualFromTime, actualToTime);
        Call<ResponseBody> call = getService().SubmitBulkCollection(Preferences.getString("AuthToken", context), Preferences.getInt("UserId", context),
                Branch.getCollection(GroupKey, context, BranchCode, PFunctionalCode, actualFromTime, actualToTime));
        process(Constants.SUBMITBULKCOLLECTION, call, callback);
    }

    public void getReceiptNo(ApiCallback callback, Context context, int jobId) {
        this.context = context;
        Call<ResponseBody> call = getService().GetReceiptNumber(jobId);
        process(Constants.GETRECEIPTNUMBER, call, callback);
    }

    public void secureVehicle(ApiCallback callback, Context context, int jobId) {
        this.context = context;
        Call<ResponseBody> call = getService().SecureVehicle(Integer.toString(jobId));
        Constants.TRANSPORT_MASTER_ID = jobId;
        process(Constants.SECUREVEHICLE, call, callback);
    }

    public void requestForEdit(ApiCallback callback, Context context, String type, String GroupKey) {
        this.context = context;
        Call<ResponseBody> call = getService().requestForEdit(Preferences.getString("AuthToken", context), getRequestType(type, GroupKey));
        process(Constants.REQUESTFOREDIT, call, callback);
    }

    public void getrequestStatus(ApiCallback callback, Context context, String RequestId) {
        this.context = context;
        Call<ResponseBody> call = getService().getrequestStatus(Preferences.getString("AuthToken", context), RequestId);
        process(Constants.GETREQUESTSTATUS, call, callback);
    }

    public void RequestForReSchedule(ApiCallback callback, Context context, JsonObject jsonObject) {
        //Log.e("request body", jsonObject.toString());

        this.context = context;
        Call<ResponseBody> call = getService().RequestForReSchedule(Preferences.getString("AuthToken", context), jsonObject);
        process(Constants.REQUESTFORRESCHEDULE, call, callback);
    }

    public void SubmitDeliveryList(ApiCallback callback, Context context, String GroupKey, String BranchCode, String PFunctionalCode, String actualFromTime, String actualToTime) {
        this.context = context;
        Call<ResponseBody> call = getService().SubmitDeliveryList(Preferences.getString("AuthToken", context), Preferences.getInt("UserId", context),
                Branch.getDelivery(GroupKey, context, BranchCode, PFunctionalCode, actualFromTime, actualToTime));
        process(Constants.SUBMITDELIVERYLIST, call, callback);
    }

    public void ConsumeBreak(ApiCallback callback, Context context, int id) {
        this.context = context;

        Call<ResponseBody> call = getService().ConsumeBreak(Preferences.getString("AuthToken", context), id);
        process(Constants.CONSUMEBREAK, call, callback);
    }

    public void sendMessage(ApiCallback callback, Context context, String message) {
        this.context = context;
        Call<ResponseBody> call = getService().sendMessage(Preferences.getInt("TeamId", context), CommonMethods.toDay(), message);
        process(Constants.SENDMESSAGE, call, callback);
    }

    public void GetMessages(Context context) {
        this.context = context;
        Log.e("last id", ":" + ChatMessage.getLastMessageId());
        Log.e("Team id", ":" + Preferences.getInt("TeamId", context));
        Call<ResponseBody> call = getService().GetMessages(Preferences.getInt("TeamId", context), CommonMethods.toDay(), ChatMessage.getLastMessageId());
        process(Constants.GETALLMESSAGE, call, null);
    }

    public void MarkMessageAsRead(ApiCallback callback, Context context, int id) {
        this.context = context;
        Log.e("last id", ":" + ChatMessage.getLastMessageId());
        Call<ResponseBody> call = getService().MarkMessageAsRead(id);
        process(Constants.MarkMessageAsRead, call, callback);
    }

    private void process(final int api_code, Call<ResponseBody> call, final ApiCallback callback) {
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.e("web response code - ", "api:" + api_code + "," + response.code());
                try {
                    if (response.code() == 409) {
                        //authalert(this);
                    } else if (response.code() == 200) {
                        try {
                            String result_body = response.body().string();

                            if (api_code == Constants.SECUREVEHICLE) {
                                Job.UpdateSecureVehicle(Constants.TRANSPORT_MASTER_ID);
                            }
                            if (api_code == Constants.GETRECEIPTNUMBER) {
                                Job.UpdateReceiptNo(Constants.TRANSPORT_MASTER_ID, result_body.replace("\"", ""));
                            }
                            if (api_code == Constants.SYNC) {
                                JSONObject obj = new JSONObject(result_body);
                                String result = obj.getString("Result");
                                //Log.e("RESULT", result_body);

                                if (result.equals("Success")) {
                                    Boolean ischangeindata = false, hasBreak = false;
                                    JSONObject jsonObject = obj.getJSONObject("Data");
                                    JSONArray jsonArray = jsonObject.getJSONArray("Orders");

                                    SyncDatabase.instance().saveCoinSeries(obj, context);
//                                    for (int i = 0; i < jsonArray.length(); i++) {
//                                        JSONObject orderJSONObject = jsonArray.getJSONObject(i);
//                                        Job.updateLatestGroupKey(orderJSONObject.getInt("TransportId"), orderJSONObject.getString("GroupKey"));
//                                    }
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject orderJSONObject = jsonArray.getJSONObject(i);
                                        if (orderJSONObject.getString("Status").equalsIgnoreCase("Updated") || orderJSONObject.getString("Status").equalsIgnoreCase("Deleted") || orderJSONObject.getString("Status").equalsIgnoreCase("New")) {
                                            ischangeindata = true;
                                            break;
                                        }
                                    }
                                    if (ischangeindata) {
                                        SyncDatabase.instance().sync(jsonArray, context);
                                        if (callback == null) {
                                            Intent intent = new Intent("syncreciverevent");
                                            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                                        }

                                    }
                                    GsonBuilder builder = new GsonBuilder();
                                    builder.excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC);
                                    Gson gson = builder.create();
                                    JSONArray UpcomingBreaks = jsonObject.getJSONArray("UpcomingBreaks");
                                    for (int i = 0; i < UpcomingBreaks.length(); i++) {
                                        JSONObject jsonbreak = UpcomingBreaks.getJSONObject(i);
                                        int BreakId = jsonbreak.getInt("Id");
                                        if (!Break.isExist(BreakId)) {
                                            Break aBreak = gson.fromJson(jsonbreak.toString(), Break.class);
                                            aBreak.BreakId = BreakId;
                                            aBreak.save();
                                            if (aBreak.Consumed) {
                                                Date EndTime = CommonMethods.getBreakTime(aBreak.EndTime);
                                                long serverTime = CommonMethods.getServerTimeInms(context);
                                                if (EndTime != null && EndTime.getTime() > serverTime) {
                                                    Date StartTime = CommonMethods.getBreakTime(aBreak.StartTime);
                                                    if (StartTime != null) {
                                                        long alarmInTime = StartTime.getTime() - serverTime;
                                                        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                                                        Intent serviceBreak = new Intent(context, BreakService.class);
                                                        serviceBreak.putExtra("BreakId", BreakId);
                                                        PendingIntent pi = PendingIntent.getService(context, BreakId, serviceBreak, PendingIntent.FLAG_UPDATE_CURRENT);
                                                        am.set(AlarmManager.RTC_WAKEUP, alarmInTime + System.currentTimeMillis(), pi);
                                                    }
                                                }
                                            }
                                            Break.setexpired(context);
                                            hasBreak = true;
                                        }
                                    }
                                    if (hasBreak) {
                                        Intent intent = new Intent("breakreciverevent");
                                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                                    }
                                    Preferences.saveBoolean("EnableManualEntry", jsonObject.getBoolean("ManualEntryEnabled"), context);
                                }
                            }
                            if (api_code == Constants.GETALLMESSAGE) {
                                JSONObject obj = new JSONObject(result_body);
                                String result = obj.getString("Result");
                                if (result.equals("Success")) {
                                    JSONArray messages = obj.getJSONArray("Data");
                                    GsonBuilder builder = new GsonBuilder();
                                    builder.excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC);
                                    Gson gson = builder.create();
                                    int newMessageCount = 0;
                                    boolean hasMessage = false;
                                    for (int i = 0; i < messages.length(); i++) {
                                        JSONObject message = messages.getJSONObject(i);
                                        int MessageId = message.getInt("Id");
                                        if (!ChatMessage.isExist(MessageId)) {
                                            ChatMessage chatMessage = gson.fromJson(message.toString(), ChatMessage.class);
                                            chatMessage.MessageId = MessageId;
                                            chatMessage.save();
                                            hasMessage = true;
                                            if (!chatMessage.IsRead && !message.getBoolean("IsFromDevice")) {
                                                newMessageCount++;
                                            }
                                        }
                                    }
                                    if (hasMessage) {

                                        Intent intent = new Intent("mesagereciverevent");
                                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                                    }
                                    if (newMessageCount > 0 && !isChatActivityRunning(context)) {
                                        playSound();
                                        showMessagepopup(newMessageCount);
                                    }
                                }
                            }
                            int maxLogSize = 1000;
                            for (int i = 0; i <= result_body.length() / maxLogSize; i++) {
                                int start = i * maxLogSize;
                                int end = (i + 1) * maxLogSize;
                                end = end > result_body.length() ? result_body.length() : end;
                                Log.e(api_code + ":", result_body.substring(start, end));
                            }
                            if (callback != null)
                                callback.onResult(api_code, response.code(), result_body);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        if (callback != null) callback.onResult(api_code, response.code(), null);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (callback != null) callback.onResult(api_code, 000, null);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                if (callback != null) callback.onResult(api_code, 000, null);
            }
        });
    }

    private JsonObject getRequestType(String type, String GroupKey) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("Module", "MC45");
        Job job = Job.getJobListByGroupKey(GroupKey).get(0);
        Branch branch = Branch.getSingle(job.GroupKey);

        String address = "";
        if (!TextUtils.isEmpty(job.BranchStreetName) && job.BranchStreetName != null){
            address += job.BranchStreetName+" , ";
        }
        if (!TextUtils.isEmpty(job.BranchTower) && job.BranchTower != null){
            address += job.BranchTower+", ";
        }
        if (!TextUtils.isEmpty(job.BranchTown) && job.BranchTown != null){
            address += job.BranchTown+" , ";
        }
        if (!TextUtils.isEmpty(job.BranchPinCode) && job.BranchPinCode != null){
            address += job.BranchPinCode;
        }

        jsonObject.addProperty("RequestType", "MANUAL ENTRY PERMISSION FOR " + type + " | " + branch.FunctionalCode + " | " + address + "|" + branch.CustomerName  + "|" + Preferences.getString("DeviceID", context));
        //jsonObject.addProperty("RequestType", "MANUAL ENTRY PERMISSION FOR " + type + "|" + branch.FunctionalCode + "|" + Preferences.getString("DeviceID", context));
        jsonObject.addProperty("RequestedBy", Preferences.getInt("UserId", context));
        jsonObject.addProperty("RequestedOn", CommonMethods.getCurrentDateTime(context));

        return jsonObject;
    }


    private void playSound() {
        try {
            MediaPlayer player = MediaPlayer.create(context, R.raw.message);
            player.setVolume(100, 100);
            player.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isChatActivityRunning(Context ctx) {
        ActivityManager mngr = (ActivityManager) ctx.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskList = mngr.getRunningTasks(1);
        if (taskList.get(0).topActivity.getClassName().equals(ChatActivity.class.getName())) {

            return true;
        }
        return false;
    }

    private void showMessagepopup(int newMessageCount) {
        if (context != null) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
            alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.setCancelable(false);
            alertDialog.setTitle("New message");
            if (newMessageCount == 1) alertDialog.setMessage(ChatMessage.getLastMessage());
            else alertDialog.setMessage(newMessageCount + " new messages");
            alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            alertDialog.show();
        }
    }

    public static APICaller instance() {
        return new APICaller();
    }
}
