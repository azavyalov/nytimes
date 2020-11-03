package com.azavyalov.nytimes.ui.news;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.azavyalov.nytimes.R;
import com.azavyalov.nytimes.network.RestApi;
import com.azavyalov.nytimes.network.dto.NewsItemDto;
import com.azavyalov.nytimes.network.dto.NewsItemsDto;
import com.azavyalov.nytimes.ui.about.AboutActivity;
import com.azavyalov.nytimes.ui.details.NewsDetailsActivity;
import com.azavyalov.nytimes.util.Util;

import java.util.List;

import io.reactivex.disposables.Disposable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;

public class NewsListActivity extends AppCompatActivity {

    public static final String TAG = NewsListActivity.class.getSimpleName();

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);

        progress = findViewById(R.id.progress);
        recycler = findViewById(R.id.news_list_recycler);
        error = findViewById(R.id.layout_error);
        errorAction = findViewById(R.id.action_button);

        adapter = new NewsAdapter(this, newsItem ->
                NewsDetailsActivity.start(NewsListActivity.this, newsItem));

        prepareRecycler();
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadNews();
    }

    @Override
    protected void onStop() {
        super.onStop();

        showProgress(false);
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
        showProgress(true);

        Call<NewsItemsDto> searchRequest = RestApi.getInstance()
                .getNewsService()
                .searchNews("home");

        searchRequest.enqueue(new Callback<NewsItemsDto>() {
            @Override
            public void onResponse(Call<NewsItemsDto> call, Response<NewsItemsDto> response) {
                NewsItemsDto newsItemsDto = response.body();
                List<NewsItemDto> newsItems = newsItemsDto.getNews();
                updateItems(newsItems);
            }

            @Override
            public void onFailure(Call<NewsItemsDto> call, Throwable t) {
                handleError(t);
            }
        });
    }

    private void updateItems(@Nullable List<NewsItemDto> newsItems) {
        if (adapter != null) adapter.replaceItems(newsItems);

        Util.setVisible(recycler, true);
        Util.setVisible(progress, false);
        Util.setVisible(error, false);
    }

    private void showProgress(boolean shouldShow) {
        Util.setVisible(progress, shouldShow);
        Util.setVisible(recycler, !shouldShow);
        Util.setVisible(error, !shouldShow);
    }

    private void handleError(Throwable th) {
        if (Util.isDebug()) {
            Log.e(TAG, th.getMessage(), th);
        }
        Util.setVisible(error, true);
        Util.setVisible(progress, false);
        Util.setVisible(recycler, false);
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
}
