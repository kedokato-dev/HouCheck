package com.kedokato_dev.houcheck.network.api

import com.kedokato_dev.houcheck.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = BuildConfig.BASE_URL_API

    private const val UPLOAD_API_BASE_URL = BuildConfig.UP_LOAD_URL_API

    val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val uploadApiInstance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(UPLOAD_API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}