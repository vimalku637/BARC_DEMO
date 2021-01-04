/*
 * Copyright (c)  2020. Indev Consultancy Private Limited,
 * Auther : Vimal Kumar
 * Date : 2020/12/15
 * Modified Date :
 * Modified By :
 */

package com.vrp.barc_demo.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vrp.barc_demo.activities.UpdateAppActivity;
import com.vrp.barc_demo.forgot_password.ForgotPassword;
import com.vrp.barc_demo.R;
import com.vrp.barc_demo.activities.UpdateQuestions;
import com.vrp.barc_demo.location_gps.AppConstants;
import com.vrp.barc_demo.location_gps.GpsUtils;
import com.vrp.barc_demo.models.LoginModel;
import com.vrp.barc_demo.rest_api.ApiClient;
import com.vrp.barc_demo.rest_api.BARC_API;
import com.vrp.barc_demo.service.Config;
import com.vrp.barc_demo.splash.SplashActivity;
import com.vrp.barc_demo.sqlite_db.SqliteHelper;
import com.vrp.barc_demo.utils.SharedPrefHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private static final String TAG = "Login_Activity";
    @BindView(R.id.et_user_name)
    TextInputEditText et_user_name;
    @BindView(R.id.et_password)
    TextInputEditText et_password;
    @BindView(R.id.btn_submit)
    MaterialButton btn_submit;
    @BindView(R.id.tv_forgot_password)
    MaterialTextView tv_forgot_password;
    @BindView(R.id.til_user_name)
    TextInputLayout til_user_name;
    ProgressDialog mprogressDialog;
    @BindView(R.id.tv_app_version)
    MaterialTextView tv_app_version;

    // /normal widgets/
    private Context context = this;
    SharedPrefHelper sharedPrefHelper;
    SqliteHelper sqliteHelper;
    LoginModel loginModel;
    String user_name;
    String password;
    //for location GPS
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    Location mLastLocation;
    String altitude;
    private String latitude;
    private String longitude;
    private boolean isGPS;
    private static final int REQUEST = 112;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    String version = "";
    boolean is_downloaded;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        setTitle(R.string.login);
        initialization();
        sqliteHelper.openDataBase();
        // /get intent values here/
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
        }
        setValues();

        if (Build.VERSION.SDK_INT >= 23) {
            Log.d("TAG","@@@ IN IF Build.VERSION.SDK_INT >= 23");
            String[] PERMISSIONS = {
                    Manifest.permission.ACCESS_FINE_LOCATION
            };
            if (!hasPermissions(this, PERMISSIONS)) {
                Log.d("TAG","@@@ IN IF hasPermissions");
                ActivityCompat.requestPermissions((Activity) this, PERMISSIONS, REQUEST );
            } else {
                Log.d("TAG","@@@ IN ELSE hasPermissions");
            }
        } else {
            Log.d("TAG","@@@ IN ELSE  Build.VERSION.SDK_INT >= 23");
        }

        getGPS();
        submitButtonClick();
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

                    displayFirebaseRegId();

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    String message = intent.getStringExtra("message");

                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();

                    //  txtMessage.setText(message);
                }
            }
        };
        displayFirebaseRegId();
        downlaodVersionCode();
    }


    @Override
    protected void onResume() {
        super.onResume();
      /*  PackageInfo pInfo = null;
        try {
            pInfo = context.getPackageManager().getPackageInfo(getPackageName(), 0);
            String version1 = pInfo.versionName;
            if (!version.equalsIgnoreCase(version1)) {
                Intent intent = new Intent(LoginActivity.this, UpdateAppActivity.class);
                startActivity(intent);
                finish();
            }

            } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }*/

    }

    private void setValues() {
        PackageInfo pInfo = null;
        try {
            pInfo = context.getPackageManager().getPackageInfo(getPackageName(), 0);
            String version1 = pInfo.versionName;
            tv_app_version.setText(getString(R.string.app_version)+version1);


        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(LoginActivity.this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String mToken = instanceIdResult.getToken();
                sharedPrefHelper.setString("Token", mToken);
                Log.e("Token", mToken);
            }
        });
        String regId = pref.getString("regId", null);
        Log.e(TAG, "Firebase reg id: " + regId);
        if (!TextUtils.isEmpty(regId)) {
            //txtRegId.setText("Firebase Reg Id: " + regId);
        } else {
            //txtRegId.setText("Firebase Reg Id is not received yet!");
        }
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

    private void initialization() {
        sharedPrefHelper = new SharedPrefHelper(this);
        sqliteHelper=new SqliteHelper(this);
        loginModel = new LoginModel();
    }

    private void submitButtonClick() {
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if (checkValidation()) {

                loginModel.setUser_name(et_user_name.getText().toString());
                loginModel.setUser_password(et_password.getText().toString());
                user_name = et_user_name.getText().toString().trim();
                password = et_password.getText().toString().trim();
                if (user_name.equalsIgnoreCase("") || (password.equalsIgnoreCase(""))) {
                    if (user_name.equalsIgnoreCase("")) {
                        et_user_name.setError("Please enter Username");
                    }
                    if (password.equalsIgnoreCase("")) {
                        et_password.setError("Please enter password");
                    }
                    // Snackbar.make(view, "Please enter user name & password", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                } else {
                    if(sharedPrefHelper.getString("user_name", "").equals(et_user_name.getText().toString()) && sharedPrefHelper.getString("user_name_password", "").equals(et_password.getText().toString())){
                        Intent intentMainActivity = new Intent(context, UpdateQuestions.class);
                        startActivity(intentMainActivity);
                        finish();
                    }
                    else{
                        if (!isInternetOn()) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                            builder.setMessage("Network Error, check your network connection.")
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setCancelable(false)
                                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.dismiss();
                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.setTitle(getString(R.string.Alert));
                            alert.show();

                        }
                        else if (isInternetOn()) {

                            loginModel.setUser_name(et_user_name.getText().toString());
                            loginModel.setUser_password(et_password.getText().toString());
                            //    loginModel.setFirebase_token(sharedPrefHelper.getString("Token",""));
                            Gson gson = new Gson();
                            String data = gson.toJson(loginModel);
                            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                            RequestBody body = RequestBody.create(JSON, data);


                            mprogressDialog = ProgressDialog.show(context, "", getString(R.string.Please_wait), true);
                            ApiClient.getClient().create(BARC_API.class).callLogin(body).enqueue(new Callback<JsonObject>() {
                                @Override
                                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response.body().toString().trim());
                                        Log.e(TAG, "onResponse: " + jsonObject.toString());
                                        String success = jsonObject.optString("success");
                                        String message = jsonObject.optString("message");
                                        if (Integer.valueOf(success) == 1) {
                                            String user_id = jsonObject.optString("user_id");
                                            String interviewer_id = jsonObject.optString("interviewer_id");
                                            String interviewer_name = jsonObject.optString("interviewer_name");
                                            String user_name = jsonObject.optString("user_name");
                                            String user_type_id = jsonObject.optString("user_type_id");
                                            String mdl_id = jsonObject.optString("mdl_id");
                                            String supervisor_id = jsonObject.optString("supervisor_id");
                                            String supervisor_name = jsonObject.optString("supervisor_name");
                                            String agency_name = jsonObject.optString("agency_name");

                                            download_city("sub_districts",user_id);
                                            download_city("nccs_matrix",user_id);
                                            mprogressDialog.dismiss();
                                            ///set preference data/
                                            setAllDataInPreferences(user_id, interviewer_id, interviewer_name, user_name,
                                                    user_type_id, mdl_id, supervisor_id, supervisor_name, agency_name);

                                            Intent intentMainActivity = new Intent(context, UpdateQuestions.class);
                                            startActivity(intentMainActivity);
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
                    }

                }
            }
            // }


        });


        //}

        tv_forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentForgotPassword = new Intent(context, ForgotPassword.class);
                startActivity(intentForgotPassword);
            }
        });
    }
    private void setAllDataInPreferences(String user_id, String interviewer_id, String interviewer_name,
                                         String user_name, String user_type_id, String mdl_id, String supervisor_id,
                                         String supervisor_name, String agency_name) {
        sharedPrefHelper.setString("user_id", user_id);
        sharedPrefHelper.setString("interviewer_id", interviewer_id);
        sharedPrefHelper.setString("interviewer_name", interviewer_name);
        sharedPrefHelper.setString("user_name", user_name);
        sharedPrefHelper.setString("user_type_id", user_type_id);
        sharedPrefHelper.setString("mdl_id", mdl_id);
        sharedPrefHelper.setString("supervisor_id", supervisor_id);
        sharedPrefHelper.setString("supervisor_name", supervisor_name);
        sharedPrefHelper.setString("agency_name", agency_name);
        sharedPrefHelper.setString("user_name_password", et_password.getText().toString());
    }

    public void download_city(String table,String user_id){
        DataDownloadInput dataDownloadInput = new DataDownloadInput();
        dataDownloadInput.setTable_name(table);
        dataDownloadInput.setUser_id(user_id);
        Gson Gson = new Gson();
        String data = Gson.toJson(dataDownloadInput);
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, data);

        ApiClient.getClient().create(BARC_API.class).saveCities(body).enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                try {
                    //JSONObject data=new JSONObject(response.body().toString().trim());
                    JsonArray data = response.body();
                    Log.i("City Data ",""+data.toString());

                    sqliteHelper.dropTable(table);
                    for (int i = 0; i < data.size(); i++) {
                        JSONObject singledata = new JSONObject(data.get(i).toString());
                        Iterator keys = singledata.keys();
                        ContentValues contentValues = new ContentValues();
                        while (keys.hasNext()) {
                            String currentDynamicKey = (String) keys.next();
                            contentValues.put(currentDynamicKey, singledata.get(currentDynamicKey).toString());
                        }
                        sqliteHelper.saveMasterTable(contentValues, table);
                    }


                } catch (Exception s) {
                    s.printStackTrace();
                    mprogressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                mprogressDialog.dismiss();
                mprogressDialog.dismiss();
            }
        });
    }


    /*private boolean checkValidation() {
    }*/




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
        if (ActivityCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("TAG", "@@@ PERMISSIONS grant");
                    mGoogleApiClient.connect();
                } else {
                    Log.d("TAG", "@@@ PERMISSIONS Denied");
                    Toast.makeText(this, "PERMISSIONS Denied", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
    public String downlaodVersionCode() {


        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://bamboo4sd.org/api3/check_version.php",
            StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://bamboo4sd.org/replica/api3/check_version.php",
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //getting the whole json object from the response
                            JSONObject obj = new JSONObject(response);
                            version = obj.getString("version");
                            Log.e("version",version);
                            sharedPrefHelper.setString("version", version);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                }
                ,
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }


    });
        requestQueue.add(stringRequest);
        return version;
    }


//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
//        startActivity(intent);
//        finish();
//    }
}
