package com.ata.gogreenowner.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;

import com.ata.gogreenowner.R;

public class DashboardActivity extends BaseActivity {

    CardView pickupAgentCard;
    CardView pendingRequestCard;
    CardView requestCardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        pickupAgentCard = findViewById(R.id.pick_agent_card);
        pickupAgentCard.setOnClickListener( v ->{
            Intent intent = new Intent(this,PickupAgentActivity.class);
            startActivity(intent);
        });

        pendingRequestCard = findViewById(R.id.pending_req_card);
        pendingRequestCard.setOnClickListener( v->{
            Intent intent = new Intent(this,PendingRequestActivity.class);
            startActivity(intent);
        });

        requestCardView = findViewById(R.id.request_card);
        requestCardView.setOnClickListener( v->{
            Intent intent = new Intent(this,RequestsActivity.class);
            startActivity(intent);
        });
    }
}