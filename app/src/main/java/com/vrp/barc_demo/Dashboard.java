package com.vrp.barc_demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.vrp.barc_demo.activities.ClusterListActivity;
import com.vrp.barc_demo.activities.MainMenu;
import com.vrp.barc_demo.login.LoginActivity;
import com.vrp.barc_demo.sqlite_db.SqliteHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Dashboard extends AppCompatActivity {
    @BindView(R.id.tv_search)
    TextView tv_search;
    @BindView(R.id.tv_Totalcluster)
    TextView tv_Totalcluster;
    @BindView(R.id.et_clucode)
    AutoCompleteTextView et_clucode;
    @BindView(R.id.tv_totalsurvey)
    TextView tv_totalsurvey;
    @BindView(R.id.spn_city)
    Spinner spn_city;
    SqliteHelper sqliteHelper;
    @BindView(R.id.pieChart)
    PieChart pieChart;
    @BindView(R.id.ll_graph)
    LinearLayout ll_graph;

    HashMap<String, Integer> CityNameHM;
    ArrayList<String> CityArrayList;
    boolean isEditable = false;
    String city_name;
    String clid;
    int CityName;
    Context context = this;
    int strTotalSurvey;
    int countProgress;
    int countReject;
    int countComplete;
    int countTerminate;
    int TotalclusterLock;
    ArrayList<String> clu_id = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        ButterKnife.bind(this);
        sqliteHelper = new SqliteHelper(this);
        setTitle(R.string.dashboard);
        initialization();


        CityNameHM = new HashMap<>();
        CityArrayList = new ArrayList<>();
        setCitySpinner();

        pieChart = findViewById(R.id.pieChart);

        setPieChart();
        /*get intent values here*/
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
        }

        setButtonClick();



        strTotalSurvey = sqliteHelper.getTotalsurvey();
        tv_totalsurvey.setText(""+strTotalSurvey);

        TotalclusterLock = sqliteHelper.getTotallockrd();
        tv_Totalcluster.setText(""+TotalclusterLock);

        //show graph here
        if (strTotalSurvey>0){
            ll_graph.setVisibility(View.VISIBLE);
        }

    }

    private void setPieChart() {

        countComplete = sqliteHelper.getChartValue(1);
        countReject = sqliteHelper.getChartValue(5);
        countProgress = sqliteHelper.getTotalchartInprogress();
        countTerminate = sqliteHelper.getTotalchart4(3);

        ArrayList Entryes = new ArrayList();
        Entryes.add(new PieEntry(countComplete, ""));
        Entryes.add(new PieEntry(countReject, ""));
        Entryes.add(new PieEntry(countProgress, ""));
        Entryes.add(new PieEntry(countTerminate, ""));
        PieDataSet set = new PieDataSet(Entryes, "");

        PieData data = new PieData(set);
        data.setValueTextSize(10);
        pieChart.setData(data);
        set.setColors(new int[]{R.color.color_green_google, R.color.color_red_google, R.color.color_yellow_google ,R.color.color_white}, Dashboard.this);
        pieChart.getDescription().setEnabled(false);
        pieChart.animateXY(800, 800);


//        this.pieChart = pieChart;
//        pieChart.setUsePercentValues(true);
//        pieChart.getDescription().setEnabled(false);
//        pieChart.setExtraOffsets(5, 10, 5, 5);
//        pieChart.setDragDecelerationFrictionCoef(0.9f);
//        pieChart.setTransparentCircleRadius(61f);
//        // pieChart.setHoleColor(Color.WHITE);
//        pieChart.animateY(1000, Easing.EasingOption.EaseInOutCubic);
//        ArrayList<PieEntry> yValues = new ArrayList<>();
//        Entryes.add(new PieEntry(2));
//        Entryes.add(new PieEntry(1));
//        Entryes.add(new PieEntry(3));
//        Entryes.add(new PieEntry(4));
//
//        PieDataSet dataSet = new PieDataSet(yValues, "");
//        dataSet.setSliceSpace(3f);
//        dataSet.setSelectionShift(10f);
//        // dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
//        PieData pieData = new PieData((dataSet));
//        pieData.setValueTextSize(15f);
//        pieData.setValueTextColor(Color.YELLOW);
//        pieChart.setData(pieData);
//        dataSet.setColors(new int[]{R.color.color_green_google, R.color.color_red_google, R.color.color_yellow_google}, Dashboard.this);

        //PieChart Ends Here
        clu_id = sqliteHelper.getClusterID();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, android.R.layout.select_dialog_item, clu_id);
        et_clucode.setThreshold(1); //will start working from first character
        et_clucode.setAdapter(adapter);
        clid = et_clucode.getText().toString();

    }

    private void setCitySpinner() {
        CityArrayList.clear();
        CityNameHM = sqliteHelper.getCity();

        for (int i = 0; i < CityNameHM.size(); i++) {
            CityArrayList.add(CityNameHM.keySet().toArray()[i].toString().trim());
        }
        Collections.sort(CityArrayList);
        if (isEditable) {
            //EducationArrayList.add(0, Id_Card);
        } else {
            CityArrayList.add(0, getString(R.string.select_city));
        }

        final ArrayAdapter vv = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, CityArrayList);
        vv.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_city.setAdapter(vv);
        if (isEditable) {
            int spinnerPosition = 0;
            String strpos1 = city_name;
            if (strpos1 != null || !strpos1.equals(null) || !strpos1.equals("")) {
                strpos1 = city_name;
                spinnerPosition = vv.getPosition(strpos1);
                spn_city.setSelection(spinnerPosition);
                spinnerPosition = 0;
            }
        }
        spn_city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (!spn_city.getSelectedItem().toString().trim().equalsIgnoreCase(getString(R.string.select_city))) {
                    if (spn_city.getSelectedItem().toString().trim() != null) {
                        CityName = CityNameHM.get(spn_city.getSelectedItem().toString().trim());
                    }
                }else{
                    CityName =0;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void setButtonClick() {
        tv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Dashboard.this, ClusterListActivity.class);
                intent.putExtra("City",CityName);
                intent.putExtra("clucode",et_clucode.getText().toString());
                startActivity(intent);
            }
        });
    }

    private void initialization() {
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) finish();
        if (item.getItemId() == R.id.home_icon) {
            Intent intentMainMenu = new Intent(context, MainMenu.class);
            intentMainMenu.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intentMainMenu);
        }
        if (item.getItemId() == R.id.logout) {
            Intent i = new Intent(Dashboard.this, LoginActivity.class);
// set the new task and clear flags
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }


}