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
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.media.midi.MidiOutputPort;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.vrp.barc_demo.R;
import com.vrp.barc_demo.location_gps.AppConstants;
import com.vrp.barc_demo.models.AnswerModel;
import com.vrp.barc_demo.rest_api.ApiClient;
import com.vrp.barc_demo.rest_api.BARC_API;
import com.vrp.barc_demo.sqlite_db.SqliteHelper;
import com.vrp.barc_demo.utils.AlertDialogClass;
import com.vrp.barc_demo.utils.CommonClass;
import com.vrp.barc_demo.utils.SharedPrefHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Query;

public class TerminateActivity extends AppCompatActivity {
    private static final String TAG = "Terminate>>";
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
    @BindView(R.id.et_name)
    TextInputEditText et_name;
    @BindView(R.id.et_address)
    TextInputEditText et_address;
    @BindView(R.id.rg_terminate)
    RadioGroup rg_terminate;
    @BindView(R.id.rb_door_lock)
    RadioButton rb_door_lock;
    @BindView(R.id.rb_not_available_at_home)
    RadioButton rb_not_available_at_home;
    @BindView(R.id.rb_refused)
    RadioButton rb_refused;
    @BindView(R.id.rb_in_eligible_nccs)
    RadioButton rb_in_eligible_nccs;
    @BindView(R.id.rb_refused_to_continue)
    RadioButton rb_refused_to_continue;
    @BindView(R.id.rb_in_eligible_age)
    RadioButton rb_in_eligible_age;
    @BindView(R.id.rb_intro_exit)
    RadioButton rb_intro_exit;
    @BindView(R.id.rb_age_terminate)
    RadioButton rb_age_terminate;
    @BindView(R.id.rb_terminate)
    RadioButton rb_terminate;
    @BindView(R.id.rb_call_back)
    RadioButton rb_call_back;
    @BindView(R.id.til_name)
    TextInputLayout til_name;
    @BindView(R.id.til_address)
    TextInputLayout til_address;

