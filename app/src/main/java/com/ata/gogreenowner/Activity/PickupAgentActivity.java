package com.ata.gogreenowner.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.ata.gogreenowner.Adapter.PickupBoyRecyclerAdapter;
import com.ata.gogreenowner.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class PickupAgentActivity extends AppCompatActivity {

    private RecyclerView pickupAgentRecyclerView;
    private PickupBoyRecyclerAdapter pickupBoyRecyclerAdapter;
    private SearchView pickUpBoySearchView;
    List<JSONObject> jsonObjectList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickup_agent);

        pickupAgentRecyclerView = findViewById(R.id.pick_agent_rv);
        pickUpBoySearchView = findViewById(R.id.pickup_search);
        pickupAgentRecyclerView.hasFixedSize();
        pickupAgentRecyclerView.setLayoutManager(new LinearLayoutManager(this
                ,LinearLayoutManager.VERTICAL,false));

        JSONObject jsonObject = new JSONObject();
        JSONObject jsonObject1 = new JSONObject();
        try {
            jsonObject.put("name", "Ayush Raj");
            jsonObject.put("phone", "6201124947");
            jsonObject.put("profilePicUrl","https://www.vhv.rs/dpng/d/63-630699_kite-png-under-100kb-transparent-png.png");

            jsonObject1.put("name", "Ayushi Poddar");
            jsonObject1.put("phone", "8084805576");
            jsonObject1.put("profilePicUrl","http://lh6.ggpht.com/_9F9_RUESS2E/S-AtYGxVAdI/AAAAAAAACvE/mpfyMoyqDSw/s800/sarolta-ban-surreal-16.jpg");
            jsonObjectList.add(jsonObject);
            jsonObjectList.add(jsonObject1);
        }catch (Exception e){
            e.printStackTrace();
        }
        pickupBoyRecyclerAdapter = new PickupBoyRecyclerAdapter(this,jsonObjectList);
        pickupAgentRecyclerView.setAdapter(pickupBoyRecyclerAdapter);

        pickUpBoySearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (jsonObjectList.size() != 0) {
                    pickupBoyRecyclerAdapter.filter(query.toLowerCase());
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        ImageView clearButton = pickUpBoySearchView.findViewById(androidx.appcompat.R.id.search_close_btn);
        clearButton.setOnClickListener(v -> {
            if(pickUpBoySearchView.getQuery().length() == 0) {
                Log.d("Ayush","CLicked!");
                pickUpBoySearchView.setQuery("", false);
                pickUpBoySearchView.setIconified(true);
            } else {
                Log.d("Ayush","CLicked123!");
                pickUpBoySearchView.setQuery("", false);
                pickupBoyRecyclerAdapter.filter(null);
            }
        });

    }

}