package com.ata.gogreenowner.Utility;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreference {
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Context context;
    int PRIVATE_MODE = 0;
    private static final String PREFER_NAME = "Quiz";
    private static final String IS_USER_LOGIN = "IsUserLoggedIn";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USER_NAME = "name";
    private static final String KEY_USER_PHONE = "phoneNumber";
    private static final String KEY_USER_IMAGE = "imageUrl";
    private static final String KEY_USER_FCM_TOKEN = "fcmToken";
    private static final String JWT_TOKEN = "jwtToken";
    private static final String REFRESH_TOKEN = "refreshToken";
    private static final String MY_PICKUP_BOY = "myPickupBoy";
    private static final String TOTAL_EARNING = "totalEarning";
    private static final String TOTAL_REQUEST = "totalRequest";

    public SharedPreference(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(PREFER_NAME,PRIVATE_MODE);
        editor = preferences.edit();
    }

    public void createUserLoginSession(String name, String phoneNumber,String imageUrl
            ,String jwtToken, String refreshToken, String totalEarning, String totalRequest){
        editor.putBoolean(IS_USER_LOGIN,true);
        editor.putString(KEY_USER_NAME,name);
        editor.putString(KEY_USER_PHONE,phoneNumber);
        editor.putString(KEY_USER_IMAGE,imageUrl);
        editor.putString(JWT_TOKEN,jwtToken);
        editor.putString(REFRESH_TOKEN,refreshToken);
        editor.putString(TOTAL_EARNING,totalEarning);
        editor.putString(TOTAL_REQUEST,totalRequest);
        editor.commit();
    }

    public void insertToken(String jwtToken, String refreshToken){
        editor.putBoolean(IS_USER_LOGIN,true);
        editor.putString(JWT_TOKEN,jwtToken);
        editor.putString(REFRESH_TOKEN,refreshToken);
        editor.commit();
    }

    public void insertMyPickupBoy(String pickupBoyJsonString){
        editor.putString(MY_PICKUP_BOY,pickupBoyJsonString);
        editor.commit();
    }

    public void insertMyProfilePic(String profilePicUrl){
        editor.putString(KEY_USER_IMAGE,profilePicUrl);
        editor.commit();
    }

    public void insertMyName(String name){
        editor.putString(KEY_USER_NAME,name);
        editor.commit();
    }

    public void removeMyPickupBoy(){
        editor.putString(MY_PICKUP_BOY,null);
        editor.commit();
    }

    public void updateJwt(String jwtToken){
        editor.putBoolean(IS_USER_LOGIN,true);
        editor.putString(JWT_TOKEN,jwtToken);
        editor.commit();
    }

    public void insertRequest(String totalRequest){
        editor.putString(TOTAL_REQUEST,totalRequest);
        editor.commit();
    }

    public void insertEarning(String totalEarning){
        editor.putString(TOTAL_EARNING,totalEarning);
        editor.commit();
    }

    public boolean isUserLoggedIn(){
        return preferences.getBoolean(IS_USER_LOGIN,false);
    }

    public String getUserId(){
        return  preferences.getString(KEY_USER_ID,null);
    }

    public String getUserName(){
        return  preferences.getString(KEY_USER_NAME,null);
    }

    public String getUserPhone(){
        return  preferences.getString(KEY_USER_PHONE,null);
    }

    public String getUserImageUrl(){
        return  preferences.getString(KEY_USER_IMAGE,null);
    }

    public String getJwtToken(){
        return  preferences.getString(JWT_TOKEN,null);
    }

    public String getRefreshToken(){
        return  preferences.getString(REFRESH_TOKEN,null);
    }

    public String getMyPickupBoy(){
        return  preferences.getString(MY_PICKUP_BOY,null);
    }

    public String getTotalRequest(){
        return  preferences.getString(TOTAL_REQUEST,null);
    }

    public String getTotalEarning(){
        return  preferences.getString(TOTAL_EARNING,null);
    }

    public void logoutUser(){
        editor.clear();
        editor.commit();
    }
}