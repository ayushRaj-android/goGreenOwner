package com.ata.gogreenowner.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;

import com.ata.gogreenowner.R;
import com.ata.gogreenowner.Utility.SharedPreference;

public class DashboardActivity extends BaseActivity {

    CardView pickupAgentCard;
    CardView pendingRequestCard;
    CardView requestCardView;
    CardView analyticsCardView;
    TextView logoutTV;
    AlertDialog.Builder alertDialogBuilder;
    SharedPreference sharedPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        sharedPreference = new SharedPreference(this);
        alertDialogBuilder = new AlertDialog.Builder(this);

        pickupAgentCard = findViewById(R.id.pick_agent_card);
        pickupAgentCard.setOnClickListener(v -> {
            Intent intent = new Intent(this, PickupAgentActivity.class);
            startActivity(intent);
        });

        pendingRequestCard = findViewById(R.id.pending_req_card);
        pendingRequestCard.setOnClickListener(v -> {
            Intent intent = new Intent(this, PendingRequestActivity.class);
            startActivity(intent);
        });

        requestCardView = findViewById(R.id.request_card);
        requestCardView.setOnClickListener(v -> {
            Intent intent = new Intent(this, RequestsActivity.class);
            startActivity(intent);
        });

        analyticsCardView = findViewById(R.id.analytics_card);
        analyticsCardView.setOnClickListener(v -> {
            Toast.makeText(this, "Will be available in next version!", Toast.LENGTH_LONG)
                    .show();
        });

        logoutTV = findViewById(R.id.logout_tv);
        logoutTV.setOnClickListener( v->{
            alertDialogBuilder.setTitle("Confirm Logout!");
            alertDialogBuilder.setIcon(R.drawable.product_logo);
            alertDialogBuilder.setMessage("Are you sure, you want to exit?");
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    sharedPreference.logoutUser();
                    Intent i = new Intent(getApplicationContext(), SplashActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    finish();
                }
            });

            alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        });
    }
}