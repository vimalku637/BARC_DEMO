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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.JsonArray;
import com.vrp.barc_demo.R;
import com.vrp.barc_demo.adapters.SurveyListAdapter;
import com.vrp.barc_demo.interfaces.ClickListener;
import com.vrp.barc_demo.login.LoginActivity;
import com.vrp.barc_demo.models.AnswerModel;
import com.vrp.barc_demo.models.SurveyModel;
import com.vrp.barc_demo.sqlite_db.SqliteHelper;
import com.vrp.barc_demo.utils.SharedPrefHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SurveyListActivity extends AppCompatActivity {
    private static final String TAG = "SurveyListActivity";
    @BindView(R.id.rv_survey_List)
    RecyclerView rv_survey_List;
    @BindView(R.id.tv_oops_no_data)
    MaterialTextView tv_oops_no_data;
    @BindView(R.id.btn_start_next_survey)
    MaterialButton btn_start_next_survey;
    @BindView(R.id.tb_layout)
    TableLayout tb_layout;
    @BindView(R.id.tv_SurveysHaltCount)
    TextView tv_SurveysHaltCount;
    @BindView(R.id.tv_SurveysCompletedcount)
    TextView tv_SurveysCompletedcount;
    @BindView(R.id.tv_SurveysRejectedCount)
    TextView tv_SurveysRejectedCount;
    @BindView(R.id.tv_HouseHoldcount)
    TextView tv_HouseHoldcount;
    @BindView(R.id.tv_teminated)
    TextView tv_teminated;
    @BindView(R.id.tv_survey_status_in_cluster)
            MaterialTextView tv_survey_status_in_cluster;

    int strTotalSurvey;
    int countProgress;
    int countReject;
    int countComplete;
    int countHouseHold;
    int countTerminate;

    /*normal widgets*/
    private Context context=this;
    private SqliteHelper sqliteHelper;
    private SharedPrefHelper sharedPrefHelper;
    private ArrayList<SurveyModel> surveyModelAl;
    private SurveyListAdapter mSurveyListAdapter;
    private String surveyObjectJSON=null;
    private String surveyFamilyObjectJSON=null;
    private String surveyTVObjectJSON=null;
    JSONObject jsonAnswers=null;
    JSONObject jsonAnswersFamily=null;
    JSONObject jsonAnswersTV=null;
    JSONArray jsonArrayAnswers=null;
    JSONArray jsonFamilyArrayAnswers=null;
    JSONArray jsonTVArrayAnswers=null;
    int totalAnswers=0;
    ArrayList<AnswerModel> answerModelList;
    ArrayList<AnswerModel> answerModelHouseholdMemberList;
    ArrayList<AnswerModel> answerModelTVList;
    private String original_address="", cluster_id="", cluster_name="",
            next_address="", previous_address="";
    int sampleSize=0;
    int totalSurveyForCluster=0;
    LocationManager manager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_list);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
        ButterKnife.bind(this);
        setTitle(R.string.survey_list);
        initialization();
        /*get intent values here*/
        Bundle bundle=getIntent().getExtras();
        if (bundle!=null) {
            /*original_address=bundle.getString("original_address", "");
            next_address=bundle.getString("next_address", "");
            previous_address=bundle.getString("previous_address", "");
            cluster_id=bundle.getString("cluster_id", "");
            cluster_name=bundle.getString("cluster_name", "");*/
        }

        setSurveyAdapter();
        setButtonClick();
        setValues();

        /*get survey sample from table*/
        sampleSize=sqliteHelper.getClusterSampleSizeFromTable(sharedPrefHelper.getString("cluster_no", ""));
        //totalSurveyForCluster=sqliteHelper.getTotalSurveyForCluster(sharedPrefHelper.getString("cluster_no", ""));
        totalSurveyForCluster=sqliteHelper.getClusterCompletedFromTable(sharedPrefHelper.getString("cluster_no", ""),"completed_record");
        countReject=sqliteHelper.getClusterCompletedFromTable(sharedPrefHelper.getString("cluster_no", ""),"tot_rejected");
        sharedPrefHelper.setInt("sampleSize",sampleSize);
        sharedPrefHelper.setInt("totalSurvey",totalSurveyForCluster);

        countProgress = sqliteHelper.getTotalchartInprogress(1);
        tv_SurveysHaltCount.setText(""+countProgress);
        //countReject = sqliteHelper.getChartValue(4,1);
        tv_SurveysRejectedCount.setText(""+countReject);
        countComplete = totalSurveyForCluster;//sqliteHelper.getChartValue(1,1);
        tv_SurveysCompletedcount.setText(""+countComplete);
        countTerminate = sqliteHelper.getTotalchart4(3,1);
        tv_teminated.setText(""+countTerminate);
        countHouseHold = countProgress+countTerminate+countComplete+countReject;//sqliteHelper.getTotalsurveyhousehold(1);
        tv_HouseHoldcount.setText(""+countHouseHold);


    }

    private void setValues() {
        tv_survey_status_in_cluster.setText("Survey status in your cluster"+" "+sharedPrefHelper.getString("cluster_no",""));
    }

    private void setButtonClick() {
        btn_start_next_survey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (totalSurveyForCluster>=sampleSize){
                    Toast.makeText(context, "You have completed all survey for this cluster.", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    buildAlertMessageNoGps();
                }
                else{
                Intent intentAddressSelection=new Intent(context, AddressSelection.class);
                /*intentAddressSelection.putExtra("original_address", original_address);
                intentAddressSelection.putExtra("previous_address", previous_address);
                intentAddressSelection.putExtra("next_address", next_address);
                intentAddressSelection.putExtra("cluster_id", cluster_id);
                intentAddressSelection.putExtra("cluster_name", cluster_name);*/
                intentAddressSelection.putExtra("screen_type", "survey");
                startActivity(intentAddressSelection);
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

    private void setSurveyAdapter() {
        surveyModelAl=sqliteHelper.getSurveyList();
        if (surveyModelAl.size()>0) {
            tb_layout.setVisibility(View.VISIBLE);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
            mSurveyListAdapter = new SurveyListAdapter(context, surveyModelAl);
            rv_survey_List.setLayoutManager(mLayoutManager);
            rv_survey_List.setAdapter(mSurveyListAdapter);

            mSurveyListAdapter.onItemClick(new ClickListener() {
                @Override
                public void onItemClick(int position) {
                    if (surveyModelAl.get(position).getStatus().equals("2")||surveyModelAl.get(position).getStatus().equals("0")) {
                        if (totalSurveyForCluster>=sampleSize) {
                            Toast.makeText(context, "You have completed all survey for this cluster.", Toast.LENGTH_SHORT).show();
                            return;
                        }else{
                            sharedPrefHelper.setInt("startPosition", 0);
                            sharedPrefHelper.setInt("endPosition", 0);
                            Intent intentSurveyActivity = new Intent(context, HouseholdSurveyActivity.class);
                            intentSurveyActivity.putExtra("survey_id", surveyModelAl.get(position).getSurvey_id());
                            sharedPrefHelper.setString("survey_id", surveyModelAl.get(position).getSurvey_id());
                            getAllSurveyDataFromTable(surveyModelAl.get(position).getSurvey_id());
                            intentSurveyActivity.putExtra("answerModelList", answerModelList);
                            intentSurveyActivity.putExtra("answerModelListFamily", answerModelHouseholdMemberList);
                            intentSurveyActivity.putExtra("answerModelListTV", answerModelTVList);
                            intentSurveyActivity.putExtra("screen_type", "survey_list");
                           /* String dateTime=surveyModelAl.get(position).getDate_time();
                            if(dateTime.equals("")){
                                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                                Calendar cal = Calendar.getInstance();
                                dateTime=dateFormat.format(cal.getTime());
                            }
                            sharedPrefHelper.setString("dateTime", dateTime);*/
                            startActivity(intentSurveyActivity);
                        }
                    }else{
                        Toast.makeText(context, "You are not able to edit this survey.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } else {
            //tv_oops_no_data.setVisibility(View.VISIBLE);
            tb_layout.setVisibility(View.GONE);
        }
    }

    private void getAllSurveyDataFromTable(String survey_id) {
        surveyObjectJSON = sqliteHelper.getSurveyData(survey_id);
        surveyFamilyObjectJSON = sqliteHelper.getSurveyFamilyData(survey_id);
        surveyTVObjectJSON = sqliteHelper.getSurveyTvData(survey_id);
        try {
            jsonAnswers= new JSONObject(surveyObjectJSON);
            if (jsonAnswers.has("survey_data")) {
                jsonArrayAnswers=jsonAnswers.getJSONArray("survey_data");
                Log.e("survey_data", "onCreate: " + jsonArrayAnswers.toString());
                if(jsonArrayAnswers.length()>0){
                    for (int i = 0; i < jsonArrayAnswers.length(); i++) {
                        JSONObject jsonObjectAns=jsonArrayAnswers.getJSONObject(i);
                        AnswerModel answerModel=new AnswerModel();
                        answerModel.setOption_id(jsonObjectAns.getString("option_id"));
                        answerModel.setOption_value(jsonObjectAns.getString("option_value"));
                        answerModel.setQuestionID(jsonObjectAns.getString("question_id"));
                        answerModel.setPre_field(jsonObjectAns.getString("pre_field"));
                        answerModel.setField_name(jsonObjectAns.getString("field_name"));
                        answerModelList.add(answerModel);
                    }
                }
            }
            if (surveyFamilyObjectJSON!=null&&!surveyFamilyObjectJSON.isEmpty()) {
                jsonAnswersFamily = new JSONObject(surveyFamilyObjectJSON);
                if (jsonAnswersFamily.has("family_data")) {
                    jsonFamilyArrayAnswers = jsonAnswersFamily.getJSONArray("family_data");
                    Log.e("family_data", "onCreate: " + jsonFamilyArrayAnswers.toString());
                    if (jsonFamilyArrayAnswers.length() > 0) {
                        for (int i = 0; i < jsonFamilyArrayAnswers.length(); i++) {
                            JSONObject jsonObjectF = jsonFamilyArrayAnswers.getJSONObject(i);
                            AnswerModel answerModel = new AnswerModel();
                            answerModel.setOption_id(jsonObjectF.getString("option_id"));
                            answerModel.setOption_value(jsonObjectF.getString("option_value"));
                            answerModel.setQuestionID(jsonObjectF.getString("question_id"));
                            answerModel.setPre_field(jsonObjectF.getString("pre_field"));
                            answerModel.setField_name(jsonObjectF.getString("field_name"));
                            answerModelHouseholdMemberList.add(answerModel);
                        }
                    }
                }
            }
            if (surveyTVObjectJSON!=null&&!surveyTVObjectJSON.isEmpty()) {
                jsonAnswersTV = new JSONObject(surveyTVObjectJSON);
                if (jsonAnswersTV.has("tv_data")) {
                    jsonTVArrayAnswers=jsonAnswersTV.getJSONArray("tv_data");
                    Log.e("tv_data", "onCreate: " + jsonTVArrayAnswers.toString());
                    if(jsonTVArrayAnswers.length()>0){
                        for (int i = 0; i < jsonTVArrayAnswers.length(); i++) {
                            JSONObject jsonObjectT = jsonTVArrayAnswers.getJSONObject(i);
                            AnswerModel answerModel = new AnswerModel();
                            answerModel.setOption_id(jsonObjectT.getString("option_id"));
                            answerModel.setOption_value(jsonObjectT.getString("option_value"));
                            answerModel.setQuestionID(jsonObjectT.getString("question_id"));
                            answerModel.setPre_field(jsonObjectT.getString("pre_field"));
                            answerModel.setField_name(jsonObjectT.getString("field_name"));
                            answerModelTVList.add(answerModel);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initialization() {
        sqliteHelper=new SqliteHelper(this);
        sharedPrefHelper=new SharedPrefHelper(this);
        surveyModelAl=new ArrayList<>();
        answerModelList=new ArrayList<>();
        answerModelHouseholdMemberList=new ArrayList<>();
        answerModelTVList=new ArrayList<>();
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) finish();
        if (item.getItemId()==R.id.home_icon) {
            Intent intentMainMenu=new Intent(context, MainMenu.class);
            intentMainMenu.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intentMainMenu);
        }
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
}
