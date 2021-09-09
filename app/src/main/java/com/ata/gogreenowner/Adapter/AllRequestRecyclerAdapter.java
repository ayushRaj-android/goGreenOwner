package com.ata.gogreenowner.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.ata.gogreenowner.Activity.RequestDetailsActivity;
import com.ata.gogreenowner.R;
import com.ata.gogreenowner.View.StepView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AllRequestRecyclerAdapter extends RecyclerView.Adapter<AllRequestRecyclerAdapter.ViewHolder> {

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E, dd-MMM-yyyy");
    public JSONArray requestListToShow;
    Context context;

    public AllRequestRecyclerAdapter(Context context, JSONArray requestList) {
        this.context = context;
        this.requestListToShow = requestList;
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
            holder.bindViewHolder(requestListToShow.getJSONObject(position));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return requestListToShow.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView address_tv;
        TextView order_placement_time;
        StepView step_view;
        LinearLayout weightValueTab;
        AppCompatImageView lineAboveWeight;
        TextView weightText;
        TextView valueText;
        View mView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            address_tv = itemView.findViewById(R.id.address_tv);
            order_placement_time = itemView.findViewById(R.id.order_placement_time);
            step_view = itemView.findViewById(R.id.step_view);
            weightValueTab = itemView.findViewById(R.id.weightValueTab);
            lineAboveWeight = itemView.findViewById(R.id.lineAboveWeight);
            weightText = itemView.findViewById(R.id.weightText);
            valueText = itemView.findViewById(R.id.valueText);
        }

        public void bindViewHolder(JSONObject jsonObject) {
            try {
                JSONObject addressObj = new JSONObject(jsonObject.get("req_address").toString());
                address_tv.setText(addressObj.getString("street"));
                Timestamp requestDate = Timestamp.valueOf(jsonObject.get("requestDate").toString());
                Date date = new Date(requestDate.getTime());
                String timeStamp = new SimpleDateFormat("EE, dd-MMM-yyyy").format(date);
                order_placement_time.setText(timeStamp);
                int status = jsonObject.getInt("status");
                mView.setOnClickListener(v -> {
                    startRequestDetailsActivity(jsonObject);
                });

                if (status == 0) {
                    step_view.selectedStep(1);
                    weightValueTab.setVisibility(View.GONE);
                    lineAboveWeight.setVisibility(View.GONE);
                } else if (status == 1) {
                    step_view.selectedStep(2);
                    weightValueTab.setVisibility(View.GONE);
                    lineAboveWeight.setVisibility(View.GONE);
                } else if (status == 3) {
                    step_view.selectedStep(3);
                    weightValueTab.setVisibility(View.VISIBLE);
                    lineAboveWeight.setVisibility(View.VISIBLE);
                    if (jsonObject.get("amount") != null) {
                        valueText.setText("Value :- \u20B9 " + jsonObject.get("amount").toString());
                    } else {
                        valueText.setText("Value :- N/A");
                    }

                    if (jsonObject.has("weight") && jsonObject.get("weight") != null) {
                        weightText.setText("Weight :- " + jsonObject.get("weight").toString() + " kg");
                    } else {
                        weightText.setText("Weight :- N/A");
                    }
                } else if (status == 5) {
                    step_view.selectedStep(4);
                    weightValueTab.setVisibility(View.VISIBLE);
                    lineAboveWeight.setVisibility(View.VISIBLE);
                    if (jsonObject.get("amount") != null) {
                        valueText.setText("Value :- \u20B9 " + jsonObject.get("amount").toString());
                    } else {
                        valueText.setText("Value :- N/A");
                    }

                    if (jsonObject.has("weight") && jsonObject.get("weight") != null) {
                        weightText.setText("Weight :- " + jsonObject.get("weight").toString() + " kg");
                    } else {
                        weightText.setText("Weight :- N/A");
                    }
                } else {
                    step_view.cancel(true);
                    weightValueTab.setVisibility(View.GONE);
                    lineAboveWeight.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public void startRequestDetailsActivity(JSONObject jsonObject) {
            try {
                int status = Integer.parseInt(jsonObject.get("status").toString());
                Intent intent = new Intent(context, RequestDetailsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("status", status);
                JSONObject addressObject = jsonObject.getJSONObject("req_address");
                intent.putExtra("pickupLocation", addressObject.get("street").toString());
                intent.putExtra("locality", addressObject.get("locality").toString());
                Timestamp requestPlacementTimestamp = getTimestampFromString(
                        jsonObject.get("requestPlacementTimestamp").toString());
                Date requestPlacementDate = new Date(requestPlacementTimestamp.getTime());
                intent.putExtra("reqPlacementDate",
                        simpleDateFormat.format(requestPlacementDate));
                Timestamp pickupTimestamp = getTimestampFromString(jsonObject.get("requestDate")
                        .toString());
                Date pickupDate = new Date(pickupTimestamp.getTime());
                String pickupTime = simpleDateFormat.format(pickupDate) + ", " +
                        jsonObject.get("timeSlot").toString();
                intent.putExtra("pickupTime", pickupTime);
                try {
                    Timestamp serviceCompletionTimestamp = getTimestampFromString(
                            jsonObject.get("serviceCompletionTimestamp").toString());
                    Date serviceCompletionDate = new Date(requestPlacementTimestamp.getTime());
                    intent.putExtra("serviceCompletionDate",
                            simpleDateFormat.format(serviceCompletionDate));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                intent.putExtra("latitude", addressObject.get("latitude").toString());
                intent.putExtra("longitude", addressObject.get("longitude").toString());
                intent.putExtra("locality", addressObject.get("locality").toString());
                intent.putExtra("requestId", jsonObject.get("requestId").toString());
                if (status > 0) {
                    try {
                        JSONObject pickupBoyObj = jsonObject.getJSONObject("boyAssigned");
                        try {
                            intent.putExtra("pickupBoyName", pickupBoyObj.getString("name"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try {
                            intent.putExtra("pickupBoyPhone", pickupBoyObj.getString("phoneNumber"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try {
                            intent.putExtra("pickupBoyPicUrl", pickupBoyObj.getString("profilePicUrl"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try {
                            intent.putExtra("pickupBoyRating", pickupBoyObj.getString("rating"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (status >= 3) {
                    try {
                        intent.putExtra("amount", jsonObject.getString("amount"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        intent.putExtra("weight", jsonObject.getString("weight"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                context.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public Timestamp getTimestampFromString(String timestampString) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
            if (timestampString != null) {
                Date date = null;
                try {
                    date = sdf.parse(timestampString);
                    Timestamp timestamp = new Timestamp(date.getTime());
                    return timestamp;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

    }
}
