package com.kedokato_dev.houcheck.data.api

import com.kedokato_dev.houcheck.data.model.UpLoadImageResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface SaveImageToServer {
    @Multipart
    @POST("/upload")
    suspend fun uploadImage(
        @Part("token") token: RequestBody,
        @Part("image_url")image_url: RequestBody,
        @Part("server") server: RequestBody
    ): UpLoadImageResponse
}
