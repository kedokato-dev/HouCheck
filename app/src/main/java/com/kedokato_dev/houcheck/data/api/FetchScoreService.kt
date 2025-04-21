package com.kedokato_dev.houcheck.data.api

import com.kedokato_dev.houcheck.data.model.ScoreResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface FetchScoreService {
    @GET("/api/score")
    suspend fun fetchScore(@Query("sessionId") sessionId: String): Response<ScoreResponse>

}