package com.vrp.barc_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.vrp.barc_demo.utils.SharedPrefHelper;

import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "Home_Activity";
    @BindView(R.id.tv_start_survey)
    TextView tv_start_survey;
    @BindView(R.id.tv_edit_survey)
    TextView tv_edit_survey;

    /*normal widgets*/
    private Context context=this;
    SharedPrefHelper sharedPrefHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        setTitle(R.string.main_menu);
        initialization();

        setButtonClick();
    }

    private void initialization() {
        sharedPrefHelper=new SharedPrefHelper(this);
    }

    private void setButtonClick() {
        tv_start_survey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Long aLong = System.currentTimeMillis()/1000;
                String uuid = aLong.toString();
                Intent intentSurveyActivity=new Intent(context, SurveyActivity.class);
                intentSurveyActivity.putExtra("survey_id", uuid);
                sharedPrefHelper.setInt("startPosition",0);
                sharedPrefHelper.setInt("endPosition",0);
                startActivity(intentSurveyActivity);
            }
        });
        tv_edit_survey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}
