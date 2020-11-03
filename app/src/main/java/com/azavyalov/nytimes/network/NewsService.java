package com.azavyalov.nytimes.network;

import androidx.annotation.NonNull;

import com.azavyalov.nytimes.network.dto.NewsResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface NewsService {

    @NonNull
    @GET("/svc/topstories/v2/{category}.json")
    Call<NewsResponse> searchNews(@Path("category") String category);
}
