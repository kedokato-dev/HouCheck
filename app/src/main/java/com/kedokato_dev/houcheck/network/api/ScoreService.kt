package com.kedokato_dev.houcheck.network.api

import com.kedokato_dev.houcheck.network.model.ScoreResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ScoreService {
    @GET("/api/score")
    suspend fun fetchScore(@Query("sessionId") sessionId: String): Response<ScoreResponse>

}