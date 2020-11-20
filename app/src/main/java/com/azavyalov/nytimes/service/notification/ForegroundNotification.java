package com.azavyalov.nytimes.service.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.azavyalov.nytimes.R;
import com.azavyalov.nytimes.service.StopNewsUpdateService;

import static com.azavyalov.nytimes.util.VersionUtils.atLeastOreo;

public class ForegroundNotification {

    private static final String CHANNEL_ID = "foreground:news:notification";
    private Context mContext;

    public ForegroundNotification(Context mContext) {
        this.mContext = mContext;
    }

    // Build a notification which shows with news updating
    public Notification create() {
        createNotificationChannel();
        return new NotificationCompat.Builder(mContext, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_update)
                .setContentTitle("NYTimes updating")
                .setContentText("Getting news from NYTimes API")
                .addAction(cancelNewsUpdate())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();
    }

    // Create a notification channel for API 26+. In older API's channels are not supporting
    private void createNotificationChannel() {
        if (atLeastOreo()) {
            CharSequence name = mContext.getString(R.string.channel_name);
            String description = mContext.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = mContext.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    // Create a cancel button in notification which calls stopService on a NewsUpdateService
    private NotificationCompat.Action cancelNewsUpdate() {
        Intent stopSelf = new Intent(mContext, StopNewsUpdateService.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, (int) System.currentTimeMillis(),
                stopSelf, PendingIntent.FLAG_CANCEL_CURRENT);
        return new NotificationCompat.Action(R.drawable.ic_cancel, "Cancel", pendingIntent);
    }
}
