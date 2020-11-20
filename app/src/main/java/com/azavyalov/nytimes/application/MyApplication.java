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

    @Override
    public void onCreate() {
        Log.d("MyApplication", "onCreate");
        super.onCreate();

        /*Constraints constraints = new Constraints.Builder()
                .setRequiresCharging(true)
                .build();*/

        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(NewsUpdateWork.class,
                15, TimeUnit.MINUTES)
                //.setConstraints(constraints)
                .build();

        WorkManager.getInstance().enqueueUniquePeriodicWork("uniqueWorkName",
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest);
    }
}
