package com.azavyalov.nytimes.network.dto;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class NewsItemDto implements Serializable {

    @Nullable
    @SerializedName("section")
    private String category;

    @SerializedName("title")
    private String title;

    @SerializedName("abstract")
    private String summary;

    @SerializedName("published_date")
    private String publishedDate;

    @SerializedName("url")
    private String url;

    @Nullable
    @SerializedName("multimedia")
    private List<MultiMediaItem> multimedia;

    @Nullable
    public String getCategory() {
        return category;
    }

    public String getTitle() {
        return title;
    }

    public String getSummary() {
        return summary;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public String getUrl() {
        return url;
    }

    @Nullable
    public List<MultiMediaItem> getMultimedia() {
        return multimedia;
    }
}
