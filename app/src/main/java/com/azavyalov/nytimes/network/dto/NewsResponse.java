package com.azavyalov.nytimes.network.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NewsResponse {

    @SerializedName("status")
    private String status;

    @SerializedName("results")
    private List<NewsItem> news;

    public String getStatus() {
        return status;
    }

    public List<NewsItem> getNews() {
        return news;
    }
}
