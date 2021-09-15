package com.ata.gogreenowner.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.ata.gogreenowner.Adapter.ApiClient;
import com.ata.gogreenowner.Adapter.ApiInterface;
import com.ata.gogreenowner.R;
import com.ata.gogreenowner.Utility.SharedPreference;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;

public class SplashActivity extends AppCompatActivity {

    SharedPreference sharedPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        sharedPreference = new SharedPreference(getApplicationContext());
        if (sharedPreference.isUserLoggedIn()) {
            updateJwtToken();
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void updateJwtToken() {
        try {
            String phoneNumber = sharedPreference.getUserPhone();
            String refreshToken = "Bearer " + sharedPreference.getRefreshToken();
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<HashMap<Object, Object>> call = apiService.generateNewJwtToken(phoneNumber, refreshToken);
            call.enqueue(new Callback<HashMap<Object, Object>>() {
                @Override
                public void onResponse(Call<HashMap<Object, Object>> call, retrofit2.Response<HashMap<Object, Object>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        HashMap<Object, Object> resultMap = response.body();
                        int statusCode = (int) (double) resultMap.get("statusCode");
                        if (statusCode == 1) {
                            String token = resultMap.get("message").toString();
                            sharedPreference.updateJwt(token);
                        }
                        Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }

                @Override
                public void onFailure(Call<HashMap<Object, Object>> call, Throwable t) {
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    intent.putExtra("sessionExpired", true);
                    startActivity(intent);
                    finish();
                }
            });
        } catch (Exception e) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.putExtra("sessionExpired", true);
            startActivity(intent);
            finish();
        }
    }
}