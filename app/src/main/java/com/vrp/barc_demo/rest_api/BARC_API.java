package com.vrp.barc_demo.rest_api;

import com.google.gson.JsonObject;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface BARC_API {
    @POST("login.php")
    Call<JsonObject> callLogin(@Body RequestBody body);
    @GET("questions.php")
    Call<JsonObject> getBarcDemoJson();
    @POST("download_cluster.php")
    Call<JsonObject> getClusterList(RequestBody body);
}
