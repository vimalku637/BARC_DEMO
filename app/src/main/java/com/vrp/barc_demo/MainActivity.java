package com.vrp.barc_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Main_Activity";
    private Button btn_submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

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
                for (int i = 0; i < jsonArrayQuestions.length(); i++) {
                    JSONObject jsonObjectQuesType=jsonArrayQuestions.getJSONObject(i);
                    if (jsonObjectQuesType.getString("question_type").equals("1")) {
                        onAddEditField(jsonObjectQuesType);
                    }
                    else if (jsonObjectQuesType.getString("question_type").equals("2")) {
                        onAddRadioButton(jsonObjectQuesType);
                    }
                    else if (jsonObjectQuesType.getString("question_type").equals("3")) {
                        onAddCheckBox(jsonObjectQuesType);
                    }
                    else if (jsonObjectQuesType.getString("question_type").equals("4")) {
                        onAddSpinner(jsonObjectQuesType);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void onAddEditField(JSONObject jsonObjectQuesType) {
        LinearLayout ll_parent=findViewById(R.id.ll_parent);

        LayoutInflater inflater=(LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView=inflater.inflate(R.layout.edit_text_layout, null);
        /*here are fields*/
        TextView tv_question=rowView.findViewById(R.id.tv_question);
        try {
            tv_question.setText(jsonObjectQuesType.getString("question_name"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        EditText edit_text=rowView.findViewById(R.id.et_answer);
        edit_text.setHint("Edit Field");

        // Add the new row before the add field button.
        ll_parent.addView(rowView, ll_parent.getChildCount());
    }
    public void onAddSpinner(JSONObject jsonObjectQuesType) {
        LinearLayout ll_parent=findViewById(R.id.ll_parent);
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
        LinearLayout ll_parent=findViewById(R.id.ll_parent);

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
        LinearLayout ll_parent=findViewById(R.id.ll_parent);
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
        btn_submit=findViewById(R.id.btn_submit);
    }
}
