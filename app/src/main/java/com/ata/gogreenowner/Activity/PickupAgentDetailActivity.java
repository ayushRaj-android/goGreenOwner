package com.ata.gogreenowner.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.ata.gogreenowner.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONException;
import org.json.JSONObject;


public class PickupAgentDetailActivity extends AppCompatActivity {

    private ImageView profile_image;
    private TextView profile_name;
    private TextView pickUp_mobile;
    private TextView total_pickups;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickup_agent_detail);

        profile_image=findViewById(R.id.profile_image);
        profile_name=findViewById(R.id.profile_name);
        pickUp_mobile=findViewById(R.id.pickUp_mobile);
        total_pickups=findViewById(R.id.total_pickups);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            try {
                JSONObject jsonObject=new JSONObject(bundle.getString("pickupJson"));
                Log.d("Ayush",jsonObject.toString());
                setProfilePic(jsonObject.getString("profilePic"));
                profile_name.setText(jsonObject.getString("name"));
                pickUp_mobile.setText(jsonObject.getString("phone").substring(2));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void setProfilePic(String url){
        Log.d("Ayush",url);
        Glide.with(this).load(url).apply(RequestOptions.circleCropTransform())
                .optionalCircleCrop().into(profile_image);
    }
}