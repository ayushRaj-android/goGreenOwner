package com.ata.gogreenowner.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ata.gogreenowner.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            address_tv=itemView.findViewById(R.id.address_tv);
        }

        public void bindViewHolder(JSONObject jsonObject){
            try {
                address_tv.setText(jsonObject.get("address").toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }


}
