package com.vrp.barc_demo.forgot_password;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.vrp.barc_demo.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ForgotPassword extends AppCompatActivity {
    @BindView(R.id.btn_submit)
    Button btn_submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        ButterKnife.bind(this);
        setTitle(R.string.forgot_password);
        initialization();
        /*get intent values here*/
        Bundle bundle=getIntent().getExtras();
        if (bundle!=null) {
        }

        setButtonClick();
    }

    private void setButtonClick() {
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
}