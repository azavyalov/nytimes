package com.azavyalov.nytimes.network;

import androidx.annotation.NonNull;

import com.azavyalov.nytimes.network.dto.NewsResponse;

import io.reactivex.Single;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface NewsService {

    @NonNull
    @GET("/svc/topstories/v2/{category}.json")
    Single<NewsResponse> searchNews(@Path("category") String category);
}
