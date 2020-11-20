package com.azavyalov.nytimes.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class StopNewsUpdateService extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("StopNewsUpdateService", "Stop news updating service");
        Intent service = new Intent(context, NewsUpdateService.class);
        context.stopService(service);
    }
}
