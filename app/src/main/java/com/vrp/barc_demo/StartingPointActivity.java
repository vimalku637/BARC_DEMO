package com.vrp.barc_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StartingPointActivity extends AppCompatActivity {
    @BindView(R.id.ll_open)
    LinearLayout ll_open;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting_point);
        ButterKnife.bind(this);
        setTitle(R.string.starting_points_details);
        initialization();
        /*get intent values here*/
        Bundle bundle=getIntent().getExtras();
        if (bundle!=null) {
        }

        setButtonClick();
    }

    private void setButtonClick() {
        ll_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(StartingPointActivity.this, OptionScreen.class);
                startActivity(intent);
            }
        });
    }

    private void initialization() {
    }
}