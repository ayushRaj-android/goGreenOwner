package com.ata.gogreenowner.Model;

import lombok.Data;

@Data
public class PendingRequests {
    private String requestId;
    String address, requestedOn, requestDate, requestTime, profilePicUrl;
    boolean isSelected;
    private String locality;

    public PendingRequests(String address, boolean isSelected) {
        this.address = address;
        this.isSelected = isSelected;
    }
}
