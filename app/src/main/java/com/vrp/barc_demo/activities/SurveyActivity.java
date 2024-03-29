/*
 * Copyright (c)  2020. Indev Consultancy Private Limited,
 * Auther : Vimal Kumar
 * Date : 2020/12/15
 * Modified Date :
 * Modified By :
 */

package com.vrp.barc_demo.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
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

import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vrp.barc_demo.R;
import com.vrp.barc_demo.models.AnswerModel;
import com.vrp.barc_demo.sqlite_db.SqliteHelper;
import com.vrp.barc_demo.utils.CommonClass;
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

public class SurveyActivity extends AppCompatActivity {
    private static final String TAG = "Survey_Activity";
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
    private int length=5;
    private int startPosition;
    private int startScreenPosition;
    private int startPositionBefore;
    private int endPosition;
    private int endScreenPosition;
    SharedPrefHelper sharedPrefHelper;
    int totalQuestions;
    int totalScreen;
    JSONObject jsonQuestions = null;
    JSONArray jsonArrayQuestions=null;
    JSONArray jsonArrayScreen=null;
    ArrayList<AnswerModel> answerModelList;
    boolean back_status=false;
    private SqliteHelper sqliteHelper;
    private String surveyObjectJSON=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        ButterKnife.bind(this);
        setTitle(R.string.survey);
        initialization();

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
                if (jsonQuestions.has("screen")) {
                    jsonArrayScreen = jsonQuestions.getJSONArray("screen");
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
    }

