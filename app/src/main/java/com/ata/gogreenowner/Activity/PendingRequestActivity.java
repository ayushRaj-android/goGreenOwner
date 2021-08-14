package com.ata.gogreenowner.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SearchView;
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

import com.ata.gogreenowner.Adapter.PendingRequestRecyclerAdapter;
import com.ata.gogreenowner.Adapter.PickupBoyPopupRecyclerAdapter;
import com.ata.gogreenowner.Model.PendingRequests;
import com.ata.gogreenowner.R;
import com.ata.gogreenowner.Utility.PendingRequestListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PendingRequestActivity extends AppCompatActivity implements PendingRequestListener {

    private RecyclerView pendingRequestRV;
    private PendingRequestRecyclerAdapter pendingRequestRecyclerAdapter;
    private List<PendingRequests> pendingRequestsList;
    private AppCompatButton assignPickupBoyButton;
    private RelativeLayout selectUnselectLayout;
    private TextView selectTextView, unSelectTextView;
    private Dialog assignPickupDialog;
    private PickupBoyPopupRecyclerAdapter pickupBoyPopupRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_request);

        assignPickupBoyButton = findViewById(R.id.assign_pickup_agent_btn);
        pendingRequestsList = new ArrayList<>();
        pendingRequestRV = findViewById(R.id.pending_request_rv);
        selectUnselectLayout = findViewById(R.id.selectUnselectLayout);
        selectTextView = findViewById(R.id.selectAllText);
        unSelectTextView = findViewById(R.id.unselectAllText);
        pendingRequestRV.hasFixedSize();
        pendingRequestRV.setLayoutManager(new LinearLayoutManager(this
                ,LinearLayoutManager.VERTICAL,false));
        assignPickupDialog = new Dialog(this);

        PendingRequests pendingRequests = new PendingRequests("Gaya",false);
        PendingRequests pendingRequests1 = new PendingRequests("Patna",false);
        PendingRequests pendingRequests2 = new PendingRequests("LKO",false);
        pendingRequestsList.add(pendingRequests);
        pendingRequestsList.add(pendingRequests1);
        pendingRequestsList.add(pendingRequests2);

        pendingRequestRecyclerAdapter = new PendingRequestRecyclerAdapter(this,
                pendingRequestsList,this);
        pendingRequestRV.setAdapter(pendingRequestRecyclerAdapter);

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
}