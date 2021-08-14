package com.ata.gogreenowner.Adapter;


import com.ata.gogreenowner.Model.JunkYardOwnerDeviceFCM;
import com.ata.gogreenowner.Model.LoginObject;

import java.util.HashMap;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiInterface {

    @POST("/auth/junkYardOwner/login")
    Call<HashMap<Object,Object>> userLogin(
            @Body LoginObject loginObject
    );

    @GET("/junkYardOwner/getAddress")
    Call<HashMap<Object,Object>> getMySavedAddress(
            @Header("Authorization") String authHeader
    );

    @POST("/junkYardOwner/generateNewToken")
    Call<HashMap<Object,Object>> generateNewJwtToken(
            @Query("phoneNumber") String phoneNumber,
            @Header("Authorization") String authHeader
    );

    @POST("/notification/registerDevice")
    Call<HashMap<Object,Object>> registerDevice(@Header("Authorization") String auth,
                                                @Query("requestId") String requestId,
                                                @Body JunkYardOwnerDeviceFCM junkYardOwnerDeviceFCM);


    @GET("/auth/junkYardOwner/getRequestList")
    Call<HashMap<Object,Object>> getRequestList(
            @Header("Authorization") String authHeader
    );

    @Multipart
    @POST("/auth/junkYardOwner/registerPickupBoy")
    Call<HashMap<Object,Object>> registerPickupBoy(@Header("Authorization") String jwtToken,
                                               @Part MultipartBody.Part profilePic,
                                               @Part MultipartBody.Part idPic,
                                               @Part("name") RequestBody name,
                                               @Part("phoneNumber") RequestBody phoneNumber,
                                               @Part("localIdNumber") RequestBody localIdNumber);

    @GET("/auth/junkyard/verifyPickupBoy")
    Call<HashMap<Object,Object>> verifyPickupBoy(@Header("Authorization") String jwtToken,
                                                 @Query("otp") String otp,
                                                 @Query("phoneNumber") String phoneNumber);

}
