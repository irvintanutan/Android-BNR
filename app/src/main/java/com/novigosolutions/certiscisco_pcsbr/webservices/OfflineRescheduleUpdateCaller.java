package com.novigosolutions.certiscisco_pcsbr.webservices;

import android.content.Context;

import com.google.gson.JsonObject;
import com.novigosolutions.certiscisco_pcsbr.models.Reschedule;
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

public class OfflineRescheduleUpdateCaller {
    public void UpdateOfflineReschedule(final Context context) {
        final Reschedule reschedule = Reschedule.getSingle();
        if (reschedule != null) {
            JsonObject rejsonObject = new JsonObject();
            rejsonObject.addProperty("TransportId", reschedule.TransportId);
            rejsonObject.addProperty("RescheduleDt", reschedule.RescheduleDt);
            rejsonObject.addProperty("Reason", reschedule.Reason);
            rejsonObject.addProperty("SignImg", reschedule.SignImg);
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.connectTimeout(30, TimeUnit.SECONDS);
            httpClient.readTimeout(30, TimeUnit.SECONDS);
            httpClient.writeTimeout(30, TimeUnit.SECONDS);
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(CertisCISCOServer.getPATH())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(UnsafeOkHttpClient.getUnsafeOkHttpClient(httpClient))
                    .build();
            Call<ResponseBody> call =retrofit.create(CertisCISCOServices.class).RequestForReSchedule(Preferences.getString("AuthToken", context),rejsonObject);

            if (call != null) {
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            if (response.code() == 200) {
                                JSONObject obj = new JSONObject(response.body().string());
                                if (obj.getString("Result").equals("Success")) {
                                    Reschedule.removeSingle(reschedule.getId());
                                    UpdateOfflineReschedule(context);
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
        }

    }

    public static OfflineRescheduleUpdateCaller instance() {
        return new OfflineRescheduleUpdateCaller();
    }
}
