package com.ata.gogreenowner.Services;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.ata.gogreenowner.Activity.RequestsActivity;
import com.ata.gogreenowner.Activity.SplashActivity;
import com.ata.gogreenowner.Adapter.ApiClient;
import com.ata.gogreenowner.Adapter.ApiInterface;
import com.ata.gogreenowner.Model.JunkYardOwnerDeviceFCM;
import com.ata.gogreenowner.R;
import com.ata.gogreenowner.Utility.SharedPreference;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFirebaseInstanceIdService extends FirebaseMessagingService {

    private static final String NOTIFICATION_CHANNEL_ID = "my_notification_channel";
    private SharedPreference sharedPreference;

    @Override
    public void onNewToken(@NonNull @NotNull String s) {
        sharedPreference = new SharedPreference(this);
        if(sharedPreference.isUserLoggedIn()){
            String android_id = Settings.Secure.getString(this.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            updateFCMForUser(s,android_id);
        }else{
            FirebaseMessaging.getInstance().subscribeToTopic("generic");
        }
    }

    private void updateFCMForUser(String token, String deviceId) {
        try {
            String userPhone = sharedPreference.getUserPhone();
            JunkYardOwnerDeviceFCM  junkYardOwnerDeviceFCM = new JunkYardOwnerDeviceFCM(deviceId,token,userPhone);
            ApiClient apiClient = new ApiClient(this);
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<HashMap<Object, Object>> call = apiService.registerDevice(junkYardOwnerDeviceFCM,"junkyard owner");
            call.enqueue(new Callback<HashMap<Object, Object>>() {
                @SneakyThrows
                @Override
                public void onResponse(Call<HashMap<Object, Object>> call, Response<HashMap<Object, Object>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        HashMap<Object, Object> resultMap = response.body();
                        int statusCode = (int) (double) resultMap.get("statusCode");
                        if (statusCode == 1 && resultMap.get("message") != null) {

                        }
                    }else{
                    }
                }

                @Override
                public void onFailure(Call<HashMap<Object, Object>> call, Throwable t) {

                }
            });
        } catch (Exception e){

        }
    }

    @Override
    public void onMessageReceived(@NonNull @NotNull RemoteMessage remoteMessage) {
        String messageTitle = null;
        if(remoteMessage.getNotification() != null && remoteMessage.getNotification().getTitle() != null){
            messageTitle = remoteMessage.getNotification().getTitle();
        }else{
            messageTitle = remoteMessage.getData().get("title");
        }
        String messageBody = null;
        if(remoteMessage.getNotification() != null && remoteMessage.getNotification().getBody() != null){
            messageBody = remoteMessage.getNotification().getBody();
        }else{
            messageBody = remoteMessage.getData().get("body");
        }
        Map<String, String> hashMap = remoteMessage.getData();
        String type = hashMap.get("type");
        if (type == null) {
            createNotification(messageTitle, messageBody,0);
        }else{
            createNotification(messageTitle, messageBody,1);
        }
    }

    public void createNotification(String title, String body,int flag) {
        int id = (int) System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Uri notificationSoundURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Intent intent = null;
        if(flag == 0){
            intent = new Intent(this, SplashActivity.class);
        }else{
            intent = new Intent(this, RequestsActivity.class);
        }
        PendingIntent pendingIntent;
        if (isAppInForeground()) {
            pendingIntent = null;
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notification", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription("Emergency Description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationManager.createNotificationChannel(notificationChannel);
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                    .build();

            Notification.Builder notification = null;
            notification = new Notification.Builder(this, NOTIFICATION_CHANNEL_ID)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setSound(notificationSoundURI, audioAttributes)
                    .setVisibility(Notification.VISIBILITY_PUBLIC);
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                notification.setSmallIcon(R.drawable.ic_notification);
                notification.setColor(getResources().getColor(R.color.colorAccent));
            } else {
                notification.setSmallIcon(R.drawable.ic_notification);
            }
            notificationManager.notify(id, notification.build());
        } else {
            NotificationCompat.Builder builder = null;
            builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
                    .setPriority(Notification.PRIORITY_MAX)
                    .setSound(notificationSoundURI);
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder.setSmallIcon(R.drawable.ic_notification);
                builder.setColor(getResources().getColor(R.color.colorAccent));
            } else {
                builder.setSmallIcon(R.drawable.ic_notification);
            }
            notificationManager.notify(id, builder.build());
        }
    }

    private boolean isAppInForeground() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> services = activityManager.getRunningAppProcesses();
        boolean isActivityFound = false;

        if (services.get(0).processName
                .equalsIgnoreCase(getPackageName()) && services.get(0).importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
            isActivityFound = true;
        }

        return isActivityFound;
    }
}

