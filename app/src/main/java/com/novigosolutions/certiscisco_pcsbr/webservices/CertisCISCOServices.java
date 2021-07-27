package com.novigosolutions.certiscisco_pcsbr.webservices;


import com.google.gson.JsonObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Streaming;

public interface CertisCISCOServices {

    @GET("api")
    Call<ResponseBody> ping();

    @GET("GetAppVersion")
    Call<ResponseBody> GetAppVersion();

    @GET("download/File/ATM_App.apk")
    @Streaming
    Call<ResponseBody> downloadFile();

    @Headers("Content-Type: application/json")
    @POST("DoAndroidLogin")
    Call<ResponseBody> signin(@Body JsonObject object);

    @Headers("Content-Type: application/json")
    @POST("OrderCheckVersionsandroid")
    Call<ResponseBody> Sync(@Header("AuthToken") String AuthToken, @Body JsonObject object);

    @Headers("Content-Type: application/json")
    @POST("SubmitBulkCollection")
    Call<ResponseBody> SubmitBulkCollection(@Header("AuthToken") String AuthToken, @Header("UserId") int UserId, @Body JsonObject object);

    @Headers("Content-Type: application/json")
    @POST("GetReceiptNo")
    Call<ResponseBody> GetReceiptNumber(@Query("jobId") int jobId);

    @Headers("Content-Type: application/json")
    @POST("RequestForEditForMC45")
    Call<ResponseBody> requestForEdit(@Header("AuthToken") String AuthToken, @Body JsonObject object);

    @Headers("Content-Type: application/json")
    @POST("CheckRequestStatus")
    Call<ResponseBody> getrequestStatus(@Header("AuthToken") String AuthToken, @Query("RequestId") String RequestId);

    @Headers("Content-Type: application/json")
    @POST("RequestForReSchedule")
    Call<ResponseBody> RequestForReSchedule(@Header("AuthToken") String AuthToken, @Body JsonObject object);

    @Headers("Content-Type: application/json")
    @POST("RequestForBreak")
    Call<ResponseBody> RequestForBreak(@Header("AuthToken") String AuthToken, @Header("UserId") int UserId, @Body JsonObject object);

    @Headers("Content-Type: application/json")
    @POST("SubmitDeliveryList")
    Call<ResponseBody> SubmitDeliveryList(@Header("AuthToken") String AuthToken, @Header("UserId") int UserId, @Body JsonObject object);

    @Headers("Content-Type: application/json")
    @POST("sendMessage")
    Call<ResponseBody> sendMessage(@Query("teamId") int teamId, @Query("date") String date, @Query("message") String message);

    @Headers("Content-Type: application/json")
    @POST("ConsumeBreak")
    Call<ResponseBody> ConsumeBreak(@Header("AuthToken") String AuthToken, @Query("id") int id);

    @Headers("Content-Type: application/json")
    @POST("GetMessages")
    Call<ResponseBody> GetMessages(@Query("teamId") int teamId, @Query("date") String date, @Query("lastId") int lastId);

    @Headers("Content-Type: application/json")
    @POST("MarkMessageAsRead")
    Call<ResponseBody> MarkMessageAsRead(@Query("meesagId") int meesagId);

}
