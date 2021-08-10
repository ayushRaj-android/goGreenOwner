package com.ata.gogreenowner.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
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
import com.ata.gogreenowner.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RequestsActivity extends BaseActivity {

    private RadioGroup reqTypeRadioGrp;
    private AppCompatSpinner searchTypeSpinner;
    private SearchView requestSearchView;
    private final String[] searchType = {"Pickup Agent","Pincode","Street Address"};
    private String selectedSearchType = "Pickup Agent";
    private RecyclerView requestsRecyclerView;
    private AllRequestRecyclerAdapter allRequestRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
                    Toast.makeText(RequestsActivity.this, rb.getText(), Toast.LENGTH_SHORT).show();
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
        setSearchTypeSpinner();
        searchTypeSpinner.setSelection(0);
        allRequestRecyclerAdapter = new AllRequestRecyclerAdapter();
        requestsRecyclerView.setAdapter(allRequestRecyclerAdapter);
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