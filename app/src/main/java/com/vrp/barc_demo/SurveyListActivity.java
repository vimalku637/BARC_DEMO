package com.vrp.barc_demo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.textview.MaterialTextView;
import com.vrp.barc_demo.adapters.SurveyListAdapter;
import com.vrp.barc_demo.interfaces.ClickListener;
import com.vrp.barc_demo.models.SurveyModel;
import com.vrp.barc_demo.sqlite_db.SqliteHelper;
import com.vrp.barc_demo.utils.SharedPrefHelper;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SurveyListActivity extends AppCompatActivity {
    private static final String TAG = "SurveyListActivity";
    @BindView(R.id.rv_survey_List)
    RecyclerView rv_survey_List;
    @BindView(R.id.tv_oops_no_data)
    MaterialTextView tv_oops_no_data;

    /*normal widgets*/
    private Context context=this;
    private SqliteHelper sqliteHelper;
    private SharedPrefHelper sharedPrefHelper;
    private ArrayList<SurveyModel> surveyModelAl;
    private SurveyListAdapter mSurveyListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_list);
        ButterKnife.bind(this);
        setTitle(R.string.survey_list);
        initialization();
        /*get intent values here*/
        Bundle bundle=getIntent().getExtras();
        if (bundle!=null) {
        }

        setSurveyAdapter();
    }

    private void setSurveyAdapter() {
        surveyModelAl=sqliteHelper.getSurveyList();
        if (surveyModelAl.size()>0) {
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
            mSurveyListAdapter = new SurveyListAdapter(context, surveyModelAl);
            rv_survey_List.setLayoutManager(mLayoutManager);
            rv_survey_List.setAdapter(mSurveyListAdapter);

            mSurveyListAdapter.onItemClick(new ClickListener() {
                @Override
                public void onItemClick(int position) {
                    Intent intentSurveyActivity=new Intent(context, SurveyActivity.class);
                    intentSurveyActivity.putExtra("survey_id", surveyModelAl.get(position).getSurvey_id());
                    intentSurveyActivity.putExtra("screen_type", "edit_survey");
                    startActivity(intentSurveyActivity);
                }
            });

        } else {
            tv_oops_no_data.setVisibility(View.VISIBLE);
        }
    }

    private void initialization() {
        sqliteHelper=new SqliteHelper(this);
        sharedPrefHelper=new SharedPrefHelper(this);
        surveyModelAl=new ArrayList<>();
    }
}
