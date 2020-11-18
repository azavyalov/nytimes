package com.azavyalov.nytimes.service.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.azavyalov.nytimes.R;
import com.azavyalov.nytimes.service.CancelNewsUpdateService;

import static android.app.NotificationManager.IMPORTANCE_DEFAULT;
import static com.azavyalov.nytimes.util.VersionUtils.atLeastOreo;

public class ForegroundNotificationBuilder {

    private static final String CHANNEL_ID_FOREGROUND = "foreground:notification:channel";
    private Context mContext;

    public ForegroundNotificationBuilder(Context context) {
        this.mContext = context;
    }

    // Build a notification which shows with news updating
    public Notification build() {
        createForegroundNotificationChannel();
        return new NotificationCompat.Builder(mContext, CHANNEL_ID_FOREGROUND)
                .setSmallIcon(R.drawable.ic_update)
                .setContentTitle("NYTimes")
                .setContentText("Updating news")
                .setTimeoutAfter(3000) // just for debug
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .addAction(createCancelUpdateAction())
                .build();
    }

    // Create a notification channel for API 26+. In older API's channels are not supporting
    private void createForegroundNotificationChannel() {
        if (atLeastOreo()) {
            CharSequence name = mContext.getString(R.string.channel_name);
            String description = mContext.getString(R.string.channel_description);
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID_FOREGROUND, name, IMPORTANCE_DEFAULT);
            channel.setDescription(description);
            NotificationManager notificationManager = mContext.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    // Create a cancel button in notification which calls stopService on a NewsUpdateService
    private NotificationCompat.Action createCancelUpdateAction() {
        Intent intent = new Intent(mContext, CancelNewsUpdateService.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                mContext,
                (int) System.currentTimeMillis(),
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        return new NotificationCompat.Action(R.drawable.ic_cancel, "Cancel", pendingIntent);
    }
}
