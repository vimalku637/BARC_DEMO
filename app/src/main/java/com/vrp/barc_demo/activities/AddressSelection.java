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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.vrp.barc_demo.R;
import com.vrp.barc_demo.models.SurveyModel;
import com.vrp.barc_demo.utils.SharedPrefHelper;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class AddressSelection extends AppCompatActivity {
    @BindView(R.id.rg_address)
    RadioGroup rg_address;
    @BindView(R.id.rb_original_address)
    RadioButton rb_original_address;
    @BindView(R.id.rb_next_address)
    RadioButton rb_next_address;
    @BindView(R.id.rb_previous_address)
    RadioButton rb_previous_address;
    @BindView(R.id.rb_substituted_address)
    RadioButton rb_substituted_address;
    @BindView(R.id.tv_original_address)
    MaterialTextView tv_original_address;
    @BindView(R.id.btn_start)
    MaterialButton btn_start;
    @BindView(R.id.spn_railway_station)
    Spinner spn_railway_station;
    @BindView(R.id.rl_layout_substituted)
    RelativeLayout rl_layout_substituted;
    @BindView(R.id.rl_layout)
    RelativeLayout rl_layout;
    @BindView(R.id.tv_next_address)
    MaterialTextView tv_next_address;
    @BindView(R.id.rl_layout_next)
    RelativeLayout rl_layout_next;
    @BindView(R.id.btn_start_next)
    MaterialButton btn_start_next;
    @BindView(R.id.rl_layout_previous)
    RelativeLayout rl_layout_previous;
    @BindView(R.id.tv_previous_address)
    MaterialTextView tv_previous_address;
    @BindView(R.id.btn_start_previous)
    MaterialButton btn_start_previous;
    @BindView(R.id.et_reasonNextAd)
    EditText et_reasonNextAd;
    @BindView(R.id.et_reasonPreviousAd)
    EditText et_reasonPreviousAd;
    @BindView(R.id.et_reasonSubstituteAd)
    EditText et_reasonSubstituteAd;

    /*normal widgets*/
    private Context context = this;
    private String original_address = "", cluster_id = "", cluster_name = "", screen_type = "",
            previous_address = "", next_address = "", address_type="";
    private ArrayList<String> railwayStationSpnAL;
    private SharedPrefHelper sharedPrefHelper;
    SurveyModel surveyModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_selection);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        ButterKnife.bind(this);
        setTitle(R.string.view_starting_points);
        surveyModel=new SurveyModel();
        initialization();
        /*get intent values here*/
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            /*original_address=bundle.getString("original_address", "");
            cluster_id=bundle.getString("cluster_id", "");
            cluster_name=bundle.getString("cluster_name", "");*/
            screen_type = bundle.getString("screen_type", "");
            /*previous_address=bundle.getString("previous_address", "");
            next_address=bundle.getString("next_address", "");*/
        }

        getPreferencesData();
        setValues();
        setRailwayStationSpinner();
        setRadioButtonClick();
        setButtonClick();
    }

    private void getPreferencesData() {
        original_address = sharedPrefHelper.getString("original_address", "");
        next_address = sharedPrefHelper.getString("next_address", "");
        previous_address = sharedPrefHelper.getString("previous_address", "");

        sharedPrefHelper.setString("address_type",et_reasonNextAd.getText().toString().trim()+" ("+address_type+")");
        sharedPrefHelper.setString("address_type",et_reasonPreviousAd.getText().toString().trim()+" ("+address_type+")");
        sharedPrefHelper.setString("address_type",et_reasonSubstituteAd.getText().toString().trim()+" ("+address_type+")");
    }

    private void setRadioButtonClick() {
        rg_address.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rb_original_address:
                        rl_layout_substituted.setVisibility(View.GONE);
                        rl_layout.setVisibility(View.VISIBLE);
                        rl_layout_next.setVisibility(View.GONE);
                        rl_layout_previous.setVisibility(View.GONE);
                        break;
                    case R.id.rb_next_address:
                        address_type="Next";
                        rl_layout_substituted.setVisibility(View.GONE);
                        rl_layout.setVisibility(View.GONE);
                        rl_layout_next.setVisibility(View.VISIBLE);
                        rl_layout_previous.setVisibility(View.GONE);
                        break;
                    case R.id.rb_previous_address:
                        address_type="Previous";
                        rl_layout_substituted.setVisibility(View.GONE);
                        rl_layout.setVisibility(View.GONE);
                        rl_layout_next.setVisibility(View.GONE);
                        rl_layout_previous.setVisibility(View.VISIBLE);
                        break;
                    case R.id.rb_substituted_address:
                        address_type="Substituted";
                        rl_layout_substituted.setVisibility(View.VISIBLE);
                        rl_layout.setVisibility(View.GONE);
                        rl_layout_next.setVisibility(View.GONE);
                        rl_layout_previous.setVisibility(View.GONE);
                        break;
                }
            }
        });
    }

    private void setRailwayStationSpinner() {
        railwayStationSpnAL.add(0, getString(R.string.select_railway_station));
        railwayStationSpnAL.add(1, "Railway station 1");
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, railwayStationSpnAL);
        spn_railway_station.setAdapter(arrayAdapter);
        spn_railway_station.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    private void setValues() {
        tv_original_address.setText(original_address);
        tv_next_address.setText(next_address);
        tv_previous_address.setText(previous_address);
//        surveyModel.setReason(et_reasonPreviousAd.getText().toString());
//        surveyModel.setReason(et_reasonNextAd.getText().toString());
//        surveyModel.setReason(et_reasonOrgAd.getText().toString());



    }

    private void setButtonClick() {
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentClusterDetails = new Intent(AddressSelection.this, ClusterDetails.class);
                /*intentClusterDetails.putExtra("cluster_id", cluster_id);
                intentClusterDetails.putExtra("cluster_name", cluster_name);*/
                intentClusterDetails.putExtra("screen_type", "survey");
                sharedPrefHelper.setString("address_type", "1");
                startActivity(intentClusterDetails);
            }
        });
        btn_start_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentClusterDetails = new Intent(AddressSelection.this, ClusterDetails.class);
                /*intentClusterDetails.putExtra("cluster_id", cluster_id);
                intentClusterDetails.putExtra("cluster_name", cluster_name);*/
                intentClusterDetails.putExtra("screen_type", "survey");
                sharedPrefHelper.setString("address_type", "2");
                startActivity(intentClusterDetails);
            }
        });
        btn_start_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentClusterDetails = new Intent(AddressSelection.this, ClusterDetails.class);
                /*intentClusterDetails.putExtra("cluster_id", cluster_id);
                intentClusterDetails.putExtra("cluster_name", cluster_name);*/
                intentClusterDetails.putExtra("screen_type", "survey");
                sharedPrefHelper.setString("address_type", "3");
                startActivity(intentClusterDetails);
            }
        });
    }

    private void initialization() {
        sharedPrefHelper = new SharedPrefHelper(this);
        railwayStationSpnAL = new ArrayList<>();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) finish();
        if (item.getItemId() == R.id.stop_survey) {
            showPopupForTerminateSurvey();
        }
        if (item.getItemId() == R.id.home_icon) {
            Intent intentMainMenu = new Intent(context, MainMenu.class);
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

    public void setTerminattion() {
        Intent intentTerminate = new Intent(context, TerminateActivity.class);
        intentTerminate.putExtra("screen_type", "terminate");
        startActivity(intentTerminate);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        /*hide and show toolbar items*/
        if (screen_type.equalsIgnoreCase("survey")) {
            MenuItem item_stop_survey = menu.findItem(R.id.stop_survey);
            item_stop_survey.setVisible(true);
            MenuItem item_logout = menu.findItem(R.id.logout);
            item_logout.setVisible(false);
        }

        return true;
    }
}