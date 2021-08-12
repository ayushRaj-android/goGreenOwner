package com.ata.gogreenowner.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.recyclerview.widget.RecyclerView;

import com.ata.gogreenowner.Activity.PendingRequestActivity;
import com.ata.gogreenowner.Model.PendingRequests;
import com.ata.gogreenowner.R;
import com.ata.gogreenowner.Utility.PendingRequestListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PendingRequestRecyclerAdapter extends RecyclerView.Adapter<PendingRequestRecyclerAdapter.ViewHolder> {
    private Context context;
    private List<PendingRequests> pendingRequestsList;
    private PendingRequestListener pendingRequestListener;
    private List<PendingRequests> selectedPendingRequestList;

    public PendingRequestRecyclerAdapter(Context context, List<PendingRequests> pendingRequestsList, PendingRequestListener pendingRequestListener) {
        this.context = context;
        this.pendingRequestsList = pendingRequestsList;
        this.pendingRequestListener = pendingRequestListener;
        selectedPendingRequestList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pending_request_card, parent, false);
        return new PendingRequestRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PendingRequests pendingRequests = pendingRequestsList.get(position);
        holder.bindPendingRequest(pendingRequests);
    }

    @Override
    public int getItemCount() {
        return pendingRequestsList.size();
    }

    public List<PendingRequests> getSelectedPendingRequest(){
         return selectedPendingRequestList;
    }

    public void selectAllRequest(){
        selectedPendingRequestList.clear();
        for(PendingRequests pendingRequests : pendingRequestsList){
            pendingRequests.setSelected(true);
        }
        selectedPendingRequestList.addAll(pendingRequestsList);
        notifyDataSetChanged();
    }

    public void unselectAllRequest(){
        selectedPendingRequestList.clear();
        for(PendingRequests pendingRequests : pendingRequestsList){
            pendingRequests.setSelected(false);
        }
        pendingRequestListener.onAssignPickupBoyAction(false);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout cardContentLayout;
        AppCompatCheckBox pendingReqCheckBox;
        TextView addressTextView, requestedOnTextView, requestDateTextView, requestTimeTextView;
        ImageView userProfilePicImageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            addressTextView = itemView.findViewById(R.id.address_tv);
            pendingReqCheckBox = itemView.findViewById(R.id.pending_req_card_cb);
            cardContentLayout = itemView.findViewById(R.id.cardContentLayout);
        }

        void bindPendingRequest(PendingRequests pendingRequests){
//            addressTextView.setText(pendingRequests.getAddress());
            if(pendingRequests.isSelected()){
                pendingReqCheckBox.setChecked(true);
            }else{
                pendingReqCheckBox.setChecked(false);
            }
            cardContentLayout.setOnClickListener( v->{
                if(pendingReqCheckBox.isChecked()){
                    selectedPendingRequestList.remove(pendingRequests);
                    pendingReqCheckBox.setChecked(false);
                    pendingRequests.setSelected(false);
                    if(selectedPendingRequestList.size() == 0){
                        pendingRequestListener.onAssignPickupBoyAction(false);
                    }
                }else{
                    selectedPendingRequestList.add(pendingRequests);
                    pendingReqCheckBox.setChecked(true);
                    pendingRequests.setSelected(true);
                    pendingRequestListener.onAssignPickupBoyAction(true);
                }
            });

            pendingReqCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(!isChecked){
                        selectedPendingRequestList.remove(pendingRequests);
                        pendingRequests.setSelected(false);
                        if(selectedPendingRequestList.size() == 0){
                            pendingRequestListener.onAssignPickupBoyAction(false);
                        }
                    }else{
                        selectedPendingRequestList.add(pendingRequests);
                        pendingRequests.setSelected(true);
                        pendingRequestListener.onAssignPickupBoyAction(true);
                    }
                }
            });
        }
    }
}
