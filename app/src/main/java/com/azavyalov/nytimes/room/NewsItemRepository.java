package com.azavyalov.nytimes.room;

import android.content.Context;
import android.util.Log;

import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

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
        Log.d("Room", "Saving news to DB");
        return Completable.fromCallable((Callable<Void>) () -> {
            AppDatabase database = AppDatabase.getInstance(mContext);
            database.newsDao().deleteAll();
            NewsEntity[] newsArray = new NewsEntity[newsList.size()];
            newsArray = newsList.toArray(newsArray);
            database.newsDao().insertAll(newsArray);
            Log.d("Room", "News was successfully saved to DB");
            return null;
        });
    }

    public Single<NewsEntity> getNewsById(int id) {
        Log.d("Room", "Getting news from DB with id: " + id);
        return Single.fromCallable(() -> {
            AppDatabase database = AppDatabase.getInstance(mContext);
            return database.newsDao().getNewsById(id);
        });
    }

    public Completable deleteNews(NewsEntity newsEntity) {
        Log.d("Room", "Deleting news from db with id: " + newsEntity.getId());
        return Completable.fromAction(() -> {
            AppDatabase database = AppDatabase.getInstance(mContext);
            database.newsDao().delete(newsEntity);
            Log.d("Room", "News has been deleted from DB." +
                    " Id: "
                    + newsEntity.getId()
                    + " Title: "
                    + newsEntity.getTitle());
        });
    }
}
