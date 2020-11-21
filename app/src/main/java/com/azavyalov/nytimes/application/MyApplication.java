package com.azavyalov.nytimes.application;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.azavyalov.nytimes.service.NewsUpdateWork;

import java.util.concurrent.TimeUnit;

public class MyApplication extends Application {

    private final static long REPEAT_INTERVAL = 180;
    private final static long INITIAL_DELAY = 30;

    // Debug
    /*private final static long REPEAT_INTERVAL = 15;
    private final static long INITIAL_DELAY = 1;*/

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("MyApplication", "onCreate");

        Constraints constraints = new Constraints.Builder()
                .setRequiresCharging(true)
                .build();

        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(
                NewsUpdateWork.class, REPEAT_INTERVAL, TimeUnit.MINUTES)
                .setInitialDelay(INITIAL_DELAY, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "uniqueWorkName",
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest);
    }
}
