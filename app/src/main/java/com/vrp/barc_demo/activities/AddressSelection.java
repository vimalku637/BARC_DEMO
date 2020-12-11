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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.vrp.barc_demo.ClusterDetails;
import com.vrp.barc_demo.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

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

    /*normal widgets*/
    private Context context=this;
    private String original_address="";
    private String cluster_id="";
    private String cluster_name="";
    private ArrayList<String> railwayStationSpnAL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_selection);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        ButterKnife.bind(this);
        setTitle(R.string.view_starting_points);
        initialization();
        /*get intent values here*/
        Bundle bundle=getIntent().getExtras();
        if (bundle!=null) {
            original_address=bundle.getString("original_address", "");
            cluster_id=bundle.getString("cluster_id", "");
            cluster_name=bundle.getString("cluster_name", "");
        }

        setValues();
        setRailwayStationSpinner();
        setRadioButtonClick();
        setButtonClick();
    }

    private void setRadioButtonClick() {
        rg_address.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rb_original_address:
                        rl_layout_substituted.setVisibility(View.GONE);
                        rl_layout.setVisibility(View.VISIBLE);
                        break;
                    case R.id.rb_next_address:
                        rl_layout_substituted.setVisibility(View.GONE);
                        rl_layout.setVisibility(View.GONE);
                        break;
                    case R.id.rb_previous_address:
                        rl_layout_substituted.setVisibility(View.GONE);
                        rl_layout.setVisibility(View.GONE);
                        break;
                    case R.id.rb_substituted_address:
                        rl_layout_substituted.setVisibility(View.VISIBLE);
                        rl_layout.setVisibility(View.GONE);
                        break;
                }
            }
        });
    }

    private void setRailwayStationSpinner() {
        railwayStationSpnAL.add(0, getString(R.string.select_railway_station));
        railwayStationSpnAL.add(1, "Railway station 1");
        ArrayAdapter arrayAdapter=new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, railwayStationSpnAL);
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
    }

    private void setButtonClick() {
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentClusterDetails=new Intent(AddressSelection.this, ClusterDetails.class);
                intentClusterDetails.putExtra("cluster_id", cluster_id);
                intentClusterDetails.putExtra("cluster_name", cluster_name);
                startActivity(intentClusterDetails);
            }
        });
    }

    private void initialization() {
        railwayStationSpnAL=new ArrayList<>();
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