    private void setButtonClick() {
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button b = (Button)view;
                String buttonText = b.getText().toString();
                    JSONArray jsonArray = new JSONArray();
                    final JSONObject jsonObject = new JSONObject();
                    for (int i = 0; i < ll_parent.getChildCount(); i++) {
                        final View childView = ll_parent.getChildAt(i);
                        try {
                        /*JSONArray jsonArrayET = new JSONArray();
                        final JSONObject jsonObjectET = new JSONObject();*/
                            if (childView instanceof EditText) {
                                EditText editText = (EditText) childView;
                                int viewID=editText.getId();

                                /*jsonArray.put(editText.getText().toString().trim());
                                jsonObject.put("edit_text", jsonArray);*/
                                if((back_status==true || screen_type.equals("survey_list")) && answerModelList.size()>startPositionBefore){
                                    answerModelList.get(startPositionBefore).setOption_value(editText.getText().toString().trim());
                                }else{
                                    AnswerModel answerModel= new AnswerModel();
                                    answerModel.setOption_id("");
                                    answerModel.setOption_value(editText.getText().toString().trim());
                                    answerModel.setSurveyID(survey_id);
                                    answerModel.setQuestionID(jsonArrayQuestions.getJSONObject(startPositionBefore).getString("question_id"));
                                    answerModel.setPre_field(jsonArrayQuestions.getJSONObject(startPositionBefore).getString("pre_field"));
                                    answerModelList.add(answerModel);
                                }
                                startPositionBefore++;
                            }
                            if (childView instanceof Button) {
                                if((back_status==true || screen_type.equals("survey_list")) && answerModelList.size()>startPositionBefore){
                                    answerModelList.get(startPositionBefore).setOption_value("");
                                }else{
                                    AnswerModel answerModel= new AnswerModel();
                                    answerModel.setOption_id("");
                                    answerModel.setOption_value("");
                                    answerModel.setSurveyID(survey_id);
                                    answerModel.setQuestionID(jsonArrayQuestions.getJSONObject(startPositionBefore).getString("question_id"));
                                    answerModel.setPre_field(jsonArrayQuestions.getJSONObject(startPositionBefore).getString("pre_field"));
                                    answerModelList.add(answerModel);
                                }
                                startPositionBefore++;
                            }
                        /*JSONArray jsonArrayRG = new JSONArray();
                        final JSONObject jsonObjectRG = new JSONObject();*/
                            else if (childView instanceof RadioGroup) {
                                RadioGroup radioGroup = (RadioGroup) childView;
                                int selectedRadioButtonId = radioGroup.getCheckedRadioButtonId();
                                android.widget.RadioButton selectedRadioButton = (android.widget.RadioButton) childView.findViewById(selectedRadioButtonId);
                                if (selectedRadioButton != null) {
                                    if((back_status==true || screen_type.equals("survey_list")) && answerModelList.size()>startPositionBefore){
                                        answerModelList.get(startPositionBefore).setOption_id(Integer.toString(selectedRadioButton.getId()));
                                    }else{
                                        AnswerModel answerModel= new AnswerModel();
                                        answerModel.setOption_id(Integer.toString(selectedRadioButton.getId()));
                                        answerModel.setOption_value("");
                                        answerModel.setSurveyID(survey_id);
                                        answerModel.setQuestionID(jsonArrayQuestions.getJSONObject(startPositionBefore).getString("question_id"));
                                        answerModel.setPre_field(jsonArrayQuestions.getJSONObject(startPositionBefore).getString("pre_field"));
                                        answerModelList.add(answerModel);
                                    }
                                    startPositionBefore++;
                                }
                            }
                        /*JSONArray jsonArraySPN = new JSONArray();
                        final JSONObject jsonObjectSPN = new JSONObject();*/
                            else if (childView instanceof Spinner) {
                                Spinner spinner = (Spinner) childView;
                                if((back_status==true || screen_type.equals("survey_list")) && answerModelList.size()>startPositionBefore){
                                    answerModelList.get(startPositionBefore).setOption_id(Long.toString(spinner.getSelectedItemId()));
                                }else{
                                    AnswerModel answerModel= new AnswerModel();
                                    answerModel.setOption_id(Long.toString(spinner.getSelectedItemId()));
                                    answerModel.setOption_value("");
                                    answerModel.setSurveyID(survey_id);
                                    answerModel.setQuestionID(jsonArrayQuestions.getJSONObject(startPositionBefore).getString("question_id"));
                                    answerModel.setPre_field(jsonArrayQuestions.getJSONObject(startPositionBefore).getString("pre_field"));
                                    answerModelList.add(answerModel);
                                }
                                startPositionBefore++;
                            }
                        /*JSONArray jsonArrayCHK = new JSONArray();
                        final JSONObject jsonObjectCHK = new JSONObject();*/
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
                                if((back_status==true || screen_type.equals("survey_list")) && answerModelList.size()>startPositionBefore){
                                    answerModelList.get(startPositionBefore).setOption_id(selectedOptions);
                                }else{
                                    AnswerModel answerModel= new AnswerModel();
                                    answerModel.setOption_id(selectedOptions);
                                    answerModel.setOption_value("");
                                    answerModel.setSurveyID(survey_id);
                                    answerModel.setQuestionID(jsonArrayQuestions.getJSONObject(startPositionBefore).getString("question_id"));
                                    answerModel.setPre_field(jsonArrayQuestions.getJSONObject(startPositionBefore).getString("pre_field"));
                                    answerModelList.add(answerModel);
                                }
                                startPositionBefore++;
                        }
                        /*else if (childView instanceof TextView) {
                                TextView textView = (TextView) childView;
                                int viewID=textView.getId();

                                *//*jsonArray.put(editText.getText().toString().trim());
                                jsonObject.put("edit_text", jsonArray);*//*
                                if(back_status==true || screen_type.equals("survey_list")){
                                    answerModelList.get(startPositionBefore).setOption_value(textView.getText().toString().trim());
                                }else{
                                    AnswerModel answerModel= new AnswerModel();
                                    answerModel.setOption_id("");
                                    answerModel.setOption_value(textView.getText().toString().trim());
                                    answerModel.setSurveyID(survey_id);
                                    answerModel.setQuestionID(jsonArrayQuestions.getJSONObject(startPositionBefore).getString("question_id"));
                                    answerModel.setPre_field(jsonArrayQuestions.getJSONObject(startPositionBefore).getString("pre_field"));
                                    answerModelList.add(answerModel);
                                }
                                startPositionBefore++;
                            }*/
                            /*else if (childView instanceof CheckBox) {
                                CheckBox checkBox = (CheckBox) childView;
                                String selectedOptions="";
                                if (checkBox.isChecked()) {
                                    jsonArray.put(checkBox.getText().toString().trim());
                                    jsonObject.put("check_box", jsonArray);
                                    if(selectedOptions.equals("")){
                                        selectedOptions=Integer.toString(checkBox.getId());
                                    }else{
                                        selectedOptions=selectedOptions+","+Integer.toString(checkBox.getId());
                                    }
                                }
                                AnswerModel answerModel= new AnswerModel();
                                answerModel.setOption_id(selectedOptions);
                                answerModel.setOption_value("");
                                answerModel.setSurveyID(survey_id);
                                answerModel.setQuestionID(jsonArrayQuestions.getJSONObject(startPositionBefore).getString("question_id"));
                                answerModelList.add(answerModel);
                                startPositionBefore++;
                            }*/
                            else{
                                childView.getRootView();
                            }

                        /*JSONObject merged = new JSONObject();
                        JSONObject[] objects = new JSONObject[]
                                {jsonObjectET, jsonObjectRG, jsonObjectSPN, jsonObjectCHK};
                        for (JSONObject obj : objects) {
                            Iterator it = obj.keys();
                            while (it.hasNext()) {
                                String key = (String)it.next();
                                try {
                                    merged.put(key, obj.get(key));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        Log.e(TAG, "onNextClick- "+merged.toString());*/

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        String json = jsonObject.toString();

                   /* Intent intentSurveyActivity1=new Intent(context, LoginActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intentSurveyActivity1.putExtra("count", count);
                    intentSurveyActivity1.putExtra("survey_id", survey_id);
                    intentSurveyActivity1.putExtra("json", json);
                    startActivity(intentSurveyActivity1);
                    finish();*/

                    }
                    //startPosition=startPosition+1;
                if(buttonText.equals("Submit")){
                    Toast.makeText(getApplicationContext(),"Thank you participation",Toast.LENGTH_LONG).show();
                    //save data in to local DB.
                    Gson gson = new Gson();
                    String listString = gson.toJson(
                            answerModelList,
                            new TypeToken<ArrayList<AnswerModel>>() {}.getType());
                    try {
                        JSONArray json_array =  new JSONArray(listString);
                        JSONObject json_object=new JSONObject();
                        json_object.put("survey_data", json_array);
                        Log.e(TAG, "onClick: "+json_object.toString());

                        if (screen_type.equals("survey_list")) {
                            sqliteHelper.updateSurveyDataInTable("survey", "survey_id", survey_id, json_object);
                        } else {
                            sqliteHelper.saveSurveyDataInTable(json_object, survey_id);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Intent intentSurveyActivity1=new Intent(context, ClusterDetails.class);
                    startActivity(intentSurveyActivity1);
                    finish();
                }
                else {
                    //back_status=false;
                    sharedPrefHelper.setInt("startPosition", startPosition);
                    endPosition = endPosition + length;
                    if (endPosition <= totalQuestions) {
                        btn_next.setText("Next");
                        sharedPrefHelper.setInt("endPosition", endPosition);
                    }
                    else {
                        endPosition = totalQuestions;
                        btn_next.setText("Submit");
                        back_status=false;
                        sharedPrefHelper.setInt("endPosition", totalQuestions);
                    }
                    Log.e(TAG, "Position >>> endPosition >>>" + endPosition + "startPosition >>>" + startPosition);
                    questionsPopulate();
                    Log.e(TAG, "onNextClick- " + jsonObject.toString());
                }
            }
        });
        btn_previous.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                if(endPosition==totalQuestions){
                    startPosition=startPosition-1;
                }else{
                    startPosition=startPosition-length;
                }
                endPosition=startPosition;
                startPosition=endPosition-length;
                //endPosition = endPosition + length;
                sharedPrefHelper.setInt("endPosition", endPosition);
                Log.e(TAG, "Position >>> endPosition >>>" + endPosition + "startPosition >>>" + startPosition);
                back_status=true;
                if(endPosition<=0){
                Intent intentHom= new Intent(SurveyActivity.this,ClusterDetails.class);
                startActivity(intentHom);
                finish();
                }else{
                    btn_next.setText("Next");
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
           for(int l=startScreenPosition;l<endScreenPosition;l++){
               for (int i = startPosition; i < endPosition; i++) {
                   JSONObject jsonObjectQuesType=jsonArrayQuestions.getJSONObject(i);
                   if (jsonObjectQuesType.getString("question_type").equals("1")) {
                       TextView txtLabel = new TextView(this);
                       EditText editText=new EditText(this);
                       editText.setId(Integer.parseInt(jsonObjectQuesType.getString("question_id")));
                       if(jsonObjectQuesType.getString("question_input_type").equals("2")){
                           editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
                       }else{
                           editText.setInputType(InputType.TYPE_CLASS_TEXT);
                       }
                       editText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(Integer.parseInt(jsonObjectQuesType.getString("max_limit")))});
                       if((back_status==true || screen_type.equals("survey_list")) && answerModelList.size()>i){
                           editText.setText(answerModelList.get(i).getOption_value());
                       }
                       txtLabel.setText(jsonObjectQuesType.getString("question_name"));
                       ll_parent.addView(txtLabel);
                       ll_parent.addView(editText);

                       //onAddEditField(jsonObjectQuesType);

                   }
                   else if (jsonObjectQuesType.getString("question_type").equals("2")) {
                       TextView txtLabel = new TextView(this);
                       txtLabel.setText(jsonObjectQuesType.getString("question_name"));
                       ll_parent.addView(txtLabel);
                       JSONArray jsonArrayOptions = jsonObjectQuesType.getJSONArray("question_options");
                       RadioGroup radioGroup=new RadioGroup(this);
                       for (int j = 0; j <jsonArrayOptions.length() ; j++) {
                           RadioButton radioButton=new RadioButton(this);
                           radioButton.setLayoutParams(new LinearLayout.LayoutParams
                                   (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                           JSONObject jsonObjectOptionValues=jsonArrayOptions.getJSONObject(j);
                           radioButton.setText(jsonObjectOptionValues.getString("option_value"));
                           radioButton.setId(Integer.parseInt(jsonObjectOptionValues.getString("option_id")));
                           if((back_status==true || screen_type.equals("survey_list")) && answerModelList.size()>i){
                               if(answerModelList.get(i).getOption_id().equals(jsonObjectOptionValues.getString("option_id"))){
                                   radioButton.setChecked(true);
                               }
                           }
                           if (radioGroup != null) {
                               radioGroup.addView(radioButton);
                           }
                       }
                       ll_parent.addView(radioGroup);
                       //onAddRadioButton(jsonObjectQuesType);
                   }
                   else if (jsonObjectQuesType.getString("question_type").equals("3")) {
                       TextView txtLabel = new TextView(this);
                       txtLabel.setText(jsonObjectQuesType.getString("question_name"));
                       ll_parent.addView(txtLabel);
                       JSONArray jsonArrayOptions = jsonObjectQuesType.getJSONArray("question_options");
                       TableLayout linearLayoutCheckbox= new TableLayout(this);
                       linearLayoutCheckbox.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                       String selectedOptions="";
                       if((back_status==true || screen_type.equals("survey_list")) && answerModelList.size()>i){
                           selectedOptions=answerModelList.get(i).getOption_id();
                           String[] arraySelectedOptions = selectedOptions.split(",");
                       }
                       for (int j = 0; j <jsonArrayOptions.length() ; j++) {
                           TableRow row =new TableRow(this);
                           row.setId(j);
                           row.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                           JSONObject jsonObject1=jsonArrayOptions.getJSONObject(j);
                           CheckBox checkBox=new CheckBox(this);
                           checkBox.setText(jsonObject1.getString("option_value"));
                           checkBox.setId(Integer.parseInt(jsonObject1.getString("option_id")));
                           if((back_status==true || screen_type.equals("survey_list")) && answerModelList.size()>i){
                               selectedOptions=answerModelList.get(i).getOption_id();
                               String[] arraySelectedOptions = selectedOptions.split(",");
                               boolean contains = Arrays.asList(arraySelectedOptions).contains(jsonObject1.getString("option_id"));
                               if(contains){
                                   checkBox.setChecked(true);
                               }
                           }
                           row.addView(checkBox);
                           linearLayoutCheckbox.addView(row);
                       }
                       ll_parent.addView(linearLayoutCheckbox);

                       //   onAddCheckBox(jsonObjectQuesType);
                   }
                   else if (jsonObjectQuesType.getString("question_type").equals("4")) {
                       TextView txtLabel = new TextView(this);
                       txtLabel.setText(jsonObjectQuesType.getString("question_name"));
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
                           if((back_status==true || screen_type.equals("survey_list")) && answerModelList.size()>i){
                               spinner.setSelection(Integer.parseInt(answerModelList.get(i).getOption_id()));
                           }
                       }
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
                   }
                   startPosition++;
               }
           }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) finish();
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
                        Intent intentTerminate=new Intent(context, TerminateActivity.class);
                        intentTerminate.putExtra("screen_type", "terminate");
                        startActivity(intentTerminate);
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
}
