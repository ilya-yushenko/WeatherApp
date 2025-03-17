package com.weatherapp.data.factory

import com.weatherapp.core.AppConstants
import com.weatherapp.data.factory.RetrofitConst.TIME_OUT
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

fun Retrofit(): Retrofit =
    Retrofit.Builder()
        .baseUrl(AppConstants.APP_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttp())
        .build()

fun okHttp(): OkHttpClient {
    val httpLoggingInterceptor = HttpLoggingInterceptor()
    httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
    return OkHttpClient.Builder()
        .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
        .readTimeout(TIME_OUT, TimeUnit.SECONDS)
        .addInterceptor(httpLoggingInterceptor)
        .build()
}

object RetrofitConst {
    const val TIME_OUT = 240L
}