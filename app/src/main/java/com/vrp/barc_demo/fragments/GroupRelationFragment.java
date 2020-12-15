/*
 * Copyright (c)  2020. Indev Consultancy Private Limited,
 * Auther : Vimal Kumar
 * Date : 2020/12/15
 * Modified Date :
 * Modified By :
 */

package com.vrp.barc_demo.fragments;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
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

import com.vrp.barc_demo.R;
import com.vrp.barc_demo.models.AnswerModel;
import com.vrp.barc_demo.sqlite_db.SqliteHelper;
import com.vrp.barc_demo.utils.SharedPrefHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class GroupRelationFragment extends Fragment {
    @BindView(R.id.ll_parent)
    LinearLayout ll_parent;

    /*normal widgets*/
    private Unbinder unbinder;
    private SqliteHelper sqliteHelper;
    private SharedPrefHelper sharedPrefHelper;
    String screen_id=null;

    public GroupRelationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_group_relation, container, false);
        unbinder = ButterKnife.bind(this, view);

        initialization();

        return view;
    }

    private void initialization() {
        sqliteHelper=new SqliteHelper(getActivity());
        sharedPrefHelper=new SharedPrefHelper(getActivity());
    }

    /*public void questionsPopulate(){
        try{
            ll_parent.removeAllViews();
            startPositionBefore=startPosition;
            endPosition=0;
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
                        description=description.replaceAll("$name",sharedPrefHelper.getString("name","Ram"));
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
                       *//*if((back_status==true || screen_type.equals("survey_list")) && answerModelList.size()>i){
                           selectedOptions=answerModelList.get(startPosition).getOption_id();
                           String[] arraySelectedOptions = selectedOptions.split(",");
                       }*//*
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

    }*/

}
