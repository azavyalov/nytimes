package com.azavyalov.nytimes.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StopNewsUpdateService extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, NewsUpdateService.class);
        context.stopService(service);
    }
}
