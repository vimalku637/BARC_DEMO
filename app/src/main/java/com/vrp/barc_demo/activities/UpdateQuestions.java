/*
 * Copyright (c)  2020. Indev Consultancy Private Limited,
 * Auther : Vimal Kumar
 * Date : 2020/12/15
 * Modified Date :
 * Modified By :
 */

package com.vrp.barc_demo.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.vrp.barc_demo.Dashboard;
import com.vrp.barc_demo.R;
import com.vrp.barc_demo.location_gps.GpsUtils;
import com.vrp.barc_demo.login.LoginActivity;
import com.vrp.barc_demo.models.LogoutModel;
import com.vrp.barc_demo.rest_api.ApiClient;
import com.vrp.barc_demo.rest_api.BARC_API;
import com.vrp.barc_demo.utils.AlertDialogClass;
import com.vrp.barc_demo.utils.CommonClass;
import com.vrp.barc_demo.utils.MyJSON;
import com.vrp.barc_demo.utils.SharedPrefHelper;

import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateQuestions extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private static final String TAG = "Update_Survey";
    @BindView(R.id.btn_submit)
    MaterialButton btn_submit;
    @BindView(R.id.btn_start_survey)
    MaterialButton btn_start_survey;
    @BindView(R.id.spn_select_survey)
    Spinner spn_select_survey;
    @BindView(R.id.tv_person_name)
    MaterialTextView tv_person_name;

    /*normal widgets*/
    private Context context=this;
    private SharedPrefHelper sharedPrefHelper;
    private ArrayList<String> surveySpnAL;
    //for location GPS
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    Location mLastLocation;
    String altitude;
    private String latitude;
    private String longitude;
    private boolean isGPS;
    LocationManager manager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_questions);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        ButterKnife.bind(this);
        setTitle(R.string.main_menu);
        initialization();
        /*get intent values here*/
        Bundle bundle=getIntent().getExtras();
        if (bundle!=null) {
        }

        getGPS();
        setSurveySpinner();
        setButtonClick();
        tv_person_name.setText( "Welcome "+sharedPrefHelper.getString("user_name", ""));
    }
    @Override
    protected void onResume() {
        super.onResume();
        PackageInfo pInfo = null;
        try {
            pInfo = context.getPackageManager().getPackageInfo(getPackageName(), 0);
            String app_version = pInfo.versionName;
            Log.i(TAG, "onResume: "+app_version);
            String version=sharedPrefHelper.getString("version", app_version);
            if (!version.equalsIgnoreCase(app_version)) {
                Intent intent = new Intent(UpdateQuestions.this, UpdateAppActivity.class);
                startActivity(intent);
                finish();
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void setSurveySpinner() {
        //surveySpnAL.add(0, getString(R.string.select_survey));
        surveySpnAL.add(0, "Survey");
        ArrayAdapter arrayAdapter=new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, surveySpnAL);
        spn_select_survey.setAdapter(arrayAdapter);
        spn_select_survey.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setButtonClick() {
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CommonClass.isInternetOn(context)) {
                    callDownloadSurveyApi();
                } else {
                    CommonClass.showPopupForNoInternet(context);
                }
            }
        });
        btn_start_survey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    buildAlertMessageNoGps();
                }else if(sharedPrefHelper.getString("download_survey", "").equals("download_survey")){
                    showPopupDownloadSurvey();
                }
                else {
                    Intent intent = new Intent(UpdateQuestions.this, MainMenu.class);
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

    private void getGPS() {
        new GpsUtils(this).turnGPSOn(new GpsUtils.onGpsListener() {
            @Override
            public void gpsStatus(boolean isGPSEnable) {
                // turn on GPS
                isGPS = isGPSEnable;
            }
        });
        buildGoogleApiClient();
    }
    synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) this)
                .addOnConnectionFailedListener((GoogleApiClient.OnConnectionFailedListener) this)
                .addApi(LocationServices.API)
                .build();
        //mGoogleApiClient.connect();

    }
    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(100); // Update location every second
        if (ActivityCompat.checkSelfPermission(UpdateQuestions.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, (com.google.android.gms.location.LocationListener) this);
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            latitude = String.valueOf(mLastLocation.getLatitude());
            longitude = String.valueOf(mLastLocation.getLongitude());
            altitude = String.valueOf(mLastLocation.getAltitude());
            sharedPrefHelper.setString("LAT", latitude);
            sharedPrefHelper.setString("LONG", longitude);
            sharedPrefHelper.setString("ALTI", altitude);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = String.valueOf(location.getLatitude());
        longitude = String.valueOf(location.getLongitude());

        System.out.println("latitude>>>>" + latitude);
        altitude = String.valueOf(location.getAltitude());
        //String Address=cf.getAddress(Double.parseDouble(latitude), Double.parseDouble(longitude));
        SharedPreferences pref = getApplicationContext().getSharedPreferences("GCMSetting", MODE_PRIVATE); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();

        editor.putString("LATTITUDE>>>", latitude);
        editor.putString("LONGITUDE>>>", longitude);
        editor.putString("ALTITUDE>>>", altitude);
        sharedPrefHelper.setString("LAT", latitude);
        sharedPrefHelper.setString("LONG", longitude);
        sharedPrefHelper.setString("ALTI", altitude);


        //editor.putString("Address", Address);

        editor.commit(); // commit changes

        /*GlobalVars.LATTITUDE = Double.parseDouble(latitude);
        GlobalVars.LONGITUDE = Double.parseDouble(longitude);
        GlobalVars.ALTITUDE = Double.parseDouble(altitude);
        new Thread(new Runnable() {
            @Override
            public void run() {
                GlobalVars.Address=cf.getAddress(GlobalVars.LATTITUDE, GlobalVars.LONGITUDE);
            }
        });*/

       /* Float thespeed = location.getSpeed();
        Double lat=location.getLatitude();
        Double lng=location.getLongitude();*/
        // tv.setText("Location -"+String.valueOf(lat)+String.valueOf(lng)+"\n Speed: "+String.valueOf(thespeed));

        //Log.v("speed", String.valueOf(thespeed));

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!mGoogleApiClient.isConnected())
            mGoogleApiClient.connect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void initialization() {
        sharedPrefHelper=new SharedPrefHelper(this);
        surveySpnAL=new ArrayList<>();
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (isInternetOn()) {
            checkStatus();
        }
    }

    private void callDownloadSurveyApi() {
        AlertDialogClass.showProgressDialog(context);
        ApiClient.getClient().create(BARC_API.class).getBarcDemoJson().enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().toString());
                    Log.e(TAG, "onResponse: "+jsonObject.toString());
                    String surveyJSON=jsonObject.toString();
                    //to save all JSON into json file
                    if (surveyJSON.length()>0) {
                        sharedPrefHelper.setString("download_survey", "done");
                        AlertDialogClass.dismissProgressDialog();
                        MyJSON.saveJSONToAsset(context, surveyJSON);
                        Intent intent=new Intent(UpdateQuestions.this, MainMenu.class);
                        startActivity(intent);
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

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) finish();
        if (item.getItemId()==R.id.home_icon) {
            if(sharedPrefHelper.getString("download_survey", "").equals("download_survey")){
                showPopupDownloadSurvey();
            }
            else {
                Intent intentMainMenu=new Intent(context, MainMenu.class);
                intentMainMenu.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intentMainMenu);
            }
        }
        if (item.getItemId()==R.id.logout){
            /*sharedPrefHelper.setString("user_name_password", "");
            sharedPrefHelper.setString("user_name", "");*/
            /*sharedPrefHelper.setString("isLogin", "");
            Intent intentLoginActivity=new Intent(context, LoginActivity.class);
            intentLoginActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intentLoginActivity);*/
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

    private void showPopupDownloadSurvey() {
        final SweetAlertDialog pDialog = new SweetAlertDialog(
                context, SweetAlertDialog.WARNING_TYPE);
        pDialog.setTitleText("Download Survey!!")
                .setContentText("Download survey first before start the survey.")
                .setConfirmText("Ok")
                //.setCancelText("No")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismiss();
                    }
                })
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismiss();
                    }
                })
                .show();
        pDialog.setCancelable(false);
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
                            Intent i = new Intent(UpdateQuestions.this, LoginActivity.class);
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
                        Intent i = new Intent(UpdateQuestions.this, LoginActivity.class);
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
