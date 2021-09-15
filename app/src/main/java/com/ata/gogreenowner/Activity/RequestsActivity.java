package com.ata.gogreenowner.Activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ata.gogreenowner.Adapter.AllRequestRecyclerAdapter;
import com.ata.gogreenowner.Adapter.ApiClient;
import com.ata.gogreenowner.Adapter.ApiInterface;
import com.ata.gogreenowner.R;
import com.ata.gogreenowner.Utility.SharedPreference;
import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestsActivity extends BaseActivity {

    private final String[] searchType = {"Pickup Agent", "Pincode", "Street Address"};
    JSONArray customerArray;
    SharedPreference sharedPreference;
    String radioGrpText = "All";
    private RadioGroup reqTypeRadioGrp;
    private AppCompatSpinner searchTypeSpinner;
    private SearchView requestSearchView;
    private String selectedSearchType = "Pickup Agent";
    private RecyclerView requestsRecyclerView;
    private AllRequestRecyclerAdapter allRequestRecyclerAdapter;
    private Dialog updateDialog;
    private JSONArray allRequestList;
    private JSONArray todayRequestList;
    private JSONArray upcomingRequestList;
    private JSONArray completedRequestList;
    private JSONArray searchRequestList;
    private Snackbar snackbar;
    private Context context;


    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        updateDialog = new Dialog(this);
        sharedPreference = new SharedPreference(this);
        allRequestList = new JSONArray();
        todayRequestList = new JSONArray();
        upcomingRequestList = new JSONArray();
        completedRequestList = new JSONArray();
        searchRequestList = new JSONArray();
        context = this;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);

        reqTypeRadioGrp = findViewById(R.id.req_type_radio_grp);
        searchTypeSpinner = findViewById(R.id.search_by_spinner);
        requestSearchView = findViewById(R.id.search_view_req);
        requestsRecyclerView = findViewById(R.id.requests_rv);
        requestsRecyclerView.hasFixedSize();
        requestsRecyclerView.setLayoutManager(new LinearLayoutManager(this
                , LinearLayoutManager.VERTICAL, false));
        reqTypeRadioGrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (null != rb && checkedId > -1) {
                    radioGrpText = rb.getText().toString();
                    if (rb.getText().toString().equalsIgnoreCase("Today")) {
                        allRequestRecyclerAdapter.requestListToShow = todayRequestList;
                        allRequestRecyclerAdapter.notifyDataSetChanged();
                    } else if (rb.getText().toString().equalsIgnoreCase("Upcoming")) {
                        allRequestRecyclerAdapter.requestListToShow = upcomingRequestList;
                        allRequestRecyclerAdapter.notifyDataSetChanged();
                    } else if (rb.getText().toString().equalsIgnoreCase("Completed")) {
                        allRequestRecyclerAdapter.requestListToShow = completedRequestList;
                        allRequestRecyclerAdapter.notifyDataSetChanged();
                    } else {
                        allRequestRecyclerAdapter.requestListToShow = allRequestList;
                        allRequestRecyclerAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

        searchTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (requestSearchView.getQuery().length() == 0) {
                    requestSearchView.setQuery("", false);
                    requestSearchView.setIconified(true);
                } else {
                    requestSearchView.setQuery("", false);
                    //pickupBoyRecyclerAdapter.filter(null);
                }
                //hideKeyboard();
                String selectedItemText = (String) parent.getItemAtPosition(position);
                selectedSearchType = selectedItemText;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        requestSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (radioGrpText.equalsIgnoreCase("all")) {
                    getSearchRequest(query, allRequestList);
                } else if (radioGrpText.equalsIgnoreCase("Today")) {
                    getSearchRequest(query, todayRequestList);
                } else if (radioGrpText.equalsIgnoreCase("Upcoming")) {
                    getSearchRequest(query, upcomingRequestList);
                } else {
                    getSearchRequest(query, completedRequestList);
                }
                if (searchRequestList.length() == 0) {
                    Toast.makeText(RequestsActivity.this, "Searched Item Not Found", Toast.LENGTH_SHORT).show();
                } else {
                    allRequestRecyclerAdapter.requestListToShow = searchRequestList;
                    allRequestRecyclerAdapter.notifyDataSetChanged();

                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        setSearchTypeSpinner();
        searchTypeSpinner.setSelection(0);
        getRequestList();

        ImageView clearButton = requestSearchView.findViewById(androidx.appcompat.R.id.search_close_btn);
        clearButton.setOnClickListener(v -> {
            if (requestSearchView.getQuery().length() == 0) {
                requestSearchView.setQuery("", false);
                requestSearchView.setIconified(true);
            } else {
                requestSearchView.setQuery("", false);
                if (radioGrpText.equalsIgnoreCase("all")) {
                    allRequestRecyclerAdapter.requestListToShow = allRequestList;
                } else if (radioGrpText.equalsIgnoreCase("Today")) {
                    allRequestRecyclerAdapter.requestListToShow = todayRequestList;
                    allRequestRecyclerAdapter.notifyDataSetChanged();
                } else if (radioGrpText.equalsIgnoreCase("Upcoming")) {
                    allRequestRecyclerAdapter.requestListToShow = upcomingRequestList;
                    allRequestRecyclerAdapter.notifyDataSetChanged();
                } else {
                    allRequestRecyclerAdapter.requestListToShow = completedRequestList;
                    allRequestRecyclerAdapter.notifyDataSetChanged();
                }
            }
        });

    }

    private void getRequestList() {
        showDialog("Getting your requests");
        ApiClient apiClient = new ApiClient(getApplicationContext());
        ApiInterface apiService = apiClient.getClient().create(ApiInterface.class);

        Call<HashMap<Object, Object>> call = apiService.getRequestList("Bearer " + sharedPreference.getJwtToken());

        call.enqueue(new Callback<HashMap<Object, Object>>() {
            @Override
            public void onResponse(Call<HashMap<Object, Object>> call,
                                   Response<HashMap<Object, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    HashMap<Object, Object> resultMap = response.body();
                    int statusCode = (int) (double) resultMap.get("statusCode");
                    if (statusCode == -1) {
                        updateDialog.dismiss();
                        showSnackbarAPI();
                    } else if (statusCode == 2) {
                        try {
                            Object customerObj = resultMap.get("data");
                            customerArray = new JSONArray(customerObj.toString());
                            allRequestList = customerArray;
                            getTodayRequests();
                            getUpcomingRequests();
                            getCompletedRequests();
                            allRequestRecyclerAdapter = new AllRequestRecyclerAdapter(getApplicationContext(), allRequestList);
                            requestsRecyclerView.setAdapter(allRequestRecyclerAdapter);
                            updateDialog.dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {
                        updateDialog.dismiss();
                        showSnackbarAPI();
                    }
                } else {
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

    public void getTodayRequests() {
        todayRequestList = new JSONArray();
        try {
            for (int i = 0; i < allRequestList.length(); i++) {
                JSONObject jsonObject = allRequestList.getJSONObject(i);
                Timestamp requestDate = Timestamp.valueOf(jsonObject.get("requestDate").toString());
                Calendar timeToCheck = Calendar.getInstance();
                timeToCheck.setTime(requestDate);
                Calendar now = Calendar.getInstance();
                if (now.get(Calendar.DAY_OF_YEAR) == timeToCheck.get(Calendar.DAY_OF_YEAR) && now.get(Calendar.YEAR) == timeToCheck.get(Calendar.YEAR)) {
                    todayRequestList.put(jsonObject);
                }
            }
        } catch (Exception e) {
        }
    }

    public void getUpcomingRequests() {
        upcomingRequestList = new JSONArray();
        try {
            for (int i = 0; i < allRequestList.length(); i++) {
                JSONObject jsonObject = allRequestList.getJSONObject(i);
                Timestamp requestDate = Timestamp.valueOf(jsonObject.get("requestDate").toString());
                Calendar timeToCheck = Calendar.getInstance();
                timeToCheck.setTime(requestDate);
                Calendar now = Calendar.getInstance();
                int status = jsonObject.getInt("status");
                if (status == 1) {
                    upcomingRequestList.put(jsonObject);
                }
            }
        } catch (Exception e) {
        }
    }

    public void getCompletedRequests() {
        completedRequestList = new JSONArray();
        try {
            for (int i = 0; i < allRequestList.length(); i++) {
                JSONObject jsonObject = allRequestList.getJSONObject(i);
                int status = jsonObject.getInt("status");
                if (status == 5) {
                    completedRequestList.put(jsonObject);
                }
            }
        } catch (Exception e) {
        }
    }

    public void getSearchRequest(String searchText, JSONArray toSearchList) {
        searchRequestList = new JSONArray();
        try {
            for (int i = 0; i < toSearchList.length(); i++) {
                JSONObject jsonObject = toSearchList.getJSONObject(i);
                if (selectedSearchType.equalsIgnoreCase("PickUp Agent")) {
                    try {
                        JSONObject pickUpAgent = new JSONObject(jsonObject.get("pickUpAgent").toString());
                        if (pickUpAgent.get("name").toString().equalsIgnoreCase(searchText)) {
                            searchRequestList.put(jsonObject);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (selectedSearchType.equalsIgnoreCase("Pincode")) {
                    try {
                        JSONObject reqAddress = new JSONObject(jsonObject.get("req_address").toString());
                        if (reqAddress.get("pincode").toString().equalsIgnoreCase(searchText)) {
                            searchRequestList.put(jsonObject);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    JSONObject reqAddress = new JSONObject(jsonObject.get("req_address").toString());
                    try {
                        if (reqAddress.get("street").toString().equalsIgnoreCase(searchText)) {
                            searchRequestList.put(jsonObject);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
        }

    }

    private void setSearchTypeSpinner() {
        final List<String> searchTypeList = new ArrayList<>(Arrays.asList(searchType));

        final ArrayAdapter<String> searchTypeSpinnerArrayAdapter = new ArrayAdapter<String>(
                getApplicationContext(), R.layout.spinner_item, searchTypeList) {
            @Override
            public boolean isEnabled(int position) {
                return true;
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);

                return view;
            }
        };

        searchTypeSpinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        searchTypeSpinner.setAdapter(searchTypeSpinnerArrayAdapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void showDialog(String text) {
        ImageView dialog_image;
        TextView dialog_text;
        updateDialog.setContentView(R.layout.loading_popup);
        updateDialog.setCanceledOnTouchOutside(false);
        updateDialog.setCancelable(false);
        dialog_image = updateDialog.findViewById(R.id.loading_image);
        dialog_text = updateDialog.findViewById(R.id.loading_text);
        Glide.with(this).load(R.drawable.ic_dash_req_card).into(dialog_image);
        dialog_text.setText(text);
        updateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        updateDialog.show();
    }

    private void showSnackbarAPI() {
        snackbar = Snackbar.make(findViewById(android.R.id.content), "Something went wrong!",
                Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("RETRY", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
                getRequestList();
            }
        });
        snackbar.show();
        snackbar.setActionTextColor(Color.RED);

    }
}