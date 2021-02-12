package com.vrp.barc_demo.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.vrp.barc_demo.R;
import com.vrp.barc_demo.rest_api.ApiClient;
import com.vrp.barc_demo.utils.SharedPrefHelper;

public class UpdateAppActivity extends AppCompatActivity {
    Button btn_update_app;
    SharedPrefHelper sharedPrefHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_app);
        setTitle("Update Application");
        btn_update_app=findViewById(R.id.btn_update_app);
        sharedPrefHelper=new SharedPrefHelper(this);
        btn_update_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    String url = "https://barc.indevconsultancy.com/apk/bi.apk";
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
            }
        });
    }
}
