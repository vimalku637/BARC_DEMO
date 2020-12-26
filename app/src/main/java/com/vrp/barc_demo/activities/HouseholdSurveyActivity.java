/*
 * Copyright (c)  2020. Indev Consultancy Private Limited,
 * Auther : Vimal Kumar
 * Date : 2020/12/15
 * Modified Date :
 * Modified By :
 */

package com.vrp.barc_demo.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.vrp.barc_demo.R;
import com.vrp.barc_demo.fragments.GroupRelationFragment;
import com.vrp.barc_demo.fragments.GroupTVFragment;
import com.vrp.barc_demo.location_gps.AppConstants;
import com.vrp.barc_demo.location_gps.GpsUtils;
import com.vrp.barc_demo.models.AnswerModel;
import com.vrp.barc_demo.models.ScreenWiseQuestionModel;
import com.vrp.barc_demo.rest_api.ApiClient;
import com.vrp.barc_demo.rest_api.BARC_API;
import com.vrp.barc_demo.sqlite_db.SqliteHelper;
import com.vrp.barc_demo.utils.ActivityCommunicator;
import com.vrp.barc_demo.utils.AlertDialogClass;
import com.vrp.barc_demo.utils.CommonClass;
import com.vrp.barc_demo.utils.FragmentCommunicator;
import com.vrp.barc_demo.utils.LimitTextWatcher;
import com.vrp.barc_demo.utils.MyJSON;
import com.vrp.barc_demo.utils.SharedPrefHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

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
    @BindView(R.id.iv_recording)
    ShapeableImageView iv_recording;

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
    int tvTotalGroup;
    int familyTotalGroup;
    int totalScreen;
    JSONObject jsonQuestions = null;
    JSONArray jsonArrayQuestions=null;
    JSONArray jsonArrayScreen=null;
    JSONArray jsonArrayScreenbackup=null;
    public static ArrayList<AnswerModel> answerModelList;
    public static ArrayList<AnswerModel> answerModelHouseholdMemberList;
    public static ArrayList<ArrayList<AnswerModel>> answerModelHouseholdMemberListTotal;
    public static ArrayList<AnswerModel> answerModelTVList;
    public static ArrayList<ArrayList<AnswerModel>> answerModelTVListTotal;
    public FragmentCommunicator fragmentCommunicator;
    ArrayList<ScreenWiseQuestionModel> arrayScreenWiseQuestionModel= new ArrayList<>();
    String screen_id=null;
    boolean back_status=true;
    private SqliteHelper sqliteHelper;
    private String surveyObjectJSON=null;
    private String editFieldValues="";
    private int groupQuestionID=0;
    //to start record audio
    private boolean isRecording;
    public static MediaRecorder mediaRecorder;
    public static AudioManager audioManager;
    public static final int RequestPermissionCode = 1;
    public static String AudioSavePathInDevice = null;
    //initialization MediaPlayer
    MediaPlayer mediaPlayer=new MediaPlayer();
    private String name="";
    private int ageInYears=0;
    private String mobileNo="",reMobileNo;
    private String sixDigitCode="",pinCode="";
    boolean isGPS=false;
    int isGPSClicked=0;

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
                ArrayList<AnswerModel> modelArrayList=new ArrayList<>();
                answerModelHouseholdMemberList = (ArrayList<AnswerModel>) getIntent().getSerializableExtra("answerModelListFamily");
                // answerModelHouseholdMemberListTotal.add(modelArrayList);
                ArrayList<AnswerModel> modelArrayList1=new ArrayList<>();
                answerModelTVList = (ArrayList<AnswerModel>) getIntent().getSerializableExtra("answerModelListTV");
                // answerModelTVListTotal.add(modelArrayList1);
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
                    jsonArrayScreen=new JSONArray();
                    jsonArrayScreen = jsonQuestions.getJSONArray("group").getJSONObject(0).getJSONArray("screens");
                    //jsonArrayScreenbackup=jsonQuestions.getJSONArray("group").getJSONObject(0).getJSONArray("screens");
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
                /*Gson gson = new Gson();
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
                }*/
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
        answerModelHouseholdMemberListTotal=new ArrayList<>();
        answerModelTVListTotal=new ArrayList<>();
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
                                String questionID=jsonArrayQuestions.getJSONObject(count).getString("question_id");
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
                                if (jsonArrayQuestions.getJSONObject(count).getString("validation_id").equals("1")
                                        &&questionID.equals("94")&&editText.getText().toString().trim().equals("")){
                                    flag=true;

                                } else {
                                    if(jsonArrayQuestions.getJSONObject(count).getString("validation_id").equals("1") && editText.getText().toString().trim().equals("")){
                                        flag=false;
                                        break;
                                    }else if (questionID.equals("46")) {
                                        name=editText.getText().toString().trim();
                                        sharedPrefHelper.setString("name", name);
                                    }else if (questionID.equals("47")) {
                                        ageInYears=Integer.parseInt(editText.getText().toString().trim());
                                        sharedPrefHelper.setInt("ageInYears", ageInYears);
                                        if(ageInYears<1){
                                            flag=false;
                                            break;
                                        }
                                        else if(ageInYears>120){
                                            flag=false;
                                            break;
                                        }
                                    }
                                    else if(questionID.equals("86")){
                                        sixDigitCode=editText.getText().toString().trim();
                                        sharedPrefHelper.setString("sixDigitCode", sixDigitCode);
                                        if (sharedPrefHelper.getString("sixDigitCode","").length()<6){
                                            flag=false;
                                            break;
                                        }
                                    }
                                    else if (questionID.equals("99")){
                                        pinCode=editText.getText().toString().trim();
                                        sharedPrefHelper.setString("pinCode", pinCode);
                                        if (sharedPrefHelper.getString("pinCode","").length()<6){
                                            flag=false;
                                            break;
                                        }
                                    }
                                    else if(questionID.equals("88")){
                                        mobileNo=editText.getText().toString().trim();
                                        sharedPrefHelper.setString("mobile_no", mobileNo);
                                        Pattern ps = Pattern.compile("^[6-9][0-9]{9}+$");
                                        Matcher ms = ps.matcher(mobileNo);
                                        boolean bs = ms.matches();
                                        if (sharedPrefHelper.getString("mobile_no", "").length()<10){
                                            flag=false;
                                            break;
                                        }else if (!bs) {
                                            flag=false;
                                            break;
                                        }
                                    }
                                    else if(questionID.equals("108")){
                                        reMobileNo=editText.getText().toString().trim();
                                        sharedPrefHelper.setString("confirm_mobile_no", reMobileNo);
                                        Pattern ps = Pattern.compile("^[6-9][0-9]{9}+$");
                                        Matcher ms = ps.matcher(reMobileNo);
                                        boolean bs = ms.matches();
                                        if (sharedPrefHelper.getString("confirm_mobile_no", "").length()<10){
                                            flag=false;
                                            break;
                                        }
                                       else if (!sharedPrefHelper.getString("confirm_mobile_no", "").equals(mobileNo)){
                                            flag=false;
                                            break;
                                        }
                                        else if (!bs) {
                                            flag=false;
                                            break;
                                        }
                                    }
                                    else if (questionID.equals("57")){
                                        String TvWorkingCondition=editText.getText().toString().trim();
                                        if(Integer.parseInt(TvWorkingCondition)>10 || Integer.parseInt(TvWorkingCondition)==0){
                                            flag=false;
                                            break;
                                        }else{
                                            sharedPrefHelper.setString("TvWorkingCondition", TvWorkingCondition);
                                        }
                                    }
                                    else if (jsonArrayQuestions.getJSONObject(count).getString("question_id").equals("31") ){
                                        if(Integer.parseInt(editText.getText().toString().trim())>20 || Integer.parseInt(editText.getText().toString().trim())==0){
                                            flag=false;
                                            break;
                                        }
                                    }

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
                                if(jsonArrayQuestions.getJSONObject(count).getString("question_id").equals("71")){
                                    sharedPrefHelper.setString("stayWith",""+radioID);
                                }else if(jsonArrayQuestions.getJSONObject(count).getString("question_id").equals("83")){
                                    sharedPrefHelper.setString("accessInternetOnMobile",""+radioID);
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
                                    if(jsonArrayQuestions.getJSONObject(count).getString("question_id").equals("53")) {
                                        String selectedOptionsSpinner = sharedPrefHelper.getString("CWE_Status", "");
                                        if (selectedOptionsSpinner.equals("1")) {
                                            flag=true;
                                        }else{
                                            flag=false;
                                            break;
                                        }
                                    }else if(jsonArrayQuestions.getJSONObject(count).getString("question_id").equals("82")){
                                        String selectedOptions=sharedPrefHelper.getString("type_of_mobile","");
                                        if (selectedOptions.equals("1")){
                                            flag=true;
                                        }else if(selectedOptions.equals("4")){
                                            flag=true;
                                        }else if(selectedOptions.equals("1,4")){
                                            flag=true;
                                        }
                                        else{
                                            flag=false;
                                            break;
                                        }
                                    }
                                    else{
                                        flag=false;
                                        break;
                                    }
                                }
                                nextPosition++;
                                count++;
                            }
                            else if (childView instanceof Spinner) {
                                Spinner spinner = (Spinner) childView;
                                if(jsonArrayQuestions.getJSONObject(count).getString("question_id").equals("75")){
                                    sharedPrefHelper.setString("tenement_type",""+spinner.getSelectedItemId());
                                }else if(jsonArrayQuestions.getJSONObject(count).getString("question_id").equals("84")){
                                    sharedPrefHelper.setString("last_time_access_internet",""+spinner.getSelectedItemId());
                                }
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
                                    if(jsonArrayQuestions.getJSONObject(count).getString("question_id").equals("50") || jsonArrayQuestions.getJSONObject(count).getString("question_id").equals("51") || jsonArrayQuestions.getJSONObject(count).getString("question_id").equals("52")) {
                                        String selectedOptionsSpinner = sharedPrefHelper.getString("CWE_Status", "");
                                        if (selectedOptionsSpinner.equals("1")) {
                                            flag=true;
                                        }else{
                                            flag=false;
                                            break;
                                        }
                                    }else if(jsonArrayQuestions.getJSONObject(count).getString("question_id").equals("72")) {
                                        String stayWith = sharedPrefHelper.getString("stayWith", "");
                                        if(!stayWith.equals("1")){
                                            flag=true;
                                        }else{
                                            flag=false;
                                            break;
                                        }
                                    }
                                    else if(jsonArrayQuestions.getJSONObject(count).getString("question_id").equals("73")) {
                                        String stayWith = sharedPrefHelper.getString("stayWith", "");
                                        if(!stayWith.equals("2")){
                                            flag=true;
                                        }else{
                                            flag=false;
                                            break;
                                        }
                                    }
                                    else if(jsonArrayQuestions.getJSONObject(count).getString("question_id").equals("75")) {
                                        String town_village_class=sharedPrefHelper.getString("town_village_class","");
                                        if(!town_village_class.equals("Urban")){
                                            flag=true;
                                        }else{
                                            flag=false;
                                            break;
                                        }
                                    }
                                    else if(jsonArrayQuestions.getJSONObject(count).getString("question_id").equals("84")){
                                        String accessInternetOnMobile=sharedPrefHelper.getString("accessInternetOnMobile","");
                                        if (!accessInternetOnMobile.equals("1")){
                                            flag=true;
                                        }else{
                                            flag=false;
                                            break;
                                        }
                                    }else if(jsonArrayQuestions.getJSONObject(count).getString("question_id").equals("85")){
                                        String lastTimeAccessInternet=sharedPrefHelper.getString("last_time_access_internet","");
                                        if (lastTimeAccessInternet.equals("9")){
                                            flag=true;
                                        }else{
                                            flag=false;
                                            break;
                                        }
                                    }
                                    else{
                                        flag=false;
                                        break;
                                    }
                                }
                                else if(jsonArrayQuestions.getJSONObject(count).getString("question_id").equals("77")){
                                    int TvWorkingCondition=Integer.parseInt(sharedPrefHelper.getString("TvWorkingCondition", ""));
                                    String spinnerIds=Long.toString(spinner.getSelectedItemId());
                                    if (spinner.getSelectedItemId()<TvWorkingCondition && spinner.getSelectedItemId()<5){
                                        flag=false;
                                        break;
                                    }else{
                                        flag=true;
                                    }
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
                                if(jsonArrayQuestions.getJSONObject(count).getString("question_id").equals("29")){
                                    if(selectedOptions.contains("4"))
                                    sharedPrefHelper.setString("selectedDurables",selectedOptions);
                                    else
                                        setTerminattion("Durables");

                                }
                                else if(jsonArrayQuestions.getJSONObject(count).getString("question_id").equals("78")){
                                    sharedPrefHelper.setString("family_language",""+selectedOptions);
                                }
                                else if (jsonArrayQuestions.getJSONObject(count).getString("question_id").equals("81")){
                                    sharedPrefHelper.setString("type_of_mobile",""+selectedOptions);
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
                                   if(jsonArrayQuestions.getJSONObject(count).getString("question_id").equals("55")) {
                                       String selectedOptionss=sharedPrefHelper.getString("selectedDurables","");
                                       String[] arraySelectedOptions = selectedOptionss.split(",");
                                       boolean contains = Arrays.asList(arraySelectedOptions).contains("9");
                                       if(contains){
                                           flag=true;
                                       } else{
                                            flag=false;
                                            break;
                                        }
                                    }else if(jsonArrayQuestions.getJSONObject(count).getString("question_id").equals("70")) {
                                       String town_village_class=sharedPrefHelper.getString("town_village_class","");
                                       if(!town_village_class.equals("Rural")){
                                           flag=true;
                                       } else{
                                           flag=false;
                                           break;
                                       }
                                   }
                                   else{
                                    flag=false;
                                    break; }
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
                    stopRecording();
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
                        JSONArray json_array =  new JSONArray(listString);
                        JSONArray json_array_family =  new JSONArray(listStringFamily);
                        JSONArray json_array_TV =  new JSONArray(listStringTV);
                        JSONObject json_object=new JSONObject();
                        json_object.put("user_id", sharedPrefHelper.getString("user_id", ""));
                        json_object.put("survey_id", survey_id);
                        json_object.put("cluster_no", sharedPrefHelper.getString("cluster_no", ""));
                        json_object.put("census_district_code", sharedPrefHelper.getString("census_district_code", ""));
                        if (AudioSavePathInDevice!=null) {
                            json_object.put("audio_recording", AudioSavePathInDevice);
                        }
                        json_object.put("GPS_latitude", sharedPrefHelper.getString("LAT", ""));
                        json_object.put("GPS_longitude", sharedPrefHelper.getString("LONG", ""));
                        json_object.put("survey_data", json_array);
                        json_object.put("family_data", json_array_family);
                        json_object.put("tv_data", json_array_TV);
                        json_object.put("date_time", sharedPrefHelper.getString("dateTime", ""));
                        Log.e(TAG, "onClick: "+json_object.toString());

                        if (screen_type.equals("survey_list")) {
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
                                Toast.makeText(context, getResources().getString(R.string.no_internet_data_saved_locally), Toast.LENGTH_SHORT).show();
                                startActivity(intentSurveyActivity1);
                                finish();
                            }
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
                                Toast.makeText(context, getResources().getString(R.string.no_internet_data_saved_locally), Toast.LENGTH_SHORT).show();
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
                    checkGPSEnable();
                    if(flag==true && isGPS==true && isGPSClicked!=1){
                        sharedPrefHelper.setInt("startPosition", startPosition);
                        endPosition = endPosition + length;
                        if (endScreenPosition<totalScreen-1) {
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
                                    json_object.put("GPS_latitude", sharedPrefHelper.getString("LAT", ""));
                                    json_object.put("GPS_longitude", sharedPrefHelper.getString("LONG", ""));
                                    json_object.put("survey_data", json_array);
                                    Log.e(TAG, "onClick: "+json_object.toString());

                                    if (screen_type.equals("survey_list")){
                                        //update data in to local DB
                                        sqliteHelper.updateSurveyDataInTable("survey", "survey_id", survey_id, json_object);
                                        sqliteHelper.updateLocalFlag("partial", "survey",
                                                Integer.parseInt(sharedPrefHelper.getString("survey_id", "")), 1);
                                    } else {
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
                            //for(int m=0;m<jsonArrayQuestions.length();m++){
                                groupRelationId = jsonArrayQuestions.getJSONObject(jsonArrayQuestions.length()-1).getString("group_relation_id");
                           // }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (groupRelationId!=null&&!groupRelationId.equalsIgnoreCase("0")) {
                           if (groupRelationId.equalsIgnoreCase("1")) {
                               familyTotalGroup=Integer.parseInt(editFieldValues);
                                Bundle bundle=new Bundle();
                                bundle.putInt("editFieldValues", Integer.parseInt(editFieldValues));
                                bundle.putInt("startScreenPosition", startScreenPosition);
                                bundle.putInt("endScreenPosition", endScreenPosition);
                                bundle.putInt("groupRelationId", Integer.parseInt(groupRelationId));
                                bundle.putInt("questionID", groupQuestionID);
                                bundle.putString("screen_type", screen_type);
                                Fragment fragment = new GroupRelationFragment();
                                fragment.setArguments(bundle);
                                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                                transaction.replace(R.id.group_relation_fragment, fragment); // fragment container id in first parameter is the  container(Main layout id) of Activity
                                transaction.addToBackStack(null);  // this will manage backstack
                                transaction.commit();
                            }
                            if (groupRelationId.equalsIgnoreCase("2")) {
                                Bundle bundle=new Bundle();
                                tvTotalGroup=Integer.parseInt(editFieldValues);
                                bundle.putInt("editFieldValues", Integer.parseInt(editFieldValues));
                                bundle.putInt("startScreenPosition", startScreenPosition);
                                bundle.putInt("endScreenPosition", endScreenPosition);
                                bundle.putInt("groupRelationId", Integer.parseInt(groupRelationId));
                                bundle.putInt("questionID", groupQuestionID);
                                bundle.putString("screen_type", screen_type);
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
                        if(isGPSClicked==1)
                            Toast.makeText(context,"Please clicked on GPS location button",Toast.LENGTH_LONG).show();
                        else if(isGPS==true)
                        Toast.makeText(context,"Please fill all required correct fields/values",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        btn_previous.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                startScreenPosition=startScreenPosition-1;
                endScreenPosition=endScreenPosition-1;
                back_status=true;
                if(endScreenPosition<=0){
                Intent intentHom= new Intent(HouseholdSurveyActivity.this, ClusterDetails.class);
                startActivity(intentHom);
                finish();
                }else{
                    isGPSClicked=0;
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
                   String questionID=jsonObjectQuesType.getString("question_id");
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
                          /* boolean status=sqliteHelper.getNCCMatrix(answerModelList.get(startPosition-2).getOption_id(),answerModelList.get(startPosition-1).getOption_id(),sharedPrefHelper.getString("nccs_matrix", ""));
                           if(status)
                           editText.setText(sharedPrefHelper.getString("nccs_matrix", ""));
                           else
                               setTerminattion("NCCS Calculator");
                      */ }
                       if(jsonObjectQuesType.getString("question_input_type").equals("2")){
                           editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
                       }else{
                           editText.setInputType(InputType.TYPE_CLASS_TEXT);
                       }
                       if (jsonObjectQuesType.getString("question_id").equals("46")) {
                           int maxLength=50;
                           editText.addTextChangedListener(new LimitTextWatcher(editText, maxLength, new LimitTextWatcher.IF_callback() {
                               @Override
                               public void callback(int left) {
                                   if(left <= 0) {
                                       Toast.makeText(context, "Please enter correct value", Toast.LENGTH_SHORT).show();
                                   }
                               }
                           }));
                       }
                       if (jsonObjectQuesType.getString("question_id").equals("47")) {
                           int maxLength=3;
                           editText.addTextChangedListener(new LimitTextWatcher(editText, maxLength, new LimitTextWatcher.IF_callback() {
                               @Override
                               public void callback(int left) {
                                   if(left <= 0) {
                                       Toast.makeText(context, "Please enter correct value", Toast.LENGTH_SHORT).show();
                                   }
                               }
                           }));
                       }
                       if (questionID.equals("32") || questionID.equals("57")) {
                           editText.addTextChangedListener(new TextWatcher() {
                               @Override
                               public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                               }

                               @Override
                               public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                   int limit=0;
                                   if(editText.getText().toString().trim().length()>0) {
                                       int value = Integer.parseInt(editText.getText().toString().trim());
                                       if (questionID.equals("32")) {
                                           limit = 20;
                                           if (!editText.getText().toString().trim().equals("")) {
                                               if (value > limit) {
                                                   //Toast.makeText(getActivity(), "Are you sure age is greater then 99.", Toast.LENGTH_SHORT).show();
                                                   Toast.makeText(context, "Household member can't be greater than 20", Toast.LENGTH_SHORT).show();
                                               } else if (value == 0) {
                                                   Toast.makeText(context, "Household member can't be 0", Toast.LENGTH_SHORT).show();
                                               }
                                           }
                                       } else {
                                           limit = 10;
                                           if (!editText.getText().toString().trim().equals("")) {
                                               if (value > limit) {
                                                   //Toast.makeText(getActivity(), "Are you sure age is greater then 99.", Toast.LENGTH_SHORT).show();
                                                   Toast.makeText(context, "No. of TV can't be greater then 10", Toast.LENGTH_SHORT).show();
                                               }
                                               else if (value > 5) {
                                                   openDialogForAgeConfirmation(editText, "Are you sure TV more than 5");
                                               } else if (value == 0) {
                                                   Toast.makeText(context, "No. of TV can't be 0", Toast.LENGTH_SHORT).show();
                                               }
                                           }
                                       }
                                   }
                               }

                               @Override
                               public void afterTextChanged(Editable editable) {

                               }
                           });
                       }
                       if (jsonObjectQuesType.getString("question_id").equals("47")) {
                           editText.addTextChangedListener(new TextWatcher() {
                               @Override
                               public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                               }

                               @Override
                               public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                   String age=editText.getText().toString().trim();
                                   if (!age.equals("")) {
                                       int ageInYear=Integer.parseInt(age);
                                       if (ageInYear>99){
                                           //Toast.makeText(getActivity(), "Are you sure age is greater then 99.", Toast.LENGTH_SHORT).show();
                                           openDialogForAgeConfirmation(editText,"Are you sure age is greater then 99");
                                       } else {
                                       }
                                   }
                               }

                               @Override
                               public void afterTextChanged(Editable editable) {

                               }
                           });
                       }
                       if (jsonObjectQuesType.getString("question_id").equals("88")){
                           int maxLength=10;
                           editText.addTextChangedListener(new LimitTextWatcher(editText, maxLength, new LimitTextWatcher.IF_callback() {
                               @Override
                               public void callback(int left) {
                                   if(left <= 0) {
                                       Toast.makeText(context, "Please enter correct value", Toast.LENGTH_SHORT).show();
                                   }
                               }
                           }));
                       }
                       if (jsonObjectQuesType.getString("question_id").equals("108")){
                           int maxLength=10;
                           editText.addTextChangedListener(new LimitTextWatcher(editText, maxLength, new LimitTextWatcher.IF_callback() {
                               @Override
                               public void callback(int left) {
                                   if(left <= 0) {
                                       Toast.makeText(context, "Please enter correct value", Toast.LENGTH_SHORT).show();
                                   }
                               }
                           }));
                       }
                       if (jsonObjectQuesType.getString("question_id").equals("94")){
                           String town_village_class=sharedPrefHelper.getString("town_village_class","");
                           if(!town_village_class.equals("Rural")){
                               txtLabel.setVisibility(View.GONE);
                               editText.setVisibility(View.GONE);
                           }
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
                       description=description.replaceAll("\\$name",sharedPrefHelper.getString("name",""));
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
                       radioGroup.setId(Integer.parseInt(jsonObjectQuesType.getString("question_id")));
                       if(questionID.equals("53")){
                           String selectedOptionsSpinner=sharedPrefHelper.getString("CWE_Status","");
                           if(selectedOptionsSpinner.equals("1")){
                               radioGroup.setVisibility(View.GONE);
                               txtLabel.setVisibility(View.GONE);
                           }
                       }
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
                           }
                           if(jsonObjectQuesType.getString("pre_field").equals("1")){
                               radioButton.setEnabled(false);
                           }
                           else if(jsonObjectQuesType.getString("question_id").equals("22") && jsonObjectOptionValues.getString("option_id").equals(sharedPrefHelper.getString("address_type", "0"))){
                               radioButton.setChecked(true);
                           }
                           else if(jsonObjectQuesType.getString("question_id").equals("76")){
                               String tenement_type=sharedPrefHelper.getString("tenement_type","");
                               if(tenement_type.equals("1") && jsonObjectOptionValues.getString("option_id").equals("1")){
                                   radioButton.setChecked(true);
                               }
                           }
                           else if(jsonObjectQuesType.getString("question_id").equals("82")){
                               String type_of_mobile=sharedPrefHelper.getString("type_of_mobile","");
                               if (type_of_mobile.equals("1")){
                                   txtLabel.setVisibility(View.GONE);
                                   radioButton.setVisibility(View.GONE);
                               }if (type_of_mobile.equals("4")){
                                   txtLabel.setVisibility(View.GONE);
                                   radioButton.setVisibility(View.GONE);
                               }if(type_of_mobile.equals("1,4")){
                                   txtLabel.setVisibility(View.GONE);
                                   radioButton.setVisibility(View.GONE);
                               }
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
                               int sepPosID = rbTag.indexOf("^");
                               String radioID = rbTag.substring(0, sepPosID);
                               int iddd=group.getId();
                               if (rb.isChecked()){
                                   if(id.equals("1")){
                                       //setTerminattion(id);
                                       showPopupForTerminateSurveyOnRadio(id,jsonObjectQuesType,rb);
                                       //Toast.makeText(context,"Termination true"+rb.getText()+"group.getId()"+group.getId(),Toast.LENGTH_LONG).show();
                                   }
                               }
                               else if(group.getId()==23){
                                    sharedPrefHelper.setString("CWE_Status",radioID);
                                    if(radioID.equalsIgnoreCase("1")){
                                        /*jsonArrayScreen.remove(11);
                                        jsonArrayScreen.remove(11);*/
                                        try{
                                            JSONArray list = new JSONArray();
                                        int len = jsonArrayScreen.length();
                                        if (jsonArrayScreen != null) {
                                            for (int i=0;i<len;i++)
                                            {
                                                if (i != 11 && i != 12)
                                                {
                                                    list.put(jsonArrayScreen.get(i));
                                                }
                                            }
                                        }
                                        jsonArrayScreen=list;
                                        totalScreen=jsonArrayScreen.length();
                                    } catch (JSONException e) {
                                       e.printStackTrace();
                                   }
                                    }else{
                                        try {
                                            //jsonArrayScreen=new JSONArray();
                                            jsonArrayScreen=jsonQuestions.getJSONArray("group").getJSONObject(0).getJSONArray("screens");
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        totalScreen=jsonArrayScreen.length();
                                    }
                               }
                               else if(iddd==24){
                                   //start recording here
                                   if (radioID.equals("1")) {
                                       sharedPrefHelper.setBoolean("isRecording",true);
                                       iv_recording.setVisibility(View.VISIBLE);
                                       startRecordingAnimation(1);
                                       startRecording();
                                   }else{
                                       iv_recording.setVisibility(View.GONE);
                                       startRecordingAnimation(2);
                                       //iv_recording.startAnimation(null);
                                       sharedPrefHelper.setBoolean("isRecording",false);
                                       stopRecording();
                                   }
                               }
                           }
                       });
                       //onAddRadioButton(jsonObjectQuesType);
                   }
                   else if (jsonObjectQuesType.getString("question_type").equals("3")) {
                       TextView txtLabel = new TextView(this);
                       String description=jsonObjectQuesType.getString("question_name");
                       description=description.replaceAll("$name",sharedPrefHelper.getString("name",""));
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
                       if(questionID.equals("55")){
                           selectedOptions=sharedPrefHelper.getString("selectedDurables","");
                           String[] arraySelectedOptions = selectedOptions.split(",");
                           boolean contains = Arrays.asList(arraySelectedOptions).contains("9");
                           if(contains){
                               txtLabel.setVisibility(View.GONE);
                               linearLayoutCheckbox.setVisibility(View.GONE);
                           }
                       }
                       if(questionID.equals("70")){
                           String town_village_class=sharedPrefHelper.getString("town_village_class","");
                           if(!town_village_class.equals("Rural")){
                               txtLabel.setVisibility(View.GONE);
                               linearLayoutCheckbox.setVisibility(View.GONE);
                           }
                       }
                       for (int j = 0; j <jsonArrayOptions.length(); j++) {
                           TableRow row =new TableRow(this);
                           row.setId(j);
                           row.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                           JSONObject jsonObject1=jsonArrayOptions.getJSONObject(j);
                           CheckBox checkBox=new CheckBox(this);
                           checkBox.setText(jsonObject1.getString("option_value"));
                           checkBox.setTextSize(12);
                           checkBox.setId(Integer.parseInt(jsonObject1.getString("option_id")));
                           if(questionID.equals("54")){
                               checkBox.setEnabled(false);
                           }

                           if((back_status==true || screen_type.equals("survey_list")) && answerModelList.size()>startPosition){
                               selectedOptions=answerModelList.get(startPosition).getOption_id();
                               String[] arraySelectedOptions = selectedOptions.split(",");
                               boolean contains = Arrays.asList(arraySelectedOptions).contains(jsonObject1.getString("option_id"));
                               if(contains){
                                   checkBox.setChecked(true);
                               }
                           }
                           else if((questionID.equals("54"))){
                               selectedOptions=sharedPrefHelper.getString("selectedDurables","");
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
                       description=description.replaceAll("$name",sharedPrefHelper.getString("name",""));
                       txtLabel.setText(description);
                       txtLabel.setTextSize(14);
                       txtLabel.setTypeface(null, Typeface.BOLD);
                       ll_parent.addView(txtLabel);
                       JSONArray jsonArrayOptions = jsonObjectQuesType.getJSONArray("question_options");
                       Spinner spinner=new Spinner(this);
                       ArrayList<String> spinnerAL=new ArrayList<>();
                       String selectedOptionsSpinner="";
                       String[] arrayselectedOptionsSpinner=null;
                       if((questionID.equals("79"))){
                           selectedOptionsSpinner=sharedPrefHelper.getString("family_language","");
                           arrayselectedOptionsSpinner = selectedOptionsSpinner.split(",");

                       }
                       if(questionID.equals("50") || questionID.equals("51") || questionID.equals("52")){
                           selectedOptionsSpinner=sharedPrefHelper.getString("CWE_Status","");
                           if(selectedOptionsSpinner.equals("1")){
                             spinner.setVisibility(View.GONE);
                             txtLabel.setVisibility(View.GONE);
                           }
                       }
                       for (int j = 0; j <jsonArrayOptions.length() ; j++) {
                           spinnerAL.clear();
                           for (int k = 0; k < jsonArrayOptions.length(); k++) {
                               JSONObject jsonObjectOptionValues=jsonArrayOptions.getJSONObject(k);
                               String spinnerOption=jsonObjectOptionValues.getString("option_value");
                               if((questionID.equals("79"))){
                                   boolean contains = Arrays.asList(arrayselectedOptionsSpinner).contains(jsonObjectOptionValues.getString("option_id"));
                                   if(contains){
                                       spinnerAL.add(spinnerOption);
                                   }
                               }else{
                                   spinnerAL.add(spinnerOption);
                               }
                           }
                           spinnerAL.add(0, getString(R.string.select_option));
                           ArrayAdapter arrayAdapter=new ArrayAdapter(this, R.layout.custom_spinner_dropdown, spinnerAL);
                           spinner.setAdapter(arrayAdapter);
                           if((back_status==true || screen_type.equals("survey_list")) && answerModelList.size()>startPosition){
                               spinner.setSelection(Integer.parseInt(answerModelList.get(startPosition).getOption_id()));
                           }
                       }
                       if((questionID.equals("72"))) {
                           String stayWith = sharedPrefHelper.getString("stayWith", "");
                           if(!stayWith.equals("1")){
                               txtLabel.setVisibility(View.GONE);
                               spinner.setVisibility(View.GONE);
                           }
                       }
                       if((questionID.equals("73"))) {
                           String stayWith = sharedPrefHelper.getString("stayWith", "");
                           if(!stayWith.equals("2")){
                               txtLabel.setVisibility(View.GONE);
                               spinner.setVisibility(View.GONE);
                           }
                       }
                       if((questionID.equals("75"))) {
                           String town_village_class=sharedPrefHelper.getString("town_village_class","");
                           if(!town_village_class.equals("Urban")){
                               txtLabel.setVisibility(View.GONE);
                               spinner.setVisibility(View.GONE);
                           }
                       }
                       if(jsonObjectQuesType.getString("question_id").equals("84")){
                           String accessInternetOnMobile=sharedPrefHelper.getString("accessInternetOnMobile","");
                           if(!accessInternetOnMobile.equals("1")){
                               txtLabel.setVisibility(View.GONE);
                               spinner.setVisibility(View.GONE);
                           }
                       }
                       if (questionID.equals("85")){
                           String lastTimeAccessInternet=sharedPrefHelper.getString("last_time_access_internet","");
                           if (lastTimeAccessInternet.equals("9")){
                               txtLabel.setVisibility(View.GONE);
                               spinner.setVisibility(View.GONE);
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
                       button.setText(jsonObjectQuesType.getString("question_name"));
                       button.setTypeface(null, Typeface.BOLD);
                       button.setTextColor(Color.WHITE);
                       button.setBackgroundResource(R.drawable.btn_background);

                       ll_parent.addView(button);
                       ll_parent.addView(textView);
                       isGPSClicked=1;
                       button.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View view) {
                               checkGPSEnable();
                               if(isGPS==true){
                                   isGPSClicked=2;
                                   textView.setText("Latitude: "+sharedPrefHelper.getString("LAT", "") +"\n"+
                                           "Longitude: "+sharedPrefHelper.getString("LONG", ""));
                               }
                           }
                       });
                   }
                   else if (jsonObjectQuesType.getString("question_type").equals("6")) {
                       TextView textView=new TextView(this);
                       textView.setId(Integer.parseInt(jsonObjectQuesType.getString("question_id")));
                       String description=jsonObjectQuesType.getString("question_name");
                       description=description.replaceAll("\\$name",sharedPrefHelper.getString("user_name",""));
                       description=description.replaceAll("\\$agency",sharedPrefHelper.getString("agency_name",""));
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

    private void openDialogForAgeConfirmation(EditText editText,String value) {
        new AlertDialog.Builder(context).setTitle("Alert!")
                .setMessage(value)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //TODO here
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //TODO here
                        editText.setText("");
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert).show();
    }
    private void openDialogForAgeConfirmationSpinner(String value) {
        new AlertDialog.Builder(context).setTitle("Alert!")
                .setMessage(value)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //TODO here
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //TODO here
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert).show();
    }

    private void startRecording() {
        if (checkPermission()) {
            mediaRecorder = new MediaRecorder();
            IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED);
            registerReceiver(mBluetoothScoReceiver, intentFilter);
            audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
            // Start Bluetooth SCO.
            audioManager.setMode(audioManager.MODE_NORMAL);
            audioManager.setBluetoothScoOn(true);
            audioManager.startBluetoothSco();
            // Stop Speaker.
            audioManager.setSpeakerphoneOn(false);
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            AudioSavePathInDevice = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + timeStamp + ".mp3";
            MediaRecorderReady();
            try {

                mediaRecorder.prepare();
                mediaRecorder.start();
                isRecording = true;

            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                //Toast.makeText(context, "Recording Failed", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                isRecording = false;
            } catch (IOException e) {
                // TODO Auto-generated catch block
               // Toast.makeText(context, "Recording Failed", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                isRecording = false;
            }
        } else {
            requestPermission();
           // Toast.makeText(this, "Failed to create recording folder.", Toast.LENGTH_SHORT).show();
            isRecording = false;
        }
    }
    private void stopRecording() {
        if (mediaRecorder != null) {
            try {
                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder = null;
            } catch (Exception e) {
                // Add your handling here.
                e.printStackTrace();
            }
        }
        //Toast.makeText(context, ""+AudioSavePathInDevice, Toast.LENGTH_SHORT).show();
    }
    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(context,
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(context,
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }
    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }
    public void MediaRecorderReady() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(AudioSavePathInDevice);
    }

    private BroadcastReceiver mBluetoothScoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int state = intent.getIntExtra(AudioManager.EXTRA_SCO_AUDIO_STATE, -1);
            System.out.println("ANDROID Audio SCO state: " + state);
            if (AudioManager.SCO_AUDIO_STATE_CONNECTED == state) {
                /*
                 * Now the connection has been established to the bluetooth device.
                 * Record audio or whatever (on another thread).With AudioRecord you can record with an object created like this:
                 * new AudioRecord(MediaRecorder.AudioSource.MIC, 8000, AudioFormat.CHANNEL_CONFIGURATION_MONO,
                 * AudioFormat.ENCODING_PCM_16BIT, audioBufferSize);
                 *
                 * After finishing, don't forget to unregister this receiver and
                 * to stop the bluetooth connection with am.stopBluetoothSco();
                 */
            } else {
            }
        }
    };

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
        if (AudioSavePathInDevice!=null) {
            intentTerminate.putExtra("AudioSavePathInDevice", AudioSavePathInDevice);
        }
        startActivity(intentTerminate);
        finish();
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
    private void showPopupForTerminateSurveyOnRadio(String id, JSONObject jsonObjectQuesType,
                                                    RadioButton radioButton) {
        new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Are you sure?")
                .setContentText("Want to terminate the interview!")
                .setConfirmText("Yes")
                .setCancelText("No")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismiss();
                        setTerminattion(id);
                    }
                })
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismiss();
                        try {
                            if (jsonObjectQuesType.getString("question_id").equals("25")){
                                if (radioButton.isChecked()){
                                    radioButton.setChecked(false);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        /*hide and show toolbar items*/
        //if (screen_type.equalsIgnoreCase("survey")) {
            MenuItem item_stop_survey=menu.findItem(R.id.stop_survey);
            item_stop_survey.setVisible(true);
            MenuItem item_logout=menu.findItem(R.id.logout);
            item_logout.setVisible(false);
        //}

        return true;
    }

    private void startRecordingAnimation(int type) {
        Animation animation=null;
        if(type==2){
            animation = new AlphaAnimation((float) 0, 0); //to change visibility from visible to invisible
        }else{
            animation = new AlphaAnimation((float) 0.5, 0); //to change visibility from visible to invisible
        }
        animation.setDuration(1000); //1 second duration for each animation cycle
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(Animation.INFINITE); //repeating indefinitely
        animation.setRepeatMode(Animation.REVERSE); //animation will start from end point once ended.
        iv_recording.startAnimation(animation); //to start animation
    }
    public void checkGPSEnable(){
        new GpsUtils(this).turnGPSOn(new GpsUtils.onGpsListener() {
            @Override
            public void gpsStatus(boolean isGPSEnable) {
                // turn on GPS
                isGPS = isGPSEnable;
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == AppConstants.GPS_REQUEST) {
                isGPS = true; // flag maintain before get location
            }
        }
        else if (resultCode == Activity.RESULT_CANCELED) {
            if (requestCode == AppConstants.GPS_REQUEST) {
                isGPS=false;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("pause","onpause Called");
        unregisterReceiver(mBluetoothScoReceiver);
        // Stop Bluetooth SCO.
        audioManager.stopBluetoothSco();
        audioManager.setMode(audioManager.MODE_NORMAL);
        audioManager.setBluetoothScoOn(false);
        // Start Speaker.
        audioManager.setSpeakerphoneOn(true);
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
        super.onDestroy();
        onBackPressedListener = null;
        stopRecording();
    }
    @Override
    public void passDataToActivity(ArrayList<ArrayList<AnswerModel>> answerModelListTotal,ArrayList<AnswerModel> answerModelList,int type){
       // Log.e("Fragment Dismissed",someValue);
        if(type==1){
            //answerModelHouseholdMemberList.clear();
            ArrayList<ArrayList<AnswerModel>> answerModelListNTotal=answerModelListTotal;
            ArrayList<AnswerModel> answerModelListN=answerModelList;
            answerModelHouseholdMemberList=answerModelListN;
            answerModelHouseholdMemberListTotal=answerModelListNTotal;
        }else if(type==2){
            //answerModelTVList.clear();
            ArrayList<ArrayList<AnswerModel>> answerModelListNTotal=answerModelListTotal;
            ArrayList<AnswerModel> answerModelListN=answerModelList;
            answerModelTVList=answerModelListN;
            answerModelTVListTotal=answerModelListNTotal;
        }
        questionsPopulate();
        //textView.setText(someValue);
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED);
        registerReceiver(mBluetoothScoReceiver, intentFilter);
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        // Start Bluetooth SCO.
        audioManager.setMode(audioManager.MODE_NORMAL);
        audioManager.setBluetoothScoOn(true);
        audioManager.startBluetoothSco();
        // Stop Speaker.
        audioManager.setSpeakerphoneOn(false);

        //date-time
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        sharedPrefHelper.setString("dateTime", dateFormat.format(cal.getTime()));
    }
}
