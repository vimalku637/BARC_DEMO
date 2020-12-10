package com.vrp.barc_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ClusterDetails extends AppCompatActivity {
    @BindView(R.id.tv_take_survey)
    TextView tv_take_survey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cluster_details);
        ButterKnife.bind(this);
        setTitle(R.string.view_starting_points);
        initialization();
        /*get intent values here*/
        Bundle bundle=getIntent().getExtras();
        if (bundle!=null) {
        }

        setButtonClick();
    }

    private void setButtonClick() {
        tv_take_survey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ClusterDetails.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initialization() {
    }
}