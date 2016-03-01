package com.development.techiefolks.golgappe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class SplashActivity extends AppCompatActivity {
    private final int SPLASH_DISPLAY_LENGTH = 2000;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_splash);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences = getSharedPreferences("sessionData", MODE_PRIVATE);
                String userid = sharedPreferences.getString("userId", "");
                String password = sharedPreferences.getString("password", "");
                if (userid == "" && password == "") {
                    Intent mainIntent = new Intent(SplashActivity.this, StartingActivity.class);
                    SplashActivity.this.startActivity(mainIntent);
                } else {
                    Intent mainIntent = new Intent(SplashActivity.this, HomePageActivity.class);
                    SplashActivity.this.startActivity(mainIntent);
                }
                finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
