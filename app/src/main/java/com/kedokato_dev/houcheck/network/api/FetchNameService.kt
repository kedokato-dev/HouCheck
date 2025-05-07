package com.kedokato_dev.houcheck.network.api

import com.kedokato_dev.houcheck.network.model.StudentNameResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface FetchNameService {
    @GET("/api/profile")
    suspend fun fetchName(@Query("sessionId") sessionId: String): Response<StudentNameResponse>
}