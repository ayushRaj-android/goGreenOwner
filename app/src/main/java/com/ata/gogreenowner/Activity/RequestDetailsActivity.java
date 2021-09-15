package com.ata.gogreenowner.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.ata.gogreenowner.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.jetbrains.annotations.NotNull;

public class RequestDetailsActivity extends BaseActivity {

    private static final int PHONE_REQUEST_CODE = 135;
    BottomSheetBehavior<LinearLayout> sheetBehavior;
    ImageView greenboxImage;
    LinearLayout pickupBoyDetailsLayout;
    TextView pickupBoyNameView;
    ImageView pickupBoyImageView;
    RatingBar pickupBoyRating;
    ImageButton pickupBoyCallButton;
    TextView statusText;
    TextView pickupLocationText;
    TextView requestPlacementDateText;
    TextView pickupTimeText;
    TextView pickupDateTimeText;
    TextView serviceCompletionText;
    TextView serviceCompletionDateTimeText;
    TextView paymentSummaryText;
    RelativeLayout paymentSummaryLayout;
    TextView amountGivenText;
    RelativeLayout weightSummaryLayout;
    TextView weightReceivedText;

    int status;
    String pickupLocation, locality;
    String reqPlacementDate, serviceCompletionDate;
    String pickupTime;
    double latitude, longitude;
    float pickupBoyRatingValue;
    String pickupBoyName, pickupBoyPicUrl, pickUpBoyPhone, weight, amount;
    private GoogleMap requestDetailsMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_details);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            status = bundle.getInt("status");
            pickupLocation = bundle.getString("pickupLocation");
            locality = bundle.getString("locality");
            reqPlacementDate = bundle.getString("reqPlacementDate");
            pickupTime = bundle.getString("pickupTime");
            latitude = Double.parseDouble(bundle.getString("latitude"));
            longitude = Double.parseDouble(bundle.getString("longitude"));
            if (status > 0) {
                pickupBoyName = bundle.getString("pickupBoyName");
                pickUpBoyPhone = bundle.getString("pickupBoyPhone");
                pickupBoyPicUrl = bundle.getString("pickupBoyPicUrl");
                pickupBoyRatingValue = Float.parseFloat(bundle.getString("pickupBoyRating"));
            }
            if (status >= 3) {
                weight = bundle.getString("weight");
                amount = bundle.getString("amount");
            }
            if (status == 5) {
                serviceCompletionDate = bundle.getString("serviceCompletionDate");
            }

        }

        if (requestDetailsMap == null) {
            SupportMapFragment addAddressMapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.req_details_map);
            addAddressMapFragment.getMapAsync(requestDetailsOnMapReadyCallback());
        }

        LinearLayout bottomSheetLL = (LinearLayout) findViewById(R.id.request_bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(bottomSheetLL);

        greenboxImage = bottomSheetLL.findViewById(R.id.greenbox_image);
        pickupBoyDetailsLayout = bottomSheetLL.findViewById(R.id.pickup_boy_details_layout);
        pickupBoyImageView = bottomSheetLL.findViewById(R.id.pickup_boy_pic_view);
        pickupBoyNameView = bottomSheetLL.findViewById(R.id.pickup_boy_name_view);
        pickupBoyRating = bottomSheetLL.findViewById(R.id.pickup_boy_rating);
        pickupBoyCallButton = bottomSheetLL.findViewById(R.id.pickup_boy_call_btn);
        statusText = bottomSheetLL.findViewById(R.id.status_text);
        pickupLocationText = bottomSheetLL.findViewById(R.id.pickup_location_text);
        requestPlacementDateText = bottomSheetLL.findViewById(R.id.request_placement_date_text);
        pickupTimeText = bottomSheetLL.findViewById(R.id.pickup_time_text);
        pickupDateTimeText = bottomSheetLL.findViewById(R.id.pickup_date_time_text);
        serviceCompletionText = bottomSheetLL.findViewById(R.id.service_completion_text);
        serviceCompletionDateTimeText = bottomSheetLL.findViewById(R.id.service_completion_date_time_text);
        paymentSummaryText = bottomSheetLL.findViewById(R.id.payment_summary_text);
        paymentSummaryLayout = bottomSheetLL.findViewById(R.id.payment_summary_layout);
        amountGivenText = bottomSheetLL.findViewById(R.id.amount_recieved_tv);
        weightSummaryLayout = bottomSheetLL.findViewById(R.id.weight_summary_layout);
        weightReceivedText = bottomSheetLL.findViewById(R.id.weight_recieved_tv);


        requestPlacementDateText.setText(reqPlacementDate);
        pickupDateTimeText.setText(pickupTime);
        pickupLocationText.setText(pickupLocation + ", " + locality);

        pickupBoyCallButton.setOnClickListener(v -> {
            makeCall(pickUpBoyPhone);
        });
        if (status == 0) {
            greenboxImage.setVisibility(View.GONE);
            pickupBoyDetailsLayout.setVisibility(View.GONE);
            statusText.setText("Waiting for Go Green Agent to be allocated!");
            serviceCompletionText.setVisibility(View.GONE);
            serviceCompletionDateTimeText.setVisibility(View.GONE);
            paymentSummaryText.setVisibility(View.GONE);
            paymentSummaryLayout.setVisibility(View.GONE);
            weightSummaryLayout.setVisibility(View.GONE);
        } else if (status == -1) {
            greenboxImage.setVisibility(View.GONE);
            pickupBoyDetailsLayout.setVisibility(View.GONE);
            statusText.setText("Cancelled!");
            statusText.setTextColor(Color.RED);
            serviceCompletionText.setVisibility(View.GONE);
            serviceCompletionDateTimeText.setVisibility(View.GONE);
            paymentSummaryText.setVisibility(View.GONE);
            paymentSummaryLayout.setVisibility(View.GONE);
            weightSummaryLayout.setVisibility(View.GONE);
            pickupTimeText.setText("Cancellation Time");
            pickupDateTimeText.setTextColor(Color.RED);
        } else if (status == 1) {
            greenboxImage.setVisibility(View.VISIBLE);
            Glide.with(this).load(pickupBoyPicUrl)
                    .apply(RequestOptions.circleCropTransform())
                    .optionalCircleCrop().into(pickupBoyImageView);
            pickupBoyNameView.setText(pickupBoyName);
            pickupBoyRating.setRating(pickupBoyRatingValue);
            statusText.setText("Go Green Agent allocated!");
            serviceCompletionText.setVisibility(View.GONE);
            serviceCompletionDateTimeText.setVisibility(View.GONE);
            paymentSummaryText.setVisibility(View.GONE);
            paymentSummaryLayout.setVisibility(View.GONE);
            weightSummaryLayout.setVisibility(View.GONE);
        } else if (status == 3) {
            greenboxImage.setVisibility(View.VISIBLE);
            Glide.with(this).load(pickupBoyPicUrl)
                    .apply(RequestOptions.circleCropTransform())
                    .optionalCircleCrop().into(pickupBoyImageView);
            pickupBoyNameView.setText(pickupBoyName);
            pickupBoyRating.setRating(pickupBoyRatingValue);
            statusText.setText("Service marked completed by Go Green Agent!");
            serviceCompletionText.setVisibility(View.GONE);
            serviceCompletionDateTimeText.setVisibility(View.GONE);
            paymentSummaryText.setVisibility(View.GONE);
            paymentSummaryLayout.setVisibility(View.GONE);
            weightSummaryLayout.setVisibility(View.GONE);
            pickupBoyCallButton.setVisibility(View.GONE);
        } else if (status == 5) {
            greenboxImage.setVisibility(View.VISIBLE);
            Glide.with(this).load(pickupBoyPicUrl)
                    .apply(RequestOptions.circleCropTransform())
                    .optionalCircleCrop().into(pickupBoyImageView);
            pickupBoyNameView.setText(pickupBoyName);
            pickupBoyRating.setRating(pickupBoyRatingValue);
            statusText.setText("Service Completed! #Go_Green");
            serviceCompletionDateTimeText.setText(serviceCompletionDate);
            amountGivenText.setText("\u20B9 " + amount);
            weightReceivedText.setText(weight + " kg");
            pickupBoyCallButton.setVisibility(View.GONE);
        }
    }

    @SuppressLint("MissingPermission")
    public OnMapReadyCallback requestDetailsOnMapReadyCallback() {
        return new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                requestDetailsMap = googleMap;
                LatLng requestLocation = new LatLng(latitude
                        , longitude);
                requestDetailsMap.addMarker(new MarkerOptions()
                        .position(requestLocation).title(pickupLocation).icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_baseline_location_city_24))).showInfoWindow();
                requestDetailsMap.animateCamera(CameraUpdateFactory.newLatLngZoom(requestLocation, 16));
            }
        };
    }

    private BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void makeCall(String phoneNumber) {
        if (getCallingPermission()) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(intent);
        } else {
            askCallingPermission();
        }
    }


    private boolean getCallingPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void askCallingPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CALL_PHONE}, PHONE_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        if (requestCode == PHONE_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //permission granted
                makeCall(pickUpBoyPhone);
            } else {
                //permission not granted
                askCallingPermission();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}