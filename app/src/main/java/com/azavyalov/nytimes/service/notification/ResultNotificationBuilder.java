package com.azavyalov.nytimes.service.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.azavyalov.nytimes.R;
import com.azavyalov.nytimes.ui.MainActivity;

import static android.app.Notification.DEFAULT_ALL;
import static android.app.NotificationManager.IMPORTANCE_DEFAULT;
import static com.azavyalov.nytimes.util.VersionUtils.atLeastOreo;

public class ResultNotificationBuilder {

    private static final String CHANNEL_ID_RESULT = "result:notification:channel";

    private Context mContext;

    public ResultNotificationBuilder(Context mContext) {
        this.mContext = mContext;
    }

    public Notification show(String message) {
        createResultNotificationChannel();
        return new NotificationCompat.Builder(mContext, CHANNEL_ID_RESULT)
                .setSmallIcon(R.drawable.ic_done)
                .setContentTitle("NYTimes")
                .setDefaults(DEFAULT_ALL)
                .setAutoCancel(true)
                .setContentIntent(createPendingIntent())
                .setTicker(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();
    }

    private void createResultNotificationChannel() {
        if (atLeastOreo()) {
            CharSequence name = mContext.getString(R.string.channel_name);
            String description = mContext.getString(R.string.channel_description);
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID_RESULT, name, IMPORTANCE_DEFAULT);
            channel.setDescription(description);
            NotificationManager manager = mContext.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private PendingIntent createPendingIntent() {
        Intent intent = new Intent(mContext, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return PendingIntent.getActivity(mContext, 0, intent, 0);
    }
}
