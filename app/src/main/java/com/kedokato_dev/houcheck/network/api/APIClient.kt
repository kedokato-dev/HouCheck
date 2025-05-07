package com.kedokato_dev.houcheck.network.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "https://sinhvienhouapi.onrender.com"

    private const val UPLOAD_API_BASE_URL = "https://upload.upanhlaylink.com"

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