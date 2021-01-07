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
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
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
    @BindView(R.id.btn_start_sub)
    MaterialButton btn_start_sub;
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
    TextInputEditText et_reasonNextAd;
    @BindView(R.id.et_reasonPreviousAd)
    TextInputEditText et_reasonPreviousAd;
    @BindView(R.id.et_reasonSubstituteAd)
    TextInputEditText et_reasonSubstituteAd;
    @BindView(R.id.til_reason_next_address)
    TextInputLayout til_reason_next_address;
    @BindView(R.id.til_reason_previous_address)
    TextInputLayout til_reason_previous_address;
    @BindView(R.id.til_reason_substitute_address)
    TextInputLayout til_reason_substitute_address;
    @BindView(R.id.tv_substitute_address)
    MaterialTextView tv_substitute_address;

    /*normal widgets*/
    private Context context = this;
    private String original_address = "", cluster_id = "", cluster_name = "", screen_type = "",
            previous_address = "", next_address = "", address_type="",spinnerValues="";
    //private ArrayList<String> railwayStationSpnAL;
    private SharedPrefHelper sharedPrefHelper;
    SurveyModel surveyModel;
    boolean isEditable=false;
    String [] railwayStationSpnAL={"Select Railway Station","Railway Station","Post Office","Bus Stand","EP Address"};

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
                        et_reasonNextAd.setText(null);
                        et_reasonPreviousAd.setText(null);
                        et_reasonSubstituteAd.setText(null);
                        break;
                    case R.id.rb_next_address:
                        address_type="Next";
                        rl_layout_substituted.setVisibility(View.GONE);
                        rl_layout.setVisibility(View.GONE);
                        rl_layout_next.setVisibility(View.VISIBLE);
                        rl_layout_previous.setVisibility(View.GONE);
                        et_reasonPreviousAd.setText(null);
                        et_reasonSubstituteAd.setText(null);
                        break;
                    case R.id.rb_previous_address:
                        address_type="Previous";
                        rl_layout_substituted.setVisibility(View.GONE);
                        rl_layout.setVisibility(View.GONE);
                        rl_layout_next.setVisibility(View.GONE);
                        rl_layout_previous.setVisibility(View.VISIBLE);
                        et_reasonNextAd.setText(null);
                        et_reasonSubstituteAd.setText(null);
                        break;
                    case R.id.rb_substituted_address:
                        address_type="Substituted";
                        rl_layout_substituted.setVisibility(View.VISIBLE);
                        rl_layout.setVisibility(View.GONE);
                        rl_layout_next.setVisibility(View.GONE);
                        rl_layout_previous.setVisibility(View.GONE);
                        et_reasonNextAd.setText(null);
                        et_reasonPreviousAd.setText(null);
                        isEditable=true;
                        setRailwayStationSpinner();
                        break;
                }
            }
        });
    }

    private void setRailwayStationSpinner() {
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, railwayStationSpnAL);
        spn_railway_station.setAdapter(arrayAdapter);

        /*if (isEditable){
            String addressType = "EP Address";
            int spinnerPosition = 0;
            String strpos1 = addressType;
            if (strpos1 != null || !strpos1.equals(null) || !strpos1.equals("")) {
                strpos1 = addressType;
                spinnerPosition = arrayAdapter.getPosition(strpos1);
                spn_railway_station.setSelection(spinnerPosition);
                spinnerPosition = 0;
            }
        }*/
        spn_railway_station.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (!spn_railway_station.getSelectedItem().toString().trim().equals("Select Railway Station")){
                    spinnerValues=spn_railway_station.getSelectedItem().toString().trim();
                    //Toast.makeText(context, ""+spinnerValues, Toast.LENGTH_SHORT).show();
                    if (spinnerValues.equals("EP Address")){
                        tv_substitute_address.setVisibility(View.VISIBLE);
                        tv_substitute_address.setText(spinnerValues);
                    }else{
                        tv_substitute_address.setVisibility(View.GONE);
                        tv_substitute_address.setText(null);
                    }
                }
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
                sharedPrefHelper.setString("reason_of_change", "");
                startActivity(intentClusterDetails);
            }
        });
        btn_start_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (et_reasonNextAd.getText().toString().trim().equals("")){
                    til_reason_next_address.setError(getString(R.string.enter_reason_of_selecting_next_address));
                    return;
                } else {
                    til_reason_next_address.setError(null);
                }
                Intent intentClusterDetails = new Intent(AddressSelection.this, ClusterDetails.class);
                /*intentClusterDetails.putExtra("cluster_id", cluster_id);
                intentClusterDetails.putExtra("cluster_name", cluster_name);*/
                intentClusterDetails.putExtra("screen_type", "survey");
                sharedPrefHelper.setString("address_type", "2");
                sharedPrefHelper.setString("reason_of_change", et_reasonNextAd.getText().toString().trim());
                startActivity(intentClusterDetails);
            }
        });
        btn_start_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (et_reasonPreviousAd.getText().toString().trim().equals("")){
                    til_reason_previous_address.setError(getString(R.string.enter_reason_of_selecting_previous_address));
                    return;
                } else {
                    til_reason_previous_address.setError(null);
                }
                Intent intentClusterDetails = new Intent(AddressSelection.this, ClusterDetails.class);
                /*intentClusterDetails.putExtra("cluster_id", cluster_id);
                intentClusterDetails.putExtra("cluster_name", cluster_name);*/
                intentClusterDetails.putExtra("screen_type", "survey");
                sharedPrefHelper.setString("address_type", "3");
                sharedPrefHelper.setString("reason_of_change", et_reasonPreviousAd.getText().toString().trim());
                startActivity(intentClusterDetails);
            }
        });
        btn_start_sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (spn_railway_station.getSelectedItem().toString().equalsIgnoreCase(getString(R.string.select_railway_station))){
                    Toast.makeText(context, "Please select railway station", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (et_reasonSubstituteAd.getText().toString().trim().equals("")){
                    til_reason_substitute_address.setError(getString(R.string.enter_reason_of_selecting_substitute_address));
                    return;
                } else {
                    til_reason_substitute_address.setError(null);
                }
                Intent intentClusterDetails = new Intent(AddressSelection.this, ClusterDetails.class);
                /*intentClusterDetails.putExtra("cluster_id", cluster_id);
                intentClusterDetails.putExtra("cluster_name", cluster_name);*/
                intentClusterDetails.putExtra("screen_type", "survey");
                sharedPrefHelper.setString("address_type", "4");
                sharedPrefHelper.setString("reason_of_change", et_reasonSubstituteAd.getText().toString().trim());
                startActivity(intentClusterDetails);
            }
        });
    }

    private void initialization() {
        sharedPrefHelper = new SharedPrefHelper(this);
        //railwayStationSpnAL = new ArrayList<>();
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