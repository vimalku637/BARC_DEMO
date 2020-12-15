package com.vrp.barc_demo.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.vrp.barc_demo.R;
import com.vrp.barc_demo.activities.HouseholdSurveyActivity;
import com.vrp.barc_demo.activities.SurveyActivity;
import com.vrp.barc_demo.utils.SharedPrefHelper;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class ClusterDetails extends AppCompatActivity {
    @BindView(R.id.tv_cluster_id)
    MaterialTextView tv_cluster_id;
    @BindView(R.id.tv_cluster_name)
    MaterialTextView tv_cluster_name;
    @BindView(R.id.btn_take_survey)
    MaterialButton btn_take_survey;
    @BindView(R.id.spn_language)
    Spinner spn_language;

    /*normal widgets*/
    private Context context=this;
    private SharedPrefHelper sharedPrefHelper;
    private String cluster_id="";
    private String cluster_name="";
    private String screen_type="";
    private ArrayList<String> languagesSpnAL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cluster_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        ButterKnife.bind(this);
        setTitle(R.string.view_starting_points);
        initialization();
        /*get intent values here*/
        Bundle bundle=getIntent().getExtras();
        if (bundle!=null) {
            cluster_id=bundle.getString("cluster_id", "");
            cluster_name=bundle.getString("cluster_name", "");
            screen_type=bundle.getString("screen_type", "");
        }

        setValues();
        setLanguageSpinner();
        setButtonClick();
    }

    private void setLanguageSpinner() {
        languagesSpnAL.add(0, getString(R.string.select_language));
        languagesSpnAL.add(1, "English");
        languagesSpnAL.add(2, "Hindi");
        ArrayAdapter arrayAdapter=new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, languagesSpnAL);
        spn_language.setAdapter(arrayAdapter);
        spn_language.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setValues() {
        tv_cluster_id.setText("Cluster id: "+cluster_id);
        tv_cluster_name.setText("Cluster Name: "+cluster_name);
    }

    private void setButtonClick() {
        btn_take_survey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Long aLong = System.currentTimeMillis()/1000;
                String uuid = aLong.toString();
                sharedPrefHelper.setString("survey_id", uuid);
                Intent intentSurveyActivity=new Intent(context, HouseholdSurveyActivity.class);
                intentSurveyActivity.putExtra("survey_id", uuid);
                intentSurveyActivity.putExtra("screen_type", "survey");
                sharedPrefHelper.setInt("startPosition",0);
                sharedPrefHelper.setInt("endPosition",1);
                startActivity(intentSurveyActivity);
            }
        });
    }

    private void initialization() {
        sharedPrefHelper=new SharedPrefHelper(this);
        languagesSpnAL=new ArrayList<>();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) finish();
        if (item.getItemId()==R.id.stop_survey) {
            showPopupForTerminateSurvey();
        }
        if (item.getItemId()==R.id.home_icon) {
            Intent intentMainMenu=new Intent(context, MainMenu.class);
            intentMainMenu.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intentMainMenu);
        }
        return super.onOptionsItemSelected(item);
    }

    private void showPopupForTerminateSurvey() {
        new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Are you sure?")
                .setContentText("Want to terminate the interview!")
                .setConfirmText("Yes")
                .setCancelText("No")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismiss();
                        setTerminattion();
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

    public void setTerminattion(){
        Intent intentTerminate=new Intent(context, TerminateActivity.class);
        intentTerminate.putExtra("screen_type", "terminate");
        startActivity(intentTerminate);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        /*hide and show toolbar items*/
        if (screen_type.equalsIgnoreCase("survey")) {
            MenuItem item_stop_survey=menu.findItem(R.id.stop_survey);
            item_stop_survey.setVisible(true);
            MenuItem item_logout=menu.findItem(R.id.logout);
            item_logout.setVisible(false);
        }

        return true;
    }
}