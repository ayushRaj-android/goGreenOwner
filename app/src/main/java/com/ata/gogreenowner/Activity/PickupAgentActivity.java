package com.ata.gogreenowner.Activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ata.gogreenowner.Adapter.ApiClient;
import com.ata.gogreenowner.Adapter.ApiInterface;
import com.ata.gogreenowner.Adapter.PickupBoyRecyclerAdapter;
import com.ata.gogreenowner.R;
import com.ata.gogreenowner.Utility.SharedPreference;
import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PickupAgentActivity extends BaseActivity {

    private RecyclerView pickupAgentRecyclerView;
    private PickupBoyRecyclerAdapter pickupBoyRecyclerAdapter;
    private SearchView pickUpBoySearchView;
    private AppCompatImageButton addPickupAgentButton;
    private SharedPreference sharedPreference;
    private Context context;
    private RelativeLayout noPickupAgentLayout;
    private Dialog updateDialog;
    private LinearLayout mainPickupAgentLayout;
    private Snackbar snackbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickup_agent);

        context = this;
        mainPickupAgentLayout = findViewById(R.id.mainPickupAgentLayout);
        pickupAgentRecyclerView = findViewById(R.id.pick_agent_rv);
        pickUpBoySearchView = findViewById(R.id.pickup_search);
        pickupAgentRecyclerView.hasFixedSize();
        pickupAgentRecyclerView.setLayoutManager(new LinearLayoutManager(this
                , LinearLayoutManager.VERTICAL, false));
        addPickupAgentButton = findViewById(R.id.addPickupAgentButton);
        sharedPreference = new SharedPreference(this);
        noPickupAgentLayout = findViewById(R.id.noPickupAgentLayout);
        updateDialog = new Dialog(this);
        pickUpBoySearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                pickupBoyRecyclerAdapter.filter(query.toLowerCase());
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        ImageView clearButton = pickUpBoySearchView.findViewById(androidx.appcompat.R.id.search_close_btn);
        clearButton.setOnClickListener(v -> {
            if (pickUpBoySearchView.getQuery().length() == 0) {
                pickUpBoySearchView.setQuery("", false);
                pickUpBoySearchView.setIconified(true);
            } else {
                pickUpBoySearchView.setQuery("", false);
                pickupBoyRecyclerAdapter.filter(null);
            }
        });

        addPickupAgentButton.setOnClickListener(v -> {
            if (snackbar != null && snackbar.isShown()) {
                snackbar.dismiss();
            }
            Intent intent = new Intent(this, RegsiterPickupAgentActivity.class);
            startActivity(intent);
            finish();
        });

        populatePickupAgent();

    }

    private void populatePickupAgent() {
        showDialog("Loading your pickup Agents!");
        String pickupBoyJSONString = sharedPreference.getMyPickupBoy();
        if (pickupBoyJSONString != null) {
            JSONArray jsonArray = null;
            try {
                jsonArray = new JSONArray(pickupBoyJSONString);
                pickupBoyRecyclerAdapter = new PickupBoyRecyclerAdapter(
                        context, jsonArray);
                pickupAgentRecyclerView.setAdapter(pickupBoyRecyclerAdapter);
                updateDialog.dismiss();
            } catch (JSONException e) {
                showSnackbarAPI();
                updateDialog.dismiss();
            }
        } else {
            ApiClient apiClient = new ApiClient(getApplicationContext());
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            String jwtToken = "Bearer " + sharedPreference.getJwtToken();
            Call<HashMap<Object, Object>> call = apiService.getMyPickupBoy(jwtToken);
            call.enqueue(new Callback<HashMap<Object, Object>>() {
                @Override
                public void onResponse(Call<HashMap<Object, Object>> call, Response<HashMap<Object, Object>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        HashMap<Object, Object> resultMap = response.body();
                        int statusCode = (int) (double) resultMap.get("statusCode");
                        if (statusCode == 1) {
                            String pickupBoyJSONString = resultMap.get("message").toString();
                            JSONArray jsonArray = null;
                            try {
                                jsonArray = new JSONArray(pickupBoyJSONString);
                                if (jsonArray.length() != 0) {
                                    pickupBoyRecyclerAdapter = new PickupBoyRecyclerAdapter(
                                            context, jsonArray);
                                    pickupAgentRecyclerView.setAdapter(pickupBoyRecyclerAdapter);
                                    sharedPreference.insertMyPickupBoy(pickupBoyJSONString);
                                    updateDialog.dismiss();
                                } else {
                                    pickupAgentRecyclerView.setVisibility(View.GONE);
                                    noPickupAgentLayout.setVisibility(View.VISIBLE);
                                    updateDialog.dismiss();
                                }
                            } catch (JSONException e) {
                                showSnackbarAPI();
                                updateDialog.dismiss();
                            }
                        }
                    } else {
                        showSnackbarAPI();
                        updateDialog.dismiss();
                    }
                }

                @Override
                public void onFailure(Call<HashMap<Object, Object>> call, Throwable t) {
                    showSnackbarAPI();
                    updateDialog.dismiss();
                }
            });
        }

    }

    private void showDialog(String text) {
        ImageView dialog_image;
        TextView dialog_text;
        updateDialog.setContentView(R.layout.loading_popup);
        updateDialog.setCanceledOnTouchOutside(false);
        updateDialog.setCancelable(false);
        dialog_image = updateDialog.findViewById(R.id.loading_image);
        dialog_text = updateDialog.findViewById(R.id.loading_text);
        Glide.with(this).load(R.drawable.ic_dash_pick_agent).into(dialog_image);
        dialog_text.setText(text);
        updateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        updateDialog.show();
    }

    private void showSnackbarAPI() {
        snackbar = Snackbar.make(mainPickupAgentLayout, "Something went wrong!",
                Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("RETRY", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
                populatePickupAgent();
            }
        });
        snackbar.show();
        snackbar.setActionTextColor(Color.RED);

    }

}