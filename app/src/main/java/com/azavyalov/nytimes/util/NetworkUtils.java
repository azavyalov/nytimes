package com.azavyalov.nytimes.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.azavyalov.nytimes.application.MyApplication;

import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;

public class NetworkUtils {

    public static NetworkUtils sNetworkUtils = new NetworkUtils();
    private NetworkReceiver mNetworkReceiver = new NetworkReceiver();
    private Subject<Boolean> mNetworkState = BehaviorSubject.createDefault(isNetworkAvailable());

    public NetworkReceiver getNetworkReceiver() {
        return mNetworkReceiver;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) MyApplication
                .getContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        }
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public class NetworkReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            mNetworkState.onNext(isNetworkAvailable());
        }
    }
}
