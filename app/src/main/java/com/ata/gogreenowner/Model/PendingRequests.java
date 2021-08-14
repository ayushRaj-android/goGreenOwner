package com.ata.gogreenowner.Model;

import lombok.Data;

@Data
public class PendingRequests {
    String address, requestedOn, requestDate, requestTime, profilePicUrl;
    boolean isSelected;

    public PendingRequests(String address, boolean isSelected) {
        this.address = address;
        this.isSelected = isSelected;
    }
}