package com.azavyalov.nytimes.ui.details;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.azavyalov.nytimes.R;
import com.azavyalov.nytimes.data.NewsItem;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

public class NewsDetailsActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView titleView;
    private TextView dateView;
    private TextView textView;
    private Button webViewButton;

    private static final String EXTRA_NEWS_ITEM = "extra:newsItem";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_details);

        final NewsItem newsItem = (NewsItem) getIntent().getSerializableExtra(EXTRA_NEWS_ITEM);

        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle(newsItem.getCategory());
        }

        findViews();
        setupViews(newsItem);
    }

    private void setupViews(NewsItem newsItem) {
        Glide.with(this)
                .load(newsItem.getImageUrl())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView);

        titleView.setText(newsItem.getTitle());
        textView.setText(newsItem.getPreviewText());
        dateView.setText(newsItem.getPublishDate());
        webViewButton.setOnClickListener(view -> {
            openWebViewActivity(newsItem.getTextUrl());
        });
    }

    private void openWebViewActivity(String newsUrl) {
        NewsDetailsWebViewActivity.start(this, newsUrl);
    }

    private void findViews() {
        imageView = findViewById(R.id.news_details_image);
        titleView = findViewById(R.id.news_details_title);
        dateView = findViewById(R.id.news_details_date);
        textView = findViewById(R.id.news_details_text);
        webViewButton = findViewById(R.id.open_in_web_view_button);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public static void start(@NonNull Context context, @NonNull NewsItem newsItem) {
        context.startActivity(new Intent(context, NewsDetailsActivity.class).putExtra(EXTRA_NEWS_ITEM, newsItem));
    }
}
