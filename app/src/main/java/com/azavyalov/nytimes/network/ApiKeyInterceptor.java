package com.azavyalov.nytimes.network;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class ApiKeyInterceptor implements Interceptor {

    private static final String PARAM_API_KEY = "api-key";
    private final String apiKey;

    public ApiKeyInterceptor(String key) {
        this.apiKey = key;
    }

    public static ApiKeyInterceptor create(String key) {
        return new ApiKeyInterceptor(key);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        // Request without api key
        final Request originalRequest = chain.request();

        final HttpUrl url = originalRequest
                .url()
                .newBuilder()
                .addQueryParameter(PARAM_API_KEY, apiKey)
                .build();

        // Request with api key
        final Request request = originalRequest
                .newBuilder()
                .url(url)
                .build();

        return chain.proceed(request);
    }
}
