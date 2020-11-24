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
import com.azavyalov.nytimes.service.notification.ForegroundNotification;
import com.azavyalov.nytimes.service.notification.ResultNotification;

import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.azavyalov.nytimes.network.NewsCategories.HOME;
import static com.azavyalov.nytimes.util.VersionUtils.atLeastNougat;
import static com.azavyalov.nytimes.util.VersionUtils.atLeastOreo;

public class NewsUpdateService extends Service {

    private static final String LOG_TAG = "NewsUpdateServiceTag";
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
        Notification notification = new ForegroundNotification(this).create();
        startForeground(1, notification);
        newsRepository = new NewsItemRepository(this.getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "onStartCommand");
        downloadNews();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "onDestroy");
        if (downloadDisposable != null && !downloadDisposable.isDisposed()) {
            downloadDisposable.dispose();
        }
    }

    private void downloadNews() {
        if (atLeastNougat()) {
            downloadDisposable = RestApi.getInstance()
                    .getNewsService()
                    .searchNews(HOME.toString())
                    .map(response -> ConverterDtoToDb.map(response.getNews()))
                    .flatMapCompletable(newsEntities -> newsRepository.saveNewsToDb(newsEntities))
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .subscribe(() -> {
                        ResultNotification notification = new ResultNotification(
                                NewsUpdateService.this);
                        notification.show(NewsUpdateService.this.getString(
                                R.string.notification_success_text));
                        NewsUpdateService.this.stopSelf();
                    }, throwable -> {
                        ResultNotification notification = new ResultNotification(
                                NewsUpdateService.this);
                        notification.show(throwable.getClass().getSimpleName());
                        NewsUpdateService.this.stopSelf();
                    });
        } else {
            NewsUpdateService.this.stopSelf();
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
