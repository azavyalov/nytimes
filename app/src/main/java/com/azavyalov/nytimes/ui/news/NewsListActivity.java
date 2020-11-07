package com.azavyalov.nytimes.ui.news;

import android.content.Intent;
import android.os.Bundle;
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
import com.azavyalov.nytimes.network.dto.ConverterDtoToNewsItem;
import com.azavyalov.nytimes.network.dto.NewsResponse;
import com.azavyalov.nytimes.ui.about.AboutActivity;
import com.azavyalov.nytimes.ui.details.NewsDetailsActivity;
import com.azavyalov.nytimes.util.Util;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;
import static com.azavyalov.nytimes.ui.news.State.HAS_DATA;
import static com.azavyalov.nytimes.ui.news.State.HAS_NO_DATA;
import static com.azavyalov.nytimes.ui.news.State.LOADING;
import static com.azavyalov.nytimes.ui.news.State.NETWORK_ERROR;
import static com.azavyalov.nytimes.ui.news.State.SERVER_ERROR;
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
    private Disposable disposable;
    @Nullable
    private FloatingActionButton updateAction;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);

        findViews();
        setAdapter();
        prepareRecycler();
        prepareRetryButton();
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadNews();
    }

    @Override
    protected void onStop() {
        super.onStop();

        Util.disposeSafe(disposable);
        disposable = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        adapter = null;
        recycler = null;
        progress = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.about_activity) {
            startActivity(new Intent(this, AboutActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadNews() {
        showState(LOADING);

        disposable = RestApi.getInstance()
                .getNewsService()
                .searchNews("home")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::updateItems, this::handleError);
    }

    private void updateItems(@Nullable Response<NewsResponse> response) {

        if (!response.isSuccessful()) {
            showState(SERVER_ERROR);
            return;
        }

        NewsResponse newsResponse = response.body();
        if (newsResponse.getNews() == null) {
            showState(HAS_NO_DATA);
            return;
        }

        List<NewsItem> news = ConverterDtoToNewsItem.map(newsResponse.getNews());
        if (news == null || news.isEmpty()) {
            showState(HAS_NO_DATA);
            return;
        }

        if (adapter != null) adapter.replaceItems(news);
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
            case SERVER_ERROR:
            case NETWORK_ERROR:
                setVisibility(error, true);
                setVisibility(recycler, false);
                setVisibility(progress, false);
                setVisibility(updateAction, true);
                break;
            default:
                throw new IllegalArgumentException("Unexpected state: " + state);
        }
    }

    private void handleError(Throwable th) {
        if (th instanceof IOException) {
            showState(NETWORK_ERROR);
            return;
        }
        showState(SERVER_ERROR);
    }

    private void findViews() {
        progress = findViewById(R.id.progress);
        recycler = findViewById(R.id.news_list_recycler);
        error = findViewById(R.id.layout_error);
        errorAction = findViewById(R.id.error_action_button);
        updateAction = findViewById(R.id.floating_action_button);
    }

    private void setAdapter() {
        adapter = new NewsAdapter(this, newsItem ->
                NewsDetailsActivity.start(NewsListActivity.this, newsItem));
    }

    private void prepareRecycler() {
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

    private void prepareRetryButton() {
        errorAction.setOnClickListener(view -> loadNews());
    }

    /*
    private void setupUpdateButton() {
        updateAction.setOnClickListener(view -> storeItemsToDb());
    }

    private void storeItemsToDb() {
        showState(LOADING);
        final Disposable disposable = RestApi.getInstance()
                .getNewsService()
                .searchNews("home")
                .map()
    }*/
}
