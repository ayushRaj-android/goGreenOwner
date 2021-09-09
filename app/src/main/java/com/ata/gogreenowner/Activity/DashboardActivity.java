package com.ata.gogreenowner.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import com.ata.gogreenowner.R;

public class DashboardActivity extends BaseActivity {

    CardView pickupAgentCard;
    CardView pendingRequestCard;
    CardView requestCardView;
    CardView analyticsCardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

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
    }
}