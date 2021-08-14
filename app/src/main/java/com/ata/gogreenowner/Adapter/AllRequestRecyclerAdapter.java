package com.ata.gogreenowner.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.ata.gogreenowner.R;
import com.ata.gogreenowner.View.StepView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class AllRequestRecyclerAdapter extends RecyclerView.Adapter<AllRequestRecyclerAdapter.ViewHolder> {

    Context context;
    JSONArray requestList;

    public AllRequestRecyclerAdapter(Context context, JSONArray requestList) {
        this.context = context;
        this.requestList = requestList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_request_card, parent, false);
        return new AllRequestRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            holder.bindViewHolder(requestList.getJSONObject(position));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return requestList.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView address_tv;
        TextView order_placement_time;
        StepView step_view;
        LinearLayout weightValueTab;
        AppCompatImageView lineAboveWeight;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            address_tv=itemView.findViewById(R.id.address_tv);
            order_placement_time=itemView.findViewById(R.id.order_placement_time);
            step_view=itemView.findViewById(R.id.step_view);
            weightValueTab=itemView.findViewById(R.id.weightValueTab);
            lineAboveWeight=itemView.findViewById(R.id.lineAboveWeight);

        }

        public void bindViewHolder(JSONObject jsonObject){
            try {
                JSONObject addressObj=new JSONObject(jsonObject.get("req_address").toString());
                address_tv.setText(addressObj.getString("street"));
                Timestamp requestDate=Timestamp.valueOf(jsonObject.get("requestDate").toString());
                Date date=new Date(requestDate.getTime());
                String timeStamp = new SimpleDateFormat("dd-MMM-yyyy").format(date);
                String dayWeekText = new SimpleDateFormat("EE").format(date);
                order_placement_time.setText(dayWeekText+" "+timeStamp);
                int status=jsonObject.getInt("status");
                status=-1;
                if(status==0){
                    step_view.selectedStep(1);
                    weightValueTab.setVisibility(View.GONE);
                    lineAboveWeight.setVisibility(View.GONE);
                }
                else if(status==1){
                    step_view.selectedStep(2);
                    weightValueTab.setVisibility(View.GONE);
                    lineAboveWeight.setVisibility(View.GONE);
                }
                else if(status==3){
                    step_view.selectedStep(3);
                }
                else if(status==5){
                    step_view.selectedStep(4);
                }
                else{
                    step_view.cancel(true);
                    weightValueTab.setVisibility(View.GONE);
                    lineAboveWeight.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }


}
