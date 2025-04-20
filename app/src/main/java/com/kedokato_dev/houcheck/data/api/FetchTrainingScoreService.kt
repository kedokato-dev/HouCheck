package com.kedokato_dev.houcheck.data.api

import com.kedokato_dev.houcheck.data.model.TrainingScoreResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface FetchTrainingScoreService {
    @GET("/api/training-score")
    suspend fun fetchTrainingScore(@Query("sessionId") sessionId: String): Response<TrainingScoreResponse>
}