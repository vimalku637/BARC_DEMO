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

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.vrp.barc_demo.utils.CommonClass;
import com.vrp.barc_demo.utils.SharedPrefHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "Login_Activity";
    @BindView(R.id.et_user_name)
    TextInputEditText et_user_name;
    @BindView(R.id.et_password)
    TextInputEditText et_password;
    @BindView(R.id.btn_submit)
    MaterialButton btn_submit;
    @BindView(R.id.tv_forgot_password)
    MaterialTextView tv_forgot_password;

    /*normal widgets*/
    private Context context=this;
    SharedPrefHelper sharedPrefHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        setTitle(R.string.login);
        initialization();
        /*get intent values here*/
        Bundle bundle=getIntent().getExtras();
        if (bundle!=null) {
        }

        submitButtonClick();
    }

    private void initialization() {
        sharedPrefHelper=new SharedPrefHelper(this);
    }

    private void submitButtonClick() {
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentMainActivity=new Intent(context, DownloadSurvey.class);
                startActivity(intentMainActivity);
            }
        });
        tv_forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentForgotPassword=new Intent(context, ForgotPassword.class);
                startActivity(intentForgotPassword);
            }
        });
    }
}
