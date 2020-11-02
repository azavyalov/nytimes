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
import com.azavyalov.nytimes.ui.about.AboutActivity;
import com.azavyalov.nytimes.data.DataUtils;
import com.azavyalov.nytimes.data.NewsItem;
import com.azavyalov.nytimes.ui.details.NewsDetailsActivity;
import com.azavyalov.nytimes.util.Util;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

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

        adapter = new NewsAdapter(this, DataUtils.generateNews(), new NewsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull NewsItem newsItem) {
                NewsDetailsActivity.start(NewsListActivity.this, newsItem);
            }
        });

        prepareRecycler();
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadItems();
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

    private void loadItems() {
        showProgress(true);

        disposable = DataUtils.observeNews()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::updateItems, this::handleError);
    }

    private void updateItems(@Nullable List<NewsItem> newsItems) {
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
