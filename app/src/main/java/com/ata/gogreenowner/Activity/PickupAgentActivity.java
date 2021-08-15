package com.ata.gogreenowner.Activity;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.ata.gogreenowner.Adapter.ApiClient;
import com.ata.gogreenowner.Adapter.ApiInterface;
import com.ata.gogreenowner.Adapter.PickupBoyRecyclerAdapter;
import com.ata.gogreenowner.R;
import com.ata.gogreenowner.Utility.SharedPreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PickupAgentActivity extends BaseActivity {

    private RecyclerView pickupAgentRecyclerView;
    private PickupBoyRecyclerAdapter pickupBoyRecyclerAdapter;
    private SearchView pickUpBoySearchView;
    List<JSONObject> jsonObjectList = new ArrayList<>();
    private AppCompatImageButton addPickupAgentButton;
    private SharedPreference sharedPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickup_agent);

        pickupAgentRecyclerView = findViewById(R.id.pick_agent_rv);
        pickUpBoySearchView = findViewById(R.id.pickup_search);
        pickupAgentRecyclerView.hasFixedSize();
        pickupAgentRecyclerView.setLayoutManager(new LinearLayoutManager(this
                ,LinearLayoutManager.VERTICAL,false));
        addPickupAgentButton = findViewById(R.id.addPickupAgentButton);
        sharedPreference = new SharedPreference(this);

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

        addPickupAgentButton.setOnClickListener( v->{
            Intent intent = new Intent(this,RegsiterPickupAgentActivity.class);
            startActivity(intent);
        });

        populatePickupAgent();

    }

    private void populatePickupAgent(){
        String pickupBoyJSONString = sharedPreference.getMyPickupBoy();
        if(pickupBoyJSONString != null){
            JSONArray jsonArray = null;
            try {
                jsonArray = new JSONArray(pickupBoyJSONString);
                pickupBoyRecyclerAdapter = new PickupBoyRecyclerAdapter(
                        getApplicationContext(),jsonArray);
                pickupAgentRecyclerView.setAdapter(pickupBoyRecyclerAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            ApiClient apiClient = new ApiClient(getApplicationContext());
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            String jwtToken = "Bearer " + sharedPreference.getJwtToken();
            Call<HashMap<Object,Object>> call = apiService.getMyPickupBoy(jwtToken);
            call.enqueue(new Callback<HashMap<Object, Object>>() {
                @Override
                public void onResponse(Call<HashMap<Object, Object>> call, Response<HashMap<Object, Object>> response) {
                    if(response.isSuccessful() && response.body() != null){
                        HashMap<Object,Object> resultMap = response.body();
                        int statusCode = (int)(double)resultMap.get("statusCode");
                        if(statusCode == 1){
                            String pickupBoyJSONString = resultMap.get("message").toString();
                            JSONArray jsonArray = null;
                            try {
                                jsonArray = new JSONArray(pickupBoyJSONString);
                                pickupBoyRecyclerAdapter = new PickupBoyRecyclerAdapter(
                                        getApplicationContext(),jsonArray);
                                pickupAgentRecyclerView.setAdapter(pickupBoyRecyclerAdapter);
                                sharedPreference.insertMyPickupBoy(pickupBoyJSONString);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Log.d("Ayush",jsonArray.toString());

                        }
                    }else{

                    }
                }

                @Override
                public void onFailure(Call<HashMap<Object, Object>> call, Throwable t) {

                }
            });
        }

    }

}