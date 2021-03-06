/*
 * Copyright (c)  2020. Indev Consultancy Private Limited,
 * Auther : Vimal Kumar
 * Date : 2020/12/15
 * Modified Date :
 * Modified By :
 */

package com.vrp.barc_demo.forgot_password;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.vrp.barc_demo.R;
import com.vrp.barc_demo.login.LoginActivity;
import com.vrp.barc_demo.models.LoginModel;
import com.vrp.barc_demo.rest_api.ApiClient;
import com.vrp.barc_demo.rest_api.BARC_API;

import org.json.JSONArray;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPassword extends AppCompatActivity {
    @BindView(R.id.btn_submit)
    Button btn_submit;
    String password;
    @BindView(R.id.et_email)
    EditText et_email;
    ProgressDialog mprogressDialog;
    LoginModel loginModel;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        ButterKnife.bind(this);
        setTitle(R.string.forgot_password);
        loginModel = new LoginModel();
        initialization();
        /*get intent values here*/
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
        }

        setButtonClick();
    }

    private void setButtonClick() {
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginModel.setUser_name(et_email.getText().toString());
                password = et_email.getText().toString().trim();
                if (password.equalsIgnoreCase("")) {
                    if (password.equalsIgnoreCase("")) {
                        et_email.setError("Please enter User Name");
                    }
                    // Snackbar.make(view, "Please enter user name & password", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                } else {

                    if (!isInternetOn()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPassword.this);
                        builder.setMessage("Network Error, check your network connection.")
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setCancelable(false)
                                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.setTitle(getString(R.string.Alert));
                        alert.show();


                    }else if (isInternetOn()){
                        loginModel.setUser_name(et_email.getText().toString());
                        Gson gson = new Gson();
                        String data = gson.toJson(loginModel);
                        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                        RequestBody body = RequestBody.create(JSON, data);

                        mprogressDialog = ProgressDialog.show(context, "", getString(R.string.sending_password), true);
                            ApiClient.getClient().create(BARC_API.class).getForgetPassword(body).enqueue(new Callback<JsonObject>() {
                                @Override
                                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response.body().toString().trim());
                                        String success = jsonObject.optString("success");
                                        if (Integer.valueOf(success) == 1) {
                                            String message = jsonObject.optString("message");
                                            Toast.makeText(context, "New Password sent successfully in Email", Toast.LENGTH_SHORT).show();

                                            Intent intent = new Intent(ForgotPassword.this, LoginActivity.class);
                                            startActivity(intent);
                                            finish();

                                        } else {
                                            Snackbar.make(findViewById(android.R.id.content), "Please Enter Valid User  ", Snackbar.LENGTH_LONG).show();

                                            // Toast.makeText(LoginActivity.this, "Server error", Toast.LENGTH_SHORT).show();
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
                }
            }
        });
    }


    private void initialization() {
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) finish();
        return super.onOptionsItemSelected(item);
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