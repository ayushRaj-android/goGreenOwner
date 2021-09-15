package com.ata.gogreenowner.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ata.gogreenowner.Adapter.ApiClient;
import com.ata.gogreenowner.Adapter.ApiInterface;
import com.ata.gogreenowner.R;
import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity implements TextWatcher {

    RelativeLayout firstLayout;
    LinearLayout secondLayout;
    Button firstNextButton;
    Button resendOtpButton;
    EditText phoneNumberEditText;
    String sessionToken;
    String phoneNumber;
    EditText editText_one;
    EditText editText_two;
    EditText editText_three;
    EditText editText_four;
    EditText editText_five;
    EditText editText_six;
    TextView timer;
    TextView errorTV;
    Dialog updateDialog;
    private Snackbar customSnackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        firstLayout = findViewById(R.id.firstLayout);
        secondLayout = findViewById(R.id.secondLayout);
        firstNextButton = findViewById(R.id.nextFirstButton);
        resendOtpButton = findViewById(R.id.resendOtpButton);
        phoneNumberEditText = findViewById(R.id.phone_number_forgot_password);
        editText_one = findViewById(R.id.editTextOne);
        editText_two = findViewById(R.id.editTextTwo);
        editText_three = findViewById(R.id.editTextThree);
        editText_four = findViewById(R.id.editTextFour);
        editText_five = findViewById(R.id.editTextFive);
        editText_six = findViewById(R.id.editTextSix);
        timer = findViewById(R.id.timer);
        errorTV = findViewById(R.id.errorTV);
        updateDialog = new Dialog(this);

        editText_one.addTextChangedListener(this);
        editText_two.addTextChangedListener(this);
        editText_three.addTextChangedListener(this);
        editText_four.addTextChangedListener(this);
        editText_five.addTextChangedListener(this);
        editText_six.addTextChangedListener(this);

        firstNextButton.setEnabled(false);

        phoneNumberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (phoneNumberEditText.getText().toString().length() == 10) {
                    firstNextButton.setEnabled(true);
                }
            }
        });

        firstNextButton.setOnClickListener(v -> {
            showDialog("Never Forget to contribute fot the greener world!");
            phoneNumber = "91" + phoneNumberEditText.getText().toString();
            Pattern pattern = Pattern.compile("[0-9]{12}");
            Matcher m = pattern.matcher(phoneNumber);
            if (!TextUtils.isEmpty(phoneNumber) && m.find() && m.group().equals(phoneNumber)) {
                ApiClient apiClient = new ApiClient(getApplicationContext());
                ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
                Call<HashMap<Object, Object>> call = apiService.forgotPassword(phoneNumber);
                call.enqueue(new Callback<HashMap<Object, Object>>() {
                    @Override
                    public void onResponse(Call<HashMap<Object, Object>> call, Response<HashMap<Object, Object>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            HashMap<Object, Object> resultMap = response.body();
                            int statusCode = (int) (double) resultMap.get("statusCode");
                            if (statusCode == 1) {
                                phoneNumberEditText.setText(null);
                                sessionToken = resultMap.get("message").toString();
                                updateDialog.dismiss();
                                firstLayout.setVisibility(View.GONE);
                                secondLayout.setVisibility(View.VISIBLE);
                                startTimer();
                            } else if (statusCode == -1) {
                                updateDialog.dismiss();
                                firstLayout.setVisibility(View.VISIBLE);
                                errorTV.setVisibility(View.VISIBLE);
                                errorTV.setText("Phone number invalid! Do Sign Up");
                                errorTV.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        errorTV.setVisibility(View.INVISIBLE);
                                    }
                                }, 3000);
                            } else {
                                updateDialog.dismiss();
                                firstLayout.setVisibility(View.VISIBLE);
                                errorTV.setVisibility(View.VISIBLE);
                                errorTV.setText("Otp Generation Failed!");
                                errorTV.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        errorTV.setVisibility(View.INVISIBLE);
                                    }
                                }, 3000);
                            }
                        } else {
                            updateDialog.dismiss();
                            firstLayout.setVisibility(View.VISIBLE);
                            errorTV.setVisibility(View.VISIBLE);
                            errorTV.setText("Otp Generation Failed!");
                            errorTV.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    errorTV.setVisibility(View.INVISIBLE);
                                }
                            }, 3000);
                        }
                    }

                    @Override
                    public void onFailure(Call<HashMap<Object, Object>> call, Throwable t) {
                        updateDialog.dismiss();
                        firstLayout.setVisibility(View.VISIBLE);
                        errorTV.setVisibility(View.VISIBLE);
                        errorTV.setText("Otp Generation Failed!");
                        errorTV.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                errorTV.setVisibility(View.INVISIBLE);
                            }
                        }, 3000);
                    }
                });
            }
        });

        resendOtpButton.setOnClickListener(v -> {
            ApiClient apiClient = new ApiClient(getApplicationContext());
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<HashMap<Object, Object>> call = apiService.resendOtp(phoneNumber, "resetPassword");
            call.enqueue(new Callback<HashMap<Object, Object>>() {
                @Override
                public void onResponse(Call<HashMap<Object, Object>> call, Response<HashMap<Object, Object>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        HashMap<Object, Object> resultMap = response.body();
                        int statusCode = (int) (double) resultMap.get("statusCode");
                        if (statusCode == 1) {
                            showSnackbar(secondLayout, "Otp Sent");
                            startTimer();
                        } else {
                            showSnackbar(secondLayout, "Otp Sending Failed!");
                        }
                    } else {
                        showSnackbar(secondLayout, "Otp Sending Failed!");
                    }
                }

                @Override
                public void onFailure(Call<HashMap<Object, Object>> call, Throwable t) {
                    showSnackbar(secondLayout, "Otp Sending Failed!");
                }
            });
        });
    }


    public void verifyOtp() {
        String otpValue = editText_one.getText().toString() + "" + editText_two.getText().toString() + ""
                + editText_three.getText().toString() + "" + editText_four.getText().toString()
                + "" + editText_five.getText().toString() + "" + editText_six.getText().toString();
        if (otpValue.length() == 6) {
            showDialog("The Earth needs to know whether you really want to contribute towards greener world!");
            ApiClient apiClient = new ApiClient(getApplicationContext());
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<HashMap<Object, Object>> call = apiService.otpVerify(otpValue, phoneNumber, "resetPassword");
            call.enqueue(new Callback<HashMap<Object, Object>>() {
                @Override
                public void onResponse(Call<HashMap<Object, Object>> call, Response<HashMap<Object, Object>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        HashMap<Object, Object> resultMap = response.body();
                        int statusCode = (int) (double) resultMap.get("statusCode");
                        if (statusCode == 1) {
                            updateDialog.dismiss();
                            secondLayout.setVisibility(View.VISIBLE);
                            showSuccessDialog();
                            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    //Do something here
                                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }, 5000);
                        } else {
                            updateDialog.dismiss();
                            secondLayout.setVisibility(View.VISIBLE);
                            showSnackbar(secondLayout, "OTP Mismatch!");
                        }
                    } else {
                        updateDialog.dismiss();
                        secondLayout.setVisibility(View.VISIBLE);
                        showSnackbar(secondLayout, "Something went wrong!");
                    }
                }

                @Override
                public void onFailure(Call<HashMap<Object, Object>> call, Throwable t) {
                    updateDialog.dismiss();
                    secondLayout.setVisibility(View.VISIBLE);
                    showSnackbar(secondLayout, "Something went wrong!");
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        if (secondLayout.getVisibility() == View.VISIBLE) {
//                || thirdLayout.getVisibility() == View.VISIBLE){
            secondLayout.setVisibility(View.GONE);
//            thirdLayout.setVisibility(View.GONE);
            firstLayout.setVisibility(View.VISIBLE);
        } else {
            startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
            finish();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.length() == 1) {
            if (editText_one.length() == 1) {
                editText_two.requestFocus();
            }
            if (editText_two.length() == 1) {
                editText_three.requestFocus();
            }
            if (editText_three.length() == 1) {
                editText_four.requestFocus();
            }
            if (editText_four.length() == 1) {
                editText_five.requestFocus();
            }
            if (editText_five.length() == 1) {
                editText_six.requestFocus();
            }
            if (editText_six.length() == 1) {
                verifyOtp();
            }

        } else if (s.length() == 0) {
            if (editText_six.length() == 0) {
                editText_five.requestFocus();
            }
            if (editText_five.length() == 0) {
                editText_four.requestFocus();
            }
            if (editText_four.length() == 0) {
                editText_three.requestFocus();
            }
            if (editText_three.length() == 0) {
                editText_two.requestFocus();
            }
            if (editText_two.length() == 0) {
                editText_one.requestFocus();
            }
        }
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

    private void showSnackbar(View view, String text) {
        customSnackbar = Snackbar.make(findViewById(android.R.id.content), text, Snackbar.LENGTH_LONG);
        View snackBarView = customSnackbar.getView();
        snackBarView.setBackgroundColor(getResources().getColor(R.color.svSelectedColor));
        TextView textView = (TextView) snackBarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(getResources().getColor(R.color.svCancelColor));
        customSnackbar.show();
    }

    public void startTimer() {
        new CountDownTimer(45000, 1000) {
            @Override
            public void onTick(long l) {
                timer.setText("0:" + l / 1000 + " s");
                resendOtpButton.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFinish() {
                timer.setText(0 + " s");
                resendOtpButton.startAnimation(AnimationUtils.loadAnimation(ForgotPasswordActivity.this, R.anim.slide_from_right));
                resendOtpButton.setVisibility(View.VISIBLE);
            }
        }.start();
    }

    private void showSuccessDialog() {
        ImageView dialog_image;
        TextView dialog_text;
        updateDialog.setContentView(R.layout.loading_popup);
        updateDialog.setCanceledOnTouchOutside(false);
        updateDialog.setCancelable(false);
        dialog_image = updateDialog.findViewById(R.id.loading_image);
        dialog_text = updateDialog.findViewById(R.id.loading_text);
        Glide.with(this).load(R.drawable.ic_baseline_check_circle_outline_24).into(dialog_image);
        dialog_text.setText("Your password has been sent to your \n registered phone number. \n Redirecting in 5 seconds");
        updateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        updateDialog.show();
    }
}