package com.azavyalov.nytimes.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.azavyalov.nytimes.R;
import com.azavyalov.nytimes.data.NewsItem;
import com.azavyalov.nytimes.ui.about.AboutActivity;
import com.azavyalov.nytimes.ui.details.NewsDetailsFragment;
import com.azavyalov.nytimes.ui.news.NewsListFragment;

public class MainActivity extends AppCompatActivity implements FragmentActionListener {

    private static final String TAG_NEWS_LIST_FRAGMENT = "news_list_fragment";
    private static final String TAG_NEWS_DETAIL_FRAGMENT = "news_detail_fragment";
    private boolean isTwoPanel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        isTwoPanel = findViewById(R.id.details_container) != null;
        if (savedInstanceState == null) {
            openNewsListFragment();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.about_activity) {
            startActivity(new Intent(this, AboutActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onNewsSelected(NewsItem newsItem) {
        openNewsDetailsFragment(newsItem.getId());
    }

    private void openNewsListFragment() {
        NewsListFragment newsListFragment = NewsListFragment.newInstance();
        newsListFragment.setFragmentActionListener(this);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.list_container, newsListFragment, TAG_NEWS_LIST_FRAGMENT)
                .addToBackStack(null)
                .commit();
    }

    private void openNewsDetailsFragment(int id) {
        NewsDetailsFragment detailsFragment = NewsDetailsFragment.newInstance(id);
        int frameId = isTwoPanel ? R.id.details_container : R.id.list_container;
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,
                        R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(frameId, detailsFragment, TAG_NEWS_DETAIL_FRAGMENT)
                .addToBackStack(TAG_NEWS_DETAIL_FRAGMENT)
                .commit();
    }
}
