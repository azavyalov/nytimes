package com.azavyalov.nytimes.ui.details;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.azavyalov.nytimes.R;
import com.azavyalov.nytimes.room.NewsEntity;
import com.azavyalov.nytimes.room.NewsItemRepository;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class NewsDetailsActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView titleView;
    private TextView dateView;
    private TextView textView;
    private Button webViewButton;

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private NewsItemRepository newsItemRepository;
    private NewsEntity mNewsEntity;
    private int newsId;

    private static final String NEWS_ITEM_ID_EXTRA = "extra:newsId";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_details);

        newsItemRepository = new NewsItemRepository(getApplicationContext());
        findViews();
        setNewsIdFromExtras();
        loadNewsItemFromDb(newsId);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_news_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.delete_button) {
            deleteNews(mNewsEntity);
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteNews(NewsEntity newsEntity) {
        if (newsEntity != null) {
            Disposable disposable = newsItemRepository
                    .deleteNews(newsEntity)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe();
            compositeDisposable.add(disposable);
            finish();
        }
    }

    private void findViews() {
        imageView = findViewById(R.id.news_details_image);
        titleView = findViewById(R.id.news_details_title);
        dateView = findViewById(R.id.news_details_date);
        textView = findViewById(R.id.news_details_text);
        webViewButton = findViewById(R.id.open_in_web_view_button);
    }

    private void setNewsIdFromExtras() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            newsId = bundle.getInt(NEWS_ITEM_ID_EXTRA);
        }
    }

    private void loadNewsItemFromDb(int id) {
        Disposable disposable = newsItemRepository
                .getNewsById(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(newsEntity -> {
                    mNewsEntity = newsEntity;
                    return mNewsEntity;
                })
                .subscribe(newsEntity -> NewsDetailsActivity.this.setupViews(newsEntity),
                        throwable -> Log.d("Room", throwable.toString()));
        compositeDisposable.add(disposable);
    }

    private void setupViews(NewsEntity newsEntity) {
        Glide.with(this)
                .load(newsEntity.getImageUrl())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView);

        titleView.setText(newsEntity.getTitle());
        textView.setText(newsEntity.getPreviewText());
        dateView.setText(newsEntity.getPublishDate());
        webViewButton.setOnClickListener(view -> {
            openWebViewActivity(newsEntity.getTextUrl());
        });
    }

    private void openWebViewActivity(String newsUrl) {
        NewsDetailsWebViewActivity.start(this, newsUrl);
    }

    public static void start(@NonNull Activity activity, @NonNull int id) {
        Intent intent = new Intent(activity, NewsDetailsActivity.class);
        intent.putExtra(NEWS_ITEM_ID_EXTRA, id);
        activity.startActivity(intent);
    }
}
