package com.ata.gogreenowner.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ata.gogreenowner.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class RegsiterPickupAgentActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regsiter_pickup_agent);

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
            bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
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
        MultipartBody.Part idImage = null;
        String agentName = agentNameText.getText().toString();
        String agentPhone = "91"+agentPhoneText.getText().toString();
        String agentAadhar = agentAadharText.getText().toString();
        if(isValidForm(agentName,agentPhone,agentAadhar)){
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
            profilePic = MultipartBody.Part.createFormData("idPic",
                    idImageFile.getName(), idPicRequestFile);
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
}