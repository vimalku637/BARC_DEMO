package com.vrp.barc_demo.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.vrp.barc_demo.R;
import com.vrp.barc_demo.models.ClusterModel;
import com.vrp.barc_demo.rest_api.ApiClient;
import com.vrp.barc_demo.rest_api.BARC_API;
import com.vrp.barc_demo.utils.AlertDialogClass;
import com.vrp.barc_demo.utils.CommonClass;
import com.vrp.barc_demo.utils.MyJSON;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClusterListActivity extends AppCompatActivity {
    private static final String TAG = "ClusterListActivity";
    @BindView(R.id.rv_cluster_list)
    RecyclerView rv_cluster_list;

    /*normal widgets*/
    private Context context=this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cluster_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        ButterKnife.bind(this);
        setTitle(R.string.dashboard);
        initialization();
        /*get intent values here*/
        Bundle bundle=getIntent().getExtras();
        if (bundle!=null) {
        }

        callClusterApi();
        setClusterAdapter();
        setButtonClick();
    }

    private void callClusterApi() {
        ClusterModel clusterModel=new ClusterModel();
        clusterModel.setPincode("422004");

        Gson gson = new Gson();
        String data = gson.toJson(clusterModel);
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, data);

        AlertDialogClass.showProgressDialog(context);
        ApiClient.getClient().create(BARC_API.class).getClusterList(body).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    JsonObject jsonObject = response.body();
                    Log.e(TAG, "onResponse: "+jsonObject.toString());

                    AlertDialogClass.dismissProgressDialog();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(context, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                AlertDialogClass.dismissProgressDialog();
            }
        });
    }

    private void setClusterAdapter() {

    }

    private void setButtonClick() {
    }

    private void initialization() {
    }
}
