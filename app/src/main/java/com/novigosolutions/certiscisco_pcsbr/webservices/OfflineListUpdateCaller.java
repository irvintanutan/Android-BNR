package com.novigosolutions.certiscisco_pcsbr.webservices;

import android.content.Context;

import android.util.Log;

import com.novigosolutions.certiscisco_pcsbr.models.Branch;
import com.novigosolutions.certiscisco_pcsbr.models.Job;
import com.novigosolutions.certiscisco_pcsbr.utils.Preferences;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by dhanrajk on 21-07-17.
 */

public class OfflineListUpdateCaller {
    public void UpdateOfflineList(final Context context) {
//        final Branch branch = Branch.getOfflineSingleBranche();
        final Job job = Job.getOfflineSingleJob();
        final Boolean isCollectionUpdating;
        Log.e("^^^^^OfflineUpdate", "single-job:" + (job == null ? "null" : job.TransportMasterId));
        if (job != null) {
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.connectTimeout(30, TimeUnit.SECONDS);
            httpClient.readTimeout(30, TimeUnit.SECONDS);
            httpClient.writeTimeout(30, TimeUnit.SECONDS);
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(CertisCISCOServer.getPATH())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(UnsafeOkHttpClient.getUnsafeOkHttpClient(httpClient))
                    .build();
            Call<ResponseBody> call = null;
            if (job.IsCollectionOrder) {
                isCollectionUpdating = true;
                call = retrofit.create(CertisCISCOServices.class).SubmitBulkCollection(Preferences.getString("AuthToken", context), Preferences.getInt("UserId", context),
                        Branch.getCollection(job.GroupKey, context, job.BranchCode, job.PFunctionalCode, job.ActualFromTime, job.ActualToTime));
            } else {
                isCollectionUpdating = false;
                call = retrofit.create(CertisCISCOServices.class).SubmitDeliveryList(Preferences.getString("AuthToken", context), Preferences.getInt("UserId", context),
                        Branch.getDelivery(job.GroupKey, context, job.BranchCode, job.PFunctionalCode, job.ActualFromTime,  job.ActualToTime));
            }
            if (call != null) {
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            if (response.code() == 200) {
                                JSONObject obj = new JSONObject(response.body().string());
                                if (obj.getString("Result").equals("Success")) {
                                    if (isCollectionUpdating) {
//                                        Branch.setColOfflineStatus(branch.GroupKey, 0);
                                        Job.setIncompleteCollectionCollected(job.GroupKey);
                                        Log.e("^^^^^OfflineUpdate", "setIncompleteCollectionCollected");
//                                        Branch.setCollected(branch.GroupKey);
                                    } else {
//                                        Branch.setDelOfflineStatus(branch.GroupKey, 0);
                                        Job.setPendingDeliveryDelivered(job.GroupKey);
                                        Log.e("^^^^^OfflineUpdate", "setPendingDeliveryDelivered");
//                                        Branch.setDelivered(branch.GroupKey);
                                    }
                                    UpdateOfflineList(context);
//                                    Intent intent = new Intent("offlinereciverevent");
//                                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
            }
        } else {
            Log.e("^^^^^OfflineUpdate", "all branches done - syncing");
            APICaller.instance().sync(null, context);
        }

    }

    public static OfflineListUpdateCaller instance() {
        return new OfflineListUpdateCaller();
    }
}
