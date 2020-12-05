package com.vrp.barc_demo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.vrp.barc_demo.rest_api.ApiClient;
import com.vrp.barc_demo.rest_api.BARC_API;
import com.vrp.barc_demo.utils.CommonClass;
import com.vrp.barc_demo.utils.MyJSON;
import com.vrp.barc_demo.utils.SharedPrefHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DownloadSurvey extends AppCompatActivity {
    private static final String TAG = "Download_Survey";
    @BindView(R.id.cl_layout)
    ConstraintLayout cl_layout;

    /*normal widgets*/
    private Context context=this;
    private SharedPrefHelper sharedPrefHelper;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_download);
        ButterKnife.bind(this);
        setTitle(R.string.download_survey);
        initialization();
        /*get intent values here*/
        Bundle bundle=getIntent().getExtras();
        if (bundle!=null) {
        }

        setButtonClick();
    }

    private void initialization() {
        sharedPrefHelper=new SharedPrefHelper(this);
    }

    private void setButtonClick() {
        cl_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CommonClass.isInternetOn(context)) {
                    callDownloadSurveyApi();
                } else {
                    CommonClass.showPopupForNoInternet(context);
                }
                Intent intentMainActivity=new Intent(context, HomeActivity.class);
                startActivity(intentMainActivity);
                finish();
            }
        });
    }

    private void callDownloadSurveyApi() {
        mProgressDialog=ProgressDialog.show(context, "", "Please wait...", true);
        ApiClient.getClient().create(BARC_API.class).getBarcDemoJson().enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    JsonObject jsonObject = response.body();
                    mProgressDialog.dismiss();
                    Log.e(TAG, "onResponse: "+jsonObject.toString());
                    String surveyJSON=jsonObject.toString();
                    //to save all JSON into json file
                    MyJSON.saveJSONToAsset(context, surveyJSON);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(context, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}
