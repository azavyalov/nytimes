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

import static com.azavyalov.nytimes.util.VersionUtils.atLeastOreo;

public class ResultNotification {

    private static final String CHANNEL_ID_RESULT = "news:notification:channel:pops";
    private static final int NOTIFICATION_ID = 2;
    private Context mContext;

    public ResultNotification(Context mContext) {
        this.mContext = mContext;
    }

    public void show(String message) {

        Notification notification = new NotificationCompat.Builder(mContext, CHANNEL_ID_RESULT)
                .setSmallIcon(R.drawable.ic_done)
                .setContentTitle("News updating")
                .setContentText("News has been successfully updated")
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContentIntent(openMainActivity(mContext))
                .setTicker(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();

        if (atLeastOreo()) {
            CharSequence name = mContext.getString(R.string.channel_name);
            String description = mContext.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID_RESULT, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = mContext.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            notificationManager.notify(NOTIFICATION_ID, notification);
        }
    }

    public static PendingIntent openMainActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        return pendingIntent;
    }
}
