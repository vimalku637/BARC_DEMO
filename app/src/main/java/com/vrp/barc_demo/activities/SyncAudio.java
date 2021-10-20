package com.vrp.barc_demo.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.JsonObject;
import com.vrp.barc_demo.R;
import com.vrp.barc_demo.adapters.AudioAdapter;
import com.vrp.barc_demo.models.SurveyModel;
import com.vrp.barc_demo.models.SyncAudioModel;
import com.vrp.barc_demo.rest_api.ApiClient;
import com.vrp.barc_demo.rest_api.BARC_API;
import com.vrp.barc_demo.sqlite_db.SqliteHelper;
import com.vrp.barc_demo.utils.CommonClass;
import com.vrp.barc_demo.utils.SharedPrefHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SyncAudio extends AppCompatActivity {
    @BindView(R.id.check_box_all)
    MaterialCheckBox check_box_all;
    @BindView(R.id.rvAudioFile)
    RecyclerView rvAudioFile;
    @BindView(R.id.tvNoDataFound)
    MaterialTextView tvNoDataFound;
    @BindView(R.id.fab)
    ExtendedFloatingActionButton fab;

    /*normal widgets*/
    private Context context=this;
    SharedPrefHelper sharedPrefHelper;
    SqliteHelper sqliteHelper;
    private AudioAdapter audioAdapter;
    private ArrayList<SurveyModel> surveyModelAL;

    private HashMap<Integer, String> selectedVlauesHM;
    private ArrayList<SyncAudioModel> syncAudioModelAl;
    ArrayList<String> surveyIds = new ArrayList<>();
    public static String AudioSavePathInDevice="";
    MultipartBody.Part part;

    HashMap<Integer,ArrayList<String>> listHashMap = new    HashMap<Integer,ArrayList<String>>();
    int sendDataCount=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_audio);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        ButterKnife.bind(this);
        setTitle(R.string.synchronize_audio);
        initialization();

        setAudioListAdapter();
        clickAllCheckedButton();
        clickOnSubmitButton();
    }

    private void clickOnSubmitButton() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get list data here
                SyncAudioModel syncAudioModel=new SyncAudioModel();
                listHashMap.clear();
                listHashMap=audioAdapter.getSelectedValues();
                if (listHashMap.size()>0){
                    ArrayList<String> mapValues = new ArrayList<>();
                    int i = 0;
                    // iterating over a map
                    for(Map.Entry<Integer, ArrayList<String>> listEntry : listHashMap.entrySet()){
                        System.out.println("Iterating list number >>> " + ++i);
                        int key = listEntry.getKey();
                        // iterating over a list
                        for(String values : listEntry.getValue()){
                            System.out.println("City >>> " + values);
                            mapValues.add(values);
                        }
                    }
                    for (int j = 0; j < mapValues.size(); j++) {
                        syncAudioModel.setSurvey_data_monitoring_id(mapValues.get(0));
                        syncAudioModel.setSurvey_id(mapValues.get(1));
                        syncAudioModel.setAudio_recording(mapValues.get(2));
                        AudioSavePathInDevice=syncAudioModel.getAudio_recording();

                        if (CommonClass.isInternetOn(context)){
                            if(!AudioSavePathInDevice.equals("")) {
                                Uri imageUri = Uri.parse(AudioSavePathInDevice);
                                File file = new File(imageUri.getPath());
                                RequestBody fileReqBody = RequestBody.create(MediaType.parse("Image/*"), file);
                                part = MultipartBody.Part.createFormData("audio_name", file.getName(), fileReqBody);
                                Log.e("audio_params-", "audio_params- "
                                        + "\n" + sharedPrefHelper.getString("user_id", "")
                                        + "\n" + syncAudioModel.getSurvey_id() + "\n" + syncAudioModel.getId() + "\n" + part);
                                String survey_id=syncAudioModel.getSurvey_id();
                                ProgressDialog mProgressDialog = ProgressDialog.show(context, "", "Please Wait...", true);
                                ApiClient.getClient().create(BARC_API.class).sendAudio(sharedPrefHelper.getString("user_id", ""), syncAudioModel.getSurvey_id(), Integer.parseInt(syncAudioModel.getId()), part).enqueue(new Callback<JsonObject>() {
                                    @Override
                                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(response.body().toString());
                                            Log.e("audio-upload", jsonObject.toString());
                                            sendDataCount=sendDataCount+1;
                                            String success = jsonObject.optString("success");
                                            if (success.equalsIgnoreCase("1")) {
                                                String message = jsonObject.optString("message");
                                                String name = jsonObject.optString("name");
                                                String file_status = jsonObject.optString("file_status");
                                                //update audio in table
                                                sqliteHelper.updateSyncAudio("survey", survey_id, 1);
                                            }
                                            if(sendDataCount==listHashMap.size()){
                                                mProgressDialog.dismiss();
                                                Intent intent = new Intent(SyncAudio.this, MainMenu.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(intent);
                                                finish();
                                            }

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            mProgressDialog.dismiss();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<JsonObject> call, Throwable t) {
                                        mProgressDialog.dismiss();
                                    }
                                });
                            }
                        }else{
                            CommonClass.showPopupForNoInternet(context);
                        }
                    }
                }
            }
        });
    }

    private void setAudioListAdapter() {
        surveyModelAL = sqliteHelper.getAudioList();
        if (surveyModelAL.size()>0){
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
            audioAdapter = new AudioAdapter(context, surveyModelAL);
            rvAudioFile.setLayoutManager(mLayoutManager);
            rvAudioFile.setAdapter(audioAdapter);
        }else{
            tvNoDataFound.setVisibility(View.VISIBLE);
        }
    }

    private void clickAllCheckedButton() {
        check_box_all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (check_box_all.isChecked()) {
                    audioAdapter.selectAll();
                }
                else {
                    audioAdapter.unSelectAll();
                }
            }
        });
    }

    private void initialization() {
        sharedPrefHelper=new SharedPrefHelper(this);
        sqliteHelper=new SqliteHelper(this);
        surveyModelAL=new ArrayList<>();
        selectedVlauesHM=new HashMap<>();
        syncAudioModelAl=new ArrayList<>();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) finish();
        if (item.getItemId()==R.id.home_icon) {
            Intent intentMainMenu=new Intent(context, MainMenu.class);
            intentMainMenu.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intentMainMenu);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem item_logout=menu.findItem(R.id.logout);
        item_logout.setVisible(false);
        return true;
    }
}