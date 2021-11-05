 /*
 * Copyright (c)  2020. Indev Consultancy Private Limited,
 * Auther : Vimal Kumar
 * Date : 2020/12/15
 * Modified Date :
 * Modified By :
 */

package com.vrp.barc_demo.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.JsonObject;
import com.vrp.barc_demo.Dashboard;
import com.vrp.barc_demo.R;
import com.vrp.barc_demo.login.LoginActivity;
import com.vrp.barc_demo.models.SurveyModel;
import com.vrp.barc_demo.rest_api.ApiClient;
import com.vrp.barc_demo.rest_api.BARC_API;
import com.vrp.barc_demo.sqlite_db.SqliteHelper;
import com.vrp.barc_demo.utils.CommonClass;
import com.vrp.barc_demo.utils.SharedPrefHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainMenu extends AppCompatActivity {
    @BindView(R.id.cv_dashboard)
    MaterialCardView cv_dashboard;
    @BindView(R.id.cv_synchronise)
    MaterialCardView cv_synchronise;
    @BindView(R.id.cv_synchronise_audio)
    MaterialCardView cv_synchronise_audio;
    @BindView(R.id.cv_export_db)
    MaterialCardView cv_export_db;
    @BindView(R.id.tv_person_name)
    MaterialTextView tv_person_name;
    @BindView(R.id.tv_synchronise)
    MaterialTextView tv_synchronise;
    @BindView(R.id.tv_synchronise_audio)
    MaterialTextView tv_synchronise_audio;

    /*normal widgets*/
    private Context context=this;
    SharedPrefHelper sharedPrefHelper;
    SqliteHelper sqliteHelper;
    private ArrayList<SurveyModel> modelArrayList;
    public static String AudioSavePathInDevice="";
    MultipartBody.Part part;
    ProgressDialog mProgressDialog=null;
    int count=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        ButterKnife.bind(this);
        setTitle(R.string.main_menu);
        initialization();
        /*get intent values here*/
        Bundle bundle=getIntent().getExtras();
        if (bundle!=null) {
        }

        setValues();
        setButtonClick();

        //get data list for sync
        modelArrayList=sqliteHelper.getAllSurveyDataFromTableToSync();
        count=modelArrayList.size();
        if(count>0){
            cv_synchronise.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
            tv_synchronise.setText(getResources().getString(R.string.synchronise)+" ("+count+")");
        }else{
            //AlertDialogClass.dismissProgressDialog();
            cv_synchronise.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4285F4")));
            tv_synchronise.setText(getResources().getString(R.string.synchronise)+" ("+count+")");
        }
    }

    private void setValues() {
        tv_person_name.setText("Welcome "+ sharedPrefHelper.getString("user_name", ""));
    }

    private void setButtonClick() {
        cv_dashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainMenu.this, Dashboard.class);
                startActivity(intent);
            }
        });
        cv_synchronise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get data list for sync
                modelArrayList=sqliteHelper.getAllSurveyDataFromTableToSync();
                count=modelArrayList.size();
                if(count>0){
                    cv_synchronise.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                    tv_synchronise.setText(getResources().getString(R.string.synchronise)+" ("+count+")");
                }else{
                    //AlertDialogClass.dismissProgressDialog();
                    cv_synchronise.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4285F4")));
                    tv_synchronise.setText(getResources().getString(R.string.synchronise) + " (" +count+ ")");
                }
                if (count>0) {
                    new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Are you sure?")
                            .setContentText("Want to synchronized survey data!")
                            .setConfirmText("Submit")
                            .setCancelText("Cancel")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismiss();
                                    //sync data here
                                    sendDataOnServer();
                                }
                            })
                            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismiss();
                                }
                            })
                            .show();
                }else{
                    Toast.makeText(context, "No survey data is pending for synchronise.", Toast.LENGTH_LONG).show();
                }
            }
        });
        cv_synchronise_audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentSyncAudio = new Intent(MainMenu.this, SyncAudio.class);
                startActivity(intentSyncAudio);
            }
        });
        cv_export_db.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File filePath=SqliteHelper.getDBFile(MainMenu.this);
                try {
                    copyFileUsingStream(filePath, copyToSDcard("barc.db"));
                    Toast.makeText(MainMenu.this, "DB Exported", Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static void copyFileUsingStream(File source, File dest) throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(source);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {

                os.write(buffer, 0, length);
            }
        } finally {
            if(is != null)
                is.close();
            if(os != null)
                os.close();
        }
    }

    public File copyToSDcard(String sFileName) {
        File root = new File(Environment.getExternalStorageDirectory(), "barc_backup");
        if (!root.exists()) {
            root.mkdirs();
        } else {
            root.delete();
            File root2 = new File(Environment.getExternalStorageDirectory(), "barc_backup");
            root2.mkdirs();
            Toast.makeText(MainMenu.this, "File Replaced", Toast.LENGTH_LONG).show();
        }
        File gpxfile = new File(root, sFileName);
        return gpxfile;
    }

    private void sendDataOnServer() {
        if (CommonClass.isInternetOn(context)){
            //if (count>0){
            mProgressDialog=ProgressDialog.show(context, "", "Please Wait...", true);
                for (int i = 0; i < modelArrayList.size(); i++) {
                    String surveyID=modelArrayList.get(i).getSurvey_id();
                    String strStatus=modelArrayList.get(i).getStatus();
                    String surveyData=modelArrayList.get(i).getSurvey_data();
                    AudioSavePathInDevice=modelArrayList.get(i).getAudio_recording();

                    //Gson gson = new Gson();
                    //String data = surveyData.toString();
                    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                    RequestBody body = RequestBody.create(JSON, surveyData);

                    //send data on server
                    sendSurveyDataOnServer(body, surveyID, strStatus);
                }
            //}
        }else{
            CommonClass.showPopupForNoInternet(context);
        }
    }

    private void sendSurveyDataOnServer(RequestBody body, String survey_id,String strStatus) {
        //AlertDialogClass.showProgressDialog(context);
        ApiClient.getClient().create(BARC_API.class).sendSurveyData(body).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().toString());
                    Log.e("knckjjkc", "survey_data-: "+jsonObject.toString());
                    //AlertDialogClass.dismissProgressDialog();
                    //mProgressDialog.dismiss();
                    String success=jsonObject.getString("success");
                    String message=jsonObject.getString("message");
                    int survey_data_monitoring_id=jsonObject.getInt("survey_data_monitoring_id");
                    if (success.equals("1")) {
                        //update id on the bases of survey id
                        sqliteHelper.updateServerId("survey", survey_id, survey_data_monitoring_id);
                        if(strStatus.equals("1")){
                            sqliteHelper.updateLocalFlag("household_survey","survey", survey_id, 1);
                        }else{
                            sqliteHelper.updateLocalFlag("terminate","survey", survey_id, 1);
                        }
                        if(count>1){
                            count=count-1;
                            //AlertDialogClass.dismissProgressDialog();
                            cv_synchronise.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4285F4")));
                            tv_synchronise.setText(getResources().getString(R.string.synchronise)+" ("+count+")");
                        }else{
                            mProgressDialog.dismiss();
                            cv_synchronise.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4285F4")));
                            tv_synchronise.setText(getResources().getString(R.string.synchronise)+" (0)");
                        }
                        /*else{
                            //AlertDialogClass.dismissProgressDialog();
                            mProgressDialog.dismiss();
                            cv_synchronise.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4285F4")));
                            tv_synchronise.setText(getResources().getString(R.string.synchronise) + " (" +count+ ")");
                        }*/

                        //send audio here
                        /*if(!AudioSavePathInDevice.equals("")) {
                            Uri imageUri = Uri.parse(AudioSavePathInDevice);
                            File file = new File(imageUri.getPath());
                            RequestBody fileReqBody = RequestBody.create(MediaType.parse("Image/*"), file);
                            part = MultipartBody.Part.createFormData("audio_name", file.getName(), fileReqBody);
                            Log.e("audio_params-", "audio_params- "
                                    + "\n" + sharedPrefHelper.getString("user_id", "")
                                    + "\n" + survey_id + "\n" + survey_data_monitoring_id + "\n" + part);

                            ApiClient.getClient().create(BARC_API.class).sendAudio(sharedPrefHelper.getString("user_id", ""), survey_id, survey_data_monitoring_id, part).enqueue(new Callback<JsonObject>() {
                                @Override
                                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response.body().toString());
                                        Log.e("audio-upload", jsonObject.toString());
                                        //AlertDialogClass.dismissProgressDialog();
                                        String success = jsonObject.optString("success");
                                        String message = jsonObject.optString("message");
                                        String name = jsonObject.optString("name");
                                        String file_status = jsonObject.optString("file_status");
                                        if (success.equalsIgnoreCase("1")) {
                                            *//*if(count>0){
                                                //AlertDialogClass.dismissProgressDialog();
                                                mProgressDialog.dismiss();
                                                cv_synchronise.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                                                tv_synchronise.setText(getResources().getString(R.string.synchronise)+" ("+count+")");
                                            }else{
                                                //AlertDialogClass.dismissProgressDialog();
                                                mProgressDialog.dismiss();
                                                cv_synchronise.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4285F4")));
                                                tv_synchronise.setText(getResources().getString(R.string.synchronise) + " (" +count+ ")");
                                            }*//*
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        //AlertDialogClass.dismissProgressDialog();
                                    }
                                }

                                @Override
                                public void onFailure(Call<JsonObject> call, Throwable t) {
                                    //AlertDialogClass.dismissProgressDialog();
                                }
                            });
                        }*/
                        /*else{
                            if(count>0){
                                //AlertDialogClass.dismissProgressDialog();
                                mProgressDialog.dismiss();
                                cv_synchronise.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                                tv_synchronise.setText(getResources().getString(R.string.synchronise)+" ("+count+")");
                            }else{
                                //AlertDialogClass.dismissProgressDialog();
                                mProgressDialog.dismiss();
                                cv_synchronise.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4285F4")));
                                tv_synchronise.setText(getResources().getString(R.string.synchronise) + " (" +count+ ")");
                            }
                        }*/
                    } else {
                        //AlertDialogClass.dismissProgressDialog();
                        mProgressDialog.dismiss();
                        CommonClass.showPopupForNoInternet(context);
                    }

                } catch (Exception e) {
                    Log.e("Exception Sync",e.getMessage().toString());
                    e.printStackTrace();
                    mProgressDialog.dismiss();
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("Exception Sync failure",t.getMessage().toString());
                Toast.makeText(context, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                //AlertDialogClass.dismissProgressDialog();
                mProgressDialog.dismiss();
            }
        });
    }

    private void initialization() {
        modelArrayList=new ArrayList<>();
        sharedPrefHelper =new SharedPrefHelper(this);
        sqliteHelper=new SqliteHelper(this);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) finish();
        if (item.getItemId()==R.id.logout){
            /*sharedPrefHelper.setString("user_name_password", "");
            sharedPrefHelper.setString("user_name", "");*/
            sharedPrefHelper.setString("isLogin", "");
            Intent intentLoginActivity=new Intent(context, LoginActivity.class);
            intentLoginActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intentLoginActivity);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //showPopupForTerminateSurvey();
    }

    private void showPopupForTerminateSurvey() {
        new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Are you sure?")
                .setContentText("Want to exit from the application!")
                .setConfirmText("Yes")
                .setCancelText("No")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismiss();
                        finish();
                    }
                })
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismiss();
                    }
                })
                .show();
    }
}