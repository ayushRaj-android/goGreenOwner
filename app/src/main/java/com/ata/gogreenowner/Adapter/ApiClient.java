package com.ata.gogreenowner.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ata.gogreenowner.Activity.SplashActivity;
import com.ata.gogreenowner.Utility.SharedPreference;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    public static final String BASE_URL = "https://green.travelparcelservice.com";//"https://quizziyapa.herokuapp.com/";
    static SharedPreference sharedPreference;
    static Context context;
    private static Retrofit retrofit = null;

    public ApiClient(Context context) {
        sharedPreference = new SharedPreference(context);
        ApiClient.context = context;
    }

    public static Retrofit getClient() {
        if (retrofit == null) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new Interceptor() {
                        @NotNull
                        @Override
                        public Response intercept(@NotNull Chain chain) throws IOException {
                            Request original = chain.request();
                            Response response = chain.proceed(original);
                            if (response.code() == 401) {
                                updateJwtToken();
                                Request request = original.newBuilder()
                                        .header("Authorization", "Bearer " +
                                                sharedPreference.getJwtToken())
                                        .method(original.method(), original.body())
                                        .build();
                                response.close();
                                Response newResp = chain.proceed(request);
                                return newResp;
                            }
                            return response;
                        }
                    })
                    .build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();
        }
        return retrofit;
    }

    public static void updateJwtToken(){
        try {
            String phoneNumber = sharedPreference.getUserPhone();
            String refreshToken = "Bearer " + sharedPreference.getRefreshToken();
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<HashMap<Object,Object>> call = apiService.generateNewJwtToken(phoneNumber, refreshToken);
            call.enqueue(new Callback<HashMap<Object,Object>>() {
                @Override
                public void onResponse(Call<HashMap<Object,Object>> call, retrofit2.Response<HashMap<Object,Object>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        HashMap<Object, Object> resultMap = response.body();
                        int statusCode = (int) (double) resultMap.get("statusCode");
                        if(statusCode == 1){
                            String token = resultMap.get("message").toString();
                            sharedPreference.updateJwt(token);
                        } else {
                            sharedPreference.logoutUser();
                            Intent i = new Intent(context, SplashActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(i);
                        }

                    }
                }

                @Override
                public void onFailure(Call<HashMap<Object,Object>> call, Throwable t) {
                }
            });
        }catch (Exception e){
            sharedPreference.logoutUser();
            Intent i = new Intent(context, SplashActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }
}
