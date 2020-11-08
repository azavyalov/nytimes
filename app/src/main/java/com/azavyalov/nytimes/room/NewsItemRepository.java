package com.azavyalov.nytimes.room;

import android.content.Context;
import android.util.Log;

import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Completable;
import io.reactivex.Observable;

public class NewsItemRepository {

    private Context mContext;

    public NewsItemRepository(Context mContext) {
        this.mContext = mContext;
    }

    public Observable<List<NewsEntity>> getNewsFromDb() {
        AppDatabase database = AppDatabase.getInstance(mContext);
        return database.newsDao().getAll();
    }

    public Completable saveNewsToDb(List<NewsEntity> newsList) {
        Log.d("Room","Saving news to DB");
        return Completable.fromCallable(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                AppDatabase database = AppDatabase.getInstance(mContext);
                database.newsDao().deleteAll();
                NewsEntity[] newsArray = new NewsEntity[newsList.size()];
                newsArray = newsList.toArray(newsArray);
                database.newsDao().insertAll(newsArray);
                Log.d("Room","News was saved to DB");
                return null;
            }
        });
    }
}
