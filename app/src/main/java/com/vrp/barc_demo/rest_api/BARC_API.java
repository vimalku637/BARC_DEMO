/*
 * Copyright (c)  2020. Indev Consultancy Private Limited,
 * Auther : Vimal Kumar
 * Date : 2020/12/15
 * Modified Date :
 * Modified By :
 */

package com.vrp.barc_demo.rest_api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface BARC_API {
    //@POST("loginv2.php")
    @POST("loginv3.php")
    Call<JsonObject> callLogin(@Body RequestBody body);
    @POST("check_status.php")
    Call<JsonObject> callCheckStatus(@Body RequestBody body);
    @POST("logout.php")
    Call<JsonObject> callLogout(@Body RequestBody body);

    @POST("forget_password.php")
    Call<JsonObject> getForgetPassword(@Body RequestBody body);

    @GET("questions_v3.php")
    Call<JsonObject> getBarcDemoJson();

    @POST("download_cluster_v3.php")
    Call<JsonArray> getClusterList(@Body RequestBody body);

    @POST("lock_cluster.php")
    Call<JsonObject> lockCluster(@Body RequestBody body);

    @POST("survey_data_upload_v2.php")
    Call<JsonObject> sendSurveyData(@Body RequestBody body);

    @POST("download_general.php")
    Call<JsonArray> saveCities(@Body RequestBody body);

    @Multipart
    @POST("upload_audio.php")
    Call<JsonObject> sendAudio(@Query("user_id") String user_id, @Query("survey_id") String survey_id,
                               @Query("survey_data_monitoring_id") int survey_data_monitoring_id,
                               @Part MultipartBody.Part part);
}
