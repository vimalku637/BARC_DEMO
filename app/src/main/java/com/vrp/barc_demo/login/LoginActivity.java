package com.vrp.barc_demo.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.vrp.barc_demo.forgot_password.ForgotPassword;
import com.vrp.barc_demo.R;
import com.vrp.barc_demo.activities.UpdateQuestions;
import com.vrp.barc_demo.utils.SharedPrefHelper;

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
    @BindView(R.id.til_user_name)
    TextInputLayout til_user_name;

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
                //if (checkValidation()) {
                    Intent intentMainActivity = new Intent(context, UpdateQuestions.class);
                    startActivity(intentMainActivity);
                //}
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

    /*private boolean checkValidation() {
    }*/
}
