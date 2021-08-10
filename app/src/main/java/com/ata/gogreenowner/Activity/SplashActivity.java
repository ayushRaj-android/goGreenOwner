package com.ata.gogreenowner.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;

import com.ata.gogreenowner.R;
import com.ata.gogreenowner.Utility.SharedPreference;

public class SplashActivity extends AppCompatActivity {

    SharedPreference sharedPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        sharedPreference = new SharedPreference(getApplicationContext());
        if(sharedPreference.isUserLoggedIn()){
            //updateJwtToken();
        }else{
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}