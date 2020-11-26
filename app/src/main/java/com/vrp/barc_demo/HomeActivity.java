package com.vrp.barc_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.UUID;

public class HomeActivity extends AppCompatActivity {
    private TextView tv_start_survey, tv_edit_survey;
    /*normal widgets*/
    private Context context=this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initViews();

        setButtonClick();
    }

    private void setButtonClick() {
        tv_start_survey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //String uuid = UUID.randomUUID().toString();
                Long aLong = System.currentTimeMillis()/1000;
                String uuid = aLong.toString();
                Intent intentSurveyActivity=new Intent(context, SurveyActivity.class);
                intentSurveyActivity.putExtra("survey_id", uuid);
                startActivity(intentSurveyActivity);
                Toast.makeText(HomeActivity.this, ""+uuid, Toast.LENGTH_LONG).show();
            }
        });
        tv_edit_survey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void initViews() {
        tv_start_survey=findViewById(R.id.tv_start_survey);
        tv_edit_survey=findViewById(R.id.tv_edit_survey);
    }
}
