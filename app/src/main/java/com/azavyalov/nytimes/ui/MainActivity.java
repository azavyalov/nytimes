package com.azavyalov.nytimes.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.azavyalov.nytimes.R;
import com.azavyalov.nytimes.ui.about.AboutActivity;
import com.azavyalov.nytimes.ui.news.NewsListFragment;

public class MainActivity extends AppCompatActivity {

    static final String TAG_NEWS_LIST_FRAGMENT = "news_list_fragment";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        openNewsListFragment();
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

    private void openNewsListFragment() {
        NewsListFragment newsListFragment = NewsListFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_frame, newsListFragment, TAG_NEWS_LIST_FRAGMENT)
                .addToBackStack(TAG_NEWS_LIST_FRAGMENT)
                .commit();
    }
}
