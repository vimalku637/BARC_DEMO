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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
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
import com.vrp.barc_demo.R;
import com.vrp.barc_demo.adapters.SurveyListAdapter;
import com.vrp.barc_demo.interfaces.ClickListener;
import com.vrp.barc_demo.models.AnswerModel;
import com.vrp.barc_demo.models.SurveyModel;
import com.vrp.barc_demo.sqlite_db.SqliteHelper;
import com.vrp.barc_demo.utils.SharedPrefHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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
    JSONObject jsonAnswers=null;
    JSONArray jsonArrayAnswers=null;
    int totalAnswers;
    ArrayList<AnswerModel> answerModelList;
    private String original_address="", cluster_id="", cluster_name="",
            next_address="", previous_address="";

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

        countProgress = sqliteHelper.getTotalchartInprogress();
        tv_SurveysHaltCount.setText(""+countProgress);
        countReject = sqliteHelper.getChartValue(5);
        tv_SurveysRejectedCount.setText(""+countReject);
        countComplete = sqliteHelper.getChartValue(1);
        tv_SurveysCompletedcount.setText(""+countComplete);
        countHouseHold = sqliteHelper.getTotalsurveyhousehold();
        tv_HouseHoldcount.setText(""+countHouseHold);
        countTerminate = sqliteHelper.getTotalchart4(3);
        tv_teminated.setText(""+countTerminate);

    }

    private void setButtonClick() {
        btn_start_next_survey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentAddressSelection=new Intent(context, AddressSelection.class);
                /*intentAddressSelection.putExtra("original_address", original_address);
                intentAddressSelection.putExtra("previous_address", previous_address);
                intentAddressSelection.putExtra("next_address", next_address);
                intentAddressSelection.putExtra("cluster_id", cluster_id);
                intentAddressSelection.putExtra("cluster_name", cluster_name);*/
                intentAddressSelection.putExtra("screen_type", "survey");
                startActivity(intentAddressSelection);
            }
        });
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
                    if (surveyModelAl.get(position).getStatus().equals("2")||surveyModelAl.get(position).getStatus().equals("4")) {
                        sharedPrefHelper.setInt("startPosition", 0);
                        sharedPrefHelper.setInt("endPosition", 0);
                        Intent intentSurveyActivity = new Intent(context, HouseholdSurveyActivity.class);
                        intentSurveyActivity.putExtra("survey_id", surveyModelAl.get(position).getSurvey_id());
                        getAllSurveyDataFromTable(surveyModelAl.get(position).getSurvey_id());
                        intentSurveyActivity.putExtra("answerModelList", answerModelList);
                        intentSurveyActivity.putExtra("screen_type", "survey_list");
                        startActivity(intentSurveyActivity);
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
        try {
            jsonAnswers= new JSONObject(surveyObjectJSON);
            if (jsonAnswers.has("survey_data")) {
                jsonArrayAnswers=jsonAnswers.getJSONArray("survey_data");
                totalAnswers=jsonArrayAnswers.length();
                Log.e("survey_data", "onCreate: " + jsonArrayAnswers.toString());
                if(totalAnswers>0){
                    for (int i = 0; i < totalAnswers; i++) {
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initialization() {
        sqliteHelper=new SqliteHelper(this);
        sharedPrefHelper=new SharedPrefHelper(this);
        surveyModelAl=new ArrayList<>();
        answerModelList=new ArrayList<>();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) finish();
        if (item.getItemId()==R.id.home_icon) {
            Intent intentMainMenu=new Intent(context, MainMenu.class);
            intentMainMenu.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intentMainMenu);
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
