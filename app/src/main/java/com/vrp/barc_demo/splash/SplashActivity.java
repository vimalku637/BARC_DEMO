/*
 * Copyright (c)  2020. Indev Consultancy Private Limited,
 * Auther : Vimal Kumar
 * Date : 2020/12/15
 * Modified Date :
 * Modified By :
 */

package com.vrp.barc_demo.splash;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.vrp.barc_demo.R;
import com.vrp.barc_demo.login.LoginActivity;
import com.vrp.barc_demo.sqlite_db.SqliteHelper;
import com.vrp.barc_demo.utils.SharedPrefHelper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SplashActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    /*normal widgets*/
    private Context context=this;
    private final int SPLASH_DISPLAY_LENGTH = 3000;
    private boolean isProgressBar=false;
    private SqliteHelper sqliteHelper;
    private SharedPrefHelper sharedPrefHelper;
    private static final Pattern p = Pattern.compile("[^\\d]*[\\d]+[^\\d]+([\\d]+)");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        isProgressBar=true;

        //call database open database method.
        sqliteHelper = new SqliteHelper(this);
        sharedPrefHelper=new SharedPrefHelper(this);

        sqliteHelper.openDataBase();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isProgressBar) {

                    String s= "Survey 1609852528Rejected";
                    Matcher m = p.matcher(s);
                    if (m.find()) {
                        Log.e("ss",m.group(1)); // second matched digits
                    }
                    Intent intentMainActivity=new Intent(context, LoginActivity.class);
                    startActivity(intentMainActivity);
                    finish();
                }
            }
        }, SPLASH_DISPLAY_LENGTH);

    }
}
