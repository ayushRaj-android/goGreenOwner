package com.ata.gogreenowner.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ata.gogreenowner.Adapter.AllRequestRecyclerAdapter;
import com.ata.gogreenowner.Adapter.ApiClient;
import com.ata.gogreenowner.Adapter.ApiInterface;
import com.ata.gogreenowner.Adapter.PendingRequestRecyclerAdapter;
import com.ata.gogreenowner.Adapter.PickupBoyPopupRecyclerAdapter;
import com.ata.gogreenowner.Model.PendingRequests;
import com.ata.gogreenowner.R;
import com.ata.gogreenowner.Utility.PendingRequestListener;
import com.ata.gogreenowner.Utility.SharedPreference;
import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PendingRequestActivity extends BaseActivity implements PendingRequestListener {

    private ConstraintLayout pendingReqMainLayout;
    private RecyclerView pendingRequestRV;
    private PendingRequestRecyclerAdapter pendingRequestRecyclerAdapter;
    private AppCompatButton assignPickupBoyButton;
    private RelativeLayout selectUnselectLayout;
    private TextView selectTextView, unSelectTextView;
    private Dialog assignPickupDialog;
    private PickupBoyPopupRecyclerAdapter pickupBoyPopupRecyclerAdapter;
    private JSONArray pendingRequestArray;
    private List<PendingRequests> pendingRequestsList;
    private Dialog updateDialog;
    SharedPreference sharedPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_request);

        pendingRequestsList = findViewById(R.id.pending_req_main_layout);
        updateDialog = new Dialog(this);
        sharedPreference=new SharedPreference(this);
        pendingRequestsList=new ArrayList<>();

        assignPickupBoyButton = findViewById(R.id.assign_pickup_agent_btn);

        pendingRequestRV = findViewById(R.id.pending_request_rv);
        selectUnselectLayout = findViewById(R.id.selectUnselectLayout);
        selectTextView = findViewById(R.id.selectAllText);
        unSelectTextView = findViewById(R.id.unselectAllText);
        pendingRequestRV.hasFixedSize();
        pendingRequestRV.setLayoutManager(new LinearLayoutManager(this
                ,LinearLayoutManager.VERTICAL,false));
        assignPickupDialog = new Dialog(this);

        getPendingRequestList();

        assignPickupBoyButton.setOnClickListener( v->{
            List<PendingRequests> selectedPendingReq = pendingRequestRecyclerAdapter.getSelectedPendingRequest();
            for(PendingRequests pr : selectedPendingReq){
//                Log.d("Ayush",pr.getAddress());
            }
            showAssignPopUp();
        });

        selectTextView.setOnClickListener( v->{
            pendingRequestRecyclerAdapter.selectAllRequest();
        });

        unSelectTextView.setOnClickListener( v->{
            pendingRequestRecyclerAdapter.unselectAllRequest();
        });
    }

    @Override
    public void onAssignPickupBoyAction(Boolean isSelected) {
        if(isSelected){
            selectUnselectLayout.setVisibility(View.VISIBLE);
            assignPickupBoyButton.setVisibility(View.VISIBLE);
        }else{
            selectUnselectLayout.setVisibility(View.GONE);
            assignPickupBoyButton.setVisibility(View.GONE);
        }
    }

    private void showAssignPopUp(){
        TextView cross;
        SearchView popupSearchView;
        RecyclerView popupRecyclerView;
        assignPickupDialog.setContentView(R.layout.pickup_agent_popup);
        assignPickupDialog.setCanceledOnTouchOutside(false);
        cross = assignPickupDialog.findViewById(R.id.cross_pop);
        popupSearchView = assignPickupDialog.findViewById(R.id.pickup_search_popup);
        popupRecyclerView = assignPickupDialog.findViewById(R.id.pickup_rv_popup);
        popupRecyclerView.hasFixedSize();
        popupRecyclerView.setLayoutManager(new LinearLayoutManager(this
                ,LinearLayoutManager.VERTICAL,false));
        List<JSONObject> jsonObjectList = new ArrayList<>();
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonObject1 = new JSONObject();
        try {
            jsonObject.put("name", "Ayush Raj");
            jsonObject.put("phone", "6201124947");
            jsonObject.put("profilePicUrl","https://lh3.googleusercontent.com/ogw/ADea4I4L3sCPJteJTIDnbQqV7jH6JH6tBaPGlFrLfDvOuw=s192-c-mo");

            jsonObject1.put("name", "Ayushi Poddar");
            jsonObject1.put("phone", "8084805576");
            jsonObject1.put("profilePicUrl","http://lh6.ggpht.com/_9F9_RUESS2E/S-AtYGxVAdI/AAAAAAAACvE/mpfyMoyqDSw/s800/sarolta-ban-surreal-16.jpg");
            jsonObjectList.add(jsonObject);
            jsonObjectList.add(jsonObject1);
        }catch (Exception e){
            e.printStackTrace();
        }
        pickupBoyPopupRecyclerAdapter = new PickupBoyPopupRecyclerAdapter(getApplicationContext(),
                jsonObjectList,assignPickupDialog);
        popupRecyclerView.setAdapter(pickupBoyPopupRecyclerAdapter);
        popupSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (jsonObjectList.size() != 0) {
                    pickupBoyPopupRecyclerAdapter.filter(query.toLowerCase());
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        ImageView clearButton = popupSearchView.findViewById(androidx.appcompat.R.id.search_close_btn);
        clearButton.setOnClickListener(v -> {
            if(popupSearchView.getQuery().length() == 0) {
                Log.d("Ayush","CLicked!");
                popupSearchView.setQuery("", true);
                popupSearchView.setIconified(true);
            } else {
                Log.d("Ayush","CLicked123!");
                popupSearchView.setQuery("", false);
                pickupBoyPopupRecyclerAdapter.filter(null);
            }
        });
        cross.setOnClickListener(v1 -> assignPickupDialog.dismiss());
        assignPickupDialog.show();
    }

    private void getPendingRequestList(){
        showDialog("Getting Pending Requests!");
        ApiClient apiClient = new ApiClient(getApplicationContext());
        ApiInterface apiService = apiClient.getClient().create(ApiInterface.class);

        Call<HashMap<Object, Object>> call=apiService.getPendingRequestList("Bearer "+sharedPreference.getJwtToken(),0);

        call.enqueue(new Callback<HashMap<Object, Object>>() {
            @Override
            public void onResponse(Call<HashMap<Object, Object>> call,
                                   Response<HashMap<Object, Object>> response) {
                if(response.isSuccessful() && response.body()!=null){
                    HashMap<Object, Object> resultMap = response.body();
                    int statusCode = (int) (double) resultMap.get("statusCode");
                    if (statusCode == -1) {
                        updateDialog.dismiss();
                    }
                    else if(statusCode==2){
                        try {
                            Object customerObj=resultMap.get("data");
                            pendingRequestArray=new JSONArray(customerObj.toString());
                            for(int i=0;i<pendingRequestArray.length();i++){
                                JSONObject pendingRequestObj=pendingRequestArray.getJSONObject(i);
                                PendingRequests pendingRequests=createPendingRequest(pendingRequestObj);
                                pendingRequestsList.add(pendingRequests);
                            }
                            pendingRequestRecyclerAdapter = new PendingRequestRecyclerAdapter(getApplicationContext(),
                                    pendingRequestsList,PendingRequestActivity.this::onAssignPickupBoyAction);
                            pendingRequestRV.setAdapter(pendingRequestRecyclerAdapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    else{
                        updateDialog.dismiss();
                        showSnackbarAPI();
                    }
                }
                else {
                    updateDialog.dismiss();
                    showSnackbarAPI();
                }
            }
            @Override
            public void onFailure(Call<HashMap<Object, Object>> call, Throwable t) {
                updateDialog.dismiss();
                showSnackbarAPI();
            }
        });
    }

    private PendingRequests createPendingRequest(JSONObject jsonObject){
        try {
            JSONObject addressObj = new JSONObject(jsonObject.get("req_address").toString());
            JSONObject custObj=new JSONObject(jsonObject.get("reqBy").toString());
            Timestamp requestDate = Timestamp.valueOf(jsonObject.get("requestDate").toString());
            Date date = new Date(requestDate.getTime());
            String requestDateString = new SimpleDateFormat("EE, dd-MMM-yyyy").format(date);
            Timestamp requestedOnDate = Timestamp.valueOf(jsonObject.get("requestPlacementTimestamp").toString());
            Date date1 = new Date(requestedOnDate.getTime());
            String requestedOnString = new SimpleDateFormat("EE, dd-MMM-yyyy").format(date1);
            PendingRequests pendingRequests = new PendingRequests(addressObj.getString("street"),false);
            pendingRequests.setRequestId(jsonObject.getString("requestId"));
            pendingRequests.setRequestDate(requestDateString);
            pendingRequests.setRequestedOn(requestedOnString);
            pendingRequests.setRequestTime(jsonObject.getString("timeSlot"));
            if(custObj.has("profilePicUrl"))
            pendingRequests.setProfilePicUrl(custObj.getString("profilePicUrl"));
            return pendingRequests;
        }catch (Exception e){
            Log.d("Ayush","Error while creating pending request obj from json");
        }
        return new PendingRequests("",false);
    }

    private void showDialog(String text) {
        ImageView dialog_image;
        TextView dialog_text;
        updateDialog.setContentView(R.layout.loading_popup);
        updateDialog.setCanceledOnTouchOutside(false);
        updateDialog.setCancelable(false);
        dialog_image = updateDialog.findViewById(R.id.loading_image);
        dialog_text = updateDialog.findViewById(R.id.loading_text);
        Glide.with(this).load(R.drawable.ic_dash_pending_request).into(dialog_image);
        dialog_text.setText(text);
        updateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        updateDialog.show();
    }

    private void showSnackbarAPI(){
        snackbar = Snackbar.make(pendingReqMainLayout,"Something went wrong!",
                Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("RETRY", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
                getPendingRequestList();
            }
        });
        snackbar.show();
        snackbar.setActionTextColor(Color.RED);

    }
}