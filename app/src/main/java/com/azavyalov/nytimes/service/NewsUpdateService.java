package com.azavyalov.nytimes.service;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.azavyalov.nytimes.R;
import com.azavyalov.nytimes.network.RestApi;
import com.azavyalov.nytimes.room.ConverterDtoToDb;
import com.azavyalov.nytimes.room.NewsItemRepository;
import com.azavyalov.nytimes.service.notification.NotificationBuilder;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.azavyalov.nytimes.util.VersionUtils.atLeastNougat;
import static com.azavyalov.nytimes.util.VersionUtils.atLeastOreo;

public class NewsUpdateService extends Service {

    private static final String LOG_TAG = "NewsUpdateService";
    private Disposable downloadDisposable;
    private NewsItemRepository newsRepository;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(LOG_TAG, "onBind");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(LOG_TAG, "onCreate");
        Notification notification = NotificationBuilder.createForegroundNotification(this);
        startForeground(1, notification);
        newsRepository = new NewsItemRepository(this.getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "onStartCommand");

        if (atLeastNougat()) {
            downloadDisposable = RestApi.getInstance()
                    .getNewsService()
                    .searchNews("home") // TODO create enum
                    .map(response -> ConverterDtoToDb.map(response.getNews()))
                    .flatMapCompletable(newsEntities -> newsRepository.saveNewsToDb(newsEntities))
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .subscribe(new Action() {
                        @Override
                        public void run() throws Exception {
                            NotificationBuilder.showResultNotification(NewsUpdateService.this,
                                    NewsUpdateService.this.getString(R.string.notification_success_text));
                            NewsUpdateService.this.stopSelf();
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            NotificationBuilder.showResultNotification(NewsUpdateService.this,
                                    throwable.getClass().getSimpleName());
                            NewsUpdateService.this.stopSelf();
                        }
                    });
        } else {
            NewsUpdateService.this.stopSelf();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "onDestroy");
        if (downloadDisposable != null && !downloadDisposable.isDisposed()) {
            downloadDisposable.dispose();
        }
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, NewsUpdateService.class);
        if (atLeastOreo()) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }
}
