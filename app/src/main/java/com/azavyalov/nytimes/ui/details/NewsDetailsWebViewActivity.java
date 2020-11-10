package com.azavyalov.nytimes.ui.details;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.azavyalov.nytimes.R;

public class NewsDetailsWebViewActivity extends AppCompatActivity {

    private static final String EXTRA_URL = "extra:url";
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_details_web_view);

        final String url = getIntent().getStringExtra(EXTRA_URL);
        setupWebView(url);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public static void start(@NonNull Activity activity, @NonNull String url) {
        Intent intent = new Intent(activity, NewsDetailsWebViewActivity.class);
        intent.putExtra(EXTRA_URL, url);
        activity.startActivity(intent);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView(String url) {
        mProgressBar = findViewById(R.id.progress_bar);
        mProgressBar.setMax(100);

        WebView webView = findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        setWebChromeClient(webView);

        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(url);
    }

    private void setWebChromeClient(WebView webView) {
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    mProgressBar.setVisibility(View.GONE);
                } else {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mProgressBar.setProgress(newProgress);
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                getSupportActionBar().setSubtitle(title);
            }
        });
    }
}
