package com.ata.gogreenowner.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;

import com.ata.gogreenowner.Adapter.ApiClient;
import com.ata.gogreenowner.Adapter.ApiInterface;
import com.ata.gogreenowner.Model.LoginObject;
import com.ata.gogreenowner.R;
import com.bumptech.glide.Glide;
import com.ata.gogreenowner.Utility.SharedPreference;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends BaseActivity {

    private Button signUpButton;
    private Button signInButton;
    private TextInputEditText phoneText;
    private TextInputEditText passwordText;
    TextView errorTV;
    TextView forgotPasswordTV;
    SharedPreference sharedPreference;
    private Dialog updateDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        phoneText = findViewById(R.id.phone_edit);
        passwordText = findViewById(R.id.password_edit);
        signUpButton = findViewById(R.id.signUpButton);
        signInButton = findViewById(R.id.signInButton);
        errorTV = findViewById(R.id.errorTV);
        forgotPasswordTV = findViewById(R.id.forgot_password_tv);
        sharedPreference = new SharedPreference(getApplicationContext());
        updateDialog = new Dialog(this);

        ApiClient apiClient = new ApiClient(getApplicationContext());
        ApiInterface apiService = apiClient.getClient().create(ApiInterface.class);

        signInButton.setOnClickListener(v -> {
            String phone = "91" + phoneText.getText().toString();
            String password = passwordText.getText().toString();
            if (!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(password)) {
                showDialog("Earth is excited seeing you getting ready for contributing to the greener world!");

                LoginObject loginObject = new LoginObject(phone, password, null, null);
                Log.d("Ayush", loginObject.toString());
                Call<HashMap<Object, Object>> call = apiService.userLogin(loginObject);
                call.enqueue(new Callback<HashMap<Object, Object>>() {
                    @Override
                    public void onResponse(Call<HashMap<Object, Object>> call,
                                           Response<HashMap<Object, Object>> response) {
                        Log.d("Ayush", response.toString());
                        if (response.isSuccessful() && response.body() != null) {
                            Log.d("Ayush", response.body().toString());
                            HashMap<Object, Object> resultMap = response.body();
                            int statusCode = (int) (double) resultMap.get("statusCode");
                            Log.d("Ayush", String.valueOf(statusCode));
                            if (statusCode == -1) {
                                updateDialog.dismiss();
                                errorTV.setVisibility(View.VISIBLE);
                                errorTV.setText("Wrong Phone Number/Password!");
                                errorTV.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        errorTV.setVisibility(View.GONE);
                                    }
                                }, 5000);

                            } else if (statusCode == 1) {
                                try {
                                    String jwtToken = resultMap.get("accessToken").toString();
                                    String refreshToken = resultMap.get("refreshToken").toString();
                                    String name = resultMap.get("name").toString();
                                    Object profilePicObject = resultMap.get("imageUrl");
                                    String profilePic = null;
                                    if (profilePicObject != null) {
                                        profilePic = resultMap.get("imageUrl").toString();
                                    }
                                    sharedPreference.createUserLoginSession(name,
                                            phone, profilePic, jwtToken, refreshToken, null, null);
//                                        getAddress(jwtToken);
                                    Intent intent = new Intent(getApplicationContext()
                                            , DashboardActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                } catch (Exception e) {
                                    updateDialog.dismiss();
                                    errorTV.setVisibility(View.VISIBLE);
                                    errorTV.setText("Extraction Error");
                                    errorTV.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            errorTV.setVisibility(View.GONE);
                                        }
                                    }, 5000);
                                }

                            } else {
                                updateDialog.dismiss();
                                errorTV.setVisibility(View.VISIBLE);
                                errorTV.setText("Login Failed!");
                                errorTV.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        errorTV.setVisibility(View.GONE);
                                    }
                                }, 5000);
                            }
                        } else {
                            updateDialog.dismiss();
                            errorTV.setVisibility(View.VISIBLE);
                            errorTV.setText("Login Failed!");
                            errorTV.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    errorTV.setVisibility(View.GONE);
                                }
                            }, 5000);
                        }
                    }

                    @Override
                    public void onFailure(Call<HashMap<Object, Object>> call, Throwable t) {
                        updateDialog.dismiss();
                        errorTV.setVisibility(View.VISIBLE);
                        errorTV.setText("Login Failed! Try Again!");
                        errorTV.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                errorTV.setVisibility(View.GONE);
                            }
                        }, 5000);
                    }
                });

            } else {
                errorTV.setVisibility(View.VISIBLE);
                errorTV.setText("Fill the Required Fields!");
                errorTV.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        errorTV.setVisibility(View.GONE);
                    }
                }, 3000);
            }
        });
    }


    public void getAddress(String jwtToken){
        ApiClient apiClient = new ApiClient(getApplicationContext());
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<HashMap<Object,Object>> call = apiService.getMySavedAddress("Bearer "+jwtToken);
        call.enqueue(new Callback<HashMap<Object, Object>>() {
            @Override
            public void onResponse(Call<HashMap<Object, Object>> call, Response<HashMap<Object, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    HashMap<Object, Object> resultMap = response.body();
                    int statusCode = (int) (double) resultMap.get("statusCode");
                    if(statusCode == 1){
                        if(resultMap.get("message") != null) {
                            String addresses = resultMap.get("message").toString();
//                            sharedPreference.insertMyAddress(addresses);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<HashMap<Object, Object>> call, Throwable t) {

            }
        });
    }

    private void showDialog(String text) {
        ImageView dialog_image;
        TextView dialog_text;
        updateDialog.setContentView(R.layout.loading_popup);
        updateDialog.setCanceledOnTouchOutside(false);
        updateDialog.setCancelable(false);
        dialog_image = updateDialog.findViewById(R.id.loading_image);
        dialog_text = updateDialog.findViewById(R.id.loading_text);
        Glide.with(this).load(R.drawable.loader).into(dialog_image);
        dialog_text.setText(text);
        updateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        updateDialog.show();
    }
}
