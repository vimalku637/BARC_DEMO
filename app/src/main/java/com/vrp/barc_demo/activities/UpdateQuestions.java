package com.vrp.barc_demo.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.gson.JsonObject;
import com.vrp.barc_demo.R;
import com.vrp.barc_demo.rest_api.ApiClient;
import com.vrp.barc_demo.rest_api.BARC_API;
import com.vrp.barc_demo.utils.AlertDialogClass;
import com.vrp.barc_demo.utils.CommonClass;
import com.vrp.barc_demo.utils.MyJSON;
import com.vrp.barc_demo.utils.SharedPrefHelper;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateQuestions extends AppCompatActivity {
    private static final String TAG = "Update_Survey";
    @BindView(R.id.btn_submit)
    MaterialButton btn_submit;
    @BindView(R.id.spn_select_survey)
    Spinner spn_select_survey;

    /*normal widgets*/
    private Context context=this;
    private SharedPrefHelper sharedPrefHelper;
    private ArrayList<String> surveySpnAL;

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

        setSurveySpinner();
        setButtonClick();
    }

    private void setSurveySpinner() {
        surveySpnAL.add(0, getString(R.string.select_survey));
        surveySpnAL.add(1, "Household 1");
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
    }

    private void initialization() {
        sharedPrefHelper=new SharedPrefHelper(this);
        surveySpnAL=new ArrayList<>();
    }

    private void callDownloadSurveyApi() {
        AlertDialogClass.showProgressDialog(context);
        ApiClient.getClient().create(BARC_API.class).getBarcDemoJson().enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    JsonObject jsonObject = response.body();
                    Log.e(TAG, "onResponse: "+jsonObject.toString());
                    String surveyJSON=jsonObject.toString();
                    //to save all JSON into json file
                    if (surveyJSON.length()>0) {
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
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
}
