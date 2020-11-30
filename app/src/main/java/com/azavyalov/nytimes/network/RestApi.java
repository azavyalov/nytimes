package com.azavyalov.nytimes.network;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static okhttp3.logging.HttpLoggingInterceptor.Level.BASIC;

public final class RestApi {

    private static final String URL = "https://api.nytimes.com";
    private static final String KEY = "xhcntGLNCj3KLpmZwdbbODCsmk7YXii5";

    private final NewsService newsService;
    private static RestApi restApi;

    public static RestApi getInstance() {
        if (restApi == null) {
            restApi = new RestApi();
        }
        return restApi;
    }

    private RestApi() {
        final OkHttpClient okHttpClient = buildOkHttpClient();
        final Retrofit retrofit = buildRetrofit(okHttpClient, provideGson());

        newsService = retrofit.create(NewsService.class);
    }

    private OkHttpClient buildOkHttpClient() {

        final HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(BASIC);

        return new OkHttpClient.Builder()
                .addInterceptor(ApiKeyInterceptor.create(KEY))
                .addInterceptor(loggingInterceptor)
                .addNetworkInterceptor(new StethoInterceptor())
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    private Retrofit buildRetrofit(OkHttpClient okHttp, Gson gson) {
        return new Retrofit.Builder()
                .baseUrl(URL)
                .client(okHttp)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    private Gson provideGson() {
        return new GsonBuilder().setPrettyPrinting().create();
    }

    public NewsService getNewsService() {
        return newsService;
    }
}
