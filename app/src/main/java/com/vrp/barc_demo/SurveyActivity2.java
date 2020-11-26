package com.vrp.barc_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import android.widget.TextView;

import com.vrp.barc_demo.utils.CommonClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SurveyActivity2 extends AppCompatActivity {
    private static final String TAG = "Main_Activity";
    private Button btn_previous, btn_stop, btn_next;

    /*normal widgets*/
    private Context context=this;
    private LinearLayout ll_parent;
    private String survey_id;
    private int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey2);

        initViews();
        /*get intent values here*/
        Bundle bundle=getIntent().getExtras();
        if (bundle!=null) {
            survey_id=bundle.getString("survey_id", "");
            count=bundle.getInt("count", 0);
        }

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject("{\n" +
                    "\t\"questions\": [\n" +
                    "\t  {\n" +
                    "\t\t\t\"question_id\": \"1\",\n" +
                    "\t\t\t\"language_id\": \"1\",\n" +
                    "\t\t\t\"question_name\": \"Question 1\",\n" +
                    "\t\t\t\"question_type\": \"1\",\n" +
                    "\t\t\t\"question_input_type\": \"1\",\n" +
                    "\t\t\t\"validation_id\": \"1\",\n" +
                    "\t\t\t\"question_options\": []\n" +
                    "\t\t},\n" +
                    "\t\t{\n" +
                    "\t\t\t\"question_id\": \"1\",\n" +
                    "\t\t\t\"language_id\": \"1\",\n" +
                    "\t\t\t\"question_name\": \"Question 2\",\n" +
                    "\t\t\t\"question_type\": \"1\",\n" +
                    "\t\t\t\"question_input_type\": \"2\",\n" +
                    "\t\t\t\"validation_id\": \"1,2\",\n" +
                    "\t\t\t\"question_options\": []\n" +
                    "\t\t},\n" +
                    "\t\t{\n" +
                    "\t\t\t\"question_id\": \"1\",\n" +
                    "\t\t\t\"language_id\": \"1\",\n" +
                    "\t\t\t\"question_name\": \"Question 3\",\n" +
                    "\t\t\t\"question_type\": \"2\",\n" +
                    "\t\t\t\"question_input_type\": \"0\",\n" +
                    "\t\t\t\"validation_id\": \"\",\n" +
                    "\t\t\t\"question_options\": [{\n" +
                    "\t\t\t\t\t\"option_value\": \"Yes\",\n" +
                    "\t\t\t\t\t\"option_id\": \"1\"\n" +
                    "\t\t\t\t},\n" +
                    "\t\t\t\t{\n" +
                    "\t\t\t\t\t\"option_value\": \"No\",\n" +
                    "\t\t\t\t\t\"option_id\": \"2\"\n" +
                    "\t\t\t\t}\n" +
                    "\t\t\t]\n" +
                    "\t\t},\n" +
                    "\t\t{\n" +
                    "\t\t\t\"question_id\": \"1\",\n" +
                    "\t\t\t\"language_id\": \"1\",\n" +
                    "\t\t\t\"question_name\": \"Question 4\",\n" +
                    "\t\t\t\"question_type\": \"3\",\n" +
                    "\t\t\t\"question_input_type\": \"1\",\n" +
                    "\t\t\t\"validation_id\": \"1\",\n" +
                    "\t\t\t\"question_options\": [{\n" +
                    "\t\t\t\t\t\"option_value\": \"Checkbox11\",\n" +
                    "\t\t\t\t\t\"option_id\": \"1\"\n" +
                    "\t\t\t\t},\n" +
                    "\t\t\t\t{\n" +
                    "\t\t\t\t\t\"option_value\": \"Checkbox12\",\n" +
                    "\t\t\t\t\t\"option_id\": \"2\"\n" +
                    "\t\t\t\t},\n" +
                    "\t\t\t\t{\n" +
                    "\t\t\t\t\t\"option_value\": \"Checkbox13\",\n" +
                    "\t\t\t\t\t\"option_id\": \"3\"\n" +
                    "\t\t\t\t},\n" +
                    "\t\t\t\t{\n" +
                    "\t\t\t\t\t\"option_value\": \"Checkbox14\",\n" +
                    "\t\t\t\t\t\"option_id\": \"4\"\n" +
                    "\t\t\t\t},\n" +
                    "\t\t\t\t{\n" +
                    "\t\t\t\t\t\"option_value\": \"Checkbox15\",\n" +
                    "\t\t\t\t\t\"option_id\": \"5\"\n" +
                    "\t\t\t\t}\n" +
                    "\t\t\t]\n" +
                    "\t\t},\n" +
                    "\t\t{\n" +
                    "\t\t\t\"question_id\": \"1\",\n" +
                    "\t\t\t\"language_id\": \"1\",\n" +
                    "\t\t\t\"question_name\": \"Question 5\",\n" +
                    "\t\t\t\"question_type\": \"4\",\n" +
                    "\t\t\t\"question_input_type\": \"1\",\n" +
                    "\t\t\t\"validation_id\": \"\",\n" +
                    "\t\t\t\"question_options\": [{\n" +
                    "\t\t\t\t\t\"option_value\": \"Spinner11\",\n" +
                    "\t\t\t\t\t\"option_id\": \"1\"\n" +
                    "\t\t\t\t},\n" +
                    "\t\t\t\t{\n" +
                    "\t\t\t\t\t\"option_value\": \"Spinner12\",\n" +
                    "\t\t\t\t\t\"option_id\": \"2\"\n" +
                    "\t\t\t\t},\n" +
                    "\t\t\t\t{\n" +
                    "\t\t\t\t\t\"option_value\": \"Spinner13\",\n" +
                    "\t\t\t\t\t\"option_id\": \"3\"\n" +
                    "\t\t\t\t},\n" +
                    "\t\t\t\t{\n" +
                    "\t\t\t\t\t\"option_value\": \"Spinner14\",\n" +
                    "\t\t\t\t\t\"option_id\": \"4\"\n" +
                    "\t\t\t\t},\n" +
                    "\t\t\t\t{\n" +
                    "\t\t\t\t\t\"option_value\": \"Spinner15\",\n" +
                    "\t\t\t\t\t\"option_id\": \"5\"\n" +
                    "\t\t\t\t}\n" +
                    "\t\t\t]\n" +
                    "\t\t},\n" +
                    "\t\t{\n" +
                    "\t\t\t\"question_id\": \"1\",\n" +
                    "\t\t\t\"language_id\": \"1\",\n" +
                    "\t\t\t\"question_name\": \"Question 6\",\n" +
                    "\t\t\t\"question_type\": \"4\",\n" +
                    "\t\t\t\"question_input_type\": \"1\",\n" +
                    "\t\t\t\"validation_id\": \"1\",\n" +
                    "\t\t\t\"question_options\": [{\n" +
                    "\t\t\t\t\t\"option_value\": \"Spinner21\",\n" +
                    "\t\t\t\t\t\"option_id\": \"1\"\n" +
                    "\t\t\t\t},\n" +
                    "\t\t\t\t{\n" +
                    "\t\t\t\t\t\"option_value\": \"Spinner22\",\n" +
                    "\t\t\t\t\t\"option_id\": \"2\"\n" +
                    "\t\t\t\t},\n" +
                    "\t\t\t\t{\n" +
                    "\t\t\t\t\t\"option_value\": \"Spinner23\",\n" +
                    "\t\t\t\t\t\"option_id\": \"3\"\n" +
                    "\t\t\t\t},\n" +
                    "\t\t\t\t{\n" +
                    "\t\t\t\t\t\"option_value\": \"Spinner24\",\n" +
                    "\t\t\t\t\t\"option_id\": \"4\"\n" +
                    "\t\t\t\t},\n" +
                    "\t\t\t\t{\n" +
                    "\t\t\t\t\t\"option_value\": \"Spinner25\",\n" +
                    "\t\t\t\t\t\"option_id\": \"5\"\n" +
                    "\t\t\t\t}\n" +
                    "\t\t\t]\n" +
                    "\t\t},\n" +
                    "\t\t{\n" +
                    "\t\t\t\"question_id\": \"1\",\n" +
                    "\t\t\t\"language_id\": \"1\",\n" +
                    "\t\t\t\"question_name\": \"Question 7\",\n" +
                    "\t\t\t\"question_type\": \"3\",\n" +
                    "\t\t\t\"question_input_type\": \"1\",\n" +
                    "\t\t\t\"validation_id\": \"\",\n" +
                    "\t\t\t\"question_options\": [{\n" +
                    "\t\t\t\t\t\"option_value\": \"Checkbox21\",\n" +
                    "\t\t\t\t\t\"option_id\": \"1\"\n" +
                    "\t\t\t\t},\n" +
                    "\t\t\t\t{\n" +
                    "\t\t\t\t\t\"option_value\": \"Checkbox22\",\n" +
                    "\t\t\t\t\t\"option_id\": \"2\"\n" +
                    "\t\t\t\t},\n" +
                    "\t\t\t\t{\n" +
                    "\t\t\t\t\t\"option_value\": \"Checkbox23\",\n" +
                    "\t\t\t\t\t\"option_id\": \"3\"\n" +
                    "\t\t\t\t},\n" +
                    "\t\t\t\t{\n" +
                    "\t\t\t\t\t\"option_value\": \"Checkbox24\",\n" +
                    "\t\t\t\t\t\"option_id\": \"4\"\n" +
                    "\t\t\t\t},\n" +
                    "\t\t\t\t{\n" +
                    "\t\t\t\t\t\"option_value\": \"Checkbox25\",\n" +
                    "\t\t\t\t\t\"option_id\": \"5\"\n" +
                    "\t\t\t\t}\n" +
                    "\t\t\t]\n" +
                    "\t\t},\n" +
                    "\t\t{\n" +
                    "\t\t\t\"question_id\": \"1\",\n" +
                    "\t\t\t\"language_id\": \"1\",\n" +
                    "\t\t\t\"question_name\": \"Question 8\",\n" +
                    "\t\t\t\"question_type\": \"1\",\n" +
                    "\t\t\t\"question_input_type\": \"1\",\n" +
                    "\t\t\t\"validation_id\": \"\",\n" +
                    "\t\t\t\"question_options\": []\n" +
                    "\t\t},\n" +
                    "\t\t{\n" +
                    "\t\t\t\"question_id\": \"1\",\n" +
                    "\t\t\t\"language_id\": \"1\",\n" +
                    "\t\t\t\"question_name\": \"Question 9\",\n" +
                    "\t\t\t\"question_type\": \"1\",\n" +
                    "\t\t\t\"question_input_type\": \"2\",\n" +
                    "\t\t\t\"validation_id\": \"1\",\n" +
                    "\t\t\t\"question_options\": []\n" +
                    "\t\t},\n" +
                    "\t\t{\n" +
                    "\t\t\t\"question_id\": \"1\",\n" +
                    "\t\t\t\"language_id\": \"1\",\n" +
                    "\t\t\t\"question_name\": \"Are sure to complete questionnaire\",\n" +
                    "\t\t\t\"question_type\": \"2\",\n" +
                    "\t\t\t\"question_input_type\": \"1\",\n" +
                    "\t\t\t\"validation_id\": \"1\",\n" +
                    "\t\t\t\"question_options\": [{\n" +
                    "\t\t\t\t\t\"option_value\": \"Yes\",\n" +
                    "\t\t\t\t\t\"option_id\": \"1\"\n" +
                    "\t\t\t\t},\n" +
                    "\t\t\t\t{\n" +
                    "\t\t\t\t\t\"option_value\": \"No\",\n" +
                    "\t\t\t\t\t\"option_id\": \"2\"\n" +
                    "\t\t\t\t}\n" +
                    "\t\t\t]\n" +
                    "\t\t}\n" +
                    "\n" +
                    "\t],\n" +
                    "\t\"questions_type\": [\n" +
                    "\t  {\n" +
                    "\t\t\t\"question_type\": \"Text\",\n" +
                    "\t\t\t\"question_type_id\": \"1\"\n" +
                    "\t\t},\n" +
                    "\t\t{\n" +
                    "\t\t\t\"question_type\": \"Radio\",\n" +
                    "\t\t\t\"question_type_id\": \"2\"\n" +
                    "\t\t},\n" +
                    "\t\t{\n" +
                    "\t\t\t\"question_type\": \"Checkbox\",\n" +
                    "\t\t\t\"question_type_id\": \"3\"\n" +
                    "\t\t},\n" +
                    "\t\t{\n" +
                    "\t\t\t\"question_type\": \"spinner\",\n" +
                    "\t\t\t\"question_type_id\": \"4\"\n" +
                    "\t\t}\n" +
                    "\t],\n" +
                    "\t\"questions_input_type\": [\n" +
                    "\t  {\n" +
                    "\t\t\t\"questions_input_type\": \"Text\",\n" +
                    "\t\t\t\"questions_input_type_id\": \"1\"\n" +
                    "\t\t},\n" +
                    "\t\t{\n" +
                    "\t\t\t\"questions_input_type\": \"Integer\",\n" +
                    "\t\t\t\"questions_input_type\": \"2\"\n" +
                    "\t\t},\n" +
                    "\t\t{\n" +
                    "\t\t\t\"questions_input_type\": \"Multiple\",\n" +
                    "\t\t\t\"questions_input_type\": \"3\"\n" +
                    "\t\t}\n" +
                    "\t],\n" +
                    "\t\"validations\": [\n" +
                    "\t  {\n" +
                    "\t\t\t\"validation_type\": \"required\",\n" +
                    "\t\t\t\"questions_input_type_id\": \"1\"\n" +
                    "\t\t},\n" +
                    "\t\t{\n" +
                    "\t\t\t\"questions_input_type\": \"can't be less then 18\",\n" +
                    "\t\t\t\"questions_input_type\": \"2\"\n" +
                    "\t\t}\n" +
                    "\t],\n" +
                    "\t\"languages\": [\n" +
                    "\t  {\n" +
                    "\t\t\t\"language_name\": \"English\",\n" +
                    "\t\t\t\"language_name_id\": \"1\"\n" +
                    "\t\t},\n" +
                    "\t\t{\n" +
                    "\t\t\t\"language_name\": \"Hindi\",\n" +
                    "\t\t\t\"language_name_id\": \"1\"\n" +
                    "\t\t}\n" +
                    "\t]\n" +
                    "}");

            if (jsonObject.has("questions")) {
                JSONArray jsonArrayQuestions = jsonObject.getJSONArray("questions");
                Log.e("questions", "onCreate: " + jsonArrayQuestions.toString());
                for (int i = count; i < count+3; i++) {
                    JSONObject jsonObjectQuesType=jsonArrayQuestions.getJSONObject(i);
                    if (jsonObjectQuesType.getString("question_type").equals("1")) {
                        TextView txtLabel = new TextView(this);
                        EditText editText=new EditText(this);
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
                        for (int j = 0; j <jsonArrayOptions.length() ; j++) {
                            RadioGroup radioGroup=new RadioGroup(this);
                            RadioButton radioButton=new RadioButton(this);
                            radioButton.setLayoutParams(new LinearLayout.LayoutParams
                                    (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            JSONObject jsonObjectOptionValues=jsonArrayOptions.getJSONObject(j);
                            radioButton.setText(jsonObjectOptionValues.getString("option_value"));
                            if (radioGroup != null) {
                                radioGroup.addView(radioButton);
                            }
                            ll_parent.addView(radioGroup);

                        }
                        //onAddRadioButton(jsonObjectQuesType);
                    }
                    else if (jsonObjectQuesType.getString("question_type").equals("3")) {
                        TextView txtLabel = new TextView(this);
                        txtLabel.setText(jsonObjectQuesType.getString("question_name"));
                        ll_parent.addView(txtLabel);
                        JSONArray jsonArrayOptions = jsonObjectQuesType.getJSONArray("question_options");
                        for (int j = 0; j <jsonArrayOptions.length() ; j++) {
                            JSONObject jsonObject1=jsonArrayOptions.getJSONObject(j);
                            CheckBox checkBox=new CheckBox(this);
                            checkBox.setText(jsonObject1.getString("option_value"));
                            ll_parent.addView(checkBox);

                        }

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

                        }
                        ll_parent.addView(spinner);

                        // onAddSpinner(jsonObjectQuesType);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        submitButtonClick();
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

    private void initViews() {
        btn_previous=findViewById(R.id.btn_previous);
        btn_stop=findViewById(R.id.btn_stop);
        btn_next=findViewById(R.id.btn_next);
        ll_parent=findViewById(R.id.ll_parent);
    }

    private void submitButtonClick() {
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONArray jsonArray = new JSONArray();
                final JSONObject jsonObject = new JSONObject();
                for (int i = 0; i < ll_parent.getChildCount(); i++) {
                    final View childView = ll_parent.getChildAt(i);
                    try {
                        if (childView instanceof EditText) {
                            EditText editText = (EditText) childView;
                            jsonArray.put(editText.getText().toString().trim());
                            jsonObject.put("edit_text", jsonArray);
                        }
                        else if (childView instanceof RadioGroup) {
                            RadioGroup radioGroup=(RadioGroup) childView;
                            int selectedRadioButtonId = radioGroup.getCheckedRadioButtonId();
                            android.widget.RadioButton selectedRadioButton = (android.widget.RadioButton) view.findViewById(selectedRadioButtonId);
                            if (selectedRadioButton!=null) {
                                jsonArray.put(selectedRadioButton.getText().toString().trim());
                                jsonObject.put("radio_button", jsonArray);
                            }
                        }
                        else if (childView instanceof Spinner) {
                            Spinner spinner = (Spinner) childView;
                            jsonArray.put(spinner.getSelectedItem().toString().trim());
                            jsonObject.put("spinner", jsonArray);
                        }
                        else if (childView instanceof CheckBox) {
                            CheckBox checkBox = (CheckBox) childView;
                            if (checkBox.isChecked()) {
                                jsonArray.put(checkBox.getText().toString().trim());
                                jsonObject.put("check_box", jsonArray);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Intent intentSurveyActivity3=new Intent(context, SurveyActivity3.class)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intentSurveyActivity3.putExtra("count", count+3);
                    intentSurveyActivity3.putExtra("survey_id", survey_id);
                    startActivity(intentSurveyActivity3);
                    finish();
                    Log.e(TAG, "onNextClick2- "+jsonObject.toString());
                }
            }
        });
        btn_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommonClass.setPopupForStopSurvey(context);
            }
        });
    }
}
