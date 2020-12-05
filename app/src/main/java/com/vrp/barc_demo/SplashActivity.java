package com.vrp.barc_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.vrp.barc_demo.sqlite_db.SqliteHelper;

public class SplashActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    /*normal widgets*/
    private Context context=this;
    private final int SPLASH_DISPLAY_LENGTH = 3000;
    private boolean isProgressBar=false;
    private SqliteHelper sqliteHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        isProgressBar=true;

        //call database open database method.
        sqliteHelper = new SqliteHelper(this);
        sqliteHelper.openDataBase();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isProgressBar) {
                    Intent intentMainActivity=new Intent(context, LoginActivity.class);
                    startActivity(intentMainActivity);
                    finish();
                }
            }
        }, SPLASH_DISPLAY_LENGTH);

    }
}
