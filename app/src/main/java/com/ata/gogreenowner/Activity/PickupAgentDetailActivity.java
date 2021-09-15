package com.ata.gogreenowner.Activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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
    private RatingBar pickupBoyRatingBar;
    private TextView totalWeightPicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickup_agent_detail);

        profile_image = findViewById(R.id.profile_image);
        profile_name = findViewById(R.id.profile_name);
        pickUp_mobile = findViewById(R.id.pickUp_mobile);
        total_pickups = findViewById(R.id.total_pickups);
        pickupBoyRatingBar = findViewById(R.id.pickup_boy_rating);
        totalWeightPicked = findViewById(R.id.total_weight_picked);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            try {
                JSONObject jsonObject = new JSONObject(bundle.getString("pickupJson"));
                Log.d("Ayush",jsonObject.toString());
                try{
                    setProfilePic(jsonObject.getString("profilePicUrl"));
                }catch (Exception e){
                    e.printStackTrace();
                }
                try{
                    profile_name.setText(jsonObject.getString("name"));
                }catch (Exception e){
                    e.printStackTrace();
                }
                try{
                    pickUp_mobile.setText(jsonObject.getString("phoneNumber").substring(2));
                }catch (Exception e){
                    e.printStackTrace();
                }
                try{
                    total_pickups.setText(jsonObject.getString("numberOfPickUps"));
                }catch (Exception e){
                    e.printStackTrace();
                }
                try{
                    totalWeightPicked.setText(jsonObject.getString("totalWeight")+" kg");
                }catch (Exception e){
                    e.printStackTrace();
                }
                try{
                    pickupBoyRatingBar.setRating(Float.parseFloat(
                            jsonObject.getString("rating")));
                }catch (Exception e){
                    e.printStackTrace();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void setProfilePic(String url) {
        Glide.with(this).load(url).apply(RequestOptions.circleCropTransform())
                .optionalCircleCrop().into(profile_image);
    }
}