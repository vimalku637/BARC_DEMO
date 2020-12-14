package com.vrp.barc_demo.rest_api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface BARC_API {
    @POST("loginv2.php")
    Call<JsonObject> callLogin(@Body RequestBody body);
    @POST("forget_password.php")
    Call<JsonObject> getForgetPassword(@Body RequestBody body);
    @GET("questions.php")
    Call<JsonObject> getBarcDemoJson();
    @POST("download_cluster.php")
    Call<JsonArray> getClusterList(@Query("pincode") String pincode);
    @POST("lock_cluster.php")
    Call<JsonObject> lockCluster(@Body RequestBody body);
    @POST("survey_data_upload.php")
    Call<JsonObject> sendSurveyData(@Body RequestBody body);
}
