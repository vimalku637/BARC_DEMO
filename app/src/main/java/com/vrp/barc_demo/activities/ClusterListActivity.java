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
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vrp.barc_demo.R;
import com.vrp.barc_demo.adapters.ClusterAdapter;
import com.vrp.barc_demo.interfaces.ClickListener;
import com.vrp.barc_demo.models.ClusterModel;
import com.vrp.barc_demo.rest_api.ApiClient;
import com.vrp.barc_demo.rest_api.BARC_API;
import com.vrp.barc_demo.sqlite_db.SqliteHelper;
import com.vrp.barc_demo.utils.AlertDialogClass;
import com.vrp.barc_demo.utils.CommonClass;
import com.vrp.barc_demo.utils.MyJSON;
import com.vrp.barc_demo.utils.SharedPrefHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.MediaType;
import okhttp3.RequestBody;
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

                            /*set and get these values for preferences*/
                            clusterModel.setCluster_no(jsonObject.getString("cluster_no"));
                            clusterModel.setState_Name(jsonObject.getString("State_Name"));
                            clusterModel.setTown_Village_Class(jsonObject.getString("Town_Village_Class"));
                            clusterModel.setCensus_District_Name(jsonObject.getString("Census_District_Name"));
                            clusterModel.setCensus_Village_Town_Code(jsonObject.getString("Census_Village_Town_Code"));
                            clusterModel.setCensus_Village_Town_Name(jsonObject.getString("Census_Village_Town_Name"));
                            clusterModel.setUA_Component(jsonObject.getString("UA_Component"));
                            clusterModel.setUA_Component_code(jsonObject.getString("UA_Component_code"));
                            clusterModel.setBARC_Town_Code(jsonObject.getString("BARC_Town_Code"));

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
                                                        if (CommonClass.isInternetOn(context)) {
                                                            ClusterModel clusterModel=new ClusterModel();
                                                            clusterModel.setCluster_id(clusterModelAL.get(position).getCluster_id());
                                                            clusterModel.setUser_id(sharedPrefHelper.getString("user_id", ""));

                                                            Gson gson = new Gson();
                                                            String data = gson.toJson(clusterModel);
                                                            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                                                            RequestBody body = RequestBody.create(JSON, data);

                                                            callLockLusterApi(body, position);
                                                        }
                                                        else {
                                                            CommonClass.showPopupForNoInternet(context);
                                                        }
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
                                        intentSurveyList.putExtra("screen_type", "survey");

                                        /*set preference data*/
                                        String cluster_no = clusterModelAL.get(position).getCluster_no();
                                        String State_Name = clusterModelAL.get(position).getState_Name();
                                        String Town_Village_Class = clusterModelAL.get(position).getTown_Village_Class();
                                        String Census_District_Name = clusterModelAL.get(position).getCensus_District_Name();
                                        String Census_Village_Town_Code = clusterModelAL.get(position).getCensus_Village_Town_Code();
                                        String Census_Village_Town_Name = clusterModelAL.get(position).getCensus_Village_Town_Name();
                                        String UA_Component = clusterModelAL.get(position).getUA_Component();
                                        String UA_Component_code = clusterModelAL.get(position).getUA_Component_code();
                                        String BARC_Town_Code = clusterModelAL.get(position).getBARC_Town_Code();

                                        setAllDataInPreferences(cluster_no,State_Name,Town_Village_Class,Census_District_Name,
                                                Census_Village_Town_Code,Census_Village_Town_Name,UA_Component,UA_Component_code,
                                                BARC_Town_Code);

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

    private void callLockLusterApi(RequestBody body, int position) {
        AlertDialogClass.showProgressDialog(context);
        ApiClient.getClient().create(BARC_API.class).lockCluster(body).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().toString());
                    Log.e(TAG, "lock_cluster-: "+jsonObject.toString());
                    String success=jsonObject.getString("success");
                    String message=jsonObject.getString("message");
                    if (success.equals("1")) {
                        AlertDialogClass.dismissProgressDialog();
                        Intent intentAddressSelection=new Intent(context, AddressSelection.class);
                        intentAddressSelection.putExtra("original_address", clusterModelAL.get(position).getOriginal_address());
                        intentAddressSelection.putExtra("next_address", clusterModelAL.get(position).getNext_address());
                        intentAddressSelection.putExtra("previous_address", clusterModelAL.get(position).getPrevious_address());
                        intentAddressSelection.putExtra("cluster_id", clusterModelAL.get(position).getCluster_id());
                        intentAddressSelection.putExtra("cluster_name", clusterModelAL.get(position).getCluster_name());
                        intentAddressSelection.putExtra("screen_type", "survey");
                        startActivity(intentAddressSelection);
                        Toast.makeText(context, ""+message, Toast.LENGTH_SHORT).show();
                    } else {
                        AlertDialogClass.dismissProgressDialog();
                        CommonClass.showPopupForNoInternet(context);
                    }

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

    private void setAllDataInPreferences(String cluster_no, String state_name, String town_village_class,
                                         String census_district_name, String census_village_town_code,
                                         String census_village_town_name, String ua_component,
                                         String ua_component_code, String barc_town_code) {
        sharedPrefHelper.setString("cluster_no", cluster_no);
        sharedPrefHelper.setString("state_name", state_name);
        sharedPrefHelper.setString("town_village_class", town_village_class);
        sharedPrefHelper.setString("census_district_name", census_district_name);
        sharedPrefHelper.setString("census_village_town_code", census_village_town_code);
        sharedPrefHelper.setString("census_village_town_name", census_village_town_name);
        sharedPrefHelper.setString("ua_component", ua_component);
        sharedPrefHelper.setString("ua_component_code", ua_component_code);
        sharedPrefHelper.setString("barc_town_code", barc_town_code);
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        sharedPrefHelper.setString("current_date", dateFormat.format(cal.getTime()));
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
