package com.ata.gogreenowner.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.RecyclerView;

import com.ata.gogreenowner.Activity.RequestsActivity;
import com.ata.gogreenowner.R;
import com.ata.gogreenowner.Utility.SharedPreference;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PickupBoyPopupRecyclerAdapter extends RecyclerView.Adapter<PickupBoyPopupRecyclerAdapter.ViewHolder> {

    private Context context;
    private List<JSONObject> changingList;
    private List<JSONObject> mainList;
    private Dialog dialog;
    private List<String> selectedRequestIdList;
    private SharedPreference sharedPreference;

    public PickupBoyPopupRecyclerAdapter(Context context, List<JSONObject> jsonObjectList
            , Dialog dialog, List<String> selectedRequestIdList) {
        this.context = context;
        this.changingList = jsonObjectList;
        this.dialog = dialog;
        this.selectedRequestIdList = selectedRequestIdList;
        mainList = new ArrayList<>();
        mainList.addAll(jsonObjectList);
        sharedPreference = new SharedPreference(context);
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

    public void filter(String filterWord) {
        changingList.clear();
        if (filterWord == null || filterWord.length() == 0) {
            changingList.addAll(mainList);
        } else {
            try {
                for (JSONObject jsonObject : mainList) {
                    if (jsonObject.get("name").toString().toLowerCase().startsWith(filterWord)) {
                        changingList.add(jsonObject);
                    }
                }
            } catch (Exception e) {
                changingList.addAll(mainList);
                e.printStackTrace();
            }
        }
        notifyDataSetChanged();
    }

    public void showLodingPopup() {
        ImageView dialog_image;
        TextView dialog_text;
        dialog.setContentView(R.layout.loading_popup);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog_image = dialog.findViewById(R.id.loading_image);
        dialog_text = dialog.findViewById(R.id.loading_text);
        Glide.with(context).load(R.drawable.loader).into(dialog_image);
        dialog_text.setText("Assigning Pickup Agent");
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void showSuccessDialog() {
        ImageView dialog_image;
        TextView dialog_text;
        dialog.setContentView(R.layout.loading_popup);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog_image = dialog.findViewById(R.id.loading_image);
        dialog_text = dialog.findViewById(R.id.loading_text);
        Glide.with(context).load(R.drawable.ic_baseline_check_circle_outline_24).into(dialog_image);
        dialog_text.setText("Pickup Boy Assigned to the request!\nRedirecting in 5 seconds");
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public void callAssignPickupApi(long id) {
        showLodingPopup();
        ApiClient apiClient = new ApiClient(context);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        String jwtToken = "Bearer " + sharedPreference.getJwtToken();
        Call<HashMap<Object, Object>> call = apiService.assignPickupBoy(jwtToken,
                selectedRequestIdList, id);
        call.enqueue(new Callback<HashMap<Object, Object>>() {
            @Override
            public void onResponse(Call<HashMap<Object, Object>> call, Response<HashMap<Object, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    HashMap<Object, Object> resultMap = response.body();
                    int statusCode = (int) (double) resultMap.get("statusCode");
                    if (statusCode == 2) {
                        dialog.dismiss();
                        showSuccessDialog();
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //Do something here
                                Intent intent = new Intent(context, RequestsActivity.class);
                                context.startActivity(intent);
                                ((Activity) context).finish();
                            }
                        }, 5000);
                    } else {
                        dialog.dismiss();
                    }
                } else {
                    dialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<HashMap<Object, Object>> call, Throwable t) {

            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView profilePic;
        private TextView agentName;
        private AppCompatImageButton assignButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profilePic = itemView.findViewById(R.id.pickup_popup_prof_pic);
            agentName = itemView.findViewById(R.id.pickup_popup_name);
            assignButton = itemView.findViewById(R.id.pickup_popup_assign);
        }

        public void bindJSONObject(JSONObject jsonObject) {
            try {
                String url = jsonObject.getString("profilePicUrl");
                Glide.with(context).load(url).apply(RequestOptions.circleCropTransform()).optionalCircleCrop().into(profilePic);
                agentName.setText(jsonObject.get("name").toString());
                assignButton.setOnClickListener(v -> {
                    try {
                        callAssignPickupApi(Long.parseLong(jsonObject.get("id").toString()));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
