package com.vrp.barc_demo.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.vrp.barc_demo.Dashboard;
import com.vrp.barc_demo.R;
import com.vrp.barc_demo.login.LoginActivity;
import com.vrp.barc_demo.models.LogoutModel;
import com.vrp.barc_demo.rest_api.ApiClient;
import com.vrp.barc_demo.rest_api.BARC_API;
import com.vrp.barc_demo.sqlite_db.SqliteHelper;
import com.vrp.barc_demo.utils.SharedPrefHelper;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NextSurvey extends AppCompatActivity {
    private static final String TAG ="" ;
    @BindView(R.id.cv_dashboard)
    MaterialCardView cv_dashboard;
    private Context context=this;
    SharedPrefHelper sharedPrefHelper;
    int sampleSize=0;
    int totalSurveyForCluster=0;
    private SqliteHelper sqliteHelper;
    LocationManager manager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next_survey);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        ButterKnife.bind(this);
        setTitle("Next Survey");
        initialization();
        /*get survey sample from table*/
       // sampleSize=sqliteHelper.getClusterSampleSizeFromTable(sharedPrefHelper.getString("cluster_no", ""));
        sampleSize=sharedPrefHelper.getInt("sampleSize",0);
        //totalSurveyForCluster=sqliteHelper.getTotalSurveyForCluster(sharedPrefHelper.getString("cluster_no", ""));
        totalSurveyForCluster=sharedPrefHelper.getInt("totalSurvey",0);
        setButtonClick();
    }


    private void setButtonClick() {
        cv_dashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    buildAlertMessageNoGps();
                }else {
                    Intent intent = new Intent(NextSurvey.this, ClusterListActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                        //exit form app while choosing 'No'
                        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                        homeIntent.addCategory( Intent.CATEGORY_HOME );
                        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(homeIntent);
                        finish();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void initialization() {
        sqliteHelper=new SqliteHelper(this);
        sharedPrefHelper=new SharedPrefHelper(this);
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (isInternetOn()) {
            checkStatus();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) finish();
        if (item.getItemId() == R.id.home_icon) {
            Intent intentMainMenu = new Intent(context, MainMenu.class);
            intentMainMenu.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intentMainMenu);
        }
        if (item.getItemId() == R.id.logout) {
           /* sharedPrefHelper.setString("user_name_password", "");
            sharedPrefHelper.setString("user_name", "");
            Intent i = new Intent(NextSurvey.this, LoginActivity.class);
// set the new task and clear flags
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);*/
            Logout(sharedPrefHelper.getString("user_id", ""));

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    private boolean isInternetOn() {

        ConnectivityManager connec = (ConnectivityManager) getSystemService(getBaseContext().CONNECTIVITY_SERVICE);

        assert connec != null;
        if (connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED
                || connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING
                || connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING
                || connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED) {
            return true;

        } else if (connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED
                || connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED) {
            return false;
        }
        return false;
    }

    private void Logout(String user_id){
        if (isInternetOn()) {
            LogoutModel logoutModel = new LogoutModel();
            logoutModel.setUser_id(user_id);
            //logoutModel.setFirebase_token(sharedPrefHelper.getString("Token",""));
            Gson gson = new Gson();
            String data = gson.toJson(logoutModel);
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(JSON, data);
            ProgressDialog mprogressDialog = ProgressDialog.show(context, "", getString(R.string.Please_wait), true);
            ApiClient.getClient().create(BARC_API.class).callLogout(body).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().toString().trim());
                        Log.e(TAG, "onResponse: " + jsonObject.toString());
                        String success = jsonObject.optString("success");
                        if (Integer.valueOf(success) == 1) {
                            sharedPrefHelper.setString("isLogin", "");
                            sharedPrefHelper.setString("user_id", "");
                            sharedPrefHelper.setString("user_name", "");
                            sharedPrefHelper.setString("user_name_password", "");
                            Intent i = new Intent(NextSurvey.this, LoginActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                            finish();
                        } else {
                            Snackbar.make(findViewById(android.R.id.content), "Please Enter Valid User & Password ", Snackbar.LENGTH_LONG).show();
                            mprogressDialog.dismiss();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    if (mprogressDialog.isShowing()) {
                        mprogressDialog.dismiss();
                    }
                }
            });
        }
        else{
            Snackbar.make(findViewById(android.R.id.content), "Internet is not available please try again!", Snackbar.LENGTH_LONG).show();
        }
    }
    private void checkStatus(){
        LogoutModel logoutModel = new LogoutModel();
        logoutModel.setUser_id(sharedPrefHelper.getString("user_id", ""));
        logoutModel.setFirebase_token(sharedPrefHelper.getString("Token",""));
        Gson gson = new Gson();
        String data = gson.toJson(logoutModel);
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, data);
        ApiClient.getClient().create(BARC_API.class).callCheckStatus(body).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().toString().trim());
                    Log.e(TAG, "onResponse: " + jsonObject.toString());
                    String success = jsonObject.optString("success");
                    if (Integer.valueOf(success) == 2) {
                        sharedPrefHelper.setString("isLogin", "");
                        sharedPrefHelper.setString("user_id", "");
                        sharedPrefHelper.setString("user_name", "");
                        sharedPrefHelper.setString("user_name_password", "");
                        Intent i = new Intent(NextSurvey.this, LoginActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        finish();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                //
            }
        });
    }


}