package com.ata.gogreenowner.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.ata.gogreenowner.Adapter.ApiClient;
import com.ata.gogreenowner.Adapter.ApiInterface;
import com.ata.gogreenowner.R;
import com.ata.gogreenowner.Utility.SharedPreference;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegsiterPickupAgentActivity extends BaseActivity implements TextWatcher {

    private ScrollView registerFormLayout;
    private TextInputLayout agentNameLayout;
    private TextInputEditText agentNameText;
    private TextInputLayout agentPhoneLayout;
    private TextInputEditText agentPhoneText;
    private TextInputLayout agentAadharLayout;
    private TextInputEditText agentAadharText;
    private TextView errorTV;
    private TextView uploadPhotoText;
    private TextView uploadAadharText;
    private AlertDialog.Builder builder;
    private ImageView uploadPhotoResImage;
    private ImageView uploadAadharResImage;
    private AppCompatButton registerAgentSubmitBtn;

    private LinearLayout otpLayout;
    private EditText editText_one;
    private EditText editText_two;
    private EditText editText_three;
    private EditText editText_four;
    private EditText editText_five;
    private EditText editText_six;
    private AppCompatButton resendOtpButton;
    private TextView timer;

    private static final int REQUEST_CAMERA = 118;
    private static final int REQUEST_EXTERNAL_STORAGE = 115;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private Uri photoUri;
    private Bitmap currentImage;
    private Uri idUri;
    private Bitmap currentIdImage;
    private File currentImageFile;
    private File idImageFile;
    private SharedPreference sharedPreference;
    private Dialog updateDialog;
    private Snackbar customSnackbar;
    private String phoneNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regsiter_pickup_agent);

        registerFormLayout = findViewById(R.id.register_form_layout);
        agentNameLayout = findViewById(R.id.name_input_layout);
        agentNameText = findViewById(R.id.name_edit);
        agentPhoneLayout = findViewById(R.id.phone_input_layout);
        agentPhoneText = findViewById(R.id.phone_edit);
        agentAadharLayout = findViewById(R.id.aadhar_input_layout);
        agentAadharText = findViewById(R.id.aadhar_edit);
        errorTV = findViewById(R.id.error_tv);
        uploadPhotoText = findViewById(R.id.uploadPhotoText);
        uploadAadharText = findViewById(R.id.uploadAadharText);
        uploadPhotoResImage = findViewById(R.id.uploadPhotoResImage);
        uploadAadharResImage = findViewById(R.id.uploadAadharResImage);
        registerAgentSubmitBtn = findViewById(R.id.register_agent_submit_btn);
        sharedPreference = new SharedPreference(this);

        otpLayout = findViewById(R.id.otpVerifyLayout);
        editText_one = findViewById(R.id.editTextOne);
        editText_two = findViewById(R.id.editTextTwo);
        editText_three = findViewById(R.id.editTextThree);
        editText_four = findViewById(R.id.editTextFour);
        editText_five = findViewById(R.id.editTextFive);
        editText_six = findViewById(R.id.editTextSix);
        resendOtpButton = findViewById(R.id.resendOtpButton);
        timer = findViewById(R.id.timer);
        updateDialog = new Dialog(this);

        editText_one.addTextChangedListener(this);
        editText_two.addTextChangedListener(this);
        editText_three.addTextChangedListener(this);
        editText_four.addTextChangedListener(this);
        editText_five.addTextChangedListener(this);
        editText_six.addTextChangedListener(this);

        registerFormLayout.setVisibility(View.VISIBLE);
        otpLayout.setVisibility(View.GONE);


        uploadPhotoText.setOnClickListener( v->{
            selectImage("photo");
        });

        uploadAadharText.setOnClickListener(v->{
            selectImage("id");
        });

        registerAgentSubmitBtn.setOnClickListener( v->{
            registerTheAgent();
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

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

    private void verifyOtp() {
        String otpValue = editText_one.getText().toString() + "" + editText_two.getText().toString() + ""
                + editText_three.getText().toString() + "" + editText_four.getText().toString()
                + "" + editText_five.getText().toString() + "" + editText_six.getText().toString();
        if (otpValue.length() == 6) {
            showDialog("Validating OTP!");
            ApiClient apiClient = new ApiClient(getApplicationContext());
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            String jwtToken = "Bearer " + sharedPreference.getJwtToken();
            Call<HashMap<Object,Object>> call = apiService.verifyPickupBoy(jwtToken,
                    otpValue,phoneNumber);
            call.enqueue(new Callback<HashMap<Object, Object>>() {
                @Override
                public void onResponse(Call<HashMap<Object, Object>> call, Response<HashMap<Object, Object>> response) {
                    Log.d("Ayush",response.toString());
                    if (response.isSuccessful() && response.body() != null) {
                        HashMap<Object,Object> resultMap = response.body();
                        int statusCode = (int)(double)resultMap.get("statusCode");
                        if(statusCode == 1){
                            updateDialog.dismiss();
                            showSuccessDialog();
                            sharedPreference.removeMyPickupBoy();
                            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    //Do something here
                                    Intent intent = new Intent(getApplicationContext(),PickupAgentActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }, 5000);
                        }else if(statusCode == -1 || statusCode == -3){
                            updateDialog.dismiss();
                            otpLayout.setVisibility(View.VISIBLE);
                            errorTV.setVisibility(View.VISIBLE);
                            errorTV.setText("OTP Incorrect/Expired!");
                            errorTV.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    errorTV.setVisibility(View.INVISIBLE);
                                }
                            }, 3000);
                        }else{
                            updateDialog.dismiss();
                            otpLayout.setVisibility(View.VISIBLE);
                            errorTV.setVisibility(View.VISIBLE);
                            errorTV.setText("Server Issue");
                            errorTV.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    errorTV.setVisibility(View.INVISIBLE);
                                }
                            }, 3000);
                        }
                    }else{
                        updateDialog.dismiss();
                        otpLayout.setVisibility(View.VISIBLE);
                        errorTV.setVisibility(View.VISIBLE);
                        errorTV.setText("Network/Server Issue!");
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
                    showSnackbar(otpLayout, "OTP Verification Failed!");
                }
            });
        }
    }


    private void selectImage(String type) {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };
       builder = new AlertDialog.Builder(RegsiterPickupAgentActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo"))
                {
                    if(getCameraPermission()) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if(type.equalsIgnoreCase("photo")){
                            startActivityForResult(intent, 1);
                        }else{
                            startActivityForResult(intent, 3);
                        }
                    }else{
                        askCameraPermission();
                    }
                }
                else if (options[item].equals("Choose from Gallery"))
                {
                    if(getStoragePermission()) {
                        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                        photoPickerIntent.setType("image/*");
                        if(type.equalsIgnoreCase("photo")) {
                            startActivityForResult(photoPickerIntent, 2);
                        }else{
                            startActivityForResult(photoPickerIntent, 4);
                        }
                    }else{
                        askStoragePermission();
                    }
                }
                else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                Bundle extras = data.getExtras();
                currentImage = (Bitmap) extras.get("data");
                Glide.with(this).load(currentImage)
                        .apply(RequestOptions.circleCropTransform())
                        .optionalCircleCrop().into(uploadPhotoResImage);
                uploadPhotoResImage.setVisibility(View.VISIBLE);
                currentImageFile = getFileFromBitmap(currentImage);
            }else if(requestCode == 2){
                photoUri = data.getData();
                if (photoUri != null) {
                    try {
                        currentImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
                        Glide.with(this).load(currentImage)
                                .apply(RequestOptions.circleCropTransform())
                                .optionalCircleCrop().into(uploadPhotoResImage);
                        uploadPhotoResImage.setVisibility(View.VISIBLE);
                        currentImageFile = getFileFromBitmap(currentImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }else if(requestCode == 3){
                Bundle extras = data.getExtras();
                currentIdImage = (Bitmap) extras.get("data");
                uploadAadharResImage.setImageBitmap(currentIdImage);
                uploadAadharResImage.setVisibility(View.VISIBLE);
                idImageFile = getFileFromBitmap(currentIdImage);
            }else if(requestCode == 4){
                idUri = data.getData();
                if (idUri != null) {
                    try {
                        currentIdImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), idUri);
                        uploadAadharResImage.setImageBitmap(currentIdImage);
                        uploadAadharResImage.setVisibility(View.VISIBLE);
                        idImageFile = getFileFromBitmap(currentIdImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private boolean getCameraPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        return false;
    }

    private void askCameraPermission() {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.CAMERA},
                    118
            );
        }
    }

    private boolean getStoragePermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        return false;
    }

    private void askStoragePermission() {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
    
    public File getFileFromBitmap(Bitmap bitmap){
        try {
            File file = new File(getApplicationContext().getCacheDir(), System.currentTimeMillis() + ".png");
            file.createNewFile();

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 60 /*ignored for PNG*/, bos);
            byte[] bitmapdata = bos.toByteArray();

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
            return file;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private void registerTheAgent() {
        MultipartBody.Part profilePic = null;
        MultipartBody.Part idPic = null;
        String agentName = agentNameText.getText().toString();
        String agentPhone = "91"+agentPhoneText.getText().toString();
        String agentAadhar = agentAadharText.getText().toString();
        if(isValidForm(agentName,agentPhone,agentAadhar)){
            phoneNumber = agentPhone;
            showDialog("Registering Pickup Agent!");
            RequestBody name = RequestBody.create(MediaType.parse("multipart/form-data"),
                    agentName);
            RequestBody phone = RequestBody.create(MediaType.parse("multipart/form-data"),
                    agentPhone);
            RequestBody aadhar = RequestBody.create(MediaType.parse("multipart/form-data"),
                    agentAadhar);

            RequestBody profilePicRequestFile =
                    RequestBody.create(MediaType.parse("multipart/form-data"), currentImageFile);
            profilePic = MultipartBody.Part.createFormData("profilePic",
                    currentImageFile.getName(), profilePicRequestFile);

            RequestBody idPicRequestFile =
                    RequestBody.create(MediaType.parse("multipart/form-data"), idImageFile);
            idPic = MultipartBody.Part.createFormData("idPic",
                    idImageFile.getName(), idPicRequestFile);

            ApiClient apiClient = new ApiClient(getApplicationContext());
            ApiInterface apiService = apiClient.getClient().create(ApiInterface.class);
            String jwtToken = "Bearer " + sharedPreference.getJwtToken();
            Call<HashMap<Object, Object>> call = apiService.registerPickupBoy(
                    jwtToken,profilePic,idPic,name,phone,aadhar);
            call.enqueue(new Callback<HashMap<Object, Object>>() {
                @Override
                public void onResponse(Call<HashMap<Object, Object>> call, Response<HashMap<Object, Object>> response) {
                    Log.d("Ayush",response.toString());
                    if (response.isSuccessful() && response.body() != null) {
                        HashMap<Object,Object> resultMap = response.body();
                        int statusCode = (int)(double)resultMap.get("statusCode");
                        if(statusCode == 1){
                            updateDialog.dismiss();
                            registerFormLayout.setVisibility(View.GONE);
                            otpLayout.setVisibility(View.VISIBLE);
                        }else if(statusCode == -1){
                            updateDialog.dismiss();
                            errorTV.setVisibility(View.VISIBLE);
                            errorTV.setText("Phone number already in use!");
                            errorTV.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    errorTV.setVisibility(View.INVISIBLE);
                                }
                            }, 3000);
                        } else{
                            updateDialog.dismiss();
                            errorTV.setVisibility(View.VISIBLE);
                            errorTV.setText("Try Again!");
                            errorTV.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    errorTV.setVisibility(View.INVISIBLE);
                                }
                            }, 3000);
                        }
                    }else{
                        updateDialog.dismiss();
                        errorTV.setVisibility(View.VISIBLE);
                        errorTV.setText("Try Again!");
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
                    errorTV.setVisibility(View.VISIBLE);
                    errorTV.setText("Try Again!");
                    errorTV.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            errorTV.setVisibility(View.INVISIBLE);
                        }
                    }, 3000);
                }
            });
        }
    }

    private boolean isValidForm(String name, String phone, String aadhar){
        Pattern pattern = Pattern.compile("[0-9]{12}");
        Matcher m = pattern.matcher(phone);
        String aadharRegex = "\\d{12}";
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(aadhar)) {
            errorTV.setVisibility(View.VISIBLE);
            errorTV.setText("Fill all the required Fields");
            errorTV.postDelayed(new Runnable() {
                @Override
                public void run() {
                    errorTV.setVisibility(View.GONE);
                }
            }, 3000);
            return false;
        }

        if(currentImage == null){
            errorTV.setVisibility(View.VISIBLE);
            errorTV.setText("Upload Profile Photo");
            errorTV.postDelayed(new Runnable() {
                @Override
                public void run() {
                    errorTV.setVisibility(View.GONE);
                }
            }, 3000);
            return false;
        }

        if(currentIdImage == null){
            errorTV.setVisibility(View.VISIBLE);
            errorTV.setText("Upload Aadhar Card");
            errorTV.postDelayed(new Runnable() {
                @Override
                public void run() {
                    errorTV.setVisibility(View.GONE);
                }
            }, 3000);
            return false;
        }

        if (!m.find() || !m.group().equals(phone)) {
            agentPhoneLayout.setError("Enter a valid Phone Number");
            agentPhoneLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    agentPhoneLayout.setError(null);
                }
            }, 3000);
            return false;
        }

        if (!aadhar.matches(aadharRegex)) {
            agentAadharLayout.setError("Enter a valid Aadhar Number");
            agentAadharLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    agentAadharLayout.setError(null);
                }
            }, 3000);
            return false;
        }

        return true;
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

    private void showSnackbar(View view ,String text){
        customSnackbar = Snackbar.make(view, text, Snackbar.LENGTH_LONG);
        View snackBarView = customSnackbar.getView();
        snackBarView.setBackgroundColor(getResources().getColor(R.color.svSelectedColor));
        TextView textView = (TextView) snackBarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(getResources().getColor(R.color.svCancelColor));
        customSnackbar.show();
    }

    private void showSuccessDialog(){
        ImageView dialog_image;
        TextView dialog_text;
        updateDialog.setContentView(R.layout.loading_popup);
        updateDialog.setCanceledOnTouchOutside(false);
        updateDialog.setCancelable(false);
        dialog_image = updateDialog.findViewById(R.id.loading_image);
        dialog_text = updateDialog.findViewById(R.id.loading_text);
        Glide.with(this).load(R.drawable.ic_baseline_check_circle_outline_24).into(dialog_image);
        dialog_text.setText("Pickup Boy Registration Successful \n Redirecting in 5 seconds");
        updateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        updateDialog.show();
    }
}