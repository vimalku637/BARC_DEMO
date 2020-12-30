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

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.vrp.barc_demo.Dashboard;
import com.vrp.barc_demo.R;
import com.vrp.barc_demo.login.LoginActivity;
import com.vrp.barc_demo.models.AnswerModel;
import com.vrp.barc_demo.models.SurveyModel;
import com.vrp.barc_demo.rest_api.ApiClient;
import com.vrp.barc_demo.rest_api.BARC_API;
import com.vrp.barc_demo.sqlite_db.SqliteHelper;
import com.vrp.barc_demo.utils.AlertDialogClass;
import com.vrp.barc_demo.utils.CommonClass;
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

public class MainMenu extends AppCompatActivity {
    @BindView(R.id.cv_dashboard)
    MaterialCardView cv_dashboard;
    @BindView(R.id.cv_synchronise)
    MaterialCardView cv_synchronise;
    @BindView(R.id.tv_person_name)
    MaterialTextView tv_person_name;
    @BindView(R.id.tv_synchronise)
    MaterialTextView tv_synchronise;

    /*normal widgets*/
    private Context context=this;
    SharedPrefHelper sharedPrefHelper;
    SqliteHelper sqliteHelper;
    private ArrayList<SurveyModel> modelArrayList;

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
        if(modelArrayList.size()>0){
            cv_synchronise.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
            tv_synchronise.setText(getResources().getString(R.string.synchronise)+" ("+modelArrayList.size()+")");
        }else{
            cv_synchronise.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4285F4")));
            tv_synchronise.setText(getResources().getString(R.string.synchronise)+" ("+0+")");
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
                if (modelArrayList.size()>0) {
                    new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Are you sure?")
                            .setContentText("Want to synchronized survey data!")
                            .setConfirmText("Submit")
                            .setCancelText("Cancel")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    //sync data here
                                    sendDataOnServer(sDialog);
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
    }

    private void sendDataOnServer(SweetAlertDialog sDialog) {
        if (CommonClass.isInternetOn(context)){
            if (modelArrayList.size()>0){
                for (int i = 0; i < modelArrayList.size(); i++) {
                    String surveyID=modelArrayList.get(i).getSurvey_id();
                    String surveyData=modelArrayList.get(i).getSurvey_data();

                    //Gson gson = new Gson();
                    String data = surveyData.toString();
                    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                    RequestBody body = RequestBody.create(JSON, data);
                    //send data on server
                    sendSurveyDataOnServer(body, surveyID, sDialog);
                }
            }
        }else{
            CommonClass.showPopupForNoInternet(context);
        }
    }

    private void sendSurveyDataOnServer(RequestBody body, String survey_id, SweetAlertDialog sDialog) {
        AlertDialogClass.showProgressDialog(context);
        ApiClient.getClient().create(BARC_API.class).sendSurveyData(body).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().toString());
                    Log.e("knckjjkc", "survey_data-: "+jsonObject.toString());
                    String success=jsonObject.getString("success");
                    String message=jsonObject.getString("message");
                    int survey_data_monitoring_id=jsonObject.getInt("survey_data_monitoring_id");
                    if (success.equals("1")) {
                        AlertDialogClass.dismissProgressDialog();
                        //update id on the bases of survey id
                        sqliteHelper.updateServerId("survey", Integer.parseInt(survey_id), survey_data_monitoring_id);
                        sqliteHelper.updateLocalFlag("household_survey","survey", Integer.parseInt(survey_id), 1);

                        //change text color & count
                        cv_synchronise.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4285F4")));
                        tv_synchronise.setText(getResources().getString(R.string.synchronise)+" ("+0+")");
                        //open success message
                        sDialog
                                .setTitleText("Success!")
                                .setContentText("Data has been successfully Synchronized!")
                                .setConfirmText("OK")
                                .setConfirmClickListener(null)
                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
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

    private void initialization() {
        modelArrayList=new ArrayList<>();
        sharedPrefHelper =new SharedPrefHelper(this);
        sqliteHelper=new SqliteHelper(this);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) finish();
        if (item.getItemId()==R.id.logout){
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