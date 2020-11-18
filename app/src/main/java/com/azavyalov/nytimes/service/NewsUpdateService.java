package com.azavyalov.nytimes.service;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.azavyalov.nytimes.R;
import com.azavyalov.nytimes.network.RestApi;
import com.azavyalov.nytimes.room.ConverterDtoToDb;
import com.azavyalov.nytimes.room.NewsItemRepository;
import com.azavyalov.nytimes.service.notification.ForegroundNotificationBuilder;
import com.azavyalov.nytimes.service.notification.ResultNotificationBuilder;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.azavyalov.nytimes.util.VersionUtils.atLeastNougat;
import static com.azavyalov.nytimes.util.VersionUtils.atLeastOreo;

public class NewsUpdateService extends Service {

    private Disposable downloadDisposable;
    private NewsItemRepository newsItemRepository;

    @Override
    public void onCreate() {
        super.onCreate();
        newsItemRepository = new NewsItemRepository(getApplicationContext());
        Notification notification = new ForegroundNotificationBuilder(this).build();
        startForeground(1, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (atLeastNougat()) {
            downloadDisposable = RestApi.getInstance()
                    .getNewsService()
                    .searchNews("home")
                    .map(response -> ConverterDtoToDb.map(response.getNews()))
                    .flatMapCompletable(newsEntities -> newsItemRepository.saveNewsToDb(newsEntities))
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .subscribe(new Action() {
                        @Override
                        public void run() throws Exception {
                            Service service = NewsUpdateService.this;
                            ResultNotificationBuilder notification = new ResultNotificationBuilder(
                                    service);
                            notification.show(service.getString(
                                    R.string.notification_success_text));
                            service.stopSelf();
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Service service = NewsUpdateService.this;
                            ResultNotificationBuilder notification = new ResultNotificationBuilder(
                                    service);
                            notification.show(throwable.getClass().getSimpleName());
                            service.stopSelf();
                        }
                    });
        }
        this.stopSelf();
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (downloadDisposable != null && !downloadDisposable.isDisposed()) {
            downloadDisposable.dispose();
        }
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, NewsUpdateService.class);

        // We separate service starting due to android SDK restrictions comes from OREO (API 26)
        if (atLeastOreo()) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }
}
