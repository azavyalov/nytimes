package com.azavyalov.nytimes.room;


import com.azavyalov.nytimes.network.dto.MultiMediaItem;
import com.azavyalov.nytimes.network.dto.NewsItemDto;

import java.util.ArrayList;
import java.util.List;

public class NewsItemConverterDtoToDb {

    public static List<NewsEntity> map(List<NewsItemDto> newsItems) {

        final List<NewsEntity> newsEntities = new ArrayList<>();
        for (NewsItemDto item : newsItems) {
            newsEntities.add(
                    new NewsEntity(
                            item.getTitle(),
                            MultiMediaItem.findImage(item.getMultimedia()),
                            item.getCategory(),
                            item.getPublishedDate(),
                            item.getSummary(),
                            item.getUrl()));
        }
        return newsEntities;
    }
}
