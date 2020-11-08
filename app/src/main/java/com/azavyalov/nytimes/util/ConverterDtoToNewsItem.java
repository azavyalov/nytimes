package com.azavyalov.nytimes.util;

import com.azavyalov.nytimes.data.NewsItem;
import com.azavyalov.nytimes.network.dto.MultiMediaItem;
import com.azavyalov.nytimes.network.dto.NewsItemDto;

import java.util.ArrayList;
import java.util.List;

public class ConverterDtoToNewsItem {

    public static List<NewsItem> map(List<NewsItemDto> dtos) {

        final List<NewsItem> newsItems = new ArrayList<>();

        for (NewsItemDto item : dtos) {
            newsItems.add(new NewsItem(
                    1,
                    item.getTitle(),
                    MultiMediaItem.findImage(item.getMultimedia()),
                    item.getCategory(),
                    item.getPublishedDate(),
                    item.getSummary(),
                    item.getUrl()));
        }
        return newsItems;
    }
}
