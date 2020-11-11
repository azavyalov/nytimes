package com.azavyalov.nytimes.network.dto;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class MultiMediaItem implements Serializable {

    @SerializedName("url")
    private String url;

    @SerializedName("type")
    private String type;

    @SerializedName("format")
    private String format;

    public String getImageUrl() {
        return url;
    }

    public String getType() {
        return type;
    }

    public String getFormat() {
        return format;
    }

    @Nullable
    public static String getMaxQualityImageUrl(@Nullable List<MultiMediaItem> multimedias) {

        if (multimedias == null || multimedias.isEmpty()) {
            return null;
        }
        final MultiMediaItem multimedia = multimedias.get(0);
        return multimedia.getImageUrl();
    }
}
