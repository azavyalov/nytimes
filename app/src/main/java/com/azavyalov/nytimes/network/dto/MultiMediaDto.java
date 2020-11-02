package com.azavyalov.nytimes.network.dto;

import com.google.gson.annotations.SerializedName;

public class MultiMediaDto {

    @SerializedName("url")
    private String url;

    @SerializedName("type")
    private String type;

    @SerializedName("format")
    private String format;

    public String getUrl() {
        return url;
    }

    public String getType() {
        return type;
    }

    public String getFormat() {
        return format;
    }
}
