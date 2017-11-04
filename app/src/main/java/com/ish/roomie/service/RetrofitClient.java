package com.ish.roomie.service;

/**
 * Created by 611399999 on 03/11/2017.
 */

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
    private static final String BASE_URL = "https://challenges.1aim.com/roombooking_app/";

    /**
     *Get the retrofit instance
     * @return retrofit instance
     */
    @NonNull
    private static Retrofit getRetrofitInstance(){
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();

                        // Customize the request
                        Request request = original.newBuilder()
                                .header("Content-Type", "application/json")
                                .build();

                        okhttp3.Response response = chain.proceed(request);
                        response.cacheResponse();
                        // Customize or return the response
                        return response;
                    }
                }).build();
        return new Retrofit.Builder().baseUrl(BASE_URL).client(okHttpClient).addCallAdapterFactory(RxJava2CallAdapterFactory.create()).addConverterFactory(GsonConverterFactory.create()).build();
    }

    /**
     * Get API service instance
     * @return ApiService instance
     */
    public static ApiService getAPIServiceInstance(){
        return getRetrofitInstance().create(ApiService.class);
    }


}
