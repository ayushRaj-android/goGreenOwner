package com.ata.gogreenowner.Activity;

import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;

import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.ata.gogreenowner.R;
import com.ata.gogreenowner.Receivers.NetworkChangeReceiver;
import com.google.android.material.snackbar.Snackbar;

public class BaseActivity extends AppCompatActivity {

    public static Snackbar snackbar;
    RelativeLayout baseLayout;
    IntentFilter intentFilter;
    NetworkChangeReceiver receiver;

    public static void showSnackbar() {
        snackbar.show();
    }

    public static void dismissSnackbar() {
        snackbar.dismiss();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        baseLayout = findViewById(R.id.baseLayout);
        intentFilter = new IntentFilter();
        intentFilter.addAction(CONNECTIVITY_ACTION);
        receiver = new NetworkChangeReceiver();
        snackbar = Snackbar.make(baseLayout, "No Internet Connection", Snackbar.LENGTH_INDEFINITE);
        View sbView = snackbar.getView();
        TextView textView = sbView.findViewById(com.google.android.material.R.id.snackbar_text);
        sbView.setBackgroundResource(R.color.colorPrimaryDark);
        textView.setTextColor(getResources().getColor(R.color.red));
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }
}