    /*normal widgets*/
    private Context context=this;
    private String screen_type="", radio_button_id="", reason="",survey_id,
            halt_radio_button_id="", AudioSavePathInDevice=null,radioButtonText="";
    private SqliteHelper sqliteHelper;
    private SharedPrefHelper sharedPrefHelper;
    public static ArrayList<AnswerModel> answerModelList;
    public static ArrayList<ArrayList<AnswerModel>> answerModelHouseholdMemberListTotal;
    public static ArrayList<ArrayList<AnswerModel>> answerModelTVListTotal;
    MultipartBody.Part part;
    private int groupID;

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
            radio_button_id=bundle.getString("radio_button_id", "");
            radioButtonText=bundle.getString("radioButtonText", "");
            AudioSavePathInDevice=bundle.getString("AudioSavePathInDevice", "");
            groupID=bundle.getInt("groupID", 0);
            answerModelList = (ArrayList<AnswerModel>) getIntent().getSerializableExtra("answerModelList");
            ArrayList<AnswerModel> arrayListFM=new ArrayList<>();
            arrayListFM= (ArrayList<AnswerModel>) getIntent().getSerializableExtra("answerFamilyMemberModelList");
            answerModelHouseholdMemberListTotal.add(arrayListFM);
            ArrayList<AnswerModel> arrayListTV=new ArrayList<>();
            arrayListFM= (ArrayList<AnswerModel>) getIntent().getSerializableExtra("answerTVModelList");
            answerModelTVListTotal.add(arrayListTV);
        }
        survey_id=sharedPrefHelper.getString("survey_id", "");
        //date-time
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        sharedPrefHelper.setString("dateTime", dateFormat.format(cal.getTime()));
        hideShowOptions();
        setValues();
        getRadioButtonValues();
        setButtonClick();
    }

    private void getRadioButtonValues() {
        rg_terminate.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rb_door_lock:
                        reason=getResources().getString(R.string.door_locked);
                        halt_radio_button_id="1";
                        til_name.setVisibility(View.GONE);
                        til_address.setVisibility(View.GONE);
                        rb_door_lock.setError(null);
                        break;
                    case R.id.rb_not_available_at_home:
                        reason=getResources().getString(R.string.hh_cwe_not_available_at_home);
                        halt_radio_button_id="2";
                        til_name.setVisibility(View.GONE);
                        til_address.setVisibility(View.GONE);
                        rb_door_lock.setError(null);
                        break;
                    case R.id.rb_refused:
                        reason=getResources().getString(R.string.refused);
                        halt_radio_button_id="3";
                        til_name.setVisibility(View.GONE);
                        til_address.setVisibility(View.GONE);
                        rb_door_lock.setError(null);
                        break;
                    case R.id.rb_in_eligible_nccs:
                        reason=getResources().getString(R.string.in_eligible_nccs);
                        halt_radio_button_id="4";
                        til_name.setVisibility(View.VISIBLE);
                        til_address.setVisibility(View.VISIBLE);
                        rb_door_lock.setError(null);
                        break;
                    case R.id.rb_refused_to_continue:
                        reason=getResources().getString(R.string.refused_to_continue_interview_after_some_time);
                        halt_radio_button_id="5";
                        til_name.setVisibility(View.GONE);
                        til_address.setVisibility(View.GONE);
                        rb_door_lock.setError(null);
                        break;
                    case R.id.rb_in_eligible_age:
                        reason=getResources().getString(R.string.in_eligible_age);
                        halt_radio_button_id="6";
                        til_name.setVisibility(View.GONE);
                        til_address.setVisibility(View.GONE);
                        rb_door_lock.setError(null);
                        break;
                    case R.id.rb_intro_exit:
                        reason=getResources().getString(R.string.intro_exit);
                        halt_radio_button_id="7";
                        til_name.setVisibility(View.GONE);
                        til_address.setVisibility(View.GONE);
                        rb_door_lock.setError(null);
                        break;
                    case R.id.rb_age_terminate:
                        reason=getResources().getString(R.string.age_terminate);
                        halt_radio_button_id="8";
                        til_name.setVisibility(View.GONE);
                        til_address.setVisibility(View.GONE);
                        rb_door_lock.setError(null);
                        break;
                    case R.id.rb_terminate:
                        reason=getResources().getString(R.string.terminate);
                        halt_radio_button_id="9";
                        til_name.setVisibility(View.GONE);
                        til_address.setVisibility(View.GONE);
                        rb_door_lock.setError(null);
                        break;
                    case R.id.rb_call_back:
                        reason=getResources().getString(R.string.call_back);
                        halt_radio_button_id="10";
                        til_name.setVisibility(View.GONE);
                        til_address.setVisibility(View.GONE);
                        rb_door_lock.setError(null);
                        break;
                }
            }
        });
    }

    private void hideShowOptions() {
        if (!radio_button_id.equals("")) {
            cl_terminate.setVisibility(View.GONE);
            //save data in to local DB.
            Gson gson = new Gson();
            String listString = gson.toJson(
                    answerModelList,
                    new TypeToken<ArrayList<AnswerModel>>() {}.getType());
            String listStringFamily = gson.toJson(
                    answerModelHouseholdMemberListTotal,
                    new TypeToken<ArrayList<AnswerModel>>() {}.getType());
            String listStringTV = gson.toJson(
                    answerModelTVListTotal,
                    new TypeToken<ArrayList<AnswerModel>>() {}.getType());
            try {
                try {
                    JSONArray json_array =  new JSONArray(listString);
                    JSONArray json_array_family =  new JSONArray(listStringFamily);
                    JSONArray json_array_TV =  new JSONArray(listStringTV);
                    JSONObject json_object = new JSONObject();

                    json_object.put("user_id", sharedPrefHelper.getString("user_id", ""));
                    json_object.put("survey_id", sharedPrefHelper.getString("survey_id", ""));
                    json_object.put("app_version", sharedPrefHelper.getString("version", "1.4"));
                    json_object.put("survey_status", "3");//for terminate
                    json_object.put("cluster_no", sharedPrefHelper.getString("cluster_no", ""));
                    json_object.put("reason_of_change", sharedPrefHelper.getString("reason_of_change", ""));
                    json_object.put("census_district_code", sharedPrefHelper.getString("census_district_code", ""));
                    if (AudioSavePathInDevice!=null) {
                        json_object.put("audio_recording", AudioSavePathInDevice);
                    }
                    json_object.put("GPS_latitude_start", sharedPrefHelper.getString("LAT", ""));
                    json_object.put("GPS_longitude_start", sharedPrefHelper.getString("LONG", ""));
                    if (!radio_button_id.equals("")) {
                        json_object.put("reason", radioButtonText);
                        json_object.put("description", "terminate");
                    }
                    if (groupID==25){
                        if (radio_button_id.equals("1")||radio_button_id.equals("2")||radio_button_id.equals("3")
                                ||radio_button_id.equals("4")||radio_button_id.equals("5")||radio_button_id.equals("6")
                                ||radio_button_id.equals("7")) {
                            json_object.put("termination_reason", "Industry Termination");
                        }
                    }else if(groupID==26||groupID==27){
                        if (radio_button_id.equals("2")) {
                            json_object.put("termination_reason", "Quota Termination");
                        }
                    }

                    json_object.put("survey_data", json_array);
                    json_object.put("GPS_latitude_mid", sharedPrefHelper.getString("LAT", ""));
                    json_object.put("GPS_longitude_mid", sharedPrefHelper.getString("LONG", ""));
                    json_object.put("family_data", json_array_family);
                    json_object.put("tv_data", json_array_TV);
                    json_object.put("date_time", sharedPrefHelper.getString("dateTime", ""));
                    json_object.put("GPS_latitude_end", sharedPrefHelper.getString("LAT", ""));
                    json_object.put("GPS_longitude_end", sharedPrefHelper.getString("LONG", ""));
                    Log.e(TAG, "onClick: " + json_object.toString());

                    //sqliteHelper.saveSurveyDataInTable(json_object, sharedPrefHelper.getString("survey_id", ""));
                    //update data in to local DB
                    sqliteHelper.updateSurveyDataInTable("survey", "survey_id", survey_id, json_object);
                    //call terminate API here
                    if (CommonClass.isInternetOn(context)) {
                        String data = json_object.toString();
                        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                        RequestBody body = RequestBody.create(JSON, data);
                        //send data on server
                        sendSurveyDataOnServer(body);
                    } else {
                        cl_terminate.setVisibility(View.GONE);
                        tv_survey_terminate.setVisibility(View.VISIBLE);
                        btn_start_new_survey.setVisibility(View.VISIBLE);
                        sqliteHelper.updateLocalFlag("terminate", "survey",
                                sharedPrefHelper.getString("survey_id", ""), 1);
                        Toast.makeText(context, getResources().getString(R.string.no_internet_data_saved_locally), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void sendSurveyDataOnServer(RequestBody body) {
        AlertDialogClass.showProgressDialog(context);
        ApiClient.getClient().create(BARC_API.class).sendSurveyData(body).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().toString());
                    Log.e(TAG, "survey_data-: "+jsonObject.toString());
                    String success=jsonObject.getString("success");
                    String message=jsonObject.getString("message");
                    int survey_data_monitoring_id=jsonObject.getInt("survey_data_monitoring_id");
                    if (success.equals("1")) {
                        AlertDialogClass.dismissProgressDialog();
                        cl_terminate.setVisibility(View.GONE);
                        tv_survey_terminate.setVisibility(View.VISIBLE);
                        btn_start_new_survey.setVisibility(View.VISIBLE);
                        //update id on the bases of survey id
                        sqliteHelper.updateServerId("survey",
                                sharedPrefHelper.getString("survey_id", ""), survey_data_monitoring_id);
                        sqliteHelper.updateLocalFlag("terminate", "survey",
                                sharedPrefHelper.getString("survey_id", ""), 1);

                        //send audio here
                        Uri imageUri = Uri.parse(AudioSavePathInDevice);
                        File file = new File(imageUri.getPath());
                        RequestBody fileReqBody = RequestBody.create(MediaType.parse("Image/*"), file);
                        part= MultipartBody.Part.createFormData("audio_name", file.getName(), fileReqBody);
                        Log.e("audio_params-", "audio_params- "
                                +"\n"+sharedPrefHelper.getString("user_id", "")
                                +"\n"+survey_id+"\n"+survey_data_monitoring_id+"\n"+part);

                        ApiClient.getClient().create(BARC_API.class).sendAudio(sharedPrefHelper.getString("user_id", ""), survey_id, survey_data_monitoring_id, part).enqueue(new Callback<JsonObject>() {
                            @Override
                            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response.body().toString());
                                    Log.e("audio-upload",jsonObject.toString());
                                    String success=jsonObject.optString("success");
                                    String message=jsonObject.optString("message");
                                    String name=jsonObject.optString("name");
                                    String file_status=jsonObject.optString("file_status");
                                    if (success.equalsIgnoreCase("1")) {
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(Call<JsonObject> call, Throwable t) {
                            }
                        });
                    } else {
                        AlertDialogClass.dismissProgressDialog();
                        CommonClass.showPopupForNoInternet(context);
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

    private void setValues() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        et_date_time.setText(dateFormat.format(cal.getTime()));
    }

    private void setButtonClick() {
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (halt_radio_button_id.equals("10")) {
                    if (checkValidationHalt()) {
                        //save data in to local DB.
                        Gson gson = new Gson();
                        String listString = gson.toJson(
                                answerModelList,
                                new TypeToken<ArrayList<AnswerModel>>() {
                                }.getType());
                        String listStringFamily = gson.toJson(
                                answerModelHouseholdMemberListTotal,
                                new TypeToken<ArrayList<AnswerModel>>() {
                                }.getType());
                        String listStringTV = gson.toJson(
                                answerModelTVListTotal,
                                new TypeToken<ArrayList<AnswerModel>>() {
                                }.getType());
                        try {
                            try {
                                JSONArray json_array = new JSONArray(listString);
                                JSONArray json_array_family = new JSONArray(listStringFamily);
                                JSONArray json_array_TV = new JSONArray(listStringTV);
                                JSONObject json_object = new JSONObject();

                                json_object.put("user_id", sharedPrefHelper.getString("user_id", ""));
                                json_object.put("survey_id", sharedPrefHelper.getString("survey_id", ""));
                                json_object.put("app_version", sharedPrefHelper.getString("version", "1.3"));
                                json_object.put("survey_status", "2");// for halt
                                json_object.put("cluster_no", sharedPrefHelper.getString("cluster_no", ""));
                                json_object.put("reason_of_change", sharedPrefHelper.getString("reason_of_change", ""));
                                json_object.put("census_district_code", sharedPrefHelper.getString("census_district_code", ""));
                                if (AudioSavePathInDevice != null) {
                                    json_object.put("audio_recording", AudioSavePathInDevice);
                                }
                                json_object.put("GPS_latitude_start", sharedPrefHelper.getString("LAT", ""));
                                json_object.put("GPS_longitude_start", sharedPrefHelper.getString("LONG", ""));
                                json_object.put("reason", reason);
                                json_object.put("description", reason);
                                json_object.put("household_name", et_name.getText().toString().trim());
                                json_object.put("address", et_address.getText().toString().trim());
                                json_object.put("survey_data", json_array);
                                json_object.put("GPS_latitude_mid", sharedPrefHelper.getString("LAT", ""));
                                json_object.put("GPS_longitude_mid", sharedPrefHelper.getString("LONG", ""));
                                json_object.put("family_data", json_array_family);
                                json_object.put("tv_data", json_array_TV);
                                json_object.put("date_time", et_date_time.getText().toString().trim());
                                json_object.put("GPS_latitude_end", sharedPrefHelper.getString("LAT", ""));
                                json_object.put("GPS_longitude_end", sharedPrefHelper.getString("LONG", ""));
                                Log.e(TAG, "onClick: " + json_object.toString());

                                //sqliteHelper.saveSurveyDataInTable(json_object, sharedPrefHelper.getString("survey_id", ""));
                                //update data in to local DB
                                sqliteHelper.updateSurveyDataInTable("survey", "survey_id", survey_id, json_object);
                                //call terminate API here
                                if (CommonClass.isInternetOn(context)) {
                                    String data = json_object.toString();
                                    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                                    RequestBody body = RequestBody.create(JSON, data);
                                    //send data on server
                                    sendHaltSurveyDataOnServer(body);
                                } else {
                                    cl_terminate.setVisibility(View.GONE);
                                    tv_survey_terminate.setText(getString(R.string.halt_survey));
                                    tv_survey_terminate.setVisibility(View.VISIBLE);
                                    btn_start_new_survey.setVisibility(View.VISIBLE);
                                    sqliteHelper.updateLocalFlag("halt", "survey",
                                            sharedPrefHelper.getString("survey_id", ""), 1);
                                    Toast.makeText(context, getResources().getString(R.string.no_internet_data_saved_locally), Toast.LENGTH_SHORT).show();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                if(halt_radio_button_id.equals("1")||halt_radio_button_id.equals("2")||halt_radio_button_id.equals("3")
                   ||halt_radio_button_id.equals("4")||halt_radio_button_id.equals("5")||halt_radio_button_id.equals("6")
                   ||halt_radio_button_id.equals("7")||halt_radio_button_id.equals("8")||halt_radio_button_id.equals("9")){
                    if (checkValidation()){
                    cl_terminate.setVisibility(View.GONE);
                    //save data in to local DB.
                    Gson gson = new Gson();
                    String listString = gson.toJson(
                            answerModelList,
                            new TypeToken<ArrayList<AnswerModel>>() {}.getType());
                    String listStringFamily = gson.toJson(
                            answerModelHouseholdMemberListTotal,
                            new TypeToken<ArrayList<AnswerModel>>() {}.getType());
                    String listStringTV = gson.toJson(
                            answerModelTVListTotal,
                            new TypeToken<ArrayList<AnswerModel>>() {}.getType());
                    try {
                        try {
                            JSONArray json_array =  new JSONArray(listString);
                            JSONArray json_array_family =  new JSONArray(listStringFamily);
                            JSONArray json_array_TV =  new JSONArray(listStringTV);
                            JSONObject json_object = new JSONObject();

                            json_object.put("user_id", sharedPrefHelper.getString("user_id", ""));
                            json_object.put("survey_id", sharedPrefHelper.getString("survey_id", ""));
                            json_object.put("app_version", sharedPrefHelper.getString("version", "1.3"));
                            json_object.put("survey_status", "3");//for terminate
                            json_object.put("cluster_no", sharedPrefHelper.getString("cluster_no", ""));
                            json_object.put("reason_of_change", sharedPrefHelper.getString("reason_of_change", ""));
                            json_object.put("census_district_code", sharedPrefHelper.getString("census_district_code", ""));
                            if (AudioSavePathInDevice!=null) {
                                json_object.put("audio_recording", AudioSavePathInDevice);
                            }
                            json_object.put("GPS_latitude_start", sharedPrefHelper.getString("LAT", ""));
                            json_object.put("GPS_longitude_start", sharedPrefHelper.getString("LONG", ""));
                            if (!radio_button_id.equals("")) {
                                json_object.put("reason", radioButtonText);
                                json_object.put("description", "terminate");
                            }else{
                                json_object.put("reason", reason);
                                json_object.put("description", reason);
                            }
                            if (halt_radio_button_id.equals("4")) {
                                json_object.put("termination_reason", "Quota Termination");
                            }else if(halt_radio_button_id.equals("1")||halt_radio_button_id.equals("2")
                                    ||halt_radio_button_id.equals("3")||halt_radio_button_id.equals("5")
                                    ||halt_radio_button_id.equals("6")||halt_radio_button_id.equals("7")
                                    ||halt_radio_button_id.equals("8")){
                                json_object.put("termination_reason", "Contact Termination");
                            }
                            json_object.put("survey_data", json_array);
                            json_object.put("GPS_latitude_mid", sharedPrefHelper.getString("LAT", ""));
                            json_object.put("GPS_longitude_mid", sharedPrefHelper.getString("LONG", ""));
                            json_object.put("family_data", json_array_family);
                            json_object.put("tv_data", json_array_TV);
                            json_object.put("date_time", et_date_time.getText().toString().trim());
                            json_object.put("GPS_latitude_end", sharedPrefHelper.getString("LAT", ""));
                            json_object.put("GPS_longitude_end", sharedPrefHelper.getString("LONG", ""));
                            Log.e(TAG, "onClick: " + json_object.toString());

                            //sqliteHelper.saveSurveyDataInTable(json_object, sharedPrefHelper.getString("survey_id", ""));
                            //update data in to local DB
                            sqliteHelper.updateSurveyDataInTable("survey", "survey_id", survey_id, json_object);
                            //call terminate API here
                            if (CommonClass.isInternetOn(context)) {
                                String data = json_object.toString();
                                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                                RequestBody body = RequestBody.create(JSON, data);
                                //send data on server
                                sendSurveyDataOnServer(body);
                            } else {
                                cl_terminate.setVisibility(View.GONE);
                                tv_survey_terminate.setVisibility(View.VISIBLE);
                                btn_start_new_survey.setVisibility(View.VISIBLE);
                                sqliteHelper.updateLocalFlag("terminate", "survey",
                                        sharedPrefHelper.getString("survey_id", ""), 1);
                                Toast.makeText(context, getResources().getString(R.string.no_internet_data_saved_locally), Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                }
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

    private boolean checkValidationHalt() {
        if (rg_terminate.getCheckedRadioButtonId() == -1) {
            Toast.makeText(context, getString(R.string.reason_of_halt), Toast.LENGTH_LONG).show();
            rb_door_lock.setError("Please choose one!");
            rb_door_lock.requestFocus();
            rb_door_lock.setFocusable(true);
            return false;
        }
        /*if (et_name.getText().toString().trim().length()==0) {
            til_name.setError("Please enter name!");
            return false;
        } else {
            til_name.setError(null);
        }
        if (et_address.getText().toString().trim().length()==0) {
            til_address.setError("Please enter address!");
            return false;
        } else {
            til_address.setError(null);
        }*/
        return true;
    }
    private boolean checkValidation() {
        if (rg_terminate.getCheckedRadioButtonId() == -1) {
            Toast.makeText(context, getString(R.string.reason_of_halt), Toast.LENGTH_LONG).show();
            rb_door_lock.setError("Please choose one!");
            rb_door_lock.requestFocus();
            rb_door_lock.setFocusable(true);
            return false;
        }
        else if (halt_radio_button_id.equals("10")){
            if (et_name.getText().toString().trim().length()==0) {
                til_name.setError("Please enter name!");
                return false;
            } else {
                til_name.setError(null);
            }
            if (et_address.getText().toString().trim().length()==0) {
                til_address.setError("Please enter address!");
                return false;
            } else {
                til_address.setError(null);
            }
        }
        return true;
    }

    private void sendHaltSurveyDataOnServer(RequestBody body) {
        AlertDialogClass.showProgressDialog(context);
        ApiClient.getClient().create(BARC_API.class).sendSurveyData(body).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().toString());
                    Log.e(TAG, "survey_data-: "+jsonObject.toString());
                    String success=jsonObject.getString("success");
                    String message=jsonObject.getString("message");
                    int survey_data_monitoring_id=jsonObject.getInt("survey_data_monitoring_id");
                    if (success.equals("1")) {
                        AlertDialogClass.dismissProgressDialog();
                        cl_terminate.setVisibility(View.GONE);
                        tv_survey_terminate.setText(getString(R.string.halt_survey));
                        tv_survey_terminate.setVisibility(View.VISIBLE);
                        btn_start_new_survey.setVisibility(View.VISIBLE);
                        //update id on the bases of survey id
                        sqliteHelper.updateServerId("survey",
                                sharedPrefHelper.getString("survey_id", ""), survey_data_monitoring_id);
                        sqliteHelper.updateLocalFlag("halt", "survey",
                                sharedPrefHelper.getString("survey_id", ""), 1);

                        //send audio here
                        Uri imageUri = Uri.parse(AudioSavePathInDevice);
                        File file = new File(imageUri.getPath());
                        RequestBody fileReqBody = RequestBody.create(MediaType.parse("Image/*"), file);
                        part= MultipartBody.Part.createFormData("audio_name", file.getName(), fileReqBody);
                        Log.e("audio_params-", "audio_params- "
                                +"\n"+sharedPrefHelper.getString("user_id", "")
                                +"\n"+survey_id+"\n"+survey_data_monitoring_id+"\n"+part);

                        ApiClient.getClient().create(BARC_API.class).sendAudio(sharedPrefHelper.getString("user_id", ""), survey_id, survey_data_monitoring_id, part).enqueue(new Callback<JsonObject>() {
                            @Override
                            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response.body().toString());
                                    Log.e("audio-upload",jsonObject.toString());
                                    String success=jsonObject.optString("success");
                                    String message=jsonObject.optString("message");
                                    String name=jsonObject.optString("name");
                                    String file_status=jsonObject.optString("file_status");
                                    if (success.equalsIgnoreCase("1")) {
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(Call<JsonObject> call, Throwable t) {
                            }
                        });
                    } else {
                        AlertDialogClass.dismissProgressDialog();
                        CommonClass.showPopupForNoInternet(context);
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

    private void initialization() {
        sqliteHelper=new SqliteHelper(this);
        sharedPrefHelper=new SharedPrefHelper(this);
        answerModelList=new ArrayList<>();
        answerModelHouseholdMemberListTotal=new ArrayList<>();
        answerModelTVListTotal=new ArrayList<>();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) finish();
        if (item.getItemId()==R.id.home_icon) {
            Intent intentMainMenu=new Intent(context, MainMenu.class);
            intentMainMenu.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intentMainMenu);
        }
        return super.onOptionsItemSelected(item);
    }

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
    @Override
    public void onResume() {
        super.onResume();
    }
}
