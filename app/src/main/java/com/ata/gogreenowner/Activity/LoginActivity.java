package com.ata.gogreenowner.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.ata.gogreenowner.Adapter.ApiClient;
import com.ata.gogreenowner.Adapter.ApiInterface;
import com.ata.gogreenowner.R;
import com.ata.gogreenowner.Utility.SharedPreference;
import com.google.android.material.textfield.TextInputEditText;

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
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
    }
}