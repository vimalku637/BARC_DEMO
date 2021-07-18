/*
 * Copyright (c)  2020. Indev Consultancy Private Limited,
 * Auther : Vimal Kumar
 * Date : 2020/12/15
 * Modified Date :
 * Modified By :
 */

package com.vrp.barc_demo.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
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
import com.google.android.material.imageview.ShapeableImageView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.vrp.barc_demo.R;
import com.vrp.barc_demo.activities.ClusterDetails;
import com.vrp.barc_demo.activities.HouseholdSurveyActivity;
import com.vrp.barc_demo.activities.TerminateActivity;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupRelationFragment extends Fragment implements HouseholdSurveyActivity.OnBackPressedListener, FragmentCommunicator {

    private static final String TAG = "GRFragment>>>";
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
    private Unbinder unbinder;
    private String survey_id="";
    private boolean isRecording=false;
    private String screen_type="";
    private int length=1;
    private int startPosition;
    private int startScreenPosition=0;
    private int startScreenParentPosition=0;
    private int startPositionBefore;
    private int endPosition;
    private int endScreenPosition=1;
    private int endScreenParentPosition=1;
    private int endScreenCount=1;
    private SharedPrefHelper sharedPrefHelper;
    int totalQuestions;
    int totalScreen;
    JSONObject jsonQuestions = null;
    JSONObject jsonQuestionsLangGroup = null;
    JSONArray jsonArrayQuestions=null;
    JSONArray jsonArrayScreen=null;
    JSONArray jsonArrayScreenGroup=null;
    public static ArrayList<AnswerModel> answerModelList;
    public static ArrayList<ArrayList<AnswerModel>> answerTVtotal=new ArrayList<>();
    ArrayList<ScreenWiseQuestionModel> arrayScreenWiseQuestionModel= new ArrayList<>();
    String screen_id="11";
    boolean back_status=true;
    private SqliteHelper sqliteHelper;
    private String surveyObjectJSON=null;
    private int editFieldValues=0;
    private int groupRelationId=0;
    private int questionID=0;

    //interface via which we communicate to hosting Activity
    private ActivityCommunicator activityCommunicator;
    private String activityAssignedValue ="";
    private static final String STRING_VALUE ="stringValue";
    public Context context;
    private String name="";
    private int ageInYears=0;
    ArrayList<String> nameAL=new ArrayList<>();
    HashMap<Integer,String> nameVL=new HashMap<>();
    HashMap<Integer,String> internetUse=new HashMap<>();
    HashMap<Integer,String> ageVL=new HashMap<>();
    int totalScreenCount=0;
    int lenSingle=0;
    int totalSingle=0;
    private int startScreenCount=0;
    private ArrayList<String> spinnerAL2=new ArrayList<>();

    public GroupRelationFragment() {
        // Required empty public constructor
    }
    public static GroupRelationFragment newInstance(){
        return new GroupRelationFragment();
    }
    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        context = getActivity();
        activityCommunicator =(ActivityCommunicator)context;
        ((HouseholdSurveyActivity)context).fragmentCommunicator = this;
        answerModelList =((HouseholdSurveyActivity)context).answerModelHouseholdMemberList;
    }
    //now on your entire fragment use context rather than getActivity()
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null)
            activityAssignedValue = savedInstanceState.getString(STRING_VALUE);
    }
    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putString(STRING_VALUE,activityAssignedValue);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_group_relation, container, false);
        ((HouseholdSurveyActivity)getActivity()).setOnBackPressedListener(this);
        unbinder = ButterKnife.bind(this, view);
        initialization();
        Bundle bundle = getArguments();
        if (bundle!=null) {
            editFieldValues=getArguments().getInt("editFieldValues");
            sharedPrefHelper.setInt("editFieldValues",editFieldValues);
            startScreenParentPosition=getArguments().getInt("startScreenPosition");
            endScreenParentPosition=getArguments().getInt("endScreenPosition");
            groupRelationId=getArguments().getInt("groupRelationId");
            questionID=getArguments().getInt("questionID");
            screen_type=getArguments().getString("screen_type", "");
        }
        survey_id=sharedPrefHelper.getString("survey_id", "");
        isRecording=sharedPrefHelper.getBoolean("isRecording", false);
        if(isRecording){
            iv_recording.setVisibility(View.VISIBLE);
            startRecordingAnimation();
        }else{
            iv_recording.setVisibility(View.GONE);
        }

        try {
            //jsonQuestions = new JSONObject(MyJSON.loadJSONFromAsset(getActivity()));
            jsonQuestionsLangGroup = new JSONObject(MyJSON.loadJSONFromAsset(context));
            if (jsonQuestionsLangGroup.has("language_group")) {
                    /*JSONArray jsonArrayGroup=jsonQuestions.getJSONArray("group");
                    JSONObject jsonObjectGroup=jsonArrayGroup.getJSONObject(0);*/
                jsonQuestions = jsonQuestionsLangGroup.getJSONArray("language_group").getJSONObject(sharedPrefHelper.getInt("langID",0));
                jsonArrayScreen=new JSONArray();
                jsonArrayScreen = jsonQuestions.getJSONArray("group").getJSONObject(groupRelationId).getJSONArray("screens");
                //jsonArrayScreenbackup=jsonQuestions.getJSONArray("group").getJSONObject(0).getJSONArray("screens");
                totalScreen = jsonArrayScreen.length();
                Log.e("Screen", "onCreate: " + jsonArrayScreen.toString());
                if(totalScreen>0){
                    answerTVtotal.clear();
                    questionsPopulate();
                    btn_next.setEnabled(true);
                    btn_stop.setEnabled(true);
                }
            }
            else if (jsonQuestions.has("group")) {
                    /*JSONArray jsonArrayGroup=jsonQuestions.getJSONArray("group");
                    JSONObject jsonObjectGroup=jsonArrayGroup.getJSONObject(0);*/
                jsonArrayScreen = jsonQuestions.getJSONArray("group").getJSONObject(groupRelationId).getJSONArray("screens");
                totalScreen = jsonArrayScreen.length();
                Log.e("Screen", "onCreate: " + jsonArrayScreen.toString());
                if(totalScreen>0){
                    answerTVtotal.clear();
                    questionsPopulate();
                }
            }
        }catch (JSONException ex){
            Log.e("questions", "onCreate: " + ex.getMessage());
        }

        setButtonClick();
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {

                        return true;
                    }
                }
                return false;
            }
        });

        return view;
    }
    private void initialization() {
        sqliteHelper=new SqliteHelper(getActivity());
        sharedPrefHelper=new SharedPrefHelper(getActivity());
        //answerModelList=new ArrayList<>();
    }
    private void startRecordingAnimation() {
        Animation animation = new AlphaAnimation((float) 0.5, 0); //to change visibility from visible to invisible
        animation.setDuration(1000); //1 second duration for each animation cycle
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(Animation.INFINITE); //repeating indefinitely
        animation.setRepeatMode(Animation.REVERSE); //animation will start from end point once ended.
        iv_recording.startAnimation(animation); //to start animation
    }

    private void showPopupForError(String data) {
        final SweetAlertDialog pDialog = new SweetAlertDialog(
                context, SweetAlertDialog.WARNING_TYPE);
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

    private void setButtonClick() {
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_next.setEnabled(false);
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
                            else if (questionID.equals("33")) {
                                name=editText.getText().toString().trim();
                                sharedPrefHelper.setString("name", name);
                                if (name.length()==1){
                                    flag=false;
                                    editText.setError("Name can't be blank or only one character");
                                    break;
                                }
                                nameVL.put(totalScreenCount,""+name);
                                Gson gson = new Gson();
                                String hashMapString = gson.toJson(nameVL);
                                //JSONObject objMember=new JSONObject(nameVL);
                                sharedPrefHelper.setString("householdMember",hashMapString);
                            }else if (questionID.equals("35")) {
                                ageInYears=Integer.parseInt(editText.getText().toString().trim());
                                sharedPrefHelper.setInt("ageInYears", ageInYears);
                                if(ageInYears<1){
                                    flag=false;
                                    editText.setError("Age can't be less then 1");
                                    break;
                                }
                                else if(ageInYears>120){
                                    flag=false;
                                    editText.setError("Age can't be greater than 120");
                                    break;
                                }
                                else if(sharedPrefHelper.getInt("editFieldValues", 0)==1&&ageInYears<15){
                                    flag=false;
                                    //openDialogForAgeConfirmationSingleHH(ageInYears);
                                    editText.setError("Age can't be less then 15 if only one member");
                                    break;
                                }
                                ageVL.put(totalScreenCount-1,""+ageInYears);
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
                                if(jsonArrayQuestions.getJSONObject(count).getString("question_id").equals("40") && jsonArrayQuestions.getJSONObject(count).getString("question_id").equals("39")){
                                    if(answerModelList.get(nextPosition).getOption_id().equals("1") && radioID!=1){
                                        sharedPrefHelper.setString("CWE_Name","");
                                    }
                                    answerModelList.get(nextPosition).setOption_id(Integer.toString(radioID));
                                }
                                else if(jsonArrayQuestions.getJSONObject(count).getString("question_id").equals("39")){
                                    if(answerModelList.get(nextPosition).getOption_id().equals("1") && radioID!=1){
                                        sharedPrefHelper.setString("HH_Name","");
                                    }
                                    answerModelList.get(nextPosition).setOption_id(Integer.toString(radioID));
                                }
                                else{
                                    answerModelList.get(nextPosition).setOption_id(Integer.toString(radioID));
                                }
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
                            else if(jsonArrayQuestions.getJSONObject(count).getString("question_id").equals("39") && radioID==1){
                                if(sharedPrefHelper.getInt("ageInYears", 0)<15){
                                    flag=false;
                                    showPopupForError("For HH age could be greater than or equal to 15");
                                    break;
                                }else{
                                    sharedPrefHelper.setString("HH_Name",nameVL.get(totalScreenCount-3));
                                    sharedPrefHelper.setString("HH_Yes","1");
                                }
                            }
                            else if(jsonArrayQuestions.getJSONObject(count).getString("question_id").equals("39") && radioID==2){
                                String hh_name=sharedPrefHelper.getString("HH_Name", "");
                                if(hh_name.equals("") && editFieldValues==startScreenCount+1){
                                    flag=false;
                                    showPopupForError("Please select HH for any family member");
                                    //Toast.makeText(context,"Please select HH for any family member",Toast.LENGTH_LONG).show();
                                    break;
                                }
                            }
                            else if(jsonArrayQuestions.getJSONObject(count).getString("question_id").equals("40") && radioID==1){
                                if(sharedPrefHelper.getInt("ageInYears", 0)<15){
                                    flag=false;
                                    showPopupForError("For CWE age could be greater than or equal to 15");
                                    //Toast.makeText(context,"For CWE age could be greater than or equal to 15",Toast.LENGTH_LONG).show();
                                    break;
                                }else{
                                    sharedPrefHelper.setString("CWE_Name",nameVL.get(totalScreenCount-3));
                                    sharedPrefHelper.setString("CWE_Yes","1");
                                }
                            }
                            else if(jsonArrayQuestions.getJSONObject(count).getString("question_id").equals("40") && radioID==2){
                                String cwe_name=sharedPrefHelper.getString("CWE_Name", "");
                                if(cwe_name.equals("") && editFieldValues==startScreenCount+1 && sharedPrefHelper.getString("CWE_Status", "").equals("1")){
                                    flag=false;
                                    showPopupForError("Please select CWE for any family member");
                                    //Toast.makeText(context,"Please select HH for any family member",Toast.LENGTH_LONG).show();
                                    break;
                                }
                            }
                            else if(jsonArrayQuestions.getJSONObject(count).getString("question_id").equals("43")){
                                internetUse.put(totalScreenCount,""+radioID);
                                Gson gson = new Gson();
                                String hashMapString = gson.toJson(internetUse);
                                sharedPrefHelper.setString("internetUse",hashMapString);
                            }
                            if (jsonArrayQuestions.getJSONObject(count).getString("question_id").equals("40")){
                                sharedPrefHelper.setString("Q1h_Member_CWE", String.valueOf(radioID));
                            }
                            nextPosition++;
                            count++;
                        }
                        else if (childView instanceof Spinner) {
                            Spinner spinner = (Spinner) childView;
                            String selectedItem=Long.toString(spinner.getSelectedItemId());
                            if(jsonArrayQuestions.getJSONObject(count).getString("question_id").equals("41")) {
                                if (ageInYears <= 12) {
                                    int sepPosID = spinner.getSelectedItem().toString().indexOf("-");
                                    //String selectedItemSpinner = spinner.getSelectedItem().toString().substring(0, sepPosID);
                                    selectedItem = spinner.getSelectedItem().toString().substring(sepPosID+1);
                                    /*int spinnnnnc=0;
                                    Map<Integer, String> map = new TreeMap<>(nameVL);
                                    for (Map.Entry<Integer, String> entry : map.entrySet()) {
                                        if (entry.getValue().equals(spinner.getSelectedItem())) {
                                            selectedItem=String.valueOf(spinnnnnc+1);
                                        }
                                        spinnnnnc++;
                                    }*/
                                }
                            }
                            JSONArray jsonArrayOptions = jsonArrayQuestions.getJSONObject(count).getJSONArray("question_options");
                            for (int k = 0; k < jsonArrayOptions.length(); k++) {
                                JSONObject jsonObjectOptionValues = jsonArrayOptions.getJSONObject(k);
                                String spinnerOptionOptionValue = jsonObjectOptionValues.getString("option_value");
                                if(spinnerOptionOptionValue.equals(spinner.getSelectedItem())){
                                    selectedItem=jsonObjectOptionValues.getString("option_id");
                                    break;
                                }
                            }
                            if (jsonArrayQuestions.getJSONObject(count).getString("question_id").equals("37")){
                                //String currentWorkingStatus=Long.toString(spinner.getSelectedItemId());
                                String currentWorkingStatus=selectedItem;
                                //if currentWorkingStatus=3 or 4 or 6 or 7 then auto populate the occupation spinner option 'Not Applicable'
                                sharedPrefHelper.setString("currentWorkingStatus", currentWorkingStatus);
                            }
                            if((back_status==true || screen_type.equals("survey_list")) && answerModelList.size()>nextPosition){
                                answerModelList.get(nextPosition).setOption_id(selectedItem);
                            }else{
                                AnswerModel answerModel= new AnswerModel();
                                answerModel.setOption_id(selectedItem);
                                answerModel.setOption_value("");
                                //answerModel.setSurveyID(survey_id);
                                answerModel.setQuestionID(jsonArrayQuestions.getJSONObject(count).getString("question_id"));
                                answerModel.setPre_field(jsonArrayQuestions.getJSONObject(count).getString("pre_field"));
                                answerModel.setField_name(jsonArrayQuestions.getJSONObject(count).getString("field_name"));
                                answerModelList.add(answerModel);
                            }
                            if(jsonArrayQuestions.getJSONObject(count).getString("validation_id").equals("1") && spinner.getSelectedItemId()==0){
                                if(jsonArrayQuestions.getJSONObject(count).getString("question_id").equals("41")){
                                    if(ageInYears<=12){
                                        flag=false;
                                        break;
                                    }
                                }else{
                                    flag=false;
                                    break;
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
                //back_status=false;
                if(flag==true){
                    sharedPrefHelper.setInt("startPosition", startPosition);
                    endPosition = endPosition + length;
                    //Log.e(TAG, "Position >>> endPosition >>>" + endPosition + "startPosition >>>" + startPosition+"startPositionBefore >>>" + startPositionBefore);
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
                    if (endScreenCount<(totalScreen*editFieldValues)-1) {
                        btn_next.setEnabled(true);
                        btn_next.setText("Next");
                        sharedPrefHelper.setInt("endPosition", endPosition);
                        //save family_data in DB
                        if (!survey_id.equals("")) {
                            Gson gson = new Gson();
                            String listString = gson.toJson(
                                    answerModelList,
                                    new TypeToken<ArrayList<AnswerModel>>() {}.getType());
                            try {
                                JSONArray json_array =  new JSONArray(listString);
                                JSONObject json_object=new JSONObject();
                                /*json_object.put("user_id", sharedPrefHelper.getString("user_id", ""));
                                json_object.put("survey_id", survey_id);
                                json_object.put("cluster_no", sharedPrefHelper.getString("cluster_no", ""));
                                json_object.put("census_district_code", sharedPrefHelper.getString("census_district_code", ""));
                                json_object.put("GPS_latitude", "27.883743");
                                json_object.put("GPS_longitude", "79.912247");*/
                                /*json_object.put("GPS_latitude", sharedPrefHelper.getString("LAT", ""));
                                json_object.put("GPS_longitude", sharedPrefHelper.getString("LONG", ""));*/
                                json_object.put("family_data", json_array);
                                Log.e(TAG, "onClick: "+json_object.toString());

                                if (endScreenPosition==1) {
                                    //save data in to local DB.
                                    sqliteHelper.updateFamilyDataInTable("survey", "survey_id", survey_id, json_object);
                                    sqliteHelper.updateLocalFlag("partial", "survey",
                                            sharedPrefHelper.getString("survey_id", ""), 1);
                                } else {
                                    //update data in to local DB
                                    sqliteHelper.updateFamilyDataInTable("survey", "survey_id", survey_id, json_object);
                                    sqliteHelper.updateLocalFlag("partial", "survey",
                                            sharedPrefHelper.getString("survey_id", ""), 1);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        startScreenPosition++;
                        endScreenPosition++;
                    }
                    else {
                        //endPosition = totalQuestions;
                        //btn_next.setText("Submit");
                        startScreenPosition++;
                        endScreenPosition++;
                        btn_next.setEnabled(false);
                        btn_stop.setVisibility(View.VISIBLE);
                        btn_next.setVisibility(View.GONE);
                    }
                    totalScreenCount++;
                    endScreenCount++;
                    questionsPopulate();
                }
                else{
                    btn_next.setEnabled(true);
                    Toast.makeText(getActivity(),"Please fill all required fields",Toast.LENGTH_LONG).show();
                }
            }
        });
        btn_previous.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {

                endScreenCount=endScreenCount-1;
                if(startScreenPosition==0){
                    startScreenCount--;
                    startScreenPosition=totalScreen-1;
                    endScreenPosition=totalScreen;
                }else{
                    startScreenPosition=startScreenPosition-1;
                    endScreenPosition=endScreenPosition-1;
                }

                back_status=true;

                if(endScreenCount<=0){
                    //getActivity().onBackPressed();
                    activityCommunicator.passDataToActivity(answerTVtotal,answerModelList,1,2);
                    doBack();
                }else{
                    btn_next.setEnabled(true);
                    btn_next.setVisibility(View.VISIBLE);
                    btn_stop.setVisibility(View.GONE);
                    totalScreenCount--;
                    startPosition=startPosition-(endPosition+Integer.parseInt(arrayScreenWiseQuestionModel.get(startScreenPosition).getquestions()));
                    questionsPopulate();
                }
            }
        });
        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean flag=true;
                btn_stop.setEnabled(false);
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
                            else if (questionID.equals("33")) {
                                name=editText.getText().toString().trim();
                                sharedPrefHelper.setString("name", name);
                                if (name.length()==1){
                                    flag=false;
                                    break;
                                }
                                nameVL.put(totalScreenCount,""+name);
                            }else if (questionID.equals("35")) {
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
                                ageVL.put(totalScreenCount-1,""+ageInYears);
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
                            if(jsonArrayQuestions.getJSONObject(count).getString("question_id").equals("39") && radioID==1){
                                sharedPrefHelper.setString("HH_Name",nameVL.get(totalScreenCount-3));
                                sharedPrefHelper.setString("HH_Yes","1");
                            }
                            else if(jsonArrayQuestions.getJSONObject(count).getString("question_id").equals("40") && radioID==1){
                                sharedPrefHelper.setString("CWE_Name",nameVL.get(totalScreenCount-3));
                                sharedPrefHelper.setString("CWE_Yes","1");
                            }
                            else if(jsonArrayQuestions.getJSONObject(count).getString("question_id").equals("43")){
                                internetUse.put(totalScreenCount,""+radioID);
                                Gson gson = new Gson();
                                String hashMapString = gson.toJson(internetUse);
                                sharedPrefHelper.setString("internetUse",hashMapString);
                            }
                            nextPosition++;
                            count++;
                        }
                        else if (childView instanceof Spinner) {
                            Spinner spinner = (Spinner) childView;
                            String selectedItem=Long.toString(spinner.getSelectedItemId());
                            if(jsonArrayQuestions.getJSONObject(count).getString("question_id").equals("41")) {
                                if (ageInYears <= 12) {
                                    int sepPosID = spinner.getSelectedItem().toString().indexOf("-");
                                    //String selectedItemSpinner = spinner.getSelectedItem().toString().substring(0, sepPosID);
                                    selectedItem = spinner.getSelectedItem().toString().substring(sepPosID+1);
                                    /*int spinnnnnc=0;
                                    Map<Integer, String> map = new TreeMap<>(nameVL);
                                    for (Map.Entry<Integer, String> entry : map.entrySet()) {
                                        if (entry.getValue().equals(spinner.getSelectedItem())) {
                                            selectedItem=String.valueOf(spinnnnnc+1);
                                        }
                                        spinnnnnc++;
                                    }*/
                                }
                            }
                            JSONArray jsonArrayOptions = jsonArrayQuestions.getJSONObject(count).getJSONArray("question_options");
                            for (int k = 0; k < jsonArrayOptions.length(); k++) {
                                JSONObject jsonObjectOptionValues = jsonArrayOptions.getJSONObject(k);
                                String spinnerOptionOptionValue = jsonObjectOptionValues.getString("option_value");
                                if(spinnerOptionOptionValue.equals(spinner.getSelectedItem())){
                                    selectedItem=jsonObjectOptionValues.getString("option_id");
                                    break;
                                }
                            }
                            if((back_status==true || screen_type.equals("survey_list")) && answerModelList.size()>nextPosition){
                                answerModelList.get(nextPosition).setOption_id(selectedItem);
                            }else{
                                AnswerModel answerModel= new AnswerModel();
                                answerModel.setOption_id(selectedItem);
                                answerModel.setOption_value("");
                                //answerModel.setSurveyID(survey_id);
                                answerModel.setQuestionID(jsonArrayQuestions.getJSONObject(count).getString("question_id"));
                                answerModel.setPre_field(jsonArrayQuestions.getJSONObject(count).getString("pre_field"));
                                answerModel.setField_name(jsonArrayQuestions.getJSONObject(count).getString("field_name"));
                                answerModelList.add(answerModel);
                            }
                            if(jsonArrayQuestions.getJSONObject(count).getString("validation_id").equals("1") && spinner.getSelectedItemId()==0){
                                if(jsonArrayQuestions.getJSONObject(count).getString("question_id").equals("41")){
                                    if(ageInYears<=12){
                                        flag=false;
                                        break;
                                    }
                                }
                                /*else if (jsonArrayQuestions.getJSONObject(count).getString("question_id").equals("38")){
                                    if (sharedPrefHelper.getString("currentWorkingStatus","").equals("1")
                                            ||sharedPrefHelper.getString("currentWorkingStatus","").equals("2")
                                            ||sharedPrefHelper.getString("currentWorkingStatus","").equals("5")){
                                        flag=true;
                                    }
                                }*/
                                else{
                                    flag=false;
                                    break;
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
                //back_status=false;
                if(flag==true){
                    btn_stop.setEnabled(false);
                    if(startScreenCount==0){
                        totalSingle = startPosition;
                    }
                    else{
                        lenSingle=totalSingle;
                        totalSingle = startPosition;
                    }
                    /*if (answerModelList != null) {
                        answerModelListSingle.clear();
                        for (int i = lenSingle; i < totalSingle; i++) {
                            answerModelListSingle.add(answerModelList.get(i));
                        }
                    }*/
                    if (answerModelList != null) {
                        int famiy_count=0;
                        int family_count_total=12;
                        answerTVtotal.clear();
                        outerloop:
                        for (int i = 0; i < answerModelList.size()/12; i++) {
                            ArrayList<AnswerModel> answerModelListSingle=new ArrayList<>();
                            for (int j = famiy_count; j <family_count_total; j++) {
                                if(answerModelList.get(j).getQuestionID().equals("33") && answerModelList.get(j).getOption_value().equals("")){
                                    Toast.makeText(context, "Please re-check member-"+(i+1), Toast.LENGTH_SHORT).show();
                                    break outerloop;
                                }else{
                                    answerModelListSingle.add(answerModelList.get(j));
                                }
                            }
                            famiy_count=famiy_count+12;
                            family_count_total=family_count_total+12;
                            answerTVtotal.add(answerModelListSingle);
                        }
                    }
                   /* if(answerTVtotal.size()>0){
                        if(startScreenCount<answerTVtotal.size()){
                            answerTVtotal.set(startScreenCount,answerModelListSingle);
                        }else{
                            answerTVtotal.add(answerModelListSingle);
                        }
                    }else{
                        answerTVtotal.add(answerModelListSingle);
                    }*/
                    if (!survey_id.equals("")) {
                        Gson gson = new Gson();
                        String listString = gson.toJson(
                                answerModelList,
                                new TypeToken<ArrayList<AnswerModel>>() {}.getType());
                        try {
                            JSONArray json_array =  new JSONArray(listString);
                            JSONObject json_object=new JSONObject();
                            json_object.put("family_data", json_array);
                            Log.e(TAG, "onClick: "+json_object.toString());
                            //update data in to local DB
                            sqliteHelper.updateFamilyDataInTable("survey", "survey_id", survey_id, json_object);
                            sqliteHelper.updateLocalFlag("partial", "survey",
                                    sharedPrefHelper.getString("survey_id", ""), 1);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if(answerTVtotal.size()==editFieldValues) {
                        activityCommunicator.passDataToActivity(answerTVtotal, answerModelList, 1, 1);
                        doBack();
                    }else{
                        Toast.makeText(context,"Please try again",Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    btn_stop.setEnabled(true);
                    Toast.makeText(getActivity(),"Please fill all required fields",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void openDialogForAgeConfirmation(EditText editText, int ageInYear) {
        if (ageInYear>120){
            /*final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            //new AlertDialog.Builder(context).setTitle("Alert!")
                    builder.setMessage("Age can't be greater then 120")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            //TODO here
                            editText.setText(null);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert).show();
                    builder.setCancelable(false);*/
            final SweetAlertDialog pDialog = new SweetAlertDialog(
                    context, SweetAlertDialog.WARNING_TYPE);
            //new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
            pDialog.setTitleText("Alert!")
                    .setContentText("Age can't be greater then 120")
                    .setConfirmText("OK")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismiss();
                            editText.setText(null);
                        }
                    })
                    .show();
            pDialog.setCancelable(false);
        }
        else {
            /*final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            //new AlertDialog.Builder(context).setTitle("Alert!")
            builder.setMessage("Are you sure age is greater then 99")
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
                            editText.setText(null);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert).show();
            builder.setCancelable(false);*/
            final SweetAlertDialog pDialog = new SweetAlertDialog(
                    context, SweetAlertDialog.WARNING_TYPE);
            //new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
            pDialog.setTitleText("Alert!")
                    .setContentText("Are you sure age is greater then 99")
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
                            editText.setText(null);
                        }
                    })
                    .show();
            pDialog.setCancelable(false);
        }
    }

    private void sendSurveyDataOnServer(RequestBody body) {
        AlertDialogClass.showProgressDialog(getActivity());
        ApiClient.getClient().create(BARC_API.class).sendSurveyData(body).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().toString());
                    //Log.e(TAG, "survey_data-: "+jsonObject.toString());
                    String success=jsonObject.getString("success");
                    String message=jsonObject.getString("message");
                    int survey_data_monitoring_id=jsonObject.getInt("survey_data_monitoring_id");
                    if (success.equals("1")) {
                        AlertDialogClass.dismissProgressDialog();
                        //update id on the bases of survey id
                        sqliteHelper.updateServerId("survey", survey_id, survey_data_monitoring_id);
                        sqliteHelper.updateLocalFlag("household_survey","survey", survey_id, 1);
                        Intent intentSurveyActivity1=new Intent(getActivity(), ClusterDetails.class);
                        startActivity(intentSurveyActivity1);
                    } else {
                        AlertDialogClass.dismissProgressDialog();
                        CommonClass.showPopupForNoInternet(getActivity());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(getActivity(), "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                AlertDialogClass.dismissProgressDialog();
            }
        });
    }


    public void questionsPopulate(){
        try{
            ll_parent.removeAllViews();
            startPositionBefore=startPosition;
            endPosition=0;
            String name="";
            if(startScreenPosition>=totalScreen){
                startScreenPosition=0;
                endScreenPosition=1;
                /*if(startScreenCount==0){
                    lenSingle=0;
                    totalSingle = startPosition;
                }
                else{
                    lenSingle=totalSingle;
                    totalSingle = startPosition;
                }*/
                /*ArrayList<AnswerModel> answerModelListSingle=new ArrayList<>();
                if (answerModelList != null) {
                    for (int i = lenSingle; i < totalSingle; i++) {
                        answerModelListSingle.add(answerModelList.get(i));
                    }
                }*/
               /* if(answerTVtotal.size()>0){
                    if(startScreenCount<answerTVtotal.size()){
                        answerTVtotal.set(startScreenCount,answerModelListSingle);
                    }else{
                        answerTVtotal.add(answerModelListSingle);
                    }
                }else{
                    answerTVtotal.add(answerModelListSingle);
                }*/
                startScreenCount++;
            }
            for(int l=startScreenPosition;l<endScreenPosition;l++){
                JSONObject jsonObjectScreen=jsonArrayScreen.getJSONObject(l);
                screen_id=jsonObjectScreen.getString("screen_no");
                jsonArrayQuestions = jsonObjectScreen.getJSONArray("questions");
                for (int i = 0; i < jsonArrayQuestions.length(); i++) {
                    JSONObject jsonObjectQuesType=jsonArrayQuestions.getJSONObject(i);
                    if (jsonObjectQuesType.getString("question_type").equals("1")) {
                        TextView txtLabel = new TextView(getActivity());
                        EditText editText=new EditText(getActivity());
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
                        }
                        if(jsonObjectQuesType.getString("question_input_type").equals("2")){
                            editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
                        }else{
                            editText.setInputType(InputType.TYPE_CLASS_TEXT);
                        }
                        if (jsonObjectQuesType.getString("question_id").equals("33")) {
                            int maxLength=50;
                            editText.addTextChangedListener(new LimitTextWatcher(editText, maxLength, new LimitTextWatcher.IF_callback() {
                                @Override
                                public void callback(int left) {
                                    if(left <= 0) {
                                        Toast.makeText(getActivity(), "input is full.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }));
                        }
                        if (jsonObjectQuesType.getString("question_id").equals("35")) {
                            int maxLength=3;
                            editText.addTextChangedListener(new LimitTextWatcher(editText, maxLength, new LimitTextWatcher.IF_callback() {
                                @Override
                                public void callback(int left) {
                                    if(left <= 0) {
                                        Toast.makeText(getActivity(), "input is full.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }));
                        }
                        if (jsonObjectQuesType.getString("question_id").equals("35")) {
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
                                            openDialogForAgeConfirmation(editText, ageInYear);
                                        }
                                        else if (sharedPrefHelper.getInt("editFieldValues", 0)==1){
                                            if (ageInYear<15){
                                                //openDialogForAgeConfirmationSingleHH(editText, ageInYear);
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void afterTextChanged(Editable editable) {

                                }
                            });
                        }
                        editText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(Integer.parseInt(jsonObjectQuesType.getString("max_limit")))});
                        if(jsonObjectQuesType.getString("pre_field").equals("1")){
                            editText.setEnabled(false);
                        }
                        if((back_status==true || screen_type.equals("survey_list")) && answerModelList.size()>startPosition){
                            editText.setText(answerModelList.get(startPosition).getOption_value());
                        }
                        String description=jsonObjectQuesType.getString("question_name");
                        description=description.replaceAll("\\$name",sharedPrefHelper.getString("name",""));
                        if(jsonObjectQuesType.getString("question_id").equals("33")){
                            int member_count=startScreenCount+1;
                            txtLabel.setText(description+"- Member-"+member_count);
                        }else{
                            txtLabel.setText(description);
                        }
                        txtLabel.setTypeface(null, Typeface.BOLD);
                        txtLabel.setTextSize(14);
                        startPosition++;
                        endPosition++;
                        ll_parent.addView(txtLabel);
                        ll_parent.addView(editText);
                        //onAddEditField(jsonObjectQuesType);
                    }
                    else if (jsonObjectQuesType.getString("question_type").equals("2")) {
                        TextView txtLabel = new TextView(getActivity());
                        String description=jsonObjectQuesType.getString("question_name");
                        description=description.replaceAll("\\$name",sharedPrefHelper.getString("name",""));
                        txtLabel.setText(description);
                        txtLabel.setTextSize(14);
                        txtLabel.setTypeface(null, Typeface.BOLD);
                        ll_parent.addView(txtLabel);
                        JSONArray jsonArrayOptions = jsonObjectQuesType.getJSONArray("question_options");
                        RadioGroup radioGroup=new RadioGroup(getActivity());
                        radioGroup.setId(Integer.parseInt(jsonObjectQuesType.getString("question_id")));
                        for (int j = 0; j <jsonArrayOptions.length() ; j++) {
                            RadioButton radioButton=new RadioButton(getActivity());
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
                                if(jsonObjectQuesType.getString("question_id").equals("40") && answerModelList.get(startPosition).getOption_id().equals("1")){
                                    radioButton.setEnabled(true);
                                    sharedPrefHelper.setString("CWE_Yes","1");
                                }
                                if(jsonObjectQuesType.getString("question_id").equals("39") && answerModelList.get(startPosition).getOption_id().equals("1")){
                                    radioButton.setEnabled(true);
                                    sharedPrefHelper.setString("HH_Yes","1");
                                }
                                else if(jsonObjectQuesType.getString("question_id").equals("40")) {
                                    if(sharedPrefHelper.getString("CWE_Yes","0").equals("1") || sharedPrefHelper.getString("CWE_Status","").equals("2")){
                                        radioButton.setEnabled(false);
                                    }
                                }
                                else if(jsonObjectQuesType.getString("question_id").equals("39") ) {
                                    if(sharedPrefHelper.getString("HH_Yes","0").equals("1")){
                                        radioButton.setEnabled(false);
                                    }
                                }
                            }
                            else if(jsonObjectQuesType.getString("question_id").equals("22") && jsonObjectOptionValues.getString("option_id").equals(sharedPrefHelper.getString("address_type", "0"))){
                                radioButton.setChecked(true);
                            }
                            else if(jsonObjectQuesType.getString("question_id").equals("39") && sharedPrefHelper.getString("HH_Yes","0").equals("1")){
                                radioButton.setEnabled(false);
                                if(jsonObjectOptionValues.getString("option_id").equals("2")){
                                    radioButton.setChecked(true);
                                }
                            }
                            else if(jsonObjectQuesType.getString("question_id").equals("39") && editFieldValues==1){
                                radioButton.setEnabled(false);
                                if(jsonObjectOptionValues.getString("option_id").equals("1")){
                                    radioButton.setChecked(true);
                                }
                            }
                            else if(jsonObjectQuesType.getString("question_id").equals("40") && sharedPrefHelper.getString("CWE_Yes","0").equals("1")){
                                radioButton.setEnabled(false);
                                if(jsonObjectOptionValues.getString("option_id").equals("2")){
                                    radioButton.setChecked(true);
                                }
                            }
                            else if (jsonObjectQuesType.getString("question_id").equals("40")&&sharedPrefHelper.getString("CWE_Status","").equals("2")){
                                radioButton.setEnabled(false);
                                if(jsonObjectOptionValues.getString("option_id").equals("2")){
                                    radioButton.setChecked(true);
                                }
                            }else if (jsonObjectQuesType.getString("question_id").equals("40")&&editFieldValues==1){
                                radioButton.setEnabled(false);
                                if(jsonObjectOptionValues.getString("option_id").equals("1")){
                                    radioButton.setChecked(true);
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
                                RadioButton rb=(RadioButton)getView().findViewById(checkedId);
                                String rbTag=rb.getTag().toString();
                                int sepPos = rbTag.indexOf("^");
                                String id=rbTag.substring(sepPos+1);
                                String radioID = rbTag.substring(0, sepPos);
                                String radioGroupID=String.valueOf(group.getId());
                               /* if(radioGroupID.equals("40") && radioID.equals("1")){
                                    sharedPrefHelper.setString("CWE_Yes","1");
                                }else if(radioGroupID.equals("40") && radioID.equals("2")){
                                    sharedPrefHelper.setString("CWE_Yes","");
                                }
                                if(radioGroupID.equals("39") && radioID.equals("1")){
                                    sharedPrefHelper.setString("HH_Yes","1");
                                }else if(radioGroupID.equals("39") && radioID.equals("2")){
                                    sharedPrefHelper.setString("HH_Yes","");
                                }*/
                               /* if(group.getId()==40){

                                }*/
                            }
                        });
                        //onAddRadioButton(jsonObjectQuesType);
                    }
                    else if (jsonObjectQuesType.getString("question_type").equals("3")) {
                        TextView txtLabel = new TextView(getActivity());
                        String description=jsonObjectQuesType.getString("question_name");
                        description=description.replaceAll("\\$name",sharedPrefHelper.getString("name",""));
                        txtLabel.setText(description);
                        txtLabel.setTextSize(14);
                        txtLabel.setTypeface(null, Typeface.BOLD);
                        ll_parent.addView(txtLabel);
                        JSONArray jsonArrayOptions = jsonObjectQuesType.getJSONArray("question_options");
                        TableLayout linearLayoutCheckbox= new TableLayout(getActivity());
                        linearLayoutCheckbox.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        String selectedOptions="";
                       /*if((back_status==true || screen_type.equals("survey_list")) && answerModelList.size()>i){
                           selectedOptions=answerModelList.get(startPosition).getOption_id();
                           String[] arraySelectedOptions = selectedOptions.split(",");
                       }*/
                        for (int j = 0; j <jsonArrayOptions.length(); j++) {
                            TableRow row =new TableRow(getActivity());
                            row.setId(j);
                            row.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            JSONObject jsonObject1=jsonArrayOptions.getJSONObject(j);
                            CheckBox checkBox=new CheckBox(getActivity());
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
                        TextView txtLabel = new TextView(getActivity());
                        String description=jsonObjectQuesType.getString("question_name");
                        description=description.replaceAll("\\$name",sharedPrefHelper.getString("name",""));
                        txtLabel.setText(description);
                        txtLabel.setTextSize(14);
                        txtLabel.setTypeface(null, Typeface.BOLD);
                        ll_parent.addView(txtLabel);

                        JSONArray jsonArrayOptions = jsonObjectQuesType.getJSONArray("question_options");
                        Spinner spinner=new Spinner(getActivity());
                        spinner.setId(Integer.parseInt(jsonObjectQuesType.getString("question_id")));
                        txtLabel.setId(Integer.parseInt(jsonObjectQuesType.getString("question_id")));
                        ArrayList<String> spinnerAL=new ArrayList<>();
                        if (jsonObjectQuesType.getString("question_id").equals("41")) {
                            ArrayList<String> nameAL = new ArrayList<>();
                            if (ageInYears > 12) {
                                spinner.setVisibility(View.GONE);
                                txtLabel.setVisibility(View.GONE);
                            }else {
                                Map<Integer, String> treeMapAge = new TreeMap<>(ageVL);
                                Map<Integer, String> treeMapName = new TreeMap<>(nameVL);
                                int spinnerIndex=1;
                                for (Map.Entry<Integer, String> entry : treeMapAge.entrySet()) {
                                    if (Integer.parseInt(entry.getValue()) > 15) {
                                        nameAL.add(treeMapName.get(entry.getKey())+"-"+spinnerIndex);
                                    }
                                    spinnerIndex++;
                                }
                            }
                            nameAL.add(0, getString(R.string.select_option));
                            ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), R.layout.custom_spinner_dropdown, nameAL);
                            spinner.setAdapter(arrayAdapter);
                            if ((back_status == true || screen_type.equals("survey_list")) && answerModelList.size() > startPosition) {
                                if(!answerModelList.get(startPosition).getOption_id().equals("0")) {
                                    int position = 0;
                                    int spnposI = 0;
                                    int spnposP = 0;
                                    Map<Integer, String> treeMapAge = new TreeMap<>(ageVL);
                                    for (Map.Entry<Integer, String> entry : treeMapAge.entrySet()) {
                                        if (Integer.parseInt(entry.getValue()) > 15) {
                                            if (Integer.parseInt(answerModelList.get(startPosition).getOption_id()) - 1 == spnposP) {
                                                position = spnposI;
                                                break;
                                            }
                                            spnposI++;
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
                                if (jsonObjectQuesType.getString("question_id").equals("36") && sharedPrefHelper.getInt("ageInYears", 0) <5) {
                                    for (int k = 0; k < 1; k++) {
                                        JSONObject jsonObjectOptionValues = jsonArrayOptions.getJSONObject(k);
                                        String spinnerOption = jsonObjectOptionValues.getString("option_value");
                                        spinnerAL.add(spinnerOption);
                                    }
                                }
                                else if (jsonObjectQuesType.getString("question_id").equals("36") && sharedPrefHelper.getInt("ageInYears", 0) <=15) {
                                    for (int k = 0; k < 5; k++) {
                                        JSONObject jsonObjectOptionValues = jsonArrayOptions.getJSONObject(k);
                                        String spinnerOption = jsonObjectOptionValues.getString("option_value");
                                        spinnerAL.add(spinnerOption);
                                    }
                                }
                                else if (jsonObjectQuesType.getString("question_id").equals("37") && sharedPrefHelper.getInt("ageInYears", 0) < 5) {
                                    for (int k = 0; k < 1; k++) {
                                        JSONObject jsonObjectOptionValues = jsonArrayOptions.getJSONObject(k);
                                        String spinnerOption = jsonObjectOptionValues.getString("option_value");
                                        spinnerAL.add(spinnerOption);
                                    }
                                }
                                else if (jsonObjectQuesType.getString("question_id").equals("38")) {
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
                                else if(jsonObjectQuesType.getString("question_id").equals("42")){
                                    String CWE_Status = sharedPrefHelper.getString("CWE_Status", "");
                                    String Q1h_Member_CWE = sharedPrefHelper.getString("Q1h_Member_CWE", "");
                                        if (CWE_Status.equals("2")) {
                                            for (int k = 0; k < 16; k++) {
                                                JSONObject jsonObjectOptionValues = jsonArrayOptions.getJSONObject(k);
                                                String spinnerOption = jsonObjectOptionValues.getString("option_value");
                                                spinnerAL.add(spinnerOption);
                                            }
                                        }
                                        else if (CWE_Status.equals("1")) {
                                            String currentOccupation = "17";
                                            for (int k = 0; k < jsonArrayOptions.length(); k++) {
                                                if(Q1h_Member_CWE.equals("1")){
                                                    if(jsonArrayOptions.getJSONObject(k).getString("option_id").equals(currentOccupation)) {
                                                        JSONObject jsonObjectOptionValues = jsonArrayOptions.getJSONObject(k);
                                                        String spinnerOption = jsonObjectOptionValues.getString("option_value");
                                                        spinnerAL.add(spinnerOption);
                                                    }
                                                }else{
                                                    if(!jsonArrayOptions.getJSONObject(k).getString("option_id").equals(currentOccupation)) {
                                                        JSONObject jsonObjectOptionValues = jsonArrayOptions.getJSONObject(k);
                                                        String spinnerOption = jsonObjectOptionValues.getString("option_value");
                                                        spinnerAL.add(spinnerOption);
                                                    }
                                                }
                                            }
                                        }
                                }
                                else {
                                    for (int k = 0; k < jsonArrayOptions.length(); k++) {
                                        JSONObject jsonObjectOptionValues = jsonArrayOptions.getJSONObject(k);
                                        String spinnerOption = jsonObjectOptionValues.getString("option_value");
                                        spinnerAL.add(spinnerOption);
                                    }
                                }

                                /*if (sharedPrefHelper.getString("Q1h_Member_CWE","").equals("1")){
                                    //spinnerAL2.add(0, getString(R.string.select_option));
                                    ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), R.layout.custom_spinner_dropdown, spinnerAL2);
                                    spinner.setAdapter(arrayAdapter);
                                }else if(sharedPrefHelper.getString("Q1h_Member_CWE","").equals("2")){
                                    //spinnerAL2.add(0, getString(R.string.select_option));
                                    ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), R.layout.custom_spinner_dropdown, spinnerAL2);
                                    spinner.setAdapter(arrayAdapter);
                                }else {*/
                                    spinnerAL.add(0, getString(R.string.select_option));
                                    ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), R.layout.custom_spinner_dropdown, spinnerAL);
                                    spinner.setAdapter(arrayAdapter);
                                    /*if (jsonObjectQuesType.getString("question_id").equals("36") && sharedPrefHelper.getInt("ageInYears", 0) < 5) {
                                        *//*if (sharedPrefHelper.getInt("ageInYears",0) < 5) {*//*
                                        String education = "Illiterate";
                                        int spinnerPosition = 0;
                                        String strpos1 = education;
                                        if (strpos1 != null || !strpos1.equals(null) || !strpos1.equals("")) {
                                            strpos1 = education;
                                            spinnerPosition = arrayAdapter.getPosition(strpos1);
                                            spinner.setSelection(spinnerPosition);
                                            spinnerPosition = 0;
                                            *//*}*//*
                                        }
                                    }*/
                                   /* if (jsonObjectQuesType.getString("question_id").equals("37") && sharedPrefHelper.getInt("ageInYears", 0) < 5) {
                                        String workingStatus = "Not Working";
                                        int spinnerPosition = 0;
                                        String strpos1 = workingStatus;
                                        if (strpos1 != null || !strpos1.equals(null) || !strpos1.equals("")) {
                                            strpos1 = workingStatus;
                                            spinnerPosition = arrayAdapter.getPosition(strpos1);
                                            spinner.setSelection(spinnerPosition);
                                            spinnerPosition = 0;
                                        }
                                    }*/
                                   /* if (jsonObjectQuesType.getString("question_id").equals("38")){
                                        String currentWorkingStatus=sharedPrefHelper.getString("currentWorkingStatus","");
                                        if (currentWorkingStatus.equals("3")||currentWorkingStatus.equals("4")
                                                ||currentWorkingStatus.equals("6")||currentWorkingStatus.equals("7")){
                                            String currentOccupation="Not Applicable";
                                            int spinnerPosition = 0;
                                            String strpos1 = currentOccupation;
                                            if (strpos1 != null || !strpos1.equals(null) || !strpos1.equals("")) {
                                                strpos1 = currentOccupation;
                                                spinnerPosition = arrayAdapter.getPosition(strpos1);
                                                spinner.setSelection(spinnerPosition);
                                                spinnerPosition = 0;
                                            }
                                        }
                                    }*/
                                //}

                                if ((back_status == true || screen_type.equals("survey_list")) && answerModelList.size() > startPosition) {
                                    int spinnerpos=0;
                                    boolean isBreak=false;

                                    for(int j = 0; j < spinnerAL.size(); j++){
                                        for (int k = 0; k < jsonArrayOptions.length(); k++) {
                                            JSONObject jsonObjectOptionValues = jsonArrayOptions.getJSONObject(k);
                                            String spinnerOptionOptionID = jsonObjectOptionValues.getString("option_id");
                                            if(spinnerOptionOptionID.equals(answerModelList.get(startPosition).getOption_id()) && spinnerAL.get(j).equals(jsonObjectOptionValues.getString("option_value"))){
                                                //spinnerpos++;
                                                isBreak=true;
                                                break;
                                            }
                                        }
                                        if(isBreak){ break; }else{
                                            if(spinnerpos<spinnerAL.size()-1){
                                                spinnerpos++;
                                            }
                                        }
                                    }
                                    //spinner.setSelection(Integer.parseInt(answerModelList.get(startPosition).getOption_id()));
                                    spinner.setSelection(spinnerpos);
                                }
                            //}
                        }

                        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                /*long spinnerID=spinner.getSelectedItemId();
                                try {
                                    if (jsonObjectQuesType.getString("question_id").equals("37")) {
                                        for (int v = 0; v < ll_parent.getChildCount(); v++) {
                                            final View childView = ll_parent.getChildAt(v);
                                            sharedPrefHelper.setString("currentWorkingStatus", Long.toString(spinnerID));
                                            String currentWorkingStatus = sharedPrefHelper.getString("currentWorkingStatus", "");
                                    if (childView instanceof Spinner) {
                                        Spinner spinner = (Spinner) childView;
                                            if(String.valueOf(spinner.getId()).equals("38")){
                                                if (currentWorkingStatus.equals("1")||currentWorkingStatus.equals("2")||currentWorkingStatus.equals("5")){
                                                    spinner.setVisibility(View.GONE);
                                                }else{
                                                    spinner.setVisibility(View.VISIBLE);
                                                }
                                            }
                                            }
                                            else if (childView instanceof TextView) {
                                                TextView textView = (TextView) childView;
                                                if(String.valueOf(textView.getId()).equals("38")){
                                                    if (currentWorkingStatus.equals("1")||currentWorkingStatus.equals("2")||currentWorkingStatus.equals("5")){
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
                                }*/
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });

                        startPosition++;
                        endPosition++;
                        ll_parent.addView(spinner);

                        // onAddSpinner(jsonObjectQuesType);
                    }
                    else if (jsonObjectQuesType.getString("question_type").equals("5")) {
                        Button button=new Button(getActivity());
                        TextView textView=new TextView(getActivity());
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
                        TextView textView=new TextView(getActivity());
                        textView.setId(Integer.parseInt(jsonObjectQuesType.getString("question_id")));
                        String description=jsonObjectQuesType.getString("question_name");
                        description=description.replaceAll("\\$name",sharedPrefHelper.getString("name",""));
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

    private void openDialogForAgeConfirmationSingleHH(int ageInYear) {
        if(sharedPrefHelper.getInt("editFieldValues",0)==1&&ageInYear<15){
            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            //new AlertDialog.Builder(context).setTitle("Alert!")
                    builder.setMessage("If family member is only one in HH then age can't be less then 15.")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            //TODO here
                            //editText.setText(null);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            //TODO here
                            //editText.setText(null);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert).show();
                    builder.setCancelable(false);
        }
    }

    private void setTerminattion(String id) {
        Intent intentTerminate=new Intent(getActivity(), TerminateActivity.class);
        intentTerminate.putExtra("screen_type", "terminate");
        intentTerminate.putExtra("radio_button_id", id);
        startActivity(intentTerminate);
    }
    @Override
    public void doBack() {
        getFragmentManager().popBackStack();
    }

    @Override
    public void passDataToFragment(String someValue) {

    }

}
