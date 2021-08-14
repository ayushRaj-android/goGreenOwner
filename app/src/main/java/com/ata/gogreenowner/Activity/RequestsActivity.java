package com.ata.gogreenowner.Activity;

import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ata.gogreenowner.Adapter.AllRequestRecyclerAdapter;
import com.ata.gogreenowner.Adapter.ApiClient;
import com.ata.gogreenowner.Adapter.ApiInterface;
import com.ata.gogreenowner.R;
import com.ata.gogreenowner.Utility.SharedPreference;

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

    private RadioGroup reqTypeRadioGrp;
    private AppCompatSpinner searchTypeSpinner;
    private SearchView requestSearchView;
    private final String[] searchType = {"Pickup Agent","Pincode","Street Address"};
    private String selectedSearchType = "Pickup Agent";
    private RecyclerView requestsRecyclerView;
    private AllRequestRecyclerAdapter allRequestRecyclerAdapter;
    private Dialog updateDialog;
    TextView errorTV;
    JSONArray customerArray;
    SharedPreference sharedPreference;
    String radioGrpText="All";

    private JSONArray allRequestList;
    private JSONArray todayRequestList;
    private JSONArray upcomingRequestList;
    private JSONArray completedRequestList;
    private JSONArray searchRequestList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        errorTV = findViewById(R.id.errorTV);
        updateDialog = new Dialog(this);

        sharedPreference=new SharedPreference(this);
        allRequestList=new JSONArray();
        todayRequestList=new JSONArray();
        upcomingRequestList=new JSONArray();
        completedRequestList=new JSONArray();
        searchRequestList=new JSONArray();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);

        reqTypeRadioGrp = findViewById(R.id.req_type_radio_grp);
        searchTypeSpinner = findViewById(R.id.search_by_spinner);
        requestSearchView = findViewById(R.id.search_view_req);
        requestsRecyclerView = findViewById(R.id.requests_rv);
        requestsRecyclerView.hasFixedSize();
        requestsRecyclerView.setLayoutManager(new LinearLayoutManager(this
                ,LinearLayoutManager.VERTICAL,false));
        reqTypeRadioGrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (null != rb && checkedId > -1) {
                    radioGrpText=rb.getText().toString();
                    Toast.makeText(RequestsActivity.this, rb.getText(), Toast.LENGTH_SHORT).show();
                    if(rb.getText().toString().equalsIgnoreCase("Today")){
                        allRequestRecyclerAdapter.requestListToShow=todayRequestList;
                        allRequestRecyclerAdapter.notifyDataSetChanged();
                    }
                    else if(rb.getText().toString().equalsIgnoreCase("Upcoming")){
                        allRequestRecyclerAdapter.requestListToShow=upcomingRequestList;
                        allRequestRecyclerAdapter.notifyDataSetChanged();
                    }
                    else if(rb.getText().toString().equalsIgnoreCase("Completed")){
                        allRequestRecyclerAdapter.requestListToShow=completedRequestList;
                        allRequestRecyclerAdapter.notifyDataSetChanged();
                    }
                    else{
                        allRequestRecyclerAdapter.requestListToShow=allRequestList;
                        allRequestRecyclerAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

        searchTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(requestSearchView.getQuery().length() == 0) {
                    requestSearchView.setQuery("", false);
                    requestSearchView.setIconified(true);
                } else {
                    requestSearchView.setQuery("", false);
                    //pickupBoyRecyclerAdapter.filter(null);
                }
                hideKeyboard();
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
                Log.d("Ayush",radioGrpText);
                if(radioGrpText.equalsIgnoreCase("all")){
                    getSearchRequest(query,allRequestList);
                }
                else if(radioGrpText.equalsIgnoreCase("Today")){
                    getSearchRequest(query,todayRequestList);
                }
                else if(radioGrpText.equalsIgnoreCase("Upcoming")){
                    getSearchRequest(query,upcomingRequestList);
                }
                else{
                    getSearchRequest(query,completedRequestList);
                }
                if(searchRequestList.length()==0){
                    Toast.makeText(RequestsActivity.this,"Searched Item Not Found", Toast.LENGTH_SHORT).show();
                }
                else{
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

    }

    private void getRequestList(){
        ApiClient apiClient = new ApiClient(getApplicationContext());
        ApiInterface apiService = apiClient.getClient().create(ApiInterface.class);

        Call<HashMap<Object, Object>> call=apiService.getRequestList("Bearer "+sharedPreference.getJwtToken());

        call.enqueue(new Callback<HashMap<Object, Object>>() {
            @Override
            public void onResponse(Call<HashMap<Object, Object>> call,
                                   Response<HashMap<Object, Object>> response) {
                Log.d("Ayush",response.toString());
                if(response.isSuccessful() && response.body()!=null){
                    HashMap<Object, Object> resultMap = response.body();
                    int statusCode = (int) (double) resultMap.get("statusCode");
                    Log.d("Ayush", String.valueOf(statusCode));
                    if (statusCode == -1) {
                        updateDialog.dismiss();
                        errorTV.setVisibility(View.VISIBLE);
                        errorTV.setText("Owner Invalid");
                        errorTV.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                errorTV.setVisibility(View.GONE);
                            }
                        }, 5000);
                    }
                    else if(statusCode==2){
                        try {
                            Object customerObj=resultMap.get("data");
                            customerArray=new JSONArray(customerObj.toString());
                            allRequestList=customerArray;
                            getTodayRequests();
                            getUpcomingRequests();
                            getCompletedRequests();
                            allRequestRecyclerAdapter = new AllRequestRecyclerAdapter(getApplicationContext(),allRequestList);
                            requestsRecyclerView.setAdapter(allRequestRecyclerAdapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    else{
                        updateDialog.dismiss();
                        errorTV.setVisibility(View.VISIBLE);
                        errorTV.setText("Internal Server Exception");
                        errorTV.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                errorTV.setVisibility(View.GONE);
                            }
                        }, 5000);
                    }
                }
                else {
                    updateDialog.dismiss();
                    errorTV.setVisibility(View.VISIBLE);
                    errorTV.setText("Internal Server Exception");
                    errorTV.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            errorTV.setVisibility(View.GONE);
                        }
                    }, 5000);
                }
            }
            @Override
            public void onFailure(Call<HashMap<Object, Object>> call, Throwable t) {
            }
        });
    }

    public void getTodayRequests(){
        todayRequestList=new JSONArray();
        try {
            for (int i = 0; i < allRequestList.length(); i++) {
                JSONObject jsonObject = allRequestList.getJSONObject(i);
                Timestamp requestDate=Timestamp.valueOf(jsonObject.get("requestDate").toString());
                Calendar timeToCheck=Calendar.getInstance();
                timeToCheck.setTime(requestDate);
                Calendar now=Calendar.getInstance();
                if(now.get(Calendar.DAY_OF_YEAR)==timeToCheck.get(Calendar.DAY_OF_YEAR) && now.get(Calendar.YEAR)==timeToCheck.get(Calendar.YEAR)){
                    todayRequestList.put(jsonObject);
                }
            }
        }catch (Exception e){
            Log.d("Ayush","Exception in get Today's request");
        }
    }

    public void getUpcomingRequests(){
        upcomingRequestList=new JSONArray();
        try {
            for (int i = 0; i < allRequestList.length(); i++) {
                JSONObject jsonObject = allRequestList.getJSONObject(i);
                Timestamp requestDate=Timestamp.valueOf(jsonObject.get("requestDate").toString());
                Calendar timeToCheck=Calendar.getInstance();
                timeToCheck.setTime(requestDate);
                Calendar now=Calendar.getInstance();
                int status=jsonObject.getInt("status");
                if(status==1){
                    upcomingRequestList.put(jsonObject);
                }
            }
        }catch (Exception e){
            Log.d("Ayush","Exception in get Today's request");
        }
    }

    public void getCompletedRequests(){
        completedRequestList=new JSONArray();
        try {
            for (int i = 0; i < allRequestList.length(); i++) {
                JSONObject jsonObject = allRequestList.getJSONObject(i);
                int status=jsonObject.getInt("status");
                if(status==5){
                    completedRequestList.put(jsonObject);
                }
            }
        }catch (Exception e){
            Log.d("Ayush","Exception in get Today's request");
        }
    }

    public void getSearchRequest(String searchText,JSONArray toSearchList){
        searchRequestList=new JSONArray();
            try {
                for (int i = 0; i < toSearchList.length(); i++) {
                    JSONObject jsonObject = toSearchList.getJSONObject(i);
                    if(selectedSearchType.equalsIgnoreCase("pickUp Agent")){
                        JSONObject pickUpAgent=new JSONObject(jsonObject.get("pickUpAgent").toString());
                        if(pickUpAgent.get("name").toString().equalsIgnoreCase(searchText)){
                            searchRequestList.put(jsonObject);
                        }
                    }
                  else if(selectedSearchType.equalsIgnoreCase("pincode")){
                        JSONObject reqAddress=new JSONObject(jsonObject.get("req_address").toString());
                        if(reqAddress.get("pincode").toString().equalsIgnoreCase(searchText)){
                            searchRequestList.put(jsonObject);
                        }
                    }
                  else {
                        JSONObject reqAddress=new JSONObject(jsonObject.get("req_address").toString());
                        if(reqAddress.get("street").toString().equalsIgnoreCase(searchText)){
                            searchRequestList.put(jsonObject);
                        }
                    }
                }
            }catch (Exception e){
                Log.d("Ayush","Exception in get Today's request");
            }

    }

    private void setSearchTypeSpinner(){
        final List<String> searchTypeList = new ArrayList<>(Arrays.asList(searchType));

        final ArrayAdapter<String> searchTypeSpinnerArrayAdapter = new ArrayAdapter<String>(
                getApplicationContext(),R.layout.spinner_item,searchTypeList){
            @Override
            public boolean isEnabled(int position){
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

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}