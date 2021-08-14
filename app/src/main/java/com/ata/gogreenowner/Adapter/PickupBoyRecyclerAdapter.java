package com.ata.gogreenowner.Adapter;

import static java.security.AccessController.getContext;

import android.Manifest;
import android.app.Activity;
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
import androidx.appcompat.widget.AppCompatImageView;
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

public class PickupBoyRecyclerAdapter extends RecyclerView.Adapter<PickupBoyRecyclerAdapter.ViewHolder>
        implements ActivityCompat.OnRequestPermissionsResultCallback{

    private Context context;
    private List<JSONObject> changingList;
    private List<JSONObject> mainList;
    private static final int PHONE_REQUEST_CODE = 105;

    public PickupBoyRecyclerAdapter(Context context, List<JSONObject> jsonObjectList) {
        this.context = context;
        this.changingList = jsonObjectList;
        mainList = new ArrayList<>();
        mainList.addAll(jsonObjectList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pickup_agent_cardview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        JSONObject jsonObject = changingList.get(position);
        try {
            holder.agentName.setText(jsonObject.get("name").toString());
            holder.agentPhone.setText(jsonObject.get("phone").toString());
            String url = jsonObject.get("profilePicUrl").toString();
            holder.setProfilePic(url);
            holder.agentCall.setOnClickListener( v->{
                try {
                    makeCall(jsonObject.get("phone").toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return changingList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView profilePic;
        private TextView agentName;
        private TextView agentPhone;
        private AppCompatImageButton agentCall;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profilePic = itemView.findViewById(R.id.pick_agent_card_profile_pic);
            agentName = itemView.findViewById(R.id.pick_agent_card_name);
            agentPhone = itemView.findViewById(R.id.pick_agent_card_phone);
            agentCall = itemView.findViewById(R.id.pick_agent_card_call);

        }

        private void setProfilePic(String url){
            Glide.with(context).load(url).apply(RequestOptions.circleCropTransform())
                    .optionalCircleCrop().into(profilePic);
        }
    }

    private void makeCall(String phoneNumber){
        if(getCallingPermission()){
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:"+phoneNumber));
            context.startActivity(intent);
        }else{
            askCallingPermission();
        }
    }

    private boolean getCallingPermission(){
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE)
                == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        return false;
    }

    private void askCallingPermission() {
        if(ContextCompat.checkSelfPermission(context,Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.CALL_PHONE},PHONE_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        if (requestCode == PHONE_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //permission granted
            } else {
                //permission not granted
                askCallingPermission();
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
}