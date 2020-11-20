package com.azavyalov.nytimes.service;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class NewsUpdateWork extends Worker {

    public NewsUpdateWork(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d("NewsUpdateWork", "onWork");
        NewsUpdateService.start(getApplicationContext());
        return Result.success();
    }
}
