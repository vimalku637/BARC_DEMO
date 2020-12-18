/*
 * Copyright (c)  2020. Indev Consultancy Private Limited,
 * Auther : Vimal Kumar
 * Date : 2020/12/15
 * Modified Date :
 * Modified By :
 */

package com.vrp.barc_demo.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.vrp.barc_demo.R;
import com.vrp.barc_demo.fragments.GroupRelationFragment;
import com.vrp.barc_demo.fragments.GroupTVFragment;
import com.vrp.barc_demo.models.AnswerModel;
import com.vrp.barc_demo.models.ScreenWiseQuestionModel;
import com.vrp.barc_demo.rest_api.ApiClient;
import com.vrp.barc_demo.rest_api.BARC_API;
import com.vrp.barc_demo.sqlite_db.SqliteHelper;
import com.vrp.barc_demo.utils.ActivityCommunicator;
import com.vrp.barc_demo.utils.AlertDialogClass;
import com.vrp.barc_demo.utils.CommonClass;
import com.vrp.barc_demo.utils.FragmentCommunicator;
import com.vrp.barc_demo.utils.MyJSON;
import com.vrp.barc_demo.utils.SharedPrefHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HouseholdSurveyActivity extends AppCompatActivity implements ActivityCommunicator {
    private static final String TAG = "HouseholdSurvey>>";
    @BindView(R.id.btn_previous)
    MaterialButton btn_previous;
    @BindView(R.id.btn_stop)
    MaterialButton btn_stop;
    @BindView(R.id.btn_next)
    MaterialButton btn_next;
    @BindView(R.id.btn_save)
    MaterialButton btn_save;
    @BindView(R.id.ll_parent)
    LinearLayout ll_parent;

    /*normal widgets*/
    private Context context=this;
    private String survey_id="";
    private String screen_type="";
    private int length=1;
    private int startPosition;
    private int startScreenPosition=0;
    private int startPositionBefore;
    private int endPosition;
    private int endScreenPosition=1;
    SharedPrefHelper sharedPrefHelper;
    int totalQuestions;
    int totalScreen;
    JSONObject jsonQuestions = null;
    JSONArray jsonArrayQuestions=null;
    JSONArray jsonArrayScreen=null;
    public static ArrayList<AnswerModel> answerModelList;
    public static ArrayList<AnswerModel> answerModelHouseholdMemberList;
    public static ArrayList<AnswerModel> answerModelTVList;
    public FragmentCommunicator fragmentCommunicator;
    ArrayList<ScreenWiseQuestionModel> arrayScreenWiseQuestionModel= new ArrayList<>();
    String screen_id=null;
    boolean back_status=true;
    private SqliteHelper sqliteHelper;
    private String surveyObjectJSON=null;
    private String editFieldValues="";
    private int groupQuestionID=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        ButterKnife.bind(this);
        setTitle(R.string.survey);
        initialization();
        Log.e(TAG, "LAT_LONG>>> "+sharedPrefHelper.getString("LAT", "")+"\n"+
                sharedPrefHelper.getString("LONG", ""));
        endPosition=sharedPrefHelper.getInt("endPosition",0);
        if(endPosition==0){
            endPosition=length;
        }
        startPosition=sharedPrefHelper.getInt("startPosition",0);

        /*get intent values here*/
        Bundle bundle=getIntent().getExtras();
        if (bundle!=null) {
            survey_id=bundle.getString("survey_id", "");
            screen_type=bundle.getString("screen_type", "");
            if (screen_type.equals("survey_list")) {
                answerModelList = (ArrayList<AnswerModel>) getIntent().getSerializableExtra("answerModelList");
            }
        }
        /*get survey data according to survey id*/
        /*if (screen_type.equals("survey_list")) {
            surveyObjectJSON = sqliteHelper.getSurveyData(survey_id);
            try {
                jsonQuestions = new JSONObject(surveyObjectJSON);
                if (jsonQuestions.has("survey_data")) {
                    jsonArrayQuestions=jsonQuestions.getJSONArray("survey_data");
                    totalQuestions=jsonArrayQuestions.length();
                    Log.e("survey_data", "onCreate: " + jsonArrayQuestions.toString());
                    if(totalQuestions>0){
                        questionsPopulate();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else {*/
            try {
                jsonQuestions = new JSONObject(MyJSON.loadJSONFromAsset(context));
                /*if (jsonQuestions.has("screen")) {
                    jsonArrayScreen = jsonQuestions.getJSONArray("screen");
                    totalScreen = jsonArrayScreen.length();
                    Log.e("Screen", "onCreate: " + jsonArrayScreen.toString());
                    if(totalScreen>0){
                        questionsPopulate();
                    }
                }*/
                if (jsonQuestions.has("group")) {
                    /*JSONArray jsonArrayGroup=jsonQuestions.getJSONArray("group");
                    JSONObject jsonObjectGroup=jsonArrayGroup.getJSONObject(0);*/
                    jsonArrayScreen = jsonQuestions.getJSONArray("group").getJSONObject(0).getJSONArray("screens");
                    totalScreen = jsonArrayScreen.length();
                    Log.e("Screen", "onCreate: " + jsonArrayScreen.toString());
                    if(totalScreen>0){
                        questionsPopulate();
                    }
                }

                /*if (jsonQuestions.has("questions")) {
                    jsonArrayQuestions = jsonQuestions.getJSONArray("questions");
                    totalQuestions = jsonArrayQuestions.length();
                    Log.e("questions", "onCreate: " + jsonArrayQuestions.toString());
                    if(totalQuestions>0){
                        questionsPopulate();
                    }
                }*/
            }catch (JSONException ex){
                Log.e("questions", "onCreate: " + ex.getMessage());
            }
        /*}*/

        setButtonClick();
        saveAllData();
    }

    private void saveAllData() {
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //save data in to local DB.
                Gson gson = new Gson();
                String listString = gson.toJson(
                        answerModelList,
                        new TypeToken<ArrayList<AnswerModel>>() {}.getType());

                try {
                    JSONArray jsonArray =  new JSONArray(listString);
                    JSONObject jsonObject=new JSONObject();
                    jsonObject.put("survey_data", jsonArray);
                    Log.e(TAG, "onClick: "+jsonObject.toString());

                    if (screen_type.equals("survey_list")) {
                        sqliteHelper.updateSurveyDataInTable("survey", "survey_id", survey_id, jsonObject);
                    } else {
                        sqliteHelper.saveSurveyDataInTable(jsonObject, survey_id);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void onAddEditField(JSONObject jsonObjectQuesType) {
        LayoutInflater inflater=(LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView=inflater.inflate(R.layout.edit_text_layout, null);
        /*here are fields*/
        TextView tv_question=rowView.findViewById(R.id.tv_question);
        EditText et_answer=rowView.findViewById(R.id.et_answer);
        try {
            tv_question.setText(jsonObjectQuesType.getString("question_name"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Add the new row before the add field button.
        ll_parent.addView(rowView, ll_parent.getChildCount());
    }
    public void onAddSpinner(JSONObject jsonObjectQuesType) {
         ll_parent=findViewById(R.id.ll_parent);
        ArrayList<String> spinnerAL=new ArrayList<>();

        LayoutInflater inflater=(LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView=inflater.inflate(R.layout.spinner_layout, null);
        /*here are fields*/
        TextView tv_question=rowView.findViewById(R.id.tv_question);
        Spinner spinner=rowView.findViewById(R.id.spinner);
        try {
            tv_question.setText(jsonObjectQuesType.getString("question_name"));
            if (jsonObjectQuesType.has("question_options")) {
                JSONArray jsonArrayOptions = jsonObjectQuesType.getJSONArray("question_options");
                spinnerAL.clear();
                for (int i = 0; i < jsonArrayOptions.length(); i++) {
                    JSONObject jsonObjectOptionValues=jsonArrayOptions.getJSONObject(i);
                    String spinnerOption=jsonObjectOptionValues.getString("option_value");
                    spinnerAL.add(spinnerOption);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        spinnerAL.add(0, getString(R.string.select_option));
        ArrayAdapter arrayAdapter=new ArrayAdapter(this, R.layout.custom_spinner_dropdown, spinnerAL);
        spinner.setAdapter(arrayAdapter);

        // Add the new row before the add field button.
        ll_parent.addView(rowView, ll_parent.getChildCount());
    }
    public void onAddRadioButton(JSONObject jsonObjectQuesType) {
         ll_parent=findViewById(R.id.ll_parent);

        LayoutInflater inflater=(LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView=inflater.inflate(R.layout.questions_layout, null);
        /*here are fields*/
        TextView tv_question=rowView.findViewById(R.id.tv_question);
        LinearLayout ll_child=rowView.findViewById(R.id.ll_child);
        try {
            tv_question.setText(jsonObjectQuesType.getString("question_name"));
            if (jsonObjectQuesType.has("question_options")) {
                JSONArray jsonArrayOptions = jsonObjectQuesType.getJSONArray("question_options");
                for (int i = 0; i < jsonArrayOptions.length(); i++) {
                    LayoutInflater inflaterRadio=(LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View radioButtonViews=inflaterRadio.inflate(R.layout.radio_button_layout, null);
                    /*here are fields*/
                    RadioGroup radio_group=radioButtonViews.findViewById(R.id.radio_group);
                    RadioButton radio_button=radioButtonViews.findViewById(R.id.radio_button);
                    JSONObject jsonObjectOptionValues=jsonArrayOptions.getJSONObject(i);
                    radio_button.setText(jsonObjectOptionValues.getString("option_value"));

                    ll_child.addView(radioButtonViews, ll_child.getChildCount());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Add the new row before the add field button.
        ll_parent.addView(rowView, ll_parent.getChildCount());
    }
    public void onAddCheckBox(JSONObject jsonObjectQuesType) {
         ll_parent=findViewById(R.id.ll_parent);
        LayoutInflater inflater=(LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView=inflater.inflate(R.layout.questions_layout, null);
        /*here are fields*/
        TextView tv_question=rowView.findViewById(R.id.tv_question);
        LinearLayout ll_child=rowView.findViewById(R.id.ll_child);
        try {
            tv_question.setText(jsonObjectQuesType.getString("question_name"));
            if (jsonObjectQuesType.has("question_options")) {
                JSONArray jsonArrayOptions = jsonObjectQuesType.getJSONArray("question_options");
                for (int i = 0; i < jsonArrayOptions.length(); i++) {
                    LayoutInflater inflaterCheck=(LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View checkBoxViews=inflaterCheck.inflate(R.layout.check_box_layout, null);
                    /*here are fields*/
                    CheckBox check_box=checkBoxViews.findViewById(R.id.check_box);
                    JSONObject jsonObjectOptionValues=jsonArrayOptions.getJSONObject(i);
                    check_box.setText(jsonObjectOptionValues.getString("option_value"));

                    ll_child.addView(checkBoxViews, ll_child.getChildCount());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Add the new row before the add field button.
        ll_parent.addView(rowView, ll_parent.getChildCount());
    }
    private void initialization() {
        sharedPrefHelper=new SharedPrefHelper(this);
        sqliteHelper=new SqliteHelper(this);
        answerModelList=new ArrayList<>();
        answerModelHouseholdMemberList=new ArrayList<>();
        answerModelTVList=new ArrayList<>();
    }

    private void setButtonClick() {
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean flag=true;
                Button b = (Button)view;
                String buttonText = b.getText().toString();
                    JSONArray jsonArray = new JSONArray();
                    final JSONObject jsonObject = new JSONObject();
                    int count=0;
                    int nextPosition=startPositionBefore;
                    for (int i = 0; i < ll_parent.getChildCount(); i++) {
                        final View childView = ll_parent.getChildAt(i);
                        try {
                            if (childView instanceof EditText) {
                                EditText editText = (EditText) childView;
                                int viewID=editText.getId();
                                editFieldValues=editText.getText().toString().trim();
                                groupQuestionID=Integer.parseInt(jsonArrayQuestions.getJSONObject(count).getString("question_id"));
                                if((back_status==true || screen_type.equals("survey_list")) && answerModelList.size()>nextPosition){
                                    answerModelList.get(nextPosition).setOption_value(editText.getText().toString().trim());
                                }
                                else{
                                    AnswerModel answerModel= new AnswerModel();
                                    answerModel.setOption_id("");
                                    answerModel.setOption_value(editText.getText().toString().trim());
                                    //answerModel.setSurveyID(survey_id);
                                    answerModel.setQuestionID(jsonArrayQuestions.getJSONObject(count).getString("question_id"));
                                    answerModel.setPre_field(jsonArrayQuestions.getJSONObject(count).getString("pre_field"));
                                    answerModel.setField_name(jsonArrayQuestions.getJSONObject(count).getString("field_name"));
                                    answerModelList.add(answerModel);
                                }
                                if(jsonArrayQuestions.getJSONObject(count).getString("validation_id").equals("1") && editText.getText().toString().trim().equals("")){
                                    flag=false;
                                    break;
                                }
                                nextPosition++;
                                count++;
                            }
                            if (childView instanceof Button) {
                                count++;
                            }
                            else if (childView instanceof RadioGroup) {
                                RadioGroup radioGroup = (RadioGroup) childView;
                                int selectedRadioButtonId = radioGroup.getCheckedRadioButtonId();
                                RadioButton selectedRadioButton = (RadioButton) childView.findViewById(selectedRadioButtonId);
                                int radioID=0;
                                if (selectedRadioButton != null) {
                                    String strTag = selectedRadioButton.getTag().toString();
                                    int sepPos = strTag.indexOf("^");
                                    radioID = Integer.parseInt(strTag.substring(0, sepPos));
                                }
                                if((back_status==true || screen_type.equals("survey_list")) && answerModelList.size()>nextPosition){
                                    //if((back_status==true || screen_type.equals("survey_list")) && answerModelList.get(startPositionBefore).getQuestionID().equals(jsonArrayQuestions.getJSONObject(count).getString("question_id"))){
                                    answerModelList.get(nextPosition).setOption_id(Integer.toString(radioID));
                                }else{
                                    AnswerModel answerModel= new AnswerModel();
                                    answerModel.setOption_id(Integer.toString(radioID));
                                    answerModel.setOption_value("");
                                    //answerModel.setSurveyID(survey_id);
                                    answerModel.setQuestionID(jsonArrayQuestions.getJSONObject(count).getString("question_id"));
                                    answerModel.setPre_field(jsonArrayQuestions.getJSONObject(count).getString("pre_field"));
                                    answerModel.setField_name(jsonArrayQuestions.getJSONObject(count).getString("field_name"));
                                    answerModelList.add(answerModel);
                                }
                                if(jsonArrayQuestions.getJSONObject(count).getString("validation_id").equals("1") && radioID==0){
                                    flag=false;
                                    break;
                                }
                                nextPosition++;
                                count++;
                            }
                            else if (childView instanceof Spinner) {
                                Spinner spinner = (Spinner) childView;
                                if((back_status==true || screen_type.equals("survey_list")) && answerModelList.size()>nextPosition){
                                    answerModelList.get(nextPosition).setOption_id(Long.toString(spinner.getSelectedItemId()));
                                }else{
                                    AnswerModel answerModel= new AnswerModel();
                                    answerModel.setOption_id(Long.toString(spinner.getSelectedItemId()));
                                    answerModel.setOption_value("");
                                    //answerModel.setSurveyID(survey_id);
                                    answerModel.setQuestionID(jsonArrayQuestions.getJSONObject(count).getString("question_id"));
                                    answerModel.setPre_field(jsonArrayQuestions.getJSONObject(count).getString("pre_field"));
                                    answerModel.setField_name(jsonArrayQuestions.getJSONObject(count).getString("field_name"));
                                    answerModelList.add(answerModel);
                                }
                                if(jsonArrayQuestions.getJSONObject(count).getString("validation_id").equals("1") && spinner.getSelectedItemId()==0){
                                    flag=false;
                                    break;
                                }
                                nextPosition++;
                                count++;
                            }
                        else if(childView instanceof TableLayout){
                                TableLayout tableLayout = (TableLayout) childView;
                                String selectedOptions="";
                                for (int k = 0; k < tableLayout.getChildCount(); k++) {
                                    //final View childViewTable = tableLayout.getChildAt(k);
                                    final TableRow row = (TableRow) tableLayout.getChildAt(k);
                                    CheckBox checkBox = (CheckBox) row.getChildAt(0);
                                    if (checkBox.isChecked()) {
                                        jsonArray.put(checkBox.getText().toString().trim());
                                        jsonObject.put("check_box", jsonArray);
                                        if(selectedOptions.equals("")){
                                            selectedOptions=Integer.toString(checkBox.getId());
                                        }else{
                                            selectedOptions=selectedOptions+","+Integer.toString(checkBox.getId());
                                        }
                                    }
                                }
                                if((back_status==true || screen_type.equals("survey_list")) && answerModelList.size()>nextPosition){
                                    answerModelList.get(nextPosition).setOption_id(selectedOptions);
                                }else{
                                    AnswerModel answerModel= new AnswerModel();
                                    answerModel.setOption_id(selectedOptions);
                                    answerModel.setOption_value("");
                                    //answerModel.setSurveyID(survey_id);
                                    answerModel.setQuestionID(jsonArrayQuestions.getJSONObject(count).getString("question_id"));
                                    answerModel.setPre_field(jsonArrayQuestions.getJSONObject(count).getString("pre_field"));
                                    answerModel.setField_name(jsonArrayQuestions.getJSONObject(count).getString("field_name"));
                                    answerModelList.add(answerModel);
                                }
                                if(jsonArrayQuestions.getJSONObject(count).getString("validation_id").equals("1") && selectedOptions.equals("")){
                                    flag=false;
                                    break;
                                }
                                nextPosition++;
                                count++;
                        }
                            else{
                                childView.getRootView();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                if(buttonText.equals("Submit")){
                    //Toast.makeText(getApplicationContext(),"Thank you participation",Toast.LENGTH_LONG).show();
                    //save data in to local DB.
                    Gson gson = new Gson();
                    String listString = gson.toJson(
                            answerModelList,
                            new TypeToken<ArrayList<AnswerModel>>() {}.getType());
                    String listStringFamily = gson.toJson(
                            answerModelHouseholdMemberList,
                            new TypeToken<ArrayList<AnswerModel>>() {}.getType());
                    String listStringTV = gson.toJson(
                            answerModelTVList,
                            new TypeToken<ArrayList<AnswerModel>>() {}.getType());
                    try {
                        JSONArray json_array =  new JSONArray(listString);
                        JSONArray json_array_family =  new JSONArray(listStringFamily);
                        JSONArray json_array_TV =  new JSONArray(listStringTV);
                        JSONObject json_object=new JSONObject();
                        json_object.put("user_id", sharedPrefHelper.getString("user_id", ""));
                        json_object.put("survey_id", survey_id);
                        json_object.put("cluster_no", sharedPrefHelper.getString("cluster_no", ""));
                        json_object.put("census_district_code", sharedPrefHelper.getString("census_district_code", ""));
                        json_object.put("GPS_latitude", "27.883743");
                        json_object.put("GPS_longitude", "79.912247");
                        /*json_object.put("GPS_latitude", sharedPrefHelper.getString("LAT", ""));
                        json_object.put("GPS_longitude", sharedPrefHelper.getString("LONG", ""));*/
                        json_object.put("survey_data", json_array);
                        json_object.put("family_data", json_array_family);
                        json_object.put("tv_data", json_array_TV);
                        Log.e(TAG, "onClick: "+json_object.toString());

                        if (screen_type.equals("survey_list")) {
                            sqliteHelper.updateSurveyDataInTable("survey", "survey_id", survey_id, json_object);
                            Intent intentSurveyActivity1=new Intent(context, ClusterDetails.class);
                            startActivity(intentSurveyActivity1);
                            finish();
                        } else {
                            //sqliteHelper.saveSurveyDataInTable(json_object, survey_id);
                            //update data in to local DB
                            sqliteHelper.updateSurveyDataInTable("survey", "survey_id", survey_id, json_object);
                            if (CommonClass.isInternetOn(context)) {
                                //get all data from survey table
                                //sqliteHelper.getAllSurveyDataFromTable(survey_id);
                                /*Gson gson = new Gson();
                                String data = gson.toJson(clusterModel);*/
                                String data = json_object.toString();
                                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                                RequestBody body = RequestBody.create(JSON, data);
                                //send data on server
                                sendSurveyDataOnServer(body);
                            } else {
                                Intent intentSurveyActivity1=new Intent(context, ClusterDetails.class);
                                startActivity(intentSurveyActivity1);
                                finish();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    //back_status=false;
                    if(flag==true){
                        sharedPrefHelper.setInt("startPosition", startPosition);
                        endPosition = endPosition + length;
                        if (endScreenPosition<totalScreen) {
                            btn_next.setText("Next");
                            sharedPrefHelper.setInt("endPosition", endPosition);
                            //save survey data JSON on every next click in DB
                            if (!survey_id.equals("")) {
                                Gson gson = new Gson();
                                String listString = gson.toJson(
                                        answerModelList,
                                        new TypeToken<ArrayList<AnswerModel>>() {}.getType());
                                try {
                                    JSONArray json_array =  new JSONArray(listString);
                                    JSONObject json_object=new JSONObject();
                                    json_object.put("user_id", sharedPrefHelper.getString("user_id", ""));
                                    json_object.put("survey_id", survey_id);
                                    json_object.put("cluster_no", sharedPrefHelper.getString("cluster_no", ""));
                                    json_object.put("census_district_code", sharedPrefHelper.getString("census_district_code", ""));
                                    json_object.put("GPS_latitude", "27.883743");
                                    json_object.put("GPS_longitude", "79.912247");
                                    /*json_object.put("GPS_latitude", sharedPrefHelper.getString("LAT", ""));
                                    json_object.put("GPS_longitude", sharedPrefHelper.getString("LONG", ""));*/
                                    json_object.put("survey_data", json_array);
                                    Log.e(TAG, "onClick: "+json_object.toString());

                                if (endScreenPosition==1) {
                                    //save data in to local DB.
                                    sqliteHelper.saveSurveyDataInTable(json_object, survey_id);
                                    sqliteHelper.updateLocalFlag("partial", "survey",
                                            Integer.parseInt(sharedPrefHelper.getString("survey_id", "")), 1);
                                } else {
                                    //update data in to local DB
                                    sqliteHelper.updateSurveyDataInTable("survey", "survey_id", survey_id, json_object);
                                    sqliteHelper.updateLocalFlag("partial", "survey",
                                            Integer.parseInt(sharedPrefHelper.getString("survey_id", "")), 1);
                                }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        else {
                            //endPosition = totalQuestions;
                            btn_next.setText("Submit");
                            back_status=false;
                            sharedPrefHelper.setInt("endPosition", totalQuestions);
                        }
                        Log.e(TAG, "Position >>> endPosition >>>" + endPosition + "startPosition >>>" + startPosition+"startPositionBefore >>>" + startPositionBefore);
                        if(endPosition!=0)
                            endPosition=endPosition-1;
                        if(back_status==true && startScreenPosition<arrayScreenWiseQuestionModel.size()){
                            arrayScreenWiseQuestionModel.get(startScreenPosition).setscreen_id(screen_id);
                            arrayScreenWiseQuestionModel.get(startScreenPosition).setquestions(""+endPosition);
                        }else{
                            ScreenWiseQuestionModel screenWiseQuestionModel=new ScreenWiseQuestionModel();
                            screenWiseQuestionModel.setscreen_id(screen_id);
                            screenWiseQuestionModel.setquestions(""+endPosition);
                            arrayScreenWiseQuestionModel.add(screenWiseQuestionModel);
                        }
                        startScreenPosition++;
                        endScreenPosition++;
                        //condition for open group-fragment
                        String groupRelationId=null;
                        try {
                            groupRelationId = jsonArrayQuestions.getJSONObject(count).getString("group_relation_id");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (groupRelationId!=null&&!groupRelationId.equalsIgnoreCase("0")) {
                           if (groupRelationId.equalsIgnoreCase("1")) {
                                Bundle bundle=new Bundle();
                                bundle.putInt("editFieldValues", Integer.parseInt(editFieldValues));
                                bundle.putInt("startScreenPosition", startScreenPosition);
                                bundle.putInt("endScreenPosition", endScreenPosition);
                                bundle.putInt("groupRelationId", Integer.parseInt(groupRelationId));
                                bundle.putInt("questionID", groupQuestionID);
                                Fragment fragment = new GroupRelationFragment();
                                fragment.setArguments(bundle);
                                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                                transaction.replace(R.id.group_relation_fragment, fragment); // fragment container id in first parameter is the  container(Main layout id) of Activity
                                transaction.addToBackStack(null);  // this will manage backstack
                                transaction.commit();
                            }
                            if (groupRelationId.equalsIgnoreCase("2")) {
                                Bundle bundle=new Bundle();
                                bundle.putInt("editFieldValues", Integer.parseInt(editFieldValues));
                                bundle.putInt("startScreenPosition", startScreenPosition);
                                bundle.putInt("endScreenPosition", endScreenPosition);
                                bundle.putInt("groupRelationId", Integer.parseInt(groupRelationId));
                                bundle.putInt("questionID", groupQuestionID);
                                Fragment fragment = new GroupTVFragment();
                                fragment.setArguments(bundle);
                                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                                transaction.replace(R.id.group_relation_fragment, fragment); // fragment container id in first parameter is the  container(Main layout id) of Activity
                                transaction.addToBackStack(null);  // this will manage backstack
                                transaction.commit();
                            }
                        }else {
                            questionsPopulate();
                        }
                        Log.e(TAG, "onNextClick- " + jsonObject.toString());
                    }else{
                        Toast.makeText(context,"Please fill all required fields",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        btn_previous.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                /*if(endScreenPosition==totalScreen){
                    startPosition=startPosition-1;
                }else{
                    startPosition=startPosition-length;
                }*/
                startScreenPosition=startScreenPosition-1;
                endScreenPosition=endScreenPosition-1;
                /*endPosition=startPosition;
                startPosition=endPosition-length;
                //endPosition = endPosition + length;
                sharedPrefHelper.setInt("endPosition", endPosition);
                Log.e(TAG, "Position >>> endPosition >>>" + endPosition + "startPosition >>>" + startPosition);*/
                back_status=true;

                if(endScreenPosition<=0){
                Intent intentHom= new Intent(HouseholdSurveyActivity.this, ClusterDetails.class);
                startActivity(intentHom);
                finish();
                }else{
                    btn_next.setText("Next");
                    startPosition=startPosition-(endPosition+Integer.parseInt(arrayScreenWiseQuestionModel.get(startScreenPosition).getquestions()));
                    questionsPopulate();
                }
            }
        });
        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommonClass.setPopupForStopSurvey(context);
            }
        });
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
                        //update id on the bases of survey id
                        sqliteHelper.updateServerId("survey", Integer.parseInt(survey_id), survey_data_monitoring_id);
                        sqliteHelper.updateLocalFlag("household_survey","survey", Integer.parseInt(survey_id), 1);
                        Intent intentSurveyActivity1=new Intent(context, ClusterDetails.class);
                        startActivity(intentSurveyActivity1);
                        finish();
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

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = context.getAssets().open("barc_question_json.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public void questionsPopulate(){
       try{
           ll_parent.removeAllViews();
           startPositionBefore=startPosition;
           endPosition=0;
           String name="";
           for(int l=startScreenPosition;l<endScreenPosition;l++){
               JSONObject jsonObjectScreen=jsonArrayScreen.getJSONObject(l);
               screen_id=jsonObjectScreen.getString("screen_no");
               jsonArrayQuestions = jsonObjectScreen.getJSONArray("questions");
               for (int i = 0; i < jsonArrayQuestions.length(); i++) {
                   JSONObject jsonObjectQuesType=jsonArrayQuestions.getJSONObject(i);
                   if (jsonObjectQuesType.getString("question_type").equals("1")) {
                       TextView txtLabel = new TextView(this);
                       EditText editText=new EditText(this);
                       editText.setId(Integer.parseInt(jsonObjectQuesType.getString("question_id")));
                       editText.setTextSize(12);
                       //pre field for screen 1
                       if (jsonObjectQuesType.getString("field_name").equals("mdl_id")) {
                           editText.setText(sharedPrefHelper.getString("mdl_id", ""));
                       } else if (jsonObjectQuesType.getString("field_name").equals("supervisor_id")) {
                           editText.setText(sharedPrefHelper.getString("supervisor_id", ""));
                       } else if (jsonObjectQuesType.getString("field_name").equals("supervisor_name")) {
                           editText.setText(sharedPrefHelper.getString("supervisor_name", ""));
                       } else if (jsonObjectQuesType.getString("field_name").equals("interviewer_id")) {
                           editText.setText(sharedPrefHelper.getString("interviewer_id", ""));
                       } else if (jsonObjectQuesType.getString("field_name").equals("interviewer_name")) {
                           editText.setText(sharedPrefHelper.getString("interviewer_name", ""));
                       } else if (jsonObjectQuesType.getString("field_name").equals("current_date")) {
                           editText.setText(sharedPrefHelper.getString("current_date", ""));
                       } //pre field for screen 2
                       else if (jsonObjectQuesType.getString("field_name").equals("Census_Village_Town_Name")) {
                           editText.setText(sharedPrefHelper.getString("census_village_town_name", ""));
                       } else if (jsonObjectQuesType.getString("field_name").equals("State_Name")) {
                           editText.setText(sharedPrefHelper.getString("state_name", ""));
                       } else if (jsonObjectQuesType.getString("field_name").equals("Census_District_Name")) {
                           editText.setText(sharedPrefHelper.getString("census_district_name", ""));
                       } else if (jsonObjectQuesType.getString("field_name").equals("Town_Village_Class")) {
                           editText.setText(sharedPrefHelper.getString("town_village_class", ""));
                       } else if (jsonObjectQuesType.getString("field_name").equals("BARC_Town_Code")) {
                           editText.setText(sharedPrefHelper.getString("barc_town_code", ""));
                       } //pre field for screen 3
                       else if (jsonObjectQuesType.getString("field_name").equals("UA_Component")) {
                           editText.setText(sharedPrefHelper.getString("ua_component", ""));
                       } else if (jsonObjectQuesType.getString("field_name").equals("UA_Component_code")) {
                           editText.setText(sharedPrefHelper.getString("ua_component_code", ""));
                       } else if (jsonObjectQuesType.getString("field_name").equals("Census_Village_Town_Code")) {
                           editText.setText(sharedPrefHelper.getString("census_village_town_code", ""));
                       } else if (jsonObjectQuesType.getString("field_name").equals("cluster_no")) {
                           editText.setText(sharedPrefHelper.getString("cluster_no", ""));
                       }//pre field for screen 15
                       else if (jsonObjectQuesType.getString("field_name").equals("nccs_hh")) {
                           editText.setText(sharedPrefHelper.getString("nccs_hh", ""));
                       }
                       else if (jsonObjectQuesType.getString("field_name").equals("interview_number")) {
                           editText.setText(survey_id);
                       }
                       else if (jsonObjectQuesType.getString("field_name").equals("nccs_matrix")) {
                           editText.setText(sharedPrefHelper.getString("nccs_matrix", ""));
                       }
                       if(jsonObjectQuesType.getString("question_input_type").equals("2")){
                           editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
                       }else{
                           editText.setInputType(InputType.TYPE_CLASS_TEXT);
                       }
                       editText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(Integer.parseInt(jsonObjectQuesType.getString("max_limit")))});
                       if(jsonObjectQuesType.getString("pre_field").equals("1")){
                           editText.setEnabled(false);
                       }
                       if((back_status==true || screen_type.equals("survey_list")) && answerModelList.size()>startPosition){
                           editText.setText(answerModelList.get(startPosition).getOption_value());
                       }
                       String description=jsonObjectQuesType.getString("question_name");
                       //description=description.replaceAll("$name",sharedPrefHelper.getString("name","Ram"));
                       description=description.replaceAll("\\$name",sharedPrefHelper.getString("name","Ram"));
                       txtLabel.setText(description);
                       txtLabel.setTypeface(null, Typeface.BOLD);
                       txtLabel.setTextSize(14);
                       startPosition++;
                       endPosition++;
                       ll_parent.addView(txtLabel);
                       ll_parent.addView(editText);
                       //onAddEditField(jsonObjectQuesType);
                   }
                   else if (jsonObjectQuesType.getString("question_type").equals("2")) {
                       TextView txtLabel = new TextView(this);
                       String description=jsonObjectQuesType.getString("question_name");
                       description=description.replaceAll("$name",sharedPrefHelper.getString("name","Ram"));
                       txtLabel.setText(description);
                       txtLabel.setTextSize(14);
                       txtLabel.setTypeface(null, Typeface.BOLD);
                       ll_parent.addView(txtLabel);
                       JSONArray jsonArrayOptions = jsonObjectQuesType.getJSONArray("question_options");
                       RadioGroup radioGroup=new RadioGroup(this);
                       radioGroup.setId(i);
                       for (int j = 0; j <jsonArrayOptions.length() ; j++) {
                           RadioButton radioButton=new RadioButton(this);
                           radioButton.setLayoutParams(new LinearLayout.LayoutParams
                                   (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                           JSONObject jsonObjectOptionValues=jsonArrayOptions.getJSONObject(j);
                           radioButton.setText(jsonObjectOptionValues.getString("option_value"));
                           radioButton.setTextSize(12);
                           String str_id=i+""+j+""+jsonObjectOptionValues.getString("option_id");
                           int idd=Integer.parseInt(str_id);
                           radioButton.setId(idd);
                           radioButton.setTag(Integer.parseInt(jsonObjectOptionValues.getString("option_id"))+"^"+Integer.parseInt(jsonObjectOptionValues.getString("is_terminate")));
                           if((back_status==true || screen_type.equals("survey_list")) && answerModelList.size()>startPosition){
                               if(answerModelList.get(startPosition).getOption_id().equals(jsonObjectOptionValues.getString("option_id"))){
                                   radioButton.setChecked(true);
                               }
                           }else if(jsonObjectQuesType.getString("question_id").equals("22") && jsonObjectOptionValues.getString("option_id").equals(sharedPrefHelper.getString("address_type", "0"))){
                               radioButton.setChecked(true);
                           }
                           if (radioGroup != null) {
                               radioGroup.addView(radioButton);
                           }
                       }
                       startPosition++;
                       endPosition++;
                       ll_parent.addView(radioGroup);
                       radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
                       {
                           @Override
                           public void onCheckedChanged(RadioGroup group, int checkedId) {
                               RadioButton rb=(RadioButton)findViewById(checkedId);
                               String rbTag=rb.getTag().toString();
                               int sepPos = rbTag.indexOf("^");
                               String id=rbTag.substring(sepPos+1);
                               if(id.equals("1")){
                                   setTerminattion(id);
                                   //Toast.makeText(context,"Termination true"+rb.getText()+"group.getId()"+group.getId(),Toast.LENGTH_LONG).show();
                               }
                           }
                       });
                       //onAddRadioButton(jsonObjectQuesType);
                   }
                   else if (jsonObjectQuesType.getString("question_type").equals("3")) {
                       TextView txtLabel = new TextView(this);
                       String description=jsonObjectQuesType.getString("question_name");
                       description=description.replaceAll("$name",sharedPrefHelper.getString("name","Ram"));
                       txtLabel.setText(description);
                       txtLabel.setTextSize(14);
                       txtLabel.setTypeface(null, Typeface.BOLD);
                       ll_parent.addView(txtLabel);
                       JSONArray jsonArrayOptions = jsonObjectQuesType.getJSONArray("question_options");
                       TableLayout linearLayoutCheckbox= new TableLayout(this);
                       linearLayoutCheckbox.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                       String selectedOptions="";
                       /*if((back_status==true || screen_type.equals("survey_list")) && answerModelList.size()>i){
                           selectedOptions=answerModelList.get(startPosition).getOption_id();
                           String[] arraySelectedOptions = selectedOptions.split(",");
                       }*/
                       for (int j = 0; j <jsonArrayOptions.length(); j++) {
                           TableRow row =new TableRow(this);
                           row.setId(j);
                           row.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                           JSONObject jsonObject1=jsonArrayOptions.getJSONObject(j);
                           CheckBox checkBox=new CheckBox(this);
                           checkBox.setText(jsonObject1.getString("option_value"));
                           checkBox.setTextSize(12);
                           checkBox.setId(Integer.parseInt(jsonObject1.getString("option_id")));
                           if((back_status==true || screen_type.equals("survey_list")) && answerModelList.size()>startPosition){
                               selectedOptions=answerModelList.get(startPosition).getOption_id();
                               String[] arraySelectedOptions = selectedOptions.split(",");
                               boolean contains = Arrays.asList(arraySelectedOptions).contains(jsonObject1.getString("option_id"));
                               if(contains){
                                   checkBox.setChecked(true);
                               }
                           }
                           row.addView(checkBox);
                           linearLayoutCheckbox.addView(row);
                       }
                       startPosition++;
                       endPosition++;
                       ll_parent.addView(linearLayoutCheckbox);

                       //   onAddCheckBox(jsonObjectQuesType);
                   }
                   else if (jsonObjectQuesType.getString("question_type").equals("4")) {
                       TextView txtLabel = new TextView(this);
                       String description=jsonObjectQuesType.getString("question_name");
                       description=description.replaceAll("$name",sharedPrefHelper.getString("name","Ram"));
                       txtLabel.setText(description);
                       txtLabel.setTextSize(14);
                       txtLabel.setTypeface(null, Typeface.BOLD);
                       ll_parent.addView(txtLabel);
                       JSONArray jsonArrayOptions = jsonObjectQuesType.getJSONArray("question_options");
                       Spinner spinner=new Spinner(this);
                       ArrayList<String> spinnerAL=new ArrayList<>();
                       for (int j = 0; j <jsonArrayOptions.length() ; j++) {
                           spinnerAL.clear();
                           for (int k = 0; k < jsonArrayOptions.length(); k++) {
                               JSONObject jsonObjectOptionValues=jsonArrayOptions.getJSONObject(k);
                               String spinnerOption=jsonObjectOptionValues.getString("option_value");
                               spinnerAL.add(spinnerOption);
                           }
                           spinnerAL.add(0, getString(R.string.select_option));
                           ArrayAdapter arrayAdapter=new ArrayAdapter(this, R.layout.custom_spinner_dropdown, spinnerAL);
                           spinner.setAdapter(arrayAdapter);
                           if((back_status==true || screen_type.equals("survey_list")) && answerModelList.size()>startPosition){
                               spinner.setSelection(Integer.parseInt(answerModelList.get(startPosition).getOption_id()));
                           }
                       }
                       startPosition++;
                       endPosition++;
                       ll_parent.addView(spinner);

                       // onAddSpinner(jsonObjectQuesType);
                   }
                   else if (jsonObjectQuesType.getString("question_type").equals("5")) {
                       Button button=new Button(this);
                       TextView textView=new TextView(this);
                       textView.setId(Integer.parseInt(jsonObjectQuesType.getString("question_id")));
                       if((back_status==true || screen_type.equals("survey_list")) && answerModelList.size()>i){
                       }
                       button.setText(jsonObjectQuesType.getString("question_name"));
                       button.setTypeface(null, Typeface.BOLD);
                       button.setTextColor(Color.WHITE);
                       button.setBackgroundResource(R.drawable.btn_background);

                       ll_parent.addView(button);
                       ll_parent.addView(textView);
                       button.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View view) {
                               textView.setText("Latitude: "+sharedPrefHelper.getString("LAT", "") +"\n"+
                                       "Longitude: "+sharedPrefHelper.getString("LONG", ""));
                           }
                       });
                   }
                   else if (jsonObjectQuesType.getString("question_type").equals("6")) {
                       TextView textView=new TextView(this);
                       textView.setId(Integer.parseInt(jsonObjectQuesType.getString("question_id")));
                       String description=jsonObjectQuesType.getString("question_name");
                       description=description.replaceAll("\\$name",sharedPrefHelper.getString("name","Ram"));
                       description=description.replaceAll("\\$agency",sharedPrefHelper.getString("agency_name","Ram"));
                       if((back_status==true || screen_type.equals("survey_list")) && answerModelList.size()>i){
                       }
                       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                           textView.setText(Html.fromHtml(description, Html.FROM_HTML_MODE_COMPACT));
                       } else {
                           textView.setText(Html.fromHtml(description));
                       }
                       //textView.setText(jsonObjectQuesType.getString("question_name"));
                       ll_parent.addView(textView);
                   }
               }
           }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            //finish();
            try {
                /*if(endScreenPosition==totalScreen){
                    startPosition=startPosition-1;
                }else{
                    startPosition=startPosition-length;
                }*/
                startScreenPosition=startScreenPosition-1;
                endScreenPosition=endScreenPosition-1;
                /*endPosition=startPosition;
                startPosition=endPosition-length;
                //endPosition = endPosition + length;
                sharedPrefHelper.setInt("endPosition", endPosition);
                Log.e(TAG, "Position >>> endPosition >>>" + endPosition + "startPosition >>>" + startPosition);*/
                back_status=true;

                if(endScreenPosition<=0){
                    Intent intentHom= new Intent(HouseholdSurveyActivity.this, ClusterDetails.class);
                    startActivity(intentHom);
                    finish();
                }else{
                    btn_next.setText("Next");
                    startPosition=startPosition-(endPosition+Integer.parseInt(arrayScreenWiseQuestionModel.get(startScreenPosition).getquestions()));
                    questionsPopulate();
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (item.getItemId()==R.id.stop_survey) {
            showPopupForTerminateSurvey();
        }
        if (item.getItemId()==R.id.home_icon) {
            Intent intentMainMenu=new Intent(context, MainMenu.class);
            intentMainMenu.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intentMainMenu);
        }
        return super.onOptionsItemSelected(item);
    }

    public void setTerminattion(String id) {
        Intent intentTerminate = new Intent(context, TerminateActivity.class);
        intentTerminate.putExtra("screen_type", "terminate");
        intentTerminate.putExtra("radio_button_id", id);//id=(reason)
        intentTerminate.putExtra("answerModelList", answerModelList);
        startActivity(intentTerminate);
        /*//save data in to local DB.
        Gson gson = new Gson();
        String listString = gson.toJson(
                answerModelList,
                new TypeToken<ArrayList<AnswerModel>>() {
                }.getType());
        try {
            JSONArray json_array = null;
            JSONObject json_object = null;
            try {
                json_array = new JSONArray(listString);
                json_object = new JSONObject();
                json_object.put("user_id", sharedPrefHelper.getString("user_id", ""));
                json_object.put("survey_id", survey_id);
                if (!id.equals("")) {
                    json_object.put("reason", id);
                }
                json_object.put("survey_data", json_array);
                Log.e(TAG, "onClick: " + json_object.toString());

                sqliteHelper.saveSurveyDataInTable(json_object, survey_id);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            intentTerminate.putExtra("json_object", json_object.toString());
            startActivity(intentTerminate);
        } catch (Exception e) {
        e.printStackTrace();
        }*/
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
                        setTerminattion("");
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        /*hide and show toolbar items*/
        if (screen_type.equalsIgnoreCase("survey")) {
            MenuItem item_stop_survey=menu.findItem(R.id.stop_survey);
            item_stop_survey.setVisible(true);
            MenuItem item_logout=menu.findItem(R.id.logout);
            item_logout.setVisible(false);
        }

        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("pause","onpause Called");
    }
    protected OnBackPressedListener onBackPressedListener;

    public interface OnBackPressedListener {
        void doBack();
    }

    public void setOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
        this.onBackPressedListener = onBackPressedListener;
    }

    @Override
    public void onBackPressed() {
        if (onBackPressedListener != null)
            onBackPressedListener.doBack();
    }

    @Override
    protected void onDestroy() {
        onBackPressedListener = null;
        super.onDestroy();
    }
    @Override
    public void passDataToActivity(ArrayList<AnswerModel> answerModelList,int type){
       // Log.e("Fragment Dismissed",someValue);
        if(type==1){
            answerModelHouseholdMemberList.clear();
            ArrayList<AnswerModel> answerModelListN=answerModelList;
            answerModelHouseholdMemberList=answerModelListN;
        }else if(type==2){
            answerModelTVList.clear();
            ArrayList<AnswerModel> answerModelListN=answerModelList;
            answerModelTVList=answerModelListN;
        }
        questionsPopulate();
        //textView.setText(someValue);
    }

}
