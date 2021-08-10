package com.ata.gogreenowner.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ata.gogreenowner.Activity.BaseActivity;
import com.ata.gogreenowner.Utility.NetworkUtil;

public class NetworkChangeReceiver extends BroadcastReceiver {
    Context mContext;
    private NetworkListener networkListener;

    public NetworkChangeReceiver(NetworkListener networkListener) {
        this.networkListener = networkListener;
    }

    public NetworkChangeReceiver() {
        this.networkListener = null;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        int status = NetworkUtil.getConnectivityStatus(context);
        if (status == NetworkUtil.TYPE_NOT_CONNECTED) {
            BaseActivity.showSnackbar();
            if (networkListener != null) {
                networkListener.onNetworkUnavailable();
            }
        } else {
            BaseActivity.dismissSnackbar();
            if (networkListener != null) {
                networkListener.onNetworkAvailable();
            }
        }
    }

    public interface NetworkListener {
        void onNetworkAvailable();

        void onNetworkUnavailable();
    }
}

