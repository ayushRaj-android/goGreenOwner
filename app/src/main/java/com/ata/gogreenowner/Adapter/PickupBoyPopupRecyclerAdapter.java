package com.ata.gogreenowner.Adapter;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ata.gogreenowner.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PickupBoyPopupRecyclerAdapter extends RecyclerView.Adapter<PickupBoyPopupRecyclerAdapter.ViewHolder> {

    private Context context;
    private List<JSONObject> changingList;
    private List<JSONObject> mainList;
    private Dialog dialog;

    public PickupBoyPopupRecyclerAdapter(Context context, List<JSONObject> jsonObjectList,Dialog dialog) {
        this.context = context;
        this.changingList = jsonObjectList;
        this.dialog = dialog;
        mainList = new ArrayList<>();
        mainList.addAll(jsonObjectList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pickup_agent_popup_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        JSONObject jsonObject = changingList.get(position);
        holder.bindJSONObject(jsonObject);
    }

    @Override
    public int getItemCount() {
        return changingList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView profilePic;
        private TextView agentName;
        private AppCompatImageButton assignButton;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profilePic = itemView.findViewById(R.id.pickup_popup_prof_pic);
            agentName = itemView.findViewById(R.id.pickup_popup_name);
            assignButton = itemView.findViewById(R.id.pickup_popup_assign);
        }

        public void bindJSONObject(JSONObject jsonObject){
            try {
                String url = jsonObject.getString("profilePicUrl");
                Log.d("Ayush",url);
                Glide.with(context).load(url).optionalCircleCrop().into(profilePic);
                agentName.setText(jsonObject.get("name").toString());
                assignButton.setOnClickListener( v->{
                    Log.d("Ayush",jsonObject.toString());
                    //dialog.dismiss();
                    showLodingPopup();
                });
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    public void filter(String filterWord) {
        changingList.clear();
        if (filterWord == null || filterWord.length() == 0) {
            changingList.addAll(mainList);
        } else {
            try {
                for (JSONObject jsonObject : mainList) {
                    if (jsonObject.get("name").toString().toLowerCase().startsWith(filterWord)){
                        Log.d("Ayush",jsonObject.get("name").toString());
                        changingList.add(jsonObject);
                    }
                }
            }catch (Exception e){
                changingList.addAll(mainList);
                e.printStackTrace();
            }
        }
        notifyDataSetChanged();
    }

    public void showLodingPopup(){
        dialog.setContentView(R.layout.loading_popup);
        //assignPickupDialog.setCanceledOnTouchOutside(false);
    }
}
