package com.vrp.barc_demo.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.textview.MaterialTextView;
import com.google.gson.JsonArray;
import com.vrp.barc_demo.R;
import com.vrp.barc_demo.adapters.ClusterAdapter;
import com.vrp.barc_demo.interfaces.ClickListener;
import com.vrp.barc_demo.models.ClusterModel;
import com.vrp.barc_demo.rest_api.ApiClient;
import com.vrp.barc_demo.rest_api.BARC_API;
import com.vrp.barc_demo.sqlite_db.SqliteHelper;
import com.vrp.barc_demo.utils.AlertDialogClass;
import com.vrp.barc_demo.utils.SharedPrefHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClusterListActivity extends AppCompatActivity {
    private static final String TAG = "ClusterListActivity";
    @BindView(R.id.rv_cluster_list)
    RecyclerView rv_cluster_list;
    @BindView(R.id.tv_oops_no_data)
    MaterialTextView tv_oops_no_data;

    /*normal widgets*/
    private Context context=this;
    private SqliteHelper sqliteHelper;
    private SharedPrefHelper sharedPrefHelper;
    private ArrayList<ClusterModel> clusterModelAL;
    private ClusterAdapter mClusterAdapter;

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
        /*ClusterModel clusterModel=new ClusterModel();
        clusterModel.setPincode("422004");

        Gson gson = new Gson();
        String data = gson.toJson(clusterModel);
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, data);*/

        AlertDialogClass.showProgressDialog(context);
        ApiClient.getClient().create(BARC_API.class).getClusterList("422004").enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                try {
                    clusterModelAL.clear();
                    JSONArray jsonArray = new JSONArray(response.body().toString());
                    Log.e(TAG, "onResponse: "+jsonArray.toString());
                    AlertDialogClass.dismissProgressDialog();
                    if (jsonArray.length()>0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject=jsonArray.getJSONObject(i);

                            //save data cluster in table.
                            Iterator keys = jsonObject.keys();
                            ContentValues contentValues = new ContentValues();
                            while (keys.hasNext()) {
                                String currentDynamicKey = (String) keys.next();
                                contentValues.put(currentDynamicKey, jsonObject.get(currentDynamicKey).toString());
                            }
                            sqliteHelper.saveMasterTable(contentValues, "cluster");

                            ClusterModel clusterModel=new ClusterModel();
                            clusterModel.setCluster_id(jsonObject.getString("cluster_no"));
                            clusterModel.setCluster_name(jsonObject.getString("Original_Town_Village"));
                            clusterModel.setAction(jsonObject.getString("lock_status"));
                            clusterModel.setOriginal_address(jsonObject.getString("Original_address"));
                            clusterModel.setNext_address(jsonObject.getString("After_10_Voter_Address"));
                            clusterModel.setPrevious_address(jsonObject.getString("Previous_10_Voter_Address"));

                            clusterModelAL.add(clusterModel);
                        }
                        if (clusterModelAL.size()>0) {
                            tv_oops_no_data.setVisibility(View.GONE);
                            LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
                            mClusterAdapter = new ClusterAdapter(context, clusterModelAL);
                            rv_cluster_list.setLayoutManager(mLayoutManager);
                            rv_cluster_list.setAdapter(mClusterAdapter);

                            mClusterAdapter.onItemClick(new ClickListener() {
                                @Override
                                public void onItemClick(int position) {
                                    if (clusterModelAL.get(position).getAction().equals("0")) {
                                        new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                                                .setTitleText("Are you sure?")
                                                .setContentText("Want to assign" + "\n" + clusterModelAL.get(position).getCluster_id())
                                                .setConfirmText("Submit")
                                                .setCancelText("Cancel")
                                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                    @Override
                                                    public void onClick(SweetAlertDialog sDialog) {
                                                        sDialog.dismiss();
                                                        //call api to lock cluster
                                                        Intent intentAddressSelection=new Intent(context, AddressSelection.class);
                                                        intentAddressSelection.putExtra("original_address", clusterModelAL.get(position).getOriginal_address());
                                                        intentAddressSelection.putExtra("next_address", clusterModelAL.get(position).getNext_address());
                                                        intentAddressSelection.putExtra("previous_address", clusterModelAL.get(position).getPrevious_address());
                                                        intentAddressSelection.putExtra("cluster_id", clusterModelAL.get(position).getCluster_id());
                                                        intentAddressSelection.putExtra("cluster_name", clusterModelAL.get(position).getCluster_name());
                                                        startActivity(intentAddressSelection);
                                                    }
                                                })
                                                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                    @Override
                                                    public void onClick(SweetAlertDialog sDialog) {
                                                        sDialog.dismiss();
                                                    }
                                                })
                                                .show();
                                    } else {
                                        Intent intentSurveyList = new Intent(context, SurveyListActivity.class);
                                        intentSurveyList.putExtra("original_address", clusterModelAL.get(position).getOriginal_address());
                                        intentSurveyList.putExtra("next_address", clusterModelAL.get(position).getNext_address());
                                        intentSurveyList.putExtra("previous_address", clusterModelAL.get(position).getPrevious_address());
                                        intentSurveyList.putExtra("cluster_id", clusterModelAL.get(position).getCluster_id());
                                        intentSurveyList.putExtra("cluster_name", clusterModelAL.get(position).getCluster_name());
                                        startActivity(intentSurveyList);
                                    }
                                }
                            });

                        } else {
                            tv_oops_no_data.setVisibility(View.VISIBLE);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
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
        sqliteHelper=new SqliteHelper(this);
        sharedPrefHelper=new SharedPrefHelper(this);
        clusterModelAL=new ArrayList<>();
    }
}
