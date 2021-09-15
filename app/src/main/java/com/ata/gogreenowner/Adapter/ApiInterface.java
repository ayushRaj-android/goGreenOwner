package com.ata.gogreenowner.Adapter;


import com.ata.gogreenowner.Model.JunkYardOwnerDeviceFCM;
import com.ata.gogreenowner.Model.LoginObject;

import java.util.HashMap;
import java.util.List;

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

    @POST("/auth/junkYardOwner/verify-resetPassword")
    Call<HashMap<Object,Object>> otpVerify(
            @Query("otp")String otp,
            @Query("phoneNumber")String phoneNumber,
            @Query("purpose") String purpose
    );

    @POST("/auth/junkYardOwner/forgot-password")
    Call<HashMap<Object,Object>> forgotPassword(
            @Query("phoneNumber")String phoneNumber
    );

    @POST("/auth/junkYardOwner/resend-otp")
    Call<HashMap<Object,Object>> resendOtp(
            @Query("phoneNumber")String phoneNumber,
            @Query("purpose")String purpose
    );

    @GET("/junkYardOwner/getAddress")
    Call<HashMap<Object,Object>> getMySavedAddress(
            @Header("Authorization") String authHeader
    );

    @POST("/auth/notification/registerDevice")
    Call<HashMap<Object,Object>> registerDevice(@Body JunkYardOwnerDeviceFCM junkYardOwnerDeviceFCM,
                                                @Query("type") String type);

    @POST("/junkYardOwner/generateNewToken")
    Call<HashMap<Object,Object>> generateNewJwtToken(
            @Query("phoneNumber") String phoneNumber,
            @Header("Authorization") String authHeader
    );


    @GET("/junkYardOwner/getRequestList")
    Call<HashMap<Object,Object>> getRequestList(
            @Header("Authorization") String authHeader
    );

    @GET("/junkYardOwner/getPendingRequestList")
    Call<HashMap<Object,Object>> getPendingRequestList(
            @Header("Authorization") String authHeader,
            @Query("pageNo") int pageNo
    );

    @Multipart
    @POST("/junkYardOwner/registerPickupBoy")
    Call<HashMap<Object,Object>> registerPickupBoy(@Header("Authorization") String jwtToken,
                                               @Part MultipartBody.Part profilePic,
                                               @Part MultipartBody.Part idPic,
                                               @Part("name") RequestBody name,
                                               @Part("phoneNumber") RequestBody phoneNumber,
                                               @Part("localIdNumber") RequestBody localIdNumber);

    @GET("/junkYardOwner/verifyPickupBoy")
    Call<HashMap<Object,Object>> verifyPickupBoy(@Header("Authorization") String jwtToken,
                                                 @Query("otp") String otp,
                                                 @Query("phoneNumber") String phoneNumber);

    @GET("/junkYardOwner/getMyPickupBoy")
    Call<HashMap<Object,Object>> getMyPickupBoy(@Header("Authorization") String jwtToken);

    @GET("/junkYardOwner/assignPickupBoy")
    Call<HashMap<Object,Object>> assignPickupBoy(@Header("Authorization") String jwtToken,
                                                 @Query("requestId") List<String> requestId,
                                                 @Query("pickupBoyId") long pickupBoyId);

}
