package com.ish.roomie.service;

import android.support.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Retrofit objects are created here
 */
public class RetrofitClient {

    /**
     * Base URL for the application API services
     */
    public static final String BASE_URL = "https://challenges.1aim.com/roombooking_app/";

    /**
     * Get the retrofit instance
     *
     * @return retrofit instance
     */
    @NonNull
    private static Retrofit getRetrofitInstance() {
        return new Retrofit.Builder().baseUrl(BASE_URL).addCallAdapterFactory(RxJava2CallAdapterFactory.create()).addConverterFactory(GsonConverterFactory.create()).build();
    }

    /**
     * Get API service instance
     *
     * @return ApiService instance
     */
    public static ApiService getAPIServiceInstance() {
        return getRetrofitInstance().create(ApiService.class);
    }


}
