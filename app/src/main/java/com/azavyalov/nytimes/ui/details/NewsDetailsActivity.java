package com.azavyalov.nytimes.ui.details;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.azavyalov.nytimes.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.azavyalov.nytimes.data.NewsItem;
import com.azavyalov.nytimes.util.Util;

public class NewsDetailsActivity extends AppCompatActivity {

    private static final String EXTRA_NEWS_ITEM = "extra:newsItem";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_details);

        final NewsItem newsItem = (NewsItem) getIntent().getSerializableExtra(EXTRA_NEWS_ITEM);

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(newsItem.getCategory().getName());
        }

        final ImageView imageView = findViewById(R.id.news_details_image);
        final TextView titleView = findViewById(R.id.news_details_title);
        final TextView dateView = findViewById(R.id.news_details_date);
        final TextView textView = findViewById(R.id.news_details_text);

        Glide.with(this)
                .load(newsItem.getImageUrl())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView);

        titleView.setText(newsItem.getTitle());
        dateView.setText(Util.formatDateTime(this, newsItem.getPublishDate()));
        textView.setText(newsItem.getTextUrl());
    }

    public static void start(@NonNull Context context, @NonNull NewsItem newsItem) {
        context.startActivity(new Intent(context, NewsDetailsActivity.class).putExtra(EXTRA_NEWS_ITEM, newsItem));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
