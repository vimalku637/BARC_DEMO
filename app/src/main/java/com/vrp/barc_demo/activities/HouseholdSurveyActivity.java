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
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import com.vrp.barc_demo.models.SurveyModel;
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Part;

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
    @BindView(R.id.btn_terminate)
    MaterialButton btn_terminate;
    @BindView(R.id.ll_parent)
    LinearLayout ll_parent;
    @BindView(R.id.iv_recording)
    ShapeableImageView iv_recording;

    /*normal widgets*/
    private Context context=this;
    private String survey_id="";
    private String screen_type="";
    private int is_household_tv=0;
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
    JSONObject jsonQuestionsLangGroup = null;
    JSONArray jsonArrayQuestions=null;
    HashMap<Integer,String> languageHashMap=new HashMap<>();
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
    MultipartBody.Part part;
    //initialization MediaPlayer
    MediaPlayer mediaPlayer=new MediaPlayer();
    private String name="";
    private int ageInYears=0;
    private String mobileNo="",reMobileNo,landlineNo;
    private String sixDigitCode="",pinCode="";
    boolean isGPS=false;
    int isGPSClicked=0;
    SurveyModel surveyModel;
    private String checkedIdForCheckBox="";
    boolean isBack_disable=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        ButterKnife.bind(this);
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
            sharedPrefHelper.setString("survey_id", survey_id);
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
        setTitle(sharedPrefHelper.getString("survey_id", ""));
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
                //jsonQuestions = new JSONObject(MyJSON.loadJSONFromAsset(context));
                jsonQuestionsLangGroup = new JSONObject(MyJSON.loadJSONFromAsset(context));
                /*if (jsonQuestions.has("screen")) {
                    jsonArrayScreen = jsonQuestions.getJSONArray("screen");
                    totalScreen = jsonArrayScreen.length();
                    Log.e("Screen", "onCreate: " + jsonArrayScreen.toString());
                    if(totalScreen>0){
                        questionsPopulate();
                    }
                }*/
                if (jsonQuestionsLangGroup.has("language_group")) {
                    /*JSONArray jsonArrayGroup=jsonQuestions.getJSONArray("group");
                    JSONObject jsonObjectGroup=jsonArrayGroup.getJSONObject(0);*/
                    jsonQuestions = jsonQuestionsLangGroup.getJSONArray("language_group").getJSONObject(sharedPrefHelper.getInt("langID",1));
                    jsonArrayScreen=new JSONArray();
                    jsonArrayScreen = jsonQuestions.getJSONArray("group").getJSONObject(0).getJSONArray("screens");
                    //jsonArrayScreenbackup=jsonQuestions.getJSONArray("group").getJSONObject(0).getJSONArray("screens");
                    totalScreen = jsonArrayScreen.length();
                    Log.e("Screen", "onCreate: " + jsonArrayScreen.toString());
                    if(totalScreen>0){
                        questionsPopulate();
                        sharedPrefHelper.setString("CWE_Yes","0");
                        sharedPrefHelper.setString("HH_Yes","0");
                        sharedPrefHelper.setString("CWE_Name","");
                        sharedPrefHelper.setString("HH_Name","");
                        sharedPrefHelper.setString("CWE_Status","");
                        sharedPrefHelper.setString("last_time_access_internet", "");
                        sharedPrefHelper.setString("rq3e_selected","");
                    }
                }
                /*if (jsonQuestions.has("group")) {
                    *//*JSONArray jsonArrayGroup=jsonQuestions.getJSONArray("group");
                    JSONObject jsonObjectGroup=jsonArrayGroup.getJSONObject(0);*//*
                    jsonArrayScreen=new JSONArray();
                    jsonArrayScreen = jsonQuestions.getJSONArray("group").getJSONObject(0).getJSONArray("screens");
                    //jsonArrayScreenbackup=jsonQuestions.getJSONArray("group").getJSONObject(0).getJSONArray("screens");
                    totalScreen = jsonArrayScreen.length();
                    Log.e("Screen", "onCreate: " + jsonArrayScreen.toString());
                    if(totalScreen>0){
                        questionsPopulate();
                        sharedPrefHelper.setString("CWE_Yes","0");
                        sharedPrefHelper.setString("HH_Yes","0");
                        sharedPrefHelper.setString("CWE_Name","");
                        sharedPrefHelper.setString("HH_Name","");
                    }
                }*/

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
        surveyModel=new SurveyModel();
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
                int occupation_id=0;
                    for (int i = 0; i < ll_parent.getChildCount(); i++) {
                        final View childView = ll_parent.getChildAt(i);
                        try {
                            if (childView instanceof EditText) {
                                EditText editText = (EditText) childView;
                                //EditText editTextSurname=(EditText) childView;
                                int viewID=editText.getId();
                                String questionID=jsonArrayQuestions.getJSONObject(count).getString("question_id");
                                String isec = "None";
                                if(questionID.equals("122")) {
                                    String town_village_class = sharedPrefHelper.getString("town_village_class", "");
                                    if ((town_village_class.equalsIgnoreCase("Urban") && occupation_id == 1) || (town_village_class.equalsIgnoreCase("Rural") && (occupation_id == 3 || occupation_id == 7))) {
                                        isec = "Labour";
                                    } else if ((town_village_class.equalsIgnoreCase("Urban") && (occupation_id == 13 || occupation_id == 14)) || (town_village_class.equalsIgnoreCase("Rural") && (occupation_id == 1 || occupation_id == 2 || occupation_id == 6 || occupation_id == 4 || occupation_id == 5))) {
                                        isec = "Farmer";
                                    } else if ((town_village_class.equalsIgnoreCase("Urban") && occupation_id == 2) || (town_village_class.equalsIgnoreCase("Rural") && (occupation_id == 8 || occupation_id == 10 || occupation_id == 13 || occupation_id == 14))) {
                                        isec = "Worker";
                                    } else if ((town_village_class.equalsIgnoreCase("Urban") && (occupation_id == 3 || occupation_id == 4 || occupation_id == 5 || occupation_id == 6)) || (town_village_class.equalsIgnoreCase("Rural") && (occupation_id == 9 || occupation_id == 15 || occupation_id == 16 || occupation_id == 17 || occupation_id == 18))) {
                                        isec = "Trader";
                                    } else if ((town_village_class.equalsIgnoreCase("Urban") && (occupation_id == 9 || occupation_id == 10)) || (town_village_class.equalsIgnoreCase("Rural") && (occupation_id == 11 || occupation_id == 13 || occupation_id == 14 || occupation_id == 21 || occupation_id == 22))) {
                                        isec = "Clerical/Sales/Supervisory";
                                    } else if (occupation_id == 7 || occupation_id == 8 || occupation_id == 11 || occupation_id == 12 || (town_village_class.equalsIgnoreCase("Rural") && (occupation_id == 12 || occupation_id == 13 || occupation_id == 14 || occupation_id == 19 || occupation_id == 20 || occupation_id == 23 || occupation_id == 24))) {
                                        isec = "Managerial/businessman/professional";
                                    } else if (occupation_id == 99) {
                                        isec = "Not Applicable";
                                    }
                                }
                                editFieldValues=editText.getText().toString().trim();
                                groupQuestionID=Integer.parseInt(jsonArrayQuestions.getJSONObject(count).getString("question_id"));
                                String text=editText.getText().toString().trim();

                                text = text.replace("\n", "").replace("\r", "");
                                is_household_tv=0;
                                if((back_status==true || screen_type.equals("survey_list")) && answerModelList.size()>nextPosition){
                                    if(questionID.equals("32")){
                                        if(!answerModelList.get(nextPosition).getOption_value().equals(text)){
                                            showPopupForDataEraseEditText(Integer.parseInt(questionID),answerModelList.get(nextPosition).getOption_value(),editText,nextPosition);
                                            flag=false;
                                            break;
                                        }
                                    }else if(questionID.equals("57")){
                                        if(!answerModelList.get(nextPosition).getOption_value().equals(text)){
                                            showPopupForDataEraseEditText(Integer.parseInt(questionID),answerModelList.get(nextPosition).getOption_value(),editText,nextPosition);
                                            flag=false;
                                            break;
                                        }
                                    }
                                    if(questionID.equals("122")){
                                        answerModelList.get(nextPosition).setOption_value(isec);
                                    }else{
                                        answerModelList.get(nextPosition).setOption_value(text);
                                    }

                                    answerModelList.get(nextPosition).setOption_id("");
                                    answerModelList.get(nextPosition).setQuestionID(jsonArrayQuestions.getJSONObject(count).getString("question_id"));
                                    answerModelList.get(nextPosition).setPre_field(jsonArrayQuestions.getJSONObject(count).getString("pre_field"));
                                    answerModelList.get(nextPosition).setField_name(jsonArrayQuestions.getJSONObject(count).getString("field_name"));
                                }
                                else{
                                    AnswerModel answerModel= new AnswerModel();
                                    answerModel.setOption_id("");
                                    if(questionID.equals("122")){
                                        answerModel.setOption_value(isec);
                                    }else{
                                        answerModel.setOption_value(text);
                                    }
                                    //answerModel.setSurveyID(survey_id);
                                    answerModel.setQuestionID(jsonArrayQuestions.getJSONObject(count).getString("question_id"));
                                    answerModel.setPre_field(jsonArrayQuestions.getJSONObject(count).getString("pre_field"));
                                    answerModel.setField_name(jsonArrayQuestions.getJSONObject(count).getString("field_name"));
                                    answerModelList.add(answerModel);
                                }
                                if (jsonArrayQuestions.getJSONObject(count).getString("validation_id").equals("1")
                                        && (questionID.equals("94")||questionID.equals("90")||questionID.equals("86")||questionID.equals("109")||questionID.equals("93")||questionID.equals("95")) && editText.getText().toString().trim().equals("")){
                                    String town_village_class = sharedPrefHelper.getString("town_village_class", "");
                                    if (questionID.equals("94")) {
                                        if (!town_village_class.equalsIgnoreCase("Rural")) {
                                            flag = true;
                                        } else {
                                            flag = false;
                                            break;
                                        }
                                    }
                                    else if (questionID.equals("90")) {
                                        String ppNumber=sharedPrefHelper.getString("pp_number","0");
                                        if (!ppNumber.equalsIgnoreCase("1")) {
                                            flag = true;
                                        } else {
                                            flag = false;
                                            break;
                                        }
                                    }
                                    else if (questionID.equals("93")) {
                                        if (!town_village_class.equalsIgnoreCase("Urban")) {
                                            flag = true;
                                        } else {
                                            flag = false;
                                            break;
                                        }
                                    }
                                    else if (questionID.equals("95")) {
                                        if (!town_village_class.equalsIgnoreCase("Urban")) {
                                            flag = true;
                                        } else {
                                            flag = false;
                                            break;
                                        }
                                    }else{
                                        flag=true;
                                    }
                                } else {
                                    if(jsonArrayQuestions.getJSONObject(count).getString("validation_id").equals("1") && editText.getText().toString().trim().equals("")){
                                        flag=false;
                                        break;
                                    }
                                    else if (questionID.equals("32")) {
                                        is_household_tv=1;
                                    }
                                    else if (questionID.equals("46")) {
                                        name=editText.getText().toString().trim();
                                        sharedPrefHelper.setString("name", name);
                                        if (name.length()==1){
                                            flag=false;
                                            editText.setError("Name can't be blank or only one character");
                                            break;
                                        }
                                        sharedPrefHelper.setString("CWE_Name", name);
                                    }
                                    else if (questionID.equals("47")) {
                                        ageInYears=Integer.parseInt(editText.getText().toString().trim());
                                        sharedPrefHelper.setInt("ageInYears", ageInYears);
                                        if(ageInYears<15){
                                            flag=false;
                                            editText.setError("Age can't be less than 15");
                                            break;
                                        }
                                        else if(ageInYears>120){
                                            editText.setError("Age can't be greater than 120");
                                            flag=false;
                                            break;
                                        }
                                    }
                                    else if(questionID.equals("86")){
                                        sixDigitCode=editText.getText().toString().trim();
                                        sharedPrefHelper.setString("sixDigitCode", sixDigitCode);
                                        if (!sharedPrefHelper.setString("sixDigitCode", sixDigitCode).equals("")) {
                                            if (sharedPrefHelper.getString("sixDigitCode", "").length() < 6) {
                                                editText.setError("Code can't be less than 6 digit");
                                                flag = false;
                                                break;
                                            }
                                        }
                                    }
                                    else if (questionID.equals("99")){
                                        pinCode=editText.getText().toString().trim();
                                        sharedPrefHelper.setString("pinCode", pinCode);
                                        if (sharedPrefHelper.getString("pinCode","").length()<6
                                                ||sharedPrefHelper.getString("pinCode","").equals("000000")){
                                            editText.setError("Pincode can't be less then 6 digit or start from 0");
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
                                            editText.setError("Mobile can't be less than 10 digit");
                                            break;
                                        }else if (!bs) {
                                            flag=false;
                                            editText.setError("Mobile no. should be start from 6-9 and maximum length should be 10");
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
                                            editText.setError("Confirm mobile can't be less than 10 digit");
                                            flag=false;
                                            break;
                                        }
                                       else if (!sharedPrefHelper.getString("confirm_mobile_no", "").equals(mobileNo)){
                                            editText.setError("Confirm mobile no. not matching with mobile no.");
                                            flag=false;
                                            break;
                                        }
                                        else if (!bs) {
                                            editText.setError("Confirm mobile no. should be start from 6-9 and maximum length should be 10");
                                            flag=false;
                                            break;
                                        }
                                    }
                                    else if(questionID.equals("109")){
                                        landlineNo=editText.getText().toString().trim();
                                        if (!landlineNo.equals("")) {
                                            if (landlineNo.length()<11) {
                                                editText.setError("Landline no. can't be less than 11 digit");
                                                flag = false;
                                                break;
                                            }
                                        }
                                    }
                                    else if (questionID.equals("57")){
                                        String TvWorkingCondition=editText.getText().toString().trim();
                                        if(Integer.parseInt(TvWorkingCondition)>10 || Integer.parseInt(TvWorkingCondition)==0){
                                            editText.setError("TV should be between 1-10 and can't be greater than 10");
                                            flag=false;
                                            break;
                                        }else{
                                            is_household_tv=2;
                                            sharedPrefHelper.setString("TvWorkingCondition", TvWorkingCondition);
                                        }
                                    }
                                    else if (questionID.equals("46")){
                                        sharedPrefHelper.setString("CWE_Name",editText.getText().toString().trim());
                                    }
                                    else if (jsonArrayQuestions.getJSONObject(count).getString("question_id").equals("32") ){
                                        if(Integer.parseInt(editText.getText().toString().trim())>20 || Integer.parseInt(editText.getText().toString().trim())==0){
                                            editText.setError("Member should be between 1-20 and can't be greater than 20");
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
                                }
                                else if(jsonArrayQuestions.getJSONObject(count).getString("question_id").equals("89")){
                                    sharedPrefHelper.setString("pp_number",""+radioID);
                                }
                                if((back_status==true || screen_type.equals("survey_list")) && answerModelList.size()>nextPosition){
                                    //if((back_status==true || screen_type.equals("survey_list")) && answerModelList.get(startPositionBefore).getQuestionID().equals(jsonArrayQuestions.getJSONObject(count).getString("question_id"))){
                                    answerModelList.get(nextPosition).setOption_id(Integer.toString(radioID));
                                    answerModelList.get(nextPosition).setOption_value("");
                                    answerModelList.get(nextPosition).setQuestionID(jsonArrayQuestions.getJSONObject(count).getString("question_id"));
                                    answerModelList.get(nextPosition).setPre_field(jsonArrayQuestions.getJSONObject(count).getString("pre_field"));
                                    answerModelList.get(nextPosition).setField_name(jsonArrayQuestions.getJSONObject(count).getString("field_name"));
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
                                    /*if(jsonArrayQuestions.getJSONObject(count).getString("question_id").equals("53")) {
                                        String selectedOptionsSpinner = sharedPrefHelper.getString("CWE_Status", "");
                                        if (selectedOptionsSpinner.equals("1")) {
                                            flag=true;
                                        }else{
                                            flag=false;
                                            break;
                                        }
                                    }else */
                                    if(jsonArrayQuestions.getJSONObject(count).getString("question_id").equals("82")){
                                        String selectedOptions=sharedPrefHelper.getString("type_of_mobile","");
                                        if (selectedOptions.contains("1")){
                                            flag=true;
                                        }else if(selectedOptions.contains("4")){
                                            flag=true;
                                        }else if(selectedOptions.contains("1")&&selectedOptions.contains("4")){
                                            flag=true;
                                        }
                                        else{
                                            flag=false;
                                            break;
                                        }
                                    }
                                    else if(jsonArrayQuestions.getJSONObject(count).getString("question_id").equals("83")){
                                        String type_of_mobile=sharedPrefHelper.getString("type_of_mobile","");
                                        if (type_of_mobile.equals("4")) {
                                            flag = true;
                                        } else {
                                            flag = false;
                                            break;
                                        }
                                    }
                                    else if(jsonArrayQuestions.getJSONObject(count).getString("question_id").equals("114")||
                                            jsonArrayQuestions.getJSONObject(count).getString("question_id").equals("115")){
                                        /*if (checkedIdForCheckBox.equals("2")||checkedIdForCheckBox.equals("3")||checkedIdForCheckBox.equals("4")) {*/
                                            flag = true;
                                        /*} else {
                                            flag = false;
                                            break;
                                        }*/
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

                                /*JSONArray jsonArrayOptions=jsonArrayQuestions.getJSONObject(count).getJSONArray("question_options");
                                for (int j = 0; j <jsonArrayOptions.length(); j++) {

                                }*/
                                //String spinnerID=Long.toString(spinner.getSelectedItemId());
                                long spinnerID=spinner.getSelectedItemId();
                                String questionID=jsonArrayQuestions.getJSONObject(count).getString("question_id");
                                if(questionID.equals("51")) {
                                    occupation_id=(int)spinner.getSelectedItemId();
                                }
                                JSONArray jsonArrayOptions = jsonArrayQuestions.getJSONObject(count).getJSONArray("question_options");

                                for (int k = 0; k < jsonArrayOptions.length(); k++) {
                                    JSONObject jsonObjectOptionValues = jsonArrayOptions.getJSONObject(k);
                                    String spinnerOptionOptionValue = jsonObjectOptionValues.getString("option_value");
                                    if(spinnerOptionOptionValue.equals(spinner.getSelectedItem())){
                                        spinnerID=Long.parseLong(jsonObjectOptionValues.getString("option_id"));
                                        break;
                                    }
                                }
                                if(jsonArrayQuestions.getJSONObject(count).getString("question_id").equals("79")){
                                    Map<Integer, String> treeMapLang = new TreeMap<>(languageHashMap);
                                    int spnposP=1;
                                    int position=1;
                                    if(spinnerID!=0){
                                        for (Map.Entry<Integer, String> entry : treeMapLang.entrySet()) {
                                            /*if (spnposP==spinnerID) {
                                                spinnerID=entry.getKey();
                                                break;
                                            }*/
                                            if (entry.getValue().equals(spinner.getSelectedItem())) {
                                                spinnerID=entry.getKey();
                                                break;
                                            }
                                            //spnposP++;
                                        }
                                    }
                                }

                                if(jsonArrayQuestions.getJSONObject(count).getString("question_id").equals("75")){
                                    sharedPrefHelper.setString("tenement_type",""+spinnerID);
                                }/*else if(jsonArrayQuestions.getJSONObject(count).getString("question_id").equals("84")){
                                    sharedPrefHelper.setString("last_time_access_internet",""+spinnerID);
                                }*/
                                else if (jsonArrayQuestions.getJSONObject(count).getString("question_id").equals("50")){
                                    //String currentWorkingStatus=Long.toString(spinner.getSelectedItemId());
                                    String currentWorkingStatus=Long.toString(spinnerID);
                                    //if currentWorkingStatus=3 or 4 or 6 or 7 then auto populate the occupation spinner option 'Not Applicable'
                                    sharedPrefHelper.setString("currentWorkingStatus", currentWorkingStatus);
                                }
                                if((back_status==true || screen_type.equals("survey_list")) && answerModelList.size()>nextPosition){
                                    if(jsonArrayQuestions.getJSONObject(count).getString("question_id").equals("100")) {
                                        answerModelList.get(nextPosition).setOption_value(spinner.getSelectedItem().toString());
                                    }else{
                                        answerModelList.get(nextPosition).setOption_value("");
                                    }
                                    answerModelList.get(nextPosition).setOption_id(Long.toString(spinnerID));
                                    answerModelList.get(nextPosition).setQuestionID(jsonArrayQuestions.getJSONObject(count).getString("question_id"));
                                    answerModelList.get(nextPosition).setPre_field(jsonArrayQuestions.getJSONObject(count).getString("pre_field"));
                                    answerModelList.get(nextPosition).setField_name(jsonArrayQuestions.getJSONObject(count).getString("field_name"));
                                }else{
                                    AnswerModel answerModel= new AnswerModel();
                                    answerModel.setOption_id(Long.toString(spinnerID));
                                    if(jsonArrayQuestions.getJSONObject(count).getString("question_id").equals("100")){
                                        answerModel.setOption_value(spinner.getSelectedItem().toString());
                                    }else{
                                        answerModel.setOption_value("");
                                    }

                                    //answerModel.setSurveyID(survey_id);
                                    answerModel.setQuestionID(jsonArrayQuestions.getJSONObject(count).getString("question_id"));
                                    answerModel.setPre_field(jsonArrayQuestions.getJSONObject(count).getString("pre_field"));
                                    answerModel.setField_name(jsonArrayQuestions.getJSONObject(count).getString("field_name"));
                                    answerModelList.add(answerModel);
                                }
                                if(jsonArrayQuestions.getJSONObject(count).getString("validation_id").equals("1")
                                        && spinner.getSelectedItemId()==0){
                                    if (jsonArrayQuestions.getJSONObject(count).getString("question_id").equals("87")) {
                                        //if (!sharedPrefHelper.getString("sixDigitCode","").equals("")) {
                                            flag = true;
                                        /*}else {
                                            flag = false;
                                            break;
                                        }*/
                                    }
                                    else {
                                        if (jsonArrayQuestions.getJSONObject(count).getString("question_id").equals("50")
                                                || jsonArrayQuestions.getJSONObject(count).getString("question_id").equals("51")
                                                || jsonArrayQuestions.getJSONObject(count).getString("question_id").equals("52")) {
                                            String selectedOptionsSpinner = sharedPrefHelper.getString("CWE_Status", "");
                                            if (selectedOptionsSpinner.equals("1")) {
                                                flag = true;
                                            } else {
                                                flag = false;
                                                break;
                                            }
                                        }
                                        else if (jsonArrayQuestions.getJSONObject(count).getString("question_id").equals("72")) {
                                            String stayWith = sharedPrefHelper.getString("stayWith", "");
                                            if (!stayWith.equals("1")) {
                                                flag = true;
                                            } else {
                                                flag = false;
                                                break;
                                            }
                                        }
                                        else if (jsonArrayQuestions.getJSONObject(count).getString("question_id").equals("73")) {
                                            String stayWith = sharedPrefHelper.getString("stayWith", "");
                                            if (!stayWith.equals("2")) {
                                                flag = true;
                                            } else {
                                                flag = false;
                                                break;
                                            }
                                        }
                                        else if (jsonArrayQuestions.getJSONObject(count).getString("question_id").equals("75")) {
                                            String town_village_class = sharedPrefHelper.getString("town_village_class", "");
                                            if (!town_village_class.equalsIgnoreCase("Urban")) {
                                                flag = true;
                                            } else {
                                                flag = false;
                                                break;
                                            }
                                        }
                                        else if (jsonArrayQuestions.getJSONObject(count).getString("question_id").equals("84")) {
                                            int internetUse=0;
                                            Gson gson = new Gson();
                                            java.lang.reflect.Type type = new TypeToken<HashMap<String, String>>(){}.getType();
                                            HashMap<Integer, String> testHashMap2 = gson.fromJson(sharedPrefHelper.getString("internetUse",""), type);
                                            ///JSONObject obj=new JSONObject(sharedPrefHelper.setString("householdMember","objMember.toString()"););
                                            if (testHashMap2!=null) {
                                                Map<Integer, String> treeMapName = new TreeMap<>(testHashMap2);

                                                for (Map.Entry<Integer, String> entry : treeMapName.entrySet()) {
                                                    if (entry.getValue().equals("1")) {
                                                        internetUse = 1;
                                                        break;
                                                    }else if(entry.getValue().equals("2")){
                                                        internetUse = 2;
                                                    }//
                                                }
                                            }
                                            if (internetUse==2) {
                                                flag = true;
                                            } else {
                                                flag = false;
                                                break;
                                            }
                                        }
                                        /*else if (jsonArrayQuestions.getJSONObject(count).getString("question_id").equals("84")) {
                                            String accessInternetOnMobile = sharedPrefHelper.getString("2100f_radio_ids", "");
                                            if (accessInternetOnMobile.equals("2")||accessInternetOnMobile.equals("9")) {
                                                flag = true;
                                            } else {
                                                flag = false;
                                                break;
                                            }
                                        }*/
                                        else if (jsonArrayQuestions.getJSONObject(count).getString("question_id").equals("85")) {
                                            String lastTimeAccessInternet = sharedPrefHelper.getString("last_time_access_internet", "");
                                            if (lastTimeAccessInternet.equals("9")) {
                                                flag = true;
                                            } else {
                                                flag = false;
                                                break;
                                            }
                                        }
                                        else {
                                            flag = false;
                                            break;
                                        }
                                    }
                                }
                                else if(jsonArrayQuestions.getJSONObject(count).getString("question_id").equals("77")){
                                    int TvWorkingCondition=Integer.parseInt(sharedPrefHelper.getString("TvWorkingCondition", ""));
                                    String spinnerIds=Long.toString(spinner.getSelectedItemId());
                                    if (spinner.getSelectedItemId()<TvWorkingCondition && spinner.getSelectedItemId()<5){
                                        flag=false;
                                        showPopupForError("No of rooms can't be less then no. of TV");
                                        break;
                                    }else{
                                        flag=true;
                                    }
                                }
                                else if(jsonArrayQuestions.getJSONObject(count).getString("question_id").equals("45")){
                                    sharedPrefHelper.setString("CWE_Living_status", ""+spinnerID);
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
                                    if(jsonArrayQuestions.getJSONObject(count).getString("question_id").equals("78")){
                                        if (checkBox.isChecked()) {
                                            jsonArray.put(checkBox.getText().toString().trim());
                                            jsonObject.put("check_box", jsonArray);
                                            if(selectedOptions.equals("")){
                                                languageHashMap.clear();
                                                selectedOptions=Integer.toString(checkBox.getId());
                                            }else{
                                                selectedOptions=selectedOptions+","+Integer.toString(checkBox.getId());
                                            }
                                            languageHashMap.put(checkBox.getId(),checkBox.getText().toString().trim());
                                        }
                                    }else{
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
                                }
                                if(jsonArrayQuestions.getJSONObject(count).getString("question_id").equals("29")){
                                    if(selectedOptions.contains("7"))
                                    sharedPrefHelper.setString("selectedDurables",selectedOptions);
                                    else {
                                        showPopupForTerminateForTV("Durables");
                                        flag = false;
                                        break;
                                    }
                                        //setTerminattion("Durables");
                                }
                                else if(jsonArrayQuestions.getJSONObject(count).getString("question_id").equals("44")) {
                                    if ((selectedOptions.contains("1") || selectedOptions.contains("2") || selectedOptions.contains("3")) && (selectedOptions.contains("6"))){
                                        showPopupForError("You can't choose no use internet with other options");
                                        flag = false;
                                        break;
                                    }
                                }
                                else if(jsonArrayQuestions.getJSONObject(count).getString("question_id").equals("69")){//30-01-2021
                                    String[] arrOfStr = selectedOptions.split("," );
                                    for (String element : arrOfStr) {
                                        if (element.equals("99")) {
                                            if(arrOfStr.length>1){
                                                showPopupForError("If you choose 'None' option then you are not allow to choose any other options.");
                                                flag = false;
                                                break;
                                            }
                                        }
                                    }
                                    /*if (selectedOptions.contains("1") || selectedOptions.contains("2") || selectedOptions.contains("3") || selectedOptions.contains("4") || selectedOptions.contains("5") || selectedOptions.contains("6") || selectedOptions.contains("7") || selectedOptions.contains("8") || selectedOptions.contains("9") || selectedOptions.contains("10") || selectedOptions.contains("11")){
                                        if (selectedOptions.contains("99")){
                                            showPopupForError("if you choose 'None' option then you are not allow to choose any other options.");
                                            flag = false;
                                        }
                                    }else if(selectedOptions.contains("99")){
                                        if(selectedOptions.contains("1") || selectedOptions.contains("2") || selectedOptions.contains("3") || selectedOptions.contains("4") || selectedOptions.contains("5") || selectedOptions.contains("6") || selectedOptions.contains("7") || selectedOptions.contains("8") || selectedOptions.contains("9") || selectedOptions.contains("10") || selectedOptions.contains("11")){
                                            showPopupForError("if you choose 'None' option then you are not allow to choose any other options.");
                                            flag = false;
                                        }
                                    }*/
                                }
                                else if(jsonArrayQuestions.getJSONObject(count).getString("question_id").equals("70")){//30-01-2021
                                    if (selectedOptions.contains("1") || selectedOptions.contains("2")){
                                        if (selectedOptions.contains("3")){
                                            showPopupForError("if you choose 'None' option then you are not allow to choose any other options.");
                                            flag = false;
                                            break;
                                        }
                                    }else if(selectedOptions.contains("3")){
                                        if(selectedOptions.contains("1") || selectedOptions.contains("2")){
                                            showPopupForError("if you choose 'None' option then you are not allow to choose any other options.");
                                            flag = false;
                                            break;
                                        }
                                    }
                                }
                                else if(jsonArrayQuestions.getJSONObject(count).getString("question_id").equals("78")){
                                    sharedPrefHelper.setString("family_language",""+selectedOptions);
                                }
                                else if (jsonArrayQuestions.getJSONObject(count).getString("question_id").equals("81")){
                                    sharedPrefHelper.setString("type_of_mobile",""+selectedOptions);
                                    if (selectedOptions.contains("1") || selectedOptions.contains("2") || selectedOptions.contains("3") || selectedOptions.contains("99")){//01-02-2021
                                        if (selectedOptions.contains("4")){
                                            showPopupForError("if you choose 'Do not have a mobile phone' option then you are not allow to choose any other options.");
                                            flag = false;
                                            break;
                                        }
                                    }else if(selectedOptions.contains("4")){
                                        if(selectedOptions.contains("1") || selectedOptions.contains("2") || selectedOptions.contains("3") || selectedOptions.contains("99")){
                                            showPopupForError("if you choose 'Do not have a mobile phone' option then you are not allow to choose any other options.");
                                            flag = false;
                                            break;
                                        }
                                    }
                                }
                                else if(jsonArrayQuestions.getJSONObject(count).getString("question_id").equals("113")) {
                                    if ((selectedOptions.contains("1") || selectedOptions.contains("2") || selectedOptions.contains("3")) && (selectedOptions.contains("4"))){
                                        showPopupForError("You can't choose none with other options");
                                        flag = false;
                                        break;
                                    }else{
                                        sharedPrefHelper.setString("rq3e_selected",""+selectedOptions);
                                    }
                                }
                                if((back_status==true || screen_type.equals("survey_list")) && answerModelList.size()>nextPosition){
                                    answerModelList.get(nextPosition).setOption_id(selectedOptions);
                                    answerModelList.get(nextPosition).setOption_value("");
                                    answerModelList.get(nextPosition).setQuestionID(jsonArrayQuestions.getJSONObject(count).getString("question_id"));
                                    answerModelList.get(nextPosition).setPre_field(jsonArrayQuestions.getJSONObject(count).getString("pre_field"));
                                    answerModelList.get(nextPosition).setField_name(jsonArrayQuestions.getJSONObject(count).getString("field_name"));
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
                                if(jsonArrayQuestions.getJSONObject(count).getString("validation_id").equals("1") && selectedOptions.equals("") && !jsonArrayQuestions.getJSONObject(count).getString("question_id").equals("70")){
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
                                    }
                                   else{
                                    flag=false;
                                    break;
                                   }
                                }
                                if(jsonArrayQuestions.getJSONObject(count).getString("question_id").equals("70") && selectedOptions.equals("") && tableLayout.getVisibility()==View.VISIBLE) {
                                    String town_village_class=sharedPrefHelper.getString("town_village_class","");
                                    if(!town_village_class.equalsIgnoreCase("Rural")){
                                        flag=true;
                                    }else{
                                        flag=false;
                                        break;
                                    }
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
                    if(flag==true) {
                        btn_next.setEnabled(false);
                        stopRecording();
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
                            JSONArray json_array = new JSONArray(listString);
                            JSONArray json_array_family = new JSONArray(listStringFamily);
                            JSONArray json_array_TV = new JSONArray(listStringTV);
                            JSONObject json_object = new JSONObject();
                            json_object.put("user_id", sharedPrefHelper.getString("user_id", ""));
                            json_object.put("survey_id", survey_id);
                            json_object.put("app_version", sharedPrefHelper.getString("version", "1.5"));
                            json_object.put("survey_status", "1");//for completed
                            json_object.put("cluster_no", sharedPrefHelper.getString("cluster_no", ""));
                            json_object.put("reason_of_change", sharedPrefHelper.getString("reason_of_change", ""));
                            json_object.put("census_district_code", sharedPrefHelper.getString("census_district_code", ""));
                            if (AudioSavePathInDevice != null) {
                                json_object.put("audio_recording", AudioSavePathInDevice);
                            }
                            json_object.put("GPS_latitude_start", sharedPrefHelper.getString("LAT", ""));
                            json_object.put("GPS_longitude_start", sharedPrefHelper.getString("LONG", ""));
                            json_object.put("survey_data", json_array);
                            json_object.put("GPS_latitude_mid", sharedPrefHelper.getString("LAT", ""));
                            json_object.put("GPS_longitude_mid", sharedPrefHelper.getString("LONG", ""));
                            json_object.put("family_data", json_array_family);
                            json_object.put("tv_data", json_array_TV);
                            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                            Calendar cal = Calendar.getInstance();
                            sharedPrefHelper.setString("dateTime", dateFormat.format(cal.getTime()));
                            json_object.put("date_time", sharedPrefHelper.getString("dateTime", ""));
                            json_object.put("GPS_latitude_end", sharedPrefHelper.getString("LAT", ""));
                            json_object.put("GPS_longitude_end", sharedPrefHelper.getString("LONG", ""));
                            Log.e(TAG, "onClick: " + json_object.toString());

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
                                    Intent intentSurveyActivity1 = new Intent(context, NextSurvey.class);
                                    sqliteHelper.updateLocalFlag("household_survey", "survey", survey_id, 0);
                                    sqliteHelper.updateClusterTable(sharedPrefHelper.getString("cluster_no", ""));
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
                                    Intent intentSurveyActivity1 = new Intent(context, NextSurvey.class);
                                    sqliteHelper.updateLocalFlag("household_survey", "survey", survey_id, 0);
                                    sqliteHelper.updateClusterTable(sharedPrefHelper.getString("cluster_no", ""));
                                    Toast.makeText(context, getResources().getString(R.string.no_internet_data_saved_locally), Toast.LENGTH_SHORT).show();
                                    startActivity(intentSurveyActivity1);
                                    finish();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            btn_next.setEnabled(true);
                        }
                    }
                    else {
                        Toast.makeText(context, "Please fill all required correct fields/values", Toast.LENGTH_LONG).show();
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
                            btn_previous.setText("Previous");
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
                                                sharedPrefHelper.getString("survey_id", ""), 1);
                                    } else {
                                        if (endScreenPosition==1) {
                                            if (sqliteHelper.getSurveyIDFromTable(survey_id).equals(survey_id)){
                                                //update data in to local DB
                                                sqliteHelper.updateSurveyDataInTable("survey", "survey_id", survey_id, json_object);
                                                sqliteHelper.updateLocalFlag("partial", "survey",
                                                        sharedPrefHelper.getString("survey_id", ""), 1);
                                            }else{
                                                //save data in to local DB.
                                                //date-time
                                                sqliteHelper.saveSurveyDataInTable(json_object, survey_id);
                                                sqliteHelper.updateLocalFlag("partial", "survey",
                                                       sharedPrefHelper.getString("survey_id", ""), 1);
                                            }
                                        } else {
                                            //update data in to local DB
                                            sqliteHelper.updateSurveyDataInTable("survey", "survey_id", survey_id, json_object);
                                            sqliteHelper.updateLocalFlag("partial", "survey",
                                                    sharedPrefHelper.getString("survey_id", ""), 1);
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
                            //String object=jsonArrayQuestions.getJSONObject(jsonArrayQuestions.length()-2).toString();
                            if(is_household_tv==1){
                                groupRelationId = jsonArrayQuestions.getJSONObject(jsonArrayQuestions.length()-2).getString("group_relation_id");
                            }else{
                                groupRelationId = jsonArrayQuestions.getJSONObject(jsonArrayQuestions.length()-1).getString("group_relation_id");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (groupRelationId!=null&&!groupRelationId.equalsIgnoreCase("0")) {
                           if (groupRelationId.equalsIgnoreCase("1")) {
                               btn_next.setEnabled(false);
                               isBack_disable=true;
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
                                btn_next.setEnabled(false);
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
                            invalidateOptionsMenu();
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
                btn_next.setEnabled(true);
                //String previousButtonText=btn_previous.getText().toString().trim();
                btnPrevious();
            }
        });
        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommonClass.setPopupForStopSurvey(context);
            }
        });
        /*btn_terminate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });*/
    }

    private void btnPreviousFragment(){
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
            startPosition=startPosition-Integer.parseInt(arrayScreenWiseQuestionModel.get(startScreenPosition).getquestions());
            questionsPopulate();
        }
    }
    private void btnPrevious(){
        //if (previousButtonText.equals("Previous")) {
            startScreenPosition = startScreenPosition - 1;
            endScreenPosition = endScreenPosition - 1;
            back_status = true;
            if (endScreenPosition <= 0) {
                Intent intentHom = new Intent(HouseholdSurveyActivity.this, ClusterDetails.class);
                startActivity(intentHom);
                finish();
            } else {
                isGPSClicked = 0;
                btn_next.setText("Next");
                startPosition = startPosition - (endPosition + Integer.parseInt(arrayScreenWiseQuestionModel.get(startScreenPosition).getquestions()));
                invalidateOptionsMenu();
                questionsPopulate();
            }
        /*}else{
            showPopupForTerminateSurvey();
        }*/
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
                        //update id on the bases of survey id
                        sqliteHelper.updateServerId("survey", survey_id, survey_data_monitoring_id);
                        sqliteHelper.updateLocalFlag("household_survey","survey", survey_id, 1);
                        sqliteHelper.updateClusterTable(sharedPrefHelper.getString("cluster_no", ""));

                        //send audio here
                        if(sharedPrefHelper.getBoolean("isRecording", false)==true) {
                            Uri imageUri = Uri.parse(AudioSavePathInDevice);
                            File file = new File(imageUri.getPath());
                            RequestBody fileReqBody = RequestBody.create(MediaType.parse("Image/*"), file);
                            part = MultipartBody.Part.createFormData("audio_name", file.getName(), fileReqBody);
                            Log.e("audio_params-", "audio_params- "
                                    + "\n" + sharedPrefHelper.getString("user_id", "")
                                    + "\n" + survey_id + "\n" + survey_data_monitoring_id + "\n" + part);

                            ApiClient.getClient().create(BARC_API.class).sendAudio(sharedPrefHelper.getString("user_id", ""), survey_id, survey_data_monitoring_id, part).enqueue(new Callback<JsonObject>() {
                                @Override
                                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                    try {
                                        AlertDialogClass.dismissProgressDialog();
                                        JSONObject jsonObject = new JSONObject(response.body().toString());
                                        Log.e("audio-upload", jsonObject.toString());
                                        String success = jsonObject.optString("success");
                                        String message = jsonObject.optString("message");
                                        String name = jsonObject.optString("name");
                                        String file_status = jsonObject.optString("file_status");
                                        if (success.equalsIgnoreCase("1")) {
                                            Intent intentSurveyActivity1 = new Intent(context, NextSurvey.class);
                                            startActivity(intentSurveyActivity1);
                                            finish();
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        AlertDialogClass.dismissProgressDialog();
                                        btn_next.setEnabled(true);
                                    }
                                }

                                @Override
                                public void onFailure(Call<JsonObject> call, Throwable t) {
                                    AlertDialogClass.dismissProgressDialog();
                                    btn_next.setEnabled(true);
                                }
                            });
                        }else{
                            AlertDialogClass.dismissProgressDialog();
                            Intent intentSurveyActivity1 = new Intent(context, NextSurvey.class);
                            startActivity(intentSurveyActivity1);
                            finish();
                        }
                    } else {
                        AlertDialogClass.dismissProgressDialog();
                        CommonClass.showPopupForNoInternet(context);
                        btn_next.setEnabled(true);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                //Toast.makeText(context, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                AlertDialogClass.dismissProgressDialog();
                btn_next.setEnabled(true);
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
               sharedPrefHelper.setString("screen_id",screen_id);
               Log.e(TAG, "questionsPopulate>>> "+screen_id);
               jsonArrayQuestions = jsonObjectScreen.getJSONArray("questions");
               for (int i = 0; i < jsonArrayQuestions.length(); i++) {
                   JSONObject jsonObjectQuesType=jsonArrayQuestions.getJSONObject(i);
                   String questionID=jsonObjectQuesType.getString("question_id");
                   //if question_type->1 then its Edit Text
                   if (jsonObjectQuesType.getString("question_type").equals("1")) {
                       TextView txtLabel = new TextView(this);
                       EditText editText=new EditText(this);
                       editText.setId(Integer.parseInt(jsonObjectQuesType.getString("question_id")));
                       editText.setTextSize(12);
                       //add edit text for sur-name
                       /*EditText editTextSurname=new EditText(this);
                       editText.setId(Integer.parseInt(jsonObjectQuesType.getString("question_id")));
                       editTextSurname.setTextSize(12);
                       editTextSurname.setHint("Please enter surname");
                       editTextSurname.setVisibility(View.GONE);*/
                       //add terminate button below edit text
                       LinearLayout layout2 = new LinearLayout(context);
                       LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                       params.gravity = Gravity.RIGHT;
                       params.weight = 1.0f;
                       layout2.setLayoutParams(params);
                       layout2.setOrientation(LinearLayout.HORIZONTAL);
                       //add terminate button below edit text
                       Button buttonTerminate=new Button(this);
                       buttonTerminate.setLayoutParams(params);
                       buttonTerminate.setText("Terminate");
                       buttonTerminate.setTextColor(Color.parseColor("#FFFFFF"));
                       buttonTerminate.setBackgroundResource(R.drawable.btn_terminate_background);
                       buttonTerminate.setAllCaps(false);
                       buttonTerminate.setVisibility(View.GONE);

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
                       } else if (jsonObjectQuesType.getString("field_name").equals("Town_Village_Class") || jsonObjectQuesType.getString("field_name").equals("BI_Weighting_town_class")) {
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
                       if (jsonObjectQuesType.getString("field_name").equals("BI_Weighting_town_class")){
                           editText.setText(sharedPrefHelper.getString("BI_Weighting_town_class", ""));
                       }
                       else if (jsonObjectQuesType.getString("field_name").equals("interview_number")) {
                           editText.setText(survey_id);
                       }
                       else if (jsonObjectQuesType.getString("question_id").equals("101")) {
                               editText.setText(sharedPrefHelper.getString("HH_Name",""));
                       }
                       else if (jsonObjectQuesType.getString("question_id").equals("102")) {
                           if (!sharedPrefHelper.getString("CWE_Name", "").equals("")) {
                               editText.setText(sharedPrefHelper.getString("CWE_Name", ""));
                           }else{
                               editText.setText(sharedPrefHelper.getString("name", ""));
                           }
                       }
                       if(jsonObjectQuesType.getString("question_input_type").equals("2")){
                           editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
                       }else{
                           editText.setInputType(InputType.TYPE_CLASS_TEXT);
                       }

                       if (jsonObjectQuesType.getString("question_id").equals("30")){
                           //btn_previous.setEnabled(false);
                           buttonTerminate.setVisibility(View.VISIBLE);
                           buttonTerminate.setOnClickListener(new View.OnClickListener() {
                               @Override
                               public void onClick(View view) {
                                   showPopupForTerminateSurveyQuestionWise("1", "Ineligible NCCS");
                               }
                           });
                       }else {
                         //  btn_previous.setEnabled(true);
                       }
                       if (jsonObjectQuesType.getString("question_id").equals("46")) {
                           editText.setHint("Please enter first name");
                           //editTextSurname.setVisibility(View.VISIBLE);
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
                                           limit = 12;
                                           if (!editText.getText().toString().trim().equals("")) {
                                               //editFieldValues=editText.getText().toString().trim();
                                               if (value > limit) {
                                                   //if family member greater than 12 terminate popup
                                                   Toast.makeText(context, "Household member can't be greater than 12", Toast.LENGTH_SHORT).show();
                                                   showPopupForTerminateSurveyQuestionWise("2", "Ineligible Household Size");
                                               } else if (value == 0) {
                                                   Toast.makeText(context, "Household member can't be 0", Toast.LENGTH_SHORT).show();
                                               }
                                           }
                                       } else {
                                           limit = 10;
                                           if (!editText.getText().toString().trim().equals("")) {
                                               if (value > limit) {
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
                       if (jsonObjectQuesType.getString("question_id").equals("32")){
                           buttonTerminate.setVisibility(View.VISIBLE);
                           buttonTerminate.setOnClickListener(new View.OnClickListener() {
                               @Override
                               public void onClick(View view) {
                                   showPopupForTerminateSurveyQuestionWise("2", "Ineligible Household Size");
                               }
                           });
                       }
                       if (jsonObjectQuesType.getString("question_id").equals("94")){
                           String town_village_class=sharedPrefHelper.getString("town_village_class","");
                           if(!town_village_class.equalsIgnoreCase("Rural")){
                               txtLabel.setVisibility(View.GONE);
                               editText.setVisibility(View.GONE);
                           }
                       }
                       if (jsonObjectQuesType.getString("question_id").equals("93") || jsonObjectQuesType.getString("question_id").equals("95") ){
                           String town_village_class=sharedPrefHelper.getString("town_village_class","");
                           if(!town_village_class.equalsIgnoreCase("Urban")){
                               txtLabel.setVisibility(View.GONE);
                               editText.setVisibility(View.GONE);
                           }
                       }
                       if (questionID.equals("99")){
                           editText.addTextChangedListener(new TextWatcher() {
                               @Override
                               public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                               }

                               @Override
                               public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                   int limit=6;
                                   if(editText.getText().toString().trim().length()>0) {
                                       int value = Integer.parseInt(editText.getText().toString().trim());
                                           if (!editText.getText().toString().trim().equals("")) {
                                               if (String.valueOf(value).length() > limit) {
                                                   Toast.makeText(context, "Pincode can't be greater than 6", Toast.LENGTH_SHORT).show();
                                               } else if (value == 0) {
                                                   Toast.makeText(context, "Pincode can't start from 0", Toast.LENGTH_SHORT).show();
                                               }
                                           }
                                       //}
                                   }
                               }

                               @Override
                               public void afterTextChanged(Editable editable) {

                               }
                           });
                       }
                       /*if (questionID.equals("109")){
                           int maxLength=10;
                           editText.addTextChangedListener(new LimitTextWatcher(editText, maxLength, new LimitTextWatcher.IF_callback() {
                               @Override
                               public void callback(int left) {
                                   if(left <= 0) {
                                       Toast.makeText(context, "Please enter correct value of Landline no.", Toast.LENGTH_SHORT).show();
                                   }
                               }
                           }));
                       }*/
                       if (questionID.equals("90")){
                           String pp_number=sharedPrefHelper.getString("pp_number", "");
                           if (pp_number.equals("2")){
                               txtLabel.setVisibility(View.GONE);
                               editText.setVisibility(View.GONE);
                           }
                       }
                       editText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(Integer.parseInt(jsonObjectQuesType.getString("max_limit")))});
                       if(jsonObjectQuesType.getString("pre_field").equals("1")){
                           if(jsonObjectQuesType.getString("question_id").equals("13") && sharedPrefHelper.getString("isReplacementTown","0").equals("1")){
                               editText.setEnabled(true);
                           }else
                           editText.setEnabled(false);
                       }
                       if((back_status==true || screen_type.equals("survey_list")) && answerModelList.size()>startPosition){
                           editText.setText(answerModelList.get(startPosition).getOption_value());
                       }
                       if (jsonObjectQuesType.getString("field_name").equals("nccs_hh")) {
                           editText.setText(sharedPrefHelper.getString("status_nccs_hh", ""));
                       }
                       else if (jsonObjectQuesType.getString("field_name").equals("nccs_matrix")) {
                           //editText.setText(sharedPrefHelper.getString("nccs_matrix", ""));
                           //String status=sqliteHelper.getNCCMatrix2(answerModelList.get(startPosition-2).getOption_id(),answerModelList.get(startPosition-1).getOption_id(),sharedPrefHelper.getString("nccs_matrix", ""));
                           String status=sqliteHelper.getNCCMatrix2(answerModelList.get(startPosition-2).getOption_id(),answerModelList.get(startPosition-1).getOption_id());
                           //sharedPrefHelper.setString("nccs_matrix", "A");
                           //String status=sharedPrefHelper.getString("nccs_matrix", "");
                           if(!status.equals(""))
                               editText.setText(status);
                           sharedPrefHelper.setString("status_nccs_hh", status);
                           /*else
                               setTerminattion("NCCS Calculator");*/
                       }
                       String description=jsonObjectQuesType.getString("question_name");
                       //description=description.replaceAll("$name",sharedPrefHelper.getString("name","Ram"));
                       description=description.replaceAll("\\$name",sharedPrefHelper.getString("name",""));
                       txtLabel.setText(description);
                       txtLabel.setTypeface(null, Typeface.BOLD);
                       txtLabel.setTextSize(14);
                       if(questionID.equals("122")){
                           editText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(50)});
                           txtLabel.setVisibility(View.GONE);
                           editText.setVisibility(View.GONE);
                           editText.setText("ISEC CWE");
                       }
                       startPosition++;
                       endPosition++;
                       ll_parent.addView(txtLabel);
                       ll_parent.addView(editText);
                       layout2.addView(buttonTerminate);
                       ll_parent.addView(layout2);
                       //ll_parent.addView(buttonTerminate);
                       //onAddEditField(jsonObjectQuesType);
                   }
                   //if question_type->2 then its Radio Button
                   else if (jsonObjectQuesType.getString("question_type").equals("2")) {
                       TextView txtLabel = new TextView(this);
                       txtLabel.setId(Integer.parseInt(jsonObjectQuesType.getString("question_id")));
                       String description=jsonObjectQuesType.getString("question_name");
                       description=description.replaceAll("$name",sharedPrefHelper.getString("name","Ram"));
                       txtLabel.setText(description);
                       txtLabel.setTextSize(14);
                       //add terminate button below edit text
                       LinearLayout layout2 = new LinearLayout(context);
                       layout2.setId(Integer.parseInt(jsonObjectQuesType.getString("question_id")));
                       LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                       params.gravity = Gravity.RIGHT;
                       params.weight = 1.0f;
                       layout2.setLayoutParams(params);
                       layout2.setOrientation(LinearLayout.HORIZONTAL);
                       //add terminate button below edit text
                       Button buttonTerminate=new Button(this);
                       buttonTerminate.setLayoutParams(params);
                       buttonTerminate.setText("Terminate");
                       buttonTerminate.setTextColor(Color.parseColor("#FFFFFF"));
                       buttonTerminate.setBackgroundResource(R.drawable.btn_terminate_background);
                       buttonTerminate.setAllCaps(false);
                       buttonTerminate.setVisibility(View.GONE);
                       buttonTerminate.setId(Integer.parseInt(jsonObjectQuesType.getString("question_id")));

                       txtLabel.setTypeface(null, Typeface.BOLD);
                       ll_parent.addView(txtLabel);
                       layout2.addView(buttonTerminate);
                       JSONArray jsonArrayOptions = jsonObjectQuesType.getJSONArray("question_options");
                       RadioGroup radioGroup=new RadioGroup(this);
                       radioGroup.setId(Integer.parseInt(jsonObjectQuesType.getString("question_id")));
                       if(jsonObjectQuesType.getString("question_id").equals("114") && !sharedPrefHelper.getString("rq3e_selected","").contains("1")){
                           txtLabel.setVisibility(View.GONE);
                           radioGroup.setVisibility(View.GONE);
                           layout2.setVisibility(View.GONE);
                       }
                       else if(jsonObjectQuesType.getString("question_id").equals("115") && !sharedPrefHelper.getString("rq3e_selected","").contains("1")){
                           txtLabel.setVisibility(View.GONE);
                           radioGroup.setVisibility(View.GONE);
                           layout2.setVisibility(View.GONE);
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
                           if(jsonObjectQuesType.getString("question_id").equals("22") && jsonObjectOptionValues.getString("option_id").equals(sharedPrefHelper.getString("address_type", "0"))){
                               radioButton.setChecked(true);
                           }
                           if((back_status==true || screen_type.equals("survey_list")) && answerModelList.size()>startPosition){
                               if(answerModelList.get(startPosition).getOption_id().equals(jsonObjectOptionValues.getString("option_id"))){
                                   radioButton.setChecked(true);
                                   if(jsonObjectQuesType.getString("question_id").equals("111")){
                                       sharedPrefHelper.setString("isReplacementTown",jsonObjectOptionValues.getString("option_id"));
                                   }
                               }
                               if(jsonObjectQuesType.getString("question_id").equals("23")){
                                   if(answerModelList.get(startPosition).getOption_id().equals(jsonObjectOptionValues.getString("option_id"))){
                                       sharedPrefHelper.setString("CWE_Status",jsonObjectOptionValues.getString("option_id"));
                                       if(jsonObjectOptionValues.getString("option_id").equals("1")){
                                           try{
                                               jsonArrayScreen=jsonQuestions.getJSONArray("group").getJSONObject(0).getJSONArray("screens");
                                               JSONArray list = new JSONArray();
                                               int len = jsonArrayScreen.length();
                                               if (jsonArrayScreen != null) {
                                                   for (int ii=0;ii<len;ii++)
                                                   {
                                                       if (ii != 11 && ii != 12)
                                                       {
                                                           list.put(jsonArrayScreen.get(ii));
                                                       }
                                                   }
                                               }
                                               if(list.length()>=24) {
                                                   jsonArrayScreen = list;
                                               }
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
                               }
                               else if(jsonObjectQuesType.getString("question_id").equals("24")){
                                   //start recording here
                                   if(answerModelList.get(startPosition).getOption_id().equals(jsonObjectOptionValues.getString("option_id"))) {
                                       if (jsonObjectOptionValues.getString("option_id").equals("1")) {
                                           sharedPrefHelper.setBoolean("isRecording", true);
                                           iv_recording.setVisibility(View.VISIBLE);
                                           startRecordingAnimation(1);
                                           startRecording();
                                       } else {
                                           iv_recording.setVisibility(View.GONE);
                                           startRecordingAnimation(2);
                                           //iv_recording.startAnimation(null);
                                           sharedPrefHelper.setBoolean("isRecording", false);
                                           stopRecording();
                                       }
                                   }
                               }
                               else if(jsonObjectQuesType.getString("question_id").equals("83")){
                                   String type_of_mobile=sharedPrefHelper.getString("type_of_mobile","");//01-02-2021
                                   if(answerModelList.get(startPosition).getOption_id().equals(jsonObjectOptionValues.getString("option_id"))) {
                                       //sharedPrefHelper.setString("2100f_radio_ids",jsonObjectOptionValues.getString("option_id"));
                                   }
                                   if(type_of_mobile.equals("4")){
                                       txtLabel.setVisibility(View.GONE);
                                       radioButton.setVisibility(View.GONE);
                                   }
                                   /*else if(type_of_mobile.equals("4")&&jsonObjectOptionValues.getString("option_id").equals("1")){//01-02-2021
                                       radioButton.setEnabled(false);
                                   }
                                   else if(type_of_mobile.equals("4")&&jsonObjectOptionValues.getString("option_id").equals("2")){
                                       radioButton.setChecked(true);
                                       radioButton.setEnabled(false);
                                   }
                                   else if(type_of_mobile.equals("4")&&jsonObjectOptionValues.getString("option_id").equals("9")){
                                       radioButton.setEnabled(false);
                                   }*/
                               }
                           }

                           if(jsonObjectQuesType.getString("question_id").equals("83")){
                               String type_of_mobile=sharedPrefHelper.getString("type_of_mobile","");
                               if(type_of_mobile.equals("4")) {
                                   txtLabel.setVisibility(View.GONE);
                                   radioButton.setVisibility(View.GONE);
                               }
                           }
                           if(jsonObjectQuesType.getString("pre_field").equals("1")){
                               radioButton.setEnabled(false);
                           }
                           else if(jsonObjectQuesType.getString("question_id").equals("76")){
                               String tenement_type=sharedPrefHelper.getString("tenement_type","");
                               if(tenement_type.equals("1") && jsonObjectOptionValues.getString("option_id").equals("1")){
                                   radioButton.setChecked(true);
                               }
                           }
                           else if(jsonObjectQuesType.getString("question_id").equals("111")){
                               String replacementTown=sharedPrefHelper.getString("replacementTown","");
                               if(replacementTown.equals("2")){
                                   radioButton.setChecked(true);
                               }
                           }
                           else if(jsonObjectQuesType.getString("question_id").equals("82")){
                               String type_of_mobile=sharedPrefHelper.getString("type_of_mobile","");
                               if (type_of_mobile.contains("1")){
                                   txtLabel.setVisibility(View.GONE);
                                   radioButton.setVisibility(View.GONE);
                               }if (type_of_mobile.contains("4")){
                                   txtLabel.setVisibility(View.GONE);
                                   radioButton.setVisibility(View.GONE);
                               }if(type_of_mobile.contains("1")&&type_of_mobile.contains("4")){
                                   txtLabel.setVisibility(View.GONE);
                                   radioButton.setVisibility(View.GONE);
                               }
                           }
                           /**
                           *
                                //coded for question RQ3f and RQ3g default should be hide
                                //but if we choose option 1 in RQ3e than they should be visible
                            *
                            **/
                           else if(questionID.equals("114")||questionID.equals("115")){
                               /*txtLabel.setVisibility(View.GONE);
                               radioButton.setVisibility(View.GONE);*/
                               buttonTerminate.setVisibility(View.VISIBLE);
                               buttonTerminate.setOnClickListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View view) {
                                       showPopupForTerminateSurveyQuestionWise("5", "Ineligible DTH Connection (Free/Paid)");
                                   }
                               });
                           }
                           else if (jsonObjectQuesType.get("question_id").equals("116")){
                               buttonTerminate.setVisibility(View.VISIBLE);
                               buttonTerminate.setOnClickListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View view) {
                                       showPopupForTerminateSurveyQuestionWise("3", "Ineligible Highest Education");
                                   }
                               });
                           }
                           if (radioGroup != null) {
                               if(questionID.equals("53")){
                                   String selectedOptionsSpinner=sharedPrefHelper.getString("CWE_Status","");
                                   if(selectedOptionsSpinner.equals("2")){
                                       String CWE_Living_status=sharedPrefHelper.getString("CWE_Living_status", "");
                                       if(CWE_Living_status.equals("4")){
                                           if(jsonObjectOptionValues.getString("option_id").equals("3")){
                                               radioGroup.addView(radioButton);
                                               radioButton.setChecked(true);
                                           }
                                       }else{
                                           if(jsonObjectOptionValues.getString("option_id").equals("2")){
                                               radioGroup.addView(radioButton);
                                               radioButton.setChecked(true);
                                           }
                                       }
                                   }else if(selectedOptionsSpinner.equals("1")){
                                       if(jsonObjectOptionValues.getString("option_id").equals("1")){
                                           radioGroup.addView(radioButton);
                                           radioButton.setChecked(true);
                                       }
                                   }
                                   txtLabel.setVisibility(View.GONE);
                                   radioButton.setVisibility(View.GONE);
                               }else{
                                   radioGroup.addView(radioButton);
                               }
                           }
                       }
                       startPosition++;
                       endPosition++;
                       ll_parent.addView(radioGroup);
                       ll_parent.addView(layout2);
                       //ll_parent.addView(buttonTerminate);
                       radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
                       {
                           @Override
                           public void onCheckedChanged(RadioGroup group, int checkedId) {
                               RadioButton rb=(RadioButton)findViewById(checkedId);
                               String radioButtonText=rb.getText().toString().trim();
                               String rbTag=rb.getTag().toString();
                               int sepPos = rbTag.indexOf("^");
                               String id=rbTag.substring(sepPos+1);
                               int sepPosID = rbTag.indexOf("^");
                               String radioID = rbTag.substring(0, sepPosID);
                               String iddd=String.valueOf(rb.getId());
                               int groupID=group.getId();
                               if (group.getId()==25){
                                   if (rb.isChecked()){
                                       //if(id.equals("1")){
                                           //setTerminattion(id);
                                       if (radioID.equals("1")||radioID.equals("2")||radioID.equals("3")
                                           ||radioID.equals("4")||radioID.equals("5")||radioID.equals("6")
                                           ||radioID.equals("7")) {
                                           showPopupForTerminateSurveyOnRadio(radioID,radioButtonText,radioGroup,groupID);
                                       }
                                           //Toast.makeText(context,"Termination true"+rb.getText()+"group.getId()"+group.getId(),Toast.LENGTH_LONG).show();
                                       //}
                                   }
                               }
                               if (group.getId()==26){
                                   if (rb.isChecked()){
                                       //if(id.equals("1")){
                                       //setTerminattion(id);
                                       if (radioID.equals("2")) {
                                           //showPopupForTerminateSurveyOnRadio(radioID,radioButtonText,radioGroup,groupID);
                                           showPopupForTerminateSurveyOnRadio(radioID,"No-Does your home have a working TV",radioGroup,groupID);
                                       }
                                       //Toast.makeText(context,"Termination true"+rb.getText()+"group.getId()"+group.getId(),Toast.LENGTH_LONG).show();
                                       //}
                                   }
                               }
                               if (group.getId()==27){
                                   if (rb.isChecked()){
                                       //if(id.equals("1")){
                                       //setTerminattion(id);
                                       if (radioID.equals("2")) {
                                           //showPopupForTerminateSurveyOnRadio(radioID,radioButtonText,radioGroup,groupID);
                                           showPopupForTerminateSurveyOnRadio(radioID,"No-Does you have a DTH/Cable Connection",radioGroup,groupID);
                                       }
                                       //Toast.makeText(context,"Termination true"+rb.getText()+"group.getId()"+group.getId(),Toast.LENGTH_LONG).show();
                                       //}
                                   }
                               }
                               if(group.getId()==23){
                                   if(rb.isChecked()){
                                       if(sharedPrefHelper.getString("CWE_Status","").equals(radioID) || sharedPrefHelper.getString("CWE_Status","").equals("")){
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
                                                       if(list.length()>=24) {
                                                           jsonArrayScreen = list;
                                                       }
                                                       totalScreen=jsonArrayScreen.length();
                                                   }
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
                                       }else{
                                           showPopupForDataEraseRadio(group.getId(),radioGroup,radioID);
                                       }
                                   }
                               }
                               else if(group.getId()==24){
                                   //start recording here
                                   if (rb.isChecked()) {
                                       if (radioID.equals("1")) {
                                           sharedPrefHelper.setBoolean("isRecording", true);
                                           iv_recording.setVisibility(View.VISIBLE);
                                           startRecordingAnimation(1);
                                           startRecording();
                                       } else if (radioID.equals("2")) {
                                           iv_recording.setVisibility(View.GONE);
                                           startRecordingAnimation(2);
                                           //iv_recording.startAnimation(null);
                                           sharedPrefHelper.setBoolean("isRecording", false);
                                           stopRecording();
                                       } else {
                                           iv_recording.setVisibility(View.GONE);
                                           startRecordingAnimation(2);
                                           //iv_recording.startAnimation(null);
                                           sharedPrefHelper.setBoolean("isRecording", false);
                                           stopRecording();
                                           //showPopupForTerminateSurveyOnRadio(radioID,radioButtonText,radioGroup,groupID);
                                           showPopupForTerminateSurveyOnRadio(radioID,"No - Do not wish to continue the interview (with or without audio recording)",radioGroup,groupID);
                                       }
                                   }
                               }
                               //hide/show question condition for DTH connection free/paid
                               /*else if(group.getId()==115){
                                   if(questionID.equals("115")) {
                                       for (int i = 0; i < ll_parent.getChildCount(); i++) {
                                           final View childView = ll_parent.getChildAt(i);
                                           if (childView instanceof TextView) {
                                               TextView textView1 = (TextView) childView;
                                               if (String.valueOf(textView1.getId()).equals("115"))
                                                   if (radioID.equals("1")) {
                                                       textView1.setVisibility(View.GONE);
                                                   } else if (radioID.equals("2")) {
                                                       textView1.setVisibility(View.VISIBLE);
                                                   }
                                           }
                                           else if (childView instanceof RadioGroup) {
                                               RadioGroup radioGroup1 = (RadioGroup) childView;
                                               if (String.valueOf(radioGroup1.getId()).equals("115")) {
                                                   if (radioID.equals("1")) {
                                                       radioGroup1.setVisibility(View.GONE);
                                                   } else if (radioID.equals("2")) {
                                                       radioGroup1.setVisibility(View.VISIBLE);
                                                   }
                                               }
                                           }
                                           if (childView instanceof LinearLayout) {
                                               LinearLayout LinearLayout1 = (LinearLayout) childView;
                                               if (String.valueOf(LinearLayout1.getId()).equals("115")){
                                                   if (radioID.equals("1")) {
                                                       LinearLayout1.setVisibility(View.VISIBLE);
                                                   }else{
                                                       LinearLayout1.setVisibility(View.GONE);
                                                   }
                                               }
                                           }
                                       }
                                   }
                               }*/
                               else if(group.getId()==101){
                                   if (radioID.equals("1")){
                                       //sharedPrefHelper.setString("2100f_radio_ids", radioID);
                                   }
                               }
                               else if(group.getId()==111){
                                   for (int i = 0; i < ll_parent.getChildCount(); i++) {
                                       final View childView = ll_parent.getChildAt(i);
                                       if (childView instanceof EditText) {
                                           EditText editText = (EditText) childView;
                                           if (String.valueOf(editText.getId()).equals("13")) {
                                               if (radioID.equals("1")) {
                                                   editText.setEnabled(true);
                                               } else {
                                                   editText.setEnabled(false);
                                               }
                                               sharedPrefHelper.setString("isReplacementTown",radioID);
                                           }
                                       }
                                   }
                               }
                               else if(group.getId()==83){
                                   /*for (int i = 0; i < ll_parent.getChildCount(); i++) {
                                       final View childView = ll_parent.getChildAt(i);
                                       sharedPrefHelper.setString("2100f_radio_ids",radioID);
                                       if (childView instanceof Spinner) {
                                           Spinner spinner = (Spinner) childView;
                                           if(String.valueOf(spinner.getId()).equals("84")){
                                               if (radioID.equals("1")){
                                                   spinner.setVisibility(View.VISIBLE);
                                               }else{
                                                   spinner.setVisibility(View.GONE);
                                               }
                                           }
                                       }else if (childView instanceof TextView) {
                                           TextView textView = (TextView) childView;
                                           if(String.valueOf(textView.getId()).equals("84")){
                                               if (radioID.equals("1")){
                                                   textView.setVisibility(View.VISIBLE);
                                               }else{
                                                   textView.setVisibility(View.GONE);
                                               }
                                           }
                                       }
                                   }*/
                               }
                           }
                       });
                       //onAddRadioButton(jsonObjectQuesType);
                   }
                   //if question_type->3 then its Check Box
                   else if (jsonObjectQuesType.getString("question_type").equals("3")) {
                       TextView txtLabel = new TextView(this);
                       String description=jsonObjectQuesType.getString("question_name");
                       description=description.replaceAll("$name",sharedPrefHelper.getString("name",""));
                       txtLabel.setText(description);
                       txtLabel.setTextSize(14);
                       //add terminate button below edit text
                       LinearLayout layout2 = new LinearLayout(context);
                       LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                       params.gravity = Gravity.RIGHT;
                       params.weight = 1.0f;
                       layout2.setLayoutParams(params);
                       layout2.setOrientation(LinearLayout.HORIZONTAL);
                       //add terminate button below edit text
                       Button buttonTerminate=new Button(this);
                       buttonTerminate.setLayoutParams(params);
                       buttonTerminate.setText("Terminate");
                       buttonTerminate.setTextColor(Color.parseColor("#FFFFFF"));
                       buttonTerminate.setBackgroundResource(R.drawable.btn_terminate_background);
                       buttonTerminate.setAllCaps(false);
                       buttonTerminate.setVisibility(View.GONE);

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
                       if(questionID.equals("78")){
                           buttonTerminate.setVisibility(View.VISIBLE);
                           buttonTerminate.setOnClickListener(new View.OnClickListener() {
                               @Override
                               public void onClick(View view) {
                                   showPopupForTerminateSurveyQuestionWise("6", "Ineligible Language");
                               }
                           });
                       }
                       if(questionID.equals("113")){
                           buttonTerminate.setVisibility(View.VISIBLE);
                           buttonTerminate.setOnClickListener(new View.OnClickListener() {
                               @Override
                               public void onClick(View view) {
                                   showPopupForTerminateSurveyQuestionWise("6", "Ineligible MOSR");
                               }
                           });
                       }

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
                           if(!town_village_class.equalsIgnoreCase("Rural")){
                               txtLabel.setVisibility(View.GONE);
                               linearLayoutCheckbox.setVisibility(View.GONE);
                           }
                       }
                       int internetUse=0;
                      if(questionID.equals("44")){
                           Gson gson = new Gson();
                           java.lang.reflect.Type type = new TypeToken<HashMap<String, String>>(){}.getType();
                           HashMap<Integer, String> testHashMap2 = gson.fromJson(sharedPrefHelper.getString("internetUse",""), type);
                           ///JSONObject obj=new JSONObject(sharedPrefHelper.setString("householdMember","objMember.toString()"););
                           if (testHashMap2!=null) {
                               Map<Integer, String> treeMapName = new TreeMap<>(testHashMap2);

                               for (Map.Entry<Integer, String> entry : treeMapName.entrySet()) {
                                   if (entry.getValue().equals("1")) {
                                       internetUse = 1;
                                       break;
                                   }else if(entry.getValue().equals("2")){//30-01-2021
                                       internetUse = 2;
                                       //break;
                                   }//
                               }
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
                           checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                               @Override
                               public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                   if (checkBox.isChecked()){
                                       try{
                                           String checkBoxText=checkBox.getText().toString().trim();
                                           String iddd=String.valueOf(checkBox.getId());
                                           if (questionID.equals("113")) {
                                               if (checkedIdForCheckBox.equals("")) {
                                                   checkedIdForCheckBox = jsonObject1.getString("option_id");
                                               }else{
                                                   if (checkedIdForCheckBox!=null){
                                                       checkedIdForCheckBox = checkedIdForCheckBox + ", " + jsonObject1.getString("option_id");
                                                   }
                                               }
                                               if(iddd.equals("1")) {
                                                   for (int i = 0; i < ll_parent.getChildCount(); i++) {
                                                       final View childView = ll_parent.getChildAt(i);
                                                       if (childView instanceof TextView) {
                                                           TextView textView1 = (TextView) childView;
                                                           if (String.valueOf(textView1.getId()).equals("114"))
                                                               textView1.setVisibility(View.VISIBLE);
                                                       } else if (childView instanceof RadioGroup) {
                                                           RadioGroup radioGroup1 = (RadioGroup) childView;
                                                           if (String.valueOf(radioGroup1.getId()).equals("114")) {
                                                               radioGroup1.setVisibility(View.VISIBLE);
                                                           }
                                                       }
                                                       if (childView instanceof LinearLayout) {
                                                           LinearLayout LinearLayout1 = (LinearLayout) childView;
                                                           if (String.valueOf(LinearLayout1.getId()).equals("114"))
                                                               LinearLayout1.setVisibility(View.VISIBLE);
                                                       }
                                                   }
                                               }
                                               else if(iddd.equals("4"))
                                               {
                                                   //btn_next.setEnabled(false);
                                               }
                                           }
                                       }catch (Exception e){
                                           e.printStackTrace();
                                       }
                                   }else {
                                       if (questionID.equals("113")) {
                                           String checkBoxText = checkBox.getText().toString().trim();
                                           String iddd = String.valueOf(checkBox.getId());
                                           if (iddd.equals("1")) {
                                               for (int i = 0; i < ll_parent.getChildCount(); i++) {
                                                   final View childView = ll_parent.getChildAt(i);
                                                   if (childView instanceof TextView) {
                                                       TextView textView1 = (TextView) childView;
                                                       if (String.valueOf(textView1.getId()).equals("114") || String.valueOf(textView1.getId()).equals("115"))
                                                           textView1.setVisibility(View.GONE);
                                                   } else if (childView instanceof RadioGroup) {
                                                       RadioGroup radioGroup1 = (RadioGroup) childView;
                                                       if (String.valueOf(radioGroup1.getId()).equals("114") || String.valueOf(radioGroup1.getId()).equals("115")) {
                                                           radioGroup1.setVisibility(View.GONE);
                                                       }
                                                   }
                                                   if (childView instanceof LinearLayout) {
                                                       LinearLayout LinearLayout1 = (LinearLayout) childView;
                                                       if (String.valueOf(LinearLayout1.getId()).equals("114") || String.valueOf(LinearLayout1.getId()).equals("115"))
                                                           LinearLayout1.setVisibility(View.GONE);
                                                   }
                                               }
                                           } else if (iddd.equals("4")) {
                                               btn_next.setEnabled(true);
                                           }
                                       }
                                   }
                               }
                           });
                           /*if (questionID.equals("113")){
                                   btn_previous.setEnabled(false);
                                   buttonTerminate.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View view) {
                                           showPopupForTerminateSurveyQuestionWise("5", "Ineligible DTH Connection (Free/Paid)");
                                       }
                                   });
                           }else {
                               btn_previous.setEnabled(true);
                           }*/
                           if(questionID.equals("54")){
                               checkBox.setEnabled(false);
                           }
                           else if(questionID.equals("44")){
                                if(jsonObject1.getString("option_id").equals("6") && internetUse==1){
                                    checkBox.setVisibility(View.GONE);
                                    checkBox.setChecked(false);//30-01-2021
                                }else if(jsonObject1.getString("option_id").equals("6") && internetUse==2){
                                    checkBox.setVisibility(View.VISIBLE);
                                    checkBox.setChecked(true);
                                    checkBox.setEnabled(false);
                                }else if(jsonObject1.getString("option_id").equals("1") && internetUse==2){
                                    checkBox.setChecked(false);
                                    checkBox.setEnabled(false);
                                }else if(jsonObject1.getString("option_id").equals("2") && internetUse==2){
                                    checkBox.setChecked(false);
                                    checkBox.setEnabled(false);
                                }else if(jsonObject1.getString("option_id").equals("3") && internetUse==2){
                                    checkBox.setChecked(false);
                                    checkBox.setEnabled(false);
                                }else if(jsonObject1.getString("option_id").equals("4") && internetUse==2){
                                    checkBox.setChecked(false);
                                    checkBox.setEnabled(false);
                                }else if(jsonObject1.getString("option_id").equals("5") && internetUse==2){
                                    checkBox.setChecked(false);
                                    checkBox.setEnabled(false);
                                }//
                           }
                           if((back_status==true || screen_type.equals("survey_list")) && answerModelList.size()>startPosition){
                               selectedOptions=answerModelList.get(startPosition).getOption_id();
                               String[] arraySelectedOptions = selectedOptions.split(",");
                               boolean contains = Arrays.asList(arraySelectedOptions).contains(jsonObject1.getString("option_id"));
                               if(questionID.equals("44")){
                                   if(internetUse==2){
                                       if(jsonObject1.getString("option_id").equals("6"))
                                       checkBox.setChecked(true);
                                   }else{
                                        if(contains && !jsonObject1.getString("option_id").equals("6")){
                                           checkBox.setChecked(true);
                                       }
                                   }
                               }
                               else if((questionID.equals("54"))){
                                   selectedOptions=sharedPrefHelper.getString("selectedDurables","");
                                   String[] arraySelectedOptionsn = selectedOptions.split(",");
                                   boolean containsn = Arrays.asList(arraySelectedOptionsn).contains(jsonObject1.getString("option_id"));
                                   if(containsn){
                                       checkBox.setChecked(true);
                                   }
                               }
                               else if(contains){
                                   if(questionID.equals("113")){
                                       sharedPrefHelper.setString("rq3e_selected",selectedOptions);
                                   }
                                   checkBox.setChecked(true);
                               }
                           }
                           if((questionID.equals("54"))){
                               selectedOptions=sharedPrefHelper.getString("selectedDurables","");
                               String[] arraySelectedOptionsn = selectedOptions.split(",");
                               boolean containsn = Arrays.asList(arraySelectedOptionsn).contains(jsonObject1.getString("option_id"));
                               if(containsn){
                                   checkBox.setChecked(true);
                               }
                           }
                           row.addView(checkBox);
                           linearLayoutCheckbox.addView(row);
                       }
                       startPosition++;
                       endPosition++;
                       ll_parent.addView(linearLayoutCheckbox);
                       layout2.addView(buttonTerminate);
                       ll_parent.addView(layout2);
                       //ll_parent.addView(buttonTerminate);
                       // onAddCheckBox(jsonObjectQuesType);
                   }
                   //if question_type->4 then its Spinner
                   else if (jsonObjectQuesType.getString("question_type").equals("4")) {
                       TextView txtLabel = new TextView(this);
                       String description=jsonObjectQuesType.getString("question_name");
                       description=description.replaceAll("\\$name",sharedPrefHelper.getString("name",""));
                       txtLabel.setText(description);
                       txtLabel.setTextSize(14);
                       LinearLayout layout2 = new LinearLayout(context);
                       LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                       params.gravity = Gravity.RIGHT;
                       params.weight = 1.0f;
                       layout2.setLayoutParams(params);
                       layout2.setOrientation(LinearLayout.HORIZONTAL);
                       //add terminate button below edit text
                       Button buttonTerminate=new Button(this);
                       buttonTerminate.setLayoutParams(params);
                       buttonTerminate.setText("Terminate");
                       buttonTerminate.setTextColor(Color.parseColor("#FFFFFF"));
                       buttonTerminate.setBackgroundResource(R.drawable.btn_terminate_background);
                       buttonTerminate.setAllCaps(false);
                       buttonTerminate.setVisibility(View.GONE);
                       txtLabel.setTypeface(null, Typeface.BOLD);
                       ll_parent.addView(txtLabel);

                       JSONArray jsonArrayOptions = jsonObjectQuesType.getJSONArray("question_options");
                       Spinner spinner=new Spinner(this);
                       spinner.setId(Integer.parseInt(jsonObjectQuesType.getString("question_id")));
                       txtLabel.setId(Integer.parseInt(jsonObjectQuesType.getString("question_id")));
                       ArrayList<String> spinnerAL=new ArrayList<>();
                       String selectedOptionsSpinner="";
                       String[] arrayselectedOptionsSpinner=null;
                       if((questionID.equals("79"))){
                           selectedOptionsSpinner=sharedPrefHelper.getString("family_language","");
                           arrayselectedOptionsSpinner = selectedOptionsSpinner.split(",");

                           buttonTerminate.setVisibility(View.VISIBLE);
                           buttonTerminate.setOnClickListener(new View.OnClickListener() {
                               @Override
                               public void onClick(View view) {
                                   showPopupForTerminateSurveyQuestionWise("6", "Ineligible Language");
                               }
                           });
                       }
                       else if(questionID.equals("50") || questionID.equals("51") || questionID.equals("52")){
                           selectedOptionsSpinner=sharedPrefHelper.getString("CWE_Status","");
                           if(selectedOptionsSpinner.equals("1")){
                             spinner.setVisibility(View.GONE);
                             txtLabel.setVisibility(View.GONE);
                           }
                       }
                       else if ((questionID.equals("72"))) {
                           String stayWith = sharedPrefHelper.getString("stayWith", "");
                           if (!stayWith.equals("1")) {
                               txtLabel.setVisibility(View.GONE);
                               spinner.setVisibility(View.GONE);
                           }
                       }
                       else if ((questionID.equals("73"))) {
                           String stayWith = sharedPrefHelper.getString("stayWith", "");
                           if (!stayWith.equals("2")) {
                               txtLabel.setVisibility(View.GONE);
                               spinner.setVisibility(View.GONE);
                           }
                       }
                       else if ((questionID.equals("75"))) {
                           String town_village_class = sharedPrefHelper.getString("town_village_class", "");
                           if (!town_village_class.equalsIgnoreCase("Urban")) {
                               txtLabel.setVisibility(View.GONE);
                               spinner.setVisibility(View.GONE);
                           }
                       }
                       int internetUse=0;
                       if(jsonObjectQuesType.getString("question_id").equals("84")){
                           Gson gson = new Gson();
                           java.lang.reflect.Type type = new TypeToken<HashMap<String, String>>(){}.getType();
                           HashMap<Integer, String> testHashMap2 = gson.fromJson(sharedPrefHelper.getString("internetUse",""), type);
                           ///JSONObject obj=new JSONObject(sharedPrefHelper.setString("householdMember","objMember.toString()"););
                           if (testHashMap2!=null) {
                               Map<Integer, String> treeMapName = new TreeMap<>(testHashMap2);

                               for (Map.Entry<Integer, String> entry : treeMapName.entrySet()) {
                                   if (entry.getValue().equals("1")) {
                                       internetUse = 1;
                                       break;
                                   }else if(entry.getValue().equals("2")){
                                       internetUse = 2;
                                       //break;
                                   }//
                               }
                           }
                           if (internetUse==2){
                               txtLabel.setVisibility(View.GONE);
                               spinner.setVisibility(View.GONE);
                           }
                       }
                           /*if (jsonObjectQuesType.getString("question_id").equals("84")) {
                               String accessInternetOnMobile = sharedPrefHelper.getString("2100f_radio_ids", "");
                               if (accessInternetOnMobile.equals("1")) {
                                   txtLabel.setVisibility(View.VISIBLE);
                                   spinner.setVisibility(View.VISIBLE);
                               } else {
                                   txtLabel.setVisibility(View.GONE);
                                   spinner.setVisibility(View.GONE);
                               }
                           }*/
                       if (questionID.equals("85")) {
                           String lastTimeAccessInternet = sharedPrefHelper.getString("last_time_access_internet", "");
                           if (lastTimeAccessInternet.equals("9")) {
                               txtLabel.setVisibility(View.GONE);
                               spinner.setVisibility(View.GONE);
                           }
                       }
                       if (jsonObjectQuesType.getString("question_id").equals("100")) {
                           ArrayList<String> nameAL = new ArrayList<>();
                           Gson gson = new Gson();
                           java.lang.reflect.Type type = new TypeToken<HashMap<String, String>>(){}.getType();
                           HashMap<Integer, String> testHashMap2 = gson.fromJson(sharedPrefHelper.getString("householdMember","objMember.toString()"), type);
                           ///JSONObject obj=new JSONObject(sharedPrefHelper.setString("householdMember","objMember.toString()"););
                           Map<Integer, String> treeMapName = new TreeMap<>(testHashMap2);
                           int spinnerIndex=1;
                           for (Map.Entry<Integer, String> entry : treeMapName.entrySet()) {
                               if(sharedPrefHelper.getString("HH_Name","").equals(entry.getValue()) || sharedPrefHelper.getString("CWE_Name","").equals(entry.getValue())) {
                                   //nameAL.add(entry.getValue() + "-" + spinnerIndex);
                                   nameAL.add(entry.getValue());
                               }
                               spinnerIndex++;
                           }
                           nameAL.add(0, getString(R.string.select_option));
                           ArrayAdapter arrayAdapter = new ArrayAdapter(context, R.layout.custom_spinner_dropdown, nameAL);
                           spinner.setAdapter(arrayAdapter);
                           if ( (back_status == true || screen_type.equals("survey_list")) && answerModelList.size() > startPosition) {
                               if(!answerModelList.get(startPosition).getOption_id().equals("0")) {
                                   int position = 0;
                                   int spnposI = 0;
                                   int spnposP = 0;
                                   Map<Integer, String> treeMapAge = new TreeMap<>(testHashMap2);
                                   try {
                                       for (Map.Entry<Integer, String> entry : treeMapAge.entrySet()) {
                                           if (Integer.parseInt(answerModelList.get(startPosition).getOption_id()) - 1 == spnposP) {
                                               position = spnposP;
                                               break;
                                           }
                                           spnposP++;
                                       }
                                       spinner.setSelection(position + 1);
                                   }catch(Exception ex){
                                       Log.e("CWE Error",ex.getMessage());
                                   }

                               }
                            }
                       }
                       else if (jsonObjectQuesType.getString("question_id").equals("79")) {
                           ArrayList<String> nameAL = new ArrayList<>();
                           Map<Integer, String> treeMapName = new TreeMap<>(languageHashMap);
                           int spinnerIndex=1;
                           for (Map.Entry<Integer, String> entry : treeMapName.entrySet()) {
                                   nameAL.add(entry.getValue());
                                   spinnerIndex++;
                           }
                           nameAL.add(0, getString(R.string.select_option));
                           ArrayAdapter arrayAdapter = new ArrayAdapter(context, R.layout.custom_spinner_dropdown, nameAL);
                           spinner.setAdapter(arrayAdapter);
                           if ( (back_status == true || screen_type.equals("survey_list")) && answerModelList.size() > startPosition) {
                               if(!answerModelList.get(startPosition).getOption_id().equals("0")) {
                                   int position = 0;
                                   int spnposI = 0;
                                   int spnposP = 0;
                                   Map<Integer, String> treeMapLang = new TreeMap<>(languageHashMap);
                                   for (Map.Entry<Integer, String> entry : treeMapLang.entrySet()) {
                                       if (Integer.parseInt(answerModelList.get(startPosition).getOption_id()) == entry.getKey()) {
                                           position = spnposP;
                                           break;
                                       }
                                       spnposP++;
                                   }
                                   spinner.setSelection(position + 1);
                               }
                           }
                       }
                       else {
                           /*for (int j = 0; j < jsonArrayOptions.length(); j++) {
                               spinnerAL.clear();*/
                           if (jsonObjectQuesType.getString("question_id").equals("49") && sharedPrefHelper.getInt("ageInYears", 0) <5) {
                               for (int k = 0; k < 1; k++) {
                                   JSONObject jsonObjectOptionValues = jsonArrayOptions.getJSONObject(k);
                                   String spinnerOption = jsonObjectOptionValues.getString("option_value");
                                   spinnerAL.add(spinnerOption);
                               }
                           }
                           else if (jsonObjectQuesType.getString("question_id").equals("49") && sharedPrefHelper.getInt("ageInYears", 0) >=5
                                   && sharedPrefHelper.getInt("ageInYears", 0) <10) {
                               for (int k = 0; k < 3; k++) {
                                   JSONObject jsonObjectOptionValues = jsonArrayOptions.getJSONObject(k);
                                   String spinnerOption = jsonObjectOptionValues.getString("option_value");
                                   spinnerAL.add(spinnerOption);
                               }
                           }
                           else if (jsonObjectQuesType.getString("question_id").equals("49") && sharedPrefHelper.getInt("ageInYears", 0) >=10
                                   && sharedPrefHelper.getInt("ageInYears", 0) <11) {
                               for (int k = 0; k < 4; k++) {
                                   JSONObject jsonObjectOptionValues = jsonArrayOptions.getJSONObject(k);
                                   String spinnerOption = jsonObjectOptionValues.getString("option_value");
                                   spinnerAL.add(spinnerOption);
                               }
                           }
                           else if (jsonObjectQuesType.getString("question_id").equals("49") && sharedPrefHelper.getInt("ageInYears", 0) >=11
                                   && sharedPrefHelper.getInt("ageInYears", 0) <15) {
                               for (int k = 0; k < 5; k++) {
                                   JSONObject jsonObjectOptionValues = jsonArrayOptions.getJSONObject(k);
                                   String spinnerOption = jsonObjectOptionValues.getString("option_value");
                                   spinnerAL.add(spinnerOption);
                               }
                           }
                           else if (jsonObjectQuesType.getString("question_id").equals("49") && sharedPrefHelper.getInt("ageInYears", 0) >=15
                                   && sharedPrefHelper.getInt("ageInYears", 0) <17) {
                               for (int k = 0; k < 7; k++) {
                                   JSONObject jsonObjectOptionValues = jsonArrayOptions.getJSONObject(k);
                                   String spinnerOption = jsonObjectOptionValues.getString("option_value");
                                   spinnerAL.add(spinnerOption);
                               }
                           }
                           else if (jsonObjectQuesType.getString("question_id").equals("49") && sharedPrefHelper.getInt("ageInYears", 0) >=17
                                   && sharedPrefHelper.getInt("ageInYears", 0) <20) {
                               for (int k = 0; k < 8; k++) {
                                   JSONObject jsonObjectOptionValues = jsonArrayOptions.getJSONObject(k);
                                   String spinnerOption = jsonObjectOptionValues.getString("option_value");
                                   spinnerAL.add(spinnerOption);
                               }
                           }
                           else if (jsonObjectQuesType.getString("question_id").equals("49") && sharedPrefHelper.getInt("ageInYears", 0) >=20
                                   && sharedPrefHelper.getInt("ageInYears", 0) <23) {
                               for (int k = 0; k < 9; k++) {
                                   JSONObject jsonObjectOptionValues = jsonArrayOptions.getJSONObject(k);
                                   String spinnerOption = jsonObjectOptionValues.getString("option_value");
                                   spinnerAL.add(spinnerOption);
                               }
                           }
                           else if (jsonObjectQuesType.getString("question_id").equals("49") && sharedPrefHelper.getInt("ageInYears", 0) >=23) {
                               for (int k = 0; k < 11; k++) {
                                   JSONObject jsonObjectOptionValues = jsonArrayOptions.getJSONObject(k);
                                   String spinnerOption = jsonObjectOptionValues.getString("option_value");
                                   spinnerAL.add(spinnerOption);
                               }
                           }
                           else if (jsonObjectQuesType.getString("question_id").equals("50") && sharedPrefHelper.getInt("ageInYears", 0) <5) {
                               for (int k = 0; k < 1; k++) {
                                   JSONObject jsonObjectOptionValues = jsonArrayOptions.getJSONObject(k);
                                   String spinnerOption = jsonObjectOptionValues.getString("option_value");
                                   spinnerAL.add(spinnerOption);
                               }
                           }
                           else if (jsonObjectQuesType.getString("question_id").equals("50") && sharedPrefHelper.getInt("ageInYears", 0) >=5
                                   && sharedPrefHelper.getInt("ageInYears", 0) <10) {
                               for (int k = 0; k < 2; k++) {
                                   JSONObject jsonObjectOptionValues = jsonArrayOptions.getJSONObject(k);
                                   String spinnerOption = jsonObjectOptionValues.getString("option_value");
                                   spinnerAL.add(spinnerOption);
                               }
                           }
                           else if (jsonObjectQuesType.getString("question_id").equals("50") && sharedPrefHelper.getInt("ageInYears", 0) >=10
                                   && sharedPrefHelper.getInt("ageInYears", 0) <16) {
                               for (int k = 2; k < 4; k++) {
                                   JSONObject jsonObjectOptionValues = jsonArrayOptions.getJSONObject(k);
                                   String spinnerOption = jsonObjectOptionValues.getString("option_value");
                                   spinnerAL.add(spinnerOption);
                               }
                           }
                           else if (jsonObjectQuesType.getString("question_id").equals("50") && sharedPrefHelper.getInt("ageInYears", 0) >=16
                                   && sharedPrefHelper.getInt("ageInYears", 0) <25) {
                               for (int k = 2; k < 6; k++) {
                                   JSONObject jsonObjectOptionValues = jsonArrayOptions.getJSONObject(k);
                                   String spinnerOption = jsonObjectOptionValues.getString("option_value");
                                   spinnerAL.add(spinnerOption);
                               }
                           }
                           else if (jsonObjectQuesType.getString("question_id").equals("50") && sharedPrefHelper.getInt("ageInYears", 0) >=25) {
                               for (int k = 2; k < 7; k++) {
                                   JSONObject jsonObjectOptionValues = jsonArrayOptions.getJSONObject(k);
                                   String spinnerOption = jsonObjectOptionValues.getString("option_value");
                                   spinnerAL.add(spinnerOption);
                               }
                           }
                          if (jsonObjectQuesType.getString("question_id").equals("51")) {//01-02-2021
                               String currentWorkingStatus=sharedPrefHelper.getString("currentWorkingStatus","");
                               String town_village_class = sharedPrefHelper.getString("town_village_class", "");
                               if (town_village_class.equalsIgnoreCase("Urban")) {
                                   for (int k = 0; k < 15; k++) {
                                       if (!currentWorkingStatus.equals("3") && !currentWorkingStatus.equals("4")
                                               && !currentWorkingStatus.equals("6") && !currentWorkingStatus.equals("7")) {
                                           String currentOccupation = "99";
                                           JSONObject jsonObjectOptionValues = jsonArrayOptions.getJSONObject(k);
                                           if(jsonObjectOptionValues.getString("option_id").equals(currentOccupation)) {
                                               String spinnerOption = jsonObjectOptionValues.getString("option_value");
                                               spinnerAL.add(spinnerOption);
                                           }
                                       }else{
                                           JSONObject jsonObjectOptionValues = jsonArrayOptions.getJSONObject(k);
                                           String spinnerOption = jsonObjectOptionValues.getString("option_value");
                                           spinnerAL.add(spinnerOption);
                                       }
                                   }
                               }
                               else {
                                   for (int k = 15; k < 40; k++) {
                                       if (!currentWorkingStatus.equals("3") && !currentWorkingStatus.equals("4")
                                               && !currentWorkingStatus.equals("6") && !currentWorkingStatus.equals("7")) {
                                           String currentOccupation = "99";
                                           JSONObject jsonObjectOptionValues = jsonArrayOptions.getJSONObject(k);
                                           if(jsonObjectOptionValues.getString("option_id").equals(currentOccupation)) {
                                               String spinnerOption = jsonObjectOptionValues.getString("option_value");
                                               spinnerAL.add(spinnerOption);
                                           }
                                       }else{
                                           JSONObject jsonObjectOptionValues = jsonArrayOptions.getJSONObject(k);
                                           String spinnerOption = jsonObjectOptionValues.getString("option_value");
                                           spinnerAL.add(spinnerOption);
                                       }
                                   }
                               }
                           }
                           else {
                               for (int k = 0; k < jsonArrayOptions.length(); k++) {
                                   JSONObject jsonObjectOptionValues = jsonArrayOptions.getJSONObject(k);
                                   String spinnerOption = jsonObjectOptionValues.getString("option_value");
                                   if ((questionID.equals("79"))) {
                                       boolean contains = Arrays.asList(arrayselectedOptionsSpinner).contains(jsonObjectOptionValues.getString("option_id"));
                                       if (contains) {
                                           spinnerAL.add(spinnerOption);
                                       }
                                   } else {
                                       spinnerAL.add(spinnerOption);
                                   }
                               }
                           }
                           spinnerAL.add(0, getString(R.string.select_option));
                           ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.custom_spinner_dropdown, spinnerAL);
                           spinner.setAdapter(arrayAdapter);
                           if (spinner.getVisibility() == View.VISIBLE) {
                               if ((back_status == true || screen_type.equals("survey_list")) && answerModelList.size() > startPosition) {
                                   // spinner.setSelection(Integer.parseInt(answerModelList.get(startPosition).getOption_id()));
                                   int spinnerpos=0;
                                   boolean isBreak=false;
                                   if(!answerModelList.get(startPosition).getOption_id().equals("0")) {
                                       for (int j = 0; j < spinnerAL.size(); j++) {
                                           for (int k = 0; k < jsonArrayOptions.length(); k++) {
                                               JSONObject jsonObjectOptionValues = jsonArrayOptions.getJSONObject(k);
                                               String spinnerOptionOptionID = jsonObjectOptionValues.getString("option_id");
                                               if (spinnerOptionOptionID.equals(answerModelList.get(startPosition).getOption_id()) && spinnerAL.get(j).equals(jsonObjectOptionValues.getString("option_value"))) {
                                                   //spinnerpos++;
                                                   isBreak = true;
                                                   break;
                                               }
                                           }
                                           if (isBreak) {
                                               break;
                                           } else {
                                               if (spinnerpos < spinnerAL.size() - 1) {
                                                   spinnerpos++;
                                               }
                                           }
                                       }
                                   }
                                   //spinner.setSelection(Integer.parseInt(answerModelList.get(startPosition).getOption_id()));
                                   spinner.setSelection(spinnerpos);
                               }
                           }
                       }
                      // if (questionID.equals("84")) {
                           spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                               @Override
                               public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                try {
                                    if (jsonObjectQuesType.getString("question_id").equals("84")) {
                                        long spinnerID=spinner.getSelectedItemId();
                                        for (int v = 0; v < ll_parent.getChildCount(); v++) {
                                            final View childView = ll_parent.getChildAt(v);
                                            sharedPrefHelper.setString("last_time_access_internet", Long.toString(spinnerID));
                                            if (childView instanceof Spinner) {
                                                Spinner spinner = (Spinner) childView;
                                                if(String.valueOf(spinner.getId()).equals("85")){
                                                    if (sharedPrefHelper.getString("last_time_access_internet", "").equals("9")){
                                                        spinner.setVisibility(View.GONE);
                                                    }else{
                                                        spinner.setVisibility(View.VISIBLE);
                                                    }
                                                }
                                            }
                                            else if (childView instanceof TextView) {
                                                TextView textView = (TextView) childView;
                                                if(String.valueOf(textView.getId()).equals("85")){
                                                    if (sharedPrefHelper.getString("last_time_access_internet", "").equals("9")){
                                                        textView.setVisibility(View.GONE);
                                                    }else{
                                                        textView.setVisibility(View.VISIBLE);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                               }
                               @Override
                               public void onNothingSelected(AdapterView<?> adapterView) {

                               }
                           });
                       //}

                       startPosition++;
                       endPosition++;
                       ll_parent.addView(spinner);
                       layout2.addView(buttonTerminate);
                       ll_parent.addView(layout2);
                       //ll_parent.addView(buttonTerminate);
                       // onAddSpinner(jsonObjectQuesType);
                   }
                   ////if question_type->5 then its Button
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
                   //if question_type->6 then its Text View
                   else if (jsonObjectQuesType.getString("question_type").equals("6")) {
                       TextView textView=new TextView(this);
                       textView.setId(Integer.parseInt(jsonObjectQuesType.getString("question_id")));
                       String description=jsonObjectQuesType.getString("question_name");
                       description=description.replaceAll("\\$name",sharedPrefHelper.getString("interviewer_name",""));
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
           if(isBack_disable==true && Integer.parseInt(screen_id)<11){
                btn_previous.setEnabled(false);
           }else{
               btn_previous.setEnabled(true);
           }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void showPopupForDataEraseEditText(int questionid,String value,EditText editText,int position) {
        String msg="";
        if(questionid==32){
            msg="If you change no. of members then all members data will remove and you have to submit again";
        }
        else if(questionid==57){
            msg="If you change no. of tv then all tv data will remove and you have to submit again";
        }
        final SweetAlertDialog pDialog = new SweetAlertDialog(
                context, SweetAlertDialog.WARNING_TYPE);
        //new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
        pDialog.setTitleText("Are you sure?")
                .setContentText(msg)
                .setConfirmText("Yes")
                .setCancelText("No")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        if(questionid==32){
                            answerModelHouseholdMemberList.clear();
                            answerModelHouseholdMemberListTotal.clear();
                            sharedPrefHelper.setString("CWE_Yes","0");
                            sharedPrefHelper.setString("HH_Yes","0");
                            sharedPrefHelper.setString("CWE_Name","");
                            sharedPrefHelper.setString("HH_Name","");
                            answerModelList.get(position).setOption_value(editText.getText().toString().trim());
                        }
                        else if(questionid==57){
                            answerModelTVList.clear();
                            answerModelTVListTotal.clear();
                            answerModelList.get(position).setOption_value(editText.getText().toString().trim());
                        }
                        sDialog.dismiss();

                    }
                })
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        editText.setText(value);
                        sDialog.dismiss();
                    }
                })
                .show();
        pDialog.setCancelable(false);
    }
    private void showPopupForDataEraseRadio(int questionid, RadioGroup radiogroup,String value) {
        String msg="";
        msg="If you change CWE status then all data will remove and you have to submit again";
        final SweetAlertDialog pDialog = new SweetAlertDialog(
                context, SweetAlertDialog.WARNING_TYPE);
        //new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
        pDialog.setTitleText("Are you sure?")
                .setContentText(msg)
                .setConfirmText("Yes")
                .setCancelText("No")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        if(questionid==23){
                            ArrayList<AnswerModel> list= new ArrayList<>();
                            if (jsonArrayScreen != null) {
                                for (int i=0;i<24;i++)
                                {
                                    if(answerModelList.size()>i) {
                                        list.add(answerModelList.get(i));
                                    }else{
                                        break;
                                    }
                                }
                            }
                            answerModelList.clear();
                            answerModelList=list;
                            answerModelHouseholdMemberList.clear();
                            answerModelHouseholdMemberListTotal.clear();
                            answerModelTVList.clear();
                            answerModelTVListTotal.clear();
                            sharedPrefHelper.setString("CWE_Status",value);
                            sharedPrefHelper.setString("CWE_Yes","0");
                            sharedPrefHelper.setString("HH_Yes","0");
                            sharedPrefHelper.setString("CWE_Name","");
                            sharedPrefHelper.setString("HH_Name","");
                            if(value.equalsIgnoreCase("1")){
                                        /*jsonArrayScreen.remove(11);
                                        jsonArrayScreen.remove(11);*/
                                try{
                                    JSONArray jlist = new JSONArray();
                                    int len = jsonArrayScreen.length();
                                        if (jsonArrayScreen != null) {
                                            for (int i = 0; i < len; i++) {
                                                if (i != 11 && i != 12) {
                                                    jlist.put(jsonArrayScreen.get(i));
                                                }
                                            }
                                        }
                                    if(jlist.length()>=24) {
                                        jsonArrayScreen = jlist;
                                    }
                                        totalScreen = jsonArrayScreen.length();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            else{
                                try {
                                    //jsonArrayScreen=new JSONArray();
                                    jsonArrayScreen=jsonQuestions.getJSONArray("group").getJSONObject(0).getJSONArray("screens");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                totalScreen=jsonArrayScreen.length();
                            }
                        }
                        sDialog.dismiss();
                    }
                })
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismiss();
                        try {
                            radiogroup.clearCheck();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                .show();
        pDialog.setCancelable(false);
    }

    private void openDialogForAgeConfirmation(EditText editText,String value) {
        /*final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        //new AlertDialog.Builder(context).setTitle("Alert!")
        builder.setMessage(value)
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
        builder.setCancelable(false);*/
        final SweetAlertDialog pDialog = new SweetAlertDialog(
                context, SweetAlertDialog.WARNING_TYPE);
        //new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
        pDialog.setTitleText("Alert!")
                .setContentText(value)
                .setConfirmText("Yes")
                .setCancelText("No")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismiss();
                    }
                })
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismiss();
                        editText.setText("");
                    }
                })
                .show();
        pDialog.setCancelable(false);
    }
    private void openDialogForAgeConfirmationSpinner(String value) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        //new AlertDialog.Builder(context).setTitle("Alert!")
        builder.setMessage(value)
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
        builder.setCancelable(false);
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
            AudioSavePathInDevice = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                        timeStamp + ".mp3";

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
                //mediaRecorder.release();
                //mediaRecorder = null;
            } catch (Exception e) {
                // Add your handling here.
                e.printStackTrace();
            }
        }
        sharedPrefHelper.setString("AudioSavePathInDevice",AudioSavePathInDevice);
        sqliteHelper.updateAudioFileInTable("survey", survey_id, AudioSavePathInDevice);
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
            String screen_id = sharedPrefHelper.getString("screen_id", "");
            /*if (screen_id.equals("1")||screen_id.equals("2")||screen_id.equals("3")||screen_id.equals("4") ||screen_id.equals("5")||screen_id.equals("6")||screen_id.equals("7")||screen_id.equals("8")||screen_id.equals("9")) {
                Toast.makeText(context, "You can't halt survey from this screen.", Toast.LENGTH_LONG).show();
            } else {*/
                showPopupForTerminateSurvey();
            //}
        }
        if (item.getItemId()==R.id.home_icon) {
            Intent intentMainMenu=new Intent(context, MainMenu.class);
            intentMainMenu.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intentMainMenu);
        }
        return super.onOptionsItemSelected(item);
    }

    public void setTerminattion(String id, int groupID,String radioButtonText) {
        stopRecording();
        Intent intentTerminate = new Intent(context, TerminateActivity.class);
        intentTerminate.putExtra("screen_type", "terminate");
        intentTerminate.putExtra("radio_button_id", id);//termination id
        intentTerminate.putExtra("radioButtonText", radioButtonText);//termination reason
        intentTerminate.putExtra("answerModelList", answerModelList);
        intentTerminate.putExtra("answerFamilyMemberModelList", answerModelHouseholdMemberListTotal);
        intentTerminate.putExtra("answerTVModelList", answerModelTVListTotal);
        intentTerminate.putExtra("AudioSavePathInDevice", AudioSavePathInDevice);
        intentTerminate.putExtra("groupID", groupID);
        intentTerminate.putExtra("screen_id", sharedPrefHelper.getString("screen_id", ""));
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
        final SweetAlertDialog pDialog = new SweetAlertDialog(
                context, SweetAlertDialog.WARNING_TYPE);
        //new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
        pDialog.setTitleText("Are you sure?")
                .setContentText("Want to Halt/Terminate the interview!")
                .setConfirmText("Yes")
                .setCancelText("No")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismiss();
                        setTerminattion("",0,"");
                    }
                })
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismiss();
                    }
                })
                .show();
        pDialog.setCancelable(false);
    }
    private void showPopupForTerminateSurveyOnRadio(String id,String radioButtonText,
                                                    RadioGroup radiogroup,int groupID) {
        final SweetAlertDialog pDialog = new SweetAlertDialog(
                context, SweetAlertDialog.WARNING_TYPE);
        //new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
        pDialog.setTitleText("Are you sure?")
                .setContentText("Want to terminate the interview!")
                .setConfirmText("Yes")
                .setCancelText("No")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismiss();
                        setTerminattion(id,groupID,radioButtonText);
                    }
                })
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismiss();
                        try {
                            radiogroup.clearCheck();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                .show();
        pDialog.setCancelable(false);
    }
    private void showPopupForTerminateForTV(String id) {
        final SweetAlertDialog pDialog = new SweetAlertDialog(
                context, SweetAlertDialog.WARNING_TYPE);
        //new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
        pDialog.setTitleText("Electricity required in durables for continue the survey")
                .setContentText("Want to terminate the interview!")
                .setConfirmText("Yes")
                .setCancelText("No")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismiss();
                        setTerminattion(id,0,"Electricity required in durables");
                    }
                })
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismiss();
                    }
                })
                .show();
        pDialog.setCancelable(false);
    }

    private void showPopupForError(String data) {
        final SweetAlertDialog pDialog = new SweetAlertDialog(
                context, SweetAlertDialog.WARNING_TYPE);
        //new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
        pDialog.setTitleText("Alert!")
                .setContentText(data)
                .setConfirmText("OK")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismiss();
                        //setTerminattion("");
                    }
                })
                /*.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismiss();
                    }
                })*/
                .show();
                pDialog.setCancelable(false);
    }

    private void showPopupForTerminateSurveyQuestionWise(String id, String reason) {
        final SweetAlertDialog pDialog = new SweetAlertDialog(
                context, SweetAlertDialog.WARNING_TYPE);
        //new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
        pDialog.setTitleText("Are you sure?")
                .setContentText("Want to Halt/Terminate the interview!")
                .setConfirmText("Yes")
                .setCancelText("No")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismiss();
                        setTerminattion(id,0,reason);
                    }
                })
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismiss();
                    }
                })
                .show();
        pDialog.setCancelable(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        /*hide and show toolbar items*/
        //if (screen_type.equalsIgnoreCase("survey")) {
        String screen_id=sharedPrefHelper.getString("screen_id", "");
        if (screen_id.equals("1")||screen_id.equals("2")||screen_id.equals("3")){
            MenuItem item_stop_survey = menu.findItem(R.id.stop_survey);
            item_stop_survey.setVisible(false);
        }else{
            MenuItem item_stop_survey = menu.findItem(R.id.stop_survey);
            item_stop_survey.setVisible(true);
            //item_stop_survey.setVisible(true);
        }
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
    public void passDataToActivity(ArrayList<ArrayList<AnswerModel>> answerModelListTotal,ArrayList<AnswerModel> answerModelList,int type,int mode){
       // Log.e("Fragment Dismissed",someValue);
        onBackPressedListener = null;
        btn_next.setEnabled(true);
        if(type==1){
            //answerModelHouseholdMemberList.clear();
            ArrayList<ArrayList<AnswerModel>> answerModelListNTotal=answerModelListTotal;
            ArrayList<AnswerModel> answerModelListN=answerModelList;
            answerModelHouseholdMemberList=answerModelListN;
            answerModelHouseholdMemberListTotal=answerModelListNTotal;
        }
        else if(type==2){
            //answerModelTVList.clear();
            ArrayList<ArrayList<AnswerModel>> answerModelListNTotal=answerModelListTotal;
            ArrayList<AnswerModel> answerModelListN=answerModelList;
            answerModelTVList=answerModelListN;
            answerModelTVListTotal=answerModelListNTotal;
        }
        if(mode==2){
            btnPreviousFragment();
        }else{
            questionsPopulate();
        }
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

    }
}
