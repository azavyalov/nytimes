package com.azavyalov.nytimes.room;

import com.azavyalov.nytimes.data.NewsItem;

import java.util.ArrayList;
import java.util.List;

public class ConverterDbToNewsItem {

    public static List<NewsItem> map(List<NewsEntity> listDb) {

        final List<NewsItem> newsItems = new ArrayList<>();
        for (NewsEntity item_db : listDb) {
            newsItems.add(
                    new NewsItem(
                            item_db.getId(),
                            item_db.getTitle(),
                            item_db.getImageUrl(),
                            item_db.getCategory(),
                            item_db.getPublishDate(),
                            item_db.getPreviewText(),
                            item_db.getTextUrl()
                    )
            );
        }
        return newsItems;
    }
}

