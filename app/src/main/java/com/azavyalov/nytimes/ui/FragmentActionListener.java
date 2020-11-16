package com.azavyalov.nytimes.ui;

import com.azavyalov.nytimes.data.NewsItem;

public interface FragmentActionListener {
    void onNewsSelected(NewsItem newsItem);
}
