package com.vrp.barc_demo.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.vrp.barc_demo.R;
import com.vrp.barc_demo.sqlite_db.SqliteHelper;
import com.vrp.barc_demo.utils.SharedPrefHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.http.Query;

public class TerminateActivity extends AppCompatActivity {
    @BindView(R.id.cl_terminate)
    ConstraintLayout cl_terminate;
    @BindView(R.id.btn_next)
    MaterialButton btn_next;
    @BindView(R.id.tv_survey_terminate)
    MaterialTextView tv_survey_terminate;
    @BindView(R.id.btn_start_new_survey)
    MaterialButton btn_start_new_survey;
    @BindView(R.id.et_date_time)
    TextInputEditText et_date_time;

    /*normal widgets*/
    private Context context=this;
    private String screen_type="";
    private SqliteHelper sqliteHelper;
    private SharedPrefHelper sharedPrefHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terminate);
        /*getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);*/
        ButterKnife.bind(this);
        setTitle(R.string.terminate);
        initialization();
        /*get intent values here*/
        Bundle bundle=getIntent().getExtras();
        if (bundle!=null) {
            screen_type=bundle.getString("screen_type", "");
        }

        setValues();
        setButtonClick();
    }

    private void setValues() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        et_date_time.setText(dateFormat.format(cal.getTime()));
    }

    private void setButtonClick() {
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cl_terminate.setVisibility(View.GONE);
                tv_survey_terminate.setVisibility(View.VISIBLE);
                btn_start_new_survey.setVisibility(View.VISIBLE);
            }
        });
        btn_start_new_survey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentSurveyList = new Intent(context, SurveyListActivity.class);
                startActivity(intentSurveyList);
            }
        });
    }

    private void initialization() {
        sqliteHelper=new SqliteHelper(this);
        sharedPrefHelper=new SharedPrefHelper(this);
    }

    /*@Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) finish();
        return super.onOptionsItemSelected(item);
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        /*hide and show toolbar items*/
        if (screen_type.equalsIgnoreCase("terminate")) {
            MenuItem item_stop_survey=menu.findItem(R.id.stop_survey);
            item_stop_survey.setVisible(false);
            MenuItem item_logout=menu.findItem(R.id.logout);
            item_logout.setVisible(false);
        }

        return true;
    }
}
