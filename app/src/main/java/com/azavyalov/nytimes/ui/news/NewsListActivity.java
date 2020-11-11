package com.azavyalov.nytimes.ui.news;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.azavyalov.nytimes.R;
import com.azavyalov.nytimes.data.NewsItem;
import com.azavyalov.nytimes.network.RestApi;
import com.azavyalov.nytimes.room.NewsItemRepository;
import com.azavyalov.nytimes.ui.about.AboutActivity;
import com.azavyalov.nytimes.ui.details.NewsDetailsActivity;
import com.azavyalov.nytimes.util.ConverterDbToNewsItem;
import com.azavyalov.nytimes.util.ConverterDtoToDb;
import com.azavyalov.nytimes.util.Util;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;
import static com.azavyalov.nytimes.ui.news.State.HAS_DATA;
import static com.azavyalov.nytimes.ui.news.State.HAS_NO_DATA;
import static com.azavyalov.nytimes.ui.news.State.LOADING;
import static com.azavyalov.nytimes.util.Util.setVisibility;

public class NewsListActivity extends AppCompatActivity {

    @Nullable
    private RecyclerView recycler;
    @Nullable
    private NewsAdapter adapter;
    @Nullable
    private ProgressBar progress;
    @Nullable
    private View error;
    @Nullable
    private Button errorAction;
    @Nullable
    private FloatingActionButton updateAction;

    private NewsItemRepository newsItemRepository;
    private CompositeDisposable compositeDisposable;

    private final NewsAdapter.OnItemClickListener clickListener =
            new NewsAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(@NonNull NewsItem newsItem) {
                    NewsListActivity.this.openDetailedNewsActivity(newsItem.getId());
                }
            };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);

        newsItemRepository = new NewsItemRepository(getApplicationContext());
        compositeDisposable = new CompositeDisposable();

        storeNewsFromApiToDb();

        findViews();
        setAdapter();
        setupRecycler();
        setupRetryButton();
        setupUpdateButton();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        subscribeToNewsFromDb();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Util.disposeSafe(compositeDisposable);
        compositeDisposable = null;
        adapter = null;
        recycler = null;
        progress = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_news_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.about_activity) {
            startActivity(new Intent(this, AboutActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private void findViews() {
        progress = findViewById(R.id.progress);
        recycler = findViewById(R.id.news_list_recycler);
        error = findViewById(R.id.error_container);
        errorAction = findViewById(R.id.error_action_button);
        updateAction = findViewById(R.id.floating_action_button);
    }

    private void setAdapter() {
        adapter = new NewsAdapter(this, clickListener);
    }

    private void setupRecycler() {
        recycler.setAdapter(adapter);
        recycler.addItemDecoration(new NewsItemDecoration(getResources()
                .getDimensionPixelSize(R.dimen.spacing_micro)));

        if (getResources().getConfiguration().orientation == ORIENTATION_LANDSCAPE) {
            recycler.setLayoutManager(new GridLayoutManager(
                    this, getResources().getInteger(R.integer.landscape_news_columns_count)));
        } else {
            recycler.setLayoutManager(new LinearLayoutManager(this));
        }
    }

    private void setupRetryButton() {
        errorAction.setOnClickListener(view -> subscribeToNewsFromDb());
    }

    private void setupUpdateButton() {
        updateAction.setOnClickListener(view -> storeNewsFromApiToDb());
    }

    private void storeNewsFromApiToDb() {
        showState(LOADING);
        final Disposable disposable = RestApi.getInstance()
                .getNewsService()
                .searchNews("home")
                .map(response -> ConverterDtoToDb.map(response.getNews()))
                .flatMapCompletable(newsEntities -> newsItemRepository.saveNewsToDb(newsEntities))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> showState(HAS_DATA), throwable -> showState(HAS_NO_DATA));
        compositeDisposable.add(disposable);
    }

    private void subscribeToNewsFromDb() {
        showState(LOADING);
        Disposable disposable = newsItemRepository
                .getNewsFromDb()
                .map(newsEntities -> ConverterDbToNewsItem.map(newsEntities))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(newsItems -> updateItems(newsItems),
                        throwable -> Log.d("Room", throwable.toString()));
        compositeDisposable.add(disposable);
    }

    private void updateItems(List<NewsItem> news) {
        if (news == null || news.isEmpty()) {
            showState(HAS_NO_DATA);
            return;
        }
        if (adapter != null) {
            adapter.replaceItems(news);
        }
        showState(HAS_DATA);
    }

    public void showState(@NonNull State state) {

        switch (state) {
            case HAS_DATA:
                setVisibility(recycler, true);
                setVisibility(progress, false);
                setVisibility(error, false);
                setVisibility(updateAction, true);
                break;
            case LOADING:
                setVisibility(progress, true);
                setVisibility(recycler, false);
                setVisibility(error, false);
                setVisibility(updateAction, false);
                break;
            case HAS_NO_DATA:
                setVisibility(error, true);
                setVisibility(recycler, false);
                setVisibility(progress, false);
                setVisibility(updateAction, true);
                break;
            default:
                throw new IllegalArgumentException("Unexpected state: " + state);
        }
    }

    public void openDetailedNewsActivity(int id) {
        NewsDetailsActivity.start(this, id);
    }
}
