package com.ata.gogreenowner.Model;

import lombok.Data;

@Data
public class LoginObject {
    String userPhone;

    String userPassword;

    String fcm;

    String deviceId;

    public LoginObject(String userPhone, String userPassword, String fcm, String deviceId) {
        this.userPhone = userPhone;
        this.userPassword = userPassword;
        this.fcm = fcm;
        this.deviceId = deviceId;
    }
}
