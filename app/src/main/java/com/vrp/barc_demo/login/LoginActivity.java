package com.vrp.barc_demo.login;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.vrp.barc_demo.forgot_password.ForgotPassword;
import com.vrp.barc_demo.R;
import com.vrp.barc_demo.activities.UpdateQuestions;
import com.vrp.barc_demo.models.LoginModel;
import com.vrp.barc_demo.rest_api.ApiClient;
import com.vrp.barc_demo.rest_api.BARC_API;
import com.vrp.barc_demo.utils.SharedPrefHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
ProgressDialog mprogressDialog;
    /*normal widgets*/
    private Context context=this;
    SharedPrefHelper sharedPrefHelper;
LoginModel loginModel;
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
        loginModel=new LoginModel();
    }

    private void submitButtonClick() {
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if (checkValidation()) {

                loginModel.setUser_name(et_user_name.getText().toString());
                loginModel.setUser_password(et_password.getText().toString());

                if (isInternetOn()) {

                    Gson gson = new Gson();
                    String data = gson.toJson(loginModel);
                    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                    RequestBody body = RequestBody.create(JSON, data);
                    sendLoginData(body);

//                    Intent intent = new Intent(LoginActivity.this, ShowDetail.class);
//                    startActivity(intent);
//                    finish();


                } else {

                    Toast.makeText(LoginActivity.this, "internet on please ", Toast.LENGTH_SHORT).show();
                }
            }
            // }


        });




                //}

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

    private void sendLoginData(RequestBody body) {
        mprogressDialog = ProgressDialog.show(this, "", getString(R.string.Please_wait), true);
        ApiClient.getClient().create(BARC_API.class).callLogin(body).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().toString().trim());
                    String success = jsonObject.optString("success");
                    if (Integer.valueOf(success)==1) {
                        String message = jsonObject.optString("message");
                        String user_id = jsonObject.optString("user_name");
                        String name = jsonObject.optString("name");
                        String user_name = jsonObject.optString("user_name");
                        String user_type_id = jsonObject.optString("user_type_id");



                        sharedPrefHelper.setString("user_type_id", user_type_id);
                        sharedPrefHelper.setString("name", name);
                        sharedPrefHelper.setString("user_id", user_id);
                        sharedPrefHelper.setString("user_name", user_name);

                        Intent intentMainActivity = new Intent(context, UpdateQuestions.class);
                        startActivity(intentMainActivity);

                    }else {
                        Snackbar.make(findViewById(android.R.id.content),"Please Enter Valid User & Password ",Snackbar.LENGTH_LONG).show();

                        mprogressDialog.dismiss();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (mprogressDialog.isShowing()) {
                    mprogressDialog.dismiss();
                }
            }
        });
    }

    private boolean isInternetOn() {

        ConnectivityManager connec = (ConnectivityManager) getSystemService(getBaseContext().CONNECTIVITY_SERVICE);

        assert connec != null;
        if (connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED
                || connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING
                || connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING
                || connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED) {
            return true;

        } else if (connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED
                || connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED) {
            return false;
        }
        return false;
    }

}
