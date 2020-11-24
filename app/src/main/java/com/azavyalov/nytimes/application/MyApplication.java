package com.azavyalov.nytimes.application;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.util.Log;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.azavyalov.nytimes.service.NewsUpdateWork;
import com.azavyalov.nytimes.util.NetworkUtils;

import java.util.concurrent.TimeUnit;

public class MyApplication extends Application {

    private static MyApplication sMyApplication;

    private final static String LOG_TAG = "MyApplicationTag";
    private final static long REPEAT_INTERVAL = 180;
    private final static long INITIAL_DELAY = 30;
    // Debug
    /*private final static long REPEAT_INTERVAL = 15;
    private final static long INITIAL_DELAY = 1;*/

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(LOG_TAG, "onCreate");

        sMyApplication = this;
        registerNetworkReceiver();
        enqueuePeriodicUpdatingWork();
    }

    public static Context getContext() {
        return sMyApplication;
    }

    private void registerNetworkReceiver() {
        registerReceiver(NetworkUtils.sNetworkUtils.getNetworkReceiver(),
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private void enqueuePeriodicUpdatingWork() {
        Constraints constraints = new Constraints.Builder()
                .setRequiresCharging(true)
                .build();

        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(
                NewsUpdateWork.class, REPEAT_INTERVAL, TimeUnit.MINUTES)
                .setInitialDelay(INITIAL_DELAY, TimeUnit.MINUTES)
                //.setConstraints(constraints)
                .build();

        Log.d(LOG_TAG, "Executing news updating work");
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "NewsUpdatingWork",
                ExistingPeriodicWorkPolicy.REPLACE,
                workRequest);
    }
